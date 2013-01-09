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

import java.io.File;
import java.util.*;

import edu.du.penrose.systems.exceptions.PropertyStorageException;
import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraApp.ProgramFileProperties;
import edu.du.penrose.systems.fedoraApp.ProgramProperties;
import edu.du.penrose.systems.fedoraApp.util.FedoraAppUtil;
import edu.du.penrose.systems.fedoraApp.util.MetsBatchFileSplitter.BatchType;
import edu.du.penrose.systems.kepi.util.DateParser;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.ExtRelList;

/**
 * Contains user options that control the batch ingest. These options are usually obtained from a web form along with options 
 * read from the batch xml file.
 * 
 * @author chet.rebman
 *
 */
public class BatchIngestOptions {

	/** IngestCommand is set in a batchFiles <ingestControl> element 
	 * <br><br>
	 * <ingestControl command="A" type="normal | replyWithPid" /> <br>
	 *    For an add... <br>
	 * type="NORMAL" (same as version one batchIngest without a command line), pid's are assigned when one at a time just prior to foxml ingest.<br>
	 * type="replyWithPid" Causes a respond immediately with the pids that will for all files contained in the batchIngest.xmlbatch file.
	 * 
	 * The pid's are reserved and saved, in comments, within the split mets files and will be assigned at a later time, when the ingest actually happens. 
	 * (this is used when performing a fedoraProxy ingest via a form post)<br><br>
	 * 
	 * NOTE: the batchIngest.xml file may have any name, it just needs to be valid batch file with an .xml extension and reside 
	 *  in the work directory..
	 * <br><br>
	 * <ingestControl command="U" type="all | meta | pco" /><br>
	 *     For an update... 
	 * type="ALL" means update meta and pco's.<br>
	 * type="Meta" is the meta data for  1 or more data-steams with the <mets> elements. <br>
	 * type="PCO" means just attached objects (Primary contect objects).
	 * */

	// batch.jsp submit buttons see BatchIngestFormController
	static public final String INGEST_SUBMIT      = "ingestEm";
	static public final String VIEW_STATUS_SUBMIT = "viewStatus";
	
	// used to identify which dropdown changed in for batchIngest.jsp 
	// NOTE:!!! THESE ARE HARD CODED INTO batchIngest.jsp!!!
	static public final String INSTITUTION_SUBMIT_ID   = "institution";
	static public final String BATCH_SET_SUBMIT_ID     = "batchSet";
	static public final String COLLECTION_SUBMIT_ID    = "fedoraCollection";
	static public final String CONTENT_MODEL_SUBMIT_ID = "contentModel";
	static public final String WORK_FILE_SUBMIT_ID     = "workFile";
	static public final String SPLIT_CB_SUBMIT_ID      = "splitCheckBox";
	static public final String ALLOW_SIMULTANIOUS_MANUAL_AND_REMOTE_INGEST_CB  = "manualAndRemoteCheckbox";
	
	static public enum FORM_ACTION { INITIAL, SAVE_SETTINGS, INGEST_SUBMIT, VIEW_ALL_SUBMIT,  WORK_FILE_CHANGE, SPLIT_CHECKBOX_CHANGE, MANUALREMOTE_CHECKBOX_CHANGE, INSTITUTION_CHANGE, BATCH_SET_CHANGE, COLLECTION_CHANGE, CONTENT_MODEL_CHANGE, ISLANDORA_CHECKBOX_CHANGE }
	
	/**
	 * MANUAL is the default and means the thread is started from the web gui 
	 * <br><br>
	 * REMOTE means the thread is started by the background timer and the ingest files were deposited by fedoraProxy after a remote form post.
	 * the properties file and the ingest(xml) files all use FedoraAppConstants#REMOTE_TASK_NAME_SUFFIX 
	 * <br><br>
	 * BACKGROUND means the thread is started by the background time and the ingest files were depsotid by a means other then fedoraProxy (ie etd depositied by proquest).
	 * The properties files and the ingest(xml) files all use FedoraAppConstants#REMOTE_TASK_NAME_SUFFIX <br><br>
	 * 
	 * NOTE: All ingest, including manual, run in their own thread.
	 */
	static public enum INGEST_THREAD_TYPE{ MANUAL, REMOTE, BACKGROUND }
	
	static public enum INGEST_COMMAND{ UPDATE, ADD, NOT_SET }
	static public enum ADD_COMMAND_TYPE{ NORMAL, REPLY_WITH_PID, PID_IN_OBJID }
	static public enum UPDATE_COMMAND_TYPE{ ALL, META, PCO, NOT_SET }
	
	
	/** IngestCommand is set in a batchFile's <ingestControl> element */
	private INGEST_COMMAND          ingestCommand = INGEST_COMMAND.NOT_SET;
	private ADD_COMMAND_TYPE       addCommandType = ADD_COMMAND_TYPE.NORMAL;
	private UPDATE_COMMAND_TYPE updateCommandType = UPDATE_COMMAND_TYPE.NOT_SET;

