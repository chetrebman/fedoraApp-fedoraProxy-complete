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