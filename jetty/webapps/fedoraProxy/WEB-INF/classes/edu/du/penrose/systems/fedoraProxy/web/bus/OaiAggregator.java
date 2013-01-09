/*
 * Copyright 2012 University of Denver
 * Author Chet Rebman
 * 
 * This file is part of FedoraProxy.
 * 
 * FedoraProxy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FedoraProxy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with FedoraProxy.  If not, see <http://www.gnu.org/licenses/>.
*/
package edu.du.penrose.systems.fedoraProxy.web.bus;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

//import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraApp.ProgramProperties;
import edu.du.penrose.systems.fedoraProxy.data.oai.AggregateList;
import edu.du.penrose.systems.fedoraProxy.data.oai.AggregateSet;
import edu.du.penrose.systems.fedoraProxy.data.oai.SingleSet;
import edu.du.penrose.systems.fedoraProxy.FedoraProxyConstants;

/**
 * This is a class that was used to aggregate a set of collections for a DU exhibit. It is no longer used. The code worked at the time so it was left
 * in feodraProxy. Every thing is hard coded and not it property files. If you wish to use it see the OAI_HOST and OAI_PORT in this file and the sets to
 * be aggregated in the AggregateList.java class
 * 
 */

/**
 * Test with...
 * 	http://localhost:7080/fedoraProxy/du/adr/oai.get?verb=ListSets or ListMetadataFormats 
 * 
 * Test ADR with
 * 	http://adr.coalliance.org:80/codu/fez/oai.php?verb=ListMetadataFormats  OLD ADR
 *  http://peakdigital.coalliance.org:80/oai2?verb=ListMetadataFormats
 *    Doesn't seem to work any more I notice the response for ListMetadataFormats is encoded in gzip where the original fez results were not 12-14-11
 * 
 * @author chet
 *
 */
@Controller
@RequestMapping(value = "/{institution}/oai.get")
public class OaiAggregator {

	/*
	 * When true FORCE_NEW_DATE, replace the OAI <datestamp> IN AGGREGATE sets with todays date forcing the record to be retrieved. 
	 * This is used to always update the data in OMEKA
	 */
	final boolean FORCE_NEW_DATE = true;

//	final String OAI_HOST = "adr.coalliance.org";
	// peakdigital.coalliance.org  USE THIS ONE
	
	private String OAI_HOST = "peakdigital.coalliance.org";
	private int    OAI_PORT = 80;

//	String ADR_OAI_GET_URL = "http://"+OAI_HOST+":"+OAI_PORT+"/codu/fez/oai.php";
	String ADR_OAI_GET_URL = "http://"+OAI_HOST+":"+OAI_PORT+"/oai2";
	
	public enum oaiCommands { ListMetadataFormats, ListSets, ListRecords }

	@RequestMapping( method = RequestMethod.GET )
	public final void handleGet(
			@PathVariable String institution, // must be adr, see below
			@RequestParam(required = true,  value = "verb") String oaiVerb,
			@RequestParam(required = false, value = "set") String set,
			@RequestParam(required = false, value = "metadataPrefix") String metadataPrefix,
			HttpServletRequest request, HttpServletResponse response )
	throws Exception { // MUST BE type  EXCETION for annotation's to work!

		try 
		{
			if ( institution.compareToIgnoreCase( "adr" ) != 0 )
			{
				response.setStatus( 404 );
				return;
			}
			
			if ( oaiCommands.ListMetadataFormats.toString().compareToIgnoreCase( oaiVerb ) == 0 )
			{
				this.performListMetadataFormats( response, oaiVerb );
			}
			if ( oaiCommands.ListSets.toString().compareToIgnoreCase( oaiVerb ) == 0 )
			{
				this.performListSets( response, oaiVerb );
			}
			if ( oaiCommands.ListRecords.toString().compareToIgnoreCase( oaiVerb ) == 0 )
			{
				if ( set == null || metadataPrefix == null || set.length() == 0 || metadataPrefix.length() == 0 ){
					throw new Exception( "Invalid list set command, missing set or metadataPrefix " );
				}
				this.performListRecords( response, oaiVerb, set, metadataPrefix, FORCE_NEW_DATE );
			}
		} 
		catch ( Exception e) 
		{
			response.sendError( 404 ); // need to send correct status here? TBD
		} 
	}
	
