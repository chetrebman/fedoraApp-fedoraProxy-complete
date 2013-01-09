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
import java.net.URL;
import java.util.Iterator;

import org.jdom.Element;
import org.jdom.filter.Filter;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import junit.framework.TestCase;

import edu.du.penrose.systems.exceptions.FatalException;
import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestXMLhandlerImpl;

import edu.du.penrose.systems.fedora.client.Administrator;
import edu.du.penrose.systems.fedora.client.objecteditor.Util;

public class modifyDatastreamTest extends TestCase {
 
    
    public modifyDatastreamTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();       
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testRun() {
                         
    	try
    	{		
	    	Administrator admin = new Administrator( "http", 8080, "localhost", "fedoraAdmin", "fedoraAdmin");
	    	
			String pid = "codu:115";
			
	   // 	xmlFile = new File( "/home/chet/javadev/fedoraApp/update_ds_test.xml" ); 
	    	File xmlFile = new File( "/home/chet/javadev/fedoraProxy/ectdBatchIngest_gary_update.xml" );   
	    	
			org.w3c.dom.Document tempDocument = BatchIngestXMLhandlerImpl.buildW3cDocument( xmlFile );	
			org.jdom.Document    xmlDocument  = BatchIngestXMLhandlerImpl.convertW3cDocumentToJDOM( tempDocument );
	    					
    		updateDatastreams( admin, pid, xmlDocument );	
        } 
    	catch ( Exception e) {
            System.out.println( "Exception: "+e.getMessage());
        }
               
    } // testRun


    /**
     * NOTE: We assume a DC datastream always exists, we use this to test for existing object.
     * 
     * @param admin
     * @param pid
     * @param xmlDocument
     * @throws FatalException
     */
    void updateDatastreams( Administrator admin, String pid, org.jdom.Document xmlDocument ) throws FatalException
    {
    	try {
    		
    		// verify that Fedora Digital Object with correct PID exists (exception thrown)
	    	try{
	    		Util.getObjectFields( admin.getAPIA(), pid, new String[] {"pid"} );
				System.out.println( "FOUND IT ");
	    	}
	    	catch ( Exception e )
	    	{
	    		throw new FatalException( "ERROR: attempt to update non-existant object:"+pid );
	    	}
	
	    	File xmlFile = new File( "/home/chet/javadev/fedoraApp/add_ds_test.xml" );       	
	    	
	    	String location = admin.getUploader().upload(xmlFile);
	
			String dsID = "";
			String[] altIDs = new String[]{};
			String dsLabel = "";
			boolean versionabe = true;
			String mimeType = "text/xml";
			String formatURI = null;
			String controlGroup = "X";
			String dsState = "A"; //Active, Deleted or Inactive
			String checksupType = null;
			String checksum = null;
			String logMessage = "";
			
	
			Iterator<Element> xmlDataIterator =  xmlDocument.getDescendants( new XmlDataFilter() );
			Element xmlData = null;
			byte[] dsContent = null;
			while ( xmlDataIterator.hasNext() )
			{ 
				xmlData = xmlDataIterator.next();
				dsID    = xmlData.getParentElement().getAttribute( "MDTYPE" ).getValue();
				dsLabel = dsID;
				if ( xmlData.getContent().size() != 3 ){
					throw new FatalException( "Incorrect content count when trying to parse xmlData for update" );
				}
				xmlData = (Element) xmlData.getContent(1); // 0 = /n 2 = /n
				
		        Format xmlFormat = Format.getPrettyFormat();
		        XMLOutputter outputter = new XMLOutputter( xmlFormat );
	
		    	//		byte[] dsContent = getBytesFromFile( xmlFile );
				dsContent = outputter.outputString( xmlData ).getBytes();
				
				boolean force = false;

				logMessage = "Update datastream:"+dsID;
				Object result = admin.getAPIM().modifyDatastreamByValue(pid, dsID, altIDs, dsLabel, mimeType, formatURI, dsContent,  checksupType, checksum, logMessage, force);       				
			}
    	}
		catch ( Exception e )
		{
			throw new FatalException( "ERROR: updating datastreams:"+e);
		}
    }
    
    /**
     * Filter to find the xmlData elements (contain datastreams) within the top mets element
     * 
     * @author chet.rebman
     *
     */   
    class XmlDataFilter implements Filter {

        public boolean matches( Object testObj ) {
            if ( Element.class.isAssignableFrom( testObj.getClass() ) ) {
                Element element = (Element) testObj;
                if ( element.getName().compareToIgnoreCase("xmlData") == 0) {
                        return true;
                }
            }
            return false;
        }   
    } 
    

} // ProgramPropertiesTest
