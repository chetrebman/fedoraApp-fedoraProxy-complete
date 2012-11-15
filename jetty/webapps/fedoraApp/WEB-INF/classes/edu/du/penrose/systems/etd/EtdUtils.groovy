package edu.du.penrose.systems.etd

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class EtdUtils {
	
	/**
	 * Logger for this class and subclasses.
	 */
	protected final Log logger = LogFactory.getLog(getClass());
	
    def FileUtil = new AntBuilder()
    
   /** TODO: logging
   * retrieves etd xml files
   * @param string filePath
   * @return list etdFiles
   */
   public getEtdXml( filePath ) {
	
        def etdFiles = []
        
        try {
            
            new File( filePath ).eachFile { etdFile ->
               
				if (etdFile.isFile() && etdFile.name.endsWith(".xml")) {
					etdFiles << etdFile
				}

            }
            
        } catch(e) {
            //println e.stackTrace
            //println "An error occurred."
        }
        
        return etdFiles		
   } 
   
   
   /**
   * retrieve directories 
   * @param string extractZipPath
   * @return list dirs
   */
   public getDirectories( extractZipPath ) {
    
        def dirs = []
        
        try {
            
            new File( extractZipPath ).eachDir { dir ->
               
				dirs << dir.toString() + "/"

            }
            
        } catch(e) {
            println e.stackTrace
            println "An error occurred."
        }
        
        return dirs
   }
   
    
   /**
   * retrieves etd xml files
   * @param list dirs
   * @param string extractZipPath
   * @return list files
   */
   public getEtdFilesFromDirectory( dirs, extractZipPath ) {
       
        def files = []
                
        dirs.each { dir ->
         
            try {
				new File( dir ).eachFile { file ->
					
					if (file.isFile()) {
						files << file
					}

                }
            } catch(e) {
                println e.stackTrace
                println "An error occurred."
            }
           
        }    
        
        return files
    }

    /**
    * copy etd files
    * @param list files
    * @param string extractZipPath
    * @return void
    **/
    public copyEtdFiles( files, extractZipPath ) {
    
        def pathSection
        def count
        def toFile
        def newFile
        def newDir
                
        files.each { fromFile ->
			
				try {
					if (fromFile.isFile()) { 
                
						pathSection = fromFile.toString().split("/")
						count = pathSection.size() - 1
						newFile = pathSection[count].toString()
						toFile = extractZipPath + newFile
                    
						this.FileUtil.copy( file: fromFile, todir: extractZipPath )
 
					} 
				} catch(e) {
                	println e.stackTrace
					println "An error occurred."
				}

        }
    }
    
    
    /**
     * copies xml files
     * @param list xml
     * @param string etdXmlPath
     * @return void 
     */
    public copyEtdXml( xml, etdXmlPath ) {
        
        xml.each { etdXml ->

			try {
				this.FileUtil.copy( file: etdXml, todir: etdXmlPath )
			} catch(e) {
				println e.stackTrace
				println "An error occurred."
			}

        } 
    }
    
    /**
     * creates list containing pcos
     * @param string extractZipPath
     * @return list etdPcos
     */
    public getEtdPco( extractZipPath ) {
        
        def etdPcos = []
        
        try {
            new File( extractZipPath ).eachFile { etdPco ->
             
				if (etdPco.isFile() && !etdPco.name.endsWith(".xml")) {
					etdPcos << etdPco
				}

            }
        } catch(e) {
            println e.stackTrace
            println "An error occurred."
        }
        
        return etdPcos 
    }
    
    /**
     * copies pcos 
     * @param list pco
     * @param string etdPCOsPath
     * @return void
     */
    public copyEtdPco( pco, etdPCOsPath ) {
     
        pco.each { etdPco ->
           
			try {
				this.FileUtil.copy( file: etdPco, todir: etdPCOsPath )
			} catch(e) {
				println e.stackTrace
				println "An error occurred."
			}  

        } 
    }
    
    /**
     * deletes files
     * @param string path
     * @return void
     */
	public deleteFiles( path ) {
				
		try {
			this.FileUtil.delete( dir: path, includes: "*.*" )
		} catch(e) {
			logger.error(  e.getMessage()  )
		}
	}
	
    /**
    * deletes directories
    * @param string dirs
    * @return void
    */
    public deleteDirectories( dirs ) {

        dirs.each { 

			try {
				this.FileUtil.delete( dir: it )
			} catch(e) {
                println e.stackTrace
				println "An error occurred."
			}
				
        }
    
    }
	
	/**
	 * get mets xml
	 * @param string filePath
	 * @return void
	 */
	public getMetsXml( filePath ) {
		
			def metsFiles = []
			
			try {
				
				new File( filePath ).eachFile { metsFile ->
				   
					if (metsFile.isFile() && metsFile.name.endsWith("_mets.xml")) {
						metsFiles << metsFile
					}
	
				}
				
			} catch(e) {
				println e.stackTrace
				println "An error occurred."
			}
			
			return metsFiles
	   }
}