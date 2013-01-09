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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.du.penrose.systems.fedora.ResourceIndexUtils;
// import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraApp.ProgramFileProperties;
import edu.du.penrose.systems.fedoraApp.ProgramProperties;
import edu.du.penrose.systems.fedoraProxy.FedoraProxyConstants;
import edu.du.penrose.systems.fedoraProxy.util.FedoraProxyServletContextListener;


/**
 * The top level proxy controller. Implements most of the code needed to perform a proxy call.<br><br>
 * REQUIRES that FEDORA_HOST, * FEDORA_PORT, FEDORA_USER, FEDORA_PWD, SOLR_HOST and SOLR_PORT are defined 
 * in the batchIngest.properties file!!<br>
 * @author chet
 *
 */
abstract public class ProxyController {

	/** 
	 * Logger for this class and subclasses.
	 */
	protected final static Log logger = LogFactory.getLog( "edu.du.penrose.systems.fedoraProxy.web.bus.ProxyController" );

	private static ProgramProperties websiteCollectionPropertiesFile = null;

	protected static String FEDORA_HOST  = null;
	protected static    int FEDORA_PORT  = 8080;
	protected static String FEDORA_REALM = null; // "FEDORA_REALM is actually Fedora or Fedora Repository Server"
	protected static String FEDORA_USER  = null;
	protected static String FEDORA_PWD   = null;

	protected static String  SOLR_HOST = null;
	protected static int     SOLR_PORT = 8080;
	private   static String SOLR_REALM = null;

	private String FEDORA_DATASTREAM_GET_CMD = null;

	/**
	 * Load the fedora host and solr host information from the program properties file.<br><br>
	 * REQUIRES that FEDORA_HOST, * FEDORA_PORT, FEDORA_USER, FEDORA_PWD, SOLR_HOST and SOLR_PORT are defined 
	 * in the batchIngest.properties file!!<br>
	 * 
	 * @see FedoraAppConstants#getProgramPropertiesURL()
	 * @see FedoraAppConstants.PROPERTIES_FILE_NAME
	 */
	public ProxyController() 
	{
		if (FEDORA_HOST == null || FEDORA_USER == null || FEDORA_PWD == null || SOLR_HOST == null  ) 
		{
			FEDORA_HOST = ProgramProperties.getInstance(
					FedoraProxyConstants.getServletContextListener().getFedoraProxyProgramPropertiesURL()).getProperty(
							FedoraProxyConstants.FedoraProxy_FEDORA_HOST_PROPERTY);
			FEDORA_PORT = Integer.valueOf(ProgramProperties.getInstance(
					FedoraProxyConstants.getServletContextListener().getFedoraProxyProgramPropertiesURL()).getProperty(
							FedoraProxyConstants.FedoraProxy_FEDORA_PORT_PROPERTY));
			FEDORA_USER = ProgramProperties.getInstance(
					FedoraProxyConstants.getServletContextListener().getFedoraProxyProgramPropertiesURL()).getProperty(
							FedoraProxyConstants.FedoraProxy_FEDORA_USER_PROPERTY);
			FEDORA_PWD = ProgramProperties.getInstance(
					FedoraProxyConstants.getServletContextListener().getFedoraProxyProgramPropertiesURL()).getProperty(
							FedoraProxyConstants.FedoraProxy_FEDORA_PWD_PROPERTY);

			SOLR_HOST = ProgramProperties.getInstance(
					FedoraProxyConstants.getServletContextListener().getFedoraProxyProgramPropertiesURL()).getProperty(
							FedoraProxyConstants.SOLR_HOST_PROPERTY);
			SOLR_PORT = Integer.valueOf(ProgramProperties.getInstance(
					FedoraProxyConstants.getServletContextListener().getFedoraProxyProgramPropertiesURL()).getProperty(
							FedoraProxyConstants.SOLR_PORT_PROPERTY));
		}
	}

	/**
	 * Get the collection pid based on the web-site portion of the URL. This is
	 * then used to make sure that datastream's are only served up for objects
	 * within the collection. The collection PID is mapped to the web site name
	 * in the websiteCollection.Properties file.
	 * 
	 * @param webSite
	 * @return the collection pid or null if not found.
	 */
	protected String getCollectionPidForWebSite(String webSite) {

		ProgramProperties myFile = getWebsiteCollectionPropertesFile();

		String collectionPid = myFile.getProperty(webSite);

		return collectionPid;
	}

	static public ProgramProperties  getWebsiteCollectionPropertesFile()
	{
		if (websiteCollectionPropertiesFile == null) {
			websiteCollectionPropertiesFile = new ProgramFileProperties(
					new File( FedoraProxyConstants.getServletContextListener().getAppRealPath()
							+ FedoraProxyServletContextListener.CONFIG_RELATIVE_URI_PATH
							+ FedoraProxyConstants.WEBSITE_COLLECTION_FILE));
		}

		return websiteCollectionPropertiesFile;
	}


