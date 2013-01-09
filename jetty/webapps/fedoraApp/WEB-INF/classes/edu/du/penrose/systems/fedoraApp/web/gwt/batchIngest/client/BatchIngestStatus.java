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

package edu.du.penrose.systems.fedoraApp.web.gwt.batchIngest.client;

import java.util.Date;
import com.google.gwt.i18n.client.DateTimeFormat;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.*;
import com.google.gwt.i18n.client.Dictionary;

//import edu.du.penrose.systems.fedoraApp.FedoraAppConstants; // TBD unable to find on class path

import edu.du.penrose.systems.fedoraApp.web.gwt.batchIngest.server.BatchIngestThreadManagerService;
import edu.du.penrose.systems.fedoraApp.web.gwt.batchIngest.server.BatchIngestThreadManagerServiceAsync;
import edu.du.penrose.systems.fedoraApp.web.gwt.batchIngest.server.StatusData;

/**
 * This class is converted to javascript via GWT Google Web ToolKit and runs in the
 * client browser. The code displays the current status of a thread, using a 
 * single object to return it from the server (StatusData). The RPC is made
 * available via the Spring framework instead of using the standard GWT model.
 * <br><br>
 * NOTE: See exporter-servlet.xml and fedoraApp-servlet.xml
 * <br>
 * @see edu.du.penrose.systems.fedoraApp.web.gwt.batchIngest.server.StatusData
 * @see 
 */


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class BatchIngestStatus implements EntryPoint { 

    final String CONTEXT_URL = "/fedoraApp/";  // TBD should be of type KepiGwtUtils.getWebApplicationName() 
    
    static final public String BATCH_SET_REQUEST_PARAM = "batch_set";
    
    static boolean userStopptedStatus = false; // added oct 2012 so that we had control over the entable ingests button
    
    final String pageVersion = "v0.26";
    final long   DELAY_BEFRE_ABORT_BTN_IN_MILLISECONDS = 10000;

    final static String REMOTE_TASK_NAME_SUFFIX     = "_REMOTE"; // TBD unable to import fedoraAppConstants
    final static String BACKGROUND_TASK_NAME_SUFFIX = "_TASK"; 
    		
 //   final String threadManagerRpcURL = "../myRPCServicesURL/batchIngestThreadManagerRPC"; 
    
    final String threadManagerRpcURL = "myRPCServicesURL/batchIngestThreadManagerRPC"; 
    
    final String contextURL = "/fedoraApp/"; //TBD until I can import FedoraAppConstants
    final int statusUpdatePeriodInMilliseconds = 1000;
    final String timeHourMinuteFormat = "EEE, MMM d, yyyy, h:mm a";
    
    boolean tryingToStop = false; // set true after stop button pressed.
    boolean ingestRunning = false; // used to display stop message only after a stop.
    StatusTimer myStatusTimer = null;
    long stopBtnPushedTime = 0l;
    int  flowBarInt = 0; // used to create rotating icon on status page to show program is running.
    
    public BatchIngestStatus(){

        myStatusTimer = new StatusTimer();
        flowBarInt = 0;
    }
    
    /**
     * This inner class provides the periodic page display and status update.
     * @author chet.rebman
     *
     */
    class StatusTimer extends com.google.gwt.user.client.Timer {

        public void run() {
            
            doItAll();
        }
        
    } // StatusTimer
    
    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        
        DateTimeFormat dateFormat = DateTimeFormat.getFormat( timeHourMinuteFormat );
        Date startTime = new Date();
        
        String startString = pageVersion+"; Start time: " + dateFormat.format( startTime ).toLowerCase();
        
        clearStatusBox();
        
        appendToStatusBox( startString );

        this.myStatusTimer = new StatusTimer();
        
        doItAll();
         
        /* I BELIEVE THIS COMMENT IS NO LONGER  VALID  8-15-12
         * I was unable to cancel a repeating timer after an exception was thrown,
         * filling the screen with error messages. We now just schedule the next
         * event after a successfully getting status from the server.
         */
        
         this.myStatusTimer.scheduleRepeating( statusUpdatePeriodInMilliseconds );
        
    } // onModuleLoad()
    
    public void doItAll(){ 
        
        updateScriptRunningChar();
        
        String batch_set = this.getBatchSetName();  
        this.getAllStatusThenDisplay( batch_set );
        
        if ( batch_set != null ) 
        {
        	this.displayCorrectButtonPanel( batch_set );
        }
        
    }

    
    BatchIngestThreadManagerServiceAsync getThreadManagerService() {

 //      String statusPojoURL = "BatchIngestThreadManagerService"; // when using standalone hosted mode
        
        BatchIngestThreadManagerServiceAsync service = 
            (BatchIngestThreadManagerServiceAsync) GWT.create(BatchIngestThreadManagerService.class);
        ServiceDefTarget target = (ServiceDefTarget) service;
        
   //     String moduleRelativeURL = GWT.getModuleBaseURL() + threadManagerRpcURL;   
        
        String moduleRelativeURL = CONTEXT_URL +  threadManagerRpcURL;  // TBD should be of type KepiGwtUtils.getWebApplicationName() + KepiGwtConstants.servicePath;
        	
        target.setServiceEntryPoint( moduleRelativeURL );

        return service;
    }


    void displayStatusBar( String status) {

        RootPanel.get("statusBar").clear();
        RootPanel.get("statusBar").add(new HTML( status ));
    }
    
    protected void clearStatusBox() {
        
        RootPanel.get("statusBox").clear();
    }
    
    protected void appendToStatusBox( String status) {

        RootPanel.get("statusBox").add(new HTML( status ));
    }
    
    void displayAllStatus( StatusData allStatus) 
    {
    	userStopptedStatus = allStatus.isStoppedByUser();
    	
        displayStatusBar( allStatus.getStatus() );
        RootPanel.get("institution").clear();
        RootPanel.get("institution").add( new HTML( String.valueOf(allStatus.getInstitution()) ) );
        RootPanel.get("totalFilesAddedSuccess").clear();
        
        if ( allStatus.isRunning() &&  allStatus.isBatchIsUpdates() )
        {

                RootPanel.get("totalFilesUpdatedSuccess").clear();
                RootPanel.get("totalFilesUpdatedSuccess").add( new HTML( String.valueOf(allStatus.getCompleted()) ) );
                RootPanel.get("totalFilesUpdatedFailed").clear();
                RootPanel.get("totalFilesUpdatedFailed").add( new HTML( String.valueOf(allStatus.getFailed()) ) );
        }	
        if ( allStatus.isRunning() && ! allStatus.isBatchIsUpdates() )
        	{
	        	RootPanel.get("totalFilesAddedSuccess").clear();
	            RootPanel.get("totalFilesAddedSuccess").add( new HTML( String.valueOf(allStatus.getCompleted()) ) );
	            RootPanel.get("totalFilesAddedFailed").clear();
	            RootPanel.get("totalFilesAddedFailed").add( new HTML( String.valueOf(allStatus.getFailed()) ) );
        	}

        if ( ! allStatus.isRunning()  )
        {
        	RootPanel.get("totalFilesAddedSuccess").clear();
            RootPanel.get("totalFilesAddedSuccess").add( new HTML( String.valueOf(allStatus.getTotalFilesAddedSuccess()) ) );
            RootPanel.get("totalFilesAddedFailed").clear();
            RootPanel.get("totalFilesAddedFailed").add( new HTML( String.valueOf(allStatus.getTotalFilesAddedFailed()) ) );
            RootPanel.get("totalFilesUpdatedSuccess").clear();
            RootPanel.get("totalFilesUpdatedSuccess").add( new HTML( String.valueOf(allStatus.getTotalFilesUpdatedSuccess()) ) );
            RootPanel.get("totalFilesUpdatedFailed").clear();
            RootPanel.get("totalFilesUpdatedFailed").add( new HTML( String.valueOf(allStatus.getTotalFilesUpdatedFailed()) ) );
        }


        RootPanel.get("collection").clear();
        RootPanel.get("collection").add( new HTML( String.valueOf(allStatus.getIslandoraCollection() ) ) );
        RootPanel.get("contentModel").clear();
        RootPanel.get("contentModel").add( new HTML( String.valueOf(allStatus.getIslandoraContentModel() ) ) );
    }

  private void displayException( Throwable exception ){
      
      this.myStatusTimer.cancel();
      
      appendToStatusBox( "EXCEPTION occured:"+exception); 
      appendToStatusBox( "EXCEPTION message:"+exception.getMessage());  
      appendToStatusBox( "EXCEPTION localized message:-"+exception.getLocalizedMessage()); 
      appendToStatusBox( "<br>");  
      appendToStatusBox( "Auto Update of page has been CANCELED to restart, please reload the page.");  
  }

  void getAllStatusThenDisplay( String batch_set ){

      BatchIngestThreadManagerServiceAsync threadMangerService = this.getThreadManagerService();
      
      AsyncCallback callBackResults = new AsyncCallback() {
          public void onFailure(Throwable exception) {
              displayException( exception );
            }
            public void onSuccess(Object result){
               displayAllStatus( (StatusData) result );
            }
      };
      
      threadMangerService.getAllBatchSetStatus( batch_set, callBackResults );  
  }
  
  void enableNewBatch( String batch_set ){

      BatchIngestThreadManagerServiceAsync threadMangerService = this.getThreadManagerService();
      
      AsyncCallback callBackResults = new AsyncCallback() {
          public void onFailure(Throwable exception) {
              displayException( exception );  
            }
            public void onSuccess(Object result){
               displayStatusBar( "New batch set enabled" );
               Window.open( contextURL+"batchIngest.htm", "_self", null);
            }
      };
      
      threadMangerService.removeBatchset( batch_set, callBackResults );  
  }

  protected void displayCorrectButtonPanel( String batch_set ){
      
      BatchIngestThreadManagerServiceAsync threadMangerService = this.getThreadManagerService();
      final String myBatchSet = batch_set;
      
      AsyncCallback callBackResults = new AsyncCallback() {
          public void onFailure(Throwable exception) {
              displayException( exception );  
            }
            public void onSuccess(Object result){

                Boolean isRunning = (Boolean) result;
                if ( isRunning.booleanValue() ) {
                    ingestRunning = true;
                    if ( ! tryingToStop ) {
                        RootPanel.get("bottonPanel").clear();
                        RootPanel.get("bottonPanel").add( getIngestRunningPanel( myBatchSet ) );    
                    }
                    else {
                        RootPanel.get("bottonPanel").clear();
                        RootPanel.get("bottonPanel").add( getTryingToStopPanel( myBatchSet ) ); 
                    }
                }
                else {
                	/* 
                	* Changes made on 8-2012 are an attempt the make that status update when a remote ingest starts, ie user does not
                	* have to reload the page, after saving settings in the GUI and waiting for a remote ingest to start.
                	*/
                    // myStatusTimer.cancel(); // 8-2012
                    if ( tryingToStop ){
                    	myStatusTimer.cancel(); // 8-2012
                    	displayScriptRunningChar( "*" ); // 8-2012
                        appendToStatusBox("Batch Ingest Stopped");
                    }
                    if ( ingestRunning ){
                        ingestRunning = false;
                        myStatusTimer.cancel(); // 8-2012
                        displayScriptRunningChar( "*" ); // 8-2012
                        DateTimeFormat dateFormat = DateTimeFormat.getFormat( timeHourMinuteFormat );
                        Date stopTime = new Date();
                        String stopString = "Stop time: " + dateFormat.format( stopTime ).toLowerCase();
                        appendToStatusBox( stopString );
                    }
                    tryingToStop = false;
                    // displayScriptRunningChar( "*" ); // 8-2012
                    RootPanel.get("bottonPanel").clear();
                    RootPanel.get("bottonPanel").add( getIngestCompletePanel( myBatchSet ) );             
                }
            }
      };

      threadMangerService.isBatchSetRunning( batch_set, callBackResults );    
  }

  protected void stopBatchIngest( String batch_set ){
      
      BatchIngestThreadManagerServiceAsync threadMangerService = this.getThreadManagerService();
      final String myBatchSet = batch_set;
      this.tryingToStop = true;
      this.stopBtnPushedTime = new Date().getTime();
      
      AsyncCallback callBackResults = new AsyncCallback() {
          public void onFailure(Throwable exception) {
              displayException( exception );  
            }
            public void onSuccess(Object result){
                // nop
            }
      };

      threadMangerService.stopBatchIngest( batch_set, callBackResults );    
  }
  
  protected void forceImediateStopOfBatchIngest( String batch_set ){

      BatchIngestThreadManagerServiceAsync threadMangerService = this.getThreadManagerService();
      
      AsyncCallback callBackResults = new AsyncCallback() {
          public void onFailure(Throwable exception) {
              displayException( exception );  
            }
            public void onSuccess(Object result){
               displayStatusBar( "New batch set enabled" );
               Window.open( contextURL+"batchIngest.htm", "_self", null);
            }
      };
      
      threadMangerService.forceHardStop( batch_set, callBackResults );  
  }
  
  protected String getBatchSetName(){
//
//      String batchSetIdElement = DOM.getElementById("batchSetId").toString();
//      int startOfValue=batchSetIdElement.indexOf(">")+1;
//      int endOfValue=batchSetIdElement.lastIndexOf("<");
//      String batch_set = DOM.getElementById("batchSetId").toString().substring(startOfValue, endOfValue);
      
/*     NOTE: this has to set manually in the HTML page! as such.
       <script language="javascript">
          var sessionAttributes = {
              batch_set: "<%=batch_set%>"
           };
       </script>
*/
      
      Dictionary parameters = Dictionary.getDictionary("sessionAttributes");
      String batch_set = parameters.get("batch_set"); 
      
      return batch_set;
  }
  
  protected Panel getIngestCompletePanel(String batch_set){

      final String myBatch_set = batch_set;
      
      final Button viewIngestReportBtn = new Button("View Ingest Report"); // TBD magic#
      final Button viewPidReportBtn = new Button("View PID Report");
      final Button enableNewBatchBtn = new Button("Enable New Batch");
      
      viewIngestReportBtn.addClickListener(new ClickListener() {
          public void onClick(Widget sender) { 
              displayStatusBar( "Opening Batch Ingest Report");
              Window.open( CONTEXT_URL+"batchIngestReport.htm?"+BATCH_SET_REQUEST_PARAM+"="+myBatch_set, "_blank", null); 
            }
          });
      viewPidReportBtn.addClickListener(new ClickListener() {
          public void onClick(Widget sender) {
              displayStatusBar("Opening Batch PID Report");
              Window.open( CONTEXT_URL+"batchIngestPidReport.htm?"+BATCH_SET_REQUEST_PARAM+"="+myBatch_set, "_blank", null);
            }
          });
      enableNewBatchBtn.addClickListener(new ClickListener() {
          public void onClick(Widget sender) {
              displayStatusBar( "Enabling new Batch Ingest for "+myBatch_set );
              enableNewBatch( myBatch_set );
            }
          });
      
      VerticalPanel   statusBtnPanel = new VerticalPanel();
      statusBtnPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
      statusBtnPanel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );   
     
      statusBtnPanel.add(viewIngestReportBtn);
      statusBtnPanel.add(viewPidReportBtn);
     
      statusBtnPanel.add(new HTML( "<br>" ));
        
      /*
       * Normally we don't want to display the enable new batch button for remote or background tasks, since they will be re-enabled
       * automatically UNLESS they were stopped by the user, in which case the user has to re-enable them.
       */
      if ( ! ( myBatch_set.contains( REMOTE_TASK_NAME_SUFFIX ) || myBatch_set.contains( BACKGROUND_TASK_NAME_SUFFIX  ) ) )
      {
    	  statusBtnPanel.add(enableNewBatchBtn);
      }
      else {
    	  if ( userStopptedStatus ) 
    	  {
    		  statusBtnPanel.add(enableNewBatchBtn);
    	  }
    	  
      }
  
      statusBtnPanel.setStyleName("statusBtnPanel");
      
      return statusBtnPanel;
  }
  

  protected Panel getIngestRunningPanel( String batch_set ){

      final Button stopIngestBtn = new Button("Stop Ingest"); // TBD magic#
      final String myBatch_set = batch_set;
      
      stopIngestBtn.addClickListener(new ClickListener() {
          public void onClick(Widget sender) {

              DateTimeFormat dateFormat = DateTimeFormat.getFormat( timeHourMinuteFormat );
              Date startTime = new Date();
              
              String stopString = dateFormat.format( startTime ).toLowerCase();
              
              appendToStatusBox("Stopping Batch Ingest: "+stopString);
              stopBatchIngest( myBatch_set );
            }
          });
      
      VerticalPanel   statusBtnPanel = new VerticalPanel();
      statusBtnPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
      statusBtnPanel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
      statusBtnPanel.add(stopIngestBtn);
      statusBtnPanel.setStyleName("statusBtnPanel");
      
      return statusBtnPanel;
  }
  
  protected Panel getTryingToStopPanel( String batch_set ){

      final Button forceStopIngestBtn = new Button("FORCE STOP"); // TBD magic#
      final String myBatch_set = batch_set;
      
      forceStopIngestBtn.addClickListener(new ClickListener() {
          public void onClick(Widget sender) {
              DateTimeFormat dateFormat = DateTimeFormat.getFormat( timeHourMinuteFormat );
              Date startTime = new Date();
          
              String haltString = dateFormat.format( startTime ).toLowerCase();
          
              appendToStatusBox("Forcing Batch Ingest STOP!: "+haltString);
              forceImediateStopOfBatchIngest( myBatch_set );
            }
          });
      
      VerticalPanel   statusBtnPanel = new VerticalPanel();
      statusBtnPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
      statusBtnPanel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
      
      if ( (new Date()).getTime() > stopBtnPushedTime + DELAY_BEFRE_ABORT_BTN_IN_MILLISECONDS ) {
          statusBtnPanel.add(forceStopIngestBtn);
      }
      
      statusBtnPanel.setStyleName("statusBtnPanel");
      
      return statusBtnPanel;
  }

  protected void removeButtonPanel(){
      
      RootPanel.get("bottonPanel").clear();
  }
  
  private void updateScriptRunningChar(){
      displayScriptRunningChar( getNewScriptRunningChar() );
  }
  
  private void displayScriptRunningChar( String displayChar ){

      RootPanel.get("scriptRunningIndicator").clear();
      RootPanel.get("scriptRunningIndicator").add( new HTML( displayChar ) );
  }
  
  private String getNewScriptRunningChar(){
      
      String dummy = "*";

      if ( flowBarInt == 0 ){ this.flowBarInt++; return "|";  }
      if ( flowBarInt == 1 ){ this.flowBarInt++; return "/";  }
      if ( flowBarInt == 2 ){ this.flowBarInt++; return "-"; }
      if ( flowBarInt == 3 ){ flowBarInt=0; return "\\"; }
      
      return dummy;
  }
} // BatchIngestStatus
