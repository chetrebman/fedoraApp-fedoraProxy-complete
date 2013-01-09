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

/**
 * Utilities that query the Fedora Resource Index. In fedoraApp, these are mainly used to 
 * discover Islandora collections and Content Models.
 */

package edu.du.penrose.systems.fedora;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;

import edu.du.penrose.systems.exceptions.FatalException;
import edu.du.penrose.systems.fedora.client.Administrator;
import edu.du.penrose.systems.fedora.client.Downloader;
import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraApp.batchIngest.bus.FedoraAppBatchIngestController;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestOptions;
import edu.du.penrose.systems.fedoraApp.util.FedoraAppUtil;

public class ResourceIndexUtils 
{	

	public static final String QUOTE = "\"";
	public static final String APOST = "\'";
	
	public static final String COLLECTION_POLICY_DATASTREAM = "COLLECTION_POLICY";
	public static final String ISLANDORA_TOP_COLLECTION     = "islandora:top";  // changed to islandora:root in later version 
	
	/**
	 * Gets all NON-Islandora collections beneath the islandora:top level collection.
	 * <br>
	 * NOTE all islandora:* namespace collections are excluded.	 
	 *  
	 * @see FedoraAppBatchIngestController#getAdministrator()
	 * @param administrator The administrator is just used to get host,port,user,pwd info.
	 * @return all top level islandora collections
	 */
	public static ArrayList getAllIslandoraChildCollections( Administrator administrator )
	{
		
		return ResourceIndexUtils.getChildCollections( administrator, ISLANDORA_TOP_COLLECTION );
	}

	/**
	 * Get a list of all objects in the topCollection that are also collections
	 *  
	 * @see FedoraAppBatchIngestController#getAdministrator()
	 * @param administrator The administrator is just used to get host,port,user,password info.
	 * @param topCollection
	 * 
	 * @return all objects in the topCollection that are also collections
	 */
	public static ArrayList getChildCollections( Administrator administrator, String topCollection )
	{
		return getChildren( administrator, topCollection, true );
	}

	/**
	 * 
	 * Get a list of all objects in the topCollection.
	 * 
	 * 
	 * @see FedoraAppBatchIngestController#getAdministrator()
	 * @param administrator The administrator is just used to get host,port,user,password info.
	 * @param topCollection
	 * 
	 * @return all objects in the topCollection that are also collections
	 */
	public static ArrayList<String> getChildObjects( Administrator administrator, String topCollection )
	{
		return getChildren( administrator, topCollection, false );
	}
	
