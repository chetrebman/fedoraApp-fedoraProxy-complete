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

package edu.du.penrose.systems.fedoraApp.web.bus.security;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;


import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraApp.ProgramProperties;
import edu.du.penrose.systems.fedoraApp.util.FedoraAppUtil;
import edu.du.penrose.systems.fedora.client.Administrator;

public class LoginFedoraFormController  extends SimpleFormController {
    
	protected Object formBackingObject(HttpServletRequest request)
    throws Exception {
		
		return new LoginFedoraCmd();
	}
	
	
	public ModelAndView processFormSubmission( HttpServletRequest request,
            HttpServletResponse response,
            Object command,
            BindException errors) throws Exception {

        LoginFedoraCmd loginFedoraCmd = ( LoginFedoraCmd ) command;
        
        if ( errors.getAllErrors().size() > 0 ) {
            return this.showForm(request, response, errors);
        }
        
        String protocol = loginFedoraCmd.getProtocol();
        int    port     = Integer.parseInt( loginFedoraCmd.getPort() );
        String host     = loginFedoraCmd.getHost();
        String userName = loginFedoraCmd.getUsername();
        String password = loginFedoraCmd.getPassword();
        
        Administrator   administrator = null;
            
        try { 
        	ProgramProperties progProp = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() );   
        	String testPid = progProp.getProperty( FedoraAppConstants.BATCH_INGEST_VALID_PID_FOR_FEDORA_CONNECTION_TEST_PROPERTY );
            administrator = FedoraAppUtil.getAdministrator(host, port, userName, password);           
        
            administrator.getAPIM().getDatastream( testPid, "DC", null);
            this.logger.info( "Sucessfull Fedora login for user:"+userName);
                // user considered logged in once the Administrator session object is set.
            request.getSession().setAttribute( FedoraAppConstants.FEDORA_USERNAME_SESSION_VARIBLE, userName );
            request.getSession().setAttribute( "edu.du.penrose.systems.fedora.client.Administrator", administrator );
        }
        catch ( Exception e ) {
            errors.rejectValue( "protocol", "form.login.loginError" );
            this.logger.error( "ERROR: Unable to login "+e.getMessage() );
            return this.showForm(request, response, errors);
        }
        
        
        //Administrator.INSTANCE.setLoginInfo(protocol, host, port, username, pass);
        

        // attempt an API-M (SOAP) operation
 //       UserInfo inf = fc.getAPIM().describeUser(user);
        
/*  try {
        if (m_finder==null) m_finder=new AutoFinder(SearchFedoraFormController.APIA);
        searchAndDisplay(m_finder.findObjects(resultFields, maxResults,
                query), displayFields);
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("ERROR: " + e.getClass().getName() + ":" + e.getMessage());
    }*/
        

        return super.processFormSubmission(request, response, command, errors);
        
	} // processFormSubmission
	
	
	
	public ModelAndView onSubmit(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
    	throws Exception {

    
		
		return new ModelAndView( this.getSuccessView() );
	}
	
} // SearchFedoraFormController
