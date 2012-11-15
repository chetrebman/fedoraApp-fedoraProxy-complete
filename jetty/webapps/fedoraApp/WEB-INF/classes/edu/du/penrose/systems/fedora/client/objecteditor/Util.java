/*
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Educational Community License (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.opensource.org/licenses/ecl1.txt">
 * http://www.opensource.org/licenses/ecl1.txt.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2007 by 
 * The Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 */

package edu.du.penrose.systems.fedora.client.objecteditor;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import org.apache.axis.types.NonNegativeInteger;

import org.fcrepo.server.types.gen.ComparisonOperator;
import org.fcrepo.server.types.gen.Condition;
import org.fcrepo.server.types.gen.FieldSearchQuery;
import org.fcrepo.server.types.gen.FieldSearchResult;
import org.fcrepo.server.types.gen.ObjectFields;

//import fedora.client.objecteditor.types.DatastreamInputSpec; // 2.2
//import fedora.client.objecteditor.types.MethodDefinition;
//import fedora.server.access.FedoraAPIA;

import org.fcrepo.client.objecteditor.types.DatastreamInputSpec; // 3.4
import org.fcrepo.client.objecteditor.types.MethodDefinition;
import org.fcrepo.server.access.FedoraAPIA;

import edu.du.penrose.systems.fedora.client.Downloader;


/**
 * THIS CLASS IS ALMOST AN EXACT COPY OF fedora.client.Downloader.
 * <br>
 * This class has been modified to remove dependencies
 * on fedora.client.Administrator static variables. Also replaced 
 * fedora.Downloader with edu.du.penrose.systems.fedora.client.Downloader
 * <br> (Original comments below).
 * <br><br>
 * Some static utility methods that might be needed across several classes
 * in this package.
 * 
 */
public abstract class Util {

