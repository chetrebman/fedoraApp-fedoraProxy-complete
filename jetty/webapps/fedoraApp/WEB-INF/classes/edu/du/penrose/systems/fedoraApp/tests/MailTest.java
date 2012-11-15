/*
 * Copyright 2011 University of Denver
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

package edu.du.penrose.systems.fedoraApp.tests;

import javax.mail.MessagingException;

import junit.framework.TestCase;
import edu.du.penrose.systems.util.SendMail;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.mail.SimpleEmail;

import java.security.Security;

public class MailTest extends TestCase {

	private static final String emailMsgTxt      = "This is your java email test.";
	private static final String emailSubjectTxt  = "Java Mail Test Subject 3333";
	private static final String emailFromAddress = "";
	// Add List of Email address to who email needs to be sent to
	private static final String[] emailList = { "", "" };
	private static final String emailCommaList = "";

	private static final String user      = "";
	private static final String password  = "";
	private static final String mailHost  = "";
	private static final String smtpPort = "";

	public void testMail()
	{
		SendMail smtpMailSender = new SendMail();

		try {

		    //	System.setProperty( "javax.net.debug", "ssl" );
			
			System.out.println( System.getProperty( "javax.net.ssl.keyStore" ) );
			System.out.println( System.getProperty( "javax.net.ssl.keyStorePassword" ) );
			System.out.println( System.getProperty( "javax.net.ssl.keyStoreType" ) );
			System.out.println( System.getProperty( "javax.net.ssl.trustStore" ) );
			System.out.println( System.getProperty( "javax.net.ssl.trustStorePassword" ) );
			System.out.println( System.getProperty( "javax.net.ssl.trustStoreType" ) );
			
			SendMail.postMailWithAuthenication( emailList, emailSubjectTxt, emailMsgTxt, emailFromAddress, mailHost, user, password, smtpPort, true );
			
			System.out.println( System.getProperty( "javax.net.ssl.keyStore" ) );
			System.out.println( System.getProperty( "javax.net.ssl.keyStorePassword" ) );
			System.out.println( System.getProperty( "javax.net.ssl.keyStoreType" ) );
			System.out.println( System.getProperty( "javax.net.ssl.trustStore" ) );
			System.out.println( System.getProperty( "javax.net.ssl.trustStorePassword" ) );
			System.out.println( System.getProperty( "javax.net.ssl.trustStoreType" ) );
			
		
	//		System.out.println("Sucessfully Sent mail to All Users");
			

		} 
		catch ( Exception e) 
		{
			e.printStackTrace();
		}


	}



	
}
