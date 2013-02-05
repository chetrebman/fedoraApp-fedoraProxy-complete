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

package edu.du.penrose.systems.fedoraApp;

import org.springframework.web.context.ServletContextAware;

import edu.du.penrose.systems.util.MyServletContextListener_INF;

 
/**
 * Gives access to FedoraAppConstants and Resources for the Fedora Web Application. This 
 * class is loaded with the servlet context at startup time.
 * <br>
 * <br>
 * To activate ServletContextAware interface the The following MUST be set in the xxxx-servlet.xml file..
 * <bean id="appConstants" class="edu.du.penrose.systems.kepi.ContextConstants" />
 * 
 * @see ServletContextAware
 * @author chet.rebman
 *
 */

public class FedoraAppConstants {      
	
	/**
	 * When processing a version one batch file (no command line), we need to inject an add command into the split xml files.
	 * 
	 */
	final static public String VERSION_ONE_COMMAND_LINE = "<!--<ingestControl command=\"A\" />-->";

	public static final String DEFAULT_BATCH_DESCRIPTION = "No batch description";
	
	/**
	 * We may be using the amd section to contain adr specific information, such as Islandora collection and content model.
	 * 
	 * EXAMPLE:
	 * <mets:dmdSec ID="dmdAlliance">
	 *     <mets:mdWrap MIMETYP="text/xml" MDTYPE="OTHER" LABEL="CustomAlliance Metadata">
	 *        <mets:xmlData>
 	 *           <islandora collection="codu:nnnn" contentModel="codu:coduBasicObject"
	 *        </mets:xmlData>
	 *     </mets:mdWrap>
	 * </mets:dmdSec>
	 */
	
		// markers for the alliance dmd section, see above, NOTE: see mixed_content_directory below
	public static final String DMD_ALLIANCE_ID           = "dmdAlliance";
	public static final String METS_DMD_SEC_ELEMENT_NAME = "dmdSec";
	public static final String METS_DMD_END_TAG          = "</mets:dmdSec>";
	public static final String ISLANDORA_ELEMENT_NAME    = "islandora";
	
		// in order to process a dmd section, see above, the institution must have a 'mixed' content directory.
	public static final String MIXED_CONTENT_DIRECTORY   = "mixed";

       // for fedora relationships (islandora), stored in a batchset properties file ie ectd.properies
	public static final String ISLANDORA_COLLECTION_ATTRIBUTE    = "collection";
	public static final String ISLANDORA_CONTENT_MODEL_ATTRIBUTE = "contentModel";
	

	public static String BATCH_FILE_IDENTIFIER = "batch_"; // this will match batch_ingest or batch_overlay, we test lowercase
	public static String BATCH_FILE_IDENTIFIER_2 = "_batch";
	public static String BATCH_FILE_SUFFIX     = ".xml";
	public static String BAGIT_FILE_SUFFIX     = ".zip";
	
    
    /** Properties UNIQUE to the remote ingest tasks **/
    static public final String REMOTE_SUCCESS_EMAIL_PROPERTIES   =  "successEmail";
    static public final String REMOTE_FAILURE_EMAIL_PROPERTIES   =  "failureEmail";
    static public final String REMOTE_SUCCESS_EMAIL_2_PROPERTIES =  "successEmail_2";
    static public final String REMOTE_FAILURE_EMAIL_2_PROPERTIES =  "failureEmail_2";
    static public final String REMOTE_EMAIL_FROM_ADDRESS_PROPERTIES =  "emailFromAddress";
    
    static public final String REMOTE_SMTP_SERVER_PROPERTY       =  "smtpServer";
    static public final String REMOTE_SMTP_SERVER_USER_PROPERTY  =  "smtpUser";
    static public final String REMOTE_SMTP_SERVER_PWD_PROPERTY   =  "smtpPassword";
    static public final String REMOTE_SMTP_SERVER_PORT_PROPERTY  =  "smtpPort";
    static public final String REMOTE_SMTP_SERVER_SSL_PROPERTY   =  "useSSL";

