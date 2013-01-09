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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

public class HttpClientUtils {

	/**
	 * Appends response form URL to a StringBuffer
	 * 
	 * @param requestUrl
	 * @param resultStringBuffer
	 * @return int request status code OR -1 if an exception occurred
	 */
	static public int getAsString( String requestUrl, StringBuffer resultStringBuffer )
	{
		HttpClient client = new HttpClient();

		//	client.getState().setCredentials(
		//			new AuthScope("localhost", 7080, null ),
		//			new UsernamePasswordCredentials("nation", "nationPW") 
		//     );
		//	client.getParams().setAuthenticationPreemptive(true);

		HttpMethod method =  new GetMethod( requestUrl );

		// method.setDoAuthentication( true );	
		// client.getParams().setAuthenticationPreemptive(true);

		// Execute and print response
		try {
			client.executeMethod( method );
			InputStream is = method.getResponseBodyAsStream();
			BufferedInputStream bis = new BufferedInputStream( is );

			String datastr = null;
			byte[] bytes = new byte[ 8192 ]; // reading as chunk of 8192 bytes
			int count = bis.read( bytes );
			while( count != -1 && count <= 8192 )
			{
				datastr = new String(bytes, 0, count);
				resultStringBuffer.append(datastr);
				count = bis.read( bytes );
			}
			bis.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			method.releaseConnection();
		}
		
		return method.getStatusCode();
	}
	
	/**
	 * Get file from URL, directories are created and files overwritten.
	 * 
	 * 
	 * @deprecated use  org.apache.commons.io.FileUtils.copyURLToFile(URL, File)
	 * @param requestUrl
	 * @param outputPathAndFileName
	 *
	 * @return int request status code OR -1 if an exception occurred
	 */
	static public int getToFile( String requestUrl, String outputPathAndFileName )
	{
		int resultStatus = -1;
		
		File outputFile = new File( outputPathAndFileName );
		String outputPath = outputFile.getAbsolutePath().replace( outputFile.getName(), "" );
		File outputDir = new File( outputPath );
		if ( ! outputDir.exists() )
		{
			outputDir.mkdir();
		}
		
		HttpClient client = new HttpClient();

		//	client.getState().setCredentials(
		//			new AuthScope("localhost", 7080, null ),
		//			new UsernamePasswordCredentials("nation", "nationPW") 
		//     );
		//	client.getParams().setAuthenticationPreemptive(true);

		HttpMethod method =  new GetMethod( requestUrl );

		//	method.setDoAuthentication( true );	
		//  client.getParams().setAuthenticationPreemptive(true);

		// Execute and print response
		try {
			
			OutputStream os = new FileOutputStream( outputFile );
			
			client.executeMethod( method );
			InputStream is = method.getResponseBodyAsStream();
			BufferedInputStream bis = new BufferedInputStream( is );

			byte[] bytes = new byte[ 8192 ]; // reading as chunk of 8192 bytes
			int count = bis.read( bytes );
			while( count != -1 && count <= 8192 )
			{
				os.write( bytes, 0, count );
				count = bis.read( bytes );
			}
			bis.close();
			os.close();
			resultStatus = method.getStatusCode();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			method.releaseConnection();
		}
		
		return resultStatus;
	}
	

	public boolean testHttpConnection()
	{
		boolean result = true;

		HttpClient httpclient = new HttpClient();
		GetMethod httpget = new GetMethod("https://www.verisign.com/"); 
		try { 
			try 
			{
				httpclient.executeMethod(httpget);
			} 
			catch ( Exception e) 
			{
				result = false;
			} 
			
			System.out.println(httpget.getStatusLine());
			
		} finally {
			httpget.releaseConnection();
		}	  
		
		if ( httpget.getStatusCode() != 200 )
		{
			result = false;
		}
		
		return result;
	}
	
} // HttpClientUtils
