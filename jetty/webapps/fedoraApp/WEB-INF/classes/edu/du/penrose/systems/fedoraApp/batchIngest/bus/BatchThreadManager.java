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

package edu.du.penrose.systems.fedoraApp.batchIngest.bus;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Contains BatchThreadHolders, giving access to all batch set threads.
 * This object contains a map of BatchThreadHolders with their batch set
 * name as the field . Each BatchThreadHolder contains one batch ingest 
 * background thread.
 * <br>
 * This class assumes that while you can have multiple ingest's running at
 * a time, for one or multiple institutions, only ONE ingest of a particular 
 * institutions 'batch set' will be running at a time. For example this object 
 * provides status for multiple CODU batch sets that may be running 
 * at the same time, however there cannot be two instances of a particular 
 * batch set under the CODU institution running at the same time.
 * 
 * @author chet.rebman
 * @see edu.du.penrose.systems.fedoraApp.batchIngest.bus
 * @see BatchThreadHolder 
 * @see FedoraAppBatchIngestController
 */
public abstract class BatchThreadManager {
	
	static public final String USER_HARD_STOP_RECIEVED = "User Hard Stop recieved";
	static public final String USER_STOP_RECIEVED = "User stop command recieved";
	static public final String INVALID_BATCHSET_NAME = "invalid batchSetName";
	static public final String THREAD_ALREADY_EXISTS  = "Thread Already exists and is running!";
	static              String WARNING_NO_ACTIVE_THREAD_PREFIX  = "Warning: batch set ";
	static              String WARNING_NO_ACTIVE_THREAD_SUFFIX = " does not have an active thread.";

    static private Map<String, BatchIngestThreadHolder> batchIngestThreadMap = null;
    
    /**
     * Set a new thread to the list of currently running threads. If a thread of this name already exists throw an Exception.
     * 
     * @param batchSetName
     * @param batchIngestControllerThread
     * @throws RuntimeException
     */
    static public void setBatchSetThread( String batchSetName, BatchIngestController batchIngestControllerThread  ) throws RuntimeException 
    {     	
        BatchThreadManager.removeBatchset(batchSetName);
        Object result = BatchThreadManager.getBatchIngestThreads().get( batchSetName );
        if ( result != null ){
            throw new RuntimeException( THREAD_ALREADY_EXISTS ); // TBD FatalException here?
        }
        BatchIngestThreadHolder threadHolder = new BatchIngestThreadHolder();
        threadHolder.setBatchIngestControllerThread( batchIngestControllerThread );
        BatchThreadManager.getBatchIngestThreads().put( batchSetName, threadHolder );
    }
    
    /**
     * @param batchSetName of type codu_ectd
     * 
     * @return true if there is a thread performing an ingest for this batch set
     */
    static public boolean isBatchSetThreadExists( String batchSetName )
    { 	
        boolean result = false;
        
        if ( getBatchIngestThreads().get( batchSetName ) != null ) {
            result = true;
        }
        return result;
    }
    
    /**
     * Remove a stopped batch ingest from the list of active batch sets
     * 
     * NOTE: If the batch ingest is still running the batch set is not removed! If
     * there is not such batch ingest set, this call is ignored.
     * 
     * @param batchSetName the batch set to remove.
     */
    static public void removeBatchset( String batchSetName )
    {
        if ( ! isBatchSetThreadExists( batchSetName ) ) {
            return;
        }
        
        if ( getBatchIngestThreads().get( batchSetName ).getBatchIngestThread().isDone() ){
            
            getBatchIngestThreads().remove( batchSetName );     
        }
        else {
           Log logger = LogFactory.getLog( "edu.du.penrose.systems.fedoraApp.batchIngest.bus.BatchThreadManager" );
           logger.warn( "Attempt to remove a running thread! Plase halt it first" );
            
        }
    }
    
    /**
     * Removes a batchSet thread even if the isDone() flag is false. USE WITH CARE!!
     * @param batchSetName
     */
    static public void removeBatchsetFORCE( String batchSetName )
    {
    	getBatchIngestThreads().remove( batchSetName );
    }
    
