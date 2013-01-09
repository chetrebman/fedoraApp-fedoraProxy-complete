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
import org.apache.commons.lang3.StringEscapeUtils
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.text.DateFormatSymbols

import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.util.XmlUtil

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class EtdCrosswalk {
	
	/**
	 * Logger for this class and subclasses.
	 */
	protected final Log logger = LogFactory.getLog(getClass());
	
    def xmlDeclaration = "<?xml version='1.0' encoding='UTF-8'?>"
     
    // MODS
    def modsXsd = "http://www.loc.gov/mods/v3/mods.xsd"
    def modsNameSpace = "<mods:mods xmlns:mods=\"http://www.loc.gov/mods/v3\"  xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.loc.gov/mods/v3 http://www.loc.gov/mods/v3/mods.xsd\">"
    
    // METS
    def metsXsd = "http://www.loc.gov/standards/mets/mets.xsd"
    
	def writeObj = new Write()
	def ValidatorObj = new Validator_NOT_USED()
	def MetsObj = new Mets()
	
	Date now = new Date();
	DateFormat CurrentDate = new SimpleDateFormat("yyyy-MM-dd")
	

	/**
	 * 
	 * @param etdPCOsPath
	 * @param xmlPath
	 * @param etdFilesList
	 * @return
	 */
    public etdToMods( etdPCOsPath, xmlPath, etdFilesList, embargoedFolderName, failedFolderName, yearCollectionMap, etdContentModel ) {
        
        def DISS_submission
        def DISS_name
        def DISS_contact
        def DISS_description
        def DISS_content
        def modsXmlMap = [:]
        def modsXml = ""
        def monthYear
        def setEmbargo
		def fileName
		def keywords = []
		def keyword
		def keywordValue	
		
        StringEscapeUtils Escape = new StringEscapeUtils()
        def embargoObj = new EtdEmbargo()
		
		//File batchFile = new File( etdPCOsPath + "du_etd_" + this.CurrentDate.format( this.now ) + "_ingest_.xml" )
		//batchFile.append( this.xmlDeclaration + "<batch version='2'><ingestControl command='a' type='normal' />", "utf-8" )
		
        etdFilesList.each { etdXml -> 

			def etdToModsThread = Thread.start {
				
			sleep 30
												
            	DISS_submission = new XmlParser().parse( etdXml )

				//println "publishing_option: " + DISS_submission.attribute("publishing_option") 
				//println "embargo_code: " + DISS_submission.attribute("embargo_code")
				//println "third_party_search: " + DISS_submission.attribute("third_party_search")
				//println "third_party_sales: " + DISS_submission.attribute("third_party_sales")
                       
				DISS_name = DISS_submission.DISS_authorship.DISS_author.DISS_name
				DISS_description = DISS_submission.DISS_description
				DISS_content = DISS_submission.DISS_content
            
				setEmbargo = embargoObj.checkEmargoCode( DISS_submission.attribute("embargo_code"), DISS_description.DISS_dates.DISS_accept_date.text() )
                       
				if ( setEmbargo != "" )
				{				
					println "EMBARGO: "+setEmbargo+"->"+etdXml;
					this.logger.info(  "EMBARGO: "+setEmbargo+"->"+etdXml );
					etdXml.renameTo( new File( embargoedFolderName, etdXml.getName()) )
					return
				}  
				                     
				// MODS                     
				modsXml += this.modsNameSpace    
				//modsXml += "\n<mods:titleInfo><mods:title>" + setEmbargo + Escape.escapeXml( DISS_description.DISS_title.text().trim() ) + "</mods:title></mods:titleInfo>"
				modsXml += "\n<mods:titleInfo><mods:title>" + Escape.escapeXml( DISS_description.DISS_title.text().trim() ) + "</mods:title></mods:titleInfo>"
				
         		/*		
				modsXml += "\n<mods:name type='personal'>"
				modsXml += "\n<mods:namePart type='given'>" + Escape.escapeXml( DISS_name.DISS_fname.text().trim() + " " + DISS_name.DISS_middle.text().trim() ) + "</mods:namePart>"
				modsXml += "\n<mods:namePart type='family'>" + Escape.escapeXml( DISS_name.DISS_surname.text().trim() ) + "</mods:namePart>"
				modsXml += "\n<mods:affiliation>" + Escape.escapeXml( DISS_description.DISS_institution.DISS_inst_contact.text().trim() ) + "</mods:affiliation>"
				modsXml += "\n<mods:role><mods:roleTerm type='text'>Author</mods:roleTerm></mods:role>"
				modsXml += "\n</mods:name>" 
                */		
				modsXml += "\n<mods:name type='personal'>"
				modsXml += "\n<mods:namePart>" + Escape.escapeXml( DISS_name.DISS_surname.text().trim() ) + ", " + Escape.escapeXml( DISS_name.DISS_fname.text().trim() ) + " " + DISS_name.DISS_middle.text().trim() + "</mods:namePart>"
				modsXml += "\n<mods:role><mods:roleTerm authority='marcrelator' type='text'>creator</mods:roleTerm></mods:role>"
				modsXml += "\n</mods:name>"
												
				modsXml += "\n<mods:name type='corporate'>"
				modsXml += "\n<mods:namePart>" + Escape.escapeXml( DISS_description.DISS_institution.DISS_inst_contact.text().trim() ) + "</mods:namePart>"
				modsXml += "\n<mods:role><mods:roleTerm authority='marcrelator' type='text'>sponsor</mods:roleTerm></mods:role>"
				modsXml += "\n</mods:name>"
							
				modsXml += "\n<mods:name type='corporate'>"
				modsXml += "\n<mods:namePart>" + Escape.escapeXml( DISS_description.DISS_institution.DISS_inst_name.text().trim() ) + "</mods:namePart>"
				modsXml += "\n<mods:role><mods:roleTerm authority='marcrelator' type='text'>degree grantor</mods:roleTerm></mods:role>"
				modsXml += "\n</mods:name>"
								
				modsXml += "\n<mods:originInfo>" 
				modsXml += "\n<mods:publisher>" + Escape.escapeXml( DISS_description.DISS_institution.DISS_inst_name.text().trim() ) + "</mods:publisher>"
				modsXml += "\n<mods:dateIssued keyDate='yes'>" + getDateString( DISS_description.DISS_dates.DISS_accept_date.text().trim() ) + "</mods:dateIssued>"
				modsXml += "\n<mods:dateCreated>" + Escape.escapeXml( DISS_description.DISS_dates.DISS_comp_date.text().trim() ) + "</mods:dateCreated>"
				modsXml += "\n</mods:originInfo>" 
                
				modsXml += "\n<mods:language><mods:languageTerm type='text'>" + Escape.escapeXml( DISS_description.DISS_categorization.DISS_language.text().trim() ) + "</mods:languageTerm></mods:language>"
				modsXml += "\n<mods:abstract>" + Escape.escapeXml( DISS_content.DISS_abstract.DISS_para.text().trim() ) + "</mods:abstract>"
				modsXml += "\n<mods:note type='thesis' displayLabel='Degree Type'>" + Escape.escapeXml( DISS_description.DISS_degree.text().trim() ) + "</mods:note>"
				
				modsXml += "\n<mods:subject>"
                	DISS_description.DISS_categorization.DISS_category.DISS_cat_desc.each { 
						modsXml += "\n<mods:topic>" + Escape.escapeXml( it.text().trim() ) + "</mods:topic>"
					} 

				keywords = DISS_description.DISS_categorization.DISS_keyword.text().trim().split(",")
													
				keywords.each { keywordAsTopic -> 
						
					keywordValue = keywordAsTopic.trim()
					
					if (!keywordValue.equals("")) {
						
						try {
						keyword = keywordValue.replaceFirst(keywordValue[0], keywordValue[0].toUpperCase())
						modsXml += "\n<mods:topic>" + Escape.escapeXml( keyword.trim() ) + "</mods:topic>"
						}
						catch ( Exception e )
						{
							println "ERROR: e.getMessage()";
						}
						
					}
					
				}

				modsXml += "\n</mods:subject>"
				
				modsXml += "\n<mods:physicalDescription><mods:extent>" + Escape.escapeXml( DISS_submission.DISS_description["@page_count"].text()  ) + "</mods:extent></mods:physicalDescription>"
				
				// check identifer names
				fileName = fileManip( etdPCOsPath, DISS_content.DISS_binary.text()  )
				




				modsXml += "\n<mods:identifier type='local'>" + Escape.escapeXml( fileName ) + "</mods:identifier>"    
             
				DISS_content.DISS_attachment.each { attachment ->
					modsXml += "\n<mods:identifier type='local'>" + Escape.escapeXml( attachment.DISS_file_name.text() ) + "</mods:identifier>"
				}
			 
				modsXml += "\n<mods:accessCondition type='useAndReproduction'>Copyright is held by the author.</mods:accessCondition>"
                
				modsXml += "\n</mods:mods>"    
   

         												
				fileName = xmlPath + fileName.replaceAll(".pdf", "") + "_mods.xml"  
				

				
				// println modsXml
				
				writeXml( fileName, modsXml  )
				modsXml = ""
				
				def passed = constructMets( this.modsNameSpace, fileName, etdPCOsPath, failedFolderName, yearCollectionMap, etdContentModel )
				
				if ( passed == null || passed == false )
				{
					def tempFile = new File (fileName)
					tempFile.renameTo( new File( failedFolderName, tempFile.getName() ))
				}
												
				// println "\n"
				        	  
			}
			etdToModsThread.join()   
        }
		
		//batchFile.append( "</batch>", "utf-8" )
		//tidyXml( etdPCOsPath + "du_etd_" + this.CurrentDate.format( this.now ) + "_ingest_.xml" )
    }
    
    /**
     * validates mods xml
     * @param string etdXmlPath
     * @return void
     */ 
    private validate( fileName, xsdUrl ) {
        
		Date now = new Date();
		DateFormat DateFormat= new SimpleDateFormat("MM-dd-yyyy")
		def date = DateFormat.format( now )
		
		return this.ValidatorObj.validate( fileName, xsdUrl, date )

    }
    
	/**
	 * writes mods xml to disk
	 * @param string fileName
	 * @param string modsXml
	 * @return void
	 */
	private writeXml( fileName, xml ) {

		this.writeObj.createXmlFile( fileName, xml )
		
	}
	
	/**
	 * 
	 */
	private writeBatchXml( fileName, mets, etdPCOsPath ) {
		
		this.writeObj.createBatchXmlFile( fileName, mets, etdPCOsPath )
		
	}
	
	/**
	 * tidy's xml batch file
	 * @param string file
	 * @return void
	 */
	private tidyXml( file ) {
		
		this.writeObj.tidyXml( file )
		
	}
	
    /**
    * manipulates etd accept date
    * @param string date
    * @return string modified date
    */
    private getDateString( date ) {
        
        def months = ['01':'January', '02':'February', '03':'March', '04':'April', '05':'May', '06':'June', '07':'July', '08':'August', '09':'September', '10':'October', '11':'November', '12':'December']
        def dateList = date.split("/")
        def monthName = months.get( dateList[0] )
        def year = dateList[2]
        
        return monthName + " " + year

    }
	
	/**
	 * 
	 * @param namespace
	 * @param xml the xml output file name and path
	 * @param etdPCOsPath
	 * @param yearCollectionMap of type 2007_D=codu:62795j
	 * @return
	 */
	private constructMets( namespace, xml, etdPCOsPath, failedFolderName, yearCollectionMap, etdContentModel ) {
		
		def collectionName				
		def mets = this.MetsObj.createMets( namespace, xml, etdPCOsPath, failedFolderName, yearCollectionMap, etdContentModel )
		
		// let the ingester do the schema check,
		
		if ( mets != null )
		{
			writeXml( xml.replaceAll("_mods", "_mets"+FedoraAppConstants.BACKGROUND_TASK_NAME_SUFFIX ), mets )
			return true;
		}
		else {
			return false;
		}
		
//		tidyXml( xml.replaceAll("_mods", "_mets") )
					
//		println mets
				
	}
	
	/**
	 * checks files and changes identifier names 
	 * @param string file
	 * @return string file
	 */
	private fileManip( etdPCOsPath, file ) {
		
		def fileSections
		def pathSections
		def pco 
		def count
			
		try {
			
			pco = new File( etdPCOsPath + file )
		
def pathSave = etdPCOsPath
def fileSave = file
println "pathSave="+pathSave 
println "fileSave="+fileSave

			if (!pco.exists()) {

				// fileSections = pco.toString().split ("_")  this takes the entire path, if the path happens to have any '_' such batch_space, things break  cr 1-8-12
				
				fileSections = pco.getName().split( "_" );
				file = fileSections[0] + "_denver_" + fileSections[1] + "_" + fileSections[2]

			}
			
			pathSections = file.toString().split("/")
			count = pathSections.size() - 1
			file = pathSections[count].toString()

		} catch (e) {
		    println "\nFilename:" + file + ": " + "Error ( " + e.getMessage() + " )\n\n";
			logger.error( "\nFilename:" + file + ": " + "Error ( " + e.getMessage() + " )\n\n" );
		}
		
if ( file.equals( "FedoraApp_denver_FedoraProxy_distribution" )  )
{
	print "got it"
}
		
		return file
	}
	    
}