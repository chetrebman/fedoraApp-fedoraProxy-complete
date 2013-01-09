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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.SimpleEmail;

import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestOptions;


public class SendMail
{
	/** 
	 * Logger for this class and subclasses.
	 */
	static protected final Log logger = LogFactory.getLog("edu.du.penrose.systems.util.SendMail");



	/**
	 * NOTE VERY WELL!! The sends an ssl email, when used with Fedora libraries this throws a SSL Exception, in order to fix this
	 * The following SSL system properties are cleared and then restored after the email is sent. l...<br>
	 * 
	 * 	System.clearProperty( "javax.net.ssl.keyStore" );
	 *  System.clearProperty( "javax.net.ssl.keyStorePassword" );
	 *  System.clearProperty( "javax.net.ssl.keyStoreType" );
	 *  System.clearProperty( "javax.net.ssl.trustStore" );
	 *  System.clearProperty( "javax.net.ssl.trustStorePassword" );
	 *  System.clearProperty( "javax.net.ssl.trustStoreType" );
	 *  
	 * @param recipients
	 * @param subject
	 * @param message
	 * @param from
	 * @param smptServerHost
	 * @param smtpUser
	 * @param smtpPassword
	 * @param port
	 * @param sslEmail
	 */
	public  static void postMailWithAuthenication( String recipients[], String subject,
			String message , String from, String smptServerHost, String smtpUser, String smtpPassword, String port, boolean sslEmail ) 
	{
		if ( from == null || ! from.contains( "@") )
		{
			logger.info( "Unable to send email, missing from address.");
			return;
		}

		String user     = smtpUser.trim();
		String password = smtpPassword.trim();


		int numberOfValidRecipients = 0;
		for ( int i=0; i<recipients.length; i++ )
		{
			if ( recipients[i] != null && recipients[i].length() > 0 && recipients[i].contains(  "@" ) )
			{
				numberOfValidRecipients++;
			}
		}

		if ( numberOfValidRecipients == 0 )
		{
			logger.info( "Unable to send email, missing recipients address.");
			return;
		}

		SimpleEmail email = new SimpleEmail();
		email.setSSL( sslEmail );
		email.setSmtpPort( Integer.valueOf( port ) );
		email.setAuthentication(user, password);
		email.setHostName( smptServerHost );

		try {
			for ( int i=0; i < numberOfValidRecipients; i++){
				email.addTo( recipients[i]);
			}
			email.setFrom( from  );
			email.setSubject( subject );
			email.setMsg( message );

		    //	System.setProperty( "javax.net.debug", "ssl" );
			
			String keyStore           = System.getProperty( "javax.net.ssl.keyStore" );
			String keyStorePassword   = System.getProperty( "javax.net.ssl.keyStorePassword" );
			String keyStoreType       = System.getProperty( "javax.net.ssl.keyStoreType" );
			String trustStore         = System.getProperty( "javax.net.ssl.trustStore" );
			String trustStorePassword = System.getProperty( "javax.net.ssl.trustStorePassword" );
			String trustStoreType     = System.getProperty( "javax.net.ssl.trustStoreType" );
			
		
			System.clearProperty( "javax.net.ssl.keyStore" );
			System.clearProperty( "javax.net.ssl.keyStorePassword" );
			System.clearProperty( "javax.net.ssl.keyStoreType" );
			System.clearProperty( "javax.net.ssl.trustStore" );
			System.clearProperty( "javax.net.ssl.trustStorePassword" );
			System.clearProperty( "javax.net.ssl.trustStoreType" );
			
			email.send();

			System.setProperty(  "javax.net.ssl.keyStore",           keyStore );
			System.setProperty(  "javax.net.ssl.keyStorePassword",   keyStorePassword );
			System.setProperty(  "javax.net.ssl.keyStoreType",       keyStoreType );
			System.setProperty(  "javax.net.ssl.trustStore",         trustStore );
			System.setProperty(  "javax.net.ssl.trustStorePassword", trustStorePassword );
			System.setProperty(  "javax.net.ssl.trustStoreType",     trustStoreType );
		
		} catch ( Exception e) {
			logger.error( "ERROR sending email:"+e.getLocalizedMessage() );
		}

	
	}


	/**
	 * Sends email to the email failure addresses contained in bathOptions
	 * 
	 * @param batchOptions
	 * @param subject
	 * @param message
	 * @param from
	 * @throws Exception
	 */
	public  static  void sendFailureEmail( BatchIngestOptions batchOptions, String subject, String message ) throws Exception
	{		
		if ( batchOptions.getStmpHost()         == null || batchOptions.getStmpHost().length() == 0 ){ return; }
		if ( batchOptions.getStmpPort()         == null || batchOptions.getStmpPort().length() == 0 ){ return; }
		if ( batchOptions.getStmpUser()         == null || batchOptions.getStmpUser().length() == 0 ){ return; }
		if ( batchOptions.getStmpPassword()     == null || batchOptions.getStmpPassword().length() == 0 ){ return; }
		if ( batchOptions.getEmailFromAddress() == null || batchOptions.getEmailFromAddress().length() == 0 ){ return; }
	
		String[]  recipientArray = new String[2];	 

		recipientArray[0] = batchOptions.getFailureEmail(); 
		recipientArray[1] = batchOptions.getFailureEmail_2(); 

		String smptServerHost = batchOptions.getStmpHost().trim();
		String smptServerPort = batchOptions.getStmpPort().trim();
		String smtpUser       = batchOptions.getStmpUser().trim();
		String smtpPassword   = batchOptions.getStmpPassword().trim();
		boolean sslEmail      = batchOptions.isStmpUseSSL();
		String fromAddress    = batchOptions.getEmailFromAddress().trim();

		postMailWithAuthenication( recipientArray, subject, message, fromAddress, smptServerHost, smtpUser, smtpPassword, smptServerPort, sslEmail );	  
	}
	