	private ExtRelList myExtRelList = null;
	
    /** variables for form options and their default values. */
	private String  submitId = ""; // this will be used to identify which element fired the submit.
	private boolean isStopOnError = false;
	private boolean clearLogFile  = false;
	private boolean clearFailedFiles = false;
	private boolean clearCompletedFiles = false;
	private boolean moveIngestedPCOsToCompleted = true;
	private boolean validatePCOchecksums = false;

	private boolean splitXMLinWorkDirToMets = false;
	private boolean allowSimultaneousManualAndRemoteIngest = false;

	private String  splitXMLfileName = "multipleMets.xml";
	
	private boolean setObjectInactive = false;  // set object inactive after ingest to  allow for work-flow/review before publishing
	
	/**
	 * Collection map contains the possible collections for ingesting objects into.<br>
	 * This map is initialized by the form controller BatchIngestFormController.
	 */
	private Map<String, String> fedoraCollectionMap = null; 
	
	/**
	 * Collection map contains the possible contentModels for ingesting objects into.<br>
	 * This map is initialized by the form controller BatchIngestFormController.
	 */
	private Map<String, String> fedoraContentModelMap = null; 
	
	/** institutionMap contains a list of directories underneath the batch_set directory ie codu for colorado denver university along
	 *  with their form label. This map is initialized by the form controller BatchIngestFormController **/
	private Map<String, String> institutionMap = null;
	
	/** bachSetMap contains a list of directories containing batch sets ie ath for athletics frid for frydland. along
	 *  with their form label. **/
	private Map<String, String> batchSetMap = null;
	
	/** 
	 * workFileMap contains the files contained in the work directory, these files contain multiple <mets> elements surrounded by 
	 * a <batch> element  along with their form label.
	 **/
	private Map<String, String> workFileMap = null;
	
	/** these are the form selections 
	 * <br>
	 *  Model and collection pids are used in Fedora Rel-ext (for islandora). They are not used for fedora ingest.
	 * */
	private String institution = "";
	private String batchSet    = "";
	private String workFile    = "";
	private String fedoraCollection = ""; 
	private String fedoraContentModel = "";

	/**
	 * Model and collection pids are used in Fedora Rel-ext (islandora). They are blank for fedora ingest.
	 */
	
	
	/*
	 * If true the entire batch should contain updates to the repository 
	 */
	private boolean batchIsUpdates = false; // replaced with batchType for batch version 2

	/** batchType, batchDescroption, batchIngestDate are set by the batch xml file and not by the user */
	private BatchType batchType = BatchType.NEW;
	private String batchDescription = FedoraAppConstants.DEFAULT_BATCH_DESCRIPTION;
	private Date batchIngestDate = null;
		
	/**
	 * If true, an error is thrown if the you try to update an existing record
	 * or create a new record when the record already exists.
	 */
	private boolean strictUpdates  = false;
	
	private INGEST_THREAD_TYPE ingestThreadType = INGEST_THREAD_TYPE.MANUAL;
	
	/**
	 * In order for a batchSet to be enabled for remote ingest tasks, it must have a remote batchSet_REMOTE.properties file and be
	 * enabled in the taskEnable.properties file. 
	 */
	private boolean hasRemotePropertiesFile = false;
	

	/**
	 * This is for background tasks that run similar to a cron job ie the etd task. It is very similar to a remote ingest task.
	 * 
	 */
	private boolean hasTaskPropertiesFile = false;
	
	/**
	 * ingestType is set in the batchIngest.properites file
	 */
//	private INGEST_TYPE ingestType = INGEST_TYPE.ISLANDORA;

