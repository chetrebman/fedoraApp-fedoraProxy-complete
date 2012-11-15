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
import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BagHandler;

import edu.du.penrose.systems.util.MyServletContextListener;
import edu.du.penrose.systems.util.MyServletContextListener_INF;
import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.domain.JSonBagger;
import gov.loc.repository.bagger.profile.BaggerProfileStore;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.writer.Writer;
import gov.loc.repository.bagit.writer.impl.FileSystemWriter;
import gov.loc.repository.bagit.writer.impl.TarBz2Writer;
import gov.loc.repository.bagit.writer.impl.TarGzWriter;
import gov.loc.repository.bagit.writer.impl.TarWriter;
import gov.loc.repository.bagit.writer.impl.ZipWriter;

public class BagitCreationTest extends TestCase {
	

	public BagitCreationTest(String name) 
    {
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
             
    	String bagDir = "/home/chet/FedoraApp_FedoraProxy_distribution_July_2012/batch_space/codu/systemsTest/work/bagit_8-8-2012";

        DefaultBag bag = null;	
    	try
    	{
    		/*
    		 * You must initialize the profileStore or DefaultBag will throw an exception.
    		 * 
    		 * FYI BaggerProfileStore initialization is defined in...
    		 * bagger-business/src/main/resources/gov/loc/repository/bagger/ctx/common/business-layer-context.xmlbagger-business/src/main/resources/gov/loc/repository/bagger/ctx/common/business-layer-context.xml
    		 * 
    		 */
    		JSonBagger json = new JSonBagger();	
    		BaggerProfileStore profileStore = new BaggerProfileStore( json );
    	
    		// create a new bag

    		BagFactory bagFactory = new BagFactory();
    		
    		bag = new DefaultBag();
    		
    		
            bag.createPreBag( new File( bagDir ), "0.96" );
            
            bag.isHoley( true );
            
            bag.isSerial(true);
    		bag.setSerialMode(DefaultBag.ZIP_MODE);
    		Writer bagWriter = null;
    		
    		bag.getFetch().setBaseURL( "http://lib-ram.cair.du.edu/bagit/" );
    	
			short mode = bag.getSerialMode();
			if (mode == DefaultBag.NO_MODE) {
				bagWriter = new FileSystemWriter(bagFactory);
			} else if (bag.getSerialMode() == DefaultBag.ZIP_MODE) {
				bagWriter = new ZipWriter(bagFactory);
			} else if (mode == DefaultBag.TAR_MODE) {
				bagWriter = new TarWriter(bagFactory);
			} else if (mode == DefaultBag.TAR_GZ_MODE) {
				bagWriter = new TarGzWriter(bagFactory);
			} else if (mode == DefaultBag.TAR_BZ2_MODE) {
				bagWriter = new TarBz2Writer(bagFactory);
			}
			

			bag.setRootDir( new File ("/home/chet/FedoraApp_FedoraProxy_distribution_July_2012/batch_space/codu/systemsTest/") );
        	// bag.setName( "newBagTest.zip"); has no effect the file name is taken from the root directory name
			bag.write(bagWriter);
			
			
      //      bag.getBaseUrl(fetchTxt)
            
    		// now validate it
    		boolean result = new BagHandler().validateBag( bagDir );
    		
    		if ( result )
    		{
    			System.out.println( "valid bag");
    		}
    		else {
    			System.out.println( "b bag");
    		}		
        } 
    	catch ( Exception e) {
            System.out.println( "Exception: "+e.getMessage());
        }
               
    } // testRun



} // ProgramPropertiesTest
