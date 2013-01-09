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

package edu.du.penrose.systems.util;


import java.util.*;
import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamSource;
import org.jdom.*;
import org.xml.sax.EntityResolver;

import org.jdom.Document;
import org.jdom.transform.*;

/**
 * This is a copy of org.jdom.transform.XSLTransformer, with the exception of the new constructor allowing you too specify the
 * XSLT processor.
 * 
 * @see "org.jdom.transform.XSLTransformer"
 * @see #XSLTransformerDUextension(File, SAXTransformerFactory)
 */
public class XSLTransformerDUextension  {

    private static final String CVS_ID =
            "@(#) $RCSfile: XSLTransformerDUextension.java,v $ $Revision: 1.5 $ $Date: 2007/11/14 04:36:54 $ $Name: jdom_1_1_1 $";

    private Templates templates;

    /**
     * The custom JDOM factory to use when building the transformation
     * result or <code>null</code> to use the default JDOM classes.
     */
    private JDOMFactory factory = null;

    // Internal constructor to support the other constructors
    private XSLTransformerDUextension(Source stylesheet) throws XSLTransformException {
        try {
            templates = TransformerFactory.newInstance()
                    .newTemplates(stylesheet);
        }
        catch (TransformerException e) {
            throw new XSLTransformException("Could not construct XSLTransformerDUextension", e);
        }
    }
    

    // New constructor added by DU.
    private XSLTransformerDUextension(Source stylesheet, SAXTransformerFactory saxTransformFactory) throws XSLTransformException {
        try {
            templates = saxTransformFactory.newTemplates(stylesheet);
        }
        catch (TransformerException e) {
            throw new XSLTransformException("Could not construct XSLTransformerDUextension", e);
        }
    }

    /**
     * Creates a transformer for a given stylesheet system id.
     *
     * @param  stylesheetSystemId  source stylesheet as a Source object
     * @throws XSLTransformException       if there's a problem in the TrAX back-end
     */
    public XSLTransformerDUextension(String stylesheetSystemId) throws XSLTransformException {
        this(new StreamSource(stylesheetSystemId));
    }

    /**
     * <p>
     * This will create a new <code>XSLTransformerDUextension</code> by
     *  reading the stylesheet from the specified
     *   <code>InputStream</code>.
     * </p>
     *
     * @param stylesheet <code>InputStream</code> from which the stylesheet is read.
     * @throws XSLTransformException when an IOException, format error, or
     * something else prevents the stylesheet from being compiled
     */
    public XSLTransformerDUextension(InputStream stylesheet) throws XSLTransformException {
        this(new StreamSource(stylesheet));
    }

    /**
     * <p>
     * This will create a new <code>XSLTransformerDUextension</code> by
     *  reading the stylesheet from the specified
     *   <code>Reader</code>.
     * </p>
     *
     * @param stylesheet <code>Reader</code> from which the stylesheet is read.
     * @throws XSLTransformException when an IOException, format error, or
     * something else prevents the stylesheet from being compiled
     */
    public XSLTransformerDUextension(Reader stylesheet) throws XSLTransformException {
        this(new StreamSource(stylesheet));
    }

    /**
     * <p>
     * This will create a new <code>XSLTransformerDUextension</code> by
     *  reading the stylesheet from the specified
     *   <code>File</code>.
     * </p>
     *
     * @param stylesheet <code>File</code> from which the stylesheet is read.
     * @throws XSLTransformException when an IOException, format error, or
     * something else prevents the stylesheet from being compiled
     */
    public XSLTransformerDUextension(File stylesheet) throws XSLTransformException {
        this(new StreamSource(stylesheet));
    }
    
    /**
     * <p>
     * This will create a new <code>XSLTransformerDUextension</code> by
     *  reading the stylesheet from the specified <code>SAXTransformerFactory</code>
     *   <code>File</code> using specified .<br><br>
     *   This is a DU extension.
     * </p>
     *
     * @param stylesheet <code>File</code> from which the stylesheet is read.
     * @param saxTransFormFactory <code>SAXTransformerFactory</code> the XSLT transformer to be used.
     * @throws XSLTransformException when an IOException, format error, or
     * something else prevents the stylesheet from being compiled
     */
    public XSLTransformerDUextension(File stylesheet, SAXTransformerFactory saxTransFormFactory ) throws XSLTransformException {
        this(new StreamSource(stylesheet), saxTransFormFactory);
    }
    
    
    
    /**
     * <p>
     * This will create a new <code>XSLTransformerDUextension</code> by
     *  reading the stylesheet from the specified
     *   <code>Document</code>.
     * </p>
     *
     * @param stylesheet <code>Document</code> containing the stylesheet.
     * @throws XSLTransformException when the supplied <code>Document</code>
     *  is not syntactically correct XSLT
     */
    public XSLTransformerDUextension(Document stylesheet) throws XSLTransformException {
        this(new JDOMSource(stylesheet));
    }

    /**
     * Transforms the given input nodes to a list of output nodes.
     *
     * @param  inputNodes          input nodes
     * @return                     transformed output nodes
     * @throws XSLTransformException       if there's a problem in the transformation
     */
    public List transform(List inputNodes) throws XSLTransformException {
        JDOMSource source = new JDOMSource(inputNodes);
        JDOMResult result = new JDOMResult();
        result.setFactory(factory);  // null ok
        try {
            templates.newTransformer().transform(source, result);
            return result.getResult();
        }
        catch (TransformerException e) {
            throw new XSLTransformException("Could not perform transformation", e);
        }
    }
    
    /**
     * Transforms the given document to an output document.
     *
     * @param  inputDoc            input document
     * @return                     transformed output document
     * @throws XSLTransformException       if there's a problem in the transformation
     */
    public Document transform(Document inputDoc) throws XSLTransformException {
    	return transform(inputDoc, null);
    }

    /**
     * Transforms the given document to an output document.
     *
     * @param  inputDoc            input document
     * @param  resolver			   entity resolver for the input document
     * @return                     transformed output document
     * @throws XSLTransformException       if there's a problem in the transformation
     */
    public Document transform(Document inputDoc, EntityResolver resolver) throws XSLTransformException {
        JDOMSource source = new JDOMSource(inputDoc, resolver);
        JDOMResult result = new JDOMResult();
        result.setFactory(factory);  // null ok
        try {
            templates.newTransformer().transform(source, result);
            return result.getDocument();
        }
        catch (TransformerException e) {
            throw new XSLTransformException("Could not perform transformation", e);
        }
    }

    /**
     * Sets a custom JDOMFactory to use when building the
     * transformation result. Use a custom factory to build the tree
     * with your own subclasses of the JDOM classes.
     *
     * @param  factory   the custom <code>JDOMFactory</code> to use or
     *                   <code>null</code> to use the default JDOM
     *                   classes.
     *
     * @see    #getFactory
     */
    public void setFactory(JDOMFactory factory) {
      this.factory = factory;
    }

    /**
     * Returns the custom JDOMFactory used to build the transformation
     * result.
     *
     * @return the custom <code>JDOMFactory</code> used to build the
     *         transformation result or <code>null</code> if the
     *         default JDOM classes are being used.
     *
     * @see    #setFactory
     */
    public JDOMFactory getFactory() {
      return this.factory;
    }

	

}