    /*
     *  Marks Remote ingest tasks, started remotely and the properties files for remote tasks, ie demo_fedoraAppDemoCollection_REMOTE.properties. 
     *  This is also used to mark the batch and ingest files, for remote ingest tasks.
     */
    
    static public final String REMOTE_TASK_NAME_SUFFIX  = "_REMOTE";  // hard coded in gwt BatchIngestStatus.java

    // this is the class that will used to perform the remote ingest.
    static public final String REMOTE_TASK_WORKER_CLASS_PROPERTY = "remoteTaskClass";
      
    /*
     *  Marks background ingest tasks, started remotely and the properties files for remote tasks, ie codu_mixed_TASK.properties.
     *  This is also used to mark the batch and ingest files, for background tasks.
     */
    
    static public final String BACKGROUND_TASK_NAME_SUFFIX  = "_TASK"; // hard coded in gwt BatchIngestStatus.java

    // this is the class that will used to perform the background task ingest.
    static public final String TASK_WORKER_CLASS_PROPERTY = "backgroundTaskClass";
      
    
    /**
     * JMS is currently NOT being used do to 'issues'
     * 
     * Set up the jms service.
     * If enabled the Fedora jms service is used to notify when a task is complete and the name of the result files should be emailed. 
     * This is more robust, but requires a working local version of Fedora to be running. When a batch ingest task is running in the
     * background, such as the ECTD, an ingest completes, but the email was not being sent until the next timer cycle. A server shutdown
     * could cause completion emails to not be sent. This why jms is recommended.
     */
    static public final String JMS_ENABLE          = "useJMS";
    static public final String JMS_SERVER_PROPERTY = "jmsHost";
    static public final String JMS_PORT_PROPERTY   = "jmsPort";
    
    /**
     * JMS is currently NOT being used do to 'issues'
     * 
     * This jms queue receives a message, with the report names to be emails for final results.
     */
    static public final String JMS_ECTD_RESULTS_Q = "ECTD_reportNames";   

    /**
     * JMS is currently NOT being used do to 'issues'
     * 
     * This jms queue triggers that start of a new ingest. text of message is ignored as of 4-19-11
     */
	public static final String JMS_ECTD_START_Q   = "ECTD_startNewBatch"; 
    
	/**
	 * At top of file that contains multiple batch files: <batch xmlRecordUpdates="true" >  If true all records should exist and these are updates.
	 * False if all records should be new and not exist in the index.
	 */
	static public final String BATCH_FILE_UPDATE_MARKER = "batchIsUpdates";
	
    /** XSLT Transform system properties **/
    static public final String TRANSFORMER_FACTORY_KEY = "javax.xml.transform.TransformerFactory";
    static public final String SAXON_6_5_SYSTEM_FACTORY = "com.icl.saxon.TransformerFactoryImpl";
    static public final String SAXON_9_SYSTEM_FACTORY  = "net.sf.saxon.TransformerFactoryImpl";

	public static final String METS_SCHEMA_URL  = "http://www.loc.gov/standards/mets/mets.xsd";

	public static final String FOXML_SCHEMA_URL = "http://www.fedora.info/definitions/1/0/foxml1-0.xsd";
    
    /** APPLICATION_CONTEXT is only for code such as GWT client code that can not get the application code dynamically. */
  //  static public String APPLICATION_CONTEXT_URL = "/fedoraApp/";

    static public final String FEDORA_USERNAME_SESSION_VARIBLE = "fedoraUserName";
    static public final String FEDORA_ADMIN_ATTRIBUTE_NAME     = "FedoraAdministrator";
    
    static public final String METS_TO_FOXML_XSL = "metsToFOXml.xsl";
    static public final String METS_TO_SOLR_XSL  = "modsToSolr.xsl";
    
    static public final String BATCH_INGEST_FOXML_FILE_NAME  = "foxmlForIngest.xml";
        
