package edu.du.penrose.systems.fedoraApp.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.du.penrose.systems.etd.Batch;
import edu.du.penrose.systems.etd.EtdCrosswalk;
import edu.du.penrose.systems.etd.EtdUtils;
import edu.du.penrose.systems.etd.Zip;
import edu.du.penrose.systems.exceptions.FatalException;
import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraApp.ProgramFileProperties;
import edu.du.penrose.systems.fedoraApp.ProgramProperties;
import edu.du.penrose.systems.fedoraApp.batchIngest.bus.BatchIngestController;
import edu.du.penrose.systems.fedoraApp.batchIngest.bus.BatchIngestThreadManager;
import edu.du.penrose.systems.fedoraApp.batchIngest.bus.DummyStatusController;
import edu.du.penrose.systems.fedoraApp.batchIngest.bus.FedoraAppBatchIngestController;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestOptions;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestOptions.INGEST_THREAD_TYPE;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.TransformMetsData;
import edu.du.penrose.systems.fedoraApp.util.FedoraAppUtil.INGEST_TYPE;
import edu.du.penrose.systems.fedoraApp.web.gwt.batchIngest.server.StatusData;


/**
 * Reads the configuration file in batch_space/codu/mixed/ which contains the work directories...<br>
 * ETD_ZIP_DIR the location of zip files depoited with the ADR by proquest.<br>
 * ETD_TEMP_DIR this is where files are unzipped and mods/mets files built.<br>
 * ETD_XML_DIR the output directory for the final ingest files.<br>
 * ETD_PCO_DIR this where the digital objects are stored ie PDF's<br>
 * ETD_FAILED_DIR files that fail to ingest are stored here.<br>
 * ETD_EMBARGOED_DIR Embargoed files are not ingested the simply moved here. They be reprocessed the next time this program is run.<br>
 * ETD_COMPLETED after successful ingest, files are placed here<br><br>
 * The proquest files are unzipped and moved to the completed folder. The unzipped files are crosswalked to MODS, the wrapped in METS. All temporary files are deleted. Finally the METS files are ingested.
 * 
 * @author chet
 *
 */
public class EtdWorker extends IngestWorker 
{
	private static boolean running = false; // a simple semaphore
	
	private ProgramProperties etdProperties  = null;
	private String zipFolderName;
	private String outFolderName;
	private String xmlFolderName;
	private String pcoFolderName;
	private String completedFolderName;
	private String embargoedFolderName;
	private String failedFolderName;

	private Map<String, String> yearCollectionArray = null;
	
	private String ETD_content_model = null;
	
	public EtdWorker(String batchSetName) {
		super(batchSetName);
	}

	
	/*
	 * We will look for ETD zip files, if found, extract, build METS containing collection names, place them in the
	 * institution/mixed directory and then our parent IngestWorker
	 * 
	 * @see edu.du.penrose.systems.fedoraApp.tasks.IngestWorker#doWork()
	 */
	public void doWork()
	{
		synchronized (this) 
		{
			if ( isRunning() ){
				return;
			}
			else {
				setRunning( true );
			}
		}
		try
		{
			StatusData currentStatus = null;
			if ( BatchIngestThreadManager.isBatchSetThreadExists( this.getName() ) )
			{
				currentStatus = BatchIngestThreadManager.getAllBatchSetStatus( this.getName() );

				if ( currentStatus != null && currentStatus.isRunning() )
				{ 
					/**
					 * A batch is currently running, so do nothing. We do not confirm that the message was received so it will be sent again.
					 */
					return; 	 
				}  
			}

			/*
			 * If the user stoped a remote ingest, we don't wan't to re-enable it! The user will need to re-enable it through
			 * the GUI.
			 */
			if ( currentStatus != null && currentStatus.isStoppedByUser() )
			{
				return;
			}
			
			
			// this is just a temporary ingester so that we can display status
			String fedoraContext = institution;		
			BatchIngestController tempController = new DummyStatusController( institution, batchSet, INGEST_THREAD_TYPE.BACKGROUND );
			BatchIngestThreadManager.setBatchSetThread( this.getName(), tempController );
			
			this.initProgramProperties();
			
			// First let's move any embargoed files to the xml directory so that they get processed again.

			BatchIngestThreadManager.setBatchSetStatus( this.getName(), "Moving Embargoed files back to input directory to be reproccessed" );
			
			File embargoDir = new File (this.embargoedFolderName ); 
			for ( File singleFile:  embargoDir.listFiles() )
			{
				singleFile.renameTo( new File( this.outFolderName, singleFile.getName() ) );
			}
			
			BatchIngestThreadManager.setBatchSetStatus( this.getName(), "Splitting ETD zip files" );
			
			Zip ZipObj = new Zip();
			EtdUtils etdUtilsObj = new EtdUtils();
			EtdCrosswalk etdCrosswalkObj = new EtdCrosswalk() ;
			Batch BatchObj = new Batch();

			ArrayList<File> zipFilesList = (ArrayList<File>) ZipObj.getZipFiles( this.zipFolderName );
			ZipObj.extract( zipFilesList, this.outFolderName );

			BatchIngestThreadManager.setBatchSetStatus( this.getName(), "Move the recently un-zipped zip files to the completed directory" );
			
			for ( File file: zipFilesList )
			{
				file.renameTo( new File( this.completedFolderName, file.getName() ) );
			}
			 
			// get all directories just created ( the new proquest zip format may not have these directories)
			Object dirs = etdUtilsObj.getDirectories( this.outFolderName );

			Object files = etdUtilsObj.getEtdFilesFromDirectory( dirs, this.outFolderName );

			// move the etd files contained in the directories to the top level directory
			BatchIngestThreadManager.setBatchSetStatus( this.getName(), "Move any ETD files contained within subdirectories to top directory" );
			etdUtilsObj.copyEtdFiles( files, this.outFolderName );

			// move the PCOs into the {batchSet}/files folder
			BatchIngestThreadManager.setBatchSetStatus( this.getName(), "Move PCOs to the files ingest directory" );
			Object pco = etdUtilsObj.getEtdPco( this.outFolderName );
			etdUtilsObj.copyEtdPco( pco, this.pcoFolderName );

			Object xml = etdUtilsObj.getEtdXml( this.outFolderName );

			BatchIngestThreadManager.setBatchSetStatus( this.getName(), "Crosswalking unziped ETD xml files" );
			// cross walk the progques xml file and store it in the {batchSet}/mets/new folder
			etdCrosswalkObj.etdToMods( this.pcoFolderName,  this.xmlFolderName, xml, this.embargoedFolderName, this.failedFolderName, this.yearCollectionArray, this.ETD_content_model );

			BatchIngestThreadManager.setBatchSetStatus( this.getName(), "Deleting temporary ETD files" );
			
			etdUtilsObj.deleteFiles( this.outFolderName );
			etdUtilsObj.deleteDirectories( dirs );
			
			BatchIngestThreadManager.setBatchSetStatus( this.getName(), "Checking to see if there is anything to ingest" );
			
			((DummyStatusController) tempController).setDone();
			
			boolean newBatchStarted = this.tryToStartNewBatchThread();

			if ( newBatchStarted ){
				this.logger.info( "New "+batchSet+" batch started" );
			}
		} catch (FatalException e) 
		{
			logger.error( e.getMessage() );
		}
		finally 
		{
			setRunning( false );
		}
	}
	