	/**
	 * 
	 * Get a list of all objects in the topCollection. The administrator is just used to get host,port,user, and pwd info.
	 * The call to the resource index is actually via httpClient and rest call. 
	 * 
	 * @see FedoraAppBatchIngestController#getAdministrator()
	 * @param administrator  The administrator is just used to get host,port,user,password info.
	 * @param topCollection
	 * @param onlyCollections =true to return only collection objects
	 * 
	 * @return all object pids in the topCollection that are also collections
	 */
	private static ArrayList<String> getChildren( Administrator administrator, String topCollection, boolean onlyCollections )
	{
		final String host = administrator.getHost();
		final int port    = administrator.getPort();
		String userName   = administrator.getUserName();
		String pwd        = administrator.getUserPassword();
		String realm      = null; // "realm is actually Fedora or Fedora Repository Server"
		
		ArrayList<String> children = new ArrayList<String>();

		String UTF_8_ENCONDING="UTF-8";

		String type     ="tuples";
		String flush    = "false"; // optional default false;
		String lang     = "itql";
		String format   = "CSV";
		String limit    = "100";   // optional default unlimited; NOT USED
		String distinct = "off"; // optional default off;
		String stream   = "off"; // optional default off;
		String query    = "select $subject from <#ri> where ( $subject <fedora-rels-ext:isMemberOfCollection><info:fedora/"+topCollection+"> )";
			// TBD kludge, I don't have time to do things right.
		String query_2    = "select $subject from <#ri> where ( $subject <fedora-rels-ext:isMemberOf><info:fedora/"+topCollection+"> )";

		String queryEncoded;
		String queryEncoded_2;
		HttpClient myClient = null;
		HttpMethod method   = null;
		HttpMethod method_2 = null;
		try {
			queryEncoded   = URLEncoder.encode( query, UTF_8_ENCONDING);
			queryEncoded_2 = URLEncoder.encode( query_2, UTF_8_ENCONDING);

			myClient = new HttpClient();
			String finalQuery = "http://"+host+":"+port+"/fedora/risearch?type="+type+"&lang="+lang+"&format="+format+"&distinct="+distinct+"&stream="+stream+"&query="+queryEncoded;
			String finalQuery_2 = "http://"+host+":"+port+"/fedora/risearch?type="+type+"&lang="+lang+"&format="+format+"&distinct="+distinct+"&stream="+stream+"&query="+queryEncoded_2;
			method   = new GetMethod( finalQuery );
			method_2 = new GetMethod( finalQuery_2 );
		
			myClient.getState().setCredentials(
					new AuthScope( host, port, realm ),
					new UsernamePasswordCredentials( userName, pwd )
			); 
			method.setDoAuthentication( true ); 
			myClient.getParams().setAuthenticationPreemptive(true);
			
			myClient.executeMethod(method);

			String response = method.getResponseBodyAsString();
			String lines[]  = response.split( "\n" );

			for ( int i=0; i < lines.length; i++ )
			{
				if ( ! lines[i].contains( "islandora" ) && ! lines[i].equalsIgnoreCase( "\"subject\"" ) )
				{
					String collectionPid = lines[i].replace( "info:fedora/", "" );
					if ( onlyCollections ) {
						if ( isCollection( administrator, collectionPid ) )
						{
							children.add( collectionPid );
						}
					}
					else {
						children.add( collectionPid );
					}
				}
			}
			
			
			myClient.executeMethod(method_2);

			String response_2 = method_2.getResponseBodyAsString();
			String lines_2[]  = response_2.split( "\n" );

			for ( int i=0; i < lines_2.length; i++ )
			{
				if ( ! lines_2[i].contains( "islandora" ) && ! lines_2[i].equalsIgnoreCase( "\"subject\"" ) )
				{
					String collectionPid = lines_2[i].replace( "info:fedora/", "" );
					if ( onlyCollections ) {
						if ( isCollection( administrator, collectionPid ) )
						{
							children.add( collectionPid );
						}
					}
					else {
						children.add( collectionPid );
					}
				}
			}
			
		} catch ( Exception e ) 
		{
			children.add( "Unable to get Collections:"+e );
			return children;
		} 
		finally {
			method.releaseConnection();
			method_2.releaseConnection();
		}
		
		return children;
	}

	/**
	 * Gets all islandora collection names and their titles beneath the islandora:top level collection in the namespace 
	 * <br> "" (empty string)
	 * will get all. The administrator is just used to get host,port,user,pwd info.
	 * The call to the resource index is actually via httpClient and rest call.
	 * <br>
	 * NOTE all islandora:* namespace collections are excluded.
	 * 
	 * @see FedoraAppBatchIngestController#getAdministrator()
	 * @param administrator The administrator is just used to get host,port,user,password info.
	 * @param namespace
	 * @return map of collection names and  titles.
	 */
	public static Map<String,String> getAllIslandoraChildCollectionsMap( Administrator administrator, String namespace)
	{	
		return ResourceIndexUtils.getChildCollectionsMap( administrator, ISLANDORA_TOP_COLLECTION, namespace );
	}
	
