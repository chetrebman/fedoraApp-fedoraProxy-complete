/*
 * Copyright 2011 University of Denver
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

package edu.du.penrose.systems.fedoraApp.web.data;

public class SearchFedoraCmd {

	/**
	 * The fedora search query.
	 */
	private String query = "*";
	
	/**
	 * The fields being returned from fedora.
	 */
	private Object[] resultFields = null;
	
	/**
	 * Field search data results
	 */
	private Object[][] fsDataResults = null;

	public String getQuery() {
		return query;
	}

	/**
	 * Set the fedpra searcj query string.
	 * @param query
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	/*
	 * Get the field search data results.
	 */
	public Object[][] getFsDataResults() {
		return fsDataResults;
	}

	/**
	 * Set the field search data results.
	 * @param fsDataResults
	 */
	public void setFsDataResults(Object[][] fsDataResults) {
		this.fsDataResults = fsDataResults;
	}

	/**
	 * The fields being returned from fedora.
	 * @return array of field results.
	 */
	public Object[] getResultFields() {
		return resultFields;
	}

	/**
	 * Set result fields being returned from fedora.
	 * @param resultFields
	 */
	public void setResultFields(Object[] resultFields) {
		this.resultFields = resultFields;
	}
	
} // SearchFedoraCmd
