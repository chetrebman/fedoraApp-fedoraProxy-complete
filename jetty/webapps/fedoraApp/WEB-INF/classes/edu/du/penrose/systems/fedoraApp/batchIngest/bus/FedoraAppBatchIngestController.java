/*
 * Copyright 2011 University of Denver
 * Author Chet Rebman
 * 
 * This file is part of FedoraApp.
 * 
 * FedoraApp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FedoraApp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with FedoraApp.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.du.penrose.systems.fedoraApp.batchIngest.bus;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.axis.types.NonNegativeInteger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Comment;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.filter.ElementFilter;
import org.jdom.filter.Filter;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.XSLTransformException;

//import fedora.client.utility.ingest.Ingest;   // 2.2
import org.fcrepo.client.utility.ingest.Ingest; //3.4 

//import fedora.server.types.gen.Datastream;  // 2.2
import org.fcrepo.server.management.FedoraAPIM;
import org.fcrepo.server.types.gen.Datastream; // 3.4

import edu.du.penrose.systems.fedoraApp.data.Pco;
import edu.du.penrose.systems.exceptions.FatalException;
import edu.du.penrose.systems.fedora.client.Administrator;
import edu.du.penrose.systems.fedora.client.Downloader;

import edu.du.penrose.systems.fedora.client.objecteditor.Util;

import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraApp.ProgramProperties;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestOptions;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestURLhandler;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestXMLhandler;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.ExtRelDefinitions;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.ExtRelList;
import edu.du.penrose.systems.fedora.ResourceIndexUtils;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.ThreadStatusMsg;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.TransformMetaData;
import edu.du.penrose.systems.fedoraApp.reports.PidReport;
import edu.du.penrose.systems.fedoraApp.util.MetsBatchFileSplitter;
import edu.du.penrose.systems.util.FileUtil; 

import edu.du.penrose.systems.fedoraApp.util.FedoraAppUtil;

/**
 * 
 * NOTE: You cannot update an existing datastream label!!<br>
 * NOTE: An exception is thrown if an object add does not contain a DC datastream.<br>
 * <br>
 * NOTE: The program relies on the ability to getNextPID( from fedora so that we
 * can embed it in the handle, and the foxml.
 * <br>
 * @author Chet Rebman
 *
 */
public class FedoraAppBatchIngestController extends BatchIngestController {

	private int totalFilesAddedSuccess    = 0;
	private int totalFilesAddedFailed    = 0;
	private int totalFilesUpdatedSuccess = 0;
	private int totalFilesUpdatedFailed  = 0;

	private TransformMetaData XSLTransformer = null;
	private String fedoraContext = null;

	// pcoFileList is used to move all completed PCOs to completed folder after successful ingest.
	private List<File> pcoFileList = null;

	private PidReport pidReport = null;

	/**
	 * Create a new FedoraAppBatchIngestController for an individual batch set.
	 * 
	 * @param administrator Gives access to fedora SOAP stubs and Fedora APIs.
	 * @param XSLTransformer Performs the XSLT to transform input XML to FOXML.
	 * @param institution The institution name for the batch set (ie codu).
	 * @param batchSet  The name of this batch set (ie ectd or frid) (Used to name reports).
	 * @param fedoraContext The context used by fedora when creating the fedora objects unique identifier (PID) This context must already be known by 
	 * fedora (specified in fedora.fcfg) This is usually the same as the institution name (ie codu).
	 * @param batchOptions Options for running this batch set (in a web environment
	 * this is obtained from a web form)
	 * @param  xmlRecordUpdates true when updating a record, false if new record.
	 * @throws FatalException thrown to stop execution of batch set.
	 * @deprecated because the parameter xmlRecordUpdates is no longer needed.
	 */
//	public FedoraAppBatchIngestController( Administrator administrator, TransformMetaData XSLTransformer, String institution, String batchSet, 
//			String fedoraContext, BatchIngestOptions batchOptions, boolean xmlRecordUpdates  )  throws FatalException 
//			{    
//		super ( institution, batchSet, batchOptions );
//
//		this.XSLTransformer = XSLTransformer;
//		FedoraAppBatchIngestController.administrator  = administrator;
//		this.fedoraContext  = fedoraContext;       
//			}

	/**
	 * Create a new FedoraAppBatchIngestController for an individual batch set.
	 * 
	 * @param XSLTransformer Performs the XSLT to transform input XML to FOXML.
	 * @param institution The institution name for the batch set (ie codu).
	 * @param batchSet  The name of this batch set (ie ectd or frid) (Used to name reports).
	 * @param fedoraContext The context used by fedora when creating the fedora objects unique identifier (PID) This context must already be known by 
	 * fedora (specified in fedora.fcfg) This is usually the same as the institution name (ie codu).
	 * @param batchOptions Options for running this batch set (in a web environment this is obtained from a web form)
	 * @throws FatalException thrown to stop execution of batch set.
	 */
	public FedoraAppBatchIngestController( TransformMetaData XSLTransformer, String institution, String batchSet, 
			String fedoraContext, BatchIngestOptions batchOptions )  throws FatalException 
	{    
		super ( institution, batchSet, batchOptions );

		this.XSLTransformer = XSLTransformer;
		
		this.fedoraContext  = fedoraContext;       
	}

	@Override
	protected void runBatch() throws FatalException 
	{
		
		this.getBatchOptions().setBatchIsUpdates( false ); 
		performBatchOperation( this.batchIngestXMLhandler_new );   	
		this.setTotalFilesAddedSuccess( this.getCurrentCompleted() );
		this.setTotalFilesAddedFailed( this.getCurrentFailed() );
		
		if ( ! this.getHaltCommand() )
		{
			this.getBatchOptions().setBatchIsUpdates( true );
			performBatchOperation( this.batchIngestXMLhandler_updates ); 
			this.setTotalFilesUpdatedSuccess( this.getCurrentCompleted() );
			this.setTotalFilesUpdatedFailed( this.getCurrentFailed() );
		}
		
		this.getReport().outputSeperateLineToReport( "Total New:"+this.getTotalFilesAddedSuccess() + "  Total Updated:"+this.getTotalFilesUpdatedSuccess() );
		
		this.pidReport.closePidReport();		
	}


	@SuppressWarnings("unchecked")
	private void performBatchOperation( BatchIngestXMLhandler batchIngestXMLhandler ) throws FatalException
	{
		this.setBatchIngestXMLhandler( batchIngestXMLhandler );

		try {
			
			/**
			 * Initialize the pid report if needed, since a single ingest can contain both adds and updates,
			 * if we are processing updates it may have already contain the results of a previous run performing add's
			 * within the same batch ingest.
			 */
			if ( this.pidReport == null ) {
				this.pidReport   = new PidReport( this.getBatchURLhandler().getNewPidReportLogingStream() ); 
				this.pidReport.write( FedoraAppConstants.PID_REPORT_HEADER );
			}
			String pid     = null;
			String objID   = null;

			Document xmlDocument     =  null;
			
			/**
			 * Let's try to login to Fedora, if we can't, retry every 10 seconds, We will top logging after 10 attempts, but
			 * will hang here forever! The fedoraApp status screen is updated and the user should be able to click the 
			 * 'Stop Ingest' button.
			 */
			boolean fedoraReady = false;
			int logErrorCount = 0;
			do {
				try {
					FedoraAppUtil.getAdministrator( this.getBatchOptions() ); // verify that we are logged in.
					fedoraReady = true;
				}
				catch ( Exception e )
				{
					logErrorCount++;
					this.updateStatusMsg( "ERROR: Unable to login to Fedora" );
					if ( logErrorCount < 10 ){
						this.logger.error( "Unable to login to Fedora"+e.getMessage() );
					}
					if ( logErrorCount == 10 ){
						this.logger.error( "Still unable to login to Fedora, this error will longer be logged." );
					}

					try {
						Thread.sleep( 10*1000 );
					} catch (InterruptedException e1) { 	}
				}
			} while ( ! fedoraReady );

			if ( this.XSLTransformer == null ) 
			{
				String errorMsg = "XSLTransformer not set! ";
				this.logger.fatal( errorMsg );
				throw new FatalException( errorMsg );
			}  

			String validate    = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.BATCH_INGEST_XML_VALIDATE_PROPERTY);
			String schemaCheck = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.BATCH_INGEST_XML_SCHEMA_CHECK_PROPERTY);

