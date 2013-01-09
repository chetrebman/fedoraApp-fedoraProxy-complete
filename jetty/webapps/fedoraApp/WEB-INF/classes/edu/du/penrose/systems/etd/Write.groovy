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

class Write {
	
	/**
	 * Logger for this class and subclasses.
	 */
	protected final Log logger = LogFactory.getLog(getClass());
	
	
    /**
    * writes xml file to disk
    * @param map modsXmlMap
    * @param string path
    * @return void
    */
    public createXmlFile( fileName, xml ) {
       
		try {
            File xmlFile = new File( fileName )
            xmlFile.write( xml.trim(), "utf-8" ) 
		} catch(e) {
			logger.error( "\nFilename:" + fileName + ": " + "Error ( " + e.getMessage() + " )\n\n"  )
		}
    }
	
	/**
	 * writes batch xml to disk
	 * @param map modsXmlMap
     * @param string path
     * @return void
	 */
	public createBatchXmlFile( fileName, xml, etdPCOsPath, logPath) {
			
		try {
			File xmlFile = new File( etdPCOsPath + fileName )
			xmlFile.append( xml.trim(), "utf-8" )
		} catch(e) {
			logger.error( "\nFilename:" + fileName + ": " + "Error ( " + e.getMessage() + " )\n\n" )
		}
		
	}
	
	/**
	 * tidy's batch xml
	 * @param string file
	 * @return void
	 */
    public tidyXml( file ) {
				
		def xml = new XmlParser(trimWhitespace:false).parse( file ) // removes comments
		new XmlNodePrinter(new PrintWriter(new FileWriter(file))).print(xml)
		
	}
}