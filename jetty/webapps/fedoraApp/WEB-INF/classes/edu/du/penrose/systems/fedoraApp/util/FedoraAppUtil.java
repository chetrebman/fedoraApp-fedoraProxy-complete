package edu.du.penrose.systems.fedoraApp.util;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.du.penrose.systems.exceptions.FatalException;
import edu.du.penrose.systems.fedora.client.Administrator;
import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraApp.ProgramFileProperties;
import edu.du.penrose.systems.fedoraApp.ProgramProperties;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestOptions;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestOptions.INGEST_THREAD_TYPE;
import edu.du.penrose.systems.util.FileUtil;
import edu.du.penrose.systems.util.MyServletContextListener_INF;


public class FedoraAppUtil 
{
	static public enum INGEST_TYPE{ FEDORA, ISLANDORA }
	
	/*
	 * We have two administrators for the simple reason that there two ways to login, one through the GUI and one usng 
	 * credentials in batchIngest.properties for remote ingest tasks.
	 */
	private static Administrator administratorForManualIngest     = null;
	private static Administrator administratorForRemoteTaskIngest = null;
	
    /** Logger for this class and subclasses */
    public final  static Log logger = LogFactory.getLog( "IngestWorker".getClass() );
    
	/**
	 * The unique name for this batch set run. This name is used for creating  report and file name. 
	 *<br><br>
	 * Email fields have been added to match those in the _REMOTE.propeties files, but they won't actually work until they are added to 
	 * the form in batchIngest.jsp Until email is added to the web ingest form, these values will always be null so they will be set to
	 * empty in the properties file, every time a new ingest is started and the form is saved.
	 *
	 * @param institution institution for this batch set.
	 * @param batchSet the batch set name.
	 * @return unique name containing the institution and batchset names.
	 */
	public static String getUniqueBatchRunName( String institution, String batchSet ) 
	{	
		return institution+"_"+batchSet+"."+FileUtil.getDateTimeMilliSecondEnsureUnique();
	}

	static public INGEST_TYPE getIngestType()
	{
		INGEST_TYPE returnType = INGEST_TYPE.FEDORA;
		
		ProgramProperties progProp = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() );
		
	    String islandIngest = progProp.getProperty( FedoraAppConstants.ISLANDORA_INGEST_PROPERTY );
	    if ( islandIngest != null && islandIngest.toLowerCase().contains( "true" ) )
	    {
	    	returnType = INGEST_TYPE.ISLANDORA;
	    } 
	    