    /**
     * Get the thread holder for a single batch set. 
     * 
     * @param batchSetName
     * @see #isBatchSetThreadExists(String)
     * @return the batch set holder or null if it does not exit
     */
    static public BatchIngestThreadHolder getBatchSetThread( String batchSetName ) 
    {
        return getBatchIngestThreads().get( batchSetName );
    }
    
    /**
     * 
     * @param batchSetName of type institution_batchSet
     * 
     * @return null if the batch set does not exist.
     */
    static public String getBatchSetStatus( String batchSetName ) 
    {
        String returnStatus = "";
        BatchIngestThreadHolder batchThread = BatchThreadManager.getBatchSetThread( batchSetName );
        if ( batchThread != null ) {
            returnStatus = batchThread.getStatusString();
        }
        else {
            returnStatus  = WARNING_NO_ACTIVE_THREAD_PREFIX+batchSetName+WARNING_NO_ACTIVE_THREAD_SUFFIX ; 
        }
       
        return returnStatus;
        
    } // getBatchSetStatus
    
    
    /**
     * @param batchSetName of type institution_batchSet
     * 
     * @param status
     */
    static public void setBatchSetStatus( String batchSetName, String status ) 
    {
        BatchIngestThreadHolder batchThread = getBatchSetThread( batchSetName );
        
        if ( batchThread != null ){  // have we have been interrupted in a forced halt?
            batchThread.setStatus( status );
            getBatchIngestThreads().put( batchSetName, batchThread );
        }
        
    } // setBatchSetStatus
    
    /**
     * 
     * @param batchSetName of type institution_batchSet
     * 
     * @return true if there is a batch ingest task currently running for this batchSet
     */
    static public boolean isBatchSetRunning ( String batchSetName ) 
    {
        boolean result = false;
        
        BatchIngestThreadHolder threadHolder = BatchThreadManager.getBatchSetThread( batchSetName );
        
        if ( threadHolder != null ) {
            BatchIngestController batchIngestController = threadHolder.getBatchIngestThread();
            if ( batchIngestController != null ) {
                result =  ! batchIngestController.isDone();
           }     
        }
        
        return result;
        
    } // isBatchSetRunning

    static public Map<String, BatchIngestThreadHolder> getBatchIngestThreads()
    {  
        if ( batchIngestThreadMap == null ) {
            batchIngestThreadMap = new HashMap<String, BatchIngestThreadHolder>();
        }
        
        return batchIngestThreadMap;
    }
    
    /**
     * 
     * @param batchSetName of type institution_batchSet
     * @return currently successfull ingests/updates for this ingest.
     */
    static public int getCurrentCompleted( String batchSetName ) 
    {
        int result = 0;
        BatchIngestController batchIngestController = null;
        BatchIngestThreadHolder threadHolder = BatchThreadManager.getBatchSetThread( batchSetName );
        if ( threadHolder != null ) {
            batchIngestController = threadHolder.getBatchIngestThread();
        }
        if ( batchIngestController != null ) {
            result = batchIngestController.getCurrentCompleted();
        }
        
        return result;
    }
    
    /**
     * 
     * @param batchSetName of type institution_batchSet
     * @return number of failed documents for this ingest.
     */
    static public int getCurrentFailed( String batchSetName ) 
    {
        int result = 0;
        BatchIngestController batchIngestController = null;
        
        BatchIngestThreadHolder threadHolder = BatchThreadManager.getBatchSetThread( batchSetName );      
        if ( threadHolder != null ) {
            batchIngestController = threadHolder.getBatchIngestThread();
        }
        if ( batchIngestController != null ) {
            result = batchIngestController.getCurrentFailed();
        }
        
        return result;
    }
    
