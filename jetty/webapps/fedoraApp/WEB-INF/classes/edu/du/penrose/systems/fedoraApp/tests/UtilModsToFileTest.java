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
import java.net.URL;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.*;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.xml.sax.InputSource;

import edu.du.penrose.systems.fedoraApp.ProgramProperties;
import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;

import edu.du.penrose.systems.exceptions.FatalException;
import edu.du.penrose.systems.exceptions.PropertyStorageException;
import edu.du.penrose.systems.util.FileUtil;
import edu.du.penrose.systems.fedoraApp.batchIngest.bus.FedoraAppBatchIngestController;
import edu.du.penrose.systems.fedoraApp.batchIngest.bus.BatchIngestThreadManager;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestURLhandler;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.TransformMetsData;

import org.fcrepo.server.types.gen.Datastream;
import edu.du.penrose.systems.fedora.client.objecteditor.Util;

public class UtilModsToFileTest extends TestCase {


    public UtilModsToFileTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();       
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public class XMLfileFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".xml"));
        }
    } // FilenameFilter

    public void testRun() {


        String inputDirName = "C:\\batch_space\\codu\\ath\\mets";
        Iterator<File>  xmlFileIterator = null;
        try {

            File inputDir = new File( inputDirName ); 
            xmlFileIterator= Arrays.asList( inputDir.listFiles( new XMLfileFilter() ) ).iterator(); 
            int fileCount = 0;
            while ( xmlFileIterator.hasNext() ) {
                writeModsToFile( inputDirName, xmlFileIterator.next(), fileCount );
                fileCount++;
            }

        } catch ( Exception e) {
            System.out.println( "Exception: "+e.getMessage());
        }



    } // testRun

    void writeModsToFile( String outputDirName, File inputFile, int fileCount ) {

        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            File outFile = null;
            FileInputStream  fis = new FileInputStream( inputFile );
            FileOutputStream fos = null;

            DataInputStream in = new DataInputStream(fis);
            br = new BufferedReader(new InputStreamReader(in));

            String doucmentType = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
            String oneLine = null;
            while ( br.ready() ) {
                oneLine = br.readLine();
                if ( oneLine.contains(  "<mods:mods" )) {

                    outFile = new File( outputDirName+"\\mods_"+fileCount+".xml" );
                    fos = new FileOutputStream( outFile );
                    
                    bw  = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
                    bw.write( doucmentType );
                    bw.newLine();
                    while ( ! oneLine.contains(  "</mods:mods" )) { // null pointer on premature end of file.
                        bw.write( oneLine );
                        bw.newLine();
                        oneLine = br.readLine();
                    }
                    bw.write( oneLine );
                    bw.newLine();
                    bw.close();   
                    fileCount++;
                }
            } // while
        }
        catch (Exception e){
            String errorMsg = "Exception:"+e;
            System.out.println( errorMsg );
        }
        finally {
            try {
                br.close();
                bw.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }        
        } 
    }




} // ProgramPropertiesTest