	private String successEmail   = null;
	private String failureEmail   = null;
	private String successEmail_2 = null;
	private String failureEmail_2 = null;
	private String stmpHost = null;
	private String stmpPort = null;
	private String stmpUser = null;
	private String stmpPassword = null;
	private boolean stmpUseSSL = true;
	private String emailFromAddress = null;
	
	
	public BatchIngestOptions() {

        Map<String, String> institutionList     = new HashMap<String, String>();
        
        batchSetMap         = new HashMap<String, String>();
        fedoraCollectionMap = new HashMap<String, String>();
        fedoraContentModelMap = new HashMap<String, String>();    
        
	    String default_institution = FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE;
	    String default_batch_set   = FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE;
	    String default_collection  = FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE;
	    String default_contentModel = FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE;
	    
	    institutionList.put( default_institution,default_institution );
	    batchSetMap.put( default_batch_set, default_batch_set );
	    fedoraCollectionMap.put( default_collection, default_collection );
	    fedoraContentModelMap.put( default_contentModel, default_contentModel );
	    
	    this.setInstitutionMap( institutionList );
        this.setInstitution( default_institution );
        this.setFedoraCollection( default_collection );
	    
	    this.setBatchSetMap(  batchSetMap );
	    this.setBatchSet( default_batch_set  );
        this.setFedoraCollectionMap( fedoraCollectionMap );
        this.setFedoraContentModelMap( fedoraContentModelMap );
	    
        Calendar gCalendar = new GregorianCalendar();
        gCalendar.set( Calendar.HOUR_OF_DAY, 0 ); // we want to search by just the date and year not the time. Even though the solr field will contain both.
        gCalendar.set( Calendar.MINUTE, 0 );
        gCalendar.set( Calendar.SECOND, 0 );
        gCalendar.set( Calendar.MILLISECOND, 0 );
        
        this.setBatchIngestDate( gCalendar.getTime() );
	}
	

	/**
	 * Return a name containing the institution and batchSet and possibly _REMOTE if it was started by
	 * a remote task, ie institution_batchSet or institution_batchSet_REMOTE.'
	 * If this is a manual task, and the batchSet is enabled for remote ingests, check to see if manual and remote
	 * ingest's are allowed to run simultaneously, if not append the REMOTE_TASK_NAME_SUFFIX, this will allow the
	 * user to view running remote tasks.
	 * 
	 * @return the batchSet name
	 */
	public String getBatchSetName()
	{
		String aBatchSetName = null;
		
		aBatchSetName = this.getInstitution() + "_" + this.getBatchSet();
		
		switch ( this.getIngestThreadType() )
		{
		case MANUAL:	
			break;
		case BACKGROUND:
			aBatchSetName = aBatchSetName + FedoraAppConstants.BACKGROUND_TASK_NAME_SUFFIX;
			break;
		case REMOTE:
			aBatchSetName = aBatchSetName + FedoraAppConstants.REMOTE_TASK_NAME_SUFFIX;
			break;
		}
		
		return aBatchSetName;
	}
	
	
	/**
	 * Get the batch description (usually set by the batchDescription element in the batch xml file).
	 * 
	 * @return the batch description
	 */
	public String getBatchDescription() {
		return batchDescription;
	}
	/**
	 * Set the batch description (usually set by the batchDescription element in the batch xml file).
	 * 
	 * @param batchDescription the batch description
	 */
	public void setBatchDescription(String batchDescription) {
		this.batchDescription = batchDescription;
	}

	/**
	 * Get the ingest date  (used by what's new page) it defaults to the current date.
	 * <br>
	 * This method was put here to make it easily accessible, .
	 * 
	 * @return the ingest date
	 */
	public Date getBatchIngestDate() {
		return batchIngestDate;
	}

	/**
	 * Set the batch ingest (usually obtained from the batchCreationDate in a batch file).<br>
	 * This date is used by the used by what's new page.
	 * 
	 * @param batchIngestDate
	 */
	public void setBatchIngestDate(Date batchIngestDate) 
	{
		this.batchIngestDate = batchIngestDate;
	}	
	
	/**
	 * Set the ingest date for this data, will try to parse date using default date patterns.
	 * 
	 * @param dateString
	 * @throws Exception if unable to parse date.
	 * @see DateParser#DEFAULT_DATE_PATTERNS
	 */
	public void setBatchIngestDate( String dateString ) throws Exception{
		
		Date ingestDate = DateParser.getDateApplyDefaultPatterns( dateString );	
		
		this.setBatchIngestDate( ingestDate );
	}
	
	/**
	 * If true, the entire batch contains updates, if false the entire batch contains
	 * new records.
	 * 
	 * @return true if the batch is updates, false if the batch is adds
	 */
	public boolean isBatchIsUpdates() {
		return this.batchIsUpdates;
	}

	/**
	 * If true, the entire batch contains updates, if false the entire batch contains
	 * new records. 
	 * 
	 * @param batchIsUpdates 
	 */
	public void setBatchIsUpdates(boolean batchIsUpdates) {
		this.batchIsUpdates = batchIsUpdates;
	}

	/**
	 * 
	 * @return NEW, UPDATES or MIXED
	 */
	public BatchType getBatchType() {
		return batchType;
	}

	/**
	 * Set if batch is all new, all updates or Mixed.
	 * 
	 * @param batchType
	 */
	public void setBatchType(BatchType batchType) {
		this.batchType = batchType;
	}

	/**
	 * If true, an error is thrown if the you try to update an existing record
	 * or create a new record when the record already exists.
	 * 
	 * @return true to force an error when updating non existing object or adding an object that already exists.
	 */
	public boolean isStrictUpdates() {
		return strictUpdates;
	}