	    return returnType;
	}
	
	public static void saveIngestFileOptions( ProgramProperties optionsProperties, BatchIngestOptions batchIngestCommand )  throws Exception
	{
		optionsProperties.saveProperty( FedoraAppConstants.BATCH_OPTION_STOP_ERROR_PROPERTY, batchIngestCommand.isStopOnError() );
		optionsProperties.saveProperty( FedoraAppConstants.BATCH_OPTION_DELETE_LOG_PROPERTY, batchIngestCommand.isClearLogFile() );
		optionsProperties.saveProperty( FedoraAppConstants.BATCH_OPTION_DELETE_FAILED_PROPERTY, batchIngestCommand.isClearFailedFiles() );
		optionsProperties.saveProperty( FedoraAppConstants.BATCH_OPTION_DELETE_COMPLETED_PROPERTY, batchIngestCommand.isClearCompletedFiles() );
		optionsProperties.saveProperty( FedoraAppConstants.BATCH_OPTION_MOVE_PCOs_PROPERTY, batchIngestCommand.isMoveIngestedPCOsToCompleted() );
		optionsProperties.saveProperty( FedoraAppConstants.BATCH_OPTION_SPLIT_METS_PROPERTY, batchIngestCommand.isSplitXMLinWorkDirToMets() );
		optionsProperties.saveProperty( FedoraAppConstants.BATCH_OPTION_MULTI_METS_FILE_NAME_PROPERTY, batchIngestCommand.getSplitXMLfileName() );
		optionsProperties.saveProperty( FedoraAppConstants.BATCH_OPTION_VALIDATE_PCO_CHECKSUMS_PROPERTY, batchIngestCommand.isValidatePCOchecksums());
		optionsProperties.saveProperty( FedoraAppConstants.BATCH_OPTION_ENFORCE_STRICT_UPDATES_CHECK_PROPERTY, batchIngestCommand.isStrictUpdates() );	
		optionsProperties.saveProperty( FedoraAppConstants.SET_OBJECT_INACTIVE_PROPERTY, batchIngestCommand.isSetObjectInactive() ); 
		optionsProperties.saveProperty( FedoraAppConstants.BATCH_OPTION_ALLOW_MANUAL_AND_REMOTE_PROPERTY, batchIngestCommand.isAllowSimultaneousManualAndRemoteIngest() ); 
		
		// Only save this in batchIngest.properties 10-20-12
		// note we don't handle the FedoraAppConstants.ISLANDORA_INGEST_PROPERTY here since that is only set manually.
		
		optionsProperties.saveProperty( FedoraAppConstants.ISLANDORA_COLLECTION_ATTRIBUTE, batchIngestCommand.getFedoraCollection() );
		optionsProperties.saveProperty( FedoraAppConstants.ISLANDORA_CONTENT_MODEL_ATTRIBUTE, batchIngestCommand.getFedoraContentModel() );    
		
/*
 * Until email is added to the web ingest form, these values will always be null so they will be set to empty in the properties file. 
 * Every time an ingest is started and the form is saved.
 */
		
		optionsProperties.saveProperty( FedoraAppConstants.REMOTE_SUCCESS_EMAIL_PROPERTIES, batchIngestCommand.getSuccessEmail()      );
		optionsProperties.saveProperty( FedoraAppConstants.REMOTE_SUCCESS_EMAIL_2_PROPERTIES,  batchIngestCommand.getSuccessEmail_2() );
		optionsProperties.saveProperty( FedoraAppConstants.REMOTE_FAILURE_EMAIL_PROPERTIES,  batchIngestCommand.getFailureEmail()     );
		optionsProperties.saveProperty( FedoraAppConstants.REMOTE_FAILURE_EMAIL_2_PROPERTIES,  batchIngestCommand.getFailureEmail_2() );
		
		optionsProperties.saveProperty( FedoraAppConstants.REMOTE_EMAIL_FROM_ADDRESS_PROPERTIES,batchIngestCommand.getEmailFromAddress() );

		optionsProperties.saveProperty( FedoraAppConstants.REMOTE_SMTP_SERVER_PROPERTY,      batchIngestCommand.getStmpHost()      );
		optionsProperties.saveProperty( FedoraAppConstants.REMOTE_SMTP_SERVER_PORT_PROPERTY,  batchIngestCommand.getStmpPort()     );
		optionsProperties.saveProperty( FedoraAppConstants.REMOTE_SMTP_SERVER_USER_PROPERTY,  batchIngestCommand.getStmpUser()     );
		optionsProperties.saveProperty( FedoraAppConstants.REMOTE_SMTP_SERVER_PWD_PROPERTY,   batchIngestCommand.getStmpPassword() );
		
		optionsProperties.saveProperty( FedoraAppConstants.REMOTE_SMTP_SERVER_SSL_PROPERTY,   batchIngestCommand.isStmpUseSSL() );	
	}
	
	private static BatchIngestOptions getPropertiesFromFile( ProgramProperties optionsProperties, BatchIngestOptions batchOptions )
	{
		try {
			
			boolean stopOnError      = optionsProperties.getProperty( FedoraAppConstants.BATCH_OPTION_STOP_ERROR_PROPERTY, batchOptions.isStopOnError() );
			boolean clearLogFile     = optionsProperties.getProperty( FedoraAppConstants.BATCH_OPTION_DELETE_LOG_PROPERTY, batchOptions.isClearLogFile() );
			boolean clearFailedFiles = optionsProperties.getProperty( FedoraAppConstants.BATCH_OPTION_DELETE_FAILED_PROPERTY, batchOptions.isClearFailedFiles() );
			boolean clearCompletedFiles = optionsProperties.getProperty( FedoraAppConstants.BATCH_OPTION_DELETE_COMPLETED_PROPERTY, batchOptions.isClearCompletedFiles() );
			boolean movePCOs         = optionsProperties.getProperty( FedoraAppConstants.BATCH_OPTION_MOVE_PCOs_PROPERTY, batchOptions.isMoveIngestedPCOsToCompleted() );
			boolean splitMETS        = optionsProperties.getProperty( FedoraAppConstants.BATCH_OPTION_SPLIT_METS_PROPERTY, batchOptions.isSplitXMLinWorkDirToMets() );
			String  splitXMLfileName = optionsProperties.getProperty( FedoraAppConstants.BATCH_OPTION_MULTI_METS_FILE_NAME_PROPERTY, batchOptions.getSplitXMLfileName() );
			boolean validatePCOchecksums = optionsProperties.getProperty( FedoraAppConstants.BATCH_OPTION_VALIDATE_PCO_CHECKSUMS_PROPERTY, batchOptions.isValidatePCOchecksums() );
			boolean enforceStrictUpdates =  optionsProperties.getProperty( FedoraAppConstants.BATCH_OPTION_ENFORCE_STRICT_UPDATES_CHECK_PROPERTY, batchOptions.isStrictUpdates() );
			boolean setObjectInactive = optionsProperties.getProperty( FedoraAppConstants.SET_OBJECT_INACTIVE_PROPERTY, batchOptions.isSetObjectInactive() );
			boolean allowManualAndRemote = optionsProperties.getProperty( FedoraAppConstants.BATCH_OPTION_ALLOW_MANUAL_AND_REMOTE_PROPERTY, batchOptions.isAllowSimultaneousManualAndRemoteIngest() );
			
			String collection    = optionsProperties.getProperty( FedoraAppConstants.ISLANDORA_COLLECTION_ATTRIBUTE );
			String contentModel = optionsProperties.getProperty( FedoraAppConstants.ISLANDORA_CONTENT_MODEL_ATTRIBUTE );
			
			batchOptions.setStopOnError( stopOnError );
			batchOptions.setClearLogFile( clearLogFile );
			batchOptions.setClearFailedFiles ( clearFailedFiles );
			batchOptions.setClearCompletedFiles ( clearCompletedFiles );   
			batchOptions.setMoveIngestedPCOsToCompleted( movePCOs );
			batchOptions.setSplitXMLinWorkDirToMets( splitMETS );
			batchOptions.setSetObjectInactive( setObjectInactive );
			batchOptions.setWorkFile( splitXMLfileName );
			batchOptions.setValidatePCOchecksums(validatePCOchecksums);
			batchOptions.setStrictUpdates(enforceStrictUpdates);
			batchOptions.setAllowSimultaneousManualAndRemoteIngest( allowManualAndRemote );
			
	    	batchOptions.setFedoraCollection( collection );    	
	    	batchOptions.setFedoraContentModel(contentModel);
		
			
			// Read the email info
			
			/*
			 * Until email is added to the web ingest form, these values will be set to empty in the properties file. 
			 * Every time an ingest is started and the form is saved.
			 */
				

			String emailSuccessAddress   = optionsProperties.getProperty( FedoraAppConstants.REMOTE_SUCCESS_EMAIL_PROPERTIES      );
			String emailSuccessAddress_2 = optionsProperties.getProperty( FedoraAppConstants.REMOTE_SUCCESS_EMAIL_2_PROPERTIES      );
			String emailFailureAddress   = optionsProperties.getProperty( FedoraAppConstants.REMOTE_FAILURE_EMAIL_PROPERTIES    );
			String emailFailureAddress_2 = optionsProperties.getProperty( FedoraAppConstants.REMOTE_FAILURE_EMAIL_2_PROPERTIES    );
			
			String emailFromAddress  = optionsProperties.getProperty( FedoraAppConstants.REMOTE_EMAIL_FROM_ADDRESS_PROPERTIES );

			String smtpServerHost = optionsProperties.getProperty( FedoraAppConstants.REMOTE_SMTP_SERVER_PROPERTY          );
			String smtpPort       = optionsProperties.getProperty( FedoraAppConstants.REMOTE_SMTP_SERVER_PORT_PROPERTY     );
			String smtpUser       = optionsProperties.getProperty( FedoraAppConstants.REMOTE_SMTP_SERVER_USER_PROPERTY     );
			String smtpPassword   = optionsProperties.getProperty( FedoraAppConstants.REMOTE_SMTP_SERVER_PWD_PROPERTY      );
			
			// now set the options 
			
			batchOptions.setSuccessEmail(   emailSuccessAddress   );
			batchOptions.setSuccessEmail_2( emailSuccessAddress_2 );
			batchOptions.setFailureEmail(   emailFailureAddress   );
			batchOptions.setFailureEmail_2( emailFailureAddress_2 );
			
			batchOptions.setEmailFromAddress( emailFromAddress );
					
			batchOptions.setStmpHost( smtpServerHost );
			batchOptions.setStmpPort( smtpPort);
			batchOptions.setStmpUser( smtpUser );
			batchOptions.setStmpPassword( smtpPassword );

			String  useSSL  = optionsProperties.getProperty( FedoraAppConstants.REMOTE_SMTP_SERVER_SSL_PROPERTY  );
			boolean sslEmail = false;
			if ( useSSL != null && ( useSSL.toLowerCase().contains( "true") || useSSL.equalsIgnoreCase( "T" ) ) ) 
			{
				sslEmail = true;
			}
			
			batchOptions.setStmpUseSSL( sslEmail );
		}
		catch ( Exception e )
		{
			logger.error( "ERROR: unable to open properties file: institution:"+batchOptions.getInstitution()+" batchSet:"+batchOptions.getBatchSet() );
		}
	
		return batchOptions;
	}
	
	/**
	 * Create a new BatchIngestOptions object and load it with ingest options from the {instituion}{batchSet}.properties file.
	 *<br><br>
	 * Email fields have been added to match those in the _REMOTE.propeties files, but they won't actually work until they are added to 
	 * the form in batchIngest.jsp Until email is added to the web ingest form, these values will always be null so they will be set to
	 * empty in the properties file, every time a new ingest is started and the form is saved.
	 * 
	 * @param optionsProperties the {instituion}{batchSet}.properties file.
	 * @return
	 */
	private static  BatchIngestOptions loadIngestFileOptions(  ProgramProperties optionsProperties )
	{
		BatchIngestOptions batchOptions = BatchIngestOptions.getGenericBatchOptions();
				
		return getPropertiesFromFile( optionsProperties, batchOptions );
	}
	
	/**
	 * Load the institution, batchSet options from a {batchSet}.properties file. Set the ingestType as remote task in the returned batchOptions.
	 * 
	 * @param myServletContextListener
	 * @param myInstitution of type 'codu'
	 * @param myBatchSet  of type 'ectd' 
	 * @return batchOptions for this batchSet
	 */
	public static BatchIngestOptions loadRemoteIngestOptions( MyServletContextListener_INF myServletContextListener, String myInstitution, String myBatchSet )
	{
		ProgramProperties optionsPropertiesFile  = new ProgramFileProperties( new File( myServletContextListener.getInstituionURL().getFile() + myInstitution +"/"+ myBatchSet + "/"+ myBatchSet+".properties" ) ); 
		
		BatchIngestOptions batchOptions = loadIngestFileOptions( optionsPropertiesFile );
			
			// unique for remote ingest
			
		batchOptions.setBatchDescription( "Remote ingest for "+myInstitution+"_"+ myBatchSet );
			
		batchOptions.setInstitution( myInstitution ); // for a manual ingest this is set by the form controller
		batchOptions.setBatchSet( myBatchSet );
		
		batchOptions.setIngestThreadType( INGEST_THREAD_TYPE.REMOTE );
   	
	    	// set to true even though work file may not exist if the batch ingest is an add of type replayWithPid
	    batchOptions.setSplitXMLinWorkDirToMets( true );

	    return batchOptions;	
	}

	/**
	 * Load the institution, batchSet options from a {batchSet}.properties file. Set the ingestType as background task in the returned batchOptions.
	 * 
	 * @param myServletContextListener
	 * @param myInstitution
	 * @param myBatchSet
	 * @return
	 */
	public static BatchIngestOptions loadBackgroundTaskIngestOptions(MyServletContextListener_INF myServletContextListener, String myInstitution, String myBatchSet )
	{
		ProgramProperties optionsPropertiesFile  = new ProgramFileProperties( new File( myServletContextListener.getInstituionURL().getFile() + myInstitution +"/"+ myBatchSet + "/"+ myBatchSet+".properties" ) ); 
		
		BatchIngestOptions batchOptions = loadIngestFileOptions( optionsPropertiesFile );
			
			// unique for remote ingest
			
		batchOptions.setBatchDescription( "Background task ingest for "+myInstitution+"_"+ myBatchSet );
			
		batchOptions.setInstitution( myInstitution ); // for a manual ingest this is set by the form controller
		batchOptions.setBatchSet( myBatchSet );
		
		batchOptions.setIngestThreadType( INGEST_THREAD_TYPE.BACKGROUND);
			
	    	// set to true even though work file may not exist if the batch ingest is an add of type replayWithPid
	    batchOptions.setSplitXMLinWorkDirToMets( true );
	    	    	
	    return batchOptions;	
	}
	
	/**
	 * @param batchSetName accepts a batchSetName of type institution_batchSet_REMOTE.
	 * To be remote enabled a batchSet must have a batchSet_REMOTE.properites file.
	 * NOTE: the task may or may not be enabled in the taskEnable.properties. This method does
	 * not check the taskEnable.properties file.
	 * 
	 * @return true if there is a file containing remote ingest options
	 */
	public static boolean isHasRemoteIngestPropertiesFile( String batchSetName )
	{
		if ( batchSetName == null ) return false;
		
		boolean result = false;
		
		try 
		{		
			Set tempSet = getRemoteConfiguredInstitutions();
			
			if ( tempSet.contains( batchSetName ) )	result = true;
			
		} catch (Exception e) {
			// TBD
		}
		
		return result;
	}

	
	/**
	 * @param batchSetName accepts a batchSetName of type institution_batchSet_TASK.
	 * NOTE: the task may or may not be enabled in the taskEnable.properties. This method does
	 * not check the taskEnable.properties file.
	 * 
	 * @return true if there is a file containing backgroud task ingest options
	 */
	public static boolean isHasTaskIngestPropertiesFile( String batchSetName )
	{
		if ( batchSetName == null ) return false;
		
		boolean result = false;
		
		try 
		{		
			Set tempSet = getTaskConfiguredInstitutions();
			
			if ( tempSet.contains( batchSetName ) )	result = true;
			
		} catch (Exception e) {
			// TBD
		}
		
		return result;
	}

	/**
	 * Returns strings of the type 'institution'_'batchSet' ie 'codu_ectd' to show the institution and batch set for batch sets 
	 * that have been configured for remote ingest's, with a institution_batchSet_REMOTE.properties file, they may or not be 
	 * enabled in the taskEnable.properties file.
	 * 
	 * @return string of type 'codu_ectd'
	 * @throws Exception
	 */
	public static Set<String> getRemoteConfiguredInstitutions() throws Exception 
	{
		Set<String> remoteEnabledInstutions = new HashSet<String>();

		File institutionDirectory = null;
		String institutionDirectoryPath = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.BATCH_INGEST_TOP_FOLDER_URL_PROPERTY );
	//	logger.info( "topInstitutionPath=" + institutionDirectoryPath );
		
		URL institutionURL = new URL( institutionDirectoryPath );
		if ( institutionURL.getProtocol().toLowerCase().equals( "file" ) ) 
		{ // TBD only file protocol currently supported.

			institutionDirectory = new File( institutionURL.getFile() );
		}
		else {
			throw new FatalException( "Unsupported Protocol for top batch ingest folder (institution directory" );
		}
		
		File[] dirNames = institutionDirectory.listFiles();
		for ( int i=0; i<dirNames.length; i++ )
		{
		//	logger.info( "processFileOrDirectory=" + dirNames[i] );
			if ( dirNames[i].isDirectory() ){
			//	logger.info( "processDirectory=" + dirNames[i] );
				File dirTemp = dirNames[i];
				File[] batchSetList = dirTemp.listFiles();
				if ( batchSetList== null ){  continue; }
				for ( int j=0; j<batchSetList.length; j++ )
				{
				//	logger.info( "processFileOrDirectory=" + batchSetList[j] );
					if ( batchSetList[j].isDirectory() ) 
					{
				//		logger.info( "processDirectory=" + batchSetList[j] );
						File configFile = new File( batchSetList[j]+"/"+batchSetList[j].getName()+FedoraAppConstants.REMOTE_TASK_NAME_SUFFIX+".properties" );
						if ( configFile.exists() )
						{
					//		logger.info( "Add worker for "+configFile.getName() );
							// this will create a worker name of type 'codu_ectd_REMOTE'
							String workerName = dirNames[i].getName() + "_" + batchSetList[j].getName()+FedoraAppConstants.REMOTE_TASK_NAME_SUFFIX;
							remoteEnabledInstutions.add( workerName );
						}					
					}
				}
			}	
		}

		return remoteEnabledInstutions;
	}
	
	
	/**
	 * Returns strings of the type 'institution'_'batchSet' ie 'codu_ectd' to show the institution and batch set for batch sets 
	 * that have been configured for background tasks ingest's, with a institution_batchSet_TASK.properties file, they may or not be 
	 * enabled in the taskEnable.properties file.
	 * 
	 * @return string of type 'codu_ectd'
	 * @throws Exception
	 */
	public static Set<String> getTaskConfiguredInstitutions() throws Exception 
	{
		Set<String> taskEnabledInstutions = new HashSet<String>();

		File institutionDirectory = null;
		String institutionDirectoryPath = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.BATCH_INGEST_TOP_FOLDER_URL_PROPERTY );
		URL institutionURL = new URL( institutionDirectoryPath );
		if ( institutionURL.getProtocol().toLowerCase().equals( "file" ) ) 
		{ // TBD only file protocol currently supported.

			institutionDirectory = new File( institutionURL.getFile() );
		}
		else {
			throw new FatalException( "Unsupported Protocol for top batch ingest folder (institution directory" );
		}
		
		File[] dirNames = institutionDirectory.listFiles();
		for ( int i=0; i<dirNames.length; i++ )
		{
			if ( dirNames[i].isDirectory() ){
				File dirTemp = dirNames[i];
				File[] batchSetList = dirTemp.listFiles();
				for ( int j=0; j<batchSetList.length; j++ )
				{
					if ( batchSetList[j].isDirectory() ) 
					{											
							File configFile = new File( batchSetList[j]+"/"+batchSetList[j].getName()+FedoraAppConstants.BACKGROUND_TASK_NAME_SUFFIX+".properties" );
							if ( configFile.exists() )
							{
								// this will create a worker name of type 'codu_ectd_REMOTE'
								String workerName = dirNames[i].getName() + "_" + batchSetList[j].getName()+FedoraAppConstants.BACKGROUND_TASK_NAME_SUFFIX;
								taskEnabledInstutions.add( workerName );
							}
					}
				}
			}	
		}

		return taskEnabledInstutions;
	}

	
	
	/**
	 * Note this will give back a pid even if the context is NOT valid! This uses a FedoraAdministrator with credentials pulled from the
	 * batchIngest.propertes file.
	 * 
	 * See runBatch() notes.

	 * @param fedoraContext
	 * @param numberOfPids
	 * @return array of reserved pids
	 * @throws FatalException
	 */
	public static String[] getPIDs( String fedoraContext, NonNegativeInteger numberOfPids ) throws FatalException 
	{
		
		if ( numberOfPids.intValue() < 1 ){
			throw new FatalException( "Invalid number of pids reqeuested:"+numberOfPids );
		}
		
		String[] pids = null;
		try {
			pids = getAdministratorUsingProgramProperiesFileCredentials().getAPIM().getNextPID( numberOfPids, fedoraContext );

			if ( pids.length != numberOfPids.intValue() ) 
			{
				throw new Exception( "Asked Fedora for "+numberOfPids+" pids but got "+pids.length+" back." );
			}
		} 
		catch ( Exception e ) {
			String errorMsg = "Unable to get PID:"+e.getLocalizedMessage();
			Log logger = LogFactory.getLog( "edu.du.penrose.systems.fedoraApp.batchIngest.bus.FedoraAppBatchIngestController" );
			logger.fatal( errorMsg );
			throw new FatalException( errorMsg );
		}
		
		return pids;
	}
	
	/**
	 * Note this will give back a pid even if the context is NOT valid!
	 * 
	 * @param host
	 * @param port
	 * @param userName
	 * @param password
	 * @param fedoraContext
	 * @param numberOfPids
	 * @return
	 * @throws FatalException
	 */
	public static String[] getPIDs( String host, int port, String userName, String password, String fedoraContext, NonNegativeInteger numberOfPids ) throws FatalException 
	{
		
		if ( numberOfPids.intValue() < 1 ){
			throw new FatalException( "Invalid number of pids reqeuested:"+numberOfPids );
		}
		
		String[] pids = null;
		try {
			 
			pids = getAdministrator( host, port, userName, password ).getAPIM().getNextPID( numberOfPids, fedoraContext );

			if ( pids.length != numberOfPids.intValue() ) 
			{
				throw new Exception( "Asked Fedora for "+numberOfPids+" pids but got "+pids.length+" back." );
			}
		} 
		catch ( Exception e ) {
			String errorMsg = "Unable to get PID:"+e.getLocalizedMessage();
			Log logger = LogFactory.getLog( "edu.du.penrose.systems.fedoraApp.batchIngest.bus.FedoraAppBatchIngestController" );
			logger.fatal( errorMsg );
			throw new FatalException( errorMsg );
		}
		
		return pids;
	}

	/**
	 * Note this will give back a pid even if the context is NOT valid!
	 * 
	 * @param host
	 * @param port
	 * @param userName
	 * @param password
	 * @param fedoraContext
	 * @param numberOfPids
	 * @return
	 * @throws FatalException
	 */
	public static String[] getPIDs( Administrator admin, String fedoraContext, NonNegativeInteger numberOfPids ) throws FatalException 
	{
		
		if ( numberOfPids.intValue() < 1 ){
			throw new FatalException( "Invalid number of pids reqeuested:"+numberOfPids );
		}
		
		String[] pids = null;
		try 
		{ 
			pids = admin.getAPIM().getNextPID( numberOfPids, fedoraContext );

			if ( pids.length != numberOfPids.intValue() ) 
			{
				throw new Exception( "Asked Fedora for "+numberOfPids+" pids but got "+pids.length+" back." );
			}
		} 
		catch ( Exception e ) {
			String errorMsg = "Unable to get PID:"+e.getLocalizedMessage();
			Log logger = LogFactory.getLog( "edu.du.penrose.systems.fedoraApp.batchIngest.bus.FedoraAppBatchIngestController" );
			logger.fatal( errorMsg );
			throw new FatalException( errorMsg );
		}
		
		return pids;
	}
	
	/**
	 * Get THE SINGLETON MANUAL ingest administrator, THERE IS ONLY ONE, if it does not exist this function will create it.
	 * 
	 * if System properties javax.net.ssl.trustStore or javax.net.ssl.trustStorePassword are not set, they are set 
	 * to "\\fedora\\client\\truststore" and "tomcat" respectively.
	 * 
	 * @return a loged in fedora administrator
	 * @throws FatalException if unable to login
	 */
	public static Administrator getAdministrator( String host, int port, String userName, String password ) throws FatalException
	{		
        try {
        	
	        if ( System.getProperty("javax.net.ssl.trustStore") == null )	
	        {
		        System.setProperty( "javax.net.ssl.trustStore", "\\fedora\\client\\truststore" ); // TBD
	        }

	        if ( System.getProperty("javax.net.ssl.trustStorePassword") == null )	
	        {
		        System.setProperty( "javax.net.ssl.trustStorePassword", "tomcat" ); // TBD
	        }
	          	
    		administratorForManualIngest = new Administrator( "http", port, host, userName, password );

		} catch (Exception e) {
			throw new FatalException( "Unable to get Fedora Administrator:"+e.getLocalizedMessage() );
		}
        
        return administratorForManualIngest;
	}
	
	/**
	 * Return either the Fedora manual ingest administrator or the remote administrator depending on the batchIngestOptions.getIngestThreadType().
	 * This will create a remote administrator if it has not been initialized, however the manual administrator will throw a
	 * login exception if the login form has not initialized it yet.
	 * 
	 * @param batchOptions
	 * @return
	 * @throws FatalException
	 */
	public static Administrator getAdministrator( BatchIngestOptions batchOptions ) throws FatalException
	{		
		Administrator myAdministrator = null;
		
		if ( batchOptions.getIngestThreadType() == INGEST_THREAD_TYPE.REMOTE || batchOptions.getIngestThreadType() == INGEST_THREAD_TYPE.BACKGROUND )
		{
			myAdministrator = getAdministratorUsingProgramProperiesFileCredentials();
		}
		else
		{
			myAdministrator = administratorForManualIngest;
			
	    	if ( myAdministrator == null ){
	    		String errorMsg = "Unable to get Fedora Administrator: Are you logged in?";
	    		logger.fatal( errorMsg );
	    		throw new FatalException( errorMsg );
	        }
		}

        return myAdministrator;
	}

	/**
	 * Get THE SINGLETON REMOTE TASK ingest administrator, THERE IS ONLY ONE, if it does not exist this function will create it..
	 * This is a method to get a fedora administrator using the settings in the program properties file (batchIngest.properites).
	 * This is a different administrator then the one used for manual ingests
	 * if System properties javax.net.ssl.trustStore or javax.net.ssl.trustStorePassword are not set, they are set 
	 * to "\\fedora\\client\\truststore" and "tomcat" respectively.
	 * 
	 * @return a loged in fedora aministrator
	 * @throws FatalException
	 */
	public static Administrator getAdministratorUsingProgramProperiesFileCredentials() throws FatalException
	{		
        try {
	        if ( administratorForRemoteTaskIngest == null )
	        {
		        if ( System.getProperty("javax.net.ssl.trustStore") == null )	
		        {
			        System.setProperty( "javax.net.ssl.trustStore", "\\fedora\\client\\truststore" ); // TBD
		        }
	
		        if ( System.getProperty("javax.net.ssl.trustStorePassword") == null )	
		        {
			        System.setProperty( "javax.net.ssl.trustStorePassword", "tomcat" ); // TBD
		        }
		          	
	    		String host     = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.FEDORA_HOST_PROPERTY );
	    		String port     = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.FEDORA_PORT_PROPERTY );
	    	   		
	    		String userName = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.FEDORA_USER_PROPERTY );	
	    		String pwd      = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.FEDORA_PWD_PROPERTY );
	    			
	    		administratorForRemoteTaskIngest =  new Administrator( "http", Integer.parseInt( port ), host, userName, pwd );
	        }
    
		} catch (Exception e) {
			throw new FatalException( "Unable to get Fedora Administrator using batchIngest.properites credentials:"+e.getLocalizedMessage() );
		}		
		
        return administratorForRemoteTaskIngest;
	}

	/**
	 * Take the existing batchIngestOptions (probably created by a form controller) and load it the settings of a properties file.
	 * @param optionsPropertiesFile
	 * @param batchIngestCommand
	 * @return  the batchIngestCommand with settings loaded from the properties file
	 */
	public static BatchIngestOptions loadIngestFileOptions(	ProgramProperties optionsPropertiesFile, BatchIngestOptions batchIngestCommand ) 
	{	
		return getPropertiesFromFile( optionsPropertiesFile, batchIngestCommand );
	}
	
}
