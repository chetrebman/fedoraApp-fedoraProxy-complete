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

package edu.du.penrose.systems.fedoraApp.reports;

import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.du.penrose.systems.exceptions.FatalException;
import edu.du.penrose.systems.util.TimeDateUtils;

import java.io.BufferedWriter; 
import java.io.IOException;

/**
 * Generates a report of number of completed and number of failed ingest files
 * at the end of a batch ingest. At list of failure errors is also displayed.
 * 
 * @author chet.rebman
 *
 */
public class BatchIngestReport {
    
    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog( "batchIngestReport" );

    int documentsCompletedCount = 0;
    int documentsFailedCount    = 0;
    int warningsCount       = 0;
    List<String> failedMessages  = null;
    List<String> warningMessages = null;
    BufferedWriter myReport = null;

    long startTime = 0; // start of batch everything, including splitting the batch file.
    long startFedoraIngestTime = 0;
    long endTime   = 0;
    
    private BatchIngestReport(){
        //nop
    }
    
    /**
     * Create a new report to be output to the supplied writer.
     * 
     * @param reportWriter
     */
    public BatchIngestReport( long startTime, BufferedWriter reportWriter ) {
        
        this.myReport   = reportWriter;
        failedMessages  = new ArrayList<String>();
        warningMessages = new ArrayList<String>();
    }
    
    /**
     * Write the report header to the open report log.
     * 
     * @throws FatalException
     */
    public void startReport() throws FatalException {
    	this.startFedoraIngestTime = System.currentTimeMillis();
        String message = "Start of Batch Ingest Report: " + TimeDateUtils.getCurrentTimeMonthDayYearHourMinute() + " *********";
        this.logger.info( "\n\n"+message+"\n" );   
        try {
            this.myReport.newLine();
            this.myReport.newLine();
            this.writeToReport(message);
            this.myReport.newLine();
        } catch (IOException e) {
            throw new FatalException( e.getMessage() );
        }
    }
    
    /**
     * Write a report footer, containing a count of completed and failed, to the open report log.
     * 
     * @throws FatalException
     */
    public void finishReport() throws FatalException {
     
    	SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    	dateFormat.setTimeZone( TimeZone.getTimeZone( "MST" ) );

    	SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm:ss");
    	dateFormat2.setTimeZone(TimeZone.getTimeZone("UTC"));
    	
        String header    = "* Results -";
        String num_completed_msg = "\tDocuments Completed \t= " + this.documentsCompletedCount;
        String num_failed_msg    = "\tDocuments FAILED \t= "    + this.documentsFailedCount;
        String num_warnings_msg  = "\tWarnings \t\t= "            + this.warningsCount;
        
        this.endTime  = System.currentTimeMillis();
        long fedoraIngestTime = this.endTime  - this.startFedoraIngestTime;
        long totalIngestTime  = this.endTime  - this.startTime;
        
        String footer    = "End of Batch Ingest Report:   " + TimeDateUtils.getCurrentTimeMonthDayYearHourMinute() + "Total Time:" +dateFormat2.format(new Date(totalIngestTime))+ "    Fedora Ingest Time: " +dateFormat2.format(new Date(fedoraIngestTime))+ " *********";
        
        this.logger.info( "\n" +header);
        this.logger.info( "\n" +num_completed_msg );
        this.logger.info( "\n" + num_warnings_msg );
        this.logger.info( "\n" + num_failed_msg );

        try {
            this.myReport.newLine();
            this.writeToReport( header ); 
            
            this.myReport.newLine();
            this.writeToReport( num_completed_msg ); 
            this.myReport.newLine();
            this.writeToReport( num_warnings_msg );
            this.myReport.newLine();
            this.writeToReport( num_failed_msg ); 

            this.myReport.newLine();
            this.outputWarningReport();

            this.myReport.newLine();
            this.outputFailedReport();
                 
            this.logger.info( "\n\n"+footer+"\n\n" );   
            this.myReport.newLine();
            this.myReport.newLine();
            this.writeToReport(footer);    
            this.myReport.newLine();
            this.myReport.newLine();

            this.myReport.close();
        }
        catch (IOException e) {
            this.logger.warn( "Unable to write to report:"+e );
           // throw new FatalException( "Unable to write to report:"+e );
        }
    }
    
    /**
     * Output a report of all failure messages to the report log.
     * 
     * @throws IOException
     */
    private void outputFailedReport() throws IOException {
    
        String message = "* Failure Report -";
        this.logger.info( "\n\n"+message );
        this.myReport.newLine();
        this.myReport.newLine();
        this.writeToReport( message ); 
        
        Iterator<String> msgIterator = failedMessages.iterator();
        
        while ( msgIterator.hasNext() ) {
            message = "\t"+msgIterator.next();
            this.logger.info( "\n"+message );
            this.myReport.newLine();
            this.writeToReport( message );
        }
        
        this.logger.info( "\n" );
        this.myReport.newLine();
        
    } 
    
    
    /**
     * Output a report of all warning messages to the report log.
     * 
     * @throws IOException
     */
    private void outputWarningReport() throws IOException {
    
        String message = "* Warnings Report -";
        this.logger.info( "\n\n"+message );
        this.myReport.newLine();
        this.myReport.newLine();
        this.writeToReport( message ); 
        
        Iterator<String> msgIterator = warningMessages.iterator();
        
        while ( msgIterator.hasNext() ) {
            message = "\t"+msgIterator.next();
            this.logger.info( "\n"+message );
            this.myReport.newLine();
            this.writeToReport( message );
        }
        
        this.logger.info( "\n" );
        this.myReport.newLine();
        
    } 
    

    /**
     * Increment the number of completed documents to be displayed in the final report.
     * 
     */
    public void incrementCompletedCount() {
        this.documentsCompletedCount++;
    }

    /**
     * Increment the number of failed documents to be displayed in the final report.
     * 
     * @param failedMessage
     */
    public void incrementFailedCount( String failedMessage ) {
        this.documentsFailedCount++;
        this.failedMessages.add( failedMessage );
    }
    
    /**
     * Add to the warnings to be displayed in the final report.
     * 
     * @param warningMessage
     */
    public void incrementWarningCount( String warningMessage ) {
        this.warningsCount++;
        this.warningMessages.add( warningMessage );
    }
 
    /**
     * Output a newline, the message, followed by a newline.
     * 
     * @param outputMessage
     * @throws FatalException
     */
    public void outputSeperateLineToReport( String outputMessage ) throws FatalException {
        this.logger.info( "\n"+outputMessage+"\n" );
        try {
            this.myReport.newLine();
            this.writeToReport( outputMessage );
            this.myReport.newLine();
        } 
        catch (IOException e) {
            throw new FatalException( "Unable to write to report:"+e );
        }
    }
    
    /*
     * Write the message to the report log.
     * 
     */
    protected void writeToReport( String message ) throws IOException {
        
       this.myReport.write( message );  
    } 
    
    
} // BatchIngestReport
