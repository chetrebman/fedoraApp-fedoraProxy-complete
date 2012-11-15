package edu.du.penrose.systems.etd
//import org.apache.commons.lang3.StringEscapeUtils

class Batch {

	public addIngestControl( metsXml ) {
		
		println metsXml
		
		// read year  <originInfo><dateCreated>
		// read degree <note>
						
		metsXml.each { metsFile ->
		
			def mets = new XmlSlurper().parse( metsFile )
			
			def title = mets.dmdSec.mdWrap.xmlData.mods.titleInfo.title.text()
			def date = mets.dmdSec.mdWrap.xmlData.mods.originInfo.dateCreated.text()
			def fileName = mets.dmdSec.mdWrap.xmlData.mods.identifier.text()
			
			//println "FileName: " + metsFile
			println "TITLE: " + title.find(/EMBARGO/)
			println "Date: " + date
			println "DegreeCode: " + fileName.find(/D/)
			
			// def fileName "du_etd_" + date + "_" + degree + "_ingest.xml"
			//File batchFile = new File( etdPCOsPath + "du_etd_" + this.CurrentDate.format( this.now ) + "_ingest_.xml" )
			
			//def modsIdentifierTypeID = modsXml.identifier[0].text()
					
			
			//batchFile.append( this.xmlDeclaration + "<batch version='2'><ingestControl command='a' type='normal' />", "utf-8" )
			//batchFile.append( "</batch>", "utf-8" )
							
		}

		//tidyXml( etdPCOsPath + "du_etd_" + this.CurrentDate.format( this.now ) + "_ingest_.xml" )
		
	}
	
}
