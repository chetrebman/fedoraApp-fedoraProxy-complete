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

package edu.du.penrose.systems.fedoraApp.web.bus.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.util.MyServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


/**
 * This intercepter is defined in the applications main fedoraApp-servlet.XML 
 * file and intercepts all request for this application. It can be used to check 
 * that a user is logged before being allowed access to anything but the login 
 * page.
 * 
 * @author Chet
 *
 */
public class SecurityManager extends HandlerInterceptorAdapter {

    /** Logger for this class and subclasses */
    
    protected final Log logger = LogFactory.getLog(getClass());

    static public String BOGUS_ATTRIBUE_NAME="SET_IN_SECRUTIY_MANAGER";
    static public String BOGUS_ATTRIBUTE_VALUE="Anything";
    
    private final String loginPage = "loginFedora.htm";
    private final String logoutPage= "logout.htm";
    private String forwardPage = "loginFedora.htm";
    boolean forceLogin         = true;
    
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {
        
        // prevent infinite loop.
        if ( request.getRequestURI().contains( getForwardPage() )){  //TBD major coupling here!
            return true;
        }        

        // handle logout.
        if ( request.getRequestURI().contains( logoutPage )){  

            String fedoraUserName = (String) request.getSession().getAttribute( FedoraAppConstants.FEDORA_USERNAME_SESSION_VARIBLE );
            if ( fedoraUserName != null ) {
                this.logger.info( "Logout for user:"+fedoraUserName);
            }
            request.getSession().removeAttribute( FedoraAppConstants.FEDORA_USERNAME_SESSION_VARIBLE ); 
            request.getSession().removeAttribute("edu.du.penrose.systems.fedora.client.Administrator"); 
            response.sendRedirect( "/"+FedoraAppConstants.getServletContextListener().getWebApplicatonName() + "/"+ loginPage ); // TBD magic #
            return false;
        }  

        //  has user logged in yet? Catch all but the above exceptions.
        Object result = request.getSession().getAttribute("edu.du.penrose.systems.fedora.client.Administrator"); 
        if (result == null && forceLogin == true) {
            response.sendRedirect( "/"+FedoraAppConstants.getServletContextListener().getWebApplicatonName() + getForwardPage() );
            return false;
        }
        else {  // have we timed out?
            if ( ! request.getSession().getAttributeNames().hasMoreElements() ) { 
                request.getSession().setAttribute( BOGUS_ATTRIBUE_NAME, BOGUS_ATTRIBUTE_VALUE );
                response.sendRedirect( "/"+FedoraAppConstants.getServletContextListener().getWebApplicatonName() + getForwardPage() );
                return false;   
            }
        }

        return true;
    }

    /**
     * @return the forwardPage
     */
    public String getForwardPage() {
        return "/"+forwardPage;
    }

    /**
     * @param forwardPage the forwardPage to set
     */
    public void setForwardPage(String forwardPage) {
        this.forwardPage = forwardPage;
    }

    /**
     * @param forceLogin the forceLogin to set
     */
    public void setForceLogin(boolean forceLogin) {
        this.forceLogin = forceLogin;
    }

} // class SecurityManager