	public OaiAggregator() 
	{
		OAI_HOST = ProgramProperties.getInstance( 
				FedoraProxyConstants.getServletContextListener().getFedoraProxyProgramPropertiesURL() ).getProperty(
						FedoraProxyConstants.FedoraProxy_FEDORA_HOST_PROPERTY );
	}

	/**
	 * Of type..
	 * 		verb=ListRecords&set=oai%3Aadr.coalliance.org%3Acodu%3A37742&metadataPrefix=oai_dc
	 * 
	 * @param myClient
	 * @param response
	 * @param verb
	 * @throws IOException
	 */
	private void performListMetadataFormats( HttpServletResponse response, String verb ) throws IOException 
	{  
		String getString = ADR_OAI_GET_URL + "?verb="+verb;

		FedoraDatastream_get.executeMethod( getString, response, false );
	}

	/**
	 * @param myClient
	 * @param response
	 * @param verb
	 * @throws IOException
	 */
	private void performListSets( HttpServletResponse response, String verb ) throws IOException 
	{   
		String getString = ADR_OAI_GET_URL + "?verb="+verb;

		executeOaiListSetsWithAggregates( getString, response, false );
	}

	/**
	 * @param myClient
	 * @param response
	 * @param verb
	 * @param set
	 * @param metadataPrefix
	 * @param forceNewDate if true the oai <datestamp> is set with todays date, to force retrieval of aggregate records..
	 * @throws IOException
	 */
	private void performListRecords( HttpServletResponse response, String verb, String set, String metadataPrefix, boolean forceNewDate ) throws IOException 
	{   	

		String getString = ADR_OAI_GET_URL + "?verb="+verb+"&set="+set+"&metadataPrefix="+metadataPrefix;

		if ( set.startsWith( FedoraProxyConstants.fedoraProxy_OAI_SET_SPEC ) )
		{
			executeOaiListAggregateSetRecords( verb, response, false, set, metadataPrefix, forceNewDate );		
		}
		else 
		{
			executeOaiListSetRecords( getString, response, false, set, metadataPrefix );		
		}
	}

