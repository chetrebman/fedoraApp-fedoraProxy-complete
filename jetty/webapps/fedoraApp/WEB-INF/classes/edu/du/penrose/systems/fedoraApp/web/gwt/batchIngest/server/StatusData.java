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

package edu.du.penrose.systems.fedoraApp.web.gwt.batchIngest.server;

/**
 * Contains all status data about a particular active batch set. This is useful
 * for passing to the client (web browser) with GWT RPC.
 * 
 * @author chet.rebman
 * @see edu.du.penrose.systems.fedoraApp.web.gwt.batchIngest.client.BatchIngestStatus
 * 
 */
public class StatusData implements com.google.gwt.user.client.rpc.IsSerializable {

    /**
     * 
     */
 //   private static final long serialVersionUID = 1L;
    
    private String batchSetName = "No Batch Set";
    private String status       = "No Status";
    private boolean isRunning   = false;
    private boolean stoppedByUser = false;
    
    private String institution   = "No institution";
    private boolean performingUpdates = false;

	private int completed = -1;
    private int failed    = -1;
	private int totalFilesAddedSuccess    = -1;
	private int totalFilesAddedFailed    = -1;
	private int totalFilesUpdatedSuccess = -1;
	private int totalFilesUpdatedFailed  = -1;
	private String islandoraCollection   = "none";
	private String islandoraContentModel = "none";
    
    public StatusData(){   
      //  System.out.println("Make new StatusData");
    }
    
    /**
     * @return the completed
     */
    public int getCompleted() {
        return completed;
    }
    /**
     * @param completed the completed to set
     */
    public void setCompleted(int completed) {
        this.completed = completed;
    }
    /**
     * @return the failed
     */
    public int getFailed() {
        return failed;
    }
    /**
     * @param failed the failed to set
     */
    public void setFailed(int failed) {
        this.failed = failed;
    }
    /**
     * @return the batch_set
     */
    public String getBatchSetName() {
        return batchSetName;
    }
    /**
     * @param batchSetName the batch_set to set
     */
    public void setBatchSetName(String batchSetName ) {
        this.batchSetName = batchSetName;
    }
    /**
     * @return the batch_set_status
     */
    public String getStatus() {
        return status;
    }
    /**
     * @param status the batch_set_status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }
    /**
     * @return the institution
     */
    public String getInstitution() {
        return institution;
    }
    /**
     * @param institution the institution to set
     */
    public void setInstitution(String institution) {
        this.institution = institution;
    }
    /**
     * @return the isRunning
     */
    public boolean isRunning() {
        return isRunning;
    }
    /**
     * @param isRunning the isRunning to set
     */
    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

	public int getTotalFilesAddedSuccess() {
		return totalFilesAddedSuccess;
	}

	public void setTotalFilesAddedSuccess(int totalFilesAddedSuccess) {
		this.totalFilesAddedSuccess = totalFilesAddedSuccess;
	}

	public int getTotalFilesAddedFailed() {
		return totalFilesAddedFailed;
	}

	public void setTotalFilesAddedFailed(int totalFilesAddedFailed) {
		this.totalFilesAddedFailed = totalFilesAddedFailed;
	}

	public int getTotalFilesUpdatedSuccess() {
		return totalFilesUpdatedSuccess;
	}

	public void setTotalFilesUpdatedSuccess(int totalFilesUpdatedSuccess) {
		this.totalFilesUpdatedSuccess = totalFilesUpdatedSuccess;
	}

	public int getTotalFilesUpdatedFailed() {
		return totalFilesUpdatedFailed;
	}

	public void setTotalFilesUpdatedFailed(int totalFilesUpdatedFailed) {
		this.totalFilesUpdatedFailed = totalFilesUpdatedFailed;
	}

	public String getIslandoraCollection() {
		return islandoraCollection;
	}

	public void setIslandoraCollection(String islandoraCollection) {
		this.islandoraCollection = islandoraCollection;
	}

	public String getIslandoraContentModel() {
		return islandoraContentModel;
	}

	public void setIslandoraContentModel(String islandoraContentModel) {
		this.islandoraContentModel = islandoraContentModel;
	}

	public boolean isBatchIsUpdates() {
		return performingUpdates;
	}

	public void setBatchIsUpdates(boolean performingUpdates ) {
		this.performingUpdates = performingUpdates;
	}

	public void setstoppedByUser(boolean wasStoppedByUser ) 
	{
		this.stoppedByUser = wasStoppedByUser;
	}

	public boolean isStoppedByUser()
	{
		return this.stoppedByUser;
	}
    
} // StatusData
