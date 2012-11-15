package edu.du.penrose.systems.etd
import edu.du.penrose.systems.util.XmlUtil 

/**
 * validates xml schemas
 * @param string xmlFile
 * @param string xsdUrl
 * @param string date
 * @return void
 */
class Validator_NOT_USED {
 
    public validate( xmlFile, xsdUrl, date, logPath ) {
        
       try {
            XmlUtil.schemaCheck(  new File( xmlFile ) , new URL( xsdUrl ) )
			return true
       } catch(e) { 
       		File logFile = new File( logPath + "etd_" + date + ".log" )
       		logFile.append( "\nFilename:" + xmlFile + ": " + "Not Valid ( " + e.getMessage() + " )\n\n" )    
			return false
       }
        
    }    
    
}