	/**
	 * Sends email to the email success addresses contained in bathOptions
	 * 
	 * @param batchOptions
	 * @param subject
	 * @param message
	 * @param from
	 * @throws Exception
	 */
	public  static  void sendSuccessEmail( BatchIngestOptions batchOptions, String subject, String message ) throws Exception
	{
		if ( batchOptions.getStmpHost()         == null || batchOptions.getStmpHost().length() == 0 ){ return; }
		if ( batchOptions.getStmpPort()         == null || batchOptions.getStmpPort().length() == 0 ){ return; }
		if ( batchOptions.getStmpUser()         == null || batchOptions.getStmpUser().length() == 0 ){ return; }
		if ( batchOptions.getStmpPassword()     == null || batchOptions.getStmpPassword().length() == 0 ){ return; }
		if ( batchOptions.getEmailFromAddress() == null || batchOptions.getEmailFromAddress().length() == 0 ){ return; }
		
		String[]  recipientArray = new String[2];	 

		recipientArray[0] = batchOptions.getSuccessEmail(); 
		recipientArray[1] = batchOptions.getSuccessEmail_2(); 

		String smptServerHost = batchOptions.getStmpHost().trim();
		String smptServerPort = batchOptions.getStmpPort().trim();
		String smtpUser       = batchOptions.getStmpUser().trim();
		String smtpPassword   = batchOptions.getStmpPassword().trim();
		boolean sslEmail      = batchOptions.isStmpUseSSL();
		String fromAddress    = batchOptions.getEmailFromAddress().trim();

		postMailWithAuthenication( recipientArray, subject, message, fromAddress, smptServerHost, smtpUser, smtpPassword, smptServerPort, sslEmail );	  
	}


	/**
	 * Sends email to the email success addresses contained in batchOptions. The ingest report and the pid report are
	 * contained within a single email. If the eamil addresses is not set no report is sent.
	 * 
	 * @param batchOptions
	 * @param String ingestReportPath
	 * @param String pidReportPath
	 * @throws Exception
	 */
	public  static  void sendReportEmail( BatchIngestOptions batchOptions, String ingestReportPath, String pidReportPath ) throws Exception
	{
		String[]  recipientArray = new String[2];	 

		recipientArray[0] = batchOptions.getSuccessEmail(); 
		recipientArray[1] = batchOptions.getSuccessEmail_2(); 

		if ( batchOptions.getStmpHost()         == null || batchOptions.getStmpHost().length() == 0 ){ return; }
		if ( batchOptions.getStmpPort()         == null || batchOptions.getStmpPort().length() == 0 ){ return; }
		if ( batchOptions.getStmpUser()         == null || batchOptions.getStmpUser().length() == 0 ){ return; }
		if ( batchOptions.getStmpPassword()     == null || batchOptions.getStmpPassword().length() == 0 ){ return; }
		if ( batchOptions.getEmailFromAddress() == null || batchOptions.getEmailFromAddress().length() == 0 ){ return; }
		
		String smptServerHost = batchOptions.getStmpHost().trim();
		String smptServerPort = batchOptions.getStmpPort().trim();
		String smtpUser       = batchOptions.getStmpUser().trim();
		String smtpPassword   = batchOptions.getStmpPassword().trim();
		boolean sslEmail      = batchOptions.isStmpUseSSL();
		String fromAddress    = batchOptions.getEmailFromAddress().trim();
		
		/**
		 * createNewFile() creates an empty file ONLY IF IT DOESN"T ALREADY EXIST, this only happens during an error condition.
		 */
		File ingestReportFile = new File( ingestReportPath );
		ingestReportFile.createNewFile(); // see above
		File pidReportFile    = new File( pidReportPath );
		pidReportFile.createNewFile(); // see above	

		StringBuffer emailMessage = null;
		String line = "";

		BufferedReader ingestReportReader = new BufferedReader(new FileReader( ingestReportFile ));
		BufferedReader pidReportReader    = new BufferedReader(new FileReader( pidReportFile ));
		emailMessage = new StringBuffer();
		while ( (line = ingestReportReader.readLine()) != null ){
			emailMessage.append( line+"\n" );
		}
		emailMessage.append( "\n\n");
		line = pidReportReader.readLine();
		while ( line != null ){
			emailMessage.append( line.replaceAll( ",", ", " )+"\n" );
			line = pidReportReader.readLine();
		}

		postMailWithAuthenication( recipientArray, batchOptions.getBatchSetName() + " Ingest Report", emailMessage.toString(), fromAddress, smptServerHost, smtpUser, smtpPassword, smptServerPort, sslEmail );	
		
		logger.info( batchOptions.getBatchSetName() + " Remote Ingest report sent");
	}


}