    /** Application properties file properties */
    static public final String BATCH_OPTION_STOP_ERROR_PROPERTY     = "stopOnError";
    static public final String BATCH_OPTION_DELETE_LOG_PROPERTY     = "deleteLogFile";
    static public final String BATCH_OPTION_DELETE_FAILED_PROPERTY  = "deleteFailedFiles";
    static public final String BATCH_OPTION_DELETE_COMPLETED_PROPERTY  = "deleteCompletedFiles";
    static public final String BATCH_OPTION_MOVE_PCOs_PROPERTY      = "movePCOsToCompleted";
    static public final String BATCH_OPTION_SPLIT_METS_PROPERTY     = "splitMETS";
    static public final String BATCH_OPTION_MULTI_METS_FILE_NAME_PROPERTY     = "splitMETSFileName";
    public static final String SOLR_SEARCH_RESULTS_ATTRIBUTE_NAME     = "solrSearchResults";
    public static final String SOLR_FACET_RESULTS                     = "facetCounts";
    static public final String BATCH_OPTION_VALIDATE_PCO_CHECKSUMS_PROPERTY     = "validatePCOchecksums";
    static public final String BATCH_OPTION_ALLOW_MANUAL_AND_REMOTE_PROPERTY     = "allowSimultaneousManualAndRemote";
    
	
    /** 
     * Strict updates, causes an error if a record does not exist during an update or does exist when inserting a new record. 
     * */
    static public final String BATCH_OPTION_ENFORCE_STRICT_UPDATES_CHECK_PROPERTY = "strictUpdateCheck";
    
    /**
     * Set object inactive after ingest, for workflow review before publishing.
     */
	public static final String SET_OBJECT_INACTIVE_PROPERTY = "setObjectInactve";
    
    static public final String FEDORA_MODS_DATASTREAM_LABEL = "Metadata Object Description Schema";
    static public final String FEDORA_DC_DATASTREAM_LABEL   = "Dublin Core Record";
    static public final String FEDORA_METS_DATASTREAM_LABEL = "Metadata Encoding and Transmission Standard";
    
    static public final String FORM_DEFAULT_SELECT_LABEL = "Please Select";
    static public final String FORM_DEFAULT_SELECT_VALUE = "Please Select"; // HARD CODED INTO batchIngest.jsp !!!!!
    
    static public final String FEDORA_WEB_CONTEXT_PATH = "/fedora";

    static public final String PROPERTIES_FILE_NAME       = "batchIngest.properties";
    static public final String TASK_ENABLE_PROPERTIES_FILE_NAME = "taskEnable.properties";
    static public final String ETD_TASK_PROPERTIES_FILE_NAME = "etd.properties";
    
    static public final String ETD_ZIP_DIR_PROPERTY  = "ETD_ZIP_DIR";
    static public final String ETD_TEMP_DIR_PROPERTY = "ETD_TEMP_DIR";
    static public final String ETD_XML_DIR_PROPERTY  = "ETD_XML_DIR";
    static public final String ETD_PCO_DIR_PROPERTY  = "ETD_PCO_DIR";
    static public final String ETD_COMPLETED_DIR_PROPERTY = "ETD_COMPLETED_DIR";
    static public final String ETD_EMBARGOED_DIR_PROPERTY = "ETD_EMBARGOED_DIR";
    static public final String ETD_FAILED_DIR_PROPERTY   = "ETD_FAILED_DIR";
    static public final String ETD_YEAR_COLLECTION_PROPERTY = "ETD_YEAR_COLLECTION";
    static public final String ETD_CONTENT_MODEL_PROPERTY   = "ETD_CONTENT_MODEL";
    		
    static public final String TASK_ENABLE_PROPERTY = "enableTasks";
    
	/*
	 * These properties are used in the main fedoraApp.properties file.*********
	 */
	public static final String FEDORA_HOST_PROPERTY   = "FEDORA_HOST";
	public static final String FEDORA_PORT_PROPERTY   = "FEDORA_PORT";

