/*
 * Copyright 2012 University of Denver
 * Author Chet Rebman
 * 
 * This file is part of FedoraProxy.
 * 
 * FedoraProxy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FedoraProxy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with FedoraProxy.  If not, see <http://www.gnu.org/licenses/>.
*/
package edu.du.penrose.systems.fedoraProxy.web.bus;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter; 
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory; 
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.ParamPart;
import com.oreilly.servlet.multipart.Part;

import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraApp.ProgramProperties;
import edu.du.penrose.systems.fedoraApp.util.FedoraAppUtil;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestOptions;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestOptions.INGEST_THREAD_TYPE;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestURLhandler;
import edu.du.penrose.systems.fedoraProxy.FedoraProxyConstants;
import edu.du.penrose.systems.util.FileUtil;
import edu.du.penrose.systems.fedoraApp.util.MetsBatchFileSplitter;


/**
 * Accepts posts from external application. The post is a multpart form, containing the PCO's (pdf's) and the batch ingest command/METS file.
 * The batch ingest command portion is parsed and then the batch files is split into the /batch_space/{institution}/{batchSet}/mets/new directory. FedoraApp
 * asynchronously processes and ingest's the file(s).
 */
@Controller
@RequestMapping("{institution}/{batchSet}/ingest.it")
public class IngestController {

	/** 
	 * Logger for this class and subclasses.
	 */
	protected final Log logger = LogFactory.getLog( getClass() );

	static public final String XML_CONTENT_TYPE = "text/xml";
	static public final String XML_FILE_SUFFIX = ".xml";

	static public final String PDF_CONTENT_TYPE = "application/pdf";
	static public final String PDF_FILE_SUFFIX = ".pdf";
	private static final int MAX_FILE_SIZE = 10*1024*1024; // 100M

	private static final String DISABLE_CMD_FILE_SUFFIX = ".DISABLED_UNTIL_PCOS_SAVED";
	
	static Map<String, String> CONTENT_TYPE_to_SUFFIX_MAP = null;

	{
		CONTENT_TYPE_to_SUFFIX_MAP = new HashMap<String, String>();
		CONTENT_TYPE_to_SUFFIX_MAP.put(XML_CONTENT_TYPE, XML_FILE_SUFFIX);
		CONTENT_TYPE_to_SUFFIX_MAP.put(PDF_CONTENT_TYPE, PDF_FILE_SUFFIX);
	}

	private BatchIngestOptions batchOptions = null;

	/**
	 * The ingest controller only performs a POST, this is a GET, so we return a 404 status.
	 * @throws IOException 
	 */
	@RequestMapping(method = RequestMethod.GET) 
	public void handleGet( HttpServletRequest request, HttpServletResponse response) throws IOException 
	{
		String msg = "Get not supported";
		logger.warn( msg ); 

		response.sendError( 501, msg );
	}

