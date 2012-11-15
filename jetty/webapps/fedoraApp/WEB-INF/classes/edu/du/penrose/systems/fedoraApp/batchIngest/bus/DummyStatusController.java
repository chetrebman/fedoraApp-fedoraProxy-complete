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