	/**
	 * If true, an error is thrown if the you try to update an existing record
	 * or create a new record when the record already exists.
	 *
	 * @param strictUpdates
	 */
	public void setStrictUpdates(boolean strictUpdates) {
		this.strictUpdates = strictUpdates;
	}
	
	/**
	 * If true the batch ingest is stopped on the first error. Normally this is
     *
	 * set false and Non Fatal errors  are simply logged.
	 * 
	 * @return true to stop on error.
	 */
	public boolean isStopOnError() {
		return isStopOnError;
	}

	public void setStopOnError(boolean isStopOnError) {
		this.isStopOnError = isStopOnError;
	}

    /**
     * Set to true to clear the log file pror to the start of the batch 
     * ingest.
     * @return the clearLogFile
     */
    public boolean isClearLogFile() {
        return clearLogFile;
    }

    /**
     * Set to true to clear the log file prior to the start of the batch 
     * ingest.
     * @param clearLogFile the clearLogFile to set
     */
    public void setClearLogFile(boolean clearLogFile) {
        this.clearLogFile = clearLogFile;
    }

    /**
     * Set to true to clear the previous failed xml files prior to the start of 
     * the batch ingest.
     * 
     * @return the clearPreviousFailedFiles
     */
    public boolean isClearFailedFiles() {
        return clearFailedFiles;
    }


    /**
     * Set to true to clear the previous failed xml files prior to the start of 
     * the batch ingest.
     * 
     * @param clearFailedFiles the clearPreviousFailedFiles to set
     */
    public void setClearFailedFiles(boolean clearFailedFiles) {
        this.clearFailedFiles = clearFailedFiles;
    }

    /**
     * Get a map containing all the known institutions.
     * 
     * @return the institutions
     */
    public Map getInstitutionMap() {
        return institutionMap;
    }

    /**
     * Set a map containing all the known institutions.
     * 
     * @param institutions the institutions to set
     */
    public void setInstitutionMap( Map<String, String> institutions) {
        this.institutionMap = institutions;
    }

    /**
     * Get a map containing all the known collections.
     * 
     * @return the fedoraCollections
     */
    public Map getFedoraCollectionMap() {
        return fedoraCollectionMap;
    }
    
    /**
     * Get a map containing all the known content models.
     * 
     * @return the fedoraCollections
     */
    public Map getFedoraContentModelMap() {
        return fedoraContentModelMap;
    }

    /**
     * Set a map containing all the known collections.
     * 
     * @param collectionMap the collectionMap to set, if null an empty map is created.
     */
    public void setFedoraCollectionMap( Map<String, String> collectionMap ) 
    {
    	if ( collectionMap == null ){
    		collectionMap = new HashMap<String,String>();
    	}
        this.fedoraCollectionMap = collectionMap;
    }
    
    /**
     * Set a map containing all the known content models.
     * 
     * @param contentModelMap the contentModelMap to set
     */
    public void setFedoraContentModelMap( Map<String, String> contentModelMap ) 
    {
    	if ( contentModelMap == null )
    	{
    		contentModelMap = new HashMap<String,String>();
    	}
        this.fedoraContentModelMap = contentModelMap;
    }

    /**
     * Get the current selected institution.
     * 
     * @return the institution
     */
    public String getInstitution() {
    	
        return institution;
    }

    /**
     * Set the current selected institution.
     * 
     * @param institution the institution to set
     */
    public void setInstitution(String institution) {

    	if ( institution == null )
    	{ 
    		this.hasRemotePropertiesFile = false;
    		this.hasTaskPropertiesFile   = false;
    		return; 
    	}
    	
        this.institution = institution;
    }
    
    /**
     * Get the current selected fedora(Islandora) Collection object. This will be "" if it hasn't been set.
     * 
     * @see #setIslandoraCollectionPID(String)
     * @return the fedoraCollection fedoraCollection or "".
     */
    public String getFedoraCollection() {
    	if ( fedoraCollection == null ){ fedoraCollection=""; }
        return fedoraCollection;
    }

    /**
     * Set the fedora(islandora) collection.
     * 
     * @param collection the collection to set
     */
    public void setFedoraCollection(String collection) {

        this.fedoraCollection = collection;
    }

    /**
     * Get a  map of know batch sets.
     * 
     * @return the batchSetMap
     */
    public Map getBatchSetMap() {
        return batchSetMap;
    }

    /**
     * Set a  map of know batch sets.
     * 
     * @param batchSetMap the batchSetMap to set
     */
    public void setBatchSetMap(Map batchSetMap) {
        this.batchSetMap = batchSetMap;
    }

    /**
     * Get current selected batch set.
     * 
     * @return the batchSet
     */
    public String getBatchSet() {
        return batchSet;
    }

