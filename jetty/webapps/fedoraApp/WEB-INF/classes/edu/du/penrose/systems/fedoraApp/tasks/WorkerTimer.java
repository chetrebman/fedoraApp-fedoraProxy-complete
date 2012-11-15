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

package edu.du.penrose.systems.fedoraApp.tasks;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraApp.ProgramFileProperties;
import edu.du.penrose.systems.fedoraApp.ProgramProperties;
import edu.du.penrose.systems.fedoraApp.util.FedoraAppUtil;

/**
 * Runs tasks/workers that are enabled in the taskEnable.properties files. Workers can be enable/disabled without
 * restarting the application. If you disable an enabled worke, the worker will be deleted, however any background 
 * tasks that were started by a worker will continue to completion..
 * 
 * This class(service) runs a list of workers (defined in the constructor) it does not have it's
 * own timer and must be scheduled with an <task:scheduled-tasks> element in the spring application's 
 * xml file.
 * <br>
 * @author chet Rebman
 *
 */

public class WorkerTimer {

	static ProgramProperties taskEnableProperties = null;
	/** 
	 * Logger for this class and subclasses.
	 */
	protected final Log logger = LogFactory.getLog(getClass());
	
	public WorkerTimer()
	{
		WorkerTimer.taskEnableProperties = new ProgramFileProperties( new File( FedoraAppConstants.getServletContextListener().getTaskEnablePropertiesURL().getFile() ) );
	}

	public void doIt() 
	{	
		Map<String,WorkerInf> workerList         = new HashMap<String,WorkerInf>();
		Set<String> remoteConfiguredInstitutions = new HashSet<String>();
		Set<String> taskConfiguredInstitutions   = new HashSet<String>();
		
		/** 
		 * Add Enabled workers to the static workerList
		 */
		try {
			if ( taskEnableProperties.getProperty( FedoraAppConstants.TASK_ENABLE_PROPERTY, false ) )
			{			
				logger.info( "WokerTimer getRemoteConfiguredInstitutions() " );
				remoteConfiguredInstitutions = FedoraAppUtil.getRemoteConfiguredInstitutions();
				logger.info( "WokerTimer remoteConfigureInstitutions: "+remoteConfiguredInstitutions );
				
				logger.info( "WokerTimer getTaskConfiguredInstitutions() "   );
				taskConfiguredInstitutions   = FedoraAppUtil.getTaskConfiguredInstitutions();	
				logger.info( "WokerTimer taskConfigureInstitutions: "  +taskConfiguredInstitutions );
				
				remoteConfiguredInstitutions.addAll( taskConfiguredInstitutions );
				
				Object[] remoteInstitutionsAsArray = remoteConfiguredInstitutions.toArray();
				for ( int i=0; i<remoteInstitutionsAsArray.length; i++ )
				{
					// batchName is of type codu_ectd_REMOTE
					String   batchSetName = (String) remoteInstitutionsAsArray[ i ];
				//	logger.info( "WokerTimer batchSetName="+batchSetName );
					String[] tempArray =  batchSetName.split( "_" );
					
					String institution = tempArray[ 0 ];
				//	logger.info( "WokerTimer institution="+institution );
					
					String batchSet    = tempArray[ 1 ];
				//	logger.info( "WokerTimer batchSet="+batchSet );
					
					if ( taskEnableProperties.getProperty( institution+"_"+batchSet, false ) )
					{
						if ( workerList != null && ! workerList.containsKey( batchSetName ))
						{	
							// worker does not exist yet, so add it to the workerList
							
							WorkerInf newWorker = WorkerFactory.getWorker( batchSetName );
							if ( newWorker != null )
							{
								// logger.info( "WokerTimer add worker="+batchSetName );
								workerList.put( batchSetName, newWorker );
							}
						}
					}
					else 
					{
						// The worker is not enabled in taskEnableProperties so remove it from the list.
						remoteConfiguredInstitutions.remove( batchSetName );
					}
				}
			}
		} 
		catch ( Exception e) 
		{
			logger.info( "ERROR: Getting task enable properties: "+e.getLocalizedMessage() );
		}
		
		/**
		 * Start all workers in the workerList and remove previously started, but now disabled workers from the workerList (this will NOT
		 * stop running background ingest tasks).
		 */
		Set<Entry<String, WorkerInf>> workerSet = workerList.entrySet();
		
		Iterator<Entry<String, WorkerInf>> workerIterator = workerSet.iterator();
		while( workerIterator.hasNext() )
		{
			WorkerInf currentWorker = workerIterator.next().getValue();
			
			// This will allow for the disabling of tasks without restarting the application.
			if( ! remoteConfiguredInstitutions.contains( currentWorker.getName() ) )
			{
				logger.info( "Worker:"+currentWorker.getName() + " disabled" );
				workerList.remove( currentWorker.getName() ); 
			}
			else
			{
				logger.info( "StartWorker:"+currentWorker.getName() );
				currentWorker.doWork();
			}	
		}
	}



} // class WorkerTimer

