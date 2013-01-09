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

import java.util.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
 
import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraApp.ProgramProperties;
import edu.du.penrose.systems.util.MyServletContextListener_INF;
import edu.du.penrose.systems.exceptions.FatalException;

/**
 * Implelmentation of BatchIngestURLhandler
 * 
 * @author chet.rebman
 *
 */
public class BatchIngestURLhandlerImpl extends BatchIngestURLhandler {

    private Map<String, URL> applicationUrlMap = null;
    String batchSetPath = null;
    private String uniqueBatchRunName = null;
    private boolean xmlRecordUpdates = false; // true if adding new records.\
    
    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());
    
	BatchIngestOptions mybatchOptions = null;
	
    private MyServletContextListener_INF myServletContextListener = null;
    
    /**
     * @see BatchIngestURLhandler#getInstance(String, String, String, boolean, BatchIngestOptions)
     * 
     * @param uniqueBatchRunName
     * @param institution
     * @param batchSet
     * @param xmlRecordUpdates true when updating a record, false if new record.
     * @throws FatalException
     */
    public BatchIngestURLhandlerImpl( MyServletContextListener_INF servletContextListener, String uniqueBatchRunName, String institution, String batchSet, boolean xmlRecordUpdates,  BatchIngestOptions batchIngestOptions ) throws FatalException {

    	this.myServletContextListener = servletContextListener;
        this.uniqueBatchRunName       = uniqueBatchRunName;
        this.batchSetPath             = institution +"/"+ batchSet+"/";
        this.loadApplicationUrlMap();
        this.xmlRecordUpdates = xmlRecordUpdates;
        this.mybatchOptions  = batchIngestOptions;
    }

    /**
     * Get location of the batch ingest METS XML new folder.
     * 
     * @see #xmlRecordUpdates
     * @return location of METS files
     */
    public URL getMetsNewFolderURL()
    {       
    	return this.applicationUrlMap.get( FedoraAppConstants.BATCH_INGEST_NEW_METS_FOLDER_PROPERTY );
    }
    
    /**
     * Get location of the batch ingest METS XML new or update folder depending on the value of
     * xmlRecordUpdates, 
     * 
     * Used only for version (1) of batch updates and for processing individual files (no batch file). 
     * Version 2 uses getMetsNewFolderURL() and getMetsUpdatesFolderURL
     * 
     * @see #xmlRecordUpdates
     * @return location of METS files
     */
    public URL getMetsFolderURL(){
        
    	if ( this.xmlRecordUpdates )
    	{
    		return this.applicationUrlMap.get( FedoraAppConstants.BATCH_INGEST_UPDATES_METS_FOLDER_PROPERTY );
    	}
    	{
            return this.applicationUrlMap.get( FedoraAppConstants.BATCH_INGEST_NEW_METS_FOLDER_PROPERTY );
    	}
    	
    }
    
    /**
     * Get location of the batch ingest METS XML updates folder.
     * 
     * @see #xmlRecordUpdates
     * @return location of METS files
     */
    public URL getMetsUpdatesFolderURL()
    {
    	return this.applicationUrlMap.get( FedoraAppConstants.BATCH_INGEST_UPDATES_METS_FOLDER_PROPERTY );	
    }

    /* (non-Javadoc)
     * @see edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestURLhandler#getWorkFolder()
     */
    public URL getWorkFolderURL(){
        
        return this.applicationUrlMap.get( FedoraAppConstants.BATCH_INGEST_WORK_FOLDER_PROPERTY );
    }
    
    /* (non-Javadoc)
     * @see edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestURLhandler#getWorkFolder()
     */
    public URL getFilesFolderURL(){
        
        return this.applicationUrlMap.get( FedoraAppConstants.BATCH_INGEST_FILES_FOLDER_PROPERTY );
    }

    /* (non-Javadoc)
     * @see edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestURLhandler#getFaledFilesFolder()
     */
    public URL getFailedFilesFolderURL() {
        
        return this.applicationUrlMap.get( FedoraAppConstants.BATCH_INGEST_FAILED_FOLDER_PROPERTY );
    }
    
    /* (non-Javadoc)
     * @see edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestURLhandler#getFailedBatchFilesFolder()
     */
    public URL getFailedBatchFilesFolderURL() {
        
        return this.applicationUrlMap.get( FedoraAppConstants.BATCH_INGEST_FAILED_BATCH_FOLDER_PROPERTY );
    }

    /* (non-Javadoc)
     * @see edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestURLhandler#getCompletedFilesFolder()
     */
    public URL getCompletedFilesFolderURL(){
         
        return this.applicationUrlMap.get( FedoraAppConstants.BATCH_INGEST_COMPLETED_FOLDER_PROPERTY );
    }

    /* (non-Javadoc)
     * @see edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestURLhandler#getCompletedBatchFilesFolder()
     */
    public URL getCompletedBatchFilesFolderURL(){
         
        return this.applicationUrlMap.get( FedoraAppConstants.BATCH_INGEST_COMPLETED_BATCH_FOLDER_PROPERTY );
    }
    
    /* (non-Javadoc)
     * @see edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestURLhandler#getImagesFolderURL()
     */
    public URL getImagesFolderURL(){
         
        return this.applicationUrlMap.get( FedoraAppConstants.BATCH_INGEST_IMAGES_FOLDER_PROPERTY );
    }
    
    /* (non-Javadoc)
     * @see edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestURLhandler#getLogFilesFolder()
     */
    public URL getLogFilesFolderURL(){
        
        return this.applicationUrlMap.get( FedoraAppConstants.BATCH_INGEST_LOGS_FOLDER_PROPERTY );
    }

	/**
    * @see edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestURLhandler#getTaskTempFolderURL()
    */
	public URL getTaskTempFolderURL() {
		
        return this.applicationUrlMap.get( FedoraAppConstants.BATCH_INGEST_TASKS_TEMP_FOLDER_PROPERTY );
	}

    private void loadApplicationUrlMap() throws FatalException {
                
       this.applicationUrlMap = new HashMap<String, URL>();        
       this.checkAndAddURL( FedoraAppConstants.BATCH_INGEST_NEW_METS_FOLDER_PROPERTY );
       this.checkAndAddURL( FedoraAppConstants.BATCH_INGEST_UPDATES_METS_FOLDER_PROPERTY );
       this.checkAndAddURL( FedoraAppConstants.BATCH_INGEST_COMPLETED_FOLDER_PROPERTY );
       this.checkAndAddURL( FedoraAppConstants.BATCH_INGEST_FAILED_FOLDER_PROPERTY );
       this.checkAndAddURL( FedoraAppConstants.BATCH_INGEST_FAILED_BATCH_FOLDER_PROPERTY );
       this.checkAndAddURL( FedoraAppConstants.BATCH_INGEST_FILES_FOLDER_PROPERTY );
       this.checkAndAddURL( FedoraAppConstants.BATCH_INGEST_LOGS_FOLDER_PROPERTY );
       this.checkAndAddURL( FedoraAppConstants.BATCH_INGEST_TASKS_TEMP_FOLDER_PROPERTY );
       this.checkAndAddURL( FedoraAppConstants.BATCH_INGEST_WORK_FOLDER_PROPERTY );
       this.checkAndAddURL( FedoraAppConstants.BATCH_INGEST_COMPLETED_BATCH_FOLDER_PROPERTY );
       this.checkAndAddURL( FedoraAppConstants.BATCH_INGEST_IMAGES_FOLDER_PROPERTY );
    }
    
    
    
    /*
     * Check that the URLs contained in the application's properties file are valid and
     * add them to the valid URL map.
     */
    private void checkAndAddURL( String property ) throws FatalException {
    	
    	String topPath = ProgramProperties.getInstance( this.myServletContextListener.getProgramPropertiesURL() ).getProperty( FedoraAppConstants.BATCH_INGEST_TOP_FOLDER_URL_PROPERTY );
    	
    	if ( ! topPath.endsWith( "/" ) ){ 
    		topPath = topPath + File.separatorChar; 
    	}
        String urlPath = topPath + this.batchSetPath + ProgramProperties.getInstance( this.myServletContextListener.getProgramPropertiesURL() ).getProperty( property ) +"/";
        
        URL  url = null;
        try {
            url = new URL( urlPath );
            this.checkSupportedURLprotocol( url );
            this.checkURLlocationExists( url );
            this.applicationUrlMap.put( property, url);
        }
        catch ( Exception e ) {
            String errorMsg = "Fatal Error: Bad Batch Set property:"+property+" = "+ e.getMessage();
            this.logger.fatal( errorMsg );
            throw new FatalException( errorMsg );
        }
        
    } // checkURLproperties
    

    /**
     * Currently only works for File: protocol URLs 
     * 
     * @param properityURL
     * @throws FatalException
     */
    void checkURLlocationExists( URL properityURL ) throws FatalException {
        
        String dirName = properityURL.getFile().replace( '/', File.separatorChar );
        File   directory = new File( dirName );
        
        if ( ! directory.exists() ) {
            
            throw new FatalException( "Fatal Error: check applications properties file, directory does not exist: "+ dirName);
        }
        
    } // checkURLlocationExists
    
    private void checkSupportedURLprotocol( URL checkURL ) throws FatalException {

        String protocol = null; 
        
        protocol = checkURL.getProtocol();
        
        if ( ! protocol.toLowerCase().equals("file") ) {
            String errorMsg = "Fatal Error: Bad URL in application properties file, unsupported protocol! " + checkURL;
            this.logger.error( errorMsg );      
            throw new FatalException( errorMsg ); 
        }
        
    } // checkURLprotocol

    
    /**
     * Currently only works with a local file.
     */
    public BufferedWriter getNewPidReportLogingStream() throws FatalException
    { 
    	URL PIDmapFileURL = null;
    	
		PIDmapFileURL = this.getLogFilesFolderURL();
    		
        BufferedWriter bufWriter = null;
        File pidMapFile = new File( PIDmapFileURL.getFile().replace('/', File.separatorChar)+File.separatorChar+this.getUniqueBatchRunName()+FedoraAppConstants.BATCH_INGEST_PID_REPORT_FILE_EXT );
        try {
            new FileOutputStream( pidMapFile );
            bufWriter = new BufferedWriter( new FileWriter(pidMapFile) );
        } catch (IOException e) {
            throw new FatalException( "Unable to open PID report file: "+pidMapFile);
        }
        
        return bufWriter;
        
    } // getPIDmapLogingStream
    

    /**
     * Currently only works with a local file.
     */
    public BufferedWriter getLoggingStream() throws FatalException {

        BufferedWriter bufWriter = null;
        File logFile = new File( this.getIngestReportLocationURL().getFile() );
        try {
            new FileOutputStream( logFile );
            bufWriter = new BufferedWriter( new FileWriter(logFile) );
        } catch (IOException e) {
            throw new FatalException( "Unable to open Ingest report file: "+logFile);
        }
        
        return bufWriter;
    }
    
    /**
     * This location will vary depending on if the ingest is running as a task.
     * A task will put the reports into a temporary directory prior to performing
     * the finalization of the ingest (e-mailing results, posting rest responses).
     * 
     * @see edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestOptions#isRunningAsRemoteTask()
     */
    public URL getIngestReportLocationURL()
    {     
        URL ingestReportURL = null;  
        
		ingestReportURL = this.getLogFilesFolderURL(); 
        
        String host     = ingestReportURL.getHost();
        String protocol = ingestReportURL.getProtocol();
        int port        = ingestReportURL.getPort();
        String filePath = ingestReportURL.getFile();
        
        try {
            ingestReportURL = new URL( protocol, host, port, filePath+this.getUniqueBatchRunName()+FedoraAppConstants.BATCH_INGEST_REPORT_FILE_EXT );
        } catch (MalformedURLException e) {
            // the original url has already been checked so this should never happen.
            throw new RuntimeException( e.getMessage() );
        }
      
        return ingestReportURL;
    }  
    /**
     * This location will vary depending on if the ingest is running as a task.
     * A task will put the reports into a temporary directory prior to performing
     * the finalization of the ingest (e-mailing results, posting rest responses).
     * 
     * @see edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestOptions#isRunningAsRemoteTask()
     */
    public URL getPidReportLocationURL()
    {    
        URL pidReportURL = null;
        
        pidReportURL = this.getLogFilesFolderURL();  
        
        String host     = pidReportURL.getHost();
        String protocol = pidReportURL.getProtocol();
        int port        = pidReportURL.getPort();
        String filePath = pidReportURL.getFile();
        
        try {
            pidReportURL = new URL( protocol, host, port, filePath+this.getUniqueBatchRunName()+FedoraAppConstants.BATCH_INGEST_PID_REPORT_FILE_EXT );
        } catch (MalformedURLException e) {
            // the original url has already been checked so this should never happen.
            throw new RuntimeException( e.getMessage() );
        }
      
        return pidReportURL;
    }
 

	public URL getImagesFolderLocationURL() {
		
		return this.getImagesFolderURL();
	}
	
    public URL getFailedFilesLocationURL() {
        
        return this.getFailedFilesFolderURL();
    }

    public URL getCompletedFilesLocationURL() {
        
        return this.getCompletedFilesFolderURL();
    }
    
    public URL getCompletedBatchFilesLocationURL() {
        
        return this.getCompletedBatchFilesFolderURL();
    }

    public URL getLogFilesLocationURL() {

        return this.getLogFilesFolderURL();
    }
    
    protected void deleteAllFilesAtLocation( URL locationURL ) throws FatalException{
        
    	this.logger.info( "DELETING ALL FILES in: " + locationURL);
        if ( ! locationURL.getProtocol().toLowerCase().equals( "file" ) ) {
            throw new FatalException( "Unsupported protocol" );
        }
        
        File directory = new File( locationURL.getFile() );
        File[] files = directory.listFiles();
        for ( int i=0; i<files.length; i++ ) {
            files[i].delete();
        }
        
    } // deletedAllFilesAtLocation

    public void deleteAllFailedFiles() throws FatalException {

        this.deleteAllFilesAtLocation( this.getFailedFilesLocationURL() ); 
    }
    
    public void deleteAllCompletedFiles() throws FatalException {

        this.deleteAllFilesAtLocation( this.getCompletedFilesLocationURL() ); 
        this.deleteAllFilesAtLocation( this.getCompletedBatchFilesLocationURL() ); 
    }

    public void deleteAllLogFiles() throws FatalException {

        this.deleteAllFilesAtLocation( this.getLogFilesLocationURL() );
    }

    public String getUniqueBatchRunName() {
        return uniqueBatchRunName;
    }
   

} // BatchIngestURLhandlerImpl