    /**
     * Set the batchSet selected in the GUI, this function will also check to see if the batch set has a remote (for remote ingesting)
     * properties file, it may take some time if there are a lot of collection so we only want to do it once.
     * 
     * @param batchSet the batchSet to set,  
     */
    public void setBatchSet(String batchSet) {
    	
    	if ( batchSet.equals( this.getBatchSet() ) ){
    		return;
    	}
    	
    	if ( batchSet == null || batchSet.contentEquals(FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE) )
    	{ 
    		this.hasRemotePropertiesFile = false;
    		this.hasTaskPropertiesFile   = false;
        	this.batchSet = batchSet;
    		return; 
    	}
    	
    	this.batchSet = batchSet;
        
        String aBatchSetName = this.getInstitution() + "_" + this.getBatchSet();
        
        // isBatchSetRemoteEnabled may be slow, so we only want to do it once.
        if ( FedoraAppUtil.isHasRemoteIngestPropertiesFile( aBatchSetName + FedoraAppConstants.REMOTE_TASK_NAME_SUFFIX ) )
        {
        	this.hasRemotePropertiesFile = true;
        }
        else {
        	this.hasRemotePropertiesFile = false;
        }
        

        if ( FedoraAppUtil.isHasTaskIngestPropertiesFile( aBatchSetName + FedoraAppConstants.BACKGROUND_TASK_NAME_SUFFIX ) )
        {
        	this.hasTaskPropertiesFile = true;
        }
        else {
        	this.hasTaskPropertiesFile = false;
        }
    }

    public boolean isHaveRemotePropertiesFile()
    {
    	return this.hasRemotePropertiesFile;
    }
    
    public boolean isHaveTaskPropertiesFile()
    {
    	return this.hasTaskPropertiesFile;
    }
    
    /**
     * Check if the batchSet has a remote properties file and the remote task is  enabled in the taskEnabled.properties file..
     * 
     * @return true if this batch set is enable for remote ingest.
     */
    public boolean isRemoteEnabled()
    {
    	boolean result = false;
    	
    	ProgramProperties taskEnableProperties = new ProgramFileProperties( new File( FedoraAppConstants.getServletContextListener().getTaskEnablePropertiesURL().getFile() ) );
    
    	try {
			if ( taskEnableProperties.getProperty( FedoraAppConstants.TASK_ENABLE_PROPERTY, false ) )
			{
				if ( this.isHaveRemotePropertiesFile() && taskEnableProperties.getProperty( this.getInstitution() + "_" + this.getBatchSet(), false ) )
		    	{
		    		result = true;
		    	}
			}
		} catch (PropertyStorageException e) 
		{		
			// TBD should never happen should log an error or sumpin
			result = false;
		}
    	  	
    	return result;
    }
    
    
    /**
     * Check if the batchSet has a task properties file and the remote task is  enabled in the taskEnabled.properties file..
     * 
     * @return true if this batch set is enable for remote ingest.
     */
    public boolean isBackgroundTaskEnabled()
    {
    	boolean result = false;
    	
    	ProgramProperties taskEnableProperties = new ProgramFileProperties( new File( FedoraAppConstants.getServletContextListener().getTaskEnablePropertiesURL().getFile() ) );
    
    	try {
			if ( taskEnableProperties.getProperty( FedoraAppConstants.TASK_ENABLE_PROPERTY, false ) )
			{
				if ( this.isHaveTaskPropertiesFile() && taskEnableProperties.getProperty( this.getInstitution() + "_" + this.getBatchSet(), false ) )
		    	{
		    		result = true;
		    	}
			}
		} catch (PropertyStorageException e) 
		{		
			// TBD should never happen should log an error or sumpin
			result = false;
		}
    	  	
    	return result;
    }
    
    /**
     * This routine DOES NOTHING it is here simply to satisfy Spring in batchIngest.jsp which uses isRemoteEnabled(), it checks for a matching 
     * setter.
     * 
     * @param dummy
     */
    public void setIsRemoteEnabled( boolean dummy )
    {
    	
    }
    
    /**
     * Set the current selected batch set.
     * 
     * @return the moveIngestedPCOsToCompleted
     */
    public boolean isMoveIngestedPCOsToCompleted() {
        return moveIngestedPCOsToCompleted;
    }

    /**
     * If true the PCO (Primary Content Objects) are moved to the completed
     * folder after they are ingested into fedora.
     * 
     * @param moveIngestedPCOsToCompleted the moveIngestedPCOsToCompleted to set
     */
    public void setMoveIngestedPCOsToCompleted(boolean moveIngestedPCOsToCompleted) {
        this.moveIngestedPCOsToCompleted = moveIngestedPCOsToCompleted;
    }

