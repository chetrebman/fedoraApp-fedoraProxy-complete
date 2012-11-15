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
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import junit.framework.TestCase;

import edu.du.penrose.systems.util.MyServletContextListener;
import edu.du.penrose.systems.util.MyServletContextListener_INF;
import edu.du.penrose.systems.utils.FileUtil;
import edu.du.penrose.systems.exceptions.FatalException;
import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraApp.batchIngest.bus.FedoraAppBatchIngestController;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestOptions;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestURLhandler;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.ExtRelDefinitions;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.ExtRelList;

import org.fcrepo.server.management.FedoraAPIM;
import org.fcrepo.server.types.gen.Datastream;

import edu.du.penrose.systems.fedora.ResourceIndexUtils;
import edu.du.penrose.systems.fedora.client.objecteditor.Util;
import edu.du.penrose.systems.fedora.client.Administrator;

import org.antlr.stringtemplate.*;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;

import edu.du.penrose.systems.fedora.client.Administrator;

public class QueryRiTest extends TestCase {


	public QueryRiTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();       
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testRun() {


		/*    	select $object $title from <#ri>
        where ($object <fedora-model:label> $title
        and $object <fedora-rels-ext:isMemberOfCollection>
<info:fedora/islandora:top>
        and $object <fedora-model:state>
<info:fedora/fedora-system:def/model#Active>)
        order by $title*/

		/*    	select $object from <#ri> where ( $object <fedora-rels-ext:isMemberOfCollection>
    	<info:fedora/islandora:top> )*/

		try
		{

	    	MyServletContextListener_INF myServletContextListener = new MyServletContextListener();
	    	FedoraAppConstants.setContextListener( myServletContextListener );
	    	
			// 		Administrator admin = new Administrator( "http", 8080, "localhost", "fedoraAdmin", "fedoraAdmin");

			FedoraAppConstants.getServletContextListener().setContextTestPath( "/home/chet/workspace-sts-2.3.2.RELEASE/fedoraApp/WebContent/" );
	        
		//	System.out.println( ResourceIndexUtils.getAllIslandoraChildCollections( FedoraAppBatchIngestController.getAdministrator() ));	
		} 
		catch ( Exception e ) {
			System.out.println( "Exception: "+e.getMessage() );
		}

	} // testRun


} // QueryRiTest
