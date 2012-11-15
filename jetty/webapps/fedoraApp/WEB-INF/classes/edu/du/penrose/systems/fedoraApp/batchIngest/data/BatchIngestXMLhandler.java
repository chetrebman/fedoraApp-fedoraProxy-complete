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

package edu.du.penrose.systems.fedoraApp.batchIngest.data;

import java.io.*;
import java.net.URL;


import org.jdom.Document;
import org.jdom.JDOMException;

import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.exceptions.FatalException;
import edu.du.penrose.systems.fedoraApp.util.FedoraAppUtil;

/**
 * Used to sequentially access XML documents contained within a folder for 
 * processing by the batch ingest program. The XML document folder is specified 
 * by a URL, in the application's properties file. Currently only URLs with a 
 * protocol of type 'file:' are allowed. 
 * <br><br>
 * NOTE: NOT THREAD SAFE! You must create a new object for each batch set.
 * 
 * @author chet.rebman
 * @see FedoraAppConstants#BATCH_INGEST_NEW_METS_FOLDER_PROPERTY
 * @see FedoraAppConstants#BATCH_INGEST_FAILED_FOLDER_PROPERTY
 * @see FedoraAppConstants#BATCH_INGEST_COMPLETED_FOLDER_PROPERTY
 * @see FedoraAppConstants#BATCH_INGEST_WORK_FOLDER_PROPERTY
 */

public abstract class BatchIngestXMLhandler 
{

	/**
	 * Builds a w3c.Document DOM from an xml file.
	 * @param xmlFile the input file.
	 * @return Document the DOM
	 * @throws Exception
	 */
    public static org.w3c.dom.Document buildW3cDocument( File xmlFile) throws Exception {
        
    	org.w3c.dom.Document xmlDoc = null;  // validate=false;  schemaCheck=false  for debug
        javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        
        javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
        xmlDoc = builder.parse( xmlFile );
        
        return xmlDoc;
    }
    
    public static org.jdom.Document convertW3cDocumentToJDOM( org.w3c.dom.Document inputDocument )
    {
    	org.jdom.input.DOMBuilder builder = new org.jdom.input.DOMBuilder();
        org.jdom.Document jdomDoc = builder.build( inputDocument );
        return jdomDoc;
    }
    
    /**
     * Factory to get a new BatchIngestXMLhandler instance.
     * 
     * @param urlHandler gives access to batch ingest location URLs 
     * @return a new BatchIngestXMLhandler
     * @throws FatalException 
     * @see FedoraAppUtil#getUniqueBatchRunName(String, String)
     */
    public static BatchIngestXMLhandler getInstance( BatchIngestURLhandler urlHandler, FilenameFilter fileFilter ) throws FatalException {
        
        return new BatchIngestXMLhandlerImpl( urlHandler.getUniqueBatchRunName(), urlHandler, fileFilter );
    }
    
    /**
     * Get the next XML document for processing. The document is checked to be valid
     * against it's schema.
     * 
     * @see FedoraAppConstants#BATCH_INGEST_NEW_METS_FOLDER_PROPERTY
     * @param validate perform xml validation
     * @param schemaCheck validate against schema.
     * @return Document the next XML document as a DOM.
     * @throws Exception on any problem including IO error, parse error etc.
     */
    public abstract Document getNextXMLdocument( boolean validate, boolean schemaCheck ) throws  Exception;


    /**
     * Get the current document. Normally the programmer should use the method getNextXMLdocument.
     *
     * @see #getNextXMLdocument(boolean, boolean)
     * @param validate perform xml validation
     * @param schemaCheck validate against schema.
     * @return an XML document as a DOM document.
     * @throws FatalException 
     * @throws JDOMException if unable create the DOM for any reason.
     */
    public abstract Document getCurrentXMLdocument( boolean validate, boolean schemaCheck ) throws FatalException, JDOMException;


    /**
     * Test to see if there is another XML document that needs to be processed.
     * 
     * @return true if there are more documents.
     */
    public abstract boolean hasNext();


    /**
     * Store a FOXML document to the work folder prior to ingesting it into 
     * Fedora.
     * <br><br>
     * 
     * @see FedoraAppConstants#BATCH_INGEST_WORK_FOLDER_PROPERTY
     * @param foxmlDocument
     * @return File containing the FOXML <em>document</em> in XML format.
     * @throws Exception
     */
    public abstract File saveFOXMLtoWorkFolder( Document foxmlDocument ) throws FatalException; 
    

    /**
     * Move the failed file to the failed files directory.
     * 
     * @see FedoraAppConstants#BATCH_INGEST_FAILED_FOLDER_PROPERTY
     * @throws FatalException unrecoverable halt the application.
     */
    public abstract void markCurrentDocumentAsFailed() throws FatalException;
    
    
    /**
     * Same as markCurrentDocumentAsFailed() in addition it also moves PCOs to failed folder.
     * We assume a mets file and look for LOCTYPE="xxxx" within a mets:FLcat element.
     * 
     * @throws FatalException if unable to to move the xml file.
     */
    public abstract void markCurrentDocumentAsFailedMovePcos() throws FatalException;
    
    /**
     * Move the completed file to the completed files directory.
     * 
     * @see FedoraAppConstants#BATCH_INGEST_COMPLETED_FOLDER_PROPERTY
     * @throws FatalException uncrecoverable halt the application.
     */
    public abstract void markCurrentDocumentAsCompleted()  throws FatalException  ;
    

    /**
     * Get the path to current XML document. This intended to only be used for logging purposes. If there is no current
     * file return null;
     * 
     * @return path to current XML document or null if no current file.
     */
    public abstract String getCurrentDocumentCanonicalPath();
    
    /**
     * Get the name of the current document in the iteration.
     * 
     * @return name of the currentXmlDocument.
     */
    public abstract String getCurrentDocumentName();
    
    /**
     * Get the number XML documents that have been marked as completed.
     * 
     * @return the number of completed XML documents
     */
    public abstract int getCurrentCompleted();
    
    /**
     * Get the number XML docuements that have been marked as failed.
     * 
     * @return number of failed XML documents
     */
    public abstract int getCurrentFailed();
  
    /**
     * Read and XML file and build a DOM from it checking it against it's schema.
     * 
     * @param xmlFile the xml file to check and turn into DOM
     * @param validate perform xml validation **** NO LONGER USED ****
     * @param schemaCheck validate against schema.
     * @param schemaLocation location of xsd file.
     * @return a valid DOM document.
     * @throws JDOMException if unable to build build a valid document.
     */
    public abstract Document buildDocumentCheckValid( File xmlFile,  boolean validate, boolean schemaCheck, URL schemaLocation ) throws JDOMException;
    

    /**
     * Convienence method to get the unique name for this run of the batch ingest.
     * 
     * @see FedoraAppUtil#getUniqueBatchRunName
     */
    public abstract String getUniqueBatchRunName();
    

    /**
     * Convienence method to access the applications Batch Ingest URL handler.
     * 
     * @return the urlHandler
     */
    public abstract BatchIngestURLhandler getUrlHandler();
    
} // BatchIngestXMLhandler