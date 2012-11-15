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

import javax.xml.transform.sax.SAXTransformerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.transform.XSLTransformException;
import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;

import edu.du.penrose.systems.util.XSLTransformerDUextension;

/**
 * Contains the xslt transform to be used as the METS to FOXML transform and
 * implements method to perform the transfrom.
 * 
 * <br><br>
 * NOTE: Sets the default java XSLT processor to Saxon9B
 * 
 * @author chet.rebman
 *
 */
public class TransformMetsData implements TransformMetaData {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());
    
    XSLTransformerDUextension transformer = null;

    public TransformMetsData() throws  XSLTransformException {

        
        this.logger.info( "Set java default XSLT processor to "+FedoraAppConstants.SAXON_9_SYSTEM_FACTORY );

        File xsltFile = new File( FedoraAppConstants.getServletContextListener().getApplicationConfigPath() + FedoraAppConstants.METS_TO_FOXML_XSL );
        this.logger.info( "LETS SEE IF XSLT FILE EXISTS = "+xsltFile.exists());

        
        
        /**
         * Our transformer tends to interact with tomcat and other webapps..so we started doing things different 2-15-2010.
         * 
         * THIS IS THE OLD WAY
         * System.setProperty( FedoraAppConstants.TRANSFORM_FACTORY_KEY, FedoraAppConstants.SAXON_9_SYSTYEM_FACTORY );   
         * transformer = new XSLTransformer( xsltFile );
         */
            
        try 
        { // NEW WAY

        	transformer = new XSLTransformerDUextension( xsltFile, (SAXTransformerFactory) Class.forName( FedoraAppConstants.SAXON_9_SYSTEM_FACTORY ).newInstance() );
		} 
        catch (Exception e) 
		{
			throw new RuntimeException( e.getMessage() );
		}

    }
    
    public Document transformMetaToFOXML(Document inputDocument) throws XSLTransformException {
        
       // throw new XSLTransformException("test"); // TBD TEMP
        
        return this.transformer.transform( inputDocument ); 
    }
    


} // TransformMetsData
