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
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Map;

import junit.framework.TestCase;

import edu.du.penrose.systems.util.FileUtil;
import edu.du.penrose.systems.exceptions.FatalException;
import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraApp.batchIngest.bus.FedoraAppBatchIngestController;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestOptions;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestURLhandler;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.ExtRelDefinitions;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.ExtRelList;

import org.fcrepo.server.management.FedoraAPIM;
import org.fcrepo.server.types.gen.Datastream;
import edu.du.penrose.systems.fedora.client.objecteditor.Util;
import edu.du.penrose.systems.fedora.client.Administrator;

import org.antlr.stringtemplate.*;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;

import edu.du.penrose.systems.fedora.client.Administrator;

public class AddExtRelTest extends TestCase {
 
    
    public AddExtRelTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();       
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testRun() {

    	// Fedora ext-rel definitions, 
    	
        
        final String pid = "codu:32";
        
    	try
    	{
    		Administrator admin = new Administrator( "http", 8080, "localhost", "fedoraAdmin", "fedoraAdmin");

	    	BatchIngestOptions batchOptions = new BatchIngestOptions();
	    	
        	// Set up the fedora external object relationships.
	    	batchOptions.addIslandoraRelationship( FedoraAppConstants.ECTDcollectionPID, FedoraAppConstants.ECTDmodelPID );
	    	
			this.addExternalRelationships( admin.getAPIM(), pid, batchOptions.getExtRelList() );
    		
        } catch ( Exception e) {
            System.out.println( "Exception: "+e.getMessage());
        }
           
           
       
    } // testRun


	private void addExternalRelationships(FedoraAPIM fedoraAPIM, String pid, ExtRelList extRelList) throws FatalException 
	{
		Iterator<ExtRelDefinitions> extRelIterator = extRelList.getIterator();
		
		while( extRelIterator.hasNext() )
		{
			ExtRelDefinitions extRelDef = extRelIterator.next();
			try 
			{
				boolean result = fedoraAPIM.addRelationship(pid, extRelDef.getPredicate(), extRelDef.getObject(), false, null );
				
//				Object out = fedoraAPIM.getRelationships( "codu:10", null);
//				System.out.println( out );				
//				boolean result = true;
				
				if ( result == false )
				{ 
					// TBD will return false if relationship already exists.
					
					// throw new FatalException( "Unable to add fedora Relationship" );
					System.out.println( "Unable to add fedora Relationship: false returned" );
				}
			} catch (RemoteException e) {
				// throw new FatalException( "Unable to add fedora Relationship:" + e );
				System.out.println( "Unable to add fedora Relationship:" + e );
			}
		}
	}

} // ProgramPropertiesTest