    /**
     * 
     * @param batchSetName of type institution_batchSet
     */
    static public void stopBatchIngest( String batchSetName ) 
    {
        BatchIngestController   batchIngestController = null;
        
        BatchIngestThreadHolder threadHolder = BatchThreadManager.getBatchSetThread( batchSetName );   
        if ( threadHolder != null ) {
            batchIngestController = threadHolder.getBatchIngestThread();    
        }

        if ( batchIngestController != null ) {
            batchIngestController.setHaltCommand( true );
            threadHolder.setStatus( USER_STOP_RECIEVED  ); 
        }
        
    } // stopBatchIngest
    

    /**
     * 
     * @param batchSetName of type institution_batchSet
     * @return the institution name
     */
    static public String  getInstitution( String batchSetName ) 
    {
        String result = "No Institution?";
        BatchIngestController   batchIngestController = null;
        
        BatchIngestThreadHolder threadHolder = BatchThreadManager.getBatchSetThread( batchSetName );
        if ( threadHolder != null ) {
            batchIngestController = threadHolder.getBatchIngestThread();
        }
        
        if ( batchIngestController != null ) {
            result = batchIngestController.getInstitution();
        }
        
        return result;
        
    } // getInstitution()
    
    
    /**
     * Get the ingest report URL for the specified batch set.
     * 
     * @param batchSetName of type institution_batchSet
     * @return inget report URL, null if not set.
     */
    static public URL getIngestReportURL( String batchSetName )
    {
        URL ingestReportURL = null;
        
        BatchIngestThreadHolder threadHolder = BatchThreadManager.getBatchSetThread( batchSetName );
        
        if ( threadHolder != null ) {
            
            ingestReportURL =  threadHolder.getIngestReportURL();
        }
        
        return ingestReportURL;
        
    } // getIngetReportURL()
    
    
    /**
     * SEt the URL of the batch ingest report
     * @param batchSetName of type institution_batchSet
     * @param ingestReportURL
     */
    static public void setIngestReportURL( String batchSetName, URL ingestReportURL )
    {
        BatchIngestThreadHolder threadHolder = BatchThreadManager.getBatchSetThread( batchSetName );
        
        if ( threadHolder != null ) {
            threadHolder.setIngestReportURL( ingestReportURL );
        }
        
    } // setIngestReportURL()  
    
    /**
     * Get the pid report URL for the specified batch set.
     * 
     * @param batchSetName fo type institution_batchSet
     * @return pid report URL, null if not set.
     */
    static public URL getPidReportURL( String batchSetName )
    {
        URL pidReportURL = null;
        
        BatchIngestThreadHolder threadHolder = BatchThreadManager.getBatchSetThread( batchSetName );
        
        if ( threadHolder != null ) {
            
            pidReportURL =  threadHolder.getPidReportURL();
        }
        
        return pidReportURL;
        
    } // getPidReportURL()
    
    
    /**
     * Set the URL of the Ingest PID Report.
     * 
     * @param batchSetName of type insitution_batchSet
     * @param pidReportURL
     */
    static public void setPidReportURL( String batchSetName, URL pidReportURL )
    {
        BatchIngestThreadHolder threadHolder = BatchThreadManager.getBatchSetThread( batchSetName );
        
        if ( threadHolder != null ) {
            threadHolder.setPidReportURL( pidReportURL );
        }
        
    } // setPidReportURL()
    
    /**
     * Close the files and stop an ingest, without waiting for the current operation
     * to finish.
     * 
     * @param batchSetName String containing name of the batch set job to stop. of type institution_batchSet
     */
    static public void forceHardStop( String batchSetName ) 
    {
        BatchIngestThreadHolder threadHolder = BatchThreadManager.getBatchSetThread( batchSetName );
        
        if ( threadHolder != null ) {

            threadHolder.setStatus( USER_HARD_STOP_RECIEVED ); 
            threadHolder.forceHardStop();
            
// removed 10-20102            removeBatchset( batchSetName );
        }
        else {
            // nothing to do.
        }
        
    }