	public static void setDefaultHeaders( HttpMethod method ) {

		method.setRequestHeader(
				"Accept",
				"application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
		method.setRequestHeader("Connection", "keep-alive");
		method.setRequestHeader("Accept-Encoding", "gzip,deflate,sdch");
		method.setRequestHeader("Accept-Language", "en-US,en;q=0.8");
		method.setRequestHeader("Accept-Charset",
				"ISO-8859-1,utf-8;q=0.7,*;q=0.3");
		method.setRequestHeader("Cache-Control", "max-age=0");

		method.getParams().setCookiePolicy(CookiePolicy.RFC_2109);

		// response.setCharacterEncoding( "gzip,deflate" );
		// response.setHeader( "Accept-Charset",
		// "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
		// response.setHeader( "Accept-Language", "en-us,en;q=0.5" );

	}

	/**
	 * 
	 * @param theClient
	 * @param authenicate true/false set preemptive authentication
	 */
	public static void setAdrCredentials(HttpClient theClient, boolean authenicate) 
	{
		theClient.getState().setCredentials(
				new AuthScope(FEDORA_HOST, FEDORA_PORT, FEDORA_REALM),
				new UsernamePasswordCredentials(FEDORA_USER, FEDORA_PWD));

		//	method.setDoAuthentication(authenicate);
		theClient.getParams().setAuthenticationPreemptive(true);
	}

	/**
	 * Call the HttpMethod and write all results and status to the HTTP response
	 * object.
	 * 
	 * THIS IS THE REQUEST WE NEED TO DUPLICATE GET
	 * /fedora/get/codu:72/ECTD_test_1_access.pdf HTTP/1.1 Host: localhost:8080
	 * Connection: keep-alive Accept:
	 * application/xml,application/xhtml+xml,text/
	 * html;q=0.9,text/plain;q=0.8,image/png,*;q=0.5 User-Agent: Mozilla/5.0
	 * (X11; U; Linux x86_64; en-US) AppleWebKit/534.10 (KHTML, like Gecko)
	 * Chrome/8.0.552.215 Safari/534.10 Accept-Encoding: gzip,deflate,sdch
	 * Accept-Language: en-US,en;q=0.8 Accept-Charset:
	 * ISO-8859-1,utf-8;q=0.7,*;q=0.3 Cookie: fez_list=YjowOw%3D%3D
	 * 
	 * ACTUAL SENT GET /fedora/get/codu:72/ECTD_test_1_access.pdf HTTP/1.1
	 * Accept:
	 * application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q
	 * =0.8,image/png,*;q=0.5 Connection: keep-alive Accept-Encoding:
	 * gzip,deflate,sdch Accept-Language: en-US,en;q=0.8 Accept-Charset:
	 * ISO-8859-1,utf-8;q=0.7,*;q=0.3 : User-Agent: Jakarta
	 * Commons-HttpClient/3.1 Host: localhost:8080
	 * 
	 * @param proxyCommand
	 * @param response
	 * @param authenicate true to set Preemptive authentication
	 */
	public static void executeMethod( String proxyCommand, HttpServletResponse response, boolean authenicate )
	{
		HttpClient theClient = new HttpClient();
		
		HttpMethod method  = new GetMethod( proxyCommand );
		
		setDefaultHeaders(method);
		
		setAdrCredentials( theClient, authenicate);
		
		try 
		{
			theClient.executeMethod(method);
			response.setStatus( method.getStatusCode() );

			// Set the content type, as it comes from the server
			Header[] headers = method.getResponseHeaders();
			for (Header header : headers) {

				if ("Content-Type".equalsIgnoreCase(header.getName())) {
					response.setContentType(header.getValue());
				}

				/**
				 * Copy all headers, except Transfer-Encoding which is getting set
				 * set elsewhere. At this point response.containsHeader(
				 * "Transfer-Encoding" ) is false, however it is getting set twice
				 * according to wireshark, therefore we do not set it here.
				 */
				if ( ! header.getName().equalsIgnoreCase("Transfer-Encoding") ) {
					response.setHeader( header.getName(), header.getValue() );
				}
			}

			// Write the body, flush and close

			InputStream is = method.getResponseBodyAsStream();

			BufferedInputStream bis = new BufferedInputStream(is);

			StringBuffer sb = new StringBuffer();
			byte[] bytes = new byte[8192]; // reading as chunk of 8192 bytes
			int count = bis.read(bytes);
			while (count != -1 && count <= 8192) {
				response.getOutputStream().write(bytes, 0, count);
				count = bis.read(bytes);
			}

			bis.close();
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (Exception e) 
		{
			logger.error( e.getMessage() );
		}
		finally {
			method.releaseConnection();
		}
	}


	/**
	 * This is the top-level function for calling the proxy specified in the proxyCommand. All results and status are written to
	 * the HTTP response object, which is returned to the calling client.
	 * 
	 * @param webSite used to retrieve the webSite collection pid from the webSiteCollection.properties file, if not found a response status of 404 is returned.
	 * @param objectPID optional, can be null, if set we verify the object is contained within the webSite collection.
	 * if it isn't, a response status of 404 is returned.
	 * @param proxyCommand
	 * @param response the http response object.
	 */
	public void performProxyCall( String webSite, String objectPID, String proxyCommand, HttpServletResponse response )
	{
		String collectionPid = this.getCollectionPidForWebSite( webSite );
		boolean exists = true;
		if ( objectPID != null)
		{
			exists = ResourceIndexUtils.isObjectInCollection( FEDORA_HOST, FEDORA_PORT, FEDORA_USER, FEDORA_PWD, collectionPid, objectPID );
		}

		if ( collectionPid != null && exists ) {

			logger.info( "Proxy command recieved:" + proxyCommand );

			executeMethod( proxyCommand, response, true );

		} else 
		{
			response.setStatus(404);  
		}

	}

}
