/*
 * Copyright 2012 University of Denver
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
package edu.du.penrose.systems.fedoraApp.batchIngest.data;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.du.penrose.systems.exceptions.BagitInvalidException;
import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraApp.ProgramProperties;
import edu.du.penrose.systems.util.Zipper;

//import gov.loc.repository.bagger.bag.impl.DefaultBag;

import gov.loc.repository.bagger.domain.JSonBagger;
import gov.loc.repository.bagger.profile.BaggerProfileStore;
import gov.loc.repository.bagit.FetchTxt;
import gov.loc.repository.bagit.verify.impl.CompleteVerifierImpl;
import gov.loc.repository.bagit.verify.impl.ParallelManifestChecksumVerifier;
import gov.loc.repository.bagit.verify.impl.ValidVerifierImpl;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import edu.du.penrose.systems.fedoraApp.batchIngest.bus.BatchIngestThreadManager;
import edu.du.penrose.systems.fedoraApp.batchIngest.bus.BatchThreadManager;
import edu.du.penrose.systems.util.FileUtil;

public class BagHandler {

	/** 
	 * Logger for this class and subclasses.
	 */
	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * 'bagit' is the delimiter used in <br>
	 * gov.loc.repository.bagger.bag.impl.DefaultBag.getBaseUrl(FetchTxt)
	 */
	static String DEFAULT_BAG_URL_DELIMITER = "bagit";
	
	static final String FILE_TRANSFER_ABORTED = "Thread aborted by user while tring to download bagit data files";
	
	private DefaultBag myBag = null;

	BatchIngestOptions batchOptions = null;
	String                myRootDir = null; // this directory contains the bag.
	String            myBagFileName = null;
	String                myBagName = null;   
	String       myBagDirectoryPath = null; // this is the directory of the actual bag
	String                myWorkDir = null;
	String               myFilesDir = null;
	String  myCompletedBatchFileDir = null;
	String  myFailedBatchFileDir    = null;
	
	public BagHandler()
	{
		// constructor
	}
	
	/**
	 * Create a bag extraction handler for the batchIngest, it uses the settings in the batchIngest.properties file, 
	 * This is heavily tied to the batch ingest and is not intended for generic use.
	 * 
	 * @param batchOptions
	 * @param bagFileName
	 * @throws Exception
	 */
	public BagHandler( BatchIngestOptions batchOptions, String bagFileName  ) throws Exception
	{
		this.batchOptions     = batchOptions;
		this.myBagFileName    = bagFileName;
	//	this.myBagName        = bagFileName.replace( FedoraAppConstants.BAGIT_FILE_SUFFIX, "" );
	//	this.myBagName        = this.myBagName.replace( FedoraAppConstants.BATCH_FILE_IDENTIFIER, "" );	
	//	this.myBagName        = this.myBagName.replace( FedoraAppConstants.REMOTE_TASK_NAME_SUFFIX, "" );		
		
		String workDirName  = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.BATCH_INGEST_WORK_FOLDER_PROPERTY );
		String filesDirName = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.BATCH_INGEST_FILES_FOLDER_PROPERTY );
        String completedBatchFileDirName = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.BATCH_INGEST_COMPLETED_BATCH_FOLDER_PROPERTY );
        String failedBatchFileDirName = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.BATCH_INGEST_FAILED_BATCH_FOLDER_PROPERTY );
              
		String batchSpaceURL = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.BATCH_INGEST_TOP_FOLDER_URL_PROPERTY );
	
		String batchSpace = new URL( batchSpaceURL ).getFile();

		this.myWorkDir  = batchSpace  + this.batchOptions.getInstitution() + "/" + this.batchOptions.getBatchSet() + "/" + workDirName  + "/";
		this.myFilesDir = batchSpace  + this.batchOptions.getInstitution() + "/" + this.batchOptions.getBatchSet() + "/" + filesDirName + "/";
		this.myCompletedBatchFileDir = batchSpace  + this.batchOptions.getInstitution() + "/" + this.batchOptions.getBatchSet() + "/" + completedBatchFileDirName + "/";
		this.myFailedBatchFileDir = batchSpace  + this.batchOptions.getInstitution() + "/" + this.batchOptions.getBatchSet() + "/" + failedBatchFileDirName + "/";
		
		this.myRootDir = batchSpace  + this.batchOptions.getInstitution() + "/" + this.batchOptions.getBatchSet() + "/" + workDirName + "/";
	}
	 
	/**
	 * Extracts bag, downloads all PCO files (in fetch.txt), checks bag validity, moves PCOs and batch file to correct locations, 
	 * The batch file will have 'batch_' prepended and if original xxxx.zip file has '_REMOTE' in the file name, '_REMOTE.xml' will
	 * be appended to the batch file name. Finally the bag directory tree is deleted, and the bag file is moved to the completed 
	 * batch files directory.<br>
	 * 	<br>	
	 * We handle existing files different for manual and remote ingest. For manual we throw an exception, for remote
	 * we add the FedoraAppConstants.DUPLICATE_FILE_SUFFIX and continue processing. This allows an external user the
	 * chance to recover from a failed ingest by posting a fixed bagit file with the same name. Otherwise someone would
	 * have to log onto the server to "fix" things.	  We determine if an ingest is remote based on the bag file name ie
	 * does it have a FedoraAppConstants.REMOTE_TASK_NAME_SUFFIX suffix.
	 * 
	 * @see FedoraAppConstants#BATCH_FILE_IDENTIFIER
	 * @see FedoraAppConstants#BAGIT_FILE_SUFFIX
	 * @see FedoraAppConstants#BATCH_FILE_SUFFIX
	 * @see FedoraAppConstants#REMOTE_TASK_NAME_SUFFIX
	 * @see FedoraAppConstants.DUPLICATE_FILE_SUFFIX
	 * 
	 * @throws Exception
	 */
	public void batchIngestExtractRetrieveAndMove() throws Exception
	{
		this.extractBagAndRetrieveFiles();
		
		BatchThreadManager.setBatchSetStatus( this.batchOptions.getBatchSetName(), "Validating Bag"  );
		
		if ( this.validateBag( this.myBag ) )
		{
			BatchThreadManager.setBatchSetStatus( this.batchOptions.getBatchSetName(), "Valid Bag"  ); 
			System.out.println( "Valid Bag"  );
			this.logger.info( "Valid Bag" );
		}
		else 
		{
			System.out.println( "ERROR: Invalid Bag"  );
			this.logger.error(  "ERROR: invalid bag:" + this.myBagName  );
			throw new BagitInvalidException(  "ERROR: invalid bag:" + this.myBagName );
		}
		
		this.movePcoFiles();
		
		this.deleteBagDirMoveBagToCompletedDir();
	}
	
	/**
	 * This function OVERWRITES. We assumes the bag name was checked as unique in the work directory and has already been extracted, throwing
	 * and error will be of no value.
	 * 
	 * @throws IOException
	 */
	private void deleteBagDirMoveBagToCompletedDir() throws IOException 
	{
		this.logger.info( "Move bag file:"+this.myBagFileName+" to "+this.myCompletedBatchFileDir );
		FileUtil.deleteDirectoryTree( new File (this.myBagDirectoryPath) );
		
		File moveFile = new File( this.myCompletedBatchFileDir  + this.myBagFileName  );
		
		File bagFile = new File( this.myRootDir + this.myBagFileName );
		
		bagFile.renameTo( moveFile );	
	}

	
	/**
	 * Accepts a directory or a zip file.
	 * 
	 * @param rootDir
	 * @param bagFileName
	 * @throws Exception
	 */
	private void extractBagAndRetrieveFiles() throws Exception
	{
		String completeBagPathAndFileName = "";
		if ( myRootDir.endsWith( ""+File.separatorChar ) )
		{
			completeBagPathAndFileName = myRootDir + this.myBagFileName;
		}
		else 
		{
			completeBagPathAndFileName = myRootDir + File.separatorChar + this.myBagFileName;
		}
		
		this.myBagName = new Zipper().getZipTopLevelDirectory( completeBagPathAndFileName );
		
		/*
		 * DefaultBag(below) accepts files or directories, we want the directory.
		 */
		this.myBagDirectoryPath = this.myRootDir + this.myBagName;
		
		/**
		 * We handle existing files different for manual and remote ingest. For manual we throw an exception, for remote
		 * we add the FedoraAppConstants.DUPLICATE_FILE_SUFFIX and continue processing. This allows an external user the
		 * chance to recover from a failed ingest by posting a fixed bagit file with the same name. Otherwise someone would
		 * have to log onto the server to "fix" things. We determine if an ingest is remote based on the bag file name ie
		 * does it have a FedoraAppConstants.REMOTE_TASK_NAME_SUFFIX suffix.
		 */
		File bagitDirectory = new File( this.myBagDirectoryPath );
		if ( bagitDirectory.exists() )
		{
			if (  this.myBagFileName.contains( FedoraAppConstants.REMOTE_TASK_NAME_SUFFIX ) )
			{
				// move the bagit file so that we don't keep trying to process it.	
				//File tempFile =    new File( this.myWorkDir 	       + File.separatorChar + this.myBagFileName );
				//tempFile.renameTo( new File( this.myFailedBatchFileDir + File.separatorChar + this.myBagFileName ) );
				
				String newFileName = FileUtil.getUniqueFileName( this.myBagDirectoryPath );
				bagitDirectory.renameTo( new File( newFileName + FedoraAppConstants.REMOTE_TASK_NAME_SUFFIX  + FedoraAppConstants.DUPLICATE_FILE_SUFFIX ) );
				bagitDirectory = new File( this.myBagDirectoryPath );
				
			}
			else
			{
				throw new Exception( "ERROR: bag directory already exists:" + this.myBagDirectoryPath );
			}       	
		}
		extractBag( this.myRootDir, completeBagPathAndFileName, this.myBagFileName );		
	
		this.myBag = getNewDefaultBag( this.myBagDirectoryPath );
		
		/**
		 * A Holey bag is one that includes fetch.txt, which points to URLs instead of local files.
		 */
		if ( this.myBag.isHoley() )
		{
			FetchTxt fetchText = this.myBag.getFetchTxt();
			fetchNeededFiles( this.myRootDir, this.myBag.getBaseUrl(fetchText), this.myBag.getFetchPayload(), this.myBagName );
		}

		
	}

	/**
	 * Get a new Default bag based on an existing bag directory structure.
	 * 
	 * @param bagDirectory
	 * @return
	 */
	public DefaultBag getNewDefaultBag( String bagDirectory )
	{
		/*
		 * You must initialize the profileStore or DefaultBag will throw an exception.
		 * 
		 * FYI BaggerProfileStore initialization is defined in...
		 * bagger-business/src/main/resources/gov/loc/repository/bagger/ctx/common/business-layer-context.xmlbagger-business/src/main/resources/gov/loc/repository/bagger/ctx/common/business-layer-context.xml
		 * 
		 */
		JSonBagger json = new JSonBagger();	
		BaggerProfileStore profileStore = new BaggerProfileStore( json );
	
		return new DefaultBag( new File( bagDirectory ) ,"0.96" );
	}
	
	/**
	 * This routing will try to retrieve a file FOREVER, retrying every 10 seconds, the fedoraApp status screen will be updated 
	 * and the the user can abort the ingest from there. If the user aborts the ingest a BagitInvalidException is thrown
	 * 
	 * @param rootDir
	 * @param baseUrl
	 * @param bagPayLoad
	 * @param bagName
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws BagitInvalidException Thrown for invalid bag or user abort (see method description).
	 */
	private void fetchNeededFiles( String rootDir, String baseUrl, List<String> bagPayLoad, String bagName ) throws MalformedURLException, IOException, BagitInvalidException 
	{	
		for( String payloadUrl : bagPayLoad )
		{
			if ( payloadUrl.contains( "data/" ) )
			{
				String msg = "Fetching " + baseUrl+BagHandler.DEFAULT_BAG_URL_DELIMITER + "/" + payloadUrl;
				BatchThreadManager.setBatchSetStatus( this.batchOptions.getBatchSetName(), msg  ); 
				this.logger.info( msg );
				
				String getUrl            = baseUrl + BagHandler.DEFAULT_BAG_URL_DELIMITER + "/" + payloadUrl;
				String outputPathAndFile = rootDir + bagName + "/" + payloadUrl;
					
				/**
				 * Let's try to fetch bagit file, if we can't, we retry every 10 seconds, We will top logging after 10 attempts, but
				 * will hang here forever! The fedoraApp status screen is updated and the user should be able to click the 
				 * 'Stop Ingest' button.
				 */
				boolean fetchReady = false;
				int logErrorCount = 0;
				do {
					try {
						org.apache.commons.io.FileUtils.copyURLToFile( new URL(getUrl), new File(outputPathAndFile) );
						fetchReady = true;
					}
					catch ( MalformedURLException e) 
					{
						this.logger.error( "Malformed URL in bagit file: Aborting ingest: "+e.getMessage() );
						FileUtil.deleteDirectoryTree( new File (this.myBagDirectoryPath) );
						throw new BagitInvalidException( e.getMessage() );
					}
					catch ( ConnectException e )
					{
						logErrorCount++;
						
						BatchThreadManager.setBatchSetStatus( this.batchOptions.getBatchSetName(), "ERROR: Unable to fetch File:"+getUrl+"-"+e.getMessage()   ); 
						if ( logErrorCount < 10 ){
							this.logger.error( "Unable to fetch bagit file:"+getUrl+"-"+e.getMessage() );
						}
						if ( logErrorCount == 10 ){
							this.logger.error( "Still unable to fetch bagit File, this error will longer be logged." );
						}

						try {
							Thread.sleep( 10*1000 );
						} catch (InterruptedException e1) { 	}
					}
					catch ( Exception e ){

						this.logger.error( "Bagit Exception: Aborting ingest: "+e.getMessage() );
						FileUtil.deleteDirectoryTree( new File (this.myBagDirectoryPath) );
						throw new BagitInvalidException( e.getMessage() );
					}
					finally
					{
						/*
						 * We may be stuck in this loop if we are unable to retrieve files from the server. Fortunately the use
						 * can stop the ingest through the manual ingest gui. We handle that condition here.
						 */
						if ( ! BatchIngestThreadManager.isBatchSetThreadExists( this.batchOptions.getBatchSetName() ) )
						{
							throw new BagitInvalidException( FILE_TRANSFER_ABORTED ); 
						}
						
						String status = BatchThreadManager.getBatchSetStatus( this.batchOptions.getBatchSetName() );
						if ( status.equals( BatchThreadManager.USER_HARD_STOP_RECIEVED) || status.equals( BatchIngestThreadManager.USER_STOP_RECIEVED) )
						{
							throw new BagitInvalidException( FILE_TRANSFER_ABORTED );
						}
						
					}
				} while ( ! fetchReady );

				
			}
		}
	}


	/**
	 * Validates an unzipped bag directory.
	 * 
	 * @param bagDirectoryPath
	 * @return
	 */
	public boolean validateBag( String bagDirectoryPath )
	{
		DefaultBag testBag = this.getNewDefaultBag( bagDirectoryPath );
		
		return this.validateBag( testBag );
	}
	
	private void extractBag( String myRootDir, String completeBagPathAndFileName, String bagFileName ) throws Exception 
	{
		String msg =  "Exctacting bag "+bagFileName;
		BatchThreadManager.setBatchSetStatus( this.batchOptions.getBatchSetName(), msg  ); 
		this.logger.info( msg );
		

		if ( completeBagPathAndFileName.endsWith( FedoraAppConstants.BAGIT_FILE_SUFFIX ) )
		{
			new Zipper().uncompress_zip( myRootDir, completeBagPathAndFileName );
		}
	}

	public boolean validateBag( DefaultBag bag ) 
	{
		CompleteVerifierImpl completeVerifier = new CompleteVerifierImpl();

		ParallelManifestChecksumVerifier manifestVerifier = new ParallelManifestChecksumVerifier();

		ValidVerifierImpl validVerifier = new ValidVerifierImpl(completeVerifier, manifestVerifier);

		String result = bag.validateBag( validVerifier );

		if ( result == null )
		{
			return true;
		}

		return false;	
	}

    /*
     * Move PCO files to the files directory and move any batch command file into the work directory last, 
     * so that if it gets processed by a task, the files it needs are already in place.
     * /
     */
	private void movePcoFiles() throws Exception 
	{
		List<String> bagPayLoad = this.myBag.getFetchPayload();
		
		File filesDirectory = new File( this.myFilesDir );
		File workDirectory  = new File( this.myWorkDir );
		File batchFile  = null;
		File destinationFile = null;
		String newBatchFileName = null;
		
		boolean success = false;
		for( String payloadUrl : bagPayLoad )
		{
			if ( payloadUrl.contains( "data/" ) )
			{	
				if (  payloadUrl.toLowerCase().endsWith( FedoraAppConstants.BATCH_FILE_SUFFIX ) )
				{
					newBatchFileName = payloadUrl.replace( "data/", "" );
					if ( ! newBatchFileName.contains(  FedoraAppConstants.BATCH_FILE_IDENTIFIER ) )
					{
						newBatchFileName = FedoraAppConstants.BATCH_FILE_IDENTIFIER + newBatchFileName;
					}
					if (  this.myBagFileName.contains(  FedoraAppConstants.REMOTE_TASK_NAME_SUFFIX ) ){
						newBatchFileName = newBatchFileName.replace( FedoraAppConstants.BATCH_FILE_SUFFIX, FedoraAppConstants.REMOTE_TASK_NAME_SUFFIX + FedoraAppConstants.BATCH_FILE_SUFFIX );
					}
					batchFile = new File( this.myBagDirectoryPath + File.separatorChar + payloadUrl.replace( '/', File.separatorChar ) );
					continue;
				}
				else {
					File fileToMove = new File( this.myBagDirectoryPath + File.separator + payloadUrl.replace( '/', File.separatorChar ) );
				
					
					/**
					 * We handle existing files different for manual and remote ingest. For manual we throw an exception, for remote
					 * we add the FedoraAppConstants.DUPLICATE_FILE_SUFFIX and continue processing. This allows an external user the
					 * chance to recover from a failed ingest by posting a fixed bagit file with the same name. Otherwise someone would
					 * have to log onto the server to "fix" things.
					 */
					destinationFile = new File( filesDirectory, fileToMove.getName() );				
					if ( destinationFile.exists() ) 
					{
						if ( this.myBagFileName.contains( FedoraAppConstants.REMOTE_TASK_NAME_SUFFIX ) )
						{
							String duplicateName = FileUtil.getUniqueFileName( fileToMove.getName() );
							File duplicateFile = new File( filesDirectory, duplicateName+FedoraAppConstants.REMOTE_TASK_NAME_SUFFIX+FedoraAppConstants.DUPLICATE_FILE_SUFFIX );	
							destinationFile.renameTo( duplicateFile );
						}
						else
						{
							throw new Exception( "ERROR: file already exists! "+ destinationFile.getAbsolutePath() );
						}
					}
					
					String msg = "Moving "+ fileToMove.getAbsolutePath()+" to "+destinationFile.getAbsolutePath();
					BatchThreadManager.setBatchSetStatus( this.batchOptions.getBatchSetName(), msg  ); 
					this.logger.info( msg );
					
					success = fileToMove.renameTo( destinationFile );
					if ( ! success ) {
						throw new Exception( "ERROR: unable to move file "+ fileToMove.getAbsolutePath()+" to "+destinationFile.getAbsolutePath());
					}
				}							
			} 
		} // for
		
		/**
		 * Must move batch command file last, so that if it gets processed by a task, the files are already in place.
		 * 
		 */
		if ( batchFile != null )
		{
			destinationFile = new File( workDirectory, newBatchFileName );	

			if ( destinationFile.exists() ) 
			{
				if ( destinationFile.getName().contains( FedoraAppConstants.REMOTE_TASK_NAME_SUFFIX ) )
				{
					String duplicateName = FileUtil.getUniqueFileName( newBatchFileName );
					File duplicateFile = new File( workDirectory, duplicateName + FedoraAppConstants.DUPLICATE_FILE_SUFFIX );
					destinationFile.renameTo( duplicateFile );
					destinationFile = new File( workDirectory, newBatchFileName );	
				}
				else
				{
					throw new Exception( "ERROR: file already exists! "+ destinationFile.getAbsolutePath() );
				}
				
			}
			
			String msg = "Moving "+ batchFile.getAbsolutePath()+" to "+destinationFile.getAbsolutePath();
			this.logger.info( msg );
			
			success = batchFile.renameTo( destinationFile );
			if ( ! success ) {
				throw new Exception( "ERROR: unable to move file "+ batchFile.getAbsolutePath()+" to "+destinationFile.getAbsolutePath());
			}
			
			this.batchOptions.setWorkFile( newBatchFileName );
		}
		else {
			throw new Exception( "ERROR: the bag is missing a batch file! (*.xml)" );
		}
	}

} // BagHandler
