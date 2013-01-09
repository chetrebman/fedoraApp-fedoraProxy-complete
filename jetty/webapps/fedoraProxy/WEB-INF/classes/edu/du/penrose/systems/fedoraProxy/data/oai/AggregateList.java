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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.du.penrose.systems.fedoraProxy.FedoraProxyConstants;

/**
 * Contains multiple aggregates set's all mapped to their aggregate set names, ie oai:feodoraProxy.cair.du.edu:fedoraProxyAgg:1 is 
 * the name for an aggregate set that contains multiple OAI set's.
 * 
 * @author chet
 *
 */
public class AggregateList {

	Map<String, AggregateSet> aggregateMap = null;
	
	public AggregateList()
	{
		this.loadHanyaHolmsMap();
	}

	/**
	 * This is a set that was use by DU to create an aggregate set for an online exhibit
	 */
	private void loadHanyaHolmsMap()
	{
		if ( this.aggregateMap == null )
		{
			this.aggregateMap = new HashMap<String, AggregateSet>();
		}
		
		AggregateSet aggregateSet = new AggregateSet( "Hanya Holm - aggregate collection", FedoraProxyConstants.fedoraProxy_OAI_SET_SPEC+"fedoraProxyAgg:1" );	
		
		// temp hard code TBD

		aggregateSet.addSet( new SingleSet( "Carson Brierly Photograph Collection", "codu:38045" ));	

		aggregateSet.addSet( new SingleSet( "Central City Opera House Association Records", "codu:38046" ));	

		aggregateSet.addSet( new SingleSet( "Marshall and Carolyn Durand Brooks Photography and Dance Collection", "codu:37065" ));	

		aggregateSet.addSet( new SingleSet( "Maxine Munt Scrapbook", "codu:54120" ));	

		aggregateSet.addSet( new SingleSet( "University of Denver Historical Photograph Collection", "codu:32562" ));	
		
		aggregateSet.addSet( new SingleSet( "Vera Sears Papers", "codu:48565" ));	
		
	    aggregateSet.addSet( new SingleSet( "DU Historical Photograph Collection", "codu:37228" ));	
		
		this.aggregateMap.put( aggregateSet.getUniqueID(), aggregateSet );
	}

	public Set<AggregateSet> getAggregateSetList() 
	{			
		return new HashSet<AggregateSet>( getMyAggregateMap().values() );
	}
	
	private Map<String, AggregateSet> getMyAggregateMap()
	{
		return this.aggregateMap;
	}
	
	public Iterator<AggregateSet> getIterator()
	{
		return this.getMyAggregateMap().values().iterator();
	}
	
	/**
	 * 
	 * @param aggregateSetName  oai:feodoraProxy.cair.du.edu:fedoraProxyAgg:1
	 * @return
	 */
	public AggregateSet getAggregateSet( String aggregateSetName )
	{
		return this.getMyAggregateMap().get( aggregateSetName );
	}

} // class AggregateList
