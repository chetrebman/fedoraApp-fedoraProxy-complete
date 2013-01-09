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

package edu.du.penrose.systems.fedoraApp;

import java.util.*;
import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.du.penrose.systems.exceptions.PropertyStorageException;

/**
 * Manages property files used by the application using a 
 * java.util.Properties object. 
 * <br><br>
 * This class does not throw exceptions if unable to access the 
 * underlying file system but rather simply returns null for a property on any
 * error.
 * 
 * @author chet.rebman
 */
public class ProgramFileProperties extends ProgramProperties  {
	
	private Properties myProperties = null;
	private long     lastFileUpdate;
	private File     propertiesFile = null;
	private String   header = "Application properties file.";
	
    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());
    
	public ProgramFileProperties( File propertiesFile ){
		
	    this.propertiesFile = propertiesFile;
		
	} // ProgramFileProperties()
	
	private Properties getMyProperties(){
	    
		// logger.info( "Opening application properties file." );
		
	    this.checkAndLoadPropertiesFile();
	    
	    return this.myProperties;
	}
	
	/**
	 * @see edu.du.penrose.systems.fedoraApp.ProgramProperties#getProperty(java.lang.String)
	 */
	public String getProperty( String key ){ // myProperties = null
			
		String result = null;

		this.checkAndLoadPropertiesFile();

        result = this.getMyProperties().getProperty( key );
        
        if ( result != null ){ result.trim(); }
		
		return result;
		
	} // getProperty()
	
	/**
	 * Load the properties file, if the file has not yet been loaded or if the
	 * file has been updated on the file system. If the file does not exist 
	 * create it.<br><br>
	 * NOTE: This is the only method that should be accessing this.myProperties
	 * variable directly!
	 * 
	 * @return File which is the Properties file managed by this obj.
	 */
	protected File checkAndLoadPropertiesFile() {
		
		File inFile = null;
		if ( this.myProperties == null ) {			
			this.myProperties = new Properties();
		}
		
		try {
			inFile = this.getPropertiesFile();
			// this.logger.info( "looking for properties file: " + inFile.getAbsolutePath() );
			if ( ! inFile.exists() ) 
			{
			    inFile.createNewFile();
				this.logger.info( "Not found, creating properties file: " + inFile.getAbsolutePath() );
			}
			if( this.lastFileUpdate != inFile.lastModified() ) 
			{
				// this.logger.info( "Reloading properties file: " + inFile.getAbsolutePath() );
				this.myProperties = new Properties(); //needed to ensure update when a property is commented out.
				this.myProperties.load( new FileInputStream( inFile ) );
				this.lastFileUpdate = inFile.lastModified();
			}
		} catch ( FileNotFoundException e ) {
			myProperties = null;
			this.logger.error( "Properties File NOT found: " + inFile.getAbsoluteFile() );
		} catch (IOException e) {
			this.logger.error( "Properties File IO Exception: " + inFile.getAbsoluteFile() );
			myProperties = null;
			String errorMsg = "IO Exception: "+e.getMessage();
			this.logger.error( errorMsg );
			throw new RuntimeException( errorMsg );
		}

		return inFile;
		
	} // loadPropertiesFile()
	
	/**
	 * Save the current contents of the Properties object to disk.
	 * 
	 * @param outFile the file to save to.
	 */
	protected void savePropertiesFile( File outFile ){
		
		try {
			logger.info( "Saving properties file: "+outFile.getAbsolutePath() );
			this.getMyProperties().store( new FileOutputStream( outFile ), this.header );
			this.lastFileUpdate = outFile.lastModified();
		} catch (FileNotFoundException e) {
			myProperties = null;
			this.logger.error( "File NOT found! "+outFile.getAbsolutePath() );
		} catch (IOException e) {
			myProperties = null;
			this.logger.error( "IO Exception! "+outFile.getAbsolutePath() );
		}
		
	} // savePropertiesFile()
	
	/**
	 * Get the properties file this object is managing.
	 * 
	 * @return File containing the full path/file name.
	 */
	protected File getPropertiesFile() {
		
		return propertiesFile;
		
	} // getPropertiesFile()
	
	/**
	 * @see edu.du.penrose.systems.fedoraApp.ProgramProperties#removeProperty(java.lang.String)
	 */
	public void removeProperty( String key ) {
		
		if ( myProperties!= null ) {
		    getMyProperties().remove(key);
			this.savePropertiesFile( this.checkAndLoadPropertiesFile() );
		}
	}	
	
	/**
	 * @see edu.du.penrose.systems.fedoraApp.ProgramProperties#saveProperty(java.lang.String, java.lang.String)
	 */
	public void saveProperty( String key, String value )
		throws PropertyStorageException {
		
		if ( value == null ){ value = ""; }
		if ( getMyProperties()!= null ) {
			if ( key == null || value == null )
			{
				throw new PropertyStorageException( "Invalid key:value key="+key+", value="+value );
			}
		    getMyProperties().setProperty(key, value);
			this.savePropertiesFile( this.checkAndLoadPropertiesFile() );
		}
		else {
			throw new PropertyStorageException( "Properties storage not initialized ");
		}
	}


    /**
     * @see ProgramProperties#getProperty(String, boolean)
     */
    public boolean getProperty(String key, boolean defaultValue)
            throws PropertyStorageException {

        String result = this.getProperty( key );
        
        if ( result == null ) {
            if ( defaultValue ) { 
                result = "true";
            }
            else {
                result = "false";
            }
            this.saveProperty( key,  result );
        }
        
        if ( result.toLowerCase().equals("true") || result.toLowerCase().equals("yes") ) {
            return true;
        }
           
        return false;     
    }
    
    /**
     * @see ProgramProperties#getProperty(String, String)
     */
    public String getProperty(String key, String defaultValue)
            throws PropertyStorageException {

        String result = this.getProperty( key );
        
        if ( result == null ) {
            result = defaultValue;
            this.saveProperty( key,  result );
        }
                   
        return result;     
    }


    /**
     * @see ProgramProperties#saveProperty(String, boolean)
     */
    public void saveProperty(String key, boolean value)
            throws PropertyStorageException {
       
        String saveValue = "false";
        if ( value ) {
            saveValue = "true";
        }
        
        this.saveProperty( key, saveValue );
    }


    /**
     * @see #setHeader(String)
     */
    public void setHeader(String header) {
        this.header = header;
    }


    /**
     * @see #getPropertyNames()
     */
    public Enumeration getPropertyNames() {

        return this.getMyProperties().propertyNames();
    }

    /**
     * @see ProgramProperties#getPropertyNamesAsArray()
     */
    public String[] getPropertyNamesAsArray(){
        
        String[] keySetArray = new String[1];
        
        if ( this.getMyProperties() == null ){
            return new String[0];
        }
        
        Set<Object> keySet  = this.getMyProperties().keySet();
        if ( keySet != null ){
            keySetArray = keySet.toArray( keySetArray );
        }
        
        return keySetArray;
    }

    /**
     * @see ProgramProperties#getPropertyNamesAsOrderedArray()
     */
    public String[] getPropertyNamesAsOrderedArray() throws Exception {
     
        String[] orderedNames = null;
        try {
            String[] unorderedNames=this.getPropertyNamesAsArray();
            orderedNames = new String[ unorderedNames.length ]; 
            for ( int i=0; i<unorderedNames.length; i++ ) 
            {
                String propValue = this.getProperty( unorderedNames[ i ] );
                int order = Integer.valueOf( propValue ).intValue();
                orderedNames[ order-1 ] = unorderedNames[ i ];
            }
        }
        catch ( Throwable e ){
            String errorMsg = "Invalid Ordered Property File! :";
            throw new Exception( errorMsg+e );
        }
        
        return orderedNames;
    }
    
} // ProgramFileProperties
