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