/*
 * Copyright 2012 University of Denver
 * Author Chet Rebman
 * 
 * This file is part of FedoraProxy.
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