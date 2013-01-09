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

/**
 * This class describes an external relationship to a subject (fedora object) and is saved in the Ext-Rel datastream of the object.
 * <br>
 * Note: subject has relationship(predicate) to object. See RDF documentation
 * 
 * @author chet
 * @see "http://fedora-commons.org/download/2.1.1/userdocs/digitalobjects/introRelsExt.html"
 *
 */
public class ExtRelDefinitions 
{
    String subject   = null;
    String predicate = null;
    String object    = null;
    
    /**
     * subject has relationship(predicate) to object.
     * 
     * @param subject PID, can be null 
     * @param predicate This is the relationship, it must be defined.
     * @param object PID This the object the relation ship is too, it must be defined.
     * @throws Exception if predicate or object is null.
     */
    ExtRelDefinitions( String subject, String predicate, String object ) throws Exception
    {
		if ( predicate == null || object == null )
		{
			throw new Exception( "both predicate and object must be defined" );
		}
		
    	this.subject   = subject;
    	this.predicate = predicate;
    	this.object    = object;
    }
    
	public String getSubject() {
		return subject;
	}
	public String getPredicate() {
		return predicate;
	}
	public String getObject() {
		return object;
	}

	/**
	 * Returns true of this External Relationship list contains the relationshipEntity
	 * as EITHER an object or a predicate.
	 * 
	 * @param relationshipEntity
	 * @return
	 */
	boolean isHasRelationshipEntity( String relationshipEntity )
	{
		if ( this.getSubject()  != null && this.getSubject().equals( relationshipEntity ))  { return true; }
		if ( this.getPredicate()!= null && this.getPredicate().equals( relationshipEntity )){ return true; }
		if ( this.getObject()   != null && this.getObject().equals( relationshipEntity ))   { return true; }
		
		return false;
	}
	
	/**
	 * If testObject is of type ExtRelDefinitions and contains same subject, predicate, object
	 * return true;
	 * 
	 * @param testObject
	 * @return true if object contain same subject, predicate, object
	 */
	@Override public boolean equals( Object testObject )
	{
		ExtRelDefinitions e;
		if ( testObject instanceof ExtRelDefinitions )
		{
		    e = (ExtRelDefinitions) testObject;
		}
		else {
			return false;
		}

		if ( subject == null && this.getSubject() != null )  { return false; }
		if ( ! e.getSubject().equalsIgnoreCase( this.getSubject() )){ return false; }
		
		if (!  e.getPredicate().equalsIgnoreCase( this.getPredicate() )){ return false; }
		
		if ( ! e.getObject().equalsIgnoreCase( this.getObject() )){ return false; }
		
		return true;
	}
	
	boolean contains( String subject, String predicate, String object )
	{
		if ( subject == null && this.getSubject() != null )  { return false; }
		if ( subject != null )	{
			if ( ! subject.equalsIgnoreCase( this.getSubject() )){ return false; }
		}
		
		if ( predicate == null || this.getPredicate() == null ){ return false; }
		if ( predicate != null ){
			if ( ! predicate.equalsIgnoreCase( this.getPredicate() )){ return false; }
		}
		
		if ( object == null || this.getObject() == null ){ return false; }
		if ( object != null ){
			if ( ! object.equalsIgnoreCase( this.getObject() )){ return false; }
		}
		
		return true;
	}
	
}
