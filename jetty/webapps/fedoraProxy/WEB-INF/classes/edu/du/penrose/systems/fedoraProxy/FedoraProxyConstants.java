package edu.du.penrose.systems.fedoraProxy;

import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraProxy.util.FedoraProxyServletContextListener_INF;
import edu.du.penrose.systems.util.MyServletContextListener_INF;

public class FedoraProxyConstants 
{
	public static final String FedoraProxy_FEDORA_HOST_PROPERTY = "fedoraProxy_FEDORA_HOST";
	public static final String FedoraProxy_FEDORA_PORT_PROPERTY = "fedoraProxy_FEDORA_PORT";
	
    /*
     * This is the Fedora user used by the fedoraProxy application, it may be the same user used by fedoraApp
     */
	public static final String FedoraProxy_FEDORA_USER_PROPERTY  = "fedoraProxy_FEDORA_USER";
	public static final String FedoraProxy_FEDORA_PWD_PROPERTY   = "fedoraProxy_FEDORA_PWD";
	
	static public final String PROPERTIES_FILE_NAME = "fedoraProxy.properties";
	
	public static final String SOLR_HOST_PROPERTY = "SOLR_HOST";
	public static final String SOLR_PORT_PROPERTY = "SOLR_PORT";

	public static final String ECTD_PDF_FORM_PART_NAME       = "ectdPdf";
	public static final String ECTD_BATCH_XML_FORM_PART_NAME = FedoraAppConstants.BATCH_FILE_IDENTIFIER;

	public static final String fedoraProxy_OAI_SET_SPEC = "oai:feodoraProxy.cair.du.edu:";  //  "oai:adr.coalliance.org:" for sets from alliance

	public static final String ADR_OAI_SET_SPEC = "oai:adr.coalliance.org:";
	
	/* 
	 * Oai was used to aggregate a set of collections for a DU exhibit. It is no longer used. The code worked at the time so it was left
	 * in feodraProxy. Every thing is hard coded and not it property files. If you wish to use it see the OAI_HOST and OAI_PORT in OaiAggregator
	 * and the sets to be aggregated in the AggregateList.java class
	 */
	
	public static final String FedoraProxyOAI_URL = "http://localhost/fedoraProxy/oai.du";
	public static final String WEBSITE_COLLECTION_FILE = "webSiteCollection.properties";
	
	//FYI old aoi was http://adr.coalliance.org/codu/fez/oai.php 

	private static FedoraProxyServletContextListener_INF myServletContextListener = null;
	
	public static void setContextListener(
			FedoraProxyServletContextListener_INF aServletContextListener) 
	{
		myServletContextListener = aServletContextListener;
	}
	
	public static FedoraProxyServletContextListener_INF getServletContextListener()
	{
		return myServletContextListener;
	}
	
}
