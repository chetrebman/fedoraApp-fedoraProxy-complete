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

package edu.du.penrose.systems.fedoraApp.tasks;

import java.io.File;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.du.penrose.systems.exceptions.FatalException;
import edu.du.penrose.systems.fedoraApp.batchIngest.bus.BatchIngestThreadManager;
import edu.du.penrose.systems.fedoraApp.batchIngest.bus.BatchThreadManager;
import edu.du.penrose.systems.fedoraApp.batchIngest.bus.FedoraAppBatchIngestController;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestOptions;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.TransformMetsData;
import edu.du.penrose.systems.fedoraApp.util.FedoraAppUtil;
import edu.du.penrose.systems.fedoraApp.web.gwt.batchIngest.server.StatusData;
import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraApp.ProgramProperties;
import edu.du.penrose.systems.util.FileUtil;

import edu.du.penrose.systems.fedoraApp.ProgramFileProperties;
import edu.du.penrose.systems.util.SendMail;

/**
 * Worker used to start a separate batch ingest background thread. Properties are loaded from the standard {institution}{batchSet}.properties
 * file AND either a {institution}{batchSet}_REMOTE.properties OR a {institution}{batchSet}_TASK.properties file.
 * <br><br>
 * NOTE: If there is an existence of both a {institution}{batchSet_REMOTE.properties and a {institution}{batchSet_TASK.properties files,
 *  the {institution}{batchSet_REMOTE.properites file is loaded, there other is ignored.
 * <br><br>
 * Batch files are processed in the following order...<br>
 * 1) We look to see if a task for this institution is currently running, if one is, no action is performed.<br>\
 * 2) If there isn't a current ingest running for the particular batch set, a new ingest task is started that will process ALL
 * METS files in the new and updates folders. <br> If there are no existing files in the METS new and updates folders
 * we look in the work directory for a batch file. The first one found is split and an ingest started.
 * <br>
 * 
 * @author Chet
 *
 */
public class IngestWorker implements WorkerInf {

	private static boolean running = false; // a simple semaphore

	/**
	 * If true use _REMOTE.properties and xml files with _REMOTE suffix see FedoraAppConstants.REMOTE_TASK_NAME_SUFFIX, other wise we 
	 * use FedoraAppConstants.BACKGROUND_TASK_NAME_SUFFIX
	 */
	private BatchIngestOptions.INGEST_THREAD_TYPE ingestThreadType = BatchIngestOptions.INGEST_THREAD_TYPE.REMOTE; // 

	/** Logger for this class and subclasses */
	public final  static Log logger = LogFactory.getLog( "IngestWorker".getClass() );

	/**
	 *  HAVE_SENT_FAILURE_EMAIL limits the # of failure email's sent.
	 *  
	 *  NOTE: This is not the same as the ingest failure email, which is performed by the BatchIngestController performing the ingest.
	 *  This email should never happen is probably due to a configuration problem.
	 */
	boolean HAVE_SENT_FAILURE_EMAIL = false;

	public  String FAILURE_EMAIL_FROM_ADDRESS = "";

	protected   String institution = null;
	protected   String batchSet    = null;

	private  ProgramProperties batchSetProperties     = null;

	private ProgramProperties programProperties  = null;

	private String myName = "Name Not Set"; // set by caller.

	private String emailSuccessAddress = null; 
	private String emailFailureAddress = null;
	private String emailSuccessAddress_2 = null;
	private String emailFailureAddress_2 = null;

	private String smtpServerHost = null;
	private String smtpUser       = null;
	private String smtpPassword   = null;
	private String smtpPort       = null;
	private boolean sslEmail      = false;
	private String emailFromAddress = null;;


	// Constructor
	/*
	 * @param batchName is of type codu_ectd where codu is the institution and ectd is the batch set name
	 */
	public IngestWorker( String batchSetName ) // needs to be public for testing.
	{
		String[] temp            = batchSetName.split( "_" );

		this.institution = temp[0];
		this.batchSet    = temp[1];

		this.setName( batchSetName );
	}

