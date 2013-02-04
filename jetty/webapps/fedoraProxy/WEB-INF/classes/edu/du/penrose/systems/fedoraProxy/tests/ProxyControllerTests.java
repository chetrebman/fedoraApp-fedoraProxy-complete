package edu.du.penrose.systems.fedoraProxy.tests;


import java.io.*;

import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.multipart.FilePart;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.MultipartPostMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Test;
import org.springframework.mock.web.*;


import edu.du.penrose.systems.fedoraProxy.FedoraProxyConstants;
import edu.du.penrose.systems.fedoraProxy.util.FedoraProxyServletContextListener;
import edu.du.penrose.systems.fedoraProxy.web.bus.IngestController;

import edu.du.penrose.systems.util.FileUtil;

public class ProxyControllerTests { 
	
	private IngestController controller = new IngestController();

    String filesPath = "/home/chet/javadev/fedoraProxy/";

//    private ApplicationContext getApplicationContext() { // page 706
//        
//         String[] paths = new String[] { "./WebContent/WEB-INF/spring/app-config.xml" };
//         
//         return new FileSystemXmlApplicationContext( paths );
//    }
   
    protected void setUp() throws Exception 
    {	
    	FedoraProxyConstants.getServletContextListener().setContextTestPath( "C://home//chet//javadev//fedoraProxy//src//main//webapp/" );
    //		FedoraProxyServletContextListener.setContextTestPath( "///home//chet//javadev//fedoraProxy//WebContent/" );   
    		
    		filesPath = "/home/chet/javadev/fedoraProxy/";
    		
    		System.out.println("\nNOTE!!!: files created, in /batch_space/..,while testing MAY not be overwriteable by the real fedoraProxy program causing errors!\n");
    }


	@Test
	public void testMockMultipartHttpServletRequestWithInputStream() throws Exception 
	{	
        
		this.setUp();
		
	//	this.localTest(); 
		
		this.remoteTest();
		
	//	this.simplePostTest();
	}

	private void remoteTest() throws Exception {
		
		HttpClient client = new HttpClient();
//				
		client.getState().setCredentials(
				new AuthScope( "localhost", 7080, null ),
				new UsernamePasswordCredentials( "demo", "demoPW" ));		
	
		String batchFileSuffix = FedoraProxyConstants.ECTD_BATCH_XML_FORM_PART_NAME;

       
        String xmlFileName = "B063StillImage_update06292012.xml";        
        String pdfFileName1 = null;
        String pdfFileName2 = null;

       String weblintURL = "http://localhost:7080/fedoraProxy/du/demo/fedoraAppDemoCollection/ingest.it";

       
//*******************************************************************************************
		
		
		File batchIngestXmlFile  = new File( filesPath + xmlFileName );
		File ectdPdfFile1        = new File( filesPath + pdfFileName1 );
		File ectdPdfFile2        = new File( filesPath + pdfFileName2 );
		
		
		MultipartPostMethod method =
		    new MultipartPostMethod( weblintURL );

		method.setDoAuthentication( true );	
		
		// note Credentials were set in remoteTest();
		
		client.getParams().setAuthenticationPreemptive(true);
		
        method.addPart( new FilePart( FedoraProxyConstants.ECTD_BATCH_XML_FORM_PART_NAME, batchIngestXmlFile, IngestController.XML_CONTENT_TYPE,"ISO-8859-1" ) );
 
        if ( pdfFileName1 != null )
        method.addPart( new FilePart( pdfFileName1, ectdPdfFile1, IngestController.PDF_CONTENT_TYPE,"ISO-8859-1" ) );
        if ( pdfFileName2 != null )
        method.addPart( new FilePart( pdfFileName2, ectdPdfFile2, IngestController.PDF_CONTENT_TYPE,"ISO-8859-1" ) );

		               // Execute and print response
		client.executeMethod( method );

		method.getStatusCode();
		
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

		method.releaseConnection();
	}
	
    
    
	private void simplePostTest()
	{
		 HttpClient client = new HttpClient();
		    client.getParams().setParameter("http.useragent", "Test Client");

		    BufferedReader br = null;

		    PostMethod method = new PostMethod("http://localhost/fedoraProxy/du/demo/fedoraAppDemoCollection/ingest.ectd");
		    method.addParameter("p", "\"java2s\""); // meaningless parameter for test

		    try{
		      int returnCode = client.executeMethod(method);

		      if(returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
		        System.err.println("The Post method is not implemented by this URI");
		        // still consume the response body
		        method.getResponseBodyAsString();
		      } else {
		        br = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
		        String readLine;
		        while(((readLine = br.readLine()) != null)) {
		          System.err.println(readLine);
		      }
		      }
		    } catch (Exception e) {
		      System.err.println(e);
		    } finally {
		      method.releaseConnection();
		      if(br != null) try { br.close(); } catch (Exception fe) {}
		    }

	}


	private void localTest() throws Exception
	{
		
		String batchFileSuffix = FedoraProxyConstants.ECTD_BATCH_XML_FORM_PART_NAME;
		
		final String xmlFileName = batchFileSuffix+"_test_1.xml";

		final String pdfFileName1 = "demo_access.pdf";
		final String pdfFileName2 = "demo_access.pdf";
		final String pdfFileName3 = "demo_access.pdf";
		
		File batchIngestXmlFile = new File( filesPath + xmlFileName );
		FileInputStream fis_xml = new FileInputStream(batchIngestXmlFile);

		File ectdPdfFile1        = new File( filesPath + pdfFileName1 );
		FileInputStream fis_pdf1 = new FileInputStream( ectdPdfFile1 );
		File ectdPdfFile2        = new File( filesPath + pdfFileName2 );
		FileInputStream fis_pdf2 = new FileInputStream( ectdPdfFile2 );
		File ectdPdfFile3        = new File( filesPath + pdfFileName3 );
		FileInputStream fis_pdf3 = new FileInputStream( ectdPdfFile3 );
		
		MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
		request.setContentType("multipart/form-data");
		request.addHeader("Content-type", "multipart/form-data"); // may not be needed
		request.setMethod("POST");

		MockHttpServletResponse response = new MockHttpServletResponse();

		request.addFile(new MockMultipartFile( FedoraProxyConstants.ECTD_BATCH_XML_FORM_PART_NAME, xmlFileName, IngestController.XML_CONTENT_TYPE, fis_xml ));
		request.addFile(new MockMultipartFile( FedoraProxyConstants.ECTD_PDF_FORM_PART_NAME+"_1", pdfFileName1, IngestController.PDF_CONTENT_TYPE, fis_pdf1 ));
		request.addFile(new MockMultipartFile( FedoraProxyConstants.ECTD_PDF_FORM_PART_NAME+"_2", pdfFileName2, IngestController.PDF_CONTENT_TYPE, fis_pdf2 ));
		request.addFile(new MockMultipartFile( FedoraProxyConstants.ECTD_PDF_FORM_PART_NAME+"_3", pdfFileName3, IngestController.PDF_CONTENT_TYPE, fis_pdf3 ));
		
		this.controller.handlePost( "demo", "fedoraAppDemoCollection", request, response );
	}
	
	
} // ProxyControllerTest
