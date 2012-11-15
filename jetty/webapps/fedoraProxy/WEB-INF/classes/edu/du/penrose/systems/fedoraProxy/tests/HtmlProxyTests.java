package edu.du.penrose.systems.fedoraProxy.tests;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.io.File;
import java.util.ArrayList;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.MultipartPostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraProxy.FedoraProxyConstants;
import edu.du.penrose.systems.fedoraProxy.web.bus.FedoraDatastream_get;
import edu.du.penrose.systems.fedoraProxy.web.bus.IngestController;

import edu.du.penrose.systems.util.FileUtil;

public class HtmlProxyTests {

	private FedoraDatastream_get controller = new FedoraDatastream_get();

	@Test
	public void testMockMultipartHttpServletRequestWithInputStream() throws IOException 
	{	
			// all test require /opt/fedora/fedora to be running
		
		// localTest();

			// expects /opt/tomcat_no_fedora/fedoraProxy to be running
	//	remoteTest( "http://lib-ram.cair.du.edu/du/fedoraProxy/datastream.get?id=codu:113/DC" );

			// expects the eclipse fedoraProxy to be running
		remoteTest( "http://localhost:7080/fedoraProxy/du/nation/datastream.get?id=codu:maps/DC" );
	}
	
	private void localTest() 
	{
		MockHttpServletRequest request = new MockHttpServletRequest();
		// request.setContentType("multipart/form-data");
		// request.addHeader("Content-type", "multipart/form-data"); // may not be needed
		request.setMethod("GET");
		
		// localhost:7080/fedoraProxy/dataStream.htm?url=/fedora/get/co:929/DC
		
		String requestUrl = "/du/nation/codu:maps/DC";
		request.setRequestURI( requestUrl );

		MockHttpServletResponse response = new MockHttpServletResponse();

		
		try {
			this.controller.proxyCall( requestUrl, "nation", null, request, response );

			System.out.println( response.getContentAsString() );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void remoteTest( String requestUrl )
	{	
		
		  HttpClient httpclient = new HttpClient();
		  GetMethod httpget = new GetMethod("https://www.verisign.com/"); 
		  try { 
		    try {
				httpclient.executeMethod(httpget);
			} catch (HttpException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    System.out.println(httpget.getStatusLine());
		  } finally {
		    httpget.releaseConnection();
		  }
		  
		HttpClient client = new HttpClient();
			
		client.getState().setCredentials(
				new AuthScope("localhost", 7080, null ),
				new UsernamePasswordCredentials("nation", "nationPW") 
        );
		client.getParams().setAuthenticationPreemptive(true);
		
		HttpMethod method =  new GetMethod( requestUrl );
			
		
		method.setDoAuthentication( true );	
		client.getParams().setAuthenticationPreemptive(true);
		
		               // Execute and print response
		try {
			client.executeMethod( method );
			InputStream is = method.getResponseBodyAsStream();
			BufferedInputStream bis = new BufferedInputStream( is );

			String datastr = null;
			StringBuffer sb = new StringBuffer();
			byte[] bytes = new byte[ 8192 ]; // reading as chunk of 8192 bytes
			int count = bis.read( bytes );
			while( count != -1 && count <= 8192 )
			{
				datastr = new String(bytes, 0, count);
				sb.append(datastr);
				count = bis.read( bytes );
			}
			bis.close();
			String response = sb.toString();

			System.out.println( response );

		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			
			method.releaseConnection();
		}


	}
}
