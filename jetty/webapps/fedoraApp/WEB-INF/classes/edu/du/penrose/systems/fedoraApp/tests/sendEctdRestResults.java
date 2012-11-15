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

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.params.HttpParams;

import junit.framework.TestCase;


import edu.du.penrose.systems.fedora.client.Administrator;

public class sendEctdRestResults extends TestCase {
 
    
    public sendEctdRestResults(String name) {
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
   		 	HttpClient client = new HttpClient();
		    client.getParams().setParameter("http.useragent", "fedoraProxy Client");


	    	final String HOST = "130.253.33.105";
	    	final String PORT = "8085";
	    	final String CMD  = "/library/updatePid";
	    	final String KEY  = "6b476c7e50726949347162353c7e3d7271336e6f3e3e4c2d703d762d24";
	    	
	    	String objid = "du_174_1793_primary_1999_kelly";
	    	String   pid = "178";
	    	
	    	StringBuffer ectdResultsUrl = new StringBuffer( "http://" );
	    	ectdResultsUrl.append( HOST );
	    	ectdResultsUrl.append( ":" );
	    	ectdResultsUrl.append( PORT );
	    	ectdResultsUrl.append( CMD );
	    	
		    GetMethod method = new GetMethod( ectdResultsUrl.toString() );
		    HttpMethodParams params = new HttpClientParams();
		    params.setParameter( "id", objid );
		    params.setParameter( "pid", pid );
  	
		    method.setParams(params);

		    try{
		      int returnCode = client.executeMethod(method);

		      if(returnCode != HttpStatus.SC_OK ) {
		        System.err.println("ERROR");
		      } 
		    } catch (Exception e) {
		      System.err.println(e);
		    } finally {
		      method.releaseConnection();
		    }
    		
        } catch ( Exception e) {
            System.out.println( "Exception: "+e.getMessage());
        }
               
    } // testRun



} // ProgramPropertiesTest