	/** USED 2-2-12
	 * 
	 * The administrator is just used to get host,port,user,pwd info.
	 * The call to the resource index is actually via httpClient and rest call.
	 * 
	 * @param topCollection such as islandora:top or codu:top
	 * @param namespace the names space of collections below the topCollection. <br>
	 * <br>
	 * For example, to retrieve the collection names for coduDuMaps and coduDuPicture...<br> <br>
	 * 	islandora:top - use topCollection=islandora:top and namespace=codu <br>
	 *  &nbsp;&nbsp&nbsp;codu:duMaps <br>
	 *  &nbsp;&nbsp&nbsp;codu:duPictures <br>
	 *  &nbsp;&nbsp&nbsp;xxxx:yyyy <br>
	 *  &nbsp;&nbsp&nbsp;zzzz:wwww <br><br>
	 *      OR <br><br>
	 *  islandora:top <br>
	 *  &nbsp;&nbsp&nbsp;codu:top - use topCollection=codu:top and namespace=codu <br>
	 *  &nbsp;&nbsp&nbsp;&nbsp;&nbsp&nbsp;codu:duMaps <br>
	 *  &nbsp;&nbsp&nbsp;&nbsp;&nbsp&nbsp;codu:duPictures  <br> 
	 *  
	 *  @param administrator  The administrator is just used to get host,port,user,password info.
	 *  @param topCollection
	 *  @param nameSpace
	 *  
	 * @return map of collection names and  titles.
	 */
	public static Map<String,String> getChildCollectionsMap(  Administrator administrator, String topCollection, String namespace )
	{
		final String host = administrator.getHost();
		final int port    = administrator.getPort();
		String userName   = administrator.getUserName();
		String pwd        = administrator.getUserPassword();
		String realm      = null; // "realm is actually Fedora or Fedora Repository Server"
				
		Map<String, String> collectionMap = new LinkedHashMap<String,String>();

		String UTF_8_ENCONDING="UTF-8";

		String type   ="tuples";
		String flush  = "false"; // optional default false;
		String lang   = "itql";
		String format = "CSV";
		String limit  = "100";   // optional default unlimited;
		String distinct = "off"; // optional default off;
		String stream   = "off"; // optional default off;
		String query    = "select $subject $title from <#ri> where ( $subject <fedora-model:label> $title and $subject <fedora-rels-ext:isMemberOfCollection><info:fedora/"+topCollection+"> ) order by $title";
			// tbd quick kludge
		String query_2    = "select $subject $title from <#ri> where ( $subject <fedora-model:label> $title and $subject <fedora-rels-ext:isMemberOf><info:fedora/"+topCollection+"> ) order by $title";

		String queryEncoded;
		String queryEncoded_2;
		HttpMethod method   = null;
		HttpMethod method_2 = null;
		try {
			queryEncoded   = URLEncoder.encode( query, UTF_8_ENCONDING);
			queryEncoded_2 = URLEncoder.encode( query_2, UTF_8_ENCONDING);

			HttpClient myClient = new HttpClient();
			method   = new GetMethod("http://"+host+":"+port+"/fedora/risearch?type="+type+"&lang="+lang+"&format="+format+"&limit="+limit+"&distinct="+distinct+"&stream="+stream+"&query="+queryEncoded);

			method_2   = new GetMethod("http://"+host+":"+port+"/fedora/risearch?type="+type+"&lang="+lang+"&format="+format+"&limit="+limit+"&distinct="+distinct+"&stream="+stream+"&query="+queryEncoded_2);

			myClient.getState().setCredentials(
					new AuthScope( host, port, realm ),
					new UsernamePasswordCredentials( userName, pwd )
			); 
			method.setDoAuthentication( true ); 
			myClient.getParams().setAuthenticationPreemptive(true);
			
			myClient.executeMethod(method); 

			String response = method.getResponseBodyAsString();
			String lines[]  = response.split( "\n" );

			for ( int i=0; i < lines.length; i++ )
			{
				if ( ! lines[i].contains( "islandora" ) && ! lines[i].equalsIgnoreCase( "\"subject\",\"title\"" ) ){
					
					String singleLine = lines[i].replace( "info:fedora/", "" );
					if ( ! singleLine.startsWith( namespace ) ){ 
						continue;
					}
					String[] pieces = singleLine.split( "," );
					String collectionName  = pieces[0];
					String collectionTitle = pieces[1];
					
					if ( isCollection( administrator, collectionName) ) // collectionName is the pid
					{
						collectionMap.put( collectionName, collectionTitle );
					}
				}
			}
			

			myClient.executeMethod(method_2); 

			String response_2 = method.getResponseBodyAsString();
			String lines_2[]  = response_2.split( "\n" );

			for ( int i=0; i < lines_2.length; i++ )
			{
				if ( ! lines_2[i].contains( "islandora" ) && ! lines_2[i].equalsIgnoreCase( "\"subject\",\"title\"" ) ){
					
					String singleLine = lines_2[i].replace( "info:fedora/", "" );
					if ( ! singleLine.startsWith( namespace ) ){ 
						continue;
					}
					String[] pieces = singleLine.split( "," );
					String collectionName  = pieces[0];
					String collectionTitle = pieces[1];
					
					if ( isCollection( administrator, collectionName) ) // collectionName is the pid
					{
						collectionMap.put( collectionName, collectionTitle );
					}
				}
			}
		} catch ( Exception e ) 
		{
			collectionMap.put( "", "Unable to get Collections:"+e );
			return collectionMap;
		} 
		finally {
			method.releaseConnection();
			method_2.releaseConnection();
		}

		return collectionMap;
	}
	
	
	
