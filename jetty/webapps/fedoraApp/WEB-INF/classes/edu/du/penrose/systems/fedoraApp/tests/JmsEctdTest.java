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

package edu.du.penrose.systems.fedoraApp.tests;

import java.io.*;
import java.util.Properties;

import org.fcrepo.client.messaging.JmsMessagingClient;
import org.fcrepo.client.messaging.MessagingClient;
import org.fcrepo.client.messaging.MessagingListener;
import org.fcrepo.server.errors.MessagingException;
import org.fcrepo.server.messaging.JMSManager;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;


import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.naming.Context;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;

import junit.framework.TestCase;


import edu.du.penrose.systems.fedora.client.Administrator;
import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraApp.ProgramProperties;
import edu.du.penrose.systems.fedoraApp.tasks.IngestWorker;
import edu.du.penrose.systems.util.MyServletContextListener;
import edu.du.penrose.systems.util.MyServletContextListener_INF;

public class JmsEctdTest extends TestCase {

    public JmsEctdTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();    

    	MyServletContextListener_INF myServletContextListener = new MyServletContextListener();
    	FedoraAppConstants.setContextListener( myServletContextListener );
    	
        FedoraAppConstants.getServletContextListener().setContextTestPath( "/home/chet/workspace-sts-2.3.2.RELEASE/fedoraApp/WebContent/" );
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testRun() {

    	try
    	{ 
    		IngestWorker ectdWorker = new edu.du.penrose.systems.fedoraApp.tasks.IngestWorker( "ectd" );
    		
    		this.sendJmsMessage( FedoraAppConstants.JMS_ECTD_RESULTS_Q, "howdy" );
    		
    	} catch ( Exception e) {
    		System.out.println( "Exception: "+e.getMessage());
    	}

        for ( int i = 1; i < 10000000; i++  ){
        	System.out.println( "looping" );
        }
    	
    } // testRun
    
    
    private void sendJmsMessage( String qName, String messageString ) throws JMSException 
	{
			ProgramProperties programProperties = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() );

			String jmsServer = programProperties.getProperty( FedoraAppConstants.JMS_SERVER_PROPERTY );
			String jmsPort   = programProperties.getProperty( FedoraAppConstants.JMS_PORT_PROPERTY );
	        String brokerURL = "tcp://"+jmsServer+":"+jmsPort;
	        
	        ConnectionFactory factory = new ActiveMQConnectionFactory( brokerURL );
	
	        Connection connection = factory.createConnection();
	        connection.start();
	        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE );
	        
	   //     System.out.println( "\n\n** session transactions is set to :"+session.getTransacted() );
	        
	        	// send message
	        
	        Destination destination = session.createQueue( qName );
	        MessageProducer producer = session.createProducer(destination);
	
	        Message message = session.createTextMessage( messageString );
	        producer.send(message);
	        connection.close();

	}
    
    
} // 
