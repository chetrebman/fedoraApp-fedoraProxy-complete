package edu.du.penrose.systems.util;

import java.net.URL;

import javax.servlet.ServletContextEvent;

import edu.du.penrose.systems.exceptions.FatalException;

public interface MyServletContextListener_INF {

	public abstract void contextDestroyed(ServletContextEvent arg0);

	/**
	 * Set the servlet context that this POJO is running in, when first 
	 * this obj. is created.
	 * 
	 * @see org.springframework.web.context.ServletContextAware
	 */
	public abstract void contextInitialized(ServletContextEvent context);

	/**
	 * Get the URL of the TOP directory that contains the institution directories in a batch_space directory.
	 * 
	 * @return URL of the institution directory 
	 * @throws RuntimeException on any error
	 */
	public abstract URL getInstituionURL() throws RuntimeException;

	/**
	 * Get the Task enable properties resource.
	 * 
	 * @return URL of the taskEnable.properties resource.
	 * @throws RuntimeException on any error
	 */
	public abstract URL getTaskEnablePropertiesURL() throws RuntimeException;

	/**
	 * Get the MAIN application properties resource.
	 * 
	 * @return URL of the program properties resource.
	 * @throws RuntimeException on any error
	 */
	public abstract URL getProgramPropertiesURL() throws RuntimeException;

	/**
	 * Get the applications real path '\'. on the local file system.
	 * @return string containing the applications root '\' path.
	 */
	public abstract String getAppRealPath();

	/**
	 * Used for setting a context path for TESTING this method should not be used outside of testing.
	 * 
	 * @deprecated
	 * @param contextTestPath
	 */
	public abstract void setContextTestPath(String contextTestPath);

	/**
	 * Get the path to the directory containing the applications configuration files.
	 * 
	 * @return String config dir path
	 */
	public abstract String getApplicationConfigPath();

	/**
	 * Return the app NAME set in web.xml, or as mapped in jetty.xml
	 * @return web application name, or null on error.
	 */
	public abstract String getWebApplicatonName();

	/**
	 * Get the real path for the \WEB-INF\ directory in the current web context.
	 * 
	 * @return the real path including the \WEB-INF\ directory
	 */
	public abstract String get_WEB_INF_path();

	/**
	 * Get a file: URL to the application versions file.
	 * @return URL to versions file.
	 */
	public abstract URL getVersionsFileURL();

}