	protected void initProgramProperties() throws FatalException
	{	
		super.initProgramProperties();
		
		File tempFile = new File( FedoraAppConstants.getServletContextListener().getInstituionURL().getFile() + "/" + this.institution + "/" + this.batchSet + "/" +this.batchSet+FedoraAppConstants.BACKGROUND_TASK_NAME_SUFFIX+".properties" );

		this.etdProperties = new ProgramFileProperties( tempFile );
		
		String temp = this.etdProperties.getProperty( FedoraAppConstants.ETD_ZIP_DIR_PROPERTY );
		if ( temp.endsWith("/") || temp.endsWith("\\") ){} else { temp = temp + File.separatorChar; }			
		this.zipFolderName = temp;
		
		temp = this.etdProperties.getProperty( FedoraAppConstants.ETD_TEMP_DIR_PROPERTY );
		if ( temp.endsWith("/") || temp.endsWith("\\") ){} else { temp = temp + File.separatorChar; }	
		this.outFolderName = temp;
		
		temp = this.etdProperties.getProperty( FedoraAppConstants.ETD_XML_DIR_PROPERTY );
		if ( temp.endsWith("/") || temp.endsWith("\\") ){} else { temp = temp + File.separatorChar; }	
		this.xmlFolderName = temp;
		
		temp = this.etdProperties.getProperty( FedoraAppConstants.ETD_PCO_DIR_PROPERTY );
		if ( temp.endsWith("/") || temp.endsWith("\\") ){} else { temp = temp + File.separatorChar; }	
		this.pcoFolderName = temp;

		temp = this.etdProperties.getProperty( FedoraAppConstants.ETD_EMBARGOED_DIR_PROPERTY );
		if ( temp.endsWith("/") || temp.endsWith("\\") ){} else { temp = temp + File.separatorChar; }	
		this.embargoedFolderName = temp;
		
		temp = this.etdProperties.getProperty( FedoraAppConstants.ETD_FAILED_DIR_PROPERTY );
		if ( temp.endsWith("/") || temp.endsWith("\\") ){} else { temp = temp + File.separatorChar; }	
		this.failedFolderName = temp;
		
		temp = this.etdProperties.getProperty( FedoraAppConstants.ETD_COMPLETED_DIR_PROPERTY );
		if ( temp.endsWith("/") || temp.endsWith("\\") ){} else { temp = temp + File.separatorChar; }	
		this.completedFolderName = temp;
		
		/*
		 * Split property of type ETD_YEAR_COLLECTION_7  = 2008_M_codu:62282
		 */
		String[] year_collection_names = this.etdProperties.getPropertyNamesAsArray();	
		this.yearCollectionArray = new HashMap<String, String>();
		for ( String key: year_collection_names )
		{
			if ( key.startsWith( FedoraAppConstants.ETD_YEAR_COLLECTION_PROPERTY ) )
			{
				String[] tempWork = this.etdProperties.getProperty( key ).split( "_" );
				
				// key would be 2008_M and the value is the collection codu:62282
				this.yearCollectionArray.put( tempWork[0] + "_" + tempWork[1], tempWork[ 2 ].trim() );
			}
		}	
		
		this.ETD_content_model =  this.etdProperties.getProperty( FedoraAppConstants.ETD_CONTENT_MODEL_PROPERTY );
	}


	private static synchronized boolean isRunning() {
		return EtdWorker.running;
	}


	private static synchronized void setRunning(boolean running) {
		EtdWorker.running = running;
	}
    
}
