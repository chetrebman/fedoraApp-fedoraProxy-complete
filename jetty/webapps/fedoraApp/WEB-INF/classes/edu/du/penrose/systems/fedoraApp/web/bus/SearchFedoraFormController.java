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

package edu.du.penrose.systems.fedoraApp.web.bus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.apache.commons.codec.DecoderException;
import org.apache.axis.client.AxisClient;

import edu.du.penrose.systems.fedoraApp.ProgramFileProperties;
import edu.du.penrose.systems.fedoraApp.ProgramProperties;
import edu.du.penrose.systems.fedoraApp.web.data.GetFedoraObjCmd;
import edu.du.penrose.systems.fedoraApp.web.data.SearchFedoraCmd;

//import fedora.server.types.gen.Condition;             // 2.2
//import fedora.server.types.gen.ComparisonOperator;
//import fedora.server.access.FedoraAPIA;
//import fedora.server.management.FedoraAPIM;
//import fedora.server.types.gen.FieldSearchQuery;
//import fedora.server.types.gen.FieldSearchResult;
//import fedora.client.utility.AutoFinder;
//import fedora.server.types.gen.ObjectFields; 


import org.fcrepo.server.types.gen.Condition;           // 3.4
import org.fcrepo.server.types.gen.ComparisonOperator;
import org.fcrepo.server.access.FedoraAPIA;
import org.fcrepo.server.management.FedoraAPIM;
import org.fcrepo.server.types.gen.FieldSearchQuery;
import org.fcrepo.server.types.gen.FieldSearchResult;
import org.fcrepo.client.utility.AutoFinder;
import org.fcrepo.server.types.gen.ObjectFields;

import edu.du.penrose.systems.fedora.client.Administrator;

import org.apache.axis.types.NonNegativeInteger;

/**
 * This form controller performs a search of the fedora repository.
 * @author chet.rebman
 *
 */
public class SearchFedoraFormController  extends SimpleFormController {
    
	protected Object formBackingObject(HttpServletRequest request)
    throws Exception {
		
		return new SearchFedoraCmd();
	}
	
	public ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
    	throws Exception {

		SearchFedoraCmd searchCmd = ( SearchFedoraCmd ) command;
		Administrator administrator = (Administrator) request.getSession().getAttribute( "edu.du.penrose.systems.fedora.client.Administrator" );
	
		FieldSearchQuery query=new FieldSearchQuery();
		query.setTerms( searchCmd.getQuery() );
		
		FieldSearchResult fsResult = null;
		int maxResults = 100;
		String[] resultFields = new String[ 3 ];
		String[] displayFields = resultFields;
		resultFields[ 0 ] = "pid";
		resultFields[ 1 ] = "cDate";
		resultFields[ 2 ] = "title";
		
		try {
		
			fsResult = administrator.getAPIA().findObjects(resultFields, 
				new NonNegativeInteger("" + maxResults), query);
		}
		catch (java.rmi.RemoteException e ){ 
			Exception newE = new Exception( e.getMessage()+" - Details in $FEDORA_HOME/server/logs/fedora.log", e );
			throw newE;
		}
		
		searchCmd.setResultFields( resultFields );
		searchCmd.setFsDataResults( this.convertSearchData(fsResult, displayFields) );
		
		request.setAttribute("edu.du.penrose.systems.fedoraApp.web.data.SearchFedoraCmd", searchCmd);
		
		return new ModelAndView( this.getSuccessView() );
	}
	
	protected Object[][] convertSearchData( FieldSearchResult fsResult, String[] displayFields )
		throws Exception {
		
		String[] m_rowPids;
		
        // put the resulting data into a structure suitable for display
        ObjectFields[] ofs=fsResult.getResultList();
        Object[][] data=new Object[ofs.length][displayFields.length];
        // while adding the pids to m_rowPids so they can be used later
        m_rowPids=new String[ofs.length];
        for (int i=0; i<ofs.length; i++) {
            ObjectFields o=ofs[i];
            m_rowPids[i]=o.getPid();
            for (int j=0; j<displayFields.length; j++) {
                data[i][j]=getValue(o, displayFields[j]);
            }
        }
        
		return data;
		
	} // convertSearchData()
	
	protected String getValue(ObjectFields o, String name) {
        if (name.equals("pid")) return o.getPid();
        if (name.equals("label")) return o.getLabel();
        
//         if (name.equals("fType")) return o.getFType();  // 2.2 use  o.getType( int ) ???? int not documented
        
//        if (name.equals("cModel")) return o.getCModel();
        
        if (name.equals("state")) return o.getState();
        if (name.equals("ownerId")) return o.getOwnerId();
        if (name.equals("cDate")) return o.getCDate();
        if (name.equals("mDate")) return o.getMDate();
        if (name.equals("dcmDate")) return o.getDcmDate();
        
//        if (name.equals("bDef")) return getList(o.getBDef()); // 2.2 ???
//        if (name.equals("bMech")) return getList(o.getBMech());
       
        if (name.equals("title")) return getList(o.getTitle());
        if (name.equals("creator")) return getList(o.getCreator());
        if (name.equals("subject")) return getList(o.getSubject());
        if (name.equals("description")) return getList(o.getDescription());
        if (name.equals("publisher")) return getList(o.getPublisher());
        if (name.equals("contributor")) return getList(o.getContributor());
        if (name.equals("date")) return getList(o.getDate());
        if (name.equals("type")) return getList(o.getType());
        if (name.equals("format")) return getList(o.getFormat());
        if (name.equals("identifier")) return getList(o.getIdentifier());
        if (name.equals("source")) return getList(o.getSource());
        if (name.equals("language")) return getList(o.getLanguage());
        if (name.equals("relation")) return getList(o.getRelation());
        if (name.equals("coverage")) return getList(o.getCoverage());
        if (name.equals("rights")) return getList(o.getRights());
        return null;
    }
    

	protected String getList(String[] s) {
        if (s==null) return "";
        StringBuffer out=new StringBuffer();
        for (int i=0; i<s.length; i++) {
            if (i>0) out.append(", ");
            out.append(s[i]);
        }
        return out.toString();
    }
    
} // SearchFedoraFormController