	/**
	 * Return result of type...
	 * 
	 * <?xml version="1.0" encoding="UTF-8"?>
     * <OAI-PMH xmlns="http://www.openarchives.org/OAI/2.0/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd">
     * <responseDate>2011-06-07T11:30:42Z</responseDate>
      * 
     *   <request verb="ListRecords" metadataPrefix="oai_dc" resumptionToken="">http://adr.coalliance.org/codu/fez/oai.php</request>
     *   <ListRecords>
     *   <record>
     *     <header>
     *       <identifier>oai:adr.coalliance.org:codu:48566</identifier>
     *       <datestamp>2010-04-25T17:35:02Z</datestamp>
     *       <setSpec>oai:adr.coalliance.org:codu:48565</setSpec>
     *     </header>
     *    <metadata>
     *      <oai_dc:dc xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:dc="http://purl.org/dc/elements/1.1/">
     *	       <dc:title>Girls Stretch at the University of Denver Lamont School of Music</dc:title>
     *		   <dc:identifier>http://adr.coalliance.org/codu/fez/view/codu:48566</dc:identifier>
     *         <dc:description>A young female student (foreground) at the University of Denver (DU) Lamont School of Music Dance Department stretches during a dance class in Denver, Colorado. Other female students are visible in the background. Children&#039;s classes at the Lamont School of Music were held at 909 Grant Street, Denver, Colorado. The Children&#039;s Dance Theatre was the performance group composed of students from the children&#039;s dance classes at Lamont.</dc:description>
     *         <dc:type>DU Image</dc:type>
     *         <dc:date>Array</dc:date> 
     *   	   <dc:creator>Brooks, Marshall</dc:creator>
     *								
     *         <dc:subject>Dance</dc:subject>
     *		   <dc:subject>Modern dance</dc:subject>
     *		   <dc:subject>Children</dc:subject>
     *         <dc:subject>Girls</dc:subject>
     *         <dc:publisher>University of Denver</dc:publisher>
     *         <dc:relation>Vera Sears Papers</dc:relation>
     *         <dc:format>http://adr.coalliance.org/codu/fez/eserv/codu:48566/M272.01.0001.0001.00002.tif</dc:format>
     *         dc:format>http://adr.coalliance.org/codu/fez/eserv/codu:48566/M272.01.0001.0001.00002_access.jpg</dc:format>
     *        </oai_dc:dc>
     *     </metadata>
     *   </record>
     *    <resumptionToken></resumptionToken>
     *   </listRecords>
     * </OAI-PMH>
     *       
	 * @param getString the get url string
	 * @param response
	 * @param authenicate
	 * @param aggregateSetName
	 * @param metadataPrefix
	 * @return
	 * @throws IOException
	 */
	String executeOaiListSetRecords( String getString, HttpServletResponse response, boolean authenicate, String aggregateSetName, String metadataPrefix ) throws IOException 
	{
		HttpClient theClient = new HttpClient();
		HttpMethod method    = new GetMethod( getString );
		
		FedoraDatastream_get.setDefaultHeaders( method );
    	
		FedoraDatastream_get.setAdrCredentials( theClient, authenicate );
    	
		String setRecords = null;
		try
		{
			theClient.executeMethod(method);
	
				// Set outgoing headers, to match headers from the server
			
			Header[] headers = method.getResponseHeaders();
			for (Header header : headers) 
			{
				if ("Content-Type".equalsIgnoreCase( header.getName() )) {
					response.setContentType( header.getValue() );
				}
	
				response.setHeader( header.getName(), header.getValue() ); 
			}
	
				// Write the body, flush and close		
	
			setRecords = method.getResponseBodyAsString();
	
			response.getWriter().print( setRecords );
		}
		finally 
		{
			method.releaseConnection();	
		}

	//	response.getOutputStream().flush();
	//	response.getOutputStream().close();
	
		return setRecords;
	}

	/**
	 * Get a list of all aggregate sets that an AggregateList object knows about.<br><br>
	 * 
	 * NOTE: I am using HttpMethod.getResponseBodyAsString() instead of HttpMethod.getResponseBodyAsStream(), to make it easy to parse, 
	 * unfortunately it reads the entire response at once. Is this a potential memory overflow? Since we are reading text not large
	 * binary files, such as a pdf or an image, hopefully we be OK. TBD
	 * <br>
	 * The response is of type...
	 * <br>
	 *   <?xml version="1.0" encoding="UTF-8"?>
	 *   <OAI-PMH xmlns="http://www.openarchives.org/OAI/2.0/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd">
	 *     <responseDate>2011-06-06T08:46:07Z</responseDate>
	 * 
	 *     <request verb="ListSets"  resumptionToken="">http://adr.coalliance.org/codu/fez/oai.php</request>
	 *     <ListSets> 
	 *       <set>
	 *         <setSpec>oai:adr.coalliance.org:codu:37742</setSpec>
	 *         <setName>A Nation In Time And Space</setName>
	 *       </set>
	 *       <set>
	 *             inject our aggregate set spec and name
	 *       </set> 0..n
	 *     </ListSets>
	 *   </OAI-PMH>
	 * 
	 * @param getString the get url string
	 * @param response
	 * @param authenicate
	 * @param set
	 * @param metadataPrefix
	 * @throws IOException 
	 */
	private void executeOaiListSetsWithAggregates( String getString, HttpServletResponse response, boolean authenicate ) throws IOException{

		HttpClient theClient = new HttpClient();
		
		HttpMethod method = new GetMethod( getString );
		
		AggregateList aggregateSets = new AggregateList();
		
		FedoraDatastream_get.setDefaultHeaders( method );
    	
		FedoraDatastream_get.setAdrCredentials( theClient, authenicate );
    	
		try {
			theClient.executeMethod(method);
	
			// Set the content type, as it comes from the server
			Header[] headers = method.getResponseHeaders();
			for (Header header : headers) {
	
				if ("Content-Type".equalsIgnoreCase( header.getName() )) {
					response.setContentType(header.getValue());
				}
	
				response.setHeader( header.getName(), header.getValue() ); 
			}
	
				// Write the body, flush and close
			
			InputStream is = method.getResponseBodyAsStream();
	
			String sets = method.getResponseBodyAsString();
	
			
			Iterator<AggregateSet> aggreateSetsIterator = aggregateSets.getIterator();
			StringBuffer aggSetsString = new StringBuffer();
			
			while ( aggreateSetsIterator.hasNext() )
			{
				AggregateSet currentAggSet = aggreateSetsIterator.next();
				
				aggSetsString.append( "<set>\n" );
				aggSetsString.append( "<setSpec>"+currentAggSet.getUniqueID() + "</setSpec>\n" );
				aggSetsString.append( "<setName>" + currentAggSet.getSetName()                     + "</setName>\n");
				aggSetsString.append( "</set>"   );
			}
			
	
			String allSets = sets.replace(  "<resumptionToken>", aggSetsString.toString() + "\n<resumptionToken>" );
	
			response.getWriter().print( allSets );
	
	//		response.getOutputStream().flush();
	//		response.getOutputStream().close();

		}
		finally 
		{
			method.releaseConnection();	
		}
		
	} // executeOaiListSetsWithAggregates()
	
