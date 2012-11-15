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
import java.net.URL;

import junit.framework.TestCase;

import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.util.MyServletContextListener;
import edu.du.penrose.systems.util.MyServletContextListener_INF;
import edu.du.penrose.systems.util.XmlUtil;

public class XmlValidateTest extends TestCase {
 
    public XmlValidateTest(String name) {
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

    public void testRun() 
    {         
    	try
    	{
    		File testFile = new File( "/home/chet/javadev/OAI_WIP/Test_MODS_55744.xml"  );
    		
    		XmlUtil.schemaCheck( testFile, new URL( "http://www.loc.gov/standards/mods/v3/mods-3-4.xsd") );
        } 
    	catch ( Exception e) {
            System.out.println( "Exception: "+e.getMessage());
        }
               
    } // testRun



} // ProgramPropertiesTest
