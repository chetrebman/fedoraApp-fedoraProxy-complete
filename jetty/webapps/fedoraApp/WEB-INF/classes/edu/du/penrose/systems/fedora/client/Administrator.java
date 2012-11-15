package edu.du.penrose.systems.fedora.client;

import java.io.*;
import java.net.URL;

import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;

// import fedora.client.FedoraClient;           // 2.2 client
// import fedora.server.access.FedoraAPIA;
//import fedora.server.management.FedoraAPIM; 

import org.fcrepo.server.access.FedoraAPIA;       // 3.4 client
import org.fcrepo.server.management.FedoraAPIM;
import edu.du.penrose.systems.fedora.client.Downloader;
import edu.du.penrose.systems.fedora.client.Uploader;   
import org.fcrepo.client.FedoraClient; 

/**
 * This is a replacement for the Fedora Distribution class 
 * fedora.client.Admininstator 
 * with the swing code and static variables removed (so that a GUI client does
 * not start up, when it is instantiated). Also a FedoraClient is created and
 * stored as a local variable with public access methods provided.
 * <br><br>
 * One fedora.client.Admininstator object is created, used to log in and then 
 * saved in the session.  
 * <br><br>
 * This class provides the initial login to fedora and then holds and provides 
 * easy access to a number of objects, such as FedoraAPIA, FedoraAPIM,
 * Downloader, Uploader and FedoraClient objects.  After initial login, the 
 * Administrator object is normally saved and accessed through a session variable.
 * <br><br>
 * NOTE: This object is stored in the session to verify that a user is logged in.
 * <br>
 * NOTE: THIS CLASS IS NOT THREAD SAFE, each user must be given their own copy!
 * 
 *
 * NOTE The following libraries are needed...
 * wsdl4j-1.5.1.jar
 * commons-discovery.jar
 * fcrepo-server-3.4-utilities-main.jar
 * jaxrpc.jar
 * logback-core-0.9.18.jar
 * logback-classic-0.9.18.jar
 * trippi-1.1.2-core.jar
 * fcrepo-common-3.4.jar
 * fcrepo-client-admin-3.4.jar
 * jdom.jar
 */

public class Administrator {

    int NO_PORT = -1;
    
    private String host     = null;
    private String protocol = null;
    private int    port     = NO_PORT;
    private String userName = null;
    private String userPassword = null;
    
    private Downloader downloader = null;
    private Uploader   uploader   = null;
    private FedoraAPIA APIA=null;
    private FedoraAPIM APIM=null;
    
    private FedoraClient fedoraClient = null;
    
    /**
     * NOTE The following libraries are needed...
     * wsdl4j-1.5.1.jar
     * commons-discovery.jar
     * fcrepo-server-3.4-utilities-main.jar
     * jaxrpc.jar
     * logback-core-0.9.18.jar
     * logback-classic-0.9.18.jar
     * trippi-1.1.2-core.jar
     * fcrepo-common-3.4.jar
     * fcrepo-client-admin-3.4.jar
     * jdom.jar
     * 
     * axis.jar
     * @param protocol
     * @param port
     * @param host
     * @param userName
     * @param userPassword
     * @throws Exception
     */
    public Administrator( String protocol, int port, String host, String userName, String userPassword ) throws Exception {
        
        URL fedoraURL = new URL(protocol, host, port, '/'+FedoraAppConstants.FEDORA_WEB_CONTEXT_PATH );
        String baseURL = fedoraURL.toString();
        
        this.protocol = protocol;
        this.port     = port;
        this.host     = host;
        this.userName = userName;
        this.userPassword = userPassword; 
        
        this.fedoraClient = new FedoraClient( baseURL, userName, userPassword );

        this.downloader   = new Downloader( this, host, port,  userName, userPassword);
        this.uploader     = new Uploader( this,  host, port,  userName, userPassword);      

            // set SOAP stubs.
        this.setAPIA( fedoraClient.getAPIA() ); 
        this.setAPIM( fedoraClient.getAPIM() );
    }
    
	public FedoraAPIA getAPIA() {
		
		return this.APIA;
	}
	
	/**
     * This is the preferred way to get an already created APIA object. As 
     * opposed to using getFedoraClient.getAPIA().
     * 
	 * @param apia
	 */
	public void setAPIA( FedoraAPIA apia) {

		this.APIA = apia;
	}

    /**  
     * This is the preferred way to get an already created APIM object. As 
     * opposed to using getFedoraClient.getAPIM().
     * 
     * @return the aPIM
     */
    public FedoraAPIM getAPIM() {
        return APIM;
    }

    /**
     * @param apim the aPIM to set
     */
    public void setAPIM(FedoraAPIM apim) {
        APIM = apim;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @return the protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @return the userPassword
     */
    public String getUserPassword() {
        return userPassword;
    }

    /**
     * @return the downloader
     */
    public Downloader getDownloader() {
        return downloader;
    }


    /**
     * @return the uploader
     */
    public Uploader getUploader() {
        return uploader;
    }

    /**
     * @return the fedoraClient
     */
    public FedoraClient getFedoraClient() {
        return fedoraClient;
    }

	
	
} // Administrator
