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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;

import org.fcrepo.server.types.gen.Datastream;
import org.jdom.Element;
import org.jdom.filter.Filter;

import edu.du.penrose.systems.fedora.client.Administrator;
import edu.du.penrose.systems.fedora.client.Downloader;
import junit.framework.TestCase;

public class getDatastreamTest extends TestCase 
{

	static final String QUOTE="\"";
	static final String APOST="\'";
	
    public getDatastreamTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();       
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testRun() {
           
    	
    	String collectionPID = "codu:ectd";
    	String dsID = "COLLECTION_POLICY";
    	
    	ArrayList<String> contentModelPIDs = new ArrayList<String>();
    	
    	try
    	{
    		Administrator administrator = new Administrator( "http", 8080, "localhost", "fedoraAdmin", "fedoraAdmin");

    		Datastream ds = administrator.getAPIM().getDatastream( collectionPID, dsID , null );
			
			Downloader dl = new Downloader( administrator, administrator.getHost(), administrator.getPort(), administrator.getUserName(), administrator.getUserPassword() );
			
			InputStream is = dl.getDatastreamContent( collectionPID, dsID, null );
			BufferedReader reader = new BufferedReader( new InputStreamReader(is, "UTF-8"));
			
			ArrayList<String> contentModels = new ArrayList<String>();
			
			String oneLine = reader.readLine();
			while ( oneLine != null ) 
			{
				if ( oneLine.contains( "content_model") )
				{
					int startIndex = oneLine.indexOf( "pid" );
					
					int  endIndex                     = oneLine.indexOf( QUOTE, startIndex+5 );
					if ( endIndex == -1 ){ endIndex   = oneLine.indexOf( APOST, startIndex+5 ); }
					
					if ( startIndex > -1 &&  endIndex > -1 )
					{
						String pid = oneLine.substring( startIndex+5, endIndex );
						contentModels.add( pid );
					}
				}
				 oneLine = reader.readLine();
			}
				
			
        } catch ( Exception e) {
            System.out.println( "Exception: "+e.getMessage());
        }
    }
    
	
}
