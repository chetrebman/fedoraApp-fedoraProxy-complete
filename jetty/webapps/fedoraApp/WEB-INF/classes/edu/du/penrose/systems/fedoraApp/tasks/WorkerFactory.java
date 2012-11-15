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
import java.lang.reflect.Constructor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraApp.ProgramFileProperties;
import edu.du.penrose.systems.fedoraApp.ProgramProperties;
import edu.du.penrose.systems.util.MyServletContextListener;

/**
 * Create the worker specified in the remote task properties file ie {institution}_{batchSet}_REMOTE.properties, if there is problem log it and return null.
 * 
 * @see FedoraAppConstants#REMOTE_TASK_WORKER_CLASS_PROPERTY
 * 
 */
public class WorkerFactory
{	
	/** 
	 * Logger for this class and subclasses.
	 */
	protected static final Log logger = LogFactory.getLog( "edu.du.penrose.systems.fedoraApp.tasks.WorkerFactory" );
	
	private  WorkerFactory()
	{
		// disble constructor
	}

	/**
	 * Return the worker specified in the remote task properties file ie {institution}_{batchSet}_REMOTE.properties, if there is problem log it and return null.
	 * 
	 * @see FedoraAppConstants#REMOTE_TASK_WORKER_CLASS_PROPERTY
	 * 
	 * @param batchSetName is of type codu_ectd where codu is the institution and ectd is the batch set name
	 * @return the worker object or null.
	 */
	public static WorkerInf getWorker( String batchSetName )
	{
		String[] tempArray =  batchSetName.split( "_" );
		String institution = tempArray[ 0 ];
		String batchSet    = tempArray[ 1 ];
		WorkerInf myWorkerObject = null;
		String propertiesFileName = null;
		String workerClassName = null;
		
		try {
			if ( batchSetName.endsWith(FedoraAppConstants.REMOTE_TASK_NAME_SUFFIX) ) {
				propertiesFileName = FedoraAppConstants.getServletContextListener().getInstituionURL().getFile() + institution +"/"+ batchSet + "/"+ batchSet+FedoraAppConstants.REMOTE_TASK_NAME_SUFFIX+".properties";
			}
			else {
				propertiesFileName = FedoraAppConstants.getServletContextListener().getInstituionURL().getFile() + institution +"/"+ batchSet + "/"+ batchSet+FedoraAppConstants.BACKGROUND_TASK_NAME_SUFFIX+".properties";
			}
			
			ProgramProperties optionsProperties  = new ProgramFileProperties( new File( propertiesFileName ) );

			if ( batchSetName.endsWith(FedoraAppConstants.REMOTE_TASK_NAME_SUFFIX) ) 
			{
				workerClassName = optionsProperties.getProperty( FedoraAppConstants.REMOTE_TASK_WORKER_CLASS_PROPERTY );
			}
			else {
				workerClassName = optionsProperties.getProperty( FedoraAppConstants.TASK_WORKER_CLASS_PROPERTY );
			}
			
			if ( workerClassName == null || workerClassName.length() == 0 )
			{
				logger.error( "Unable to find ingest class in"+propertiesFileName );
				return null;
			}


			Constructor<?> workerConsctuctor = 	Class.forName( workerClassName ).getConstructor( String.class );		
			myWorkerObject = (WorkerInf) workerConsctuctor.newInstance( batchSetName );		
		}
		catch ( Exception e) 
		{
			logger.error( "Unable to find ingest class for instituion_batchSet:"+institution+", "+batchSet+"  :"+e.getLocalizedMessage() );
			return null;
		}


		return myWorkerObject;
	}
}