	public void  doWork()
	{	
		synchronized (this) 
		{
			if ( isRunning() ){
				return;
			}
			else {
				setRunning( true );
			}
		}
		try
		{
			// we call initProgramProperties each time, so that the configuration can be changed dynamically.

			this.initProgramProperties();

			boolean newBatchStarted = this.tryToStartNewBatchThread();

			if ( newBatchStarted ){
				logger.info( "New "+batchSet+" batch started" );
			}

		} catch (FatalException e){
			logger.error( e.getMessage() );
		}
		finally {
			setRunning( false );
		}
	}

	/**
	 * Make sure no batch is running, if one has just completed clean up any unprocessed reports (including sending the rest response) 
	 * then try to start a new batch.
	 * 
	 * @return true of a new thread was started
	 */
	public boolean tryToStartNewBatchThread()
	{
		boolean newIngestStarted = false;
		try 
		{		
			StatusData currentStatus = null;
			if ( BatchIngestThreadManager.isBatchSetThreadExists( this.getName() ) )
			{
				currentStatus = BatchIngestThreadManager.getAllBatchSetStatus( this.getName() );

				if ( currentStatus != null && currentStatus.isRunning() )
				{ 
					/**
					 * A batch is currently running, so do nothing. We do not confirm that the message was received so it will be sent again.
					 */
					return newIngestStarted; 	 
				}  
			}

			/*
			 * If the user stoped a remote ingest, we don't wan't to re-enable it! The user will need to re-enable it through
			 * the GUI.
			 */
			if ( currentStatus != null && currentStatus.isStoppedByUser() )
			{
				return newIngestStarted;
			}
			
			
			/**
			 * Let's start a new Batch Ingest!
			 */

			BatchIngestOptions batchOptions = FedoraAppUtil.loadRemoteIngestOptions( FedoraAppConstants.getServletContextListener(), this.institution, this.batchSet ); 

			
			batchOptions.setIngestThreadType( this.ingestThreadType );
			

			batchOptions.setStmpHost( this.smtpServerHost );
			batchOptions.setStmpPort( this.smtpPort);
			batchOptions.setStmpUser( this.smtpUser );
			batchOptions.setStmpPassword( this.smtpPassword );
			batchOptions.setSuccessEmail( this.emailSuccessAddress );
			batchOptions.setSuccessEmail_2( this.emailSuccessAddress_2 );
			batchOptions.setFailureEmail(   this.emailFailureAddress );
			batchOptions.setFailureEmail_2( this.emailFailureAddress_2 );
			batchOptions.setStmpUseSSL( this.sslEmail );
			batchOptions.setEmailFromAddress( this.emailFromAddress );

			newIngestStarted = this.startNewBatch( batchOptions );

			// NOTE: The above may not execute if another process got started first.  	
		} 
		catch (Exception e) 
		{
			String errorMsg = this.getName()+" Remote  worker ERROR: "+e.getMessage();
			logger.error( "Unable to start new "+this.getName()+" batch:"+e.getLocalizedMessage() );

			String[] emailToList   = new String[]{ emailFailureAddress, emailFailureAddress_2 };

			try {
				if ( ! HAVE_SENT_FAILURE_EMAIL )
				{
					this.sendEmail( emailToList, this.getName()+" Remote Ingest Failure", errorMsg, this.emailFromAddress, this.smtpPort, this.sslEmail );
					HAVE_SENT_FAILURE_EMAIL = true;

				}
			} catch (Exception e1) {		
				logger.error( "Unable to send fail email"+this.getName()+" batch:"+e1.getLocalizedMessage() );
			} 
		}

		return newIngestStarted; // a new batch ingest thread has been started.
	}

