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

package edu.du.penrose.systems.fedoraApp.batchIngest.data;

import java.io.*;
import java.util.*;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.input.*;

import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.exceptions.FatalException;
import edu.du.penrose.systems.util.XmlUtil;
import edu.du.penrose.systems.util.FileUtil;

/**
 * Implementaion of BatchIngestXMLhandler
 * 
 */
public class BatchIngestXMLhandlerImpl extends BatchIngestXMLhandler { 

	public static final String QUOTE = "\"";
	public static final String APOST = "\'";
 
    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());
    
    private static File metsDirectory       = null; 
    private Iterator <File> xmlFileIterator = null; 
    private File currentXmlFile             = null; 
    private int currentCompleted = 0;
    private int currentFailed    = 0;
    private BatchIngestURLhandler urlHandler;
    private String uniqueBatchRunName = null;
    
    private FilenameFilter myFileFilter = null;
    
    private BatchIngestXMLhandlerImpl(){
        //nop
    }
    
    /**
     * Creates a new BatchIngestXMLhandlerImpl and checks that all URLs in the application property file are correct.
     * @param uniqueBatchRunName The name used for the application reoprt .txt 
     * file and the pid report .csv file.
     * @throws FatalException if  application property file contains invalid URLs
     */
    BatchIngestXMLhandlerImpl(  String uniqueBatchRunName, BatchIngestURLhandler urlHandler, FilenameFilter fileFilter ) throws FatalException {
        
        this.urlHandler = urlHandler;
           
        this.uniqueBatchRunName = uniqueBatchRunName;
        
        this.myFileFilter = fileFilter;

        // TBD major coupling here myFileFilter must be set before calling setFileXMLiterator, which is depreciated as well.
        this.setFileXMLiterator( this.urlHandler.getMetsFolderURL() );
        
    } // BatchIngestXMLhandlerImpl
    

    /**
     * Set the class variable xmlFileIterator based on files contained in a local directory.
     * 
     * @param fileURL
     * @throws FatalException 
     */
    private void setFileXMLiterator( URL fileURL ) {

        String metsDirectoryName = fileURL.getFile().replace( '/', File.separatorChar );
        
        metsDirectory = new FileComparator( metsDirectoryName );
        
        
        xmlFileIterator = Arrays.asList( metsDirectory.listFiles( this.myFileFilter ) ).iterator(); 
        
    } // getFileList

    class FileComparator extends File {

        FileComparator( String fileName ) {
            super( fileName );
        }
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        
        public int compareTo( File theFile ) {
            
            return this.getName().compareTo( theFile.getName() );
        }
        
    } // FileComparator
    
    
    /**
     * @throws Exception 
     * @see BatchIngestXMLhandler#getNextXMLdocument( boolean, boolean )
     */
    public Document getNextXMLdocument( boolean validate, boolean schemaCheck ) throws Exception {
        
        currentXmlFile = xmlFileIterator.next();
        
        try 
        {
        	return this.getCurrentXMLdocument( validate, schemaCheck );
        }
        catch ( Exception e )
        {
        	throw new Exception( e.getMessage() );
        }
        
    }

    
    /**
     * @throws JDOMException 
     * @throws Exception 
     * @see BatchIngestXMLhandler#getCurrentXMLdocument(boolean, boolean)
     */
    public Document getCurrentXMLdocument( boolean validate, boolean schemaCheck ) throws JDOMException {
        
        Document xmlDoc    = null; // validate=false schemaCheck=false
     
        try {
			xmlDoc = this.buildDocumentCheckValid( this.currentXmlFile, validate, schemaCheck, new URL( FedoraAppConstants.METS_SCHEMA_URL ) );
		} catch (MalformedURLException e) {

			new JDOMException( e.getMessage() ); // really shouldn't happen
		}
        
        return xmlDoc;
        
    } // getNextMetsRecord()
    

    public boolean hasNext() {
        
        return this.xmlFileIterator.hasNext();
    }
    
    /**
     * @throws IOException 
     * @throws FileNotFoundException 
     * @see BatchIngestXMLhandler#saveFOXMLtoWorkFolder(Document)
     */
    public File saveFOXMLtoWorkFolder( Document foxmlDocument ) throws FatalException {
        
        String foxDirName = null;
        File  foxXMLdirectory = null;
        File  newFOXMLfile = null;

        Format xmlFormat = Format.getPrettyFormat();
        XMLOutputter outputter = new XMLOutputter( xmlFormat );
        
        try {
            foxDirName = this.urlHandler.getWorkFolderURL().getFile().replace( '/', File.separatorChar );
            foxXMLdirectory = new File( foxDirName );
        
            newFOXMLfile = new File( foxXMLdirectory+File.separator + FedoraAppConstants.BATCH_INGEST_FOXML_FILE_NAME  );
            newFOXMLfile.delete(); // just to make sure we don't ingest old one later if new one fails.
            outputter.output( foxmlDocument, new FileOutputStream( newFOXMLfile ) );
        }
        catch ( Exception e ) {
            throw new FatalException( "Fatal Error: "+e.getMessage() );
        }
        
        return newFOXMLfile;
        
    } // outputFOXMLdocument
    
    
    /**
     * @see BatchIngestXMLhandler#markCurrentDocumentAsFailed()
     */
    public void markCurrentDocumentAsFailed() throws FatalException 
    {
        this.currentFailed++;
		BatchIngestURLhandler.transferFileToUrlWithUniqueName( this.currentXmlFile, this.urlHandler.getFailedFilesFolderURL() );   
    }


    /**
     * @see BatchIngestXMLhandler#markCurrentDocumentAsFailed()
     */
    public void markCurrentDocumentAsFailedMovePcos() throws FatalException 
    {
		FileInputStream batchFileInputStream;
		try 
		{
			// attempt to move PCOs listed in the xml file to the failed folder. We don't throw an exception since they may not even exist.
			
			batchFileInputStream = new FileInputStream( this.currentXmlFile );
			DataInputStream  batchFileDataInputStream = new DataInputStream( batchFileInputStream );
			BufferedReader   batchFileBufferedReader  = new BufferedReader( new InputStreamReader(batchFileDataInputStream) );
			String oneLine = null;
			
			while ( batchFileBufferedReader.ready()  ) {
				oneLine = batchFileBufferedReader.readLine();
				if ( oneLine.contains( "LOCTYPE" ) && oneLine.contains( "mets:FLocat" ) )
				{
					int fileIdPos   = oneLine.indexOf( "LOCTYPE" );
					
					int  firstQuote                      = oneLine.indexOf( QUOTE, fileIdPos );
					if ( firstQuote == -1 ){ firstQuote  = oneLine.indexOf( APOST, fileIdPos ); }
					
					int  lastQuote                      = oneLine.indexOf( QUOTE, firstQuote+1 );
					if ( lastQuote == -1 ){ lastQuote   = oneLine.indexOf( APOST, firstQuote+1 ); }
					
					String fileName = oneLine.substring( firstQuote+1, lastQuote ).trim();
					
					fileName.replaceFirst( "file:", "" );
					
					String newLocatonFileDirectory = this.urlHandler.getFilesFolderURL().getFile().replace( '/', File.separatorChar );
				        
					File pcoFile  = new File( newLocatonFileDirectory + File.separator+fileName );		
					 
					BatchIngestURLhandler.transferFileToUrlWithUniqueName( pcoFile, this.urlHandler.getFailedFilesFolderURL() );  
				}
			}
		} 
		catch (Exception e ) 
		{
			logger.equals( "Error while trying to move PCOs after batch failure:"+ e.getMessage() );
		}
		
		markCurrentDocumentAsFailed();
    }

    /**
     * @see BatchIngestXMLhandler#markCurrentDocumentAsCompleted()
     */
    public void markCurrentDocumentAsCompleted() throws FatalException {

		BatchIngestURLhandler.transferFileToUrlWithUniqueName( this.currentXmlFile, this.urlHandler.getCompletedFilesFolderURL() );   
        this.currentCompleted++;
    }

    /**
     * @see BatchIngestXMLhandler#getCurrentDocumentCanonicalPath()
     */
    public String getCurrentDocumentCanonicalPath() {

        String result = null;
        
        try {
            result = this.currentXmlFile.getCanonicalPath();     
        }
        catch ( Exception e ) { 
            // nop
        }
        
        return result;
    }
    
    /**
     * Currently only works for File: protocol URLs 
     * 
     * @param properityURL
     * @throws FatalException
     */
    void checkURLlocationExists( URL properityURL ) throws FatalException {
        
        String dirName = properityURL.getFile().replace( '/', File.separatorChar );
        File   directory = new File( dirName );
        
        if ( ! directory.exists() ) {
            
            throw new FatalException( "Fatal Error: check applications properties file, directory does not exist: "+ dirName);
        }
        
    } // checkURLlocationExists

    
    /**
     * @see BatchIngestXMLhandler#getCurrentCompleted()
     */
    public int getCurrentCompleted() {
        
        return this.currentCompleted;
    }

    
    /**
     * @see BatchIngestXMLhandler#getCurrentFailed()
     */
    public int getCurrentFailed() {

        return this.currentFailed;
    }
    
    /**
     * @see BatchIngestXMLhandler#getCurrentDocumentName()
     */
    public String getCurrentDocumentName() {
        
        return this.currentXmlFile.getName();
    }
 
    /**
     * @see BatchIngestXMLhandler#getUniqueBatchRunName
     */
    public String getUniqueBatchRunName() {
        return uniqueBatchRunName;
    }

    /**
     * @see BatchIngestXMLhandler#getUrlHandler()
     */
    public BatchIngestURLhandler getUrlHandler() {
        return urlHandler;
    }

    
   /**
    * @see BatchIngestXMLhandler#buildDocumentCheckValid(File, boolean, boolean, URL)
    */
    public Document buildDocumentCheckValid( File xmlFile, boolean validate, boolean schemaCheck, URL schemaLocation ) throws JDOMException {
        
        Document xmlDocument = null;  // validate=false;  schemaCheck=false  for debug
        
        /* Creating a JDOM directly was unreliable the 'type' attribute in the mods sections kept getting set to a value of  
         * 'simple' ie...<br>
         * <mods:name type="corporate"> became <mods:name type="simple">
         * <mods:name type="personal">  became <mods:name type="simple">
         * 
         * SAXBuilder builder = new SAXBuilder("org.apache.xerces.parsers.SAXParser", validate ); // TBD true to check validity
         * builder.setFeature("http://apache.org/xml/features/validation/schema", schemaCheck ); // TBD  true to check validity
         * xmlDoc = builder.build( new FileInputStream( xmlFile ) );  
         * 
         * We now create a org.w3c.dom.Document and then convert it to a org.jdom.Document. This has made it necessary to
         * add an extra step to check against the schema 3-29-2010
        */
        
        try {
            if ( xmlFileIterator != null  ) 
            {
            	if ( schemaCheck )
            	{
            		XmlUtil.schemaCheck( xmlFile, schemaLocation );
            	}

        		org.w3c.dom.Document testDocument = buildW3cDocument( xmlFile );
        		xmlDocument = convertW3cDocumentToJDOM( testDocument );
            }
        }
        catch ( Exception e ) {
            String errorMsg = "Error: Unable to create DOM " + e.getMessage();
            this.logger.error( errorMsg );
            // throw new FatalException( errorMsg );
            throw new JDOMException( errorMsg );
        }      
        
        return xmlDocument; 
        
    } // buildDocumentCheckValid()
    
    
} // BatchIngestXMLhandlerImpl
