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

package edu.du.penrose.systems.fedoraApp.batchIngest.data;

import java.io.BufferedWriter;
import java.io.File;
import java.net.URL;

import edu.du.penrose.systems.fedoraApp.util.FedoraAppUtil;
import edu.du.penrose.systems.util.FileUtil;
import edu.du.penrose.systems.util.MyServletContextListener_INF;
import edu.du.penrose.systems.exceptions.FatalException;

/**
 * Gives access to URLs that describe the location of data used by batch ingest.
 * There are also some convience routines to delete files at a URL and move files
 * to a URL.
 * 
 * @author chet.rebman
 *
 */
public abstract class BatchIngestURLhandler {

    /**
     * Get location of the batch ingest METS XML input folder containing files that have never 
     * been ingested.
     * 
     * @return location of new METS files
     */
    public abstract URL getMetsNewFolderURL();

    /**
     * Get location of the batch ingest METS XML input folder containing nothing but updates
     * to existing objects.
     * 
     * @return location of METS files containing updates.
     */
    public abstract URL getMetsUpdatesFolderURL();
    
    /**
     * Get location of the batch ingest METS XML new or update folder depending on the value of
     * xmlRecordUpdates, this the directory containing the METS that is currently being processed.
     * 
     * @deprecated Use getMetsUpdatesFolderURL() and getMetsFolderURL()
     * @return location of METS files
     */
    public abstract URL getMetsFolderURL();

    /**
     * Get location of the batch ingest temporary work folder
     * 
     * @return location of batch ingest temporary work folder
     */
    public abstract URL getWorkFolderURL();
    
    /**
     * Get location of the batch ingest image (thumbnails) folder
     * 
     * @return location of batch ingest image (thumbnails)  folder
     */
    public abstract URL getImagesFolderURL();
    
   

    /**
     * Get location of the batch ingest attachment files (PCOs) folder.
     * 
     * @return location of batch ingest temporary work folder
     */
    public abstract URL getFilesFolderURL();


    /**
     * Get location of the batch ingest failed XML output folder
     * @return location of failed ingest files
     */
    public abstract URL getFailedFilesFolderURL();
    
    /**
     * Get location of the batchIngest command failed XML output folder, this usually means the file cold not be split.
     * @return location of failed ingest files
     */
    public abstract URL getFailedBatchFilesFolderURL();


    /**
     * Get location of the batch ingest completed XML output folder
     * @return location of completed ingest files
     */
    public abstract URL getCompletedFilesFolderURL();

    /**
     * Get location of the batch ingest completed XML batch file (files contain multiple METS) output folder
     * @return location of completed ingest batch files
     */
    public abstract URL getCompletedBatchFilesFolderURL();


    /**
     * Get location of the batch ingest report/log output folder
     * @return location of report/log files
     */
    public abstract URL getLogFilesFolderURL();
    
    /**
     * Tasks such as the ectd task use a temporary folder for reports/logs prior to performing final ingest tasks, such
     * as email and rest responses.
     * 
     * @return URL of the temporary folder used by tasks
     */
    public abstract URL getTaskTempFolderURL();

    /**
     * Factory to obtain an instance of a BatchIngestURLhandler.
     * 
     * @param webContext see the web.xml file for the context listener
     * @param uniqueBatchRunName a unique name to use for reports etc.
     * @param institution name of the instituion ie. codu (colorado denver university)
     * @param batchSet the name of the batch set.
     * @param xmlRecordUpdates true when updating a record, false if new record.
     * @param batchIngestOptions 
     * @return a new BatchIngestURLhandler
     * @see FedoraAppUtil#getUniqueBatchRunName(String, String)
     * @throws FatalException if unable to create a new object (probably bad 
     * URLs specified in properties file.)
     */
    static public BatchIngestURLhandler getInstance( MyServletContextListener_INF servletContextListener, String uniqueBatchRunName, String institution, String batchSet, boolean xmlRecordUpdates, BatchIngestOptions batchIngestOptions ) throws FatalException {
        
        return new BatchIngestURLhandlerImpl(  servletContextListener, uniqueBatchRunName, institution, batchSet, xmlRecordUpdates, batchIngestOptions );
    }
     
    
    /**
     * Open a NEW buffered writer with a unique name
     * @return BufferedWriter for the PID report.
     * @throws FatalException
     */
    public abstract BufferedWriter getNewPidReportLogingStream() throws FatalException;
    
    
    /**
     * Open a NEW buffered writer with a unique name
     * @return BufferedWriter for the log file.
     * @throws FatalException
     */
    public abstract BufferedWriter getLoggingStream() throws FatalException;