    /**
     * True to split the specified XML file into seperate METS files which are 
     * written to the METS directory
     * 
     * @see #getSplitXMLfileName()
     * @return the splitXMLinWorkDirToMets
     */
    public boolean isSplitXMLinWorkDirToMets() {
        return splitXMLinWorkDirToMets;
    }

    /**
     * True to split the specified XML file into separate METS files which are 
     * written to the METS directory
     * 
     * @see #splitXMLfileName
     * @param splitXMLinWorkDirToMets the splitXMLinWorkDirToMets to set
     */
    public void setSplitXMLinWorkDirToMets(boolean splitXMLinWorkDirToMets) {
        this.splitXMLinWorkDirToMets = splitXMLinWorkDirToMets;
    }

    
    public void setAllowSimultaneousManualAndRemoteIngest(boolean simultaneousManualAndRemote ) {
        this.allowSimultaneousManualAndRemoteIngest = simultaneousManualAndRemote;
    }
    
    public boolean isAllowSimultaneousManualAndRemoteIngest() {
        return allowSimultaneousManualAndRemoteIngest;
    }

    /**
     * Set the XML file to be split.
     * 
     * @deprecated using workFile now (supports dropdown list using workFileMap).
     * @see #splitXMLinWorkDirToMets
     * @param splitXMLfileName the splitXMLfileName to set
     */
    public void setSplitXMLfileName(String splitXMLfileName) {
        this.splitXMLfileName = splitXMLfileName;
    }

    
    /**
     * Get the XML file to be split.
     * 
     * @see #splitXMLinWorkDirToMets
     * @return the splitXMLfileName
     */
    public String getSplitXMLfileName() {
        return splitXMLfileName;
    }
    
    /**
     * Helper method for creating the right size form input box.<br>
     * 
     * @return size of the XML file name.
     */
    public int getSplitXMLfileNameLength() {
        
        return this.getSplitXMLfileName().length();
    }

    /**
     * True to remove all prior completed files from the completed folder.
     * 
     * @return the clearCompletedFiles
     */
    public boolean isClearCompletedFiles() {
        return clearCompletedFiles;
    }

    /**
     * True to remove all prior completed files from the completed folder.
     * 
     * @param clearCompletedFiles the clearCompletedFiles to set
     */
    public void setClearCompletedFiles(boolean clearCompletedFiles) {
        this.clearCompletedFiles = clearCompletedFiles;
    }

    /**
     * @return the validatePCOchecksums
     */
    public boolean isValidatePCOchecksums() {
        return validatePCOchecksums;
    }

    /**
     * @param validatePCOchecksums the validatePCOchecksums to set
     */
    public void setValidatePCOchecksums(boolean validatePCOchecksums) {
        this.validatePCOchecksums = validatePCOchecksums;
    }

	public Map<String, String> getWorkFileMap() {
		return this.workFileMap;
	}

	/**
	 * NOTE FedoraAppConstants.BATCH_INGEST_FOXML_FILE_NAME will be REMOVED!
	 * 
	 * @see FedoraAppConstants#BATCH_INGEST_FOXML_FILE_NAME
	 * @param workFileMap
	 */
	public void setWorkFileMap(Map<String, String> workFileMap) {
		this.workFileMap = workFileMap;
		this.workFileMap.remove( FedoraAppConstants.BATCH_INGEST_FOXML_FILE_NAME );
	}

	public String getWorkFile() {
		return workFile;
	}

	public void setWorkFile(String workFile) {
		this.workFile = workFile;
		this.splitXMLfileName = workFile;
	}

	/**
	 * This will remove all rel-ext relationships
	 */
	public void clearRelExtList()
	{
		myExtRelList = new ExtRelList();
	}
	
	public ExtRelList getExtRelList() {
		
		if ( myExtRelList == null )
		{
			myExtRelList = new ExtRelList();		
		}
		
		return myExtRelList;
	}
	
	/**
	 * @see ExtRelList#isHasRelationshipEntity(String)
	 * @param relationshipEntity
	 * @return true if we have a relationship entity to send to fedora for this object
	 */
	public boolean isHasRelationshipEntity( String relationshipEntity )
	{
		return this.getExtRelList().isHasRelationshipEntity( relationshipEntity );
	}
	
	/**
	 * Check to see if there is rels-ext relationship defined.
	 * 
	 * @see #setIslandoraCollectionPID(String)
	 * @see #setIslandoraContentModelPID(String)
	 * @see #addIslandoraRelationship(String, String)
	 * 
	 * @return true if we have a rels-ext to send to fedora for this object
	 */
	public boolean isHasExtRelsEntry()
	{
		return ! this.getExtRelList().isEmpty();
	}


	/**
	 * @param modelPID
	 */
	public void setIslandoraContentModelPID(String modelPID) 
	{		
		if ( modelPID == null || modelPID.length() == 0 ){
			return;
		}

		this.fedoraContentModel = modelPID;
		
	}

	

