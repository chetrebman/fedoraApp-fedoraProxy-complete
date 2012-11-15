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

package edu.du.penrose.systems.fedoraApp.web.bus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.validation.BindException;

import edu.du.penrose.systems.fedoraApp.web.data.GetFedoraObjCmd;

//import fedora.server.types.gen.Datastream;    //. 2.2
//import fedora.server.types.gen.ObjectFields;
//import fedora.server.access.FedoraAPIA;

import org.fcrepo.server.types.gen.ObjectFields; // 3.4
import org.fcrepo.server.access.FedoraAPIA;

import edu.du.penrose.systems.fedora.client.Administrator;

//import fedora.client.objecteditor.Util;

import edu.du.penrose.systems.fedora.client.objecteditor.Util;
import edu.du.penrose.systems.util.MyServletContextListener;

/**
 * Form controller to retrieve an object based on its PID from the Fedora
 * repository.
 * 
 * @author chet.rebman
 *
 */
public class GetFedorObjFormController extends SimpleFormController {

	protected Object formBackingObject(HttpServletRequest request)
    throws Exception {

		GetFedoraObjCmd commandObj = new GetFedoraObjCmd();
		
		return commandObj;
	}
	
	protected ModelAndView processFormSubmission(HttpServletRequest request,
            HttpServletResponse response,
            Object command,
            BindException errors)
     throws Exception {
		
		 // request.getParameter( "objectPID")
		
		GetFedoraObjCmd getFOcmd    = (GetFedoraObjCmd) command;
		Administrator administrator = (Administrator) request.getSession().getAttribute( "edu.du.penrose.systems.fedora.client.Administrator" );
		
		
        // query the server for field object fields
		
		try {
			GetFedorObjFormController.getFedoraObj( request, administrator.getAPIA(), getFOcmd.getObjectPID() );
		}
		catch ( Exception e ) {
			errors.rejectValue( "objectPID", null, e.getMessage() );
		}
        
		
		return super.processFormSubmission( request, response, command, errors );
		
	} // processFormSubmission
	
	public ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
    	throws Exception {
		
		GetFedoraObjCmd getFOcmd = (GetFedoraObjCmd) command;

		return new ModelAndView( MyServletContextListener.JSP_URI_PATH+"viewFO.jsp?"+getFOcmd.getObjectPID() );
	}

	/**
	 * Get a fedora object from the fedora server.
	 * 
	 * TBD Is this the best place for this?
	 * 
	 * @param request OPTIONAL HTTP request object  used to save results for jsp
	 * pages. Set to null if there is no request object.
	 * @param apia
	 * @param objectPID
	 * @return The result fedora object fields.
	 * @throws Exception 
	 */
	static public ObjectFields getFedoraObj( HttpServletRequest request, FedoraAPIA apia, String objectPID ) 
		throws Exception {
		
		 ObjectFields results=Util.getObjectFields( apia, objectPID, 
                 new String[] {"pid", 
                               "state", 
                               "label", 
                               "cModel", 
                               "cDate", 
                               "mDate", 
                               "ownerId", 
                               "fType"});
		 
		 if ( request != null ){
			 request.setAttribute( "fedora.server.types.gen.ObjectFields", results );
		 }
		 
		return results;
		
	} // getFedoraObj
	
} // GetFedorObjFormController
