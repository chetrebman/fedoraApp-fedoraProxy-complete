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

package edu.du.penrose.systems.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraApp.ProgramProperties;

public class MyServletContextListener implements ServletContextListener, MyServletContextListener_INF {

	private static ServletContext  myServletContext = null;
	private static String        webContextTestPath = null;
	
	private MyServletContextListener_INF myInstance = null;
	
    static public  String VERSIONS_FILE_NAME = "versions.txt";

    static public  String JSP_URI_PATH = "/WEB-INF/jsp/";
    static private String CONFIG_DIR_NAME = "config";
    static private String RESOURCES_DIR_NAME = "resources";
    public static  String CONFIG_RELATIVE_URI_PATH   = "WEB-INF/" + CONFIG_DIR_NAME + "/";
    static public  String RESOURCES_RELATIVE_URI_PATH = "WEB-INF/" + RESOURCES_DIR_NAME + "/";

    public MyServletContextListener()
    {
    	FedoraAppConstants.setContextListener( this );
    }

	/* (non-Javadoc)
	 * @see edu.du.penrose.systems.util.MyServletContextListener_INF#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0) 
	{
		//
	}

	/* Set the system property 'webapp.root' = to the the real path of our web application and ensure that it ends with a trailing
	 * File.separatorChar (/ or \).
	 * 
	 * @see edu.du.penrose.systems.util.MyServletContextListener_INF#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent context ) {

		MyServletContextListener.myServletContext = context.getServletContext();	
		
		String realPath = myServletContext.getRealPath("/");
		if ( ! realPath.endsWith( ""+File.separatorChar ) ){
			realPath = realPath + File.separatorChar;
		}
		System.setProperty( "webapp.root", realPath );
	}

	/* (non-Javadoc)
	 * @see edu.du.penrose.systems.util.MyServletContextListener_INF#getInstituionURL()
	 */
	public URL getInstituionURL() throws RuntimeException{
		
		URL resourceURL;
	    try {        	
	    	String institutionDirectoryPath = ProgramProperties.getInstance( getProgramPropertiesURL() ).getProperty( FedoraAppConstants.BATCH_INGEST_TOP_FOLDER_URL_PROPERTY );
	    	
	    	if ( ! institutionDirectoryPath.endsWith( "/" ) ){
	    		institutionDirectoryPath = institutionDirectoryPath + '/';
	    	}
	    	resourceURL  = new URL( institutionDirectoryPath );
	    } catch (Exception e) {
	
	      throw new RuntimeException( e.getMessage() );
	    }
			
		return resourceURL;
	}

	/* (non-Javadoc)
	 * @see edu.du.penrose.systems.util.MyServletContextListener_INF#getTaskEnablePropertiesURL()
	 */
	public URL getTaskEnablePropertiesURL() throws RuntimeException{
		
		URL resourceURL;
	    try {
	    	resourceURL = new URL( "file:"+getAppRealPath()+CONFIG_RELATIVE_URI_PATH+FedoraAppConstants.TASK_ENABLE_PROPERTIES_FILE_NAME );
	    } 
	    catch (Exception e) 
	    {
	      throw new RuntimeException( e.getMessage() );
	    }
			
		return resourceURL;
	}

	/* (non-Javadoc)
	 * @see edu.du.penrose.systems.util.MyServletContextListener_INF#getProgramPropertiesURL()
	 */
	public URL getProgramPropertiesURL() throws RuntimeException{
		
		URL resourceURL;
	    try
	    {
	        resourceURL = new URL( "file:"+getAppRealPath()+CONFIG_RELATIVE_URI_PATH+FedoraAppConstants.PROPERTIES_FILE_NAME );
	    } 
	    catch (Exception e) 
	    {
	      throw new RuntimeException( e.getMessage() );
	    }
			
		return resourceURL;
	}

	/* (non-Javadoc)
	 * @see edu.du.penrose.systems.util.MyServletContextListener_INF#getAppRealPath()
	 */
	public String getAppRealPath() {
		
		String realPath = null;
		WebApplicationContext wac = null;
		if ( MyServletContextListener.webContextTestPath == null ) 
		{
			realPath = myServletContext.getRealPath("/");
		}
		else {
			realPath = MyServletContextListener.webContextTestPath; 
		}
		
		if ( ! realPath.endsWith( File.separator ) ){ 
			realPath = realPath + File.separator ;
		}
		return realPath;
	}

	/* (non-Javadoc)
	 * @see edu.du.penrose.systems.util.MyServletContextListener_INF#setContextTestPath(java.lang.String)
	 */
	public void setContextTestPath( String contextTestPath ) {
		
		MyServletContextListener.webContextTestPath = contextTestPath;
	}
	
	/* (non-Javadoc)
	 * @see edu.du.penrose.systems.util.MyServletContextListener_INF#getApplicationConfigPath()
	 */
	public String getApplicationConfigPath () {
	    
	    return get_WEB_INF_path() + CONFIG_DIR_NAME + File.separatorChar;

	}
	
	/* (non-Javadoc)
	 * @see edu.du.penrose.systems.util.MyServletContextListener_INF#getWebApplicatonName()
	 */
	public String getWebApplicatonName() {

		return myServletContext.getServletContextName();
	}
	
	/* (non-Javadoc)
	 * @see edu.du.penrose.systems.util.MyServletContextListener_INF#get_WEB_INF_path()
	 */
	public String get_WEB_INF_path() 
	{
		return getAppRealPath()+"WEB-INF"+File.separator; 
	}	
	


	
	
	/* (non-Javadoc)
	 * @see edu.du.penrose.systems.util.MyServletContextListener_INF#getVersionsFileURL()
	 */
	public URL getVersionsFileURL(){

        URL resourceURL;
        try {
            resourceURL = new URL( "file:///"+getAppRealPath()+RESOURCES_RELATIVE_URI_PATH+VERSIONS_FILE_NAME );
        } catch (MalformedURLException e) {

          throw new RuntimeException( e.getMessage() );
        }
            
        return resourceURL;
	}

	/**
	 *  Return the web application context we are running in.
	 * 
	 * @return WebApplicationContext
	 */
	private  WebApplicationContext getWebAppContext() {
		
		WebApplicationContext wac = null;
		
	    wac = WebApplicationContextUtils.getRequiredWebApplicationContext( MyServletContextListener.myServletContext );
		
		return null;	
	}

	
}
