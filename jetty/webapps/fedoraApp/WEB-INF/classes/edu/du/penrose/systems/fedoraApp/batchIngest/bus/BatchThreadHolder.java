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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.du.penrose.systems.fedoraApp.batchIngest.data.ThreadStatusMsg;

/**
 * Contains a single thread running a specific batch set along with information 
 * about the thread and the thread's current status. Among the information
 * stored about the thread is the threads status, the thread's ingest report 
 * location and the PID report location. Each thread is an instance of 
 * a BatchIngesterController
 * 
 * @author chet.rebman
 * @see BatchIngestThreadManager
 * @see FedoraAppBatchIngestController
 */
public abstract class BatchThreadHolder {

	private ThreadStatusMsg threadStatus = new ThreadStatusMsg();
	
    private BatchIngestController myBatchIngetControlerThread = null;
    private URL ingestReportURL     = null; 
    private URL pidReportURL        = null;
    private URL finalLogLocationURL = null;
    
    /** 
     * Logger for this class and subclasses.
     */
    protected final Log logger = LogFactory.getLog(getClass());
    
    /* (non-Javadoc)
	 * @see edu.du.penrose.systems.fedoraApp.batchIngest.bus.ThreadStatus#setStatus(java.lang.String)
	 */
    public synchronized void setStatus( String statusString ) {
        
        this.threadStatus.setStatus( statusString );
    }
    
    /**
     * Return the internal status object, this allows methods that perform updates to be passed the status object
     * and not the entire thread.
     * 
     * @return thread status object
     */
    public ThreadStatusMsg getThreadStatus()
    {
    	return this.threadStatus;
    }
    
    /* (non-Javadoc)
	 * @see edu.du.penrose.systems.fedoraApp.batchIngest.bus.ThreadStatus#getStatusString()
	 */
    public synchronized String getStatusString()
    {
        
        return this.getThreadStatus().getStatus();
    }
    
    /**
     * Get the single batch ingest thread that this object manages.
     * 
     * @return a FedoraAppBatchIngestController thread.
     */
    public BatchIngestController getBatchIngestThread() {
        
        return this.myBatchIngetControlerThread;
    }
    
    /**
     * Set the single batch ingest thread that this object manages.
     * 
     * @param controllerThread
     */
    void setBatchIngestControllerThread( BatchIngestController controllerThread ) {
        
        this.myBatchIngetControlerThread = controllerThread;
    }

    /**
     * Get the log file URL
     * 
     * @return the log file URL, null if it has not been set.
     */
    public URL getIngestReportURL() {
        return ingestReportURL;
    }

    /**
     * Set the log file URL
     * 
     * @param logFileURL the log file URL
     */
    public void setIngestReportURL(URL logFileURL ) {
        this.ingestReportURL = logFileURL;
    }

    /**
     * Get the URL of this batch ingest PID report.
     * 
     * @return the pidReportURL
     */
    public URL getPidReportURL() {
        return pidReportURL;
    }

    public URL getLogLocationAfterEmailURL() 
    {
    	return finalLogLocationURL;
    }
    /**
     * Set the URL location of this ingests PID report.
     * 
     * @param pidReportURL the pidReportURL to set
     */
    public void setPidReportURL(URL pidReportURL) {
        this.pidReportURL = pidReportURL;
    }
    

	public void setLogLocationAfterEmail(URL logFilesFolderURL) {
		
		this.finalLogLocationURL = logFilesFolderURL;
	}
	
    /**
     * Close the files and stop a batch ingest, without waiting for the current operation
     * to finish.
     * 
     */
    public void forceHardStop() {
        
        this.logger.warn( "Performing Forced Stop" );
        this.myBatchIngetControlerThread.forceHardStop();
        ((Thread) myBatchIngetControlerThread ).interrupt();
        // this.myBatchIngetConrolerThread = null;
    }
}