	public static int getTotalFilesAddedFailed(String batchSetName ) 
	{
        int result = 0;
        BatchIngestController batchIngestController = null;
        BatchIngestThreadHolder threadHolder = BatchThreadManager.getBatchSetThread( batchSetName );
        if ( threadHolder != null ) {
            batchIngestController = threadHolder.getBatchIngestThread();
        }
        if ( batchIngestController != null ) {
            result = batchIngestController.getTotalFilesAddedFailed();
        }
        return result;
	}
	public static int getTotalFilesAddedSuccess(String batchSetName ) 
	{
        int result = 0;
        BatchIngestController batchIngestController = null;
        BatchIngestThreadHolder threadHolder = BatchThreadManager.getBatchSetThread( batchSetName );
        if ( threadHolder != null ) {
            batchIngestController = threadHolder.getBatchIngestThread();
        }
        if ( batchIngestController != null ) {
            result = batchIngestController.getTotalFilesAddedSuccess();
        }
        return result;
	}
	
	public static int getTotalFilesUpdatedFailed(String batchSetName ) 
	{
        int result = 0;
        BatchIngestController batchIngestController = null;
        BatchIngestThreadHolder threadHolder = BatchThreadManager.getBatchSetThread( batchSetName );
        if ( threadHolder != null ) {
            batchIngestController = threadHolder.getBatchIngestThread();
        }
        if ( batchIngestController != null ) {
            result = batchIngestController.getTotalFilesUpdatedFailed();
        }
        return result;
	}
	
	public static int getTotalFilesUpdatedSuccess(String batchSetName ) 
	{
        int result = 0;
        BatchIngestController batchIngestController = null;
        BatchIngestThreadHolder threadHolder = BatchThreadManager.getBatchSetThread( batchSetName );
        if ( threadHolder != null ) {
            batchIngestController = threadHolder.getBatchIngestThread();
        }
        if ( batchIngestController != null ) {
            result = batchIngestController.getTotalFilesUpdatedSuccess();
        }
        return result;
	}

	public static String getIslandoraCollection(String batchSetName ) 
	{
        String result = "none";
        BatchIngestController batchIngestController = null;
        BatchIngestThreadHolder threadHolder = BatchThreadManager.getBatchSetThread( batchSetName );
        if ( threadHolder != null ) {
            batchIngestController = threadHolder.getBatchIngestThread();
        }
        if ( batchIngestController != null ) {
            result = batchIngestController.getIslandoraCollection();
        }
        return result;
	}
	
	public static String getIslandoraContentModel(String batchSetName ) 
	{
        String result = "none";
        BatchIngestController batchIngestController = null;
        BatchIngestThreadHolder threadHolder = BatchThreadManager.getBatchSetThread( batchSetName );
        if ( threadHolder != null ) {
            batchIngestController = threadHolder.getBatchIngestThread();
        }
        if ( batchIngestController != null ) {
            result = batchIngestController.getIslandoraContentModel();
        }
        return result;
	}

	public static boolean isBatchIsUpdates( String batchSetName ) 
	{
        boolean result = true;
        BatchIngestController batchIngestController = null;
        BatchIngestThreadHolder threadHolder = BatchThreadManager.getBatchSetThread( batchSetName );
        if ( threadHolder != null ) {
            batchIngestController = threadHolder.getBatchIngestThread();
        }
        if ( batchIngestController != null ) {
            result = batchIngestController.isBatchIsUpdates();
        }
        return result;
	}
	
	/**
	 * This was added oct 2012 to signify that the thread is not running due to a user stop command. This is needed so that remote
	 * ingests can test for it in addition to the isRunning() flag, other wise the remote task sees the thread as not running 
	 * and will simply restart it, if ingest files exist,  after  the user stops it via the GUI
	 * 
	 * @param batchSetName
	 * @return
	 */
	public static boolean isStoppedByUser( String batchSetName )
	{
		boolean result = true;
        BatchIngestController batchIngestController = null;
        BatchIngestThreadHolder threadHolder = BatchThreadManager.getBatchSetThread( batchSetName );
        if ( threadHolder != null ) {
            batchIngestController = threadHolder.getBatchIngestThread();
        }
        if ( batchIngestController != null ) {
            result = batchIngestController.getHaltCommand();
        }
        return result;
	}
	
}
