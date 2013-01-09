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

import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraApp.ProgramProperties;
import edu.du.penrose.systems.util.MyServletContextListener;

public class LoginFedoraCmd {

    private String host     = null;
    private String port     = null;
    
	private String protocol = "http";
	
	private String userName = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.FEDORA_USER_PROPERTY );	
//	private String pwd      = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.FEDORA_PWD_PROPERTY );
	private String pwd      = "force login from web form";
	
	LoginFedoraCmd()
	{
		host = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.FEDORA_HOST_PROPERTY );
		port = ProgramProperties.getInstance( FedoraAppConstants.getServletContextListener().getProgramPropertiesURL() ).getProperty( FedoraAppConstants.FEDORA_PORT_PROPERTY );
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getUsername() {
		return userName;
	}
	public void setUsername(String username) {
		this.userName = username;
	}
	public String getPassword() {
		return pwd;
	}
	public void setPassword(String password) {
		this.pwd = password;
	}
    /**
     * @return the port
     */
    public String getPort() {
        return port;
    }
    /**
     * @param port the port to set
     */
    public void setPort(String port) {
        this.port = port;
    }
    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }
    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }
    
    public int getPortAsInt() {
        ;
        return Integer.parseInt( port );
    }
	
} // LoginFedoraCmd
