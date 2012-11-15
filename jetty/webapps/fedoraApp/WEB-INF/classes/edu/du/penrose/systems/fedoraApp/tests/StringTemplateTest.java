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
import java.security.MessageDigest;
import java.util.Map;

import junit.framework.TestCase;

import edu.du.penrose.systems.util.FileUtil;
import edu.du.penrose.systems.fedoraApp.batchIngest.bus.FedoraAppBatchIngestController;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestURLhandler;

import org.fcrepo.server.types.gen.Datastream;
import edu.du.penrose.systems.fedora.client.objecteditor.Util;
import edu.du.penrose.systems.fedora.client.Administrator;

import org.antlr.stringtemplate.*;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;

public class StringTemplateTest extends TestCase {
 
    
    public StringTemplateTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();       
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testRun() {
        
    	try{
    		
String testString = "this is the $PID$  there";
    		
    	StringTemplateGroup group = new StringTemplateGroup("xmlGroup", "/home/chet/javadev/fedoraApp/WebContent/WEB-INF/templates" ,  DefaultTemplateLexer.class );

        StringTemplate ds = group.getInstanceOf("foxml_rels-ext._DS");

        final String PID_NAMESPACE   = "codu";
        final String PID             = "codu:TEST_PID";
        final String COLLECTION_NAME = "ectd";
        final String CONTENT_MODEL   = "ectdCModel";
        
    	ds.setAttribute( "PID_NAMESPACE", PID_NAMESPACE );
    	ds.setAttribute( "PID", PID );
    	ds.setAttribute( "COLLECTION_NAME", COLLECTION_NAME );
    	ds.setAttribute( "CONTENT_MODEL", CONTENT_MODEL );
    
   
        System.out.println( ds.toString() );
            
        } catch ( Exception e) {
            System.out.println( "Exception: "+e.getMessage());
        }
           
           
       
    } // testRun

    
} // ProgramPropertiesTest
