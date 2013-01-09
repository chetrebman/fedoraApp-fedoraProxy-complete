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

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import edu.du.penrose.systems.fedoraApp.ProgramProperties;
import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;

import edu.du.penrose.systems.fedoraApp.batchIngest.bus.FedoraAppBatchIngestController;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.TransformMetsData;
import edu.du.penrose.systems.util.MyServletContextListener;
import edu.du.penrose.systems.util.MyServletContextListener_INF;
import edu.du.penrose.systems.exceptions.PropertyStorageException;

public class IngestControllerTest extends TestCase {


    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());
    
    private ProgramProperties programProperties = null;
    
    private ApplicationContext getApplicationContext() { // page 706
        
         String[] paths = new String[] { "./WebContent/WEB-INF/fedoraApp-servlet.xml" };
         
         return new FileSystemXmlApplicationContext( paths );
    }
    
    public IngestControllerTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();

    	MyServletContextListener_INF myServletContextListener = new MyServletContextListener();
    	FedoraAppConstants.setContextListener( myServletContextListener );
    	
        FedoraAppConstants.getServletContextListener().setContextTestPath( "\\javadev\\fedoraApp\\WebContent\\" );
        
        ApplicationContext ctx = this.getApplicationContext(); //page 706
        this.programProperties = (ProgramProperties) ctx.getBean( "programProperties" );
        
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testRun() {
        
     //   FedoraAppBatchIngestController ingestController = new FedoraAppBatchIngestController();
         
        try {
          //  ingestController.setXSLTransformer( new TransformMetsData() );
        }
        catch ( Exception e ){
            // nop
        }
        
        try {
    //        ingestController.runBatch();
        } catch ( Exception e) {
            
        }
    }

} // ProgramPropertiesTest