    /*
     * the Fedora user and password are needed for background  remote ingest tasks.
     */
	public static final String FEDORA_USER_PROPERTY = "FEDORA_USER";
	public static final String FEDORA_PWD_PROPERTY   = "FEDORA_PWD";

	public static final String ISLANDORA_INGEST_PROPERTY              = "ISLANDORA_INGEST";
	
	public static final String BATCH_INGEST_TOP_FOLDER_URL_PROPERTY   = "BATCH_INGEST_TOP_FOLDER_URL";
	public static final String BATCH_INGEST_NEW_METS_FOLDER_PROPERTY  = "BATCH_INGEST_NEW_XML_FOLDER";
	public static final String BATCH_INGEST_UPDATES_METS_FOLDER_PROPERTY  = "BATCH_INGEST_UPDATES_XML_FOLDER";
	public static final String BATCH_INGEST_FILES_FOLDER_PROPERTY     = "BATCH_INGEST_PCO_FOLDER";
    public static final String BATCH_INGEST_COMPLETED_FOLDER_PROPERTY = "BATCH_INGEST_COMPLETED_FOLDER";
    public static final String BATCH_INGEST_FAILED_FOLDER_PROPERTY    = "BATCH_INGEST_FAILED_FOLDER";
    public static final String BATCH_INGEST_FAILED_BATCH_FOLDER_PROPERTY = "BATCH_INGEST_FAILED_BATCH_FOLDER";
    public static final String BATCH_INGEST_WORK_FOLDER_PROPERTY      = "BATCH_INGEST_WORK_FOLDER";
	public static final String BATCH_INGEST_LOGS_FOLDER_PROPERTY      = "BATCH_INGEST_LOGS_FOLDER";
	public static final String BATCH_INGEST_TASKS_TEMP_FOLDER_PROPERTY  = "BATCH_INGEST_TASKS_TEMP_FOLDER";
	public static final String BATCH_INGEST_COMPLETED_BATCH_FOLDER_PROPERTY = "BATCH_INGEST_COMPLETED_BATCH_FOLDER";
	public static final String BATCH_INGEST_IMAGES_FOLDER_PROPERTY     = "BATCH_INGEST_IMAGES_FOLDER";


    public static final String BATCH_INGEST_HANDLE_SERVER_PROPERTY      = "BATCH_INGEST_ADR_HANDLE_SERVER";
    public static final String BATCH_INGEST_HANDLE_SERVER_PORT_PROPERTY = "BATCH_INGEST_ADR_HANDLE_SERVER_PORT";
    public static final String BATCH_INGEST_HANDLE_SERVER_APP_PROPERTY  = "BATCH_INGEST_ADR_HANDLE_SERVER_APP";

    public static final String BATCH_INGEST_WORLD_HANDLE_SERVER_PROPERTY = "BATCH_INGEST_WORLD_HANDLE_SERVER";
    
    public static final String BATCH_INGEST_DISABLE_GET_HANDLE_PROPERTY = "BATCH_INGEST_DISABLE_GET_HANDLE";
    
    public static final String BATCH_INGEST_DISABLE_STOP_BATCH_PROPERTY = "STOP_BATCH";

    public static final String BATCH_INGEST_XML_VALIDATE_PROPERTY     = "BATCH_INGEST_XML_VALIDATE"; 
    public static final String BATCH_INGEST_XML_SCHEMA_CHECK_PROPERTY = "BATCH_INGEST_XML_SCHEMA_CHECK";

    public static final String BATCH_INGEST_ENABLE_SIMULTANEOUS_MANUAL_AND_REMOTE_INGESTS_CHECKBOX_PROPERTY = "BATCH_INGEST_ENABLE_SIMULTANEOUS_MANUAL_AND_REMOTE_INGESTS_CHECKBOX";
    
    public static final String BATCH_INGEST_VALID_PID_FOR_FEDORA_CONNECTION_TEST_PROPERTY = "BATCH_INGEST_VALID_PID_TO_CHECK_FEDORA_CONNECTION";
    