	/**
	 * Process a batch ingest POST from an external application. Security is set in applicatonContext.xml. The post request has PCO and
	 * the batch ingest (METS) file attached. The batch file must have a Form Part Name must match FedoraAppConstants.BATCH_FILE_IDENTIFIER
	 * (batch_) so that it will be placed in the 'work' directory, all other files are placed in the 'files' directory. If the file
	 * ends in FedoraAppConstants.BATCH_FILE_SUFFIX (.xml) the file is disabled (by changing the suffix) until all of the other files
	 * have been transfered and written to disk. It is then re-enabled so that it can be seen by the ingester.
	 * 
	 * If an errors occurs  the batch file AND the PCOS are moved to the failed batch directory. 
	 * 
	 * If this is a  replyWithPid operation (set in the batch file) The file will be split with pids set in individual batch files. 
	 * Other wise the batch file is simply enabled after all attached PCOs have been saved to the file system. FedoraApp tasks 
	 * (if enabled) will look for mets files in the new and update directories and for batch files with a '_REMOTE' flag. 
	 * 
	 * @see FedoraAppConstants.REMOTE_TASK_NAME_SUFFIX
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.POST)
	public void handlePost( @PathVariable String institution, @PathVariable String batchSet, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Map<String,String> pidMap = null;

		logger.info( institution+", "+batchSet+ " Post!"); 


		boolean allowed = this.verifyRemoteAllowed( institution, batchSet ); 
		
		if ( ! allowed )
		{
			String msg = "The institution:"+institution+" batchSet:"+batchSet+" does not appear to be enabled for remote ingest. Check the  webSiteCollection.properties file";
			logger.error( msg );
			System.out.println( msg );
			response.sendError( 500, msg );  
			return;
		}
		
		String myBatchRunName = FedoraAppUtil.getUniqueBatchRunName( institution, batchSet );

		boolean xmlRecordUpdates = false;

		PrintWriter htmlResponse = response.getWriter();
		
		htmlResponse.println( "<html>\n<head>\n</head>\n<body>\n");
		htmlResponse.println( batchSet+" Ingest" );

		File batchCmdFile = null;
		String batchCommandFileName = null;
		String batchCommandFileName_DISABLED = null;
		boolean haveBatchFile = false;
		
		String workFolder                = null;
		String filesFolder               = null; 
		String completedBatchFilesFolder = null;
		String failedBatchFileFolder     = null;
		
		ArrayList<File> pcoFileList = new ArrayList();
		
		try {
			BatchIngestURLhandler urlHandler = BatchIngestURLhandler.getInstance( FedoraProxyConstants.getServletContextListener(), myBatchRunName, institution, batchSet,	xmlRecordUpdates, batchOptions);

			workFolder                = urlHandler.getWorkFolderURL().getFile().replace('/', File.separatorChar);	
			filesFolder               = urlHandler.getFilesFolderURL().getFile().replace('/', File.separatorChar);	
			completedBatchFilesFolder = urlHandler.getCompletedBatchFilesFolderURL().getFile().replace('/', File.separatorChar);	
			failedBatchFileFolder     = urlHandler.getFailedBatchFilesFolderURL().getFile().replace('/', File.separatorChar);
			
			MultipartParser mp = new MultipartParser(request, MAX_FILE_SIZE ); 
			Part part;
			while ((part = mp.readNextPart()) != null) 
			{
				String formPartName = part.getName();
				if (part.isParam()) {
					// it's a parameter part
					ParamPart paramPart = (ParamPart) part;
					String value = paramPart.getStringValue();
					htmlResponse.println("param; name=" + formPartName + ", value=" + value);
				}
				else if (part.isFile()) {
					// it's a file part
					FilePart filePart = (FilePart) part;
					String fileName = filePart.getFileName();
					if (fileName != null) {
						// the part actually contained a file
						// Check the form part name, to determine if it is a batch file.
						if ( formPartName.equalsIgnoreCase( FedoraProxyConstants.ECTD_BATCH_XML_FORM_PART_NAME ) )
						{
							if ( ! fileName.toLowerCase().contains( FedoraAppConstants.BATCH_FILE_IDENTIFIER ) )
							{
								fileName = FedoraAppConstants.BATCH_FILE_IDENTIFIER + fileName;
							}
							
							if ( fileName.toLowerCase().contains( FedoraAppConstants.BATCH_FILE_SUFFIX )) {
								haveBatchFile = true;
								fileName = FileUtil.getBatchUniqueFileName( fileName, new BatchIngestOptions().setIngestThreadType( INGEST_THREAD_TYPE.REMOTE ) );
								batchCommandFileName          = fileName;
								batchCommandFileName_DISABLED = fileName + DISABLE_CMD_FILE_SUFFIX;
								// write the standard batch file
								this.writeFile( formPartName, batchCommandFileName_DISABLED, filePart, workFolder, htmlResponse) ;	
							}
							else if ( fileName.toLowerCase().contains( FedoraAppConstants.BAGIT_FILE_SUFFIX )) 
							{
								haveBatchFile = false; 
								fileName = fileName.replace( FedoraAppConstants.BAGIT_FILE_SUFFIX, FedoraAppConstants.REMOTE_TASK_NAME_SUFFIX + FedoraAppConstants.BAGIT_FILE_SUFFIX );
								// write the bagit file.
								this.writeFile( formPartName, fileName, filePart, workFolder, htmlResponse) ;
							}
							else {
								throw new Exception( "Unkown batch file type");
							}
						}
						else 
						{
							// write the PCO 'Primary Content File (data); file
							this.writeFile( formPartName, fileName, filePart, filesFolder, htmlResponse) ;	
							// save a copy of each file, in case there is error, so we can delete it, see method description.
							pcoFileList.add( new File( filesFolder, fileName ) );
						}
					
					}
					else {
						// the field did not contain a file
						htmlResponse.println("file; name=" + formPartName + "; EMPTY");

					}

				}
			} // while next form part
			
			if ( haveBatchFile )	
			{
				/**
				 * Finally enable the batch file. THIS MUST BE DONE LAST. The command file is processed asynchronously by fedoraApp,
				 * so we need to have all the work files in place prior to installing the command file. If this is a REPLY_WITH_PID ingest
				 * we will split the batch file here so that we can insert the pid into the individual ingest files.
				 */
		
