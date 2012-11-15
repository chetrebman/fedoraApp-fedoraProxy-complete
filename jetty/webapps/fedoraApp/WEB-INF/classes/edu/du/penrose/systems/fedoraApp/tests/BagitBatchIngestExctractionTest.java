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

import junit.framework.TestCase;
import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BagHandler;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestOptions;

import edu.du.penrose.systems.util.MyServletContextListener;
import edu.du.penrose.systems.util.MyServletContextListener_INF;

public class BagitBatchIngestExctractionTest extends TestCase {
	
    public BagitBatchIngestExctractionTest(String name) {
        super(name);
        
    	
    }

    protected void setUp() throws Exception 
    {
    	MyServletContextListener_INF myServletContextListener = new MyServletContextListener();
    	FedoraAppConstants.setContextListener( myServletContextListener );
    	
	   //     MyServletContextListener.setContextTestPath( "C:\\home\\chet\\javadev\\fedoraApp\\WebContent\\" );
    	FedoraAppConstants.getServletContextListener().setContextTestPath( "/home/chet/javadev/fedoraApp/WebContent/" );
	        
	 //       MyServletContextListener.setContextTestPath( "/home/chet/workspace-sts-2.3.2.RELEASE/fedoraApp/WebContent/" );
	        
	   //     ApplicationContext ctx = this.getApplicationContext(); //page 706
	   //     this.programProperties = (ProgramProperties) ctx.getBean( "programProperties" ); 
    	

    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testRun() {
             

  //  	String bagFileName = "bagit_3-12-2012_bag.gz";
    	String bagFileName = "bagit_8-7-2012.zip";
    	
   // 	String bagFileName = "3-12-2012_bag";
    	
    	
    	try
    	{ 
    		BatchIngestOptions batchOptions = BatchIngestOptions.getGenericBatchOptions();
    		
    		batchOptions.setInstitution( "codu" );
    		batchOptions.setBatchSet( "systemsTest" );
    		
    		new BagHandler( batchOptions, bagFileName ).batchIngestExtractRetrieveAndMove(); 		
    		
        } 
    	catch ( Exception e) {
            System.out.println( "Exception: "+e.getMessage());
        }
               
    } // testRun



} // ProgramPropertiesTest