	/** USED 2-2-12
	 * 
	 * Get all content models for a collection. If this object is not a collection, an empty map is returned.
	 * We return pid:pid (key and value both = pid) because we want to use the pid for the label in the form rather then the actual label.
	 * 
	 * The administrator is just used to get host,port,user,pwd info.
	 * The call to the resource index is actually via httpClient and rest call.
	 * 
	 * @see FedoraAppBatchIngestController#getAdministrator()
	 * @param administrator The administrator is just used to get host,port,user,password info.
	 * @param collectionPID
	 * @return map of pid and pid for all content models in the collection (see above).
	 */
	public static Map<String,String>  getAllIslandoraCollectionContentModelsMap( Administrator administrator, String collectionPID )
	{
		Map<String, String> contentModelMap = new HashMap<String,String>();
		
		String dsID = "COLLECTION_POLICY";

		try
		{
			Downloader dl = new Downloader( administrator, administrator.getHost(), administrator.getPort(), administrator.getUserName(), administrator.getUserPassword() );
			
			InputStream is = dl.getDatastreamContent( collectionPID, dsID, null );
			BufferedReader reader = new BufferedReader( new InputStreamReader(is, "UTF-8"));
			
			String oneLine = reader.readLine();
			while ( oneLine != null ) 
			{
				if ( oneLine.contains( "content_model ") )
				{
					int startIndex = oneLine.indexOf( "pid" );
					
					int  endIndex                     = oneLine.indexOf( QUOTE, startIndex+5 );
					if ( endIndex == -1 ){ endIndex   = oneLine.indexOf( APOST, startIndex+5 ); }
					
					String pid ="";
					String name = "";
					
					if ( startIndex > -1 &&  endIndex > -1 )
					{
						pid = oneLine.substring( startIndex+5, endIndex );
					}
					else {
						continue;
					}
					startIndex = oneLine.indexOf( "name" );
					
				         endIndex                   = oneLine.indexOf( QUOTE, startIndex+6 );
					if ( endIndex == -1 ){ endIndex = oneLine.indexOf( APOST, startIndex+6 ); }
					
					if ( startIndex > -1 &&  endIndex > -1 )
					{
						name = oneLine.substring( startIndex+6, endIndex );
					}
					else {
						continue;
					}
					
					if ( name.length() > 0 && pid.length() > 0 ){
						contentModelMap.put( pid, pid );
					}
				}
				 oneLine = reader.readLine();
			}
		}
		catch( Exception e )
		{
			// TBD need to log
			System.out.println( "Unable to get content model for "+collectionPID+" - "+e.getMessage() );
		}
		
		return contentModelMap;
	}
	
    /** USED 2-2-12
     * 
     * Try to access the COLLECTION_POLICY datastream to determine if this is a collection.
     * On any error we assume object is NOT a collection. 
     * 
	 * @see FedoraAppBatchIngestController#getAdministrator()
     * @param administrator The administrator is just used to get host,port,user,password info.
     * @param collectionPID
     * @return true if object is a collection
     */
	public static boolean isCollection( Administrator administrator, String collectionPID )
	{
		boolean isCollection = false;

		try
		{
			Downloader dl = new Downloader( administrator, administrator.getHost(), administrator.getPort(), administrator.getUserName(), administrator.getUserPassword() );
			
			InputStream is = dl.getDatastreamContent( collectionPID, COLLECTION_POLICY_DATASTREAM, null );
			is.close();
			isCollection = true;
		}
		catch( Exception e )
		{		
			//System.out.println( "Warning "+collectionPID+" is not a collection" );
			// we assume the error is a 404 error caused by this not being a collection.
		}
		
		return isCollection;
	}
	