	/**
     * 
	 * @param collectionPID
	 */
	public void setIslandoraCollectionPID(String collectionPID) 
	{	
		if ( collectionPID == null || collectionPID.length() == 0 ){
			return;
		}
		
		this.fedoraCollection = collectionPID;
		
	}
	
	/**
	 * Add the relationship and set the fedoraCollection and the fedoraContentModel
	 * 
	 * @see #setIslandoraCollectionPID(String)
	 * @see #setIslandoraContentModelPID(String)
	 * 
	 * @param collectionPID
	 * @param contentModelPID
	 */
	public void addIslandoraRelationship(String collectionPID, String contentModelPID ) 
	{
		this.getExtRelList().addIslandoraRelationship( collectionPID, contentModelPID );
		
		this.setIslandoraCollectionPID(     collectionPID );
        this.setIslandoraContentModelPID( contentModelPID );
	}


	public INGEST_COMMAND getIngestCommand() {
		return ingestCommand;
	}

	public void setIngestCommand(INGEST_COMMAND ingestCommand) {
		this.ingestCommand = ingestCommand;
	}

	public UPDATE_COMMAND_TYPE getUpdateCommandType() {
		return updateCommandType;
	}

	public void setUpdateCommandType(UPDATE_COMMAND_TYPE updateCommandType) {
		this.updateCommandType = updateCommandType;
	}

	public ADD_COMMAND_TYPE getAddCommandType() {
		return addCommandType;
	}

	public void setAddCommandType(ADD_COMMAND_TYPE addCommandType) {
		this.addCommandType = addCommandType;
	}

	
	/**
	 * Get the fedora(Islandora) content model or "" if not set.
	 * 
	 * @return fedora content model or ""
	 */
	public String getFedoraContentModel() {
		if ( fedoraContentModel == null ){ fedoraContentModel=""; }
		return fedoraContentModel;
	}

    /**
     * Set the fedora(Islandora) content model
     * @param fedoraContentModel
     */
	public void setFedoraContentModel(String fedoraContentModel) {
		this.fedoraContentModel = fedoraContentModel;
	}


	public String getSubmitId() {
		return submitId;
	}


	public void setSubmitId(String submitId) {
		this.submitId = submitId;
	}


	public boolean isSetObjectInactive() {
		return setObjectInactive;
	}


	public void setSetObjectInactive(boolean setObjectInactive) {
		this.setObjectInactive = setObjectInactive;
	}


	public String getSuccessEmail() {
		return successEmail;
	}


	public void setSuccessEmail(String successEmail) {
		this.successEmail = successEmail;
	}


	public String getFailureEmail() {
		return failureEmail;
	}


	public void setFailureEmail(String failureEmail) {
		this.failureEmail = failureEmail;
	}


	public String getSuccessEmail_2() {
		return successEmail_2;
	}


	public void setSuccessEmail_2(String successEmail_2) {
		this.successEmail_2 = successEmail_2;
	}


	public String getFailureEmail_2() {
		return failureEmail_2;
	}


	public void setFailureEmail_2(String failureEmail_2) {
		this.failureEmail_2 = failureEmail_2;
	}


	public String getStmpHost() {
		return stmpHost;
	}


	public void setStmpHost(String stmpHost) {
		this.stmpHost = stmpHost;
	}


	public String getStmpPort() {
		return stmpPort;
	}


	public void setStmpPort(String stmpPort) {
		this.stmpPort = stmpPort;
	}


	public String getStmpUser() {
		return stmpUser;
	}


	public void setStmpUser(String stmpUser) {
		this.stmpUser = stmpUser;
	}


	public String getStmpPassword() {
		return stmpPassword;
	}


	public void setStmpPassword(String stmpPassword) {
		this.stmpPassword = stmpPassword;
	}


	public boolean isStmpUseSSL() {
		return stmpUseSSL;
	}


	public void setStmpUseSSL(boolean stmpUseSSL) {
		this.stmpUseSSL = stmpUseSSL;
	}


	public String getEmailFromAddress() {
		return emailFromAddress;
	}


	public void setEmailFromAddress(String emailFromAddress) {
		this.emailFromAddress = emailFromAddress;
	}
	
	
	/**
	 * MANUAL is the default and means the thread is started from the web gui 
	 * <br><br>
	 * REMOTE means the thread is started by the background timer and the ingest files were deposited by fedoraProxy after a remote form post.
	 * the properties file and the ingest(xml) files all use FedoraAppConstants#REMOTE_TASK_NAME_SUFFIX 
	 * <br><br>
	 * BACKGROUND means the thread is started by the background time and the ingest files were depsotid by a means other then fedoraProxy (ie etd depositied by proquest).
	 * The properties files and the ingest(xml) files all use FedoraAppConstants#REMOTE_TASK_NAME_SUFFIX <br><br>
	 * 
	 * NOTE: All ingest, including manual, run in their own thread.
	 * @param threadType INGEST_THREAD_TYPE
	 * @return this
	 */
	public BatchIngestOptions setIngestThreadType( INGEST_THREAD_TYPE threadType )
	{
		this.ingestThreadType = threadType;	
		
		return this;
	}

