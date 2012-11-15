package edu.du.penrose.systems.fedoraProxy.util;

import java.net.URL;

import edu.du.penrose.systems.util.MyServletContextListener_INF;

public interface FedoraProxyServletContextListener_INF extends MyServletContextListener_INF
{
	
	/**
	 * Get the fedoraProxy application properties resource.
	 * 
	 * @return URL of the program properties resource.
	 * @throws RuntimeException on any error
	 */
	public abstract URL getFedoraProxyProgramPropertiesURL() throws RuntimeException;
	
}