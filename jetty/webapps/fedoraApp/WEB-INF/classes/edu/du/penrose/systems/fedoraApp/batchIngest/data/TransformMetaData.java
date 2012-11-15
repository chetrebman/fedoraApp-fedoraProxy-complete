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

import java.io.File;

import org.jdom.Document;
import org.jdom.transform.XSLTransformException;

/**
 * Define the interface for transforming a Document into another Document type
 * using an XSLT transformation.
 * 
 * @author chet.rebman
 *
 */
public interface TransformMetaData {

    /**
     * Transform the metadata to FOXML format. The transformation used depends on the implemening 
     * subclass.
     * 
     * @return transformed DOM Document.
     * @throws XSLTransformException 
     */
    Document transformMetaToFOXML( Document inputDocument ) throws XSLTransformException;
    
    
} // TransformMetaData
