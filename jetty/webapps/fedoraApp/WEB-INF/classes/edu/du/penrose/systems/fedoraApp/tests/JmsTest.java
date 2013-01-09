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

public class JmsTest extends TestCase {

    public JmsTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();       
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testRun() {

    	// http://pookey.co.uk/wordpress/archives/74-playing-with-activemq-using-maven
    	// http://java.sun.com/developer/technicalArticles/Ecommerce/jms/index.html

        String brokerURL = "tcp://localhost:61616";
        
        ConnectionFactory factory;
        Connection connection;
        Session session;
        MessageProducer producer;

    	final String fedoraAppEmailQueue = FedoraAppConstants.JMS_ECTD_RESULTS_Q;
    	
    	String string_1 = "/home/chet/batch_space/codu/ectd/logs/codu.ectd.april-08-2011_17:15:36-297.txt"; // ingest report
    	String string_2 = "/home/chet/batch_space/codu/ectd/logs/codu.ectd.april-08-2011_17:15:36-297.csv"; // pid report
    	
    	try
    	{ 
    		factory = new ActiveMQConnectionFactory(brokerURL);

            connection = factory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE );
            
            System.out.println( "\n\n** session transactions is set to :"+session.getTransacted() );
            
            	// send message
            
            Destination destination = session.createQueue( fedoraAppEmailQueue );
            producer = session.createProducer(destination);

            System.out.println("Creating Message " );
            Message message = session.createTextMessage( string_1 + "\n" + string_2 + "\n" );
            producer.send(message);
            connection.close();
            

            	// consume message 
            connection = factory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE );
            destination              = session.createQueue( fedoraAppEmailQueue );
            MessageConsumer consumer = session.createConsumer( destination );
            consumer.setMessageListener( null );
            consumer.setMessageListener( new ActiveMQListener() );

            for ( int i = 1; i < 10000000; i++  ){
            	System.out.println( "looping" );
            }
    	} catch ( Exception e) {
    		System.out.println( "Exception: "+e.getMessage());
    	}


    } // testRun
    
    public class ActiveMQListener implements MessageListener
    {
        public void onMessage(Message message)
        {
            try
            {
                if (message instanceof TextMessage)
                {
                    TextMessage txtMessage = (TextMessage)message;
                    
                    String[] results = txtMessage.getText().split( "\n");

                    System.out.println("\nMessage received: string 1 = " + results[0] );
                    System.out.println("Message received: string 2 = " + results[1] );
                    
                    txtMessage.acknowledge();
                }
                else
                {
                    System.out.println("Invalid message received.");
                }
            }
            catch (JMSException e)
            {
                System.out.println("Caught:" + e);
                e.printStackTrace();
            }
        }
    }
    


    /**
     * Create a fedora listener  NOT TESTED but it compiles!
     * 
     * @author chet
     *
     */
    public class Example implements MessagingListener {
        MessagingClient messagingClient;
        public void start() throws MessagingException {
            Properties properties = new Properties();
            properties.setProperty(Context.INITIAL_CONTEXT_FACTORY,
                                   "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
            properties.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
            properties.setProperty(JMSManager.CONNECTION_FACTORY_NAME, "ConnectionFactory");
            properties.setProperty("topic.fedora", "fedora.apim.*");
            messagingClient = new JmsMessagingClient("example1", this, properties, false);
            messagingClient.start();
            
            
        }
        public void stop() throws MessagingException {
            messagingClient.stop(false);
        }
        public void onMessage(String clientId, Message message) {
            String messageText = "";
            try {
                messageText = ((TextMessage)message).getText();
            } catch(JMSException e) {
                System.err.println("Error retrieving message text " + e.getMessage());
            }
            System.out.println("Message received: " + messageText + " from client " + clientId);
        }
    }

} // ProgramPropertiesTest
