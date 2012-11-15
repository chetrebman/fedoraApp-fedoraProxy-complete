import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.MultipartPostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;


public class PostBatchFile {

	static public final String XML_CONTENT_TYPE = "text/xml";
	static public final String XML_FILE_SUFFIX = ".xml";

	static public final String PDF_CONTENT_TYPE = "application/pdf";
	static public final String PDF_FILE_SUFFIX = ".pdf";

	public static final String ECTD_BATCH_XML_FORM_PART_NAME = "batch_";

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args)  {
	
		try {
			switch( args.length  )
			{
			case 4:
				performPost( args[0], args[1], args[2], args[3], new String[]{} );
				break;
			case 5:
				performPost( args[0], args[1], args[2], args[3], new String[]{ args[4] } );
				break;
			case 6:
				performPost( args[0], args[1], args[2], args[3], new String[]{ args[4], args[5] } );
				break;
			default:
				throw new Exception( "USAGE: user, password, url, batchFileName, attachedFile 1(optional),  attachedFile 2(optional) " );
			}
		} catch (Exception e) 
		{
			e.printStackTrace();
		}


	} // main

	/**
	 * 
	 * @param postUrl 
	 * @param xmlFileName the batch file.
	 * @param attchedFiles zero to two attached files 
	 * @throws Exception
	 */
	static private void performPost( String userName, String password, String postURL, String xmlFileName, String[] attchedFiles ) throws Exception
	{
		URL postUrl = new URL(postURL); 
		
		String pdfFileName1 = null;
		String pdfFileName2 = null;
		if ( attchedFiles != null )
		{
			if ( attchedFiles.length > 0 ){ pdfFileName1 = attchedFiles[0]; }
			if ( attchedFiles.length > 1 ){ pdfFileName2 = attchedFiles[1]; }
		}
	

//		String filesPath = "/home/chet/javadev/fedoraProxy/";
		String filesPath = "";

		File batchIngestXmlFile  = new File( filesPath + xmlFileName );
		if ( ! batchIngestXmlFile.exists() )
		{
			throw new Exception( "batch file not found" );
		}
		
		File ectdPdfFile1        = new File( filesPath + pdfFileName1 );
		File ectdPdfFile2        = new File( filesPath + pdfFileName2 );

		HttpClient client = new HttpClient();

		client.getState().setCredentials(
				new AuthScope( postUrl.getHost(), postUrl.getPort(), null ),
				new UsernamePasswordCredentials( userName, password ));

		MultipartPostMethod method =
			new MultipartPostMethod( postURL );

		method.setDoAuthentication( true );	

		client.getParams().setAuthenticationPreemptive(true);

		try {
			method.addPart( new FilePart( PostBatchFile.ECTD_BATCH_XML_FORM_PART_NAME, batchIngestXmlFile, PostBatchFile.XML_CONTENT_TYPE,"ISO-8859-1" ) );

			if ( pdfFileName1 != null )
				method.addPart( new FilePart( pdfFileName1, ectdPdfFile1, PostBatchFile.PDF_CONTENT_TYPE,"ISO-8859-1" ) );
			if ( pdfFileName2 != null )
				method.addPart( new FilePart( pdfFileName2, ectdPdfFile2, PostBatchFile.PDF_CONTENT_TYPE,"ISO-8859-1" ) );

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
		}
		catch ( Exception e ){
			e.printStackTrace();
		}
		finally {	
			method.releaseConnection();
		}
	}

} // PostBatchFile