	/**
	 * Load the {institution}{batchSet}_REMOTE.properties OR the {institution}{batchSet}_BACKGROUND.properties file.
	 * 
	 * if this.programProperties == null initialize it, then initialize all other program properties.
	 * @throws FatalException 
	 */
	protected void initProgramProperties() throws FatalException
	{
		if ( this.programProperties == null )
		{
			this.programProperties  = new ProgramFileProperties( new File( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL().getFile() ) ); 

			// Look for either a {batchSet}_REMOTE.properties file or a {batchSet}_TASK.properties file

			File tempFile = new File( FedoraAppConstants.getServletContextListener().getInstituionURL().getFile() + "/" + this.institution + "/" + this.batchSet 
					+ "/" +this.batchSet+FedoraAppConstants.REMOTE_TASK_NAME_SUFFIX+".properties" );
			this.ingestThreadType = BatchIngestOptions.INGEST_THREAD_TYPE.REMOTE;
			if ( ! tempFile.exists() )
			{
				tempFile = new File( FedoraAppConstants.getServletContextListener().getInstituionURL().getFile() + "/" + this.institution + "/" + this.batchSet 
						+ "/" +this.batchSet+FedoraAppConstants.BACKGROUND_TASK_NAME_SUFFIX+".properties" );
				this.ingestThreadType = BatchIngestOptions.INGEST_THREAD_TYPE.BACKGROUND;
			}

			if ( ! tempFile.exists() ){
				String errorMsg  = "Unable to find a "+FedoraAppConstants.REMOTE_TASK_NAME_SUFFIX+" or "+ FedoraAppConstants.BACKGROUND_TASK_NAME_SUFFIX;
				throw new FatalException( errorMsg );
			}
			this.batchSetProperties = new ProgramFileProperties( tempFile );
		}

		this.emailSuccessAddress   = this.batchSetProperties.getProperty( FedoraAppConstants.REMOTE_SUCCESS_EMAIL_PROPERTIES   );
		this.emailSuccessAddress_2 = this.batchSetProperties.getProperty( FedoraAppConstants.REMOTE_SUCCESS_EMAIL_2_PROPERTIES );
		this.emailFailureAddress   = this.batchSetProperties.getProperty( FedoraAppConstants.REMOTE_FAILURE_EMAIL_PROPERTIES   );
		this.emailFailureAddress_2 = this.batchSetProperties.getProperty( FedoraAppConstants.REMOTE_FAILURE_EMAIL_2_PROPERTIES );

		this.smtpServerHost   = this.batchSetProperties.getProperty( FedoraAppConstants.REMOTE_SMTP_SERVER_PROPERTY      );
		this.smtpUser         = this.batchSetProperties.getProperty( FedoraAppConstants.REMOTE_SMTP_SERVER_USER_PROPERTY );
		this.smtpPassword     = this.batchSetProperties.getProperty( FedoraAppConstants.REMOTE_SMTP_SERVER_PWD_PROPERTY  );
		this.smtpPort         = this.batchSetProperties.getProperty( FedoraAppConstants.REMOTE_SMTP_SERVER_PORT_PROPERTY  );
		this.emailFromAddress = this.batchSetProperties.getProperty( FedoraAppConstants.REMOTE_EMAIL_FROM_ADDRESS_PROPERTIES  );

		String  useSSL  = this.batchSetProperties.getProperty( FedoraAppConstants.REMOTE_SMTP_SERVER_SSL_PROPERTY  );
		this.sslEmail = false;
		if ( useSSL != null && ( useSSL.toLowerCase().contains( "true") || useSSL.equalsIgnoreCase( "T" ) ) ) 
		{
			this.sslEmail = true;
		}
	}


