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

package edu.du.penrose.systems.fedoraApp.batchIngest.bus;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;

import edu.du.penrose.systems.exceptions.BagitInvalidException;
import edu.du.penrose.systems.exceptions.FatalException;
import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BagHandler;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestOptions;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestURLhandler;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestXMLhandler;
import edu.du.penrose.systems.fedoraApp.reports.BatchIngestReport;
import edu.du.penrose.systems.fedoraApp.util.FedoraAppUtil;
import edu.du.penrose.systems.util.FileUtil;
import edu.du.penrose.systems.util.SendMail;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.ThreadStatusMsg;

/**
 * Top level class that contains common routines for performing a batch ingest.
 * 
 * @author chet
 *
 */
public abstract class BatchIngestController  extends Thread {

	/** 
	 * Logger for this class and subclasses.
	 */
	protected final Log logger = LogFactory.getLog(getClass());

	private String myBatchRunName = null;
	private BatchIngestURLhandler tempBatchURLhandler = null; //used prior to batchIngestXMLhandler initialization.
	
	private boolean haltCommand = false;
	private BatchIngestReport report = null;

	boolean done = false;

	private String institution  = null;
	private String batchSet     = null; 

	BatchIngestOptions batchOptions = null;

	private BatchIngestXMLhandler batchIngestXMLhandler = null; // points to currently running batch
				
		//manages the directory containing xml files for new objects.
	protected BatchIngestXMLhandler batchIngestXMLhandler_new     = null; 
		// manages the directory containing xml files for updating existing objects.
	protected BatchIngestXMLhandler batchIngestXMLhandler_updates = null; 

	protected BatchIngestURLhandler batchURLhandler_new     = null;
	protected BatchIngestURLhandler batchURLhandler_updates = null;
	
	private BatchIngestController( )
	{
		// disable
	}

    protected long startTime = 0; // start of batch everything including splitting the batch file.
    
	/**
	 * 
	 * @param institutionName the institution name for this batch set. This is used in reports and 
	 * to locate the batch set.
	 * @param batchSet  the name of this batch set. This is used in reports and to locate
	 * the batch set.
	 * @param batchRunOptions
	 */
	public BatchIngestController( String institutionName, String batchSet, BatchIngestOptions batchRunOptions ) throws FatalException
	{
		this.institution      = institutionName;
		this.batchSet         = batchSet;
		this.batchOptions     = batchRunOptions;
		
		this.myBatchRunName = FedoraAppUtil.getUniqueBatchRunName(institutionName, batchSet);
		
		this.tempBatchURLhandler = BatchIngestURLhandler.getInstance( FedoraAppConstants.getServletContextListener(), this.myBatchRunName, this.institution, this.batchSet, false, this.batchOptions );
	}

	protected BatchIngestXMLhandler getBatchIngestXMLhandler() {
		return this.batchIngestXMLhandler;
	}

	protected void setBatchIngestXMLhandler( BatchIngestXMLhandler newBatchIngestXmlHandler ) 
	{
		this.batchIngestXMLhandler = newBatchIngestXmlHandler;
	}

	protected String getBatchPath() 
	{	 
		return this.getInstitution()+'/'+this.getBatchSet()+'/';
	}

	protected BatchIngestOptions getBatchOptions() 
	{
		return this.batchOptions;
	}

	protected BatchIngestReport getReport() 
	{
		return this.report;
	}

	protected void setReport(BatchIngestReport report) {
		this.report = report;
	}

	/**
	 * Return the runtime statistic of the number files that have been ingested
	 * so far.
	 * 
	 * @return the number of files ingest so far.
	 */
	public int getCurrentCompleted() {

		if ( this.getBatchIngestXMLhandler() == null ) { 
			return 0;
		}   
		return this.getBatchIngestXMLhandler().getCurrentCompleted();
	}

	/**
	 * Return the runtime statistic of the number files that have failed so far 
	 * during the ingest.
	 * 
	 * @return the number of files failed so far during ingest.
	 */
	public int getCurrentFailed() {

		if ( this.getBatchIngestXMLhandler() == null ) { 
			return 0;
		}
		return this.getBatchIngestXMLhandler().getCurrentFailed();
	}
 
	/**
	 * Start the batch ingest thread running.
	 */ 

