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

import javax.servlet.http.HttpServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.io.*;

import edu.du.penrose.systems.exceptions.NoInitNonCheckedException;

/**
 * Give access to the servlet-context variables from anywhere in the application. 
 * <br>
 * This servlet is called when the web container(tomcat)starts the application. 
 * Only the init method is implemented which allows us to get a copy of the 
 * current ServletConfig for this web application.
 * <br>
 * Note: the servlet must be registered in web.xml with a startup value of 1 so
 * that other classes can use it during their init. process.
 * <br>
 * Note: Marked as depreciated since in a spring applicaton you can simply implemnet 
 * ServletContextAware
 * 
 * @author chet.rebman
 * @deprecated In a spring applicaton you can simply implemnet ServletContextAware
 * @see org.springframework.web.context.ServletContextAware
 *
 */
public class WebAppInit extends HttpServlet {

	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    static String myWebContextPath = null;
	
	static ServletConfig mySerletConfig = null;
	
	/**
	 * This method is called when the web application is put into service, giving
	 * use access to the ServeletConfig object.
	 * 
	 */
	public void init(ServletConfig config)throws ServletException {
		
		WebAppInit.mySerletConfig = config;
		
	}
	
	/**
	 * Get the real path for the current web context root directory.
	 * 
	 * @throws NoInitNonCheckedException
	 * @return the real path for the web context root directory
	 */
	public String getWebContextPath() {
		
		if ( WebAppInit.mySerletConfig == null ) {
		
			throw new NoInitNonCheckedException( "Web containter not initialized");
		}
		
		WebAppInit.myWebContextPath = mySerletConfig.getServletContext().getRealPath("/");
		return WebAppInit.myWebContextPath;
	}
	
	/**
	 * Get the real path for the \WEB-INF\ directory in the current web context.
	 * 
	 * @return the real path including the \WEB-INF\ directory
	 */
	public String get_WEB_INF_path() {
					
		if ( WebAppInit.myWebContextPath == null ) {

			WebAppInit.myWebContextPath = this.getWebContextPath();
		}
		
		return WebAppInit.myWebContextPath+"WEB-INF"+File.separator;
	}
	
	/**
	 * Used for setting a context path for TESTING this method should not be used outside of testing.
	 * 
	 * @deprecated
	 * @param contextTestPath
	 */
	public void setContextTestPath( String contextTestPath ) {
		
		myWebContextPath = contextTestPath;
	}
	
} // WebAppInit