	/**
	 * Return true an object exists in an collection, this object may or may not be another collection, A COLLECTION OBJECT is considered to 
	 * be a member of it's own Collection!!. This is needed for fedoraProxy. When fedoraProxy get's a request it always verifies that
	 * the requested object is within a web-sites collection for security reasons. Therefore if we want to get a a datastream within
	 * a collection object, it has to pass the isObjectInCollection() test. <br>
	 * If the collection object or the object is null, return false.
	 * 
	 * The administrator is just used to get the host,port,user, and pwd info.
	 * The call to the resource index is actually via httpClient and rest call.
	 * 
	 * @see #isCollection(Administrator, String)
	 * @see FedoraAppBatchIngestController#getAdministrator()
	 * @param administrator The administrator is just used to get host,port,user,password info.
	 * @param collectionPid 
	 * @param object 
	 * @return true if object is a child of the collection
	 */
	public static boolean isObjectInCollection(  Administrator administrator, String collectionPid, String object )
	{
		if ( collectionPid == null || object == null )
		{
			return false;
		}
		
		/*
		 * If the collection and the object are the same return true, see description above.
		 */
		if ( collectionPid.equalsIgnoreCase( object ) )
		{
			return true;
		}
		
		try {
			String namespace = object.split( ":" )[0];
			
			ArrayList allChidren = getChildObjects( administrator, collectionPid );
					
			if ( allChidren.contains( object )){
				return true;
			}
			else {
				return false;
			}
		}
		catch (Exception e){
			return false;
		}
		
	}
	
	/**
	 * See if an object exists in an collection, this object may or may not be another collection.
	 * 
	 * @param host
	 * @param port
	 * @param userName
	 * @param password
	 * @param collectionPid
	 * @param object
	 * @return true if object is in the collection
	 */
	public static boolean isObjectInCollection( String host, int port, String userName, String password, String collectionPid, String object )
	{
		boolean result = false;
		 
		try {
			Administrator administrator = FedoraAppUtil.getAdministrator(host, port, userName, password);
			
			return isObjectInCollection( administrator, collectionPid, object );
				
		} catch (FatalException e) 
		{	
			// TBD need to log

			System.out.println( "Unable to process isObjectInCollection:"+e.getMessage() );
			
			return false;
		}
		
	}

	/**
	 * Verify that the Collection and Content model contained in the batchOptions object exist.
	 * 
	 * @param administrator The administrator is just used to get host,port,user,password info.
	 * @param batchOptions contains the collection and content model.
	 * @throws FatalException used to stop entire batch operation, since all of the rest of the batch files will fail.
	 * @throws Exception used to just log the error, this is used in a mixed ingest (determined by batchOptions.getBatchSet() == mixed )
	 */
	public static void verifyValidCollectionAndContentModel (
			Administrator administrator, BatchIngestOptions batchOptions) throws FatalException, Exception
	{
		String collectionPID   = batchOptions.getFedoraCollection().trim();
		String contentModelPID = batchOptions.getFedoraContentModel().trim();
		
		boolean result = false;
		if ( isCollection( administrator, collectionPID ) )
		{
			Map map = getAllIslandoraCollectionContentModelsMap(administrator, collectionPID ); // map.put( contentModelPID, contentModelPID );     this is handy when debugging
			if ( ! map.containsKey( contentModelPID ) ){
				if ( batchOptions.getBatchSet().equalsIgnoreCase( FedoraAppConstants.MIXED_CONTENT_DIRECTORY ) ) {
					throw new Exception( "Invalid contentModel: " +contentModelPID ); // will not stop batch.l
				}
				else {
					throw new FatalException( "Invalid contentModel: " +contentModelPID ); // will stop entire batch operation.
				}
				
			}
		}
		else {
			if ( batchOptions.getBatchSet().equalsIgnoreCase( FedoraAppConstants.MIXED_CONTENT_DIRECTORY ) ) {
				throw new Exception(  "Invalid ingest collection: " +collectionPID );  // will not stop batch.l
			}
			else {
				throw new FatalException( "Invalid ingest collection: " +collectionPID );// will stop entire batch operation.
			}
		}
	}
	
	

} // ResourceIndexUtils