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
package edu.du.penrose.systems.fedoraApp.util

import java.io.File;

import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;

class EnableDisableRemoteTasks {

	static main(args) {

		if ( args.length < 2 )
		{
			println "\nUsage: topDiretoryToSearchForTaskEnablePropertiesFile, enableRemote \n"
			return
		}

		enableDisableRemoteTasks( args[0].bytes, args[1] )
	}

	/**
	 * Tries to get the web application WEB-INF directory from FedoraAppConstants store servletContained (which must be set!!)
	 * @see FedoraAppConstants#getServletContextListener()
	 * @param enableTasks
	 * @return
	 */
	def static enableDisableRemoteTasks( boolean enableTasks ) 
	{
		String webInfPath = FedoraAppConstants.getServletContextListener().get_WEB_INF_path();

		this.enableDisableRemoteTasks( webInfPath, enableTasks )
	}

	/**
	 * Recursive looks for the taskEnable.properites file and enable/disables remote ingest tasks
	 * @param topDirToSearch
	 * @param enableTasks
	 * @return
	 */
	def static enableDisableRemoteTasks( String topDirToSearch, boolean enableTasks ) {
		new File( topDirToSearch ).eachDirRecurse() { dir ->
			dir.eachFileMatch(~/${FedoraAppConstants.TASK_ENABLE_PROPERTIES_FILE_NAME}/) 
			{ 
				file ->  this.setRemote( file, enableTasks ) 
			}
		}
	}

	/**
	 * 
	 * @param inputFile
	 * @param enableTasks
	 * @return
	 */
	def static setRemote( File inputFile, enableTasks ) {
		def outputFile = new File( inputFile.getAbsolutePath() +".tmp" )

		outputFile.write("") // If file already exists, remove any previous contents
		
		boolean madeChange = false;
		List lines = inputFile.readLines()
		for ( String line in lines ) {
			if ( line.startsWith( FedoraAppConstants.TASK_ENABLE_PROPERTY ) ) {
				if ( enableTasks) {
					line = FedoraAppConstants.TASK_ENABLE_PROPERTY+"=true"
				}
				else {
					line = FedoraAppConstants.TASK_ENABLE_PROPERTY+"=false"
				}
				madeChange = true
			}
			outputFile << line+"\n"
		}
		if ( madeChange ) {
			println "Set "+FedoraAppConstants.TASK_ENABLE_PROPERTY+"="+enableTasks+" in "+inputFile
			String inputFileName = inputFile.getAbsolutePath();
			inputFile.delete()
			outputFile.renameTo( inputFileName )
		}
		else {
			outputFile.delete()
		}
	}
}
