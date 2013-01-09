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
package edu.du.penrose.systems.fedoraProxy.data.oai;

/**
 * Represents a single OAI set, containing the set name and it's uniqueID. For example
 * the "Carson Brierly Photograph Collection" with the feodra PID codu:38045
 * 
 * @author chet
 *
 */
public class SingleSet {

	private String name;
	private String uniqueID;	
	
	//  default constructor
	public SingleSet(){
		
	}
	
	/**
	 * Create a single set, containing the OAI name and unique id..
	 * 
	 * @param name the OAI set name ie "Carson Brierly Photograph Collection"
	 * @param uniqueID the OAI set uniqueID ie codu:38045
	 */
	public SingleSet( String name, String uniqueID )
	{
		this.name        = name;
		this.uniqueID    = uniqueID;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUniqueID() {
		return uniqueID;
	}
	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}
	
}
