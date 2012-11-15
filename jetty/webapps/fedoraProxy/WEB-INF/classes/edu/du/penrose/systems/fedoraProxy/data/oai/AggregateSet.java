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