    /**
     * Get the URL for ingest report of the current batch set.
     * 
     * @return ingest report file URL
     */
    public abstract URL getIngestReportLocationURL();


    /**
     * Get the URL to put the log files after email, (if an email is begin set, only for ectd tasks as of 5-11-2011)
     * 
     * @return log directory URL
     */
 //   public abstract URL getLogLocationAfterEmailURL();
    
    /**
     * Get the URL for PID report of the current batch set.
     * 
     * @return PIRD report file URL
     */
    public abstract URL getPidReportLocationURL();
 
    
    /**
     * Get the location of batch ingest log files.
     * 
     * @return URL the location of batch ingest log files.
     */
    public abstract URL getLogFilesLocationURL();

    /**
     * Delete all files in the failed files location.
     * 
     * @see #getFailedFilesFolderURL()
     * @throws FatalException
     */
    public abstract void deleteAllFailedFiles() throws FatalException;
    
    /**
     * Delete all completed files in the commleted files location.
     * 
     * @see #getCompletedFilesFolderURL()
     * @throws FatalException
     */
    public abstract void deleteAllCompletedFiles() throws FatalException;
    
    /**
     * Delete all files in the log file location.
     * 
     * @see #getLogFilesLocationURL()
     * @throws FatalException
     */
    public abstract void deleteAllLogFiles() throws FatalException;
    
   
    /**
     * @return the uniqueBatchRunName
     */
    public abstract String getUniqueBatchRunName();
    

    /**
     * This routine is made public as a convenience to others it used internally
     * for moving the XML files after they are completed.
     * Transfers a local file into the specified URL. Currently only
     * 'file' protocol is supported so it must point to a  local 
     * directory/folder.
     * 
     * If the file already exists in the completed directory, overwrite it.
     * 
     * @param newLocationURL location to move the current file to.
     * @throws FatalException non recoverable error
     */
    public static void transferFileToURL( File fileToMove, URL newLocationURL ) throws FatalException {
        
        if ( ! newLocationURL.getProtocol().toLowerCase().equals( "file" ) ) {
            
            throw new FatalException( "Unsupported Protocol" );
        }
        
        File newLocation = null; //  
        try {
            String newLocatonFileDirectory = newLocationURL.getFile().replace( '/', File.separatorChar );
        
            newLocation = new File( newLocatonFileDirectory + File.separator+fileToMove.getName() );
            boolean moveSuccessfull = false;
            
            if ( fileToMove.exists() ) {
                newLocation.delete(); // make sure it doesn't already exist
                moveSuccessfull = fileToMove.renameTo( newLocation );
            } 
            else {
                throw new FatalException( "Could not find file! file="+fileToMove );  
            }
            
            if ( ! moveSuccessfull ) {
                throw new FatalException( "False returned from File.rename()" );  
            }
        }
        catch ( Exception e ) {
            String errorMsg = "Unable to move completed file: "+fileToMove+" to " + newLocation+"; "+e;
           // this.logger.fatal( errorMsg );  
            throw new FatalException( errorMsg );
        }
        
    } // transferCompletedFiles()

    /**
     * Move the file giving it a unique by appended the data and time.
     * 
     * Note: currently only works with file: protocol oct-2012
     * 
     * @see FileUtil#getUniqueFileName(String)
     * @param fileToMove
     * @param newLocationURL
     * @throws FatalException
     */
    public static void transferFileToUrlWithUniqueName(  File fileToMove, URL newLocationURL )  throws FatalException{
        
        if ( ! newLocationURL.getProtocol().toLowerCase().equals( "file" ) ) {
            
            throw new FatalException( "Unsupported Protocol" );
        }
        
        File newLocation = null; //  
        try {
            String newLocatonFileDirectory = newLocationURL.getFile().replace( '/', File.separatorChar );
        
            String newFilename = FileUtil.getUniqueFileName( fileToMove.getName() );
            newLocation = new File( newLocatonFileDirectory + File.separator+ newFilename);
            boolean moveSuccessfull = false;
            
            if ( fileToMove.exists() ) {
                newLocation.delete(); // make sure it doesn't already exist
                moveSuccessfull = fileToMove.renameTo( newLocation );
            } 
            else {
                throw new FatalException( "Could not find file! file="+fileToMove );  
            }
            
            if ( ! moveSuccessfull ) {
                throw new FatalException( "False returned from File.rename()" );  
            }
        }
        catch ( Exception e ) {
            String errorMsg = "Unable to move completed file: "+fileToMove+" to " + newLocation+"; "+e;
           // this.logger.fatal( errorMsg );  
            throw new FatalException( errorMsg );
        }
    }
    
    
} // BatchIngestURLhandler