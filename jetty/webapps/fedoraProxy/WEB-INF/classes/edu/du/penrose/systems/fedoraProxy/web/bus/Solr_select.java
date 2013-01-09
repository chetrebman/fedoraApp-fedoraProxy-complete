/*
 * Copyright 2012 University of Denver
 * Author Chet Rebman
 * 
 * This file is part of FedoraProxy.
 * 
 * FedoraProxy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FedoraProxy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with FedoraProxy.  If not, see <http://www.gnu.org/licenses/>.
*/
package edu.du.penrose.systems.fedoraProxy.web.bus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles a select request to a solr instance. <br>
 * NOTE: Requires a valid batchIngest.properties file with SOLR_HOST, SOLR_PORT,
 * SOLR_USER, SOLR_PWD definded!! Assumes solr is at /solr/
 * 
 * @author chet
 * 
 *         TEST with
 *         http://lib-ram.cair.du.edu:7080/fedoraProxy/du/nation/solr/select?q=dc.subject:maps 
 *         OR
 *         http://digitaldu.coalliance.org/fedoraProxy/du/nation/solr/select?q=dc.subject:maps 
 *         
 *            becomes  http://solr.coalliance.org:8080/solr/select?q=dc.subject:maps
 */
@Controller
@RequestMapping(value = "/{webSite}/solr/select")
public class Solr_select extends ProxyController {

	private static String SOLR_QUERY_CMD = null;

	public Solr_select() 
	{
		super();

		SOLR_QUERY_CMD = "http://" + SOLR_HOST + ":" + SOLR_PORT
				+ "/solr/select?";
	}

	/** 
	 * @param webSite     (path variable)         used to retrieve the webSite collection pid from the webSiteCollection.properties file, if not found a response status of 404 is returned.
	 * @param queryString ('q' request parameter) the query string passed to solr, should be escaped.
	 * <br>
	 * @param request the original http request object.
	 * @param response the http response sent back to the browser.
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.GET)
	public final void proxyCall(
			@RequestParam(required = true, value = "q") String queryString, 
			@PathVariable String webSite, HttpServletRequest request,
			HttpServletResponse response) throws Exception  // MUST BE type Exception for annotation to work!
	{ 
	
		this.logger.info( "Request recived:" + request.getQueryString() );
		
		this.performProxyCall( webSite, null, SOLR_QUERY_CMD+ request.getQueryString(), response );
				
	} // proxyCall

} // Solr_select
