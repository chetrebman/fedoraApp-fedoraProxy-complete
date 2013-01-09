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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Defines aggregate set, with unique name and id, that contains 0..N SingleSet's each of which defines the name and unique id of an OAI set.
 * 
 * @author chet
 * @see SingleSet
 */
public class AggregateSet {

	private Set<SingleSet> setOfOaiSets = new HashSet<SingleSet>();
	
	private String setName = "ERROR: not set";

	private String uniqueID = "ERROR: id not set" ;
	
	private AggregateSet()
	{
		// disable default constructor
	}
	
	/**
	 * Create an aggregate set containing it's name and ID along with the SingleSets that are part of the aggregate set.
	 * 
	 * @param setName ie "Hanya Holm - aggregate collection"
	 * @param uniqueID ie oai:feodoraProxy.cair.du.edu:fedoraProxyAgg:1
	 */
	public AggregateSet( String setName, String uniqueID )
	{
		this.setName  = setName;
		this.uniqueID = uniqueID;
	}
	
	public void addSet( SingleSet singleSet )
	{
		this.setOfOaiSets.add( singleSet );
	}
	
	public Iterator<SingleSet> getIterator()
	{
		return this.setOfOaiSets.iterator();
	}

	public String getSetName() {
		return setName;
	}


	public String getUniqueID() {
		return uniqueID;
	}


} // AggregateSet
