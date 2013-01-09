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

import junit.framework.TestCase;


import edu.du.penrose.systems.fedora.client.Administrator;

public class setObjectInactiveTest extends TestCase {
 
    
    public setObjectInactiveTest(String name) {
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

    		String pid = "codu:ectdCModel";
    		String state = "I"; // A, I, D
    		String label = null;
    		String ownerId = null;
    		String logMessage = "Set state inactive";
    		
    		Object result = admin.getAPIM().modifyObject(pid, state, label, ownerId, logMessage );
    		
    		System.out.println( result );	
        } 
    	catch ( Exception e) {
            System.out.println( "Exception: "+e.getMessage());
        }
               
    } // testRun



} // ProgramPropertiesTest
