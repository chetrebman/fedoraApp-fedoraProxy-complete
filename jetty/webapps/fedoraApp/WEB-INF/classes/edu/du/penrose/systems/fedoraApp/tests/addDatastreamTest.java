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

import java.io.*;

import junit.framework.TestCase;


import edu.du.penrose.systems.fedora.client.Administrator;

public class addDatastreamTest extends TestCase {
 
    
    public addDatastreamTest(String name) {
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

        	File managedFile = new File( "/batch_space/codu/ectd/images/pdf.png" );
        	
        	String location = admin.getUploader().upload(managedFile);
        	
    		String pid = "codu:9";
			String dsID = "TN6";
			String[] altIDs = new String[]{};
			String dsLabel = "Thumbnail.php";
			boolean versionabe = false;
			String mimeType = "image/png";
			String formatURI = null;
			String dsLocation = location;
			String controlGroup = "M";
			String dsState = "A";
			String checksupType = null;
			String checksum = null;
			String logMessage = "Ingest default PDF thumbnail";
			
			String dsIDreturned = admin.getAPIM().addDatastream(pid, dsID, altIDs, dsLabel, versionabe, mimeType, formatURI, dsLocation, controlGroup, dsState, checksupType, checksum, logMessage);
    		
        } catch ( Exception e) {
            System.out.println( "Exception: "+e.getMessage());
        }
               
    } // testRun



} // ProgramPropertiesTest