				batchCmdFile = new File( workFolder,  batchCommandFileName_DISABLED ); // still has disable suffix
	
				BatchIngestOptions batchOptions = FedoraAppUtil.loadRemoteIngestOptions( FedoraProxyConstants.getServletContextListener(), institution, batchSet );
				batchOptions = MetsBatchFileSplitter.setCommandLineOptions( batchOptions, batchCmdFile );
				switch (batchOptions.getAddCommandType()) {
				case PID_IN_OBJID:
				case NORMAL:
						// enable batch file
					batchCmdFile.renameTo( new File( workFolder, batchCommandFileName ) ); batchOptions.setSplitXMLinWorkDirToMets( false ); 
					   // batchCmdFile now has outdated info, yea java, update it.
					batchCmdFile        =  new File( workFolder, batchCommandFileName );
					break;
				case REPLY_WITH_PID:
						// split the batch file
					batchOptions.setSplitXMLinWorkDirToMets( false );  
					String fedoraHost     = ProgramProperties.getInstance( FedoraProxyConstants.getServletContextListener().getFedoraProxyProgramPropertiesURL() ).getProperty( FedoraProxyConstants.FedoraProxy_FEDORA_HOST_PROPERTY );
					String fedoraPort     = ProgramProperties.getInstance( FedoraProxyConstants.getServletContextListener().getFedoraProxyProgramPropertiesURL() ).getProperty( FedoraProxyConstants.FedoraProxy_FEDORA_PORT_PROPERTY );
					String fedoraUser     = ProgramProperties.getInstance( FedoraProxyConstants.getServletContextListener().getFedoraProxyProgramPropertiesURL() ).getProperty( FedoraProxyConstants.FedoraProxy_FEDORA_USER_PROPERTY );
					String fedoraPassword = ProgramProperties.getInstance( FedoraProxyConstants.getServletContextListener().getFedoraProxyProgramPropertiesURL() ).getProperty( FedoraProxyConstants.FedoraProxy_FEDORA_PWD_PROPERTY );
					
					pidMap = MetsBatchFileSplitter.splitMetsBatchFile_version_2( batchOptions, null, batchCmdFile, urlHandler.getMetsNewFolderURL().getFile(), urlHandler.getMetsUpdatesFolderURL().getFile(), false,
							fedoraHost, fedoraPort, fedoraUser, fedoraPassword );					
					
						// move batch file to completed folder
					batchCmdFile.renameTo( new File( completedBatchFilesFolder, batchCommandFileName ) ); 
	
					htmlResponse.println( "\n\nThe following PIDs have been 'Reserved' and will be assigned when the next ADR ingest occurs.\n");
					htmlResponse.println( "At completion of the ingest a pass/fail email will be sent\n\n");
	
					Set<Map.Entry<String,String>> pidEntrySet = pidMap.entrySet();
					Iterator<Map.Entry<String, String>> pidIterator = pidEntrySet.iterator();
					while ( pidIterator.hasNext() )
					{
						Map.Entry<String,String> oneEntry = pidIterator.next();
						String objID       = oneEntry.getKey();
						String assignedPid = oneEntry.getValue();
	
						htmlResponse.println( "ReservedPID=\""+assignedPid+"\" OBJID=\""+objID+"\" \n");
					}
	
					break;
				default:
					break;
				}
	