	/**
	 * Retrieve all sets for a particular aggregate set from an AggregateList object.
	 * <br>
	 * NOTE: I am using HttpMethod.getResponseBodyAsString() instead of HttpMethod.getResponseBodyAsStream(), to make it easy to parse, 
	 * unfortunately it reads the entire response at once. Is this a potential memory overflow? Since we are reading text not large
	 * binary files, such as a pdf or an image, hopefully we be OK. TBD
	 * <br><br>
	 * All resumption token values are removed, since fedoraProxy does not know how to handle a request that uses them (in the instance
	 * of a restart due to a network error)
	 * 
	 * The response if of type...
	 * 
	 * <?xml version="1.0" encoding="UTF-8"?>
	 * <OAI-PMH xmlns="http://www.openarchives.org/OAI/2.0/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd">
	 * <responseDate>2011-06-06T10:54:51Z</responseDate>
	 *  
	 *   <request verb="ListRecords" metadataPrefix="oai_dc" resumptionToken="">http://adr.coalliance.org/codu/fez/oai.php</request>
	 *   <ListRecords>
	 *   <record>
	 *     <header>
	 *       <identifier>oai:adr.coalliance.org:codu:55689</identifier>
	 *       <datestamp>2010-09-20T14:09:34Z</datestamp>
 	 *       <setSpec>oai:adr.coalliance.org:codu:55690</setSpec>         
  	 *      </header>
	 *      <metadata>
 	 *         <oai_dc:dc xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd" xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" xmlns:dc="http://purl.org/dc/elements/1.1/">
	 * 	          <dc:title>University of Denver Alumna A. Helen Anderson</dc:title>
	 * 		       <dc:identifier>http://adr.coalliance.org/codu/fez/view/codu:55689</dc:identifier>
 	 *             <dc:description>B.A., 1914; M.A., 1931</dc:description>
  	 *             <dc:type>DU Image</dc:type>
  	 *             <dc:date>Array</dc:date>								
	 * 		       <dc:subject>Education</dc:subject>
	 * 		       <dc:subject>School integration</dc:subject>
	 * 		       <dc:subject>Busing for school integration</dc:subject>
	 * 		       <dc:publisher>University of Denver</dc:publisher>
	 * 		       <dc:relation>A. Helen Anderson Papers</dc:relation>
	 * 		       <dc:format>http://adr.coalliance.org/codu/fez/eserv/codu:55689/U201.02.0002.0023.00002.tif</dc:format>
	 * 		       <dc:rights>Copyright restrictions may apply. User is responsible for all copyright compliance.</dc:rights>
	 * 	        </oai_dc:dc>
	 * 	    </metadata>
	 *      </record>
	 *      <record>
	 *      	.......More aggregate records.
	 *      <record>
	 *      <resumptionToken></resumptionToken>
	 *   </ListRecords>
	 * </OAI-PMH>
	 * 
	 * @param verb the oai verb
	 * @param response
	 * @param authenicate
	 * @param aggregateSetName
	 * @param metadataPrefix
	 * @param forceNewDate if true the oai <datestamp> is set with todays date, to force retrieval.
	 * @throws IOException 
	 */
	private void executeOaiListAggregateSetRecords( String verb, HttpServletResponse response, boolean authenicate, String aggregateSetName, String metadataPrefix,  boolean forceNewDate ) throws IOException {
		
		AggregateList aggregateSets = new AggregateList();

		AggregateSet setToRetrieve = aggregateSets.getAggregateSet( aggregateSetName );

		if ( setToRetrieve == null ) 
		{
			return; // TBD this will create an error. return empty set?
		}
		Iterator<SingleSet> setIterator = setToRetrieve.getIterator();
		
		HttpClient theClient = new HttpClient();

		boolean firstRecordOfFirstSet = true;
		String header       = null;
		String footer       = null;
		HttpMethod method   = null;
		try {
			while ( setIterator.hasNext() )
			{	
				SingleSet singleSet = setIterator.next();

				method = new GetMethod( ADR_OAI_GET_URL + "?verb="+verb+"&set="+FedoraProxyConstants.ADR_OAI_SET_SPEC+singleSet.getUniqueID()+"&metadataPrefix="+metadataPrefix );
				theClient.executeMethod(method);				
				String recordList = method.getResponseBodyAsString();
				
				int listRecordTagLoc    = recordList.toLowerCase().indexOf( "<listrecords>");
				int listRecordEndTagLoc = recordList.toLowerCase().indexOf( "</listrecords>");
			//	int resumptionTagLoc       = recordList.toLowerCase().indexOf( "<resumptiontoken>" );
			//	int resumptionEndTagLoc    = recordList.toLowerCase().indexOf( "<resumptiontoken>" );
				
				if ( listRecordTagLoc <0 || listRecordEndTagLoc < 0  )
				{
					// TBD need to log error
					
					String errorMsg = "Error retieving: "+singleSet.getName();
					System.out.println( errorMsg );
					
					continue; // abort and move attempt next set.
				}
				
				String justTheRecords = recordList.substring( listRecordTagLoc+14, listRecordEndTagLoc );	
				justTheRecords        = justTheRecords.substring( 0, justTheRecords.indexOf( "<resumptionToken>" ));

				if ( firstRecordOfFirstSet )
				{
					header = recordList.substring( 0, listRecordTagLoc + 14 );
					
//					int verbLoc    = header.indexOf( "<verb=" );
//					int verbEndLod = header.indexOf( '>', verbLoc );
//					int requestEndLod = header.indexOf( "</request>", verbEndLod );
//					String textToReplace = header.substring( verbEndLod+1, requestEndLod);
//							
//					header.replace( textToReplace, fedoraProxyConstants.FedoraProxyOAI_URL ); // TBD this will this change!
					
					// see above notes about <resumptionToken>
					footer = "<resumptionToken></resumptionToken>\n" + recordList.substring( listRecordEndTagLoc );
					firstRecordOfFirstSet = false;
					
					response.getWriter().print( header );			
					System.out.println( header );
				}

				if ( forceNewDate )
				{
					justTheRecords = justTheRecords.replaceAll("<datestamp>\\d\\d\\d\\d-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\dZ</datestamp>", "<datestamp>2011-08-04T00:00:00Z</datestamp>" );
				}
				response.getWriter().print( justTheRecords );
				System.out.println( justTheRecords );
			}
			System.out.println( footer );
			response.getWriter().print( footer );
		}
		catch (Exception e) 
		{
			response.sendError( 404 );
		}
		finally {
			method.releaseConnection();
		}

	} // executeOaiListRecordsWithAggregates


} // OaiAggregator


