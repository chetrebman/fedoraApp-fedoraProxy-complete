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

import java.util.*;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.du.penrose.systems.exceptions.FatalException;
import edu.du.penrose.systems.fedoraApp.web.gwt.batchIngest.server.StatusData;


/**
 * Provides a working class of type BatchThreadManager. To avoid GWT security error each web application
 * has to provide a copy of ThreadManger that uses a local StatusData object.
 * 
 * 
 * @author chet.rebman
 * @see edu.du.penrose.systems.fedoraApp.batchIngest.bus
 * @see BatchIngestThreadHolder 
 * @see BatchIngestController
 */
public class BatchIngestThreadManager extends BatchThreadManager {
  
    /**
     * Even thought this object defines static  methods to make for easy access
     * throughout the program, we need to be able to instantiate the object for
     * access from a GWT (google tools kit) service.
     */
    public BatchIngestThreadManager() {
        // nop
    }

    /**
     * @param batchSetName of type institution_batchSet
     * 
     * @return status of the task performing an ingest for this batchSet
     */
    static public StatusData getAllBatchSetStatus( String batchSetName ){
    
    	if ( ! batchSetName.contains( "_") )
    	{
    		System.out.println( "invalid batchSetName" ); // TBD temp for debug
    	}
        StatusData allStatus = new StatusData();

        allStatus.setBatchIsUpdates( isBatchIsUpdates(batchSetName) );
        allStatus.setBatchSetName(batchSetName);
        allStatus.setStatus( getBatchSetStatus(batchSetName) );
        allStatus.setCompleted( getCurrentCompleted( batchSetName ) );
        allStatus.setFailed( getCurrentFailed( batchSetName ) );
        allStatus.setInstitution( getInstitution( batchSetName ) );
        allStatus.setRunning( isBatchSetRunning( batchSetName ) );

        allStatus.setTotalFilesAddedSuccess(   getTotalFilesAddedSuccess( batchSetName )   );
        allStatus.setTotalFilesAddedFailed(    getTotalFilesAddedFailed( batchSetName )    );
        allStatus.setTotalFilesUpdatedSuccess( getTotalFilesUpdatedSuccess( batchSetName ) );
        allStatus.setTotalFilesUpdatedFailed(  getTotalFilesUpdatedFailed( batchSetName )  );

        allStatus.setIslandoraCollection( getIslandoraCollection( batchSetName ));
        allStatus.setIslandoraContentModel( getIslandoraContentModel( batchSetName ));
        
        allStatus.setstoppedByUser( isStoppedByUser( batchSetName) );
        
        return allStatus;
    }


    
} // BatchIngestThreadManager