    public static Map getBDefLabelMap( FedoraAPIA apia )
            throws IOException {
        try {
            HashMap labelMap=new HashMap();
            FieldSearchQuery query=new FieldSearchQuery();
            Condition[] conditions=new Condition[1];
            conditions[0]=new Condition();
            conditions[0].setProperty("fType");
            conditions[0].setOperator(ComparisonOperator.fromValue("eq"));
            conditions[0].setValue("D");
            query.setConditions(conditions);
            String[] fields=new String[] {"pid", "label"};
            FieldSearchResult result=apia.findObjects(
                        fields, new NonNegativeInteger("50"), query);
            while (result!=null) {
                ObjectFields[] resultList=result.getResultList();
                for (int i=0; i<resultList.length; i++) {
                    labelMap.put(resultList[i].getPid(), resultList[i].getLabel());
                }
                if (result.getListSession()!=null) {
                    result=apia.resumeFindObjects(
                            result.getListSession().getToken());
                } else {
                    result=null;
                }
            }
            return labelMap;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Get a map of pid-to-label of behavior mechanisms that implement
     * the behavior defined by the indicated bdef.
     */
    public static Map getBMechLabelMap( FedoraAPIA apia, String bDefPID) 
            throws IOException {
        try {
            HashMap labelMap=new HashMap();
            FieldSearchQuery query=new FieldSearchQuery();
            Condition[] conditions=new Condition[2];
            conditions[0]=new Condition();
            conditions[0].setProperty("fType");
            conditions[0].setOperator(ComparisonOperator.fromValue("eq"));
            conditions[0].setValue("M");
            conditions[1]=new Condition();
            conditions[1].setProperty("bDef");
            conditions[1].setOperator(ComparisonOperator.fromValue("has"));
            conditions[1].setValue(bDefPID);
            query.setConditions(conditions);
            String[] fields=new String[] {"pid", "label"};
            FieldSearchResult result=apia.findObjects(
                        fields, new NonNegativeInteger("50"), query);
            while (result!=null) {
                ObjectFields[] resultList=result.getResultList();
                for (int i=0; i<resultList.length; i++) {
                    labelMap.put(resultList[i].getPid(), resultList[i].getLabel());
                }
                if (result.getListSession()!=null) {
                    result=apia.resumeFindObjects(
                            result.getListSession().getToken());
                } else {
                    result=null;
                }
            }
            return labelMap;
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }

    public static Map getInputSpecMap( Downloader downloader, Set bMechPIDs) 
            throws IOException {
        HashMap specMap=new HashMap();
        Iterator iter=bMechPIDs.iterator();
        while (iter.hasNext()) {
            String pid=(String) iter.next();
            specMap.put(pid, getInputSpec( downloader, pid ));
        }
        return specMap;
    }

    public static DatastreamInputSpec getInputSpec( Downloader downloader, String bMechPID ) 
            throws IOException {
        HashMap hash=new HashMap();
        hash.put("itemID", "DSINPUTSPEC");
        /*return DatastreamInputSpec.parse(
                Administrator.DOWNLOADER.getDissemination(
                        bMechPID,
                        "fedora-system:3",
                        "getItem",
                        hash,
                        null)
                );*/
        return DatastreamInputSpec.parse(
                downloader.getDatastreamDissemination(
                        bMechPID,
                        "DSINPUTSPEC",
                        null)
                );        

    }

    /**
     * Get the list of MethodDefinition objects defined by the indicated
     * behavior definition.
     */
    public static java.util.List getMethodDefinitions( Downloader downloader, String bDefPID) 
            throws IOException {
        HashMap parms=new HashMap();
        parms.put("itemID", "METHODMAP");
        /*return MethodDefinition.parse(
                Administrator.DOWNLOADER.getDissemination(bDefPID, 
                                                          "fedora-system:3",
                                                          "getItem", 
                                                          parms, 
                                                          null));*/
        return MethodDefinition.parse(
                downloader.getDatastreamDissemination(bDefPID, 
                                                          "METHODMAP", 
                                                          null));        
    }

    /**
     * Get the indicated fields of the indicated object from the repository.
     */
    public static ObjectFields getObjectFields( FedoraAPIA apia, String pid, String[] fields)
            throws IOException {
        FieldSearchQuery query=new FieldSearchQuery();
        Condition[] conditions=new Condition[1];
        conditions[0]=new Condition();
        conditions[0].setProperty("pid");
        conditions[0].setOperator(ComparisonOperator.fromValue("eq"));
        conditions[0].setValue(pid);
        query.setConditions(conditions);
        FieldSearchResult result=apia.findObjects(
                    fields, new NonNegativeInteger("1"), query);
        ObjectFields[] resultList=result.getResultList();
        if (resultList==null || resultList.length==0) {
            throw new IOException("Object not found in repository");
        }
        return resultList[0];
    }

    /**
     * Layout the provided components in two columns, each left-aligned,
     * where the left column's cells are as narrow as possible.
     *
     * If north is true, all cells will be laid out to the NORTHwest.
     * This is useful when some rows' cells aren't the same size vertically.
     * If allowStretching is true, components on the right will be stretched
     * if they can be.
     */
    public static void addRows(JComponent[] left, 
                               JComponent[] right,
                               GridBagLayout gridBag, 
                               Container container, 
                               boolean north,
                               boolean allowStretching) {
        GridBagConstraints c=new GridBagConstraints();
        c.insets=new Insets(0, 4, 4, 4);
        if (north) {
            c.anchor=GridBagConstraints.NORTHWEST;
        } else {
            c.anchor=GridBagConstraints.WEST;
        }
        for (int i=0; i<left.length; i++) {
            c.gridwidth=GridBagConstraints.RELATIVE; //next-to-last
            c.fill=GridBagConstraints.NONE;      //reset to default
            c.weightx=0.0;                       //reset to default
            gridBag.setConstraints(left[i], c);
            container.add(left[i]);

            c.gridwidth=GridBagConstraints.REMAINDER;     //end row
            if (right[i] instanceof JComboBox) {
                if (allowStretching) {
                    c.fill=GridBagConstraints.HORIZONTAL;
                }
            } else {
                c.fill=GridBagConstraints.HORIZONTAL;
            }
            c.weightx=1.0;
            gridBag.setConstraints(right[i], c);
            container.add(right[i]);
        }

    }

}
