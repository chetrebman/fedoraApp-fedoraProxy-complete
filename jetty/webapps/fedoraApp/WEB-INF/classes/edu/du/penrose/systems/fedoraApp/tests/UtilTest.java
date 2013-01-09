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
import java.math.BigInteger;
import java.security.MessageDigest;

import junit.framework.TestCase;

import edu.du.penrose.systems.util.FileUtil;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestURLhandler;
import edu.du.penrose.systems.fedoraApp.util.FedoraAppUtil;

import org.fcrepo.server.types.gen.Datastream;
import edu.du.penrose.systems.fedora.client.objecteditor.Util;
import edu.du.penrose.systems.fedora.client.Administrator;

public class UtilTest extends TestCase {
 
    
    public UtilTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();       
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testRun() {
        
        BatchIngestURLhandler batchURLhandler = null;
        String institution = "codu";
        String batch_set   = "frid";
        String uniqueBatchRunName = FedoraAppUtil.getUniqueBatchRunName(institution, batch_set );
        batchURLhandler = null;
        
        MessageDigest digest = null;
        File inputFile = null;
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        byte[] inputBuffer = new byte[8192];
        
        Datastream aDS = null;  
        String fedoraCheckSum = null;
        String pid = "codu:761";
        String dsID  = "frid00003.tif";
        
        // fez_batch_test-1-5.xml
        // frid-mets-r1-5-revised.xml
        try {

         //   batchURLhandler = BatchIngestURLhandler.getInstance(uniqueBatchRunName, institution, batch_set );
         //   edu.du.penrose.systems.fedoraApp.batchIngest.data.SplitXml.split(  batchURLhandler, "frid", new File( "\\batch_space\\codu\\frid\\work\\frid-mets-00001-03000.xml" ) );

            Administrator administrator = new Administrator( "http", 8080, "localhost", "fedoraAdmin", "fedoraAdmin" );
            
            // verify that it exists (exception thrown)
            Util.getObjectFields( administrator.getAPIA(), pid, new String[] {"pid"} );     
                  
            aDS = administrator.getAPIM().getDatastream(pid, dsID , null);
               // aDS.setChecksumType( "AUTO" );
            fedoraCheckSum = aDS.getChecksum(); //
            
            
            inputFile = new File( "C:\\batch_space\\codu\\frid\\files\\"+dsID );
            fis   = new FileInputStream( inputFile );
           //  bis   = new BufferedInputStream( fis );
            
            digest = MessageDigest.getInstance("MD5");
            digest.reset();
            int bytesRead = 0;
            while ( (bytesRead = fis.read(inputBuffer)) > 0 ){
                digest.update( inputBuffer, 0, bytesRead );
            }
            
            byte[] fileCheckSum = digest.digest();
            
            BigInteger bigInt = new BigInteger(1, fileCheckSum);
            String output = bigInt.toString(16);

            String fileUtilChecksum = FileUtil.getMD5( inputFile );
            
            System.out.println("Fedora checksum:"+ fedoraCheckSum );
            System.out.println("File checksum:"  + output );
            System.out.println("File UTIL checksum:"  + fileUtilChecksum );
            System.out.println("Stop here" );
            
            
            
        } catch ( Exception e) {
            System.out.println( "Exception: "+e.getMessage());
        }
           
           
       
    } // testRun

    
} // ProgramPropertiesTest