    /*
     * END main fedoraApp.properties file. ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
     */
    
 //   public static String BATCH_INGEST_REPORT_PATH_URL = "file://localhost/batch_space/codu/frid/logs/";
 //   public static String BATCH_INGEST_REPORT_FILE_NAME = "batch_ingest_report.log";
	
    public static final String BATCH_INGEST_PID_REPORT_FILE_EXT = ".csv";
    public static final String BATCH_INGEST_REPORT_FILE_EXT = ".txt";
    
    
    public static final String BATCH_INGEST_VIEW_REPORT_BTN_NAME      = "ViewIngestReport";
    public static final String BATCH_INGEST_VIEW_PID_REPORT_BTN_NAME  = "ViewPidReport";
    public static final String BATCH_INGEST_ENABLE_NEW_BATCH_BTN_NAME = "enableNewBatch"; 
    public static final String BATCH_INGEST_STOP_INGEST_BTN_NAME      = "stopIngest"; 
    

    /** The following is used for the automated remote ingest timer tasks */
	public static final String GENERIC_INSTITUTION_NAME = FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE;
	public static final String DU_INSTITUTION_NAME = "codu";
	public static final String GENERIC_BATCHSET_NAME  = FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE;
	
    /**
     * Constants specific to ECTD are no longer used and are just left as an example. Aug 2012
     * 
     * Used be Fedora Rel-Ext (islandora) for ECTD
     */
	public static final String ECTD_BATCHSET_NAME  = "ectd";
	public static final String  ECTDcollectionPID=DU_INSTITUTION_NAME+":"+ECTD_BATCHSET_NAME;
	public static final String  ECTDmodelPID     =DU_INSTITUTION_NAME+":ectdCModel"; // NOTE: this is not the model used at the ADR 

	/** There are contained in the <batch><ingestControl> element of a batch command file. **/
	public static final String BATCH_ADD_COMMAND    = "a";
	public static final String BATCH_UPDATE_COMMAND = "u";

	public static final String BATCH_COMMAND_BATCH_UPDATE_TYPE_ALL  = "all";
	public static final String BATCH_COMMAND_BATCH_UPDATE_TYPE_META = "meta";
	public static final String BATCH_COMMAND_BATCH_UPDATE_TYPE_PCO  = "pco";
	
	public static final String BATCH_COMMAND_BATCH_ADD_TYPE_NORMAL       = "normal";
	public static final String BATCH_COMMAND_BATCH_ADD_TYPE_ReplyWithPid = "replywithpid";
	public static final String BATCH_COMMAND_BATCH_ADD_TYPE_PidInOBJID   = "pidInOBJID";

	// put in a pid report to mark that the pid was updated versus added
	public static final String PID_REPORT_UPDATED_MARKER       = "Updated";
	public static final String PID_REPORT_UPDATE_FAILED_MARKER = "Update failed";

		// used as flag between BatchIngestFormController and batchIngest.jsp
	public static final String BATCH_SET_NAME_ATTRIBUTE = "VALID_BATCH_SET";
	
		// used to call web.gwt.public.batchIngestStatus.jsp from the viewRunningIngests page.
	public static final String BATCH_SET_NAME_REQUEST_PARAM = "batchSetName";

	public static final String PID_REPORT_COLUMN_TITLES = "PID, OBJID, dmdSec ID, Ingest File";
	public static final String PID_REPORT_HEADER = PID_REPORT_COLUMN_TITLES + "\n";

	public static final String DUPLICATE_FILE_SUFFIX = "_DUPLICATE";
	
	private static MyServletContextListener_INF myServletContextListener = null;
	
	public static void setContextListener(
			MyServletContextListener_INF aServletContextListener) 
	{
		myServletContextListener = aServletContextListener;
	}
	
	public static MyServletContextListener_INF getServletContextListener()
	{
		return myServletContextListener;
	}
	
	
} // FedoraAppConstants