				htmlResponse.println( "\n\n</body>\n</html>" );
				htmlResponse.flush();
				
			} // if haveBatchFile

		} catch (Exception e) {

			StringBuffer message = new StringBuffer("\nERROR:"+e.getLocalizedMessage()+"\n " );
					
			Set keys = request.getParameterMap().keySet();
			Iterator keyIterator = keys.iterator();
			if ( keyIterator.hasNext() )
			{
				message.append( "Parameters found are..\n" );
			}
			while ( keyIterator.hasNext() )
			{
				String key = (String) keyIterator.next();
				String values[] =  (String[]) request.getParameterMap().get( key );
				if ( values != null ){
					message.append( key+"="+values[0] );
				}
			}
			response.sendError( 500, message.toString() );

			// move batch file to failed folder
			// not sure of current name, so just attempt to move both.
			if ( batchCmdFile != null )
			{	
				batchCmdFile.renameTo( new File( failedBatchFileFolder, batchCommandFileName) );
			}
			else 
			{
				if ( batchCommandFileName_DISABLED != null )
				{	
					batchCmdFile = new File( workFolder,  batchCommandFileName_DISABLED ); 
					batchCmdFile.renameTo( new File( failedBatchFileFolder, batchCommandFileName) );
				}
			}
			
			/* move PCOs to failed folder
			 * NOTE: If the error was caused by PCOs of the same name already existing in the files folder, they will not yet be in the
			 *  pcoFileList and will NOT be moved, we don't wan't to break a previous ingest that is in process.
			 */
			for( File t : pcoFileList )
			{
				t.renameTo( new File( failedBatchFileFolder, t.getName()) );
			}

			logger.error( e.getMessage() );  
		}
		finally 
		{
			// TBD
		}
	}


	/**
	 * Verify that this institution is allowed to ingest into the batch set based on webSiteCollection.properties file.
	 * 
	 * @param institution
	 * @param batchSet
	 * @return
	 */
	private boolean verifyRemoteAllowed(String institution, String batchSet) {
		
		ProgramProperties myFile = ProxyController.getWebsiteCollectionPropertesFile();
		
		String temp = myFile.getProperty( institution );
		
		if ( temp== null || temp.length()==0 ){
			return false;
		}
		
		String[] allowedBatchSets = temp.split( "," );
		
		for ( int i=0; i<allowedBatchSets.length; i++ )
		{
			if ( allowedBatchSets[i].trim().equalsIgnoreCase( batchSet ) ) 
				
				return true;
		}

		String msg = "the webSiteCollection.properites file does not contain the batchSet:"+batchSet;
		this.logger.warn( msg );
		System.out.println( msg );
		
		return false;
	}

	/**
	 * Write the file part of a multipart form to a directory, write status to the printWriter(form response).
	 * 
	 * @param formPartName the name for this part of the form, in the multi-part form
	 * 
	 * @param fileName The file name, no path
	 * @param filePart
	 * @param destination the path and name of the destination directory
	 * @param out anything written to this object is returned to the client.
	 * @throws Exception
	 */
	private void writeFile( String formPartName, String fileName, FilePart filePart, String destination, PrintWriter out ) throws Exception
	{
		File directory = new File( destination );
		File file      = new File( destination + fileName );

		if ( ! directory.isDirectory()) {
			throw new Exception("The Supplied upload directory:" + destination + ", is invalid." );
		}
		
		if ( file.exists() ){
			throw new Exception("The Supplied file already exists:" + destination + fileName );
		}
		
		long size = filePart.writeTo( file );
		fileName = fileName.replace( DISABLE_CMD_FILE_SUFFIX, "" );
		out.println("file; name=" + formPartName + "; filename=" + fileName +
				", filePath=" + filePart.getFilePath() +
				", content type=" + filePart.getContentType() +
				", size=" + size);
	}


} // IngestECTDcontroller