	public void run() 
	{
		this.startTime = System.currentTimeMillis();
		
		String finalStatusMsg = null;
		try 
		{					
			BatchThreadManager.setIngestReportURL( this.getBatchSetName(), this.getBatchURLhandler().getIngestReportLocationURL() ); 
			BatchThreadManager.setPidReportURL(    this.getBatchSetName(), this.getBatchURLhandler().getPidReportLocationURL()    );		
		
				/* This must happen before we output any data to the log file (may get deleted). Also we want to get the
				 * xml files in their location before initializing any BatchIngestXMLhandler objects.
				 */
			this.performPreIngestOptions();
				
			FilenameFilter fileFilter = null; 
				
			switch (  this.batchOptions.getIngestThreadType() ) {
			case MANUAL:
				fileFilter = new FileUtil.XML_NON_remote_fileFilter();
				break;
			case REMOTE:
				fileFilter = new FileUtil.XML_remote_fileFilter();
				break;
			case BACKGROUND:
				fileFilter = new FileUtil.XML_task_fileFilter();
				break;
			}
			
			
				// manages the directory containing xml files for new objects.			
			this.batchIngestXMLhandler_new = BatchIngestXMLhandler.getInstance( BatchIngestURLhandler.getInstance( FedoraAppConstants.getServletContextListener(), this.myBatchRunName, 
					this.institution, this.batchSet, false, this.getBatchOptions() ), fileFilter  ); 
			
				// manages the directory containing xml files for updating existing objects.
			this.batchIngestXMLhandler_updates = BatchIngestXMLhandler.getInstance( BatchIngestURLhandler.getInstance( FedoraAppConstants.getServletContextListener(), this.myBatchRunName,
					this.institution, this.batchSet, true, this.getBatchOptions() ), fileFilter  ); 
		
				// Current log may get deleted, so reopen it. We assume it has no data.
				// at this point a file of typ codu.ectd.june-20-2012 has been opened in the logs or tasksTemp (remote ingest) directory
			this.report = new BatchIngestReport( this.startTime, this.getBatchURLhandler().getLoggingStream() );
			
			this.getReport().startReport();

			this.updateStatusMsg( "Start batch ingest" );

			this.runBatch();
		}  
//		catch ( badBag )
//		{
//		}
//		}
//		catch ( MalformedURLException e ) // this is thrown by a bad url in bagit.
//		{
//			
//		}
		catch ( FatalException e ) {
			finalStatusMsg = "<FONT color=\"red\">ERROR, Batch Halted: </FONT>"+e.getMessage();
			String stars = "************************************";
			String errorMessage = "BATCH FAILED! "+e.getMessage()+" ";

			this.logger.fatal( "\n"+stars+"\n" );
			this.logger.fatal( "\n"+errorMessage+"\n" );
			this.setDone( true );
			try {
				if ( this.getReport() == null ) // an error may have occurred in batch ingest options.
				{
					this.report = new BatchIngestReport( this.startTime, this.getBatchURLhandler().getLoggingStream() );	
					this.getReport().startReport();
				}
				this.getReport().outputSeperateLineToReport( "" );
				this.getReport().outputSeperateLineToReport( stars );
				this.getReport().outputSeperateLineToReport( errorMessage );
				this.getReport().outputSeperateLineToReport( stars );
				this.getReport().outputSeperateLineToReport( "" );
				this.logger.error( "\n"+stars+errorMessage+stars+"\n" );
				
				switch ( this.getBatchOptions().getIngestThreadType() ){
				case MANUAL:
					/*This is here just to be consistent. Email for batch ingest will not acutally work until the fields are added into the
					 * form on batchIngest.jsp. They have been added to the FedoraAppUtil.loadIngestFileOptions and saveIngestFileOptions()
					 * 
					 */
					SendMail.sendFailureEmail( this.getBatchOptions(), "BACKGROUND INGEST FAILURE", "FatalException:"+e.getMessage() );
					break;
				case BACKGROUND:
					String appPath = FedoraAppConstants.getServletContextListener().getAppRealPath();
					
		//			EnableDisableRemoteTasks.enableDisableRemoteTasks( appPath, false );

					String failureMessage =  "FatalException:"+e.getMessage();
					this.logger.error( failureMessage );
					
					this.getReport().outputSeperateLineToReport( stars );
					this.getReport().outputSeperateLineToReport( failureMessage );
					this.getReport().outputSeperateLineToReport( stars );
					
					SendMail.sendFailureEmail( this.getBatchOptions(), "BACKGROUND INGEST FAILURE", failureMessage );
					break;
				case REMOTE:
					 appPath = FedoraAppConstants.getServletContextListener().getAppRealPath();
					
		//			EnableDisableRemoteTasks.enableDisableRemoteTasks( appPath, false );

					failureMessage =  "FatalException:"+e.getMessage();
					this.logger.error( failureMessage );
					
					this.getReport().outputSeperateLineToReport( stars );
					this.getReport().outputSeperateLineToReport( failureMessage );
					this.getReport().outputSeperateLineToReport( stars );
					
					SendMail.sendFailureEmail( this.getBatchOptions(), "REMOTE INGEST FAILURE", failureMessage );
					break;
				}

				
			} catch (Exception e1) {
				this.logger.error( errorMessage );
			}               
		} 
		finally {
			this.setDone( true );
			
			if ( this.getHaltCommand() ) 
			{
				finalStatusMsg = BatchThreadManager.USER_STOP_RECIEVED;
			}
			
			if ( finalStatusMsg == null ) // not sure why this needed.
			{	
					finalStatusMsg = "Completed";							
			}
			
			this.updateStatusMsg( finalStatusMsg );
			try 
			{
				if ( this.getReport() == null ) // and error may have occurred in batch ingest options.
				{
					this.report = new BatchIngestReport( this.startTime, this.getBatchURLhandler().getLoggingStream() );	
					this.getReport().startReport();
				}

				this.getReport().finishReport();

				SendMail.sendReportEmail( this.getBatchOptions(), BatchIngestThreadManager.getBatchSetThread( this.getBatchOptions().getBatchSetName() ).getIngestReportURL().getFile(), 
					                      BatchIngestThreadManager.getBatchSetThread( this.getBatchOptions().getBatchSetName()  ).getPidReportURL().getFile() );					
			} 
			catch (Exception e) {
				this.logger.error( "ERROR:"+e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
	} // run

	
	/**
	 * Return runtime status of thread.
	 * 
	 * @return if true the batch ingest is complete.
	 */
	public boolean isDone()
	{
		return this.done;
	}

	/**
	 * Set the halt command. If the halt command is true at the time of the next
	 * file ingest the batch thread will exit.
	 * 
	 * @param haltThread true to stop ingest.
	 */
	public void setHaltCommand( boolean haltThread ) 
	{
		this.logger.info( "Halt command recieved in Batch Ingest Thread ");
		this.haltCommand = haltThread; 
	}

	/**
	 * Check if the thread is set to stop after current file (and PCOs) are 
	 * ingested into Fedora.
	 * 
	 * @return true if set to halt after next ingest.
	 */
	public boolean getHaltCommand() 
	{
		return this.haltCommand;
	}

	/**
	 * Get the OBJID contained in a METS element. The OBJID is set by the provider of
	 * the METS writing the original XML file.
	 * 
	 * @param metsDocument
	 * @return the OBJID vaule in the METS document.
	 */

	public String getOBJIDfromMets( Document metsDocument ){

		return metsDocument.getRootElement().getAttribute( "OBJID" ).getValue(); // TBD magic #
	}

	/**
	 * Get the instituion name for this batch set This is used in reports and 
	 * to locate the batch set.
	 */
	public String getInstitution()
	{
		return this.institution;
	}



	/**
	 * Return the name of this batch set. This is used in reports and to locate
	 * the batch set.
	 * 
	 * @return the batchSet
	 */
	public String getBatchSet()
	{
		return this.batchSet;
	}

	/**
	 * A convenience method to obtain the applications URL handler which has all
	 * of the folder locations used by the batch ingest application.
	 * 
	 * @return the batchURLhandler
	 */
	public BatchIngestURLhandler getBatchURLhandler()
	{
		if (  this.batchIngestXMLhandler == null )
		{
			return this.tempBatchURLhandler;
		}
		
		return this.batchIngestXMLhandler.getUrlHandler(); 
	}

	/**
	 * @param done the done to set
	 */	
	protected void setDone(boolean done) 
	{
		this.logger.info( "Batch Ingest 'done' status set to "+done );
		this.done = done;
	}


	/**
	 * Close the files and stop a batch ingest, without waiting for the current operation
	 * to finish.
	 * 
	 */
	public void forceHardStop() {

		this.setDone( true );
		this.setHaltCommand( true );
		
		this.logger.info( "Performing FORCED STOP of batch set:"+this.batchSet );

		try {
			this.report.finishReport();
		} catch (FatalException e) {
			this.logger.fatal( "EXCEPTION DURING FORCED STOP of batch set:"+this.batchSet+" :"+e);
		}
	}

	protected void updateStatusMsg( String statusMsg ) {

		this.logger.info( statusMsg );
		BatchThreadManager.setBatchSetStatus( this.getBatchSetName(), statusMsg ); 
	}


	/**
	 * UPdate the Report and Move the xml and the PCO files (if pointed to in xml file) to the failed folder.
	 * 
	 * @param errorMsg
	 * @throws FatalException
	 */
	protected void markAsFailed( String errorMsg ) throws FatalException {

		this.setHaltCommand( this.getHaltCommand() || this.getBatchOptions().isStopOnError() );
		this.report.incrementFailedCount( errorMsg );
		
		//this.batchIngestXMLhandler.markCurrentDocumentAsFailed();	
		this.batchIngestXMLhandler.markCurrentDocumentAsFailedMovePcos();
	}

	protected void markAsCompleted() throws FatalException
	{
		this.getBatchIngestXMLhandler().markCurrentDocumentAsCompleted();
		this.getReport().incrementCompletedCount();		
	}

	/**
	 * If any pre-batch ingest user options have been set, perform them now.
	 * @throws Exception 
	 */
	protected void performPreIngestOptions() throws FatalException {
	
		// do file deletions first!
		if ( this.getBatchOptions().isClearFailedFiles() ) {
	
			this.getBatchURLhandler().deleteAllFailedFiles(); 
		}
	
		if ( this.getBatchOptions().isClearCompletedFiles() ) {
	
			this.getBatchURLhandler().deleteAllCompletedFiles(); 
		}
	
		if ( this.getBatchOptions().isClearLogFile() ) {
	
			this.getBatchURLhandler().deleteAllLogFiles(); 
		}      
	
		// prepare to ingest.
		if ( this.getBatchOptions().isSplitXMLinWorkDirToMets() ) {
	
			// if it ends in zip we assume it is a bagit file.
			if ( this.getBatchOptions().getSplitXMLfileName().toLowerCase().endsWith( FedoraAppConstants.BAGIT_FILE_SUFFIX ) ) 
			{
				try {
					new BagHandler( batchOptions, this.getBatchOptions().getSplitXMLfileName() ).batchIngestExtractRetrieveAndMove();						
				} 
				catch ( BagitInvalidException e )
				{
					String bagitBatchFile = this.getBatchURLhandler().getWorkFolderURL().getPath() + this.getBatchOptions().getSplitXMLfileName();
					BatchIngestURLhandler.transferFileToURL( new File( bagitBatchFile ), this.getBatchURLhandler().getFailedBatchFilesFolderURL() );
					throw new FatalException( e.getMessage() );
				}
				catch (Exception e) {	 
					String bagitBatchFile = this.getBatchURLhandler().getWorkFolderURL().getPath() + this.getBatchOptions().getSplitXMLfileName();
					BatchIngestURLhandler.transferFileToURL( new File( bagitBatchFile ), this.getBatchURLhandler().getFailedBatchFilesFolderURL() );
					throw new FatalException( e.getMessage() );
				} 	
			}
			
			this.splitMets();
			this.updateStatusMsg( "Preparing to start ingest." );
			try {
				// Thread.sleep( 10000 ); // give file system a chance to catch up.
				Thread.sleep( 2000 ); // This really isn't needed. Just allows above message to be viewed, helps for debug.
			} catch (InterruptedException e) {
				//nop
			}
		}
	
	} // performPreIngestOptions()

	/**
	 * Split a single file containing multiple METS records into separate METS
	 * files. The original file is contained in the work directory and the
	 * separated files are saved to the METS directory. After a successful split
	 * the original file is moved to the completed directory. If the file fails
	 * to split it is left in the work directory. Use the <batch>? element in
	 * the batch file to update BatchOptions. True for existing records, false means 
	 * all records are new.
	 * 
	 * @see BatchIngestOptions#setBatchIsUpdates(boolean)
	 * @throws FatalException
	 */
	protected void splitMets() throws FatalException {
	
		this.updateStatusMsg( "Spliting XML file: " + this.getBatchOptions().getSplitXMLfileName() );
	
		File fileToSplit = null;
		try {
			URL workFolder      = this.getBatchURLhandler().getWorkFolderURL();
			URL metsUpdatesFolder = this.getBatchURLhandler().getMetsUpdatesFolderURL();
			URL metsNewFolder     = this.getBatchURLhandler().getMetsNewFolderURL();
			
			if ( ! workFolder.getProtocol().toLowerCase().equals( "file" )) {
				throw new FatalException( "Unsupported protocol: " + workFolder.getProtocol() );
			}
			fileToSplit = new File ( workFolder.getFile() + File.separatorChar + this.getBatchOptions().getSplitXMLfileName() );
	  
			ThreadStatusMsg statusUpdater = BatchThreadManager.getBatchSetThread( this.getBatchOptions().getBatchSetName() ).getThreadStatus();
			
			this.callFileSplitter( this.getBatchOptions(), statusUpdater, fileToSplit, metsNewFolder.getFile(), metsUpdatesFolder.getFile() );      

			BatchIngestURLhandler tempURLhandler = BatchIngestURLhandler.getInstance( FedoraAppConstants.getServletContextListener(), this.myBatchRunName, this.institution, this.batchSet, false, this.getBatchOptions() );
			
			/**
			 * The batch file has been split, so move it so that it is not process again.
			 */
			BatchIngestURLhandler.transferFileToURL( fileToSplit, tempURLhandler.getCompletedBatchFilesFolderURL() );
			
		} catch ( Exception e) {
			BatchIngestURLhandler.transferFileToURL( fileToSplit, this.getBatchURLhandler().getFailedBatchFilesFolderURL() );
			throw new FatalException( e.getMessage() );
		}
	
	} // splitMets()

	/**
	 * Defines method to split a large xml file, into a number of smaller xml files, storing them into the specified
	 * directories.
	 *  
	 * All sub classes must implement this method with a call to the actual method of splitting the file.
	 * 
	 * @see edu.du.penrose.systems.fedoraApp.batchIngest.data.MetsBatchFileSplitter.MetsBatchFileSplitter()
	 * 
	 * @param threadStatus used for logging status.
	 * @param ingestOptions contains user options and options set in the batch xml file.
	 * @param fileToSplit file containing mets surrounded by <batch> element.
	 * @param metsNewDirectory     METS for new objects will be put into this directory.
	 * @param metsUpdatesDirectory METS for objects to be updated will be put into this directory.
	 * @throws Exception
	 */
	abstract protected void callFileSplitter( BatchIngestOptions ingestOptions, ThreadStatusMsg threadStatus, File fileToSplit, String MetsNewDirectory, String MetsUpdatesDirectory ) throws Exception;

	/**
	 * The main program that performs the Fedora batch ingest. After ingest we 
	 * check that the object is actually in Fedora with the PID Fedora supplied
	 * earlier, if it is not an Exception is thrown. 
	 * <br><br>
	 * NOTE; IF Fedora does not like the PID due to an invalid context, Fedora
	 * does not generate an error. An error will be generated after ingest when 
	 * we check that the object is in Fedora with the supplied PID. Therefore if
	 * you get an error on retrieving the object, the object may actually be in 
	 * Fedora in the 'changeme' context with a PID of type changeme:xxx.
	 * 
	 * @throws Exception on any fatal error.
	 */
	protected abstract void runBatch() throws FatalException; // runBatch
	

	public    abstract int getTotalFilesAddedSuccess();

	protected abstract void setTotalFilesAddedSuccess(int totalFilesAddedSuccess);

	public abstract int getTotalFilesAddedFailed() ;

	protected abstract void setTotalFilesAddedFailed(int totalFilesAddedFailed);

	public abstract int getTotalFilesUpdatedSuccess() ;

	protected    abstract void setTotalFilesUpdatedSuccess(int totalFilesUpdatedSuccess);

	public abstract int getTotalFilesUpdatedFailed();

	protected    abstract void setTotalFilesUpdatedFailed(int totalFilesUpdatedFailed);

	/** the islandoraCollection is displayed on the final gwt result page
	 * @see edu.du.penrose.systems.fedoraApp.web.gwt.batchIngest.client.BatchIngestStatus
	 * @see "edu/du/penrose/systems/fedoraApp/web/gwt/batchIngest/public/batchIngestStatus.jsp"
	 */
	public  String getIslandoraCollection(){ return null; }
	
	/** the islandoraContentModel is displayed on the final gwt result page
	 * @see edu.du.penrose.systems.fedoraApp.web.gwt.batchIngest.client.BatchIngestStatus
	 * @see "edu/du/penrose/systems/fedoraApp/web/gwt/batchIngest/public/batchIngestStatus.jsp"
	 */
	public  String getIslandoraContentModel(){ return null; }

	public abstract boolean isBatchIsUpdates();

	/**
	 * return the batch set name of type 'institution_batchSet'
	 * 
	 * @return institution_batchset
	 */
	public String getBatchSetName() 
	{
		return this.getBatchOptions().getBatchSetName();
	}

	
} // BatchIngestController