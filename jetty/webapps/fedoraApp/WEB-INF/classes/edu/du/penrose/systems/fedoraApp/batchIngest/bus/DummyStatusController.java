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

import java.io.File;

import edu.du.penrose.systems.exceptions.FatalException;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestOptions;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestOptions.INGEST_THREAD_TYPE;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.ThreadStatusMsg;

/**
 * Creates a fake ingestController and is used in rare cases when status needs to be displayed through the BatchThreadManger without
 * an actual BatchIngestController thread. 
 * 
 * If this class is current contained in a BatchThreadManger (which is the point) it will block any task of the same name from
 * running, until done() is called. When this controller is marked as done, it can be removed from the BatchThreadManger 
 * and a new of the same batchSet name can added.
 * 
 * @author chet
 *
 */
public class DummyStatusController extends BatchIngestController {

	public DummyStatusController(String institutionName, String batchSet, INGEST_THREAD_TYPE threadType ) throws FatalException 
			
	{
		super( institutionName, batchSet, BatchIngestOptions.getGenericBatchOptions() );
		
		/*
		 * Institution and batchSet and threadType are needed to create the correct thread name
		 */
		this.batchOptions.setInstitution( institutionName );
		this.batchOptions.setBatchSet( batchSet );
		
		/*
		 * This ingestType is needed so that the correct thread name is used it ie xxx or xxx_REMOTE or xxx_TASK
		 */
		this.batchOptions.setIngestThreadType( threadType );
	}

	/**
	 * If this class is current contained in a BatchThreadManger (which is the point) it will block any task of the same name from
	 * running, until this method is called. When this controller is marked as done, it can be removed from the BatchThreadManger 
	 * and a new of the same batchSet name can added.
	 */
	public void setDone()
	{
		this.setDone( true );
	}
	
	@Override
	protected void callFileSplitter(BatchIngestOptions ingestOptions,
			ThreadStatusMsg threadStatus, File fileToSplit,
			String MetsNewDirectory, String MetsUpdatesDirectory)
			throws Exception 
	{ 	}

	@Override
	protected void runBatch() throws FatalException {

	}

	@Override
	public int getTotalFilesAddedSuccess() {
		return 0;
	}

	@Override
	protected void setTotalFilesAddedSuccess(int totalFilesAddedSuccess) {

	}

	@Override
	public int getTotalFilesAddedFailed() {
		return 0;
	}

	@Override
	protected void setTotalFilesAddedFailed(int totalFilesAddedFailed) {
	}

	@Override
	public int getTotalFilesUpdatedSuccess() {
		return 0;
	}

	@Override
	protected void setTotalFilesUpdatedSuccess(int totalFilesUpdatedSuccess) {

	}

	@Override
	public int getTotalFilesUpdatedFailed() {
		return 0;
	}

	@Override
	protected void setTotalFilesUpdatedFailed(int totalFilesUpdatedFailed) {

	}

	@Override
	public boolean isBatchIsUpdates() {
		return false;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
