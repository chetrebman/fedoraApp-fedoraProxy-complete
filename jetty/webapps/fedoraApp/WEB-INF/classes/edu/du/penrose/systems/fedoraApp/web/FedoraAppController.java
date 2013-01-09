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

package edu.du.penrose.systems.fedoraApp.web;

import org.springframework.web.servlet.mvc.Controller;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.web.servlet.ModelAndView;
 
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.du.penrose.systems.fedoraApp.web.exceptions.PageNotFoundNonCheckedException;
import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.util.MyServletContextListener;

/**
 * This is top level web/servlet controller for the Fedora application. Web 
 * requests that are not mapped to forms are passed to this object for processing.
 * 
 * @author Chet
 */
public class FedoraAppController implements Controller {

    /** Logger for this class and subclasses */
    
	    protected final Log logger = LogFactory.getLog(getClass());

	    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response)
	            throws ServletException, IOException {
	    	
	        logger.info("FedoraAppController searching for view:"+request.getRequestURL());
//
//	        if ( request.getRequestURL().toString().endsWith( "/batchIngest.htm" )){
//		        return new ModelAndView( FedoraAppConstants.JSP_URI_PATH+"batchIngest.jsp" );      	
//	        }
	        
	        if ( request.getRequestURL().toString().toLowerCase().endsWith( "/error.htm" )){
		        return new ModelAndView( MyServletContextListener.JSP_URI_PATH+"error.jsp" );      	
	        }

	        if ( request.getRequestURL().toString().toLowerCase().endsWith( "/hello.htm" )){
		        return new ModelAndView( MyServletContextListener.JSP_URI_PATH+"hello.jsp" );      	
	        }

	        if ( request.getRequestURL().toString().toLowerCase().endsWith( "/index.htm" )){
		        return new ModelAndView( MyServletContextListener.JSP_URI_PATH+"hello.jsp" );      	
	        }
	        
	        if ( request.getRequestURL().toString().toLowerCase().endsWith( "/viewrunningingests.htm" )){
		        return new ModelAndView( MyServletContextListener.JSP_URI_PATH+"viewRunningIngests.jsp" );      	
	        }

	        if ( request.getRequestURL().toString().endsWith( "/getFedoraObj" )){
		        return new ModelAndView( MyServletContextListener.JSP_URI_PATH+"getFedoraObj.jsp" );      	
	        }

	        if ( request.getRequestURL().toString().endsWith( "/searchFedora" )){
		        return new ModelAndView( MyServletContextListener.JSP_URI_PATH+"searchFedora.jsp" );   
	        }

	        if ( request.getRequestURL().toString().endsWith( "/loginFedora" )){
		        return new ModelAndView( MyServletContextListener.JSP_URI_PATH+"loginFedora.jsp" );   
	        }
	        
            if ( request.getRequestURL().toString().endsWith( "/viewFO.htm" )){
                return new ModelAndView( MyServletContextListener.JSP_URI_PATH+"viewFO.jsp" );   
            }

            if ( request.getRequestURL().toString().endsWith( "/batchIngestReport.htm" )){
                return new ModelAndView( MyServletContextListener.JSP_URI_PATH+"batchIngestReport.jsp" );   
            }
            
            if ( request.getRequestURL().toString().endsWith( "/batchIngestPidReport.htm" )){
                return new ModelAndView( MyServletContextListener.JSP_URI_PATH+"batchIngestPidReport.jsp" );   
            }

//            if ( request.getRequestURL().toString().endsWith( "/batchIngestStatus.htm" )){
//                return new ModelAndView( MyServletContextListener.JSP_URI_PATH+"batchIngestStatus.jsp" );   
//            }
            
            if ( request.getRequestURL().toString().endsWith( "/versions.htm" )){
                return new ModelAndView( MyServletContextListener.JSP_URI_PATH+"versions.jsp" );   
            }
	        
		    
	 	   throw new PageNotFoundNonCheckedException("Page not found");
	 	   
	        
	    } // handleRequest()
	    
	   
} // FedoraAppController
