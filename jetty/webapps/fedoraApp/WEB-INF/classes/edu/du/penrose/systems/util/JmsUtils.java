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

package edu.du.penrose.systems.util;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraApp.ProgramProperties;

public class JmsUtils {


	/**
	 * Reads host and server infor from the program properties file.
	 * 
	 * @see FedoraAppConstants#JMS_SERVER_PROPERTY
	 * @see  FedoraAppConstants#JMS_PORT_PROPERTY 
	 * @param qName
	 * @param messageString
	 * @throws JMSException
	 */
	static public void sendJmsMessage( String qName, String messageString ) throws JMSException 
	{
			ProgramProperties programProperties = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() );

			String jmsServer = programProperties.getProperty( FedoraAppConstants.JMS_SERVER_PROPERTY );
			String jmsPort   = programProperties.getProperty( FedoraAppConstants.JMS_PORT_PROPERTY );
	        String brokerURL = "tcp://"+jmsServer+":"+jmsPort;
	        
	        ConnectionFactory factory = new ActiveMQConnectionFactory( brokerURL );
	
	        Connection connection = factory.createConnection();
	        connection.start();
	        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE );
	        
	        System.out.println( "\n\n** session transactions is set to :"+session.getTransacted() );
	        
	        	// send message
	        
	        Destination destination = session.createQueue( qName );
	        MessageProducer producer = session.createProducer(destination);
	
	        Message message = session.createTextMessage( messageString );
	        producer.send(message);
	        connection.close();

	}
}
