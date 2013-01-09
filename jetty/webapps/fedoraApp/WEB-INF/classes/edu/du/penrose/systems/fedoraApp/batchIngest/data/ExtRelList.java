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

package edu.du.penrose.systems.fedoraApp.batchIngest.data;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Contains a list of Ext-Rel defintions.
 * 
 * @author chet
 * @see ExtRelDefinitions
 */
public class ExtRelList 
{
    static public final String ExtRel_PREDICATE    = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
    static public final String ExtRel_OBJECT       = "http://www.w3.org/1999/02/22-rdf-syntax-ns#description";      

    static public final String ExtRel_COLLECTION_PREDICATE    = "info:fedora/fedora-system:def/relations-external#isMemberOfCollection";
    static public final String ExtRel_COLLECTION_OBJECT       = "info:fedora/"; // this will have the correct Fedora PID appended
    
    static public final String ExtRel_CONTENT_MODEL_PREDICATE = "info:fedora/fedora-system:def/model#hasModel";
    static public final String ExtRel_CONTENT_MODEL_OBJECT    = "info:fedora/"; // this will have the correct Fedora PID appended
    
    static public final String ExtRel_FEDORA_SYSTEM_PREDICATE = "info:fedora/fedora-system:FedoraObject-3.0";
    static public final String ExtRel_FEDORA_SYSTEM_OBJECT    = "info:fedora/fedora-system:def/model#hasModel";
    
	private Set<ExtRelDefinitions> myExtRelList = new HashSet<ExtRelDefinitions>();
	
	public Iterator<ExtRelDefinitions> getIterator(){
		
		return this.myExtRelList.iterator();
	}

    /**
     * Add subject has relationship(predicate) to object, If it does not already exist.
     * 
     * @param subject PID, can be null.
     * @param predicate This is the relationship, it must be defined or the relationship is NOT added.
     * @param object  PID  This is the object the relation ship is too, it must be defined  or the relationship is NOT added.
     */
	public void addRelationship( String subject, String predicate, String object )
	{
		if ( predicate == null || object == null )
		{
			return;
		}
		
		try 
		{
			Iterator<ExtRelDefinitions> listIterator = getMyExtRelList().iterator();
			while ( listIterator.hasNext() )
			{
				ExtRelDefinitions extDef = listIterator.next();
				if ( extDef.contains( subject, predicate, object) ){
					return;
				}
			}
			
			this.myExtRelList.add( new ExtRelDefinitions( subject, predicate, object ) );
		} 
		catch (Exception e) 
		{
			System.out.println( "Exception: "+e ); // tbd need to log
		}
		
	}
	
	private Set<ExtRelDefinitions> getMyExtRelList()
	{
		return this.myExtRelList;
	}
	
	public boolean isEmpty(){
		
		return this.myExtRelList.isEmpty();
	}
	

	/**
	 * Returns true of this External Relationship list contains the relationshipEntity
	 * as EITHER an object or a predicate.
	 * 
	 * @param relationshipEntity
	 * @return true if there is relationship entity for this object.
	 */
	public boolean isHasRelationshipEntity( String relationshipEntity )
	{
		Iterator<ExtRelDefinitions> listIterator = this.getIterator();
		
		while ( listIterator.hasNext() )
		{
			ExtRelDefinitions extDef = listIterator.next();
		    if ( extDef.isHasRelationshipEntity(relationshipEntity)){ return true; }
		}
		
		return false;
	}

	public void addIslandoraRelationship( String collectionPID, String  contentModelPID )
	{	
    	this.addRelationship( null, ExtRelList.ExtRel_COLLECTION_PREDICATE,    ExtRelList.ExtRel_COLLECTION_OBJECT+collectionPID );
    	this.addRelationship( null, ExtRelList.ExtRel_CONTENT_MODEL_PREDICATE, ExtRelList.ExtRel_CONTENT_MODEL_OBJECT+contentModelPID );
    	
    	//this.addRelationship( null, ExtRelList.ExtRel_PREDICATE,               ExtRelList.ExtRel_OBJECT );
    	//this.addRelationship( null, ExtRelList.ExtRel_FEDORA_SYSTEM_PREDICATE, ExtRelList.ExtRel_FEDORA_SYSTEM_OBJECT );
	}
	
} // ExtRelList
