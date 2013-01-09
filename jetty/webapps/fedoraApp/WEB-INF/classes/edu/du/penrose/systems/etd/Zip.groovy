/*
 * Copyright 2012 University of Denver
 * Author Fernando Reyes
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
package edu.du.penrose.systems.etd

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.exception.ZipException

class Zip {
    	
	/**
	 * Logger for this class and subclasses.
	 */
	protected final Log logger = LogFactory.getLog(getClass());
	
    /**
    * unzips files
    * @param list zipFiles
    * @param string extractPath
    * @return void
    */
    public extract( zipFiles, extractPath ) {
        
        zipFiles.each { zip -> 
        
            def unzipThread = Thread.start {
                
                sleep 30
                
                ZipFile zipFile = new ZipFile( zip )  
                
                try {
                    zipFile.extractAll( extractPath )
                    println "Unzipping File..."
                } catch (ZipException e) {
					logger.error( e.getMessage() )
                }
            }
            
            unzipThread.join() 
        }
    }
    
    
    /**
    * retrieves zip files
    * @param string filePath
    * @return list zipFiles
    */
    public getZipFiles( filePath ) {
	
        def zipFiles = []
                
        try {
            new File( filePath ).eachFile { zipFile ->
                
				if (zipFile.isFile() && zipFile.name.endsWith(".zip")) {
					zipFiles << zipFile
				}

            }
        } catch(e) {
            println e.stackTrace
            println "No zip files were found."
        }
                       
        return zipFiles
			
   } 
	
}