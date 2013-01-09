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
