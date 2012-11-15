package edu.du.penrose.systems.fedoraProxy.web.bus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * Serve up a a Fedora Datastream. Security to this level is provided by URL
 * mapping the applicationContext.xml file. We then get the collection pid based
 * on the web-site portion of the URL. This is then used to make sure that
 * Datastream's belong to objects within the collection. The collection PID is
 * mapped to the web site name in the websiteCollection.Properties file. <br>
 * Assumes fedora is at /fedora/
 * 
 * @author chet
 * 
 *         TEST WITH...
 *       
 *        http://localhost:7080/fedoraProxy/du/nation/datastream.get/codu:57084/DC
 * or
 *        http://digitaldu.coalliance.org/fedoraProxy/du/nation/datastream.get/codu:57084/DC
 *        http://digitaldu.coalliance.org/fedoraProxy/du/ectd/datastream.get/codu:60105/DC
 *        
 *   becomes..
 *         
 *         http://fedora.coalliance.org:8080/fedora/get/codu:57084/DC                NEW FEDORA
 *         
 *         
 *         http://localhost:8080/fedora/get/codu:113/MODS                          LOCAL Fedora
 *         
 * OR      http://adr.coalliance.org:9080/fedora/get/codu:37742/COLLECTION_POLICY    OLD FEDORA
 * 
 *         
 * 
 */
@Controller
@RequestMapping(value = "/{webSite}/datastream.get/{objectPID}/{dsID}")
public class FedoraDatastream_get extends ProxyController {

	static String FEDORA_DATASTREAM_GET_CMD = null;

	public FedoraDatastream_get() 
	{
		super();

		FEDORA_DATASTREAM_GET_CMD = "http://" + FEDORA_HOST + ":" + FEDORA_PORT + "/fedora/get/";
	}

    /**
     * @param webSite   (path variable)          used to retrieve the webSite collection pid from the webSiteCollection.properties file, if not found a response status of 404 is returned.
     * @param objectPID ('pid'request parameter) the object containing the datastream. The object must be contained with the webSite collection.
     * @param dsID      ('ds' request parameter) the datastream id.
     * <br>
	 * @param request the original http request object.
	 * @param response the http response sent back to the browser.
     * @throws Exception
     */
	@RequestMapping(method = RequestMethod.GET)
	public final void proxyCall(
		//	@RequestParam(required = true, value = "pid") String objectPID, @RequestParam(required = true, value = "ds") String dsID, 
			@PathVariable String webSite, @PathVariable String objectPID, @PathVariable String dsID, HttpServletRequest request,
			HttpServletResponse response) throws Exception // MUST BE type Exception for annotation to work!
	{ 	
		
		this.performProxyCall( webSite, objectPID, FEDORA_DATASTREAM_GET_CMD + objectPID + "/" + dsID, response );	
		
	}

}
