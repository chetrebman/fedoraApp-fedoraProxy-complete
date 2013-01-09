/*
 * Copyright 2012 University of Denver
 * Author Chet Rebman
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
package edu.du.penrose.systems.util;

import java.util.*;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

public class HttpClientUtils_2 {

	static int NOT_INITIALIZED = -1;
	static String EMPTY_STRING = "";

	final static String SESSION_ID        = "__sid";
	
	final static Logger logger = Logger.getLogger( "org.apache.commons.httpclient.HttpClient" );
	
	public static class WebPage 
	{
		int status     = NOT_INITIALIZED;
		String webPage = EMPTY_STRING;
		public int getStatus() {
			return status;
		}
		public void setStatus(int status) {
			this.status = status;
		}
		public String getWebPage() {
			return webPage;
		}
		public void setWebPage(String webPage) {
			this.webPage = webPage;
		}
	}
	
	static public WebPage academicSearch( URL initialLink )
	{
		WebPage wp = new WebPage();
		HttpClient client = new HttpClient();
		GetMethod getMethod  = new GetMethod( initialLink.toString() );
		
		getMethod.setFollowRedirects( true );

		try {
			
//			System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
//			System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
//			System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "debug");
//			System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "debug");
//			
			getMethod.setRequestHeader("User-Agent", "Mozilla/5.001 (windows; U; NT4.0; en-US; rv:1.0) Gecko/25250101" ); // in case they check
			int statusCode  = client.executeMethod( getMethod );
			String contents = getMethod.getResponseBodyAsString();
			String formPage = getMethod.getURI().toString(); // page we were sent to.
			wp.setStatus( statusCode );
			wp.setWebPage( contents );
//			System.out.println();System.out.println();System.out.println();System.out.println();System.out.println(contents);	
			

String sid = getSID( contents );
String postURL = "http://web.ebscohost.com/ehost/search?vid=1&hid=9&sid="+sid;

			PostMethod postMethod = new PostMethod( "http://web.ebscohost.com/ehost/search?vid=1&hid=9&sid=c3220986-66e4-4fd1-ba6a-30504f3694ad%40sessionmgr13" );
			postMethod.setRequestHeader("User-Agent", "Mozilla/5.001 (windows; U; NT4.0; en-US; rv:1.0) Gecko/25250101" ); // in case they check                             
			
			// postMethod.setFollowRedirects( true ); causes error
			postMethod.setParameter( SESSION_ID, sid );
			
			setResolution( postMethod, contents );
			setNonchangingFields( postMethod );
			
			statusCode  = client.executeMethod( postMethod );
			
			if ( statusCode > 300 ){
				String resultsPage = getMethod.getURI().toString(); // page we were sent to.
				 getMethod  = new GetMethod( resultsPage );
				 getMethod.setRequestHeader("User-Agent", "Mozilla/5.001 (windows; U; NT4.0; en-US; rv:1.0) Gecko/25250101" ); // in case they check
				  statusCode  = client.executeMethod( getMethod );
				  
					contents = getMethod.getResponseBodyAsString();
					wp.setStatus( statusCode );
					wp.setWebPage( contents );
			}
			contents = postMethod.getResponseBodyAsString();
			
			
			wp.setStatus( statusCode );
			wp.setWebPage( contents );
			
			System.out.println();System.out.println();System.out.println();System.out.println();System.out.println(contents);	
		}
		catch (Exception e)
		{
			System.out.println( e );
		}
		finally 
		{
			getMethod.releaseConnection();
		}
		return wp;
	}

	static String getSID( String formContents )
	{
		
		String[] sidStuff        = formContents.split( SESSION_ID );
		String sidStuff2 = sidStuff[2].substring(1, sidStuff[2].indexOf(">") );
		String sid = sidStuff2.substring( sidStuff2.indexOf("\"")+1, sidStuff2.lastIndexOf("\"") );
		
		
		return sid;
	}
	
	static void setResolution( PostMethod postMethod, String formContents )
	{
		final String SCREEN_RESOLUTION = "__ScreenResolution";
		
		String[] resolutionStuff = formContents.split( SCREEN_RESOLUTION );

//		String resolution = resolutionStuff[1].substring(0, resolution[1].indexOf(">") );  // value gets set by javascript.

		postMethod.setParameter( SCREEN_RESOLUTION, "1280 : 1024" );  // TBD see above
		
	}

	static PostMethod setChangehangingFields( PostMethod postMethod, String formContents )
	{
		final String SESSION_ID        = "__sid";
		final String SCREEN_RESOLUTION = "__ScreenResolution";
		
		String[] sidStuff        = formContents.split( SESSION_ID );
		String[] resolutionStuff = formContents.split( SCREEN_RESOLUTION );

		String sidStuff2 = sidStuff[2].substring(1, sidStuff[2].indexOf(">") );
		String sid = sidStuff2.substring( sidStuff2.indexOf("\"")+1, sidStuff2.lastIndexOf("\"") );
		
//		String resolution = resolutionStuff[1].substring(0, resolution[1].indexOf(">") );  // value gets set by javascript.

		postMethod.setParameter( SESSION_ID, sid );
		postMethod.setParameter( SCREEN_RESOLUTION, "1280 : 1024" );  // TBD see above
		
		return postMethod;
	}
	
	static PostMethod setNonchangingFields( PostMethod postMethod )
	{
		postMethod.setParameter( "__EVENTTARGET", "" );
		postMethod.setParameter( "__EVENTARGUMENT", "" );
		postMethod.setParameter( "CurrentSearchMode", "Bool" );
		postMethod.setParameter( "FirstFindFieldIdHolder", "ctl00_ctl00_BaseFindField_FindField_ctl00_guidedFields_fieldRepeater_ctl01_SearchTerm" );
		postMethod.setParameter( "FirstFindFieldTagIdHolder", "FirstFindFieldTagIdHolder	ctl00_ctl00_BaseFindField_FindField_ctl00_guidedFields_fieldRepeater_ctl01_DbTag" );
		postMethod.setParameter( "FirstFindFieldInLabelHolder", "ctl00_ctl00_BaseFindField_FindField_ctl00_guidedFields_fieldRepeater_ctl01_inLabel" );
		postMethod.setParameter( "FirstFindFieldRowMeasure", "ctl00_ctl00_BaseFindField_FindField_ctl00_guidedFields_fieldRepeater_ctl02_fieldRow" );
		postMethod.setParameter( "__vid", "1" );
		postMethod.setParameter( "__CUSTOMVIEWSTATE", "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" );
		postMethod.setParameter( "__ScreenResolution", "1280 : 1024" );
		postMethod.setParameter( "__VIEWSTATE", "" );
		postMethod.setParameter( "ajax", "enabled" );
		postMethod.setParameter( "ctl00$ctl00$ToolbarArea$toolbar$toolbarNavControl$folderControl$folderHasItemsTitle", "Folder has items" );
		postMethod.setParameter( "ctl00$ctl00$ToolbarArea$toolbar$toolbarNavControl$folderControl$folderHasNoItemsTitle", "Folder is empty" );
		postMethod.setParameter( "ctl00$ctl00$BaseFindField$FindField$ctl00$guidedFields$fieldRepeater$ctl01$SearchTerm", "finance" );
		postMethod.setParameter( "ctl00$ctl00$BaseFindField$FindField$ctl00$guidedFields$fieldRepeater$ctl01$DbTag", "" );
		postMethod.setParameter( "ctl00$ctl00$BaseFindField$FindField$ctl00$guidedFields$fieldRepeater$ctl02$Op", "and" );
		postMethod.setParameter( "ctl00$ctl00$BaseFindField$FindField$ctl00$guidedFields$fieldRepeater$ctl02$SearchTerm", "" );
		postMethod.setParameter( "ctl00$ctl00$BaseFindField$FindField$ctl00$guidedFields$fieldRepeater$ctl02$DbTag", "" );
		postMethod.setParameter( "ctl00$ctl00$BaseFindField$FindField$ctl00$guidedFields$fieldRepeater$ctl03$Op", "and" );
		postMethod.setParameter( "ctl00$ctl00$BaseFindField$FindField$ctl00$guidedFields$fieldRepeater$ctl03$SearchTerm", "" );
		postMethod.setParameter( "ctl00$ctl00$BaseFindField$FindField$ctl00$guidedFields$fieldRepeater$ctl03$DbTag", "" );
		postMethod.setParameter( "ctl00$ctl00$BaseFindField$FindField$ctl00$SearchButton", "Search" );
		postMethod.setParameter( "searchMode", "Bool" );
		postMethod.setParameter( "common_DT1", "" );
		postMethod.setParameter( "common_DT1_FromYear	", "" );
		postMethod.setParameter( "common_DT1_ToMonth	", "" );
		postMethod.setParameter( "common_DT1_ToYear	", "" );
		postMethod.setParameter( "common_SO", "" );
		postMethod.setParameter( "common_PT82", "" );
		postMethod.setParameter( "common_PZ1", "" );
		postMethod.setParameter( "common_LA10", "" );
		postMethod.setParameter( "common_PG4", "" );
		postMethod.setParameter( "common_PG4_NumVal	", "" );
		
		return postMethod;
	}
	
	
	
	/**
	 * Check if a web page exists, Redirects are NOT followed.
	 * 
	 * @param pageToGet
	 * @return
	 */
	static public WebPage getPage( URL pageToGet )
	{
		WebPage wp = new WebPage();
		HttpClient client = new HttpClient();
		GetMethod method  = new GetMethod( pageToGet.toString() );
		method.setFollowRedirects( false );

		try {
			int statusCode  = client.executeMethod( method );

			String contents = method.getResponseBodyAsString();

			
			
			wp.setStatus( statusCode );
			wp.setWebPage( contents );
		}
		catch (Exception e)
		{
			// TBD
		}
		finally 
		{
			method.releaseConnection();
		}
		return wp;
	}

	static	public boolean checkPageExists( URL urlToCheck )
	{
			switch ( getPage( urlToCheck ).getStatus() )
			{
			case 200: return true;  
			case 404: return false;
			default:  return false;
			}
	}

} // HttpClientUtils
