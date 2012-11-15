package edu.du.penrose.systems.etd

import org.apache.commons.lang3.StringEscapeUtils
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.jmimemagic.*

class Mets {

	/**
	 * Logger for this class and subclasses.
	 */
	protected final Log logger = LogFactory.getLog(getClass());
	
	
	/**
	 * 
	 * @param nameSpace
	 * @param xml the mods path and file name
	 * @param etdPCOsPath
	 * @return
	 */
	public createMets( nameSpace, xml, etdPCOsPath, failedFolderName, Map<String, String> yearCollectionMap, etdContentModel ) {
		
		StringEscapeUtils Escape = new StringEscapeUtils()
		def mets = ""
		def mods = new File( xml ).text.replaceAll( nameSpace, "<mods:mods xmlns:mods='http://www.loc.gov/mods/v3' >")
		def modsXml = new XmlSlurper().parse( xml )
		def mimeType
		def modsTitle = Escape.escapeXml( modsXml.titleInfo.title.text() )
		def modsIdentifierTypeID = modsXml.identifier[0].text()
		def pathSection
		def count
		def id
		def order
		def collection=null
		def contentModel
		
		def dateCreated = modsXml.originInfo.dateCreated.text()
		
		def masters   = false
		def doctorial = false
		def boolean knowType = false;
		if ( xml.contains( "M_") ){
			masters = true
			knowType = true
		}
		if ( xml.contains( "D_") ){
			doctorial = true
			knowType = true
		} 

		
		if ( ! knowType )
		{
			// the type is not in the name, so look inside the mods.
			def type = null
			def note = modsXml.note.text()
			if ( note.contains( "PhD") || note.contains( 'Ph.D.')){
				doctorial = true
			}
			else {
				if ( note.contains( "MA" ) || note.contains("MS") || note.contains("M.S.") || note.contains("M.A.") ){
					masters = true
				}
				else {
					masters = false
				}
			}
		}
		
		if ( doctorial )
		{
			collection=yearCollectionMap.get( dateCreated+"_"+"D" )
		}
		else
		{
			collection=yearCollectionMap.get( dateCreated+"_"+"M" )
		}
				
		if ( collection == null ){
			println "collection not set for:"+xml
			this.logger.error(  "collection not set for:"+xml );

			return null
		}
		
		
		mets += "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
		mets += "\n<!--<ingestControl command=\"A\" type=\"normal\" />-->"
		mets += "\n<mets:mets xmlns:mets='http://www.loc.gov/METS/' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='http://www.loc.gov/METS/ http://www.loc.gov/standards/mets/mets.xsd http://www.loc.gov/mods/v3 http://www.loc.gov/mods/v3/mods-3-3.xsd http://purl.org/dc/elements/1.1/ http://dublincore.org/schemas/xmls/qdc/dc.xsd' OBJID='" + modsIdentifierTypeID + "' LABEL='" + modsTitle + "'>"
	
		
		mets += '\n<mets:dmdSec ID="dmdAlliance">'
		mets += '\n<mets:mdWrap MIMETYPE="text/xml" MDTYPE="OTHER" LABEL="CustomAlliance Metadata">'
		mets += "\n<mets:xmlData>"
		mets += '\n<islandora collection="'+collection+'" contentModel="'+etdContentModel+'" />'
		mets += "\n</mets:xmlData>"
		mets += "\n</mets:mdWrap>"
	    mets += "\n</mets:dmdSec>"
		
		mets += "\n<mets:dmdSec ID='purchased_METS.id1'>"
		mets += "\n<mets:mdWrap MIMETYPE='text/xml' MDTYPE='MODS'>"
		mets += "\n<mets:xmlData>"
						 
		// START MODS
		mets += "\n"
		mets += mods.replaceAll( "<?xml version='1.0' encoding='UTF-8'?>", "" )
		// END MODS
		
		mets +=	"\n</mets:xmlData>"	
		mets += "\n</mets:mdWrap>"
		mets += "\n</mets:dmdSec>"

		pathSection = xml.toString().split("/")
		count = pathSection.size() - 1
		id = pathSection[count].toString()

		mets += "\n<mets:fileSec>"
		mets += "\n<mets:fileGrp ID='" + "ETD_" + id.replaceAll("_mods.xml", "") + "' USE='master'>"

		modsXml.identifier.eachWithIndex { fileSec, i ->
			
			// println etdPCOsPath + fileSec.text()  

			try {
				if ( fileSec == null || fileSec.text() == null )
				{
					println "null" 
				}
				print fileSec.text()  
				mimeType = Magic.getMagicMatch( new File( etdPCOsPath + fileSec.text() ), false ).getMimeType()
				// if the mimeType is not known, you will get a mimeType of ????
						
			} catch (MagicException e) { 
				print xml
			    print "\nFilename:" + fileSec.text() + ": " + "Error ( " + e.getMessage() + " )\n\n" 
				logger.error( "\nFilename:" + fileSec.text() + ": " + "Error ( " + e.getMessage() + " )\n\n" );
				mimeType = '????' // just continue, it is probably best to have something ingested that can fixed later.
			}
			
			if ( mimeType.contains( '?') )
			{
				def haveMimeType = false;
				if ( fileSec.text().endsWith( '.avi' ) ) { mimeType="video/avi";       haveMimeType=true }
				if ( fileSec.text().endsWith( '.mov' ) ) { mimeType="video/quicktime"; haveMimeType=true }
				if ( fileSec.text().endsWith( '.zip' ) ) { mimeType="application/zip"; haveMimeType=true }
				if ( fileSec.text().endsWith( '.m4v' ) ) { mimeType="video/x-m4v";     haveMimeType=true }
				if ( fileSec.text().endsWith( '.mpeg' )) { mimeType="video/mpeg";      haveMimeType=true }
				if ( fileSec.text().endsWith( '.mpg' ) ) { mimeType="video/mpeg";      haveMimeType=true }
				if ( fileSec.text().endsWith( '.mp3' ) ) { mimeType="audio/mpeg";      haveMimeType=true }
				
				if ( haveMimeType )
				{
					this.logger.warn(  "mime type not correctly detected for:"+fileSec.text()+" Using:"+mimeType );
				}
				else 
				{
					this.logger.error(  "mime type not correctly detected for:"+fileSec.text() );
				}
				
			}
			//mets += "\n<mets:file MIMETYPE='" + mimeType + "' ID='" + "ETD_" + fileSec.text() + "-" + i + "'>"  cr 10-8-12
			mets += "\n<mets:file MIMETYPE='" + mimeType + "' ID='" + fileSec.text() + "'>"
			
			mets += "\n<mets:FLocat xmlns:xlink='http://www.w3.org/1999/xlink' LOCTYPE='URL' xlink:href='file:" + fileSec.text() + "'></mets:FLocat>"
			mets += "\n</mets:file>"
			
		}
		
		mets += "\n</mets:fileGrp>"
		mets += "\n</mets:fileSec>"
		
		if (modsXml.identifier.size() > 1) {
			
			// mets += "\n<mets:structMap ID='ETD_" + id.replaceAll("_mods.xml", ".pdf") + "'>"
			mets += "\n<mets:structMap TYPE='logical'>"
			// mets += "\n<mets:div TYPE='ETD' ID='ETD'>" cr 10-8-12
			mets += "\n<mets:div TYPE='section' LABEL='" + modsTitle + "' ID='ETD_" + id.replaceAll("_mods.xml", ".pdf") + "'>"
			
				modsXml.identifier.eachWithIndex { structMap, i ->
					
					if (i.equals(0)) {
						order = i + 1
						mets += "\n<mets:div ORDER='" + order + "' TYPE='main' LABEL='" + modsTitle + "'>"
						
						// mets += "\n<mets:fptr FILEID='ETD'></mets:fptr>" 
						//mets += "\n<mets:fptr FILEID='ETD_" + modsXml.identifier[i].text() + "'></mets:fptr>" // cr 10-8-12
						mets += "\n<mets:fptr FILEID='" + modsXml.identifier[i].text() + "'></mets:fptr>" 
						
						mets += "\n</mets:div>"
					} else {
						order = i + 1
						mets += "\n<mets:div ORDER='" + order + "' TYPE='section'>"
						
						// mets += "\n<mets:fptr FILEID='ETD'></mets:fptr>"
						// mets += "\n<mets:fptr FILEID='ETD_" + modsXml.identifier[i].text() + "'></mets:fptr>"  // cr 10-8-12
						mets += "\n<mets:fptr FILEID='" + modsXml.identifier[i].text() + "'></mets:fptr>" 
						mets += "\n</mets:div>"
					} 
				}
				
			mets += "\n</mets:div>"
			mets += "\n</mets:structMap>"
			
		} else {
		
			mets += "\n<mets:structMap TYPE='logical'>"
			mets += "\n<mets:div TYPE='section' LABEL='" + modsTitle + "' ID='ETD_" + id.replaceAll("_mods.xml", ".pdf") + "'>"
			// mets += "\n<mets:fptr FILEID='ETD_" + modsXml.identifier.text() + "'></mets:fptr>" // cr 10-8-12
			mets += "\n<mets:fptr FILEID='" + modsXml.identifier.text() + "'></mets:fptr>" 
			mets += "\n</mets:div>"
			mets += "\n</mets:structMap>"
		
		}
		
		mets += "\n</mets:mets>"

		new File( xml ).delete();
		return mets
		
	}
	
}