	/**
	 * If there is not a batch currently running, start a new batch, first by processing any 
	 * orphaned files in mets new/updates folders (from previous failure), next look for new batch file
	 * in the work folder.
	 * 
	 * NOTE: If there are multiple files in the work folder, there is no guaranteed of the order in which they
	 * will be processed.
	 * 
	 * @param batchOptions
	 * @throws Exception
	 * @return true if a new batch was started.
	 */
	private  synchronized boolean startNewBatch( BatchIngestOptions batchOptions ) throws Exception
	{         
		String batchIngestFolderUrl = programProperties.getProperty( FedoraAppConstants.BATCH_INGEST_TOP_FOLDER_URL_PROPERTY );
		String batchIngestFolder    = new URL( batchIngestFolderUrl ).getFile();
		String work_folder          = programProperties.getProperty( FedoraAppConstants.BATCH_INGEST_WORK_FOLDER_PROPERTY );
		String mets_new_folder      = programProperties.getProperty( FedoraAppConstants.BATCH_INGEST_NEW_METS_FOLDER_PROPERTY );
		String mets_update_folder   = programProperties.getProperty( FedoraAppConstants.BATCH_INGEST_UPDATES_METS_FOLDER_PROPERTY );

		File ingestWorkFolder  = new File( batchIngestFolder + File.separator + institution + File.separator + batchSet + File.separator + work_folder );
		File metsNewFolder     = new File( batchIngestFolder + File.separator + institution + File.separator + batchSet + File.separator + mets_new_folder );
		File metsUpdateFolder  = new File( batchIngestFolder + File.separator + institution + File.separator + batchSet + File.separator + mets_update_folder );

		String[] workFileList = null;
		File[]        newList = null;
		File[]     updateList = null;

		switch ( batchOptions.getIngestThreadType() )
		{
		case MANUAL:
			throw new FatalException( "MANUAL thread type, in a remote or background task!" );
		case REMOTE:
			workFileList = FileUtil.getRemoteBatchFileList( ingestWorkFolder );   
			newList      = FileUtil.getRemoteXmlFileList(   metsNewFolder    );
			updateList   = FileUtil.getRemoteXmlFileList(   metsUpdateFolder ); 
			break;
		case BACKGROUND:
			workFileList = FileUtil.getTaskBatchFileList( ingestWorkFolder );
			newList      = FileUtil.getTaskXmlFileList(   metsNewFolder    );
			updateList   = FileUtil.getTaskXmlFileList(   metsUpdateFolder ); 
			break;
		}
		
		if (  newList.length > 0 || updateList.length > 0  )
		{
			/** 
			 * There are METS files waiting to be processed, due to previous failure or they were put there by some
			 *  other means so go ahead and process them.
			 */
			batchOptions.setSplitXMLinWorkDirToMets( false ); // ignore any work files.
		}
		else 
		{		
			/**
			 * Check for an existing work (batch) file.
			 */
			if ( workFileList.length > 0 ) 
			{
				// prepare to process new batch.
				batchOptions.setWorkFile( workFileList[0] );
				batchOptions.setSplitXMLinWorkDirToMets( true );		
			}
			else
			{
				return false; // nothing to do.
			}
		}
		
		// Check again to make sure there is not a valid running thread
		StatusData currentStatus = null;
		if ( BatchIngestThreadManager.isBatchSetThreadExists( this.getName() ) )
		{
			currentStatus = BatchIngestThreadManager.getAllBatchSetStatus( this.getName() );

			if ( currentStatus != null && currentStatus.isRunning() )
			{ 
				/**
				 * A batch is currently running, so do nothing. We do not confirm that the message was received so it will be sent again.
				 */
				return false; 	 
			}  
			else {
				BatchIngestThreadManager.removeBatchset( this.getName() ); // remove any old thread
			}
		}
	
		String fedoraContext = institution;
		FedoraAppBatchIngestController remoteIngestController = new FedoraAppBatchIngestController( new TransformMetsData(), institution, batchSet, fedoraContext, batchOptions );

		BatchIngestThreadManager.setBatchSetThread( this.getName(), remoteIngestController );

		// Create a background thread and start a new batch ingest.
		
		new Thread( remoteIngestController ).start();

		return true;
	}



	/**
	 * 
	 * @param emailList
	 * @param emailSubject
	 * @param emailMessage
	 * @param emailFromAddress
	 * @throws Exception
	 */
	void sendEmail( String[] emailList, String emailSubject, String emailMessage, String emailFromAddress, String port, boolean sslEmail ) throws Exception
	{


		SendMail.postMailWithAuthenication( emailList, emailSubject, emailMessage,  emailFromAddress, smtpServerHost, smtpUser, smtpPassword, port, sslEmail );
	}

	public void sendFailureEmail( String errorMsg ){

		String[] emailToList   = new String[]{ emailFailureAddress, emailFailureAddress_2 };

		try 
		{		
			SendMail.postMailWithAuthenication( emailToList, this.getName()+" Remote Ingest Failure", errorMsg, FAILURE_EMAIL_FROM_ADDRESS, this.smtpServerHost, smtpUser, smtpPassword, this.smtpPort, this.sslEmail );
		} 
		catch (Exception e) {

			this.logger.error( "Unable to send failure email:"+e.getMessage()+" - The message was:"+errorMsg );
		}
	}

	public String getName() 
	{
		return this.myName;
	}

	public WorkerInf setName(String workerName) 
	{
		this.myName = workerName;

		return this;
	}


	private static synchronized boolean isRunning() {
		return IngestWorker.running;
	}

	private static synchronized void setRunning(boolean running) {
		IngestWorker.running = running;
	}

}
