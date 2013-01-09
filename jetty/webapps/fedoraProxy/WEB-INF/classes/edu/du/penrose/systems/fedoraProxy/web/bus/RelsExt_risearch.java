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
 * Handles a select request to a solr instance.<br>
 * NOTE: Requires a valid batchIngest.properties file with FEDORA_HOST,
 * FEDORA_PORT, FEDORA_USER, FEDORA_PWD definded!!<br>
 * Assumes fedora is at /fedora/
 * 
 * @author chet
 * 
 *   TEST with
 *     http://lib-ram.cair.du.edu:7080/fedoraProxy/du/nation/risearch?type=tuples&lang=itql&format=CSV&limit=100&distinct=off&stream=off&query=select+%24object+from+%3C%23ri%3E+where+%28+%24object+%3Cfedora-rels-ext%3AisMemberOfCollection%3E%3Cinfo%3Afedora%2Fcodu%3A37742%3E+%29
 * 
 * 	 becomes...
 * 	   http://fedora.coalliance.org:8080/fedora/risearch?type=tuples&lang=itql&format=CSV&limit=100&distinct=off&stream=off&query=select+%24object+from+%3C%23ri%3E+where+%28+%24object+%3Cfedora-rels-ext%3AisMemberOfCollection%3E%3Cinfo%3Afedora%2Fcodu%3A37742%3E+%29
 * 
 *   which is...
 *     http://fedora.coalliance.org:8080/fedora/risearch?type=tuples&lang=itql&format=CSV&limit=100&distinct=off&stream=off&query=select $object from <#ri> where ( $object <fedora-rels-ext:isMemberOfCollection><info:fedora/codu:37742> )
 */
@Controller
@RequestMapping(value = "/{webSite}/risearch" )
public class RelsExt_risearch extends ProxyController {

	private static String RELEXT_OBJECT_CMD = null;

	public RelsExt_risearch() 
	{
		super();

		RELEXT_OBJECT_CMD = "http://" + FEDORA_HOST + ":" + FEDORA_PORT + "/fedora/risearch?";
	}

	/**
	 * @param webSite  (path variable) used to retrieve the webSite collection pid from the webSiteCollection.properties file, if not found a response status of 404 is returned.
	 * <br>
	 * @param request the original http request object.
	 * @param response the http response sent back to the browser.
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.GET)
	public final void proxyCall(
			@PathVariable String webSite, HttpServletRequest request,
			HttpServletResponse response) throws Exception // MUST BE type Exception for annotation to work!
	{ 
		
		this.performProxyCall( webSite, null, RELEXT_OBJECT_CMD + request.getQueryString(), response );
		
	} // proxyCall

} // RelsExt_risearch