			while ( ! this.getHaltCommand() && this.getBatchIngestXMLhandler().hasNext() && ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.BATCH_INGEST_DISABLE_STOP_BATCH_PROPERTY )== null ) {
				try {
					try {

						pcoFileList = new ArrayList<File>();

						// get next xml document and check validity

						xmlDocument = this.getBatchIngestXMLhandler().getNextXMLdocument( new Boolean( validate ), new Boolean( schemaCheck ) );

						Iterator<Comment> commandLineIterator =  xmlDocument.getDescendants( new CommandLineFilter() );  

						if ( ! commandLineIterator.hasNext() ) 
						{
							throw new FatalException( "ERROR:Batch Version 2 command line not found at top of xml file" );
						}
					
						Comment commandLineComment = commandLineIterator.next(); // we assume only one.
						String         commandLine = commandLineComment.getText();
				
						boolean validCommandLine = MetsBatchFileSplitter.parseCommandLine( this.getBatchOptions(), commandLine );
						if ( ! validCommandLine )
						{
							throw new Exception( "ERROR: invalid command line = " + commandLine );
						}

						/**
						 * The islandora collection and contentModel is set by the user or the  {batchSet}_REMOTE.properties file, unless 
						 * the batchSet is 'mixed' in which case IT IS OVER-RIDDEN by an dmdAllianceElement in the mets file.
						 */
						if ( this.getBatchOptions().getBatchSet().equalsIgnoreCase( FedoraAppConstants.MIXED_CONTENT_DIRECTORY ) )
						{
							Iterator<Element> adrDmdSectionIterator = xmlDocument.getDescendants( new AdrDmdSectionFilter() );
							if ( adrDmdSectionIterator.hasNext() )
							{
								Element amdSection = adrDmdSectionIterator.next();
								MetsBatchFileSplitter.parseDmdAllianceElement( this.getBatchOptions(), amdSection ); 
							}
							else {
								throw new Exception( "ERROR: a 'mixed' batch set ingest does not contain a <mets:dmdSec ID='dmdAlliance'>  section");
							}
						}
						
						
						boolean haveCollectionPID   = true;
						boolean haveContentModelPID = true;
						if ( this.getBatchOptions().getFedoraCollection() == null 
								|| this.getBatchOptions().getFedoraCollection().equals(FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE))
						{
							haveCollectionPID = false;
						}
						if ( this.getBatchOptions().getFedoraContentModel() == null 
								|| this.getBatchOptions().getFedoraContentModel().equals(FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE) )
						{
							haveContentModelPID = false;
						}
						
						/**
						 * At this point we may or may not have an islandora collection and contentModel. If performing
						 * an Islandora ingest verify valid collection and contentModel. <BR>
						 * NOTE: Islandora ingest is set by the ISLANDORA_INGEST property in
						 * config/batchIngest.properites.
						 */
						
						switch ( FedoraAppUtil.getIngestType() ){
						case FEDORA:
							break;
						case ISLANDORA:
							if ( (haveCollectionPID && haveContentModelPID) )
							{
								boolean doIt = true; // this is nice for debugging
								if ( doIt ){
									ResourceIndexUtils.verifyValidCollectionAndContentModel( FedoraAppUtil.getAdministrator( this.getBatchOptions()), this.getBatchOptions() );
								}
							}
							else 
							{
								//throw new FatalException( "An islandora ingest must have a collectionPID: "+this.getBatchOptions().getFedoraCollection() 
								//		+ " and a contentModelPID: "+this.getBatchOptions().getFedoraContentModel() );
								throw new Exception( "An islandora ingest must have a collectionPID: "+this.getBatchOptions().getFedoraCollection() 
										+ " and a contentModelPID: "+this.getBatchOptions().getFedoraContentModel() );
							}
							break;
						default:
							break;	
						}
							
						this.updateStatusMsg( "Processing: "+this.getBatchIngestXMLhandler().getCurrentDocumentName() );

						switch( this.getBatchOptions().getIngestCommand() ){
						case ADD:	
							switch ( this.getBatchOptions().getAddCommandType() ){
							
							case PID_IN_OBJID:
							case REPLY_WITH_PID: 
								pid = getPidFromOBJID( xmlDocument );
								this.addNewObject( xmlDocument, validate, schemaCheck, pid );
								break;
							case NORMAL:
							default: 
								pid = this.addNewObject( xmlDocument, validate, schemaCheck, null );
								break;
							}					
							break;
						case UPDATE:	 
							pid = this.updateExistingObject( xmlDocument, this.getBatchOptions() );
							break;	
						default:
							throw new Exception( "ERROR: Unknown ingest command" );
						}

						this.markAsCompleted();

						/*
						 * If user selected, Move PCOs to files directory with a unique name appended so that they do not get overwritten.
						 */
						if ( this.getBatchOptions().isMoveIngestedPCOsToCompleted() ) {
							for ( int i=0; i < this.pcoFileList.size(); i++ ) 
							{			 
								String newFileName = edu.du.penrose.systems.util.FileUtil.getBatchUniqueFileName( this.pcoFileList.get( i ).getPath(),  this.getBatchOptions() );

								File newFile = new File (  newFileName );
								this.pcoFileList.get( i ).renameTo( newFile );																
								
								BatchIngestURLhandler.transferFileToURL( newFile, this.getBatchURLhandler().getCompletedFilesFolderURL() );
							}
						}

						// Append pid, objID and dmdSec ID TO PID Report
						objID = this.getOBJIDfromMets( xmlDocument );
						StringBuffer dmdSecIDs = new StringBuffer();

						Iterator<Element> amdSecterator =  xmlDocument.getDescendants( new DmdSectionForModsFilter() );  
						while ( amdSecterator.hasNext() ) 
						{
							dmdSecIDs.append( amdSecterator.next().getAttributeValue( "ID" ) ); 
							if ( amdSecterator.hasNext() ){ dmdSecIDs.append( "," ); };
						}
						
						this.pidReport.write( pid+","+objID+"," +dmdSecIDs+ ","+ this.getBatchIngestXMLhandler().getCurrentDocumentName());
						
						this.pidReport.newLine();

						this.updateStatusMsg( "Ingest Success for "+this.getBatchIngestXMLhandler().getCurrentDocumentCanonicalPath() );
					}
					catch ( XSLTransformException e ) {
						String errorMsg = "Transform to FOXML FAILED for "+this.getBatchIngestXMLhandler().getCurrentDocumentCanonicalPath() + " "+e.getMessage();
						this.logger.error( errorMsg );
						markAsFailed( errorMsg );
					}
					catch ( JDOMException e ) {
						String errorMsg = "Unable to create DOM from supplied input: "+this.getBatchIngestXMLhandler().getCurrentDocumentCanonicalPath()+ " " + e.getMessage();
						this.logger.error( errorMsg );
						markAsFailed( errorMsg );
					}
					catch ( FatalException  e ) {
						// assume error has already have been logged.
						//   this.report.outputSeperateLineToReport( e.getMessage() );
						//   this.report.finishReport();
						
						String errorMsg = this.getBatchIngestXMLhandler().getCurrentDocumentName() + ": " + e.getMessage();
						throw new FatalException ( errorMsg ); 
					}
					catch ( Exception e ) {
						String errorMsg = "Ingest FAILED for "+this.getBatchIngestXMLhandler().getCurrentDocumentCanonicalPath() + " "+e.getMessage();
						this.logger.error( errorMsg );
						markAsFailed( errorMsg );
					}
				}
				catch ( FatalException  e ) {
					// assume error has already have been logged.
					this.getReport().outputSeperateLineToReport( e.getMessage() );
					//   this.report.finishReport();
					throw e;
				}

			} // while
		}
		finally 
		{
			this.pidReport.flushPidReport();
		}
	}


	String getPidFromOBJID( Document xmlDocument ) throws Exception
	{
		String pid="";
		Iterator<Element> idIterator =  xmlDocument.getDescendants( new MetsFilter() );
		if ( idIterator.hasNext() )
		{
			String uniqueIDValue = idIterator.next().getAttributeValue("OBJID");
			if ( uniqueIDValue != null && uniqueIDValue.length() > 0 )
			{
				// check if the Fedora Digital Object  exists.
				try{
					Util.getObjectFields( FedoraAppUtil.getAdministrator( this.getBatchOptions()).getAPIA(), uniqueIDValue, new String[] {"pid"} );
					this.logger.error( "Attempt to add new object, but it already exists! "+uniqueIDValue);
					throw new Exception( "ERROR: attempt to update non-existant object="+uniqueIDValue );
				}
				catch ( Exception e )
				{
					// an exception means the object does not exist.
				}

				pid =  uniqueIDValue;    
			}
			else
			{
				throw new Exception( "ERROR: attempt to add with type=pidInOBJID, but OBJID is not set" );
			}
		}

		else {
			throw new Exception( "ERROR: attempt to add with type=pidInOBJID, but OBJID is not set" );
		}
		
		return pid;
	}
	
	/**
	 * NOTE: Update the datastreams within the Ingest document, then update it in Fedora, We assume a DC datastream always exists, 
	 * we use this to test for existing object. <br>
	 * <BR>
	 * <mets:mdWrap MIMETYPE="text/xml" MDTYPE="MODS">  MDTYPE is used for the datastream id
	 * <br>
	 * <br> NOTE: We update based on datastream ID, this why you cannot update an existing datastream 
     * label in Fedora!!
	 * 
	 * @param admin
	 * @param pid
	 * @param xmlDocument
	 * @throws FatalException
	 */
	void updateFedoraXmlDatastreams( Administrator admin, String pid, org.jdom.Document xmlDocument ) throws FatalException
	{
		try {
				
			String dsID    = "";
			
				// update  datastream's within the document with the handle (label update in Fedora is ignored).
			
			this.addFoxmlPid_AddHandleAndUpdateLabelInModsDataStream( xmlDocument, pid, FedoraAppConstants.FEDORA_MODS_DATASTREAM_LABEL, new ModsInMetsFilter() ); 
			this.addHandleAndUpdateLabelInDcDataStream(   xmlDocument, pid, FedoraAppConstants.FEDORA_DC_DATASTREAM_LABEL,   new DcInMetsFilter() );    

			Iterator<Element> xmlDataIterator =  xmlDocument.getDescendants( new XmlDataFilter() );
			Element xmlData = null;
			while ( xmlDataIterator.hasNext() )
			{ 
				xmlData  = xmlDataIterator.next();
				dsID     = xmlData.getParentElement().getAttribute( "MDTYPE" ).getValue();
				
				
				if ( xmlData.getContent().size() != 3 ){
					throw new FatalException( "Incorrect content count when trying to parse xmlData for update" );
				}
				xmlData = (Element) xmlData.getContent(1); // 0 = /n 2 = /n

				Format xmlFormat = Format.getPrettyFormat();

				XMLOutputter outputter = new XMLOutputter( xmlFormat );
				
				//		byte[] dsContent = getBytesFromFile( xmlFile );
				byte[] dsContent = outputter.outputString( xmlData ).getBytes();
				
				// update based on datastream ID this is why the datastream label will be ignored.
				updateFedoraXmlDataStream( admin, pid, dsID, "text/xml", dsContent );
			}
		}
		catch ( Exception e )
		{
			throw new FatalException( "ERROR: updating datastreams:"+e);
		}
	}
	
	/**
	 * Update a PCO datastream, if the datastream does not exist try to addit
	 * 
	 * @param admin
	 * @param pid
	 * @param pcoFiles
	 * @throws FatalException
	 */
	void updateFedoraPcoDataStreams( Administrator admin, String pid, Set<Pco> pcoFiles ) throws FatalException
	{
		String dsIDsave = "";
		try 
		{
			Iterator<Pco> fileIterator = pcoFiles.iterator();
			while ( fileIterator.hasNext() )
			{	
				Pco currentPco = fileIterator.next();
				dsIDsave = currentPco.getDsID();
				try
				{
					Downloader dl = new Downloader( admin, admin.getHost(), admin.getPort(), admin.getUserName(), admin.getUserPassword() );
					
					InputStream is = dl.getDatastreamContent( pid, currentPco.getDsID(), null );
					is.close();
					updateObjectDataStream( admin, pid, currentPco.getDsID(), currentPco.getMimeType(), currentPco.getFile() );
				}
				catch( Exception e )
				{		
					addDataStreamToExistingObject( admin, pid, currentPco.getDsID(), currentPco.getMimeType(), currentPco.getFile() );
				}
			}
		}
		catch ( Exception e )
		{
			String errorMsg = "ERROR: Unable to update pid, datastream, "+pid+", "+ dsIDsave+" :"+e;
			markAsFailed( errorMsg );
			this.pidReport.write( pid+","+FedoraAppConstants.PID_REPORT_UPDATE_FAILED_MARKER+"," + this.getBatchIngestXMLhandler().getCurrentDocumentName());
			throw new FatalException( errorMsg );
		}
	}
	
	/**
	 * Update the actual datastream in Fedora
	 * 
	 * @param admin
	 * @param pid
	 * @param dsID
	 * @param mimeType
	 * @param dsContent byte array containing object. TBD LOOKS LIKE OUT OF MEMEORY WAITING TO HAPPEN.
	 * @throws RemoteException
	 */
	void updateFedoraXmlDataStream( Administrator admin, String pid, String dsID, String mimeType, byte[] dsContent ) throws RemoteException
	{	
		String[] altIDs = new String[]{};
		boolean versionabe = true;
		String formatURI = null;
		String controlGroup = "X";
		String dsState = null; // don't change the object state
		String checksupType = null;
		String checksum = null;
		String logMessage = "";

		Datastream ds = admin.getAPIM().getDatastream(pid, dsID, null );
		String dsLabel = ds.getLabel();		

		boolean force = false;

		logMessage = "Update datastream:"+dsID;
		logger.info( logMessage );

		Object result = admin.getAPIM().modifyDatastreamByValue(pid, dsID, altIDs, dsLabel, mimeType, formatURI, dsContent,  checksupType, checksum, logMessage, force);       				
	}
	
	/**
	 * Add a datastream to an existing object.
	 * 
	 * @param admin
	 * @param pid
	 * @param dsID
	 * @param mimeType
	 * @param dsContentLocation
	 * @throws Exception
	 */
	void addDataStreamToExistingObject( Administrator admin, String pid, String dsID, String mimeType, File managedFile ) throws Exception
	{
		try
    	{
        	String location = admin.getUploader().upload(managedFile);
        	
			String[] altIDs = new String[]{};
			String dsLabel = "Thumbnail.php";
			boolean versionabe = false;
			String formatURI = null;
			String dsLocation = location;
			String controlGroup = "M";
			String dsState = "A";
			String checksupType = null;
			String checksum = null;
			String logMessage = "add new datastream";
			
			String dsIDreturned = admin.getAPIM().addDatastream(pid, dsID, altIDs, dsLabel, versionabe, mimeType, formatURI, dsLocation, controlGroup, dsState, checksupType, checksum, logMessage);
    		
        } catch ( Exception e) {
            System.out.println( "Exception: "+e.getMessage());
        }
	}
	
	/**
	 * Upload an update an existing datastream for an existing object.
	 * 
	 * @param admin
	 * @param pid
	 * @param dsID
	 * @param mimeType
	 * @param dsContentLocation
	 * @throws Exception if the datastream does not exist
	 */
	void updateObjectDataStream( Administrator admin, String pid, String dsID, String mimeType, File dsContentLocation ) throws Exception
	{	
		String[] altIDs = new String[]{};
		boolean versionabe = true;
		String formatURI = null;
		String controlGroup = "X";
		String dsState = null; // don't change the object state
		String checksupType = null;
		String checksum = null;
		String logMessage = "";

		Datastream ds = admin.getAPIM().getDatastream(pid, dsID, null );
		String dsLabel = ds.getLabel();		

		boolean force = false;

		logMessage = "Update datastream:"+dsID;
		logger.info( logMessage );
		
		// upload file and get temp file url, fedoraFileLocation is of type uploaded://1415"
		String fedoraFileID = FedoraAppUtil.getAdministrator( this.getBatchOptions() ).getFedoraClient().uploadFile( dsContentLocation );  
				
		Object result = admin.getAPIM().modifyDatastreamByReference(pid, dsID, altIDs, dsLabel, mimeType, formatURI, fedoraFileID,  checksupType, checksum, logMessage, force);       				
	}

	/**
	 * Check that the document exists is Fedora and then update all datastream's contained in the xmlDocument 
	 * 
	 * @param xmlDocument
	 * @param batchIngestOptions used to get update command type.
	 * @return The pid that has been updated.
	 * @throws Exception
	 */
	private String updateExistingObject( Document xmlDocument, BatchIngestOptions batchIngestOptions ) throws Exception
	{
		String pid = null;
		
		/**
		 * Look for a PID in the OBJID field and verify that the object exists.
		 */
		Iterator<Element> idIterator =  xmlDocument.getDescendants( new MetsFilter() );
		if ( idIterator.hasNext() )
		{
			String uniqueIDValue = idIterator.next().getAttributeValue("OBJID");
			if ( uniqueIDValue != null && uniqueIDValue.length() > 0 )
			{
				// verify that the Fedora Digital Object exists.
				try{
					Util.getObjectFields( FedoraAppUtil.getAdministrator( this.getBatchOptions() ).getAPIA(), uniqueIDValue, new String[] {"pid"} );
				}
				catch ( Exception e )
				{
					this.logger.error( "Unable to update object: "+e.getLocalizedMessage() );
					throw new Exception( "ERROR: attempt to update non-existant object="+uniqueIDValue );
				}

				pid =  uniqueIDValue;  
				
					// let's do some tests
				
				if ( ! pid.startsWith( this.getBatchOptions().getInstitution() ) ){
					throw new Exception( "ERROR: Attempt to update an object outside of current namespace (institution) " );
				}
				
				if ( ! ResourceIndexUtils.isObjectInCollection( FedoraAppUtil.getAdministrator( this.getBatchOptions() ), this.getBatchOptions().getFedoraCollection(), pid ) ){
					throw new Exception( "ERROR: Attempt to update an object:"+pid+" outside of current collection:"+this.getBatchOptions().getFedoraCollection() );
				}
				 
				String handle = getHandle( xmlDocument ); // handle not required, but if there is one make sure it matches
				if ( handle != null && ! handle.contains( pid ) ){
					throw new Exception( "ERROR: the handle does not contain the OBJID(pid)!" );
				}
				
					// everything looks good, perform the update
				
				switch ( batchIngestOptions.getUpdateCommandType() )
				{
				case ALL:
					this.updateFedoraXmlDatastreams( FedoraAppUtil.getAdministrator( this.getBatchOptions() ), pid, xmlDocument );	
					this.updateFedoraPcoDataStreams( FedoraAppUtil.getAdministrator( this.getBatchOptions() ), pid, this.getPCOsForDSupdate( xmlDocument ) );
					break;
				case META:
					this.updateFedoraXmlDatastreams( FedoraAppUtil.getAdministrator( this.getBatchOptions() ), pid, xmlDocument );	
					break;
				case PCO:
					this.updateFedoraPcoDataStreams( FedoraAppUtil.getAdministrator( this.getBatchOptions() ), pid, this.getPCOsForDSupdate( xmlDocument ) );
					break;
				case NOT_SET:
				default:
					throw new Exception( "ERROR: invalid command type = "+batchIngestOptions.getUpdateCommandType() );
				}	       
			}
			else
			{
				throw new Exception( "ERROR: attempt to update, but OBJID is not set" );
			}
		}
		else {
			throw new Exception( "ERROR: attempt to update, but OBJID is not set" );
		}
		
		return pid;
	}


	private String getHandle( Document xmlDocument ) 
	{
		String handle = null;
		
		Iterator<Element> xmlDataIterator =  xmlDocument.getDescendants( new HandleInModsLocation() );
		Element xmlData = null;
		while ( xmlDataIterator.hasNext() )
		{ 
			xmlData = xmlDataIterator.next();
			handle  = xmlData.getParentElement().getValue();
		}
		
		return handle;
	}

	/**
	 * Used to find the handle within a MODS recordInf0/location element
	 * <mods:recordInfo> 
	 *    <mods:location>
	 *    <mods:url usage="primary display">http://hdl.handle.net/10176/codu:60015</mods:url>
	 *    </mods:location>
	 * </mods:recordInfo>
	 * @author chet
	 *
	 */
	class HandleInModsLocation implements Filter {

		public boolean matches( Object testObj ) {
			if ( Element.class.isAssignableFrom( testObj.getClass() ) ) {
				Element element = (Element) testObj;
				if ( element.getName().compareToIgnoreCase("location") == 0) {
					if ( element.getParentElement().getName().compareToIgnoreCase( "recordInfo") == 0)
						if ( element.getParentElement().getParentElement().getName().compareToIgnoreCase( "mods") == 0)
						return true;
				}
			}
			return false;
		}
	}
	
	/**
	 * Take batch ingest <METS> XML file and perform the following actions.
	 * 
	 * TransformDocument to FOXML
	 *
	 * Get Unique PID and HANDLE [ apim.getNextPid() + ADR handler server ]
	 *
	 * Set FOXML datastream labels ie MODS or METS (should be in xform)
	 *
	 * Add PID and HANDLE to datastreams
	 *    <mods:identifier type="pid">codu:37863</mods:identifier>
	 *    <foxml:digitalObject ... PID="codu:224">
	 *    <mods:location>
	 *       <mods:url usage="primary display">http://hdl.handle.net/10136/codu:224</mods:url>
	 *    </mods:location>
	 *    <dc:identifier>http://hdl.handle.net/10136/codu:224</dc:identifier>
	 *
	 * Upload PCOs and get FedoraID ie uploaded://1415 [ FedoraClient.uploadFile() uses REST ]
	 *
	 * Embed FedoraID into FOXML
	 *     <foxml:datastream>...<foxml:contentLocation TYPE="URL" REF="uploaded://462" />
	 *     
	 * Validate FOXML document ( if set in batchIngestOptions )
	 *
	 * Ingest FOXML document [ Ingest.oneFromFile which uses apim.ingest() ]
	 *
	 * Check valid ingest. ( PID exists and has valid checksums )
	 *
	 * Add external relationships. (should be in xform) [ fedoraAPIM.addRelationship() ]
	 *
	 * @param xmlDocument
	 * @param validate
	 * @param schemaCheck
	 * @param reservedPid if not null, use this pid instead of getting a new one from Fedora.
	 * @return String the assigned pid
	 * @throws Exception
	 */
	private String addNewObject( Document xmlDocument, String validate, String schemaCheck, String reservedPid ) throws Exception
	{
		File foxmlFile = null;
		Document foxmlDocument   = null;

		// transform document to FOXML
		foxmlDocument = this.XSLTransformer.transformMetaToFOXML( xmlDocument );  // TBD type=simple is in xmlDocument ERROR!

		// get unique pid and adr handle.
		this.updateStatusMsg( "Get PID and Handle for: "+this.getBatchIngestXMLhandler().getCurrentDocumentName() );
		String pid = null;
		if ( reservedPid == null ){
			pid = this.getPidAndReserveHandle( fedoraContext );
		}
		else {
			pid = reservedPid;

			if ( handleServerEnabled() )
			{
				this.reserveHandle( pid );
			}
		}

		// embed handle url and label the Datastreams in the FOXML document
		boolean success = false;
		this.addFoxmlPid_AddHandleAndUpdateLabelInModsDataStream( foxmlDocument, pid, FedoraAppConstants.FEDORA_MODS_DATASTREAM_LABEL, new ModsInDatastreamFilter() ); 
		success = this.addHandleAndUpdateLabelInDcDataStream(   foxmlDocument, pid, FedoraAppConstants.FEDORA_DC_DATASTREAM_LABEL,   new DcInDatastreamFilter()   ); 

		//  * NOTE: As of Aug 7 2012, a DC datastream is no longer required. Islandora will create it's own.
		//if ( !success ){
		//    throw new Exception( "ERROR: DC datastream not found!" );
		//}
		
		this.updateLabelInMetsDataStream(         foxmlDocument,      FedoraAppConstants.FEDORA_METS_DATASTREAM_LABEL,  new MetsInDatastreamFilter());

		//Rather then add to foxml we send relationships directly to fedora after object is created, see this.addExternalRelationships() below.
		//		if ( !this.getBatchOptions().getExtRelList().isEmpty() ){
		//			if ( this.getBatchOptions().getExtRelList().isHasRelationshipEntity( edu.du.penrose.systems.fedoraApp.batchIngest.data.ExtRelList.ExtRel_COLLECTION_OBJECT ) )
		//			{
		//				// add external relationships for islandora
		//                this.addRelExtDatastreamToFoxml( foxmlDocument, pid );				
		//			}
		//		}


		// upload the primary content objects and embed temp url to them within the FOXML document
		this.updateStatusMsg( "Uploading PCO's for: "+this.getBatchIngestXMLhandler().getCurrentDocumentName() );
		this.uploadPrimaryContentObjectsEmbedTempURL( foxmlDocument );

		// save new FOXML document to work dir.
		foxmlFile = this.getBatchIngestXMLhandler().saveFOXMLtoWorkFolder( foxmlDocument );        

		this.updateStatusMsg( "Check and Ingest new FOXML document for: "+this.getBatchIngestXMLhandler().getCurrentDocumentName() );

		// check new FOXML is valid
		this.updateStatusMsg( "Check validity of new FOXML document" );
		this.getBatchIngestXMLhandler().buildDocumentCheckValid( foxmlFile,  new Boolean( validate ), new Boolean( schemaCheck ), new URL( FedoraAppConstants.FOXML_SCHEMA_URL ));

		// Ingest new FOXML document
		Ingest.oneFromFile(foxmlFile, "foxml1.0", FedoraAppUtil.getAdministrator( this.getBatchOptions() ).getAPIA(), FedoraAppUtil.getAdministrator( this.getBatchOptions() ).getAPIM(), null );

		// Check that object is really in fedora with correct pid. Throw exception if not found.
		this.updateStatusMsg( "Verify that Ingest was valid for: "+pid );

		this.checkForValidIngest( pid, this.pcoFileList );

		// Mark as completed, move files to completed directory
		// TBD Need to think about what to do if mark as completed FAils, after file is already ingested

		/**
		 * If islandora ingest, define external relationship based on value of collection and content model.
		 */

		
		
	    
		if ( FedoraAppUtil.getIngestType() == FedoraAppUtil.INGEST_TYPE.ISLANDORA )
	    {
			this.getBatchOptions().clearRelExtList();
			
	    	this.getBatchOptions().addIslandoraRelationship( this.getBatchOptions().getFedoraCollection(), this.getBatchOptions().getFedoraContentModel() );
	    		
			this.addExternalRelationships( FedoraAppUtil.getAdministrator( this.getBatchOptions() ).getAPIM(), pid, this.getBatchOptions().getExtRelList() );

			/*
			 * Thi ECTD thumbnail is no longer used, this just left as an example Aug-2012
			 */
			if ( this.getBatchOptions().isHasRelationshipEntity( ExtRelList.ExtRel_CONTENT_MODEL_OBJECT+FedoraAppConstants.ECTDmodelPID ) )
			{
				this.addECTDcollectionThumbnail( pid );
			}		
	    }
	    
	    String label   = null;
    	String ownerId = null;
	    if ( this.getBatchOptions().isSetObjectInactive() )
	    {    		
	    	String state = "I"; // A, I, D	    	
	    	String logMessage = "Set state inactive";
	    	FedoraAppUtil.getAdministrator( this.getBatchOptions() ).getAPIM().modifyObject(pid, state, label, ownerId, logMessage );
	    }
	    else 
	    {
	    	String state = "A"; // A, I, D
	    	String logMessage = "Set state active";
	 		FedoraAppUtil.getAdministrator( this.getBatchOptions() ).getAPIM().modifyObject(pid, state, label, ownerId, logMessage );
	    }
	    
		return pid;
	}

	/**
	 * Filter to find the xmlData elements (contain datastreams) within the top mets element
	 * 
	 * @author chet.rebman
	 *
	 */   
	class XmlDataFilter implements Filter {

		public boolean matches( Object testObj ) {
			if ( Element.class.isAssignableFrom( testObj.getClass() ) ) {
				Element element = (Element) testObj;
				if ( element.getName().compareToIgnoreCase("xmlData") == 0) {
					return true;
				}
			}
			return false;
		}   
	} 

	/**
	 * Filter to find the mets element
	 * 
	 * @author chet.rebman
	 *
	 */   
	class MetsFilter implements Filter {

		public boolean matches( Object testObj ) {
			if ( Element.class.isAssignableFrom( testObj.getClass() ) ) {
				Element element = (Element) testObj;
				if ( element.getName().compareToIgnoreCase("mets") == 0) {
					if ( element.getParentElement() == null)
						return true;
				}
			}
			return false;
		}   
	} 

	/**
	 * @deprecated No longer used, but is left as an example.
	 * 
	 * @param pid
	 */
	private void addECTDcollectionThumbnail(String pid) {

		try
		{
			File managedFile = new File( this.batchIngestXMLhandler_new.getUrlHandler().getImagesFolderURL().getFile()+"pdf.png" );

			String location = FedoraAppUtil.getAdministrator( this.getBatchOptions() ).getUploader().upload(managedFile);

			String dsID = "TN";
			String[] altIDs = new String[]{};
			String dsLabel = "Thumbnail.php";
			boolean versionabe = false;
			String mimeType = "image/png";
			String formatURI = null;
			String dsLocation = location;
			String controlGroup = "M";
			String dsState = null; // don't change the object state
			String checksupType = null;
			String checksum = null;
			String logMessage = "Ingest default PDF thumbnail";

			String dsIDreturned = FedoraAppUtil.getAdministrator( this.getBatchOptions() ).getAPIM().addDatastream(pid, dsID, altIDs, dsLabel, versionabe, mimeType, formatURI, dsLocation, controlGroup, dsState, checksupType, checksum, logMessage);

		} catch ( Exception e) {
			System.out.println( "Exception: "+e.getMessage());
		}

	}

	private void addExternalRelationships(FedoraAPIM fedoraAPIM, String pid, ExtRelList extRelList) throws FatalException 
	{
		Iterator<ExtRelDefinitions> extRelIterator = extRelList.getIterator();

		while( extRelIterator.hasNext() )
		{
			ExtRelDefinitions extRelDef = extRelIterator.next();
			try 
			{
				
				this.updateStatusMsg( "Add external relationships - RELS-EXT" );
				this.logger.info( "info:fedora/"+pid+" - "+ extRelDef.getPredicate() +" - " + extRelDef.getObject() );
				boolean result = fedoraAPIM.addRelationship( "info:fedora/"+pid, extRelDef.getPredicate(), extRelDef.getObject(), false, null );

				if ( result == false ){
					throw new FatalException( "Unable to add fedora Relationship" );
				}
			} catch (RemoteException e) {
				throw new FatalException( "Unable to add fedora Relationship:" + e );
			}
		}
	}


	/**
	 * Filter to find the MODS elements within a datastream element.
	 * @author chet.rebman
	 *
	 */
	class ModsInDatastreamFilter implements Filter {

		public boolean matches( Object testObj ) {
			if ( Element.class.isAssignableFrom( testObj.getClass() ) ) {
				Element element = (Element) testObj;
				if ( element.getName().compareToIgnoreCase("mods") == 0) {
					if ( element.getParentElement().getParentElement().getName().compareToIgnoreCase( "datastreamVersion") == 0)
						return true;
				}
			}
			return false;
		}
	} // ModsInDatastreamFilter

	/**
	 * Filter to find the MODS elements within a Mets element.
	 * @author chet.rebman
	 *
	 */
	class ModsInMetsFilter implements Filter {

		public boolean matches( Object testObj ) {
			if ( Element.class.isAssignableFrom( testObj.getClass() ) ) {
				Element element = (Element) testObj;
				if ( element.getName().compareToIgnoreCase("mods") == 0) {
					if ( element.getParentElement().getName().compareToIgnoreCase( "xmlData") == 0)
						return true;
				}
			}
			return false;
		}
	} // ModsInDatastreamFilter


	/*
	 * Filter to find mets:dms section that do NOT have an ADR id.
	 * 
	 * @see  FedoraAppConstant#DMD_ALLIANCE_ID
	 * @see edu.du.penrose.systems.fedoraApp.batchIngest.bus.FedoraAppBatchIngestController.DmdSectionForModsFilter
	 * @see edu.du.penrose.systems.fedoraApp.batchIngest.bus.FedoraAppBatchIngestController.AdrDmdSectionFilter
	 */
	class DmdSectionFilter implements Filter {

		public boolean matches( Object testObj ) {
			if ( Element.class.isAssignableFrom( testObj.getClass() ) ) {
				Element element = (Element) testObj;
				if ( element.getName().compareToIgnoreCase( FedoraAppConstants.METS_DMD_SEC_ELEMENT_NAME  ) == 0
						&& element.getAttribute("ID").getValue().compareTo(FedoraAppConstants.DMD_ALLIANCE_ID) != 0) {
					return true;
				}
			}
			return false;
		}

	} // DmdSectionFilter
	
	/*
	 * Filter to find mets:dms section that do NOT have an ADR id. and contains MODS xml
	 * 
	 * @see  FedoraAppConstant#DMD_ALLIANCE_ID
	 * @see edu.du.penrose.systems.fedoraApp.batchIngest.bus.FedoraAppBatchIngestController.DmdSectionFilter
	 * @see edu.du.penrose.systems.fedoraApp.batchIngest.bus.FedoraAppBatchIngestController.AdrDmdSectionFilter
	 */
	class DmdSectionForModsFilter implements Filter {

		public boolean matches( Object testObj ) {
			if ( Element.class.isAssignableFrom( testObj.getClass() ) ) {
				Element element = (Element) testObj;
				if ( element.getName().compareToIgnoreCase( FedoraAppConstants.METS_DMD_SEC_ELEMENT_NAME  ) == 0
						&& element.getAttribute("ID").getValue().compareTo(FedoraAppConstants.DMD_ALLIANCE_ID) != 0) {
					if ( element.getDescendants( new ModsInMetsFilter() ).hasNext() )
						return true;
				}
			}
			return false;
		}

	} // DmdSectionForModsFilter
	
	/**
	 * Filter to find the adr mets:dmd section
	 * 
	 * @see FedoraAppConstant#DMD_ALLIANCE_ID
	 * @see edu.du.penrose.systems.fedoraApp.batchIngest.bus.FedoraAppBatchIngestController.DmdSectionFilter
	 * @see edu.du.penrose.systems.fedoraApp.batchIngest.bus.FedoraAppBatchIngestController.DmdSectionForModsFilter
	 * @author chet.rebman
	 *
	 */
	class AdrDmdSectionFilter implements Filter {

		public boolean matches( Object testObj ) {
			if ( Element.class.isAssignableFrom( testObj.getClass() ) ) {
				Element element = (Element) testObj;
				if ( element.getName().compareToIgnoreCase( FedoraAppConstants.METS_DMD_SEC_ELEMENT_NAME  ) == 0
						&& element.getAttribute("ID").getValue().compareTo(FedoraAppConstants.DMD_ALLIANCE_ID) == 0) {
					return true;
				}
			}
			return false;
		}
	} // adrDmdSectionFilter


	/**
	 * Filter to find comment containing the batch ingest command line
	 * @author chet.rebman
	 *
	 */
	class CommandLineFilter implements Filter {

		public boolean matches( Object testObj ) {
			if ( Comment.class.isAssignableFrom( testObj.getClass() ) ) {
				Comment comment = (Comment) testObj;
				if ( comment.getText().toLowerCase().contains( "<ingestcontrol" )) {
					return true;
				}
			}
			return false;
		}
	} // CommandLineFilter

	/**
	 * Filter to find the MODS elements within a datastream element.
	 * @author chet.rebman
	 *
	 */
	class LocationInModsDatastreamFilter implements Filter {

		public boolean matches( Object testObj ) {
			if ( Element.class.isAssignableFrom( testObj.getClass() ) ) {
				Element element = (Element) testObj;
				if ( element.getName().compareToIgnoreCase("location") == 0) {
					if ( element.getParentElement().getName().compareToIgnoreCase( "mods") == 0) {
						if ( element.getParentElement().getParentElement().getParentElement().getName().compareToIgnoreCase( "datastreamVersion") == 0)
							return true; //  
					}
				}
			}
			return false;
		}
	} // LocationInModsDatastreamFilter

	/**
	 * Filter to find the DC elements within a datastream element.
	 * @author chet.rebman
	 *
	 */
	class DcInDatastreamFilter implements Filter {

		public boolean matches( Object testObj ) {
			if ( Element.class.isAssignableFrom( testObj.getClass() ) ) {
				Element element = (Element) testObj;
				if ( element.getName().compareToIgnoreCase("dc") == 0) {
					if ( element.getParentElement().getParentElement().getName().compareToIgnoreCase( "datastreamVersion") == 0)
						return true;
				}
			}
			return false;
		}
	} // DcInDatastreamFilter

	/**
	 * Filter to find the DC elements within a mets element.
	 * @author chet.rebman
	 *
	 */
	class DcInMetsFilter implements Filter {

		public boolean matches( Object testObj ) {
			if ( Element.class.isAssignableFrom( testObj.getClass() ) ) {
				Element element = (Element) testObj;
				if ( element.getName().compareToIgnoreCase("dc") == 0) {
					if ( element.getParentElement().getName().compareToIgnoreCase( "xmlData") == 0)
						return true;
				}
			}
			return false;
		}
	} // DcInDatastreamFilter

	/**
	 * Filter to find the first datastream  element
	 * @author chet
	 *
	 */
	class FirstDatastreamFilter implements Filter {

		public boolean matches( Object testObj ) {
			if ( Element.class.isAssignableFrom( testObj.getClass() ) ) {
				Element element = (Element) testObj;
				if ( element.getName().compareToIgnoreCase("datastream") == 0) {
					return true;
				}
			}
			return false;
		}
	} // EndOfFirstDatastreamFilter


	/**
	 * 
	 * Filter to find the METS elements within a datastream element.
	 * @author chet.rebman
	 *
	 */
	class MetsInDatastreamFilter implements Filter {

		public boolean matches( Object testObj ) {
			if ( Element.class.isAssignableFrom( testObj.getClass() ) ) {
				Element element = (Element) testObj;
				if ( element.getName().compareToIgnoreCase("mets") == 0) {
					if ( element.getParentElement().getParentElement().getName().compareToIgnoreCase( "datastreamVersion") == 0)
						return true;
				}
			}
			return false;
		}
	} // MetsInDatastreamFilter

	/**
	 * Filter to find the top level METS element.
	 * @author chet.rebman
	 *
	 */
	class MetsRootFilter implements Filter {

		public boolean matches( Object testObj ) {
			if ( Element.class.isAssignableFrom( testObj.getClass() ) ) {
				Element element = (Element) testObj;
				if ( element.getName().compareToIgnoreCase("mets") == 0) {
					if ( element.isRootElement()) 
						return true;
				}
			}
			return false;
		}
	} // MetsInDatastreamFilter



	/**
	 * Get the PID and if handler server is enabled, contact the ADR handler server 
	 * to register the handle (with handle.net).
	 * <br>
	 * 
	 * @param fedoraContext The fedora context where the object will stored.
	 * @return The Fedora PID (Data Object, DO, identifier) for object after 
	 * ingest.
	 * @throws FatalException
	 */
	protected String getPidAndReserveHandle( String fedoraContext ) throws FatalException {

		String[] pid = null;
		try {		
			pid = FedoraAppUtil.getPIDs( FedoraAppUtil.getAdministrator( this.getBatchOptions() ), fedoraContext, new NonNegativeInteger("1") );

			if ( handleServerEnabled() )
			{
				this.reserveHandle( pid[0] );
			}
		}
		catch ( Exception e ) {
			throw new FatalException( "Unable to get PID and or handle: " + e.getMessage() );
		}

		return pid[0];

	} // getPidAndReserveHandle

	private void reserveHandle( String pid ) throws FatalException
	{
		if ( ! FedoraAppBatchIngestController.registerHandle( pid ) ) {
			String errorMsg = "Unable to get Handle for PID=" + pid;
			this.logger.fatal( errorMsg );
			throw new FatalException( errorMsg );
		}
	}
	
	private boolean handleServerEnabled()
	{
		if ( ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty(FedoraAppConstants.BATCH_INGEST_DISABLE_GET_HANDLE_PROPERTY) != null ) {
			System.out.println("\n\n************GET_HANDLE() IS DISABLED!! ******************\n\n");
			logger.info( "************GET_HANDLE() IS DISABLED!! *****************" ) ;
			return false;
		}
		
		return true;
	}
	
	
	/**
	 * If this  foxml document contains a MODS datastream, update the datastream label, which is displayed
	 * in fedora. 
	 * <br>
	 * Add PID to <foxml:digitalObject PID="xxx">
	 * <br>
	 * If the handle server is enabled. Embed the PID and handle URL within the MODs section just before the </mods:mods> end element
	 * <br><br>
	 * The embed handle url in the xml document is of type....
	 *  "<mods:url usage=\"primary display\">" + handle + "</mods:url>"; 
	 *  where handle = http://hdl.handle.net/10135/THE_FEDORA_PID
	 *  <br>
	 * 
	 * @param ingestDocument document which possibly contains a mods datastream.
	 * @param pid
	 * @param dataStreamLabel
	 * @return true if a <mods> section was found and updated.
	 * @throws Exception  if unable to find <location> element
	 */
	protected boolean addFoxmlPid_AddHandleAndUpdateLabelInModsDataStream( Document ingestDocument, String pid, String dataStreamLabel, Filter modsFilter ) throws Exception{

		Iterator<Element> modsIterator = null;
		Iterator<Element> docIterator = null;
		Iterator<Element> locIterator = null;
		Element modsElement     = null;
		Element locationElement = null;
		Element newModsUrlElement = null;
		Element newLocationElement = null;

		modsIterator = ingestDocument.getDescendants( modsFilter );   
		if ( ! modsIterator.hasNext() )
		{
			return false; // no mods element!
		}
	
			// if this is a foxml document put the pid in the digitalObject element
		docIterator = ingestDocument.getDescendants( new ElementFilter( "digitalObject" ) );  
		if ( docIterator.hasNext() ){
			Element digitalObject = null;
			digitalObject = docIterator.next();
			digitalObject.setAttribute( "PID", pid );
		}

		modsElement = modsIterator.next(); // TBD we assume only one mods element, within a mets element!

			// if this is a foxml document, update the datastreamVersion LABEL attribute
		if ( modsElement.getParentElement().getParentElement().getName().compareToIgnoreCase( "datastreamVersion") == 0){
			modsElement.getParentElement().getParentElement().getAttribute( "LABEL" ).setValue( dataStreamLabel );
		}
  

		if ( handleServerEnabled() )
		{	
			String handle = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.BATCH_INGEST_WORLD_HANDLE_SERVER_PROPERTY ) + pid;

			locIterator = ingestDocument.getDescendants( new LocationInModsDatastreamFilter() ); 
			if ( locIterator.hasNext() ) {
				locationElement = locIterator.next(); 
	
				// embed the world url handle
				newModsUrlElement = new Element( "url" ).setNamespace( modsElement.getNamespace() );
				newModsUrlElement.setAttribute( "usage", "primary display" );
				newModsUrlElement.addContent( handle );
				locationElement.addContent( newModsUrlElement );
			}
			else {
				this.updateStatusMsg( "Warning: "+"Unable to find location element within mods element" );
				// add location element
				newLocationElement = new Element( "location" ).setNamespace( modsElement.getNamespace() );
	
				// embed a location element and the world url handle
				newModsUrlElement = new Element( "url" ).setNamespace( modsElement.getNamespace() );
				newModsUrlElement.setAttribute( "usage", "primary display" );
				newModsUrlElement.addContent( handle );
				newLocationElement.addContent( newModsUrlElement );
				modsElement.addContent( newLocationElement );
			}
		}
		
		return true;
		
	} // updateModsDataStream

	/**
	 * NOTE: As of Aug 7 2012, a DC datastream is no longer required. Islandora will create it's own.
	 * 
	 * If this is a foxml document containing a mods datastream, update the datastream label, which is displayed
	 * in fedora. 
	 * <br><br>
	 * If the handle server is enabled, Embed the PID and handle URL within the MODs section just before the </mods:mods> end element
	 * <br><br>
	 * The embed handle url in the xml document is of type....
	 *  "<dc:identifier> + handle + "</dc:identifier>"; 
	 *  where handle = http://hdl.handle.net/10135/THE_FEDORA_PID
	 *  <br>
	 * @param ingestDocument document containing the DC stream
	 * @param pid the fedora pid
	 * @param dataStreamLabel label to be added to the dc datastream 
	 * @return true if a <dc> stream has found and updated.
	 * @throws Exception 
	 */
	protected boolean addHandleAndUpdateLabelInDcDataStream( Document ingestDocument, String pid, String dataStreamLabel, Filter dcFilter ) throws Exception{

		Iterator<Element> docIterator = null;

		String handle = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.BATCH_INGEST_WORLD_HANDLE_SERVER_PROPERTY ) + pid;

		docIterator = ingestDocument.getDescendants( dcFilter );   
		if ( ! docIterator.hasNext() )
		{
			return false;
		} 
		
		Element dcElement = null;
		dcElement = docIterator.next(); // we assume only one dc element!
		
			// if this is a foxml document, update the datastreamVersion LABEL attribute
		if ( dcElement.getParentElement().getParentElement().getName().compareToIgnoreCase( "datastreamVersion") == 0){
			dcElement.getParentElement().getParentElement().getAttribute( "LABEL" ).setValue( dataStreamLabel );
		}

		if ( handleServerEnabled() )
		{
			// embed the world url handle
			Element newDcURlElement = new Element( "identifier" ).setNamespace( dcElement.getNamespace() );
			
			newDcURlElement.addContent( handle );
			dcElement.addContent( newDcURlElement ); 
		}
		
		return true;
		
	} // updateDcDataStream

	/**
	 * @deprecated  we use this.addExternalRelationships instead
	 * @param foxmlDocument
	 * @param pid
	 * @throws Exception
	 */
	protected void addRelExtDatastreamToFoxml( Document foxmlDocument, String pid ) throws Exception{

		Iterator<Element> docIterator = null;

		/*		  <foxml:datastream ID="RELS-EXT" CONTROL_GROUP="X">
		    <foxml:datastreamVersion FORMAT_URI="info:fedora/fedora-system:FedoraRELSExt-1.0" ID="RELS-EXT.0" MIMETYPE="application/rdf+xml" LABEL="RDF Statements about this Object">
		      <foxml:xmlContent>
		        <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" xmlns:fedora="info:fedora/fedora-system:def/relations-external#" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:fedora-model="info:fedora/fedora-system:def/model#">
		          <rdf:description rdf:about="info:fedora/codu:38">
		            <fedora:isMemberOfCollection rdf:resource="info:fedora/codu:ectd"/>
		            <fedora-model:hasModel rdf:resource="info:fedora/codu:ectdCModel"/>
		          </rdf:description>
		        </rdf:RDF>
		      </foxml:xmlContent>
		    </foxml:datastreamVersion>
		  </foxml:datastream>
		 */

		docIterator = foxmlDocument.getDescendants( new FirstDatastreamFilter() );   

		if ( ! docIterator.hasNext() ){
			throw new Exception( "Unable to find existing datastream element" );
		}
		Element topLevelElement = docIterator.next().getParentElement(); 

		// "<foxml:datastream ID=\"RELS-EXT\" CONTROL_GROUP=\"X\">" 	
		Element extRelDsElement = new Element( "datastream" ).setNamespace( topLevelElement.getNamespace() );
		extRelDsElement.setAttribute("ID",     "RELS-EXT" );
		extRelDsElement.setAttribute("CONTROL_GROUP", "X" );

		//"<foxml:datastreamVersion FORMAT_URI="info:fedora/fedora-system:FedoraRELSExt-1.0" ID="RELS-EXT.0" MIMETYPE="application/rdf+xml" LABEL="RDF Statements about this Object">" );		
		Element dsVersionElement = new Element( "datastreamVersion" ).setNamespace( topLevelElement.getNamespace() );
		dsVersionElement.setAttribute("FORMAT_URI","info:fedora/fedora-system:FedoraRELSExt-1.0" );
		dsVersionElement.setAttribute("ID", "RELS-EXT.0" );		
		dsVersionElement.setAttribute("MIMETYPE", "application/rdf+xml" );		
		dsVersionElement.setAttribute("LABEL", "RDF Statements about this Object" );
		extRelDsElement.addContent( dsVersionElement );

		// extRelDataStreamContent.append( "<foxml:xmlContent>" );	
		Element xmlContentElement = new Element( "xmlContent" ).setNamespace( topLevelElement.getNamespace() );
		dsVersionElement.addContent( xmlContentElement );

		/* <rdf:RDF 
		 * xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
		 * xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
		 * xmlns:fedora="info:fedora/fedora-system:def/relations-external#" 
		 * xmlns:dc="http://purl.org/dc/elements/1.1/" 
		 * xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" 
		 * xmlns:fedora-model="info:fedora/fedora-system:def/model#">" );	
		 */		
		Element rdfElement = new Element( "RDF" );	

		Namespace rdfNamespace =  Namespace.getNamespace( "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#" );
		rdfElement.setNamespace( rdfNamespace );
		rdfElement.addNamespaceDeclaration( Namespace.getNamespace( "rdfs", "http://www.w3.org/2000/01/rdf-schema#" ) );
		Namespace fedoraNamespace = Namespace.getNamespace( "fedora", "info:fedora/fedora-system:def/relations-external#" );
		rdfElement.addNamespaceDeclaration( fedoraNamespace );
		rdfElement.addNamespaceDeclaration( Namespace.getNamespace( "dc", "http://purl.org/dc/elements/1.1/" ) );
		rdfElement.addNamespaceDeclaration( Namespace.getNamespace( "oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/" ) );
		Namespace fedoraModelNamespace = Namespace.getNamespace( "fedora-model", "info:fedora/fedora-system:def/model#" );
		rdfElement.addNamespaceDeclaration( fedoraModelNamespace );
		xmlContentElement.addContent( rdfElement );

		// <rdf:description rdf:about="info:fedora/codu:38">
		Element descriptionElement = new Element( "description" ).setNamespace( rdfNamespace );	
		descriptionElement.setAttribute( "about","info:fedora/"+pid, rdfNamespace );
		rdfElement.addContent( descriptionElement );

		// "<fedora:isMemberOfCollection rdf:resource="info:fedora/codu:ectd"/>"
		Element collectionElement = new Element( "isMemberOfCollection" ).setNamespace( fedoraNamespace );	
		collectionElement.setAttribute( "resource","info:fedora"+FedoraAppConstants.ECTDmodelPID, rdfNamespace );
		descriptionElement.addContent( collectionElement );

		// <fedora-model:hasModel rdf:resource="info:fedora/codu:ectdCModel"/>
		Element modelElement = new Element( "hasModel" ).setNamespace( fedoraModelNamespace );		
		modelElement.setAttribute("resource","info:fedora/"+FedoraAppConstants.ECTDmodelPID, rdfNamespace );;
		descriptionElement.addContent( modelElement );

		topLevelElement.addContent( extRelDsElement );

	} // addRelExtDatastream

	/**
	 * Add a Fedora label to a foxml document with a METS datastream, if it exists.
	 * 
	 * @param ingestDocument the document containing the mets datastream
	 * @param dataStreamLabel label to be added for the mets datastream
	 * 
	 * @return true if mets datastream is found and updated.
	 */
	protected boolean updateLabelInMetsDataStream( Document ingestDocument, String dataStreamLabel, Filter metsFilter ){

		Iterator<Element> docIterator = null;

		docIterator = ingestDocument.getDescendants( metsFilter );   
		if( ! docIterator.hasNext() )
		{
			return false;
		}
		
		Element metsElement = null;
		while ( docIterator.hasNext() ) {
			metsElement = docIterator.next(); // TBD we assume only one mets element!
			if ( metsElement.getParentElement().getParentElement().getName().compareToIgnoreCase( "datastreamVersion") == 0){
				metsElement.getParentElement().getParentElement().getAttribute( "LABEL" ).setValue( dataStreamLabel );
			}
		}
		
		return true;

	} // updateMetsDataStream

	/**
	 * Upload all primary content objects and embed their internal Fedora 
	 * locations within the FOXML.
	 * <br><br>
	 * NOTE:<br>
	 * The original PCO location is embeded in a METS record in a FLocat element.
	 * <br> <mets:FLocat LOCTYPE="URL" xlink:href="file:frid00003sm.jpg"/>
	 * and transformed in the foxml document as the contentLocation element
	 * <br> <foxml:contentLocation TYPE="URL" REF="uploaded://1415"/>
	 * @param xmlDocument the original batch ingest xml file.
	 * @throws FatalException
	 * @throws Exception
	 */
	protected void uploadPrimaryContentObjectsEmbedTempURL( Document xmlDocument ) throws FatalException, Exception {

		Iterator<Element> contentLocations = xmlDocument.getDescendants( new ElementFilter( "contentLocation" ) );
		Element fileLocationElement = null;
		URL fileNameURL = null; // filename is embeded in FLocat as a URL of type file:frid00003sm.jpg
		while ( contentLocations.hasNext() ) {
			fileLocationElement = contentLocations.next(); 
			try {
				fileNameURL = new URL( fileLocationElement.getAttribute( "REF" ).getValue() );

				if ( fileNameURL.getProtocol().compareToIgnoreCase( "File" ) != 0 ) {
					throw new MalformedURLException( "Only the File: procotol is accepted for Primary Context Objects" );
				}

				String fileLocation = new URL( ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.BATCH_INGEST_TOP_FOLDER_URL_PROPERTY ) ).getFile()          
				+ this.getBatchPath() 
				+ ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.BATCH_INGEST_FILES_FOLDER_PROPERTY ) + '/'
				+   fileNameURL.getFile();     

				File pcoFile = new File ( fileLocation );
				if ( ! pcoFile.exists() ) {
					throw new Exception( "Primary content file (contentLocation) not found: "+fileNameURL.toString() );
				}
				// upload file and get temp file url, fedoraFileLocation is of type uploaded://1415"
				String fedoraFileID = FedoraAppUtil.getAdministrator( this.getBatchOptions() ).getFedoraClient().uploadFile( pcoFile );   

				// save the location of PCOs for checking upload and moving files to completed folder later
				this.pcoFileList.add( pcoFile );

				// embed the temp. file url
				fileLocationElement.setAttribute( "REF", fedoraFileID );
			} catch (MalformedURLException e) {
				throw new Exception( "contentLocation has an invalid URL: "+fileLocationElement.getAttribute( "REF" ).getValue() );
			}
		}

	} // uploadPrimaryContentObjects

	/**
	 * Get the PCOs so that we can update EXISTING data streams, when performing an update of type="pco"
	 * 
	 * @param xmlDocument the original batch ingest xml file.
	 * @throws FatalException
	 * @throws Exception
	 */
	protected Set<Pco> getPCOsForDSupdate( Document xmlDocument ) throws FatalException, Exception {

		Iterator<Element> flocatElements = xmlDocument.getDescendants( new ElementFilter( "FLocat" ) );
		Element fileLocationElement = null;
		URL fileNameURL = null; // filename is embeded in FLocat as a URL of type file:frid00003sm.jpg
		Set<Pco> returnList = new HashSet<Pco>();
		
		while ( flocatElements.hasNext() ) {
			fileLocationElement = flocatElements.next(); 
			try {
				fileNameURL     = new URL( fileLocationElement.getAttribute( "href", fileLocationElement.getNamespace( "xlink" ) ).getValue() );
				String dsID     = fileLocationElement.getParentElement().getAttribute( "ID" ).getValue();
				String mimeType = fileLocationElement.getParentElement().getAttribute( "MIMETYPE" ).getValue();
				
				if ( fileNameURL.getProtocol().compareToIgnoreCase( "File" ) != 0 ) {
					throw new MalformedURLException( "Only the File: procotol is accepted for Primary Context Objects" );
				}

				String fileLocation = new URL( ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.BATCH_INGEST_TOP_FOLDER_URL_PROPERTY ) ).getFile()          
				+ this.getBatchPath() 
				+ ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.BATCH_INGEST_FILES_FOLDER_PROPERTY ) + '/'
				+   fileNameURL.getFile();     

				File pcoFile = new File ( fileLocation );
				if ( ! pcoFile.exists() ) {
					throw new Exception( "Primary content file (contentLocation) not found: "+fileNameURL.toString() );
				}
				pcoFile = null;
				
				Pco pcoDef = new Pco( new File ( fileLocation ), dsID, mimeType );
				returnList.add( pcoDef );	
			} 
			catch (MalformedURLException e) {
				throw new Exception( "contentLocation has an invalid URL: "+fileLocationElement.getAttribute( "REF" ).getValue() );
			}
		}

		return returnList;
		
	} // uploadPrimaryContentObjects

	/**
	 * Connects with the ADR and requests a handle to be assigned from handle.net 
	 * for a specific PID. Note: The handle is the URL with the pid. We don't receive
	 * anything back.
	 * <br><br>
	 * NOTE: Looks for BATCH_INGEST_DISABLE_GET_HANDLE in the application 
	 * properties file, if set return true without attempting to obtain a
	 * handle.
	 * 
	 * @see FedoraAppConstants#BATCH_INGEST_DISABLE_GET_HANDLE_PROPERTY
	 * @param pid
	 * @return true if a handle was assigned
	 * @throws FatalException if unable to assign handle.
	 */
	public static boolean registerHandle( String pid ) throws FatalException {

		// http://sword.coalliance.org:9080/handles/handles.jsp?debug=true&pid=coalliance:1279

		HttpURLConnection httpConnection = null;
		URL handleURL = null;

		String handleServer     = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.BATCH_INGEST_HANDLE_SERVER_PROPERTY );
		String handleServerPort = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.BATCH_INGEST_HANDLE_SERVER_PORT_PROPERTY );
		String handleServerApp  = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.BATCH_INGEST_HANDLE_SERVER_APP_PROPERTY );

		InputStream responseIS   = null;
		InputStreamReader inputStreamReader = null;
		String responseString         = null;
		BufferedReader bufferedReader = null;

		try {
			handleURL = new URL("http://"+handleServer+":"+handleServerPort+"/"+handleServerApp+"debug=true&adr3=true&pid="+pid);
			httpConnection = (HttpURLConnection) handleURL.openConnection();
			httpConnection.setRequestMethod( "GET"  );
			responseIS = (InputStream) httpConnection.getContent();
		} 
		catch (MalformedURLException e) {
			throw new FatalException( "BAD URL to obtain ADR Handle: "+e);
		}
		catch (Exception e) {
			// return true; // TBD for debug,
			throw new FatalException( "Unable to get ADR URL content: "+e);
		}

		inputStreamReader = new InputStreamReader( responseIS );
		bufferedReader    = new BufferedReader(inputStreamReader) ;

		try {
			while ( bufferedReader.readLine() != null ) {
				responseString = bufferedReader.readLine();
				if ( responseString.toLowerCase().contains("success") ) {
					return true;
				}
				if ( responseString.toLowerCase().contains("error") ) {
					throw new FatalException( "Unable to obtain ADR Handle: "+responseString);
				}
			}
		} catch (IOException e) {
			throw new FatalException( "Unable to obtain ADR Handle: "+e);
		}

		return false;

	} // registerHandle


	/**
	 * Check that the ingest was correct. We check that the object is stored with
	 * correct PID and that the checksum of the DataStreams matches that
	 * of the Primary Content Object files.
	 * 
	 * @param pid the fedora pid
	 * @param pcoFileList list of pco file names.
	 * @throws Exception on any error TBD throw FatalException here?
	 */
	protected void checkForValidIngest( String pid, List<File> pcoFileList )  throws FatalException {

		Datastream fedoraDS = null;  
		int pcoCounter      = 0;  
		String fedoraChecksum = null;
		String pcoFileChecksum = null;
		try {
			// verify that Fedora Digital Object with correct PID exists (exception thrown)
			Util.getObjectFields( FedoraAppUtil.getAdministrator( this.getBatchOptions() ).getAPIA(), pid, new String[] {"pid"} );

			if ( this.getBatchOptions().isValidatePCOchecksums() ) {

				for( pcoCounter=0; pcoCounter<pcoFileList.size(); pcoCounter++){ //  pcoFileList.get(0).getAbsolutePath()

					// get MD5 for fedora DS and PCO File
					fedoraDS = FedoraAppUtil.getAdministrator( this.getBatchOptions() ).getAPIM().getDatastream(pid, pcoFileList.get( pcoCounter ).getName() , null);
					fedoraChecksum  = fedoraDS.getChecksum(); 
					pcoFileChecksum = FileUtil.getMD5( pcoFileList.get( pcoCounter ) );
					while ( pcoFileChecksum.length() < 32 ){ 
						pcoFileChecksum = "0" + pcoFileChecksum;
					}
					// compare checksums
					if ( ! fedoraChecksum.equals( pcoFileChecksum) ) {
						String errorMsg = "MD5 checksum does not match for" + pcoFileList.get( pcoCounter ).getName();
						this.updateStatusMsg( errorMsg );
						throw new FatalException( errorMsg );
					}
				} // pco loop

			} // if validate checksums
		} 
		catch (Exception e) {
			String errorMsg = "Unable to get object for PID:"+pid+" : "+e.getMessage() + " : Does the /batch_space/'institution' name match a valid fedora context?";
			this.updateStatusMsg( errorMsg );
			throw new FatalException( errorMsg );
		}     

	} // checkForValidIngest

	@Override
	protected void callFileSplitter(BatchIngestOptions ingestOptions,ThreadStatusMsg threadStatus, File fileToSplit, String MetsNewDirectory, String MetsUpdatesDirectory )
	throws Exception
	{		
		boolean nameFileFromOBJID = false;
		MetsBatchFileSplitter.splitMetsBatchFile( ingestOptions, threadStatus, fileToSplit, MetsNewDirectory, MetsUpdatesDirectory , nameFileFromOBJID );
	}


	/**
	 * This method is overridden to allow the closing of the unique fedora ADR pid report.
	 */
	public void forceHardStop() {

		try {
			this.pidReport.closePidReport();
		} catch (Exception e ){
			this.logger.fatal( "EXCEPTION DURING FORCED STOP of batch set:"+this.getBatchSet()+" :"+e);
		}

		super.forceHardStop();
	}

	public int getTotalFilesAddedSuccess() {
		return this.totalFilesAddedSuccess;
	}

	protected void setTotalFilesAddedSuccess(int totalFilesAddedSuccess) {
		this.totalFilesAddedSuccess = totalFilesAddedSuccess;
	}


	/** the islandoraCollection is displayed on the final gwt result page
	 * @see edu.du.penrose.systems.fedoraApp.web.gwt.batchIngest.client.BatchIngestStatus
	 * @see "edu/du/penrose/systems/fedoraApp/web/gwt/batchIngest/public/batchIngestStatus.jsp"
	 */
	public String getIslandoraCollection(){
		return this.getBatchOptions().getFedoraCollection(); 
	}
	
	/** the islandoraContentModel is displayed on the final gwt result page
	 * @see edu.du.penrose.systems.fedoraApp.web.gwt.batchIngest.client.BatchIngestStatus
	 * @see "edu/du/penrose/systems/fedoraApp/web/gwt/batchIngest/public/batchIngestStatus.jsp"
	 */
	public String getIslandoraContentModel(){
		return this.getBatchOptions().getFedoraContentModel();
	}
	
	
	public int getTotalFilesAddedFailed() {
		return this.totalFilesAddedFailed;
	}

	protected void setTotalFilesAddedFailed(int totalFilesAddedFailed) {
		this.totalFilesAddedFailed = totalFilesAddedFailed;
	}

	public int getTotalFilesUpdatedSuccess() { 
		return this.totalFilesUpdatedSuccess;
	}

	protected void setTotalFilesUpdatedSuccess(int totalFilesUpdatedSuccess) {
		this.totalFilesUpdatedSuccess = totalFilesUpdatedSuccess;
	}

	public int getTotalFilesUpdatedFailed() {
		return this.totalFilesUpdatedFailed;
	}

	protected void setTotalFilesUpdatedFailed(int totalFilesUpdatedFailed) {
		this.totalFilesUpdatedFailed = totalFilesUpdatedFailed;
	}

	public boolean isBatchIsUpdates()
	{
		return this.getBatchOptions().isBatchIsUpdates();
	}
	
} // FedoraAppBatchIngestController