	/**
	 * MANUAL is the default and means the thread is started from the web gui 
	 * <br><br>
	 * REMOTE means the thread is started by the background timer and the ingest files were deposited by fedoraProxy after a remote form post.
	 * the properties file and the ingest(xml) files all use FedoraAppConstants#REMOTE_TASK_NAME_SUFFIX 
	 * <br><br>
	 * BACKGROUND means the thread is started by the background time and the ingest files were depsotid by a means other then fedoraProxy (ie etd depositied by proquest).
	 * The properties files and the ingest(xml) files all use FedoraAppConstants#REMOTE_TASK_NAME_SUFFIX <br><br>
	 * 
	 * NOTE: All ingest, including manual, run in their own thread.
	 */
	public INGEST_THREAD_TYPE getIngestThreadType() 
	{
		return this.ingestThreadType;
	}


	/**
	 * Sets the following batchOptions...<br>
	 * <br>
	 * batchOptions.setBatchDescription( "generic ingest" ); // set from batch file? <br>
	 * batchOptions.setInstitution(  FedoraAppConstants.GENERIC_INSTITUTION_NAME ); <br>
	 * batchOptions.setBatchSet( FedoraAppConstants.GENERIC_BATCHSET_NAME ); <br>
	 * batchOptions.setClearCompletedFiles( false ); <br>
	 * batchOptions.setClearFailedFiles( false ); <br>
	 * batchOptions.setClearLogFile( false ); <br>
	 * batchOptions.setSplitXMLinWorkDirToMets( true ); <br>
	 * batchOptions.setStopOnError(false) <br>
	 * batchOptions.setStrictUpdates(true) <br>
	 * batchOptions.setValidatePCOchecksums(false) <br>
	 * batchOPtions.setIngestThreadType( INGEST_THREAD_TYPE.REMOTE ) // this will use the taskTemp folder and take two task runs to complete an ingest. <br>
	 * Set the ExtRel list for fedora VERY IMPORTANT so that it gets ingested into correct islandora collection.  	  <br>
	 * 
	 * @return the batchOptions object
	 */
	static public BatchIngestOptions getGenericBatchOptions()
	{
		BatchIngestOptions batchOptions = new BatchIngestOptions();
		
		batchOptions.setBatchDescription( "generic ingest" ); // set from batch file?
		//batchOptions.setBatchIngestDate( xxxx ); // what's new not used only by facts.
		// batchOptions.setBatchIsUpdates( false ); // depreciated.
		batchOptions.setBatchSet( FedoraAppConstants.GENERIC_BATCHSET_NAME );
		// batchOptions.setBatchType(batchType); // set from batch file.
		batchOptions.setClearCompletedFiles( false );
		batchOptions.setClearFailedFiles( false );
		batchOptions.setClearLogFile( false );  // TBD When do these get removed?
		batchOptions.setInstitution( FedoraAppConstants.GENERIC_INSTITUTION_NAME );
		// batchOptions.setSplitXMLfileName(); // set from disk read
		
		batchOptions.setIngestThreadType( INGEST_THREAD_TYPE.REMOTE );
		
		// set to true even though work file may not exist if the batch ingest is an add of type replayWithPid
		batchOptions.setSplitXMLinWorkDirToMets( true );
		
	
		batchOptions.setStopOnError( false );
		batchOptions.setStrictUpdates( true );
		batchOptions.setValidatePCOchecksums( false );	
		
		// Set up the fedora external object relationships. This will be overwritten if set in the batch ingest file.
		
		return batchOptions;
	}
	
	/**
	 * A convenience method. Get's the generic batch options with institution and batchSet set along with..<br>
	 * 
	 * batchOPtions.setIngestThreadType( INGEST_THREAD_TYPE.BACKGROUND )
	 * 
	 * @param institution
	 * @param batchSet
	 * @return
	 */
	static public BatchIngestOptions getGenericBackgroundBatchOptions( String institution, String batchSet )
	{
		BatchIngestOptions tempOptions = BatchIngestOptions.getGenericBatchOptions();
		tempOptions.setInstitution( institution );
		tempOptions.setBatchSet( batchSet );
		tempOptions.setIngestThreadType( INGEST_THREAD_TYPE.BACKGROUND );
		
		return tempOptions;
	}
	
} // BatchIngestOptions
