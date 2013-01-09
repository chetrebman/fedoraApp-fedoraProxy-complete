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

class Main {
	
    public static void main(String[] args) {
        		       
        def zipsPath = args[0]        
        def extractZipPath = args[1]  
        def etdXmlPath = args[2]      
        def etdPCOsPath = args[3]     
        def logPath = args[4]         
		        
        def ZipObj = new Zip()
        def etdUtilsObj = new EtdUtils()
        def etdCrosswalkObj = new EtdCrosswalk() 
        def BatchObj = new Batch()
				
        def zipFilesList = ZipObj.getZipFiles( zipsPath )
        ZipObj.extract( zipFilesList, extractZipPath )
               
        def dirs = etdUtilsObj.getDirectories( extractZipPath )
                
        def files = etdUtilsObj.getEtdFilesFromDirectory( dirs, extractZipPath )
        etdUtilsObj.copyEtdFiles( files, extractZipPath )
        
		def pco = etdUtilsObj.getEtdPco( extractZipPath )
		etdUtilsObj.copyEtdPco( pco, etdPCOsPath )
		
        def xml = etdUtilsObj.getEtdXml( extractZipPath )
        
        etdCrosswalkObj.etdToMods( etdPCOsPath, logPath, etdXmlPath, xml )

		etdUtilsObj.deleteFiles( extractZipPath )
        etdUtilsObj.deleteDirectories( dirs )
				
    }
    
}