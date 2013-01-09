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

package edu.du.penrose.systems.fedoraApp.web.bus;

import java.util.*;
import java.net.*;
import java.io.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.view.RedirectView;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraApp.ProgramProperties;

import edu.du.penrose.systems.exceptions.FatalException;
import edu.du.penrose.systems.fedoraApp.batchIngest.bus.BatchIngestController;
import edu.du.penrose.systems.fedoraApp.batchIngest.bus.FedoraAppBatchIngestController;
import edu.du.penrose.systems.fedoraApp.batchIngest.bus.BatchIngestThreadManager;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestOptions;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.TransformMetsData;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestOptions.FORM_ACTION;
import edu.du.penrose.systems.fedoraApp.util.FedoraAppUtil;
import edu.du.penrose.systems.fedora.ResourceIndexUtils;
import edu.du.penrose.systems.fedora.client.Administrator;

//import fedora.client.objecteditor.Util;

import edu.du.penrose.systems.util.FileUtil;
import edu.du.penrose.systems.util.MyServletContextListener;

/**
 * Batch ingest form controller. This controller loads user options from a
 * properties file and saves them in the command object. This class also saves
 * the user options to the properties file.
 * 
 * @see BatchIngestOptions
 * @author chet.rebman
 *
 */
public class BatchIngestFormController extends SimpleFormController {


	/** Logger for this class and subclasses */
	protected final Log logger = LogFactory.getLog(getClass());

	/**
	 * Get a copy of the initial form command object (BatchIngestOptions). Loading
	 * the institution drop-down list based on the contents of the institution
	 * directory. Initialize the batch set drop down list to empty.
	 */
	protected Object formBackingObject(HttpServletRequest request)
	throws Exception {

		BatchIngestOptions commandObj = new BatchIngestOptions();


		File institutionDirectory = null;
		String institutionDirectoryPath = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.BATCH_INGEST_TOP_FOLDER_URL_PROPERTY );
		URL institutionURL = new URL( institutionDirectoryPath );
		if ( institutionURL.getProtocol().toLowerCase().equals( "file" ) ) 
		{ // TBD only file protocol currently supported.

			institutionDirectory = new File( institutionURL.getFile() );
		}
		else {
			throw new FatalException( "Unsupported Protocol for top batch ingest folder (institution directory" );
		}

		// load institution drop down list with directory contents.
		Map<String, String> institutionMap = new HashMap<String, String>();

		if ( ! institutionDirectory.exists() )
		{
			throw new FatalException( "Invalid Directory:"+institutionDirectory.toString() );
		}
		String[] institutionList = institutionDirectory.list();
		for ( int i =0; i<institutionList.length; i++ ) {
			if ( institutionList[i].compareToIgnoreCase("readme.txt") == 0 )
			{
				continue;
			}
			institutionMap.put( institutionList[i], institutionList[i] );
		}

		commandObj.setInstitutionMap( institutionMap );

		// initialize batch set drop-down list to empty.
		Map<String, String> batchSetMap = new HashMap<String, String>();
		commandObj.setBatchSetMap( batchSetMap );

		// initialize fedora collection drop-down list to empty.

		commandObj.setFedoraCollectionMap( null );

		this.enableIngestButtonIfInputsValid(request, commandObj );

		return commandObj;
	}

	/**
	 * Process the form submission checking it for the request type. IF this
	 * is a 'ingestAll' or 'viewStatus' request forward to the submit() action.'
	 * <br>
	 * Otherwise update the instituion and batch set dropdown lists.
	 * 
	 */
	public ModelAndView processFormSubmission(HttpServletRequest request,
			HttpServletResponse response,
			Object command,
			BindException errors)
	throws Exception {

		BatchIngestOptions batchIngestCommand = (BatchIngestOptions) command;

		String submitTypeString = batchIngestCommand.getSubmitId();
		BatchIngestOptions.FORM_ACTION submitType = BatchIngestOptions.FORM_ACTION.INITIAL;

			// look for submit button pressed
		if ( request.getParameter( "saveSettings" ) != null ) {
			submitType = FORM_ACTION.SAVE_SETTINGS; }
		
		if ( request.getParameter( "ingestAll"  ) != null ){ 
			submitType = FORM_ACTION.INGEST_SUBMIT; }
		
		if ( request.getParameter( "viewStatus" ) != null ){ 
			submitType = FORM_ACTION.VIEW_ALL_SUBMIT; }
		
		if ( submitType ==  BatchIngestOptions.FORM_ACTION.INITIAL)
		{
				// look for change in one of the drop-down menus
			if ( submitTypeString.equals( BatchIngestOptions.INSTITUTION_SUBMIT_ID   )){ submitType = FORM_ACTION.INSTITUTION_CHANGE;   }
			if ( submitTypeString.equals( BatchIngestOptions.BATCH_SET_SUBMIT_ID     )){ submitType = FORM_ACTION.BATCH_SET_CHANGE;     }
			if ( submitTypeString.equals( BatchIngestOptions.COLLECTION_SUBMIT_ID    )){ submitType = FORM_ACTION.COLLECTION_CHANGE;    }
			if ( submitTypeString.equals( BatchIngestOptions.CONTENT_MODEL_SUBMIT_ID )){ submitType = FORM_ACTION.CONTENT_MODEL_CHANGE; }
			if ( submitTypeString.equals( BatchIngestOptions.SPLIT_CB_SUBMIT_ID      )){ submitType = FORM_ACTION.SPLIT_CHECKBOX_CHANGE;}
			if ( submitTypeString.equals( BatchIngestOptions.ALLOW_SIMULTANIOUS_MANUAL_AND_REMOTE_INGEST_CB )){ submitType = FORM_ACTION.MANUALREMOTE_CHECKBOX_CHANGE;}
			if ( submitTypeString.equals( BatchIngestOptions.WORK_FILE_SUBMIT_ID      )){ submitType = FORM_ACTION.WORK_FILE_CHANGE;    }
	//		if ( submitTypeString.equals( BatchIngestOptions.ALLOW_SIMULTANIOUS_MANUAL_AND_REMOTE_INGEST_CB     )){ submitType = FORM_ACTION.ALLOW_SIMULTANEOUS_CHANGE;    }
				
		}
			
		switch ( submitType ){
		case INGEST_SUBMIT:
			if ( batchIngestCommand.getInstitution().equals( FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE ) 
					|| batchIngestCommand.getBatchSet().equals( FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE ) ) {

				errors.rejectValue( "batchSet", "batchIngest.error.invalidBatchSet" ); 

				return this.showForm( request, response, errors );
			}
			return this.onSubmit(request, response, command, errors);
		case VIEW_ALL_SUBMIT:
			if ( batchIngestCommand.getInstitution().equals( FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE ) 
					|| batchIngestCommand.getBatchSet().equals( FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE ) ) {

				errors.rejectValue( "batchSet", "batchIngest.error.invalidBatchSet" ); 

				return this.showForm( request, response, errors ); 
			}
			return this.onSubmit(request, response, command, errors);
		case INSTITUTION_CHANGE:
			return this.institutionFormChange( request, response, batchIngestCommand, errors );
		case BATCH_SET_CHANGE:
			this.batchSetFormChange( request, response, batchIngestCommand, errors );
			this.enableIngestButtonIfInputsValid( request, batchIngestCommand );
			return this.showForm( request, response, errors );
//		case ALLOW_SIMULTANEOUS_CHANGE:
//			this.batchSetFormChange( request, response, batchIngestCommand, errors );
		case COLLECTION_CHANGE:
			this.handleCollectionChange(request, response, batchIngestCommand, errors);
			this.enableIngestButtonIfInputsValid(request, batchIngestCommand);
			return this.showForm( request, response, errors );
		case CONTENT_MODEL_CHANGE:
			if ( batchIngestCommand.getFedoraCollection().equals( FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE ) 
					^ batchIngestCommand.getFedoraContentModel().equals( FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE ) ) 
			{
					// it is ok if both are not selected, since it is not required, otherwise it is an error.
				errors.rejectValue( "fedoraContentModel", "batchIngest.error.invalidContentModel" ); 
			}
			
			this.enableIngestButtonIfInputsValid( request, batchIngestCommand );
			return this.showForm( request, response, errors );		
		case WORK_FILE_CHANGE:
			this.enableIngestButtonIfInputsValid(request, batchIngestCommand);
			return this.showForm( request, response, errors );
		case SPLIT_CHECKBOX_CHANGE:
			if ( ! batchIngestCommand.isSplitXMLinWorkDirToMets() ){ 
				batchIngestCommand.setWorkFile(  FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE ); 
			}
			this.enableIngestButtonIfInputsValid(request, batchIngestCommand);		
			return this.showForm( request, response, errors );
		case MANUALREMOTE_CHECKBOX_CHANGE:
			this.enableIngestButtonIfInputsValid( request, batchIngestCommand );
			return this.showForm( request, response, errors );
		case ISLANDORA_CHECKBOX_CHANGE:
			this.enableIngestButtonIfInputsValid( request, batchIngestCommand );
			return this.showForm( request, response, errors );
		case SAVE_SETTINGS:
			this.saveProperties( batchIngestCommand );
			return this.showForm( request, response, errors );
		default:
			return this.showForm( request, response, errors ); 
		}


	} // processFormSubmission

	
	private ModelAndView institutionFormChange(HttpServletRequest request,
			HttpServletResponse response,
			BatchIngestOptions batchIngestCommand, BindException errors) throws Exception 
		{
		batchIngestCommand.setWorkFileMap( new HashMap<String, String>() );
		
		batchIngestCommand.setBatchSet( FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE );
		
		batchIngestCommand.setFedoraContentModel( FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE );	
		batchIngestCommand.setFedoraCollection( FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE );
		
		batchIngestCommand.setFedoraCollectionMap( null );
		batchIngestCommand.setFedoraContentModelMap( null );
		
		this.enableIngestButtonIfInputsValid(request, batchIngestCommand);
		
		if ( batchIngestCommand.getInstitution().equals( FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE ) ) {

			// there is no institution selected so re-initialize the batchSet and collection. NOTE collection only used in an islandora ingest

			Map<String, String> batchSetMap = new HashMap<String, String>();
			batchIngestCommand.setBatchSetMap( batchSetMap );
	
			batchIngestCommand.setFedoraCollectionMap( null  );

			return this.showForm( request, response, errors ); 
		}
		else {

			File institutionDirectory = null;
			String institutionDirectoryPath = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.BATCH_INGEST_TOP_FOLDER_URL_PROPERTY );
			URL institutionURL = new URL( institutionDirectoryPath );
			if ( institutionURL.getProtocol().toLowerCase().equals( "file" ) ) {

				institutionDirectory = new File( institutionURL.getFile() );
			}
			else {
				throw new FatalException( "Unsupported Protocol for top batch ingest folder (institution directory)" );
			}

			File batch_set = new File( institutionDirectory.getCanonicalPath()+File.separatorChar+batchIngestCommand.getInstitution() );

			Map<String, String> batchSetMap = new HashMap<String, String>();
			String[] batchSetList = batch_set.list();
			for ( int i =0; i<batchSetList.length; i++ ) 
			{
				if ( batchSetList[i].compareToIgnoreCase("readme.txt") == 0 )
				{
					continue;
				}
				batchSetMap.put( batchSetList[i], batchSetList[i] );
			}
			batchIngestCommand.setBatchSetMap( batchSetMap );

			// is this an islandora ingest?


			if ( FedoraAppUtil.getIngestType() == FedoraAppUtil.INGEST_TYPE.ISLANDORA )
			{		
				// this will make sure that the collection list is initialized.
				this.handleCollectionChange(request, response, batchIngestCommand, errors);
			}
		}

		return this.showForm( request, response, errors );
	}

	/**
	 * Load settings from properties file.
	 * 
	 * @param request
	 * @param response
	 * @param batchIngestCommand
	 * @param errors
	 * @throws Exception
	 */
	private void batchSetFormChange( HttpServletRequest request, HttpServletResponse response, BatchIngestOptions batchIngestCommand, BindException errors) throws Exception
	{	

		// if we starting from the beginning, remove any previous batch set (used by batchIngest.jsp
		if ( batchIngestCommand.getBatchSet().equals( FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE ) )
		{
			errors.rejectValue( "batchSet", "batchIngest.error.invalidBatchSet" ); 
		}
		else 
		{
			File institutionDirectory = null;
			String institutionDirectoryPath = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.BATCH_INGEST_TOP_FOLDER_URL_PROPERTY );
			URL institutionURL = new URL( institutionDirectoryPath );
			if ( institutionURL.getProtocol().toLowerCase().equals( "file" ) ) 
			{ // TBD only file protocol currently supported.

				institutionDirectory = new File( institutionURL.getFile() );
			}
			else {
				throw new FatalException( "Unsupported Protocol for top batch ingest folder (institution directory" );
			}

			// load the .properties file for this batch set
			String institution = batchIngestCommand.getInstitution();
			String batchSet   = batchIngestCommand.getBatchSet();
			String batchSetOptionsLocation = institutionDirectoryPath + institution +"/"+ batchSet + "/"+ batchIngestCommand.getBatchSet()+".properties";
			ProgramProperties optionsPropertiesFile = ProgramProperties.getInstance( new URL( batchSetOptionsLocation ) );

			batchIngestCommand = FedoraAppUtil.loadIngestFileOptions( optionsPropertiesFile, batchIngestCommand ); 
			
			batchIngestCommand.setInstitution( institution );
			batchIngestCommand.setBatchSet( batchSet );
			
			String collectionPID = batchIngestCommand.getFedoraCollection();
			String modelPID      = batchIngestCommand.getFedoraContentModel();
			
			if ( collectionPID.equals( "" ) ){ collectionPID =  FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE; }
			if ( modelPID.equals( "" ) ){           modelPID =  FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE; }
			batchIngestCommand.setFedoraCollection( collectionPID );
			batchIngestCommand.setFedoraContentModel( modelPID );
		
			// load work drop-down list with directory contents.

			String workDirectoryName = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.BATCH_INGEST_WORK_FOLDER_PROPERTY );

			File workDirectory = new File( institutionDirectory.getCanonicalPath()+File.separatorChar+batchIngestCommand.getInstitution()+File.separatorChar+batchIngestCommand.getBatchSet()+File.separatorChar+workDirectoryName );

			Map<String, String> workMap = new HashMap<String, String>();

			if ( ! workDirectory.exists() )
			{
				throw new FatalException( "Invalid Directory:"+workDirectory.toString() );
			}

			String[] workList = null;
			
			workList = workDirectory.list( new FileUtil.XML_or_ZIP_NON_remote_fileFilter() );
			
			for ( int i =0; i < workList.length; i++ ) 
			{
				workMap.put( workList[i], workList[i] );
			}

			batchIngestCommand.setWorkFileMap( workMap );
		}

	} // batchSetFormChange()

	
	private void enableIngestButtonIfInputsValid(  HttpServletRequest request, BatchIngestOptions batchIngestCommand )
	{
			// first we set the batch set, which enables the submit button in batchIngest.jsp

		request.setAttribute( FedoraAppConstants.BATCH_SET_NAME_ATTRIBUTE, batchIngestCommand.getBatchSetName() );

			// if split work file is enable, we must have a valid file selected
		
		if ( batchIngestCommand.isSplitXMLinWorkDirToMets() && batchIngestCommand.getWorkFile().equals(FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE))
		{
			request.getSession().removeAttribute( FedoraAppConstants.BATCH_SET_NAME_ATTRIBUTE );
		}
		else
		{
			request.getSession().setAttribute( FedoraAppConstants.BATCH_SET_NAME_ATTRIBUTE , batchIngestCommand.getBatchSetName() );
		}

			// check if a valid institution is set
		
		if( batchIngestCommand.getInstitution().equals( FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE )) 
		{
			request.getSession().removeAttribute( FedoraAppConstants.BATCH_SET_NAME_ATTRIBUTE );		
		}
		
			// check if valid batch set is set
		
		if( batchIngestCommand.getBatchSet().equals(FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE)) 
		{
			request.getSession().removeAttribute( FedoraAppConstants.BATCH_SET_NAME_ATTRIBUTE );		
		}
		
		if ( FedoraAppUtil.getIngestType() == FedoraAppUtil.INGEST_TYPE.ISLANDORA && ! batchIngestCommand.getBatchSetName().contains( FedoraAppConstants.MIXED_CONTENT_DIRECTORY ) )
		{
			// if a collection is specified, make sure a content is specified
			if ( ( batchIngestCommand.getFedoraCollection().equals( FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE ) 
					|| batchIngestCommand.getFedoraContentModel().equals( FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE ) ) ) 
			{
				request.getSession().removeAttribute( FedoraAppConstants.BATCH_SET_NAME_ATTRIBUTE );			
			}
		}
//		
//		if ( ! batchIngestCommand.isAllowSimultaneousManualAndRemoteIngest() ){
//			request.getSession().removeAttribute( FedoraAppConstants.BATCH_SET_NAME_ATTRIBUTE );
//		}
	} 
	
	private void handleCollectionChange(HttpServletRequest request, HttpServletResponse response, BatchIngestOptions batchIngestCommand, BindException errors ) 
	{
		Administrator administrator  = (Administrator) request.getSession().getAttribute( "edu.du.penrose.systems.fedora.client.Administrator" );

		/**
		 * The collectionPID and contentModel are only used by an islandora ingest.
		 */
		String collectionPID = batchIngestCommand.getFedoraCollection();

		String topCollection = collectionPID;
		if ( collectionPID.compareTo( FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE ) == 0 ){
			topCollection = batchIngestCommand.getInstitution()+":top";
		}

		Map<String,String> collectionMap = ResourceIndexUtils.getChildCollectionsMap( administrator, topCollection,  batchIngestCommand.getInstitution()); 	
//		if ( collectionMap.size() > 0 ){
			batchIngestCommand.setFedoraCollectionMap( collectionMap     );		
//		}

		if ( collectionPID.equals( FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE ) )
		{
			batchIngestCommand.setFedoraContentModelMap( new HashMap<String, String>() );	
			batchIngestCommand.setFedoraContentModel( FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE );	
		}
		else
		{
			Map<String,String> contentModelMap = ResourceIndexUtils.getAllIslandoraCollectionContentModelsMap( administrator, batchIngestCommand.getFedoraCollection() ); 

			batchIngestCommand.setFedoraContentModelMap( contentModelMap );		

			batchIngestCommand.setFedoraContentModel( FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE );	

			errors.rejectValue( "fedoraContentModel", "batchIngest.error.invalidContentModel" ); 
		}

	}

	/**
	 * Update the user options command object (BatchIngestOptions) and start a 
	 * new ingest.
	 */
	public ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
	throws Exception {

		BatchIngestOptions batchIngestCommand = (BatchIngestOptions) command;	
		BatchIngestController batchIngestController = null;
		
		boolean allowBatch = false;

		String fedoraContext = batchIngestCommand.getInstitution();
		String batch_set     = batchIngestCommand.getBatchSet(); // dir name and becomes fedora context
		String intitution    = batchIngestCommand.getInstitution();

		this.saveProperties( batchIngestCommand );
		
		
			// used by gwt display status page. edu.du.penrose.systems.fedoraApp.web.gwt.batchIngest.public.batchIngestStatus.jsp
		request.setAttribute( FedoraAppConstants.BATCH_SET_NAME_ATTRIBUTE, batchIngestCommand.getBatchSetName() );
		
		// if the batch_set is already running, do not start another one, forward to the status page.

		if ( BatchIngestThreadManager.getBatchSetStatus( batchIngestCommand.getBatchSetName() ) 
				!= null && BatchIngestThreadManager.isBatchSetRunning( batchIngestCommand.getBatchSetName() ) ) 
		{
			return new ModelAndView( new RedirectView( "/"+FedoraAppConstants.getServletContextListener().getWebApplicatonName()+this.getSuccessView() ) ); 
		}

		if ( request.getParameter( "ingestAll" ) != null ) {  // start a new batch ingest

			try {   
				
				this.saveProperties( batchIngestCommand );
				
				/*
				 * If this is an Islandora ingest, we will have the model and collection defined, but we still need set up the relationships
				 * This will occur later in the ingest process, see FedoraAppBatchIngest
				 */

				// Create a background thread and start a new batch ingest.
				batchIngestController = new FedoraAppBatchIngestController( new TransformMetsData(), intitution, batch_set, fedoraContext, batchIngestCommand );
				BatchIngestThreadManager.setBatchSetThread( batchIngestCommand.getBatchSetName(), batchIngestController );
				new Thread( batchIngestController ).start();
				
			}
			catch ( Exception e ) {
				this.logger.error( e.getMessage() );
				throw e;
			}
		}

		return new ModelAndView( new RedirectView( "/"+FedoraAppConstants.getServletContextListener().getWebApplicatonName()+this.getSuccessView() ) );    
	}

	private void saveProperties( BatchIngestOptions batchIngestCommand ) throws Exception
	{
		String institutionDirectoryPath = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.BATCH_INGEST_TOP_FOLDER_URL_PROPERTY );
		String institutionOptionsLocation = institutionDirectoryPath + batchIngestCommand.getInstitution() +"/"+ batchIngestCommand.getBatchSet() + "/"+ batchIngestCommand.getBatchSet()+".properties";
		ProgramProperties optionsProperties = ProgramProperties.getInstance( new URL( institutionOptionsLocation ) );

		FedoraAppUtil.saveIngestFileOptions( optionsProperties, batchIngestCommand );
		
		batchIngestCommand.setIslandoraCollectionPID(optionsProperties.getProperty(FedoraAppConstants.ISLANDORA_COLLECTION_ATTRIBUTE, "" ));
		batchIngestCommand.setIslandoraContentModelPID(optionsProperties.getProperty(FedoraAppConstants.ISLANDORA_CONTENT_MODEL_ATTRIBUTE, "" )); 
	}

} // GetFedorObjFormController