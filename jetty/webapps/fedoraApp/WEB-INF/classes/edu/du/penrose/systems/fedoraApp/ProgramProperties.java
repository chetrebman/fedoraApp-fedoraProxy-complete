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

import edu.du.penrose.systems.exceptions.PropertyStorageException;

import java.io.*;
import java.util.*;
import java.net.*;

/**
 * Manages multiple properties files used by the application using a 
 * java.util.Properties object. <br>
 * Provides the interface for saving and retrieving properties that describe and
 * control the current application.
 * 
 * @author chet.rebman
 *
 */
public abstract class ProgramProperties {

    /**
     * Value used in properties file for a true value;
     */
    static public final String TRUE="TRUE";
    
    /**
     * Value used in properties file for a false value;
     */
    static public final String FALSE="FALSE";
    
    static public Map<URL, ProgramProperties> PropertiesHolder = null;
    
    /**
     * Get the property from the underlying storage medium.
     * If the storage medium or the field/value does not exist null is returned.
     * If the key is null, an exception is thrown.
     * 
     * @param key the field for the desired value
     * @return value for the field
     */
    public abstract String getProperty(String key);

    /**
     * Get the property from the underlying storage medium.
     * If the storage medium or the field/value does not exist the default value 
     * is returned.
     * If the key is null, return default value.
     * 
     * @param key the field name.
     * @param defaultValue if the field does not exist, set this as default.
     * @return value the keys value
     * @throws PropertyStorageException 
     */
    public abstract boolean getProperty(String key, boolean defaultValue ) throws PropertyStorageException; 
    
    /**
     * Get the property from the underlying storage medium.
     * If the storage medium or the field/value does not exist the default value 
     * is returned.
     * If the key is null, return default value.
     * 
     * @param key the field for the desired value
     * @param defaultValue if the field does not exist, set this as default.
     * @return value the keys value
     * @throws PropertyStorageException 
     */
    public abstract String getProperty(String key, String defaultValue ) throws PropertyStorageException; 

	/**
	 * Remove the property field and it's value from the underlying storage medium.
	 * If the storage medium or the field/value does not exist no error is returned.
	 * 
	 * @param key the property to remove.
	 */
	public abstract void removeProperty(String key);

    /**
     * Save the field and it's value to the underlying storage medium, Make sure
     * you set the file header first!
     * 
     * @see #setHeader(String)
     * @param key the property.
     * @param value the properties value. if null it is set to and empty string ""
     * @throws PropertyStorageException if unable to save to storage medium.
     */
    public abstract void saveProperty(String key, String value) 
        throws PropertyStorageException;
    
    /**
     * Save the field and it's value to the underlying storage medium.Make sure
     * you set the file header first!
     * 
     * @see #setHeader(String)
     * @param key the property.
     * @param value the properties value.
     * @throws PropertyStorageException if unable to save to storage medium.
     */
    public abstract void saveProperty(String key, boolean value) 
        throws PropertyStorageException;
	
	/**
	 * Manages multiple properties files used by the application using a 
	 * java.util.Properties object. 
	 * This method acts as a factory that returns an instance that gives access
	 * to the program properties at the specified URL. The Properties for a URL
	 * is stored in a static map once it is created for use through out the life
	 * of the application.
	 * 
	 * @param propertiesFileURL location of the program properties resource.
	 * @return the application ProgramProperties
	 * @throws RuntimeException on any error.
	 */
	public static ProgramProperties getInstance( URL propertiesFileURL ) {
		
	    ProgramProperties propertiesOBJ = null;
	    if ( PropertiesHolder == null ) {
	        PropertiesHolder = new HashMap<URL, ProgramProperties>();
	    }
	    propertiesOBJ = PropertiesHolder.get( propertiesFileURL );
	    
	    if ( propertiesOBJ == null ) 
	    {   
    	    if ( propertiesFileURL.getProtocol().toLowerCase().equals( "file") ) {
    	        propertiesOBJ = new ProgramFileProperties( new File( propertiesFileURL.getFile() ) );
    	        PropertiesHolder.put( propertiesFileURL, propertiesOBJ );
    	    }
    	     
    	    if ( propertiesOBJ == null ) {
    	        throw new RuntimeException( "Unsupported Protocol for properties file: "+propertiesFileURL );
    	    }
	    }
		
	    return propertiesOBJ;
	    
	} // getInstance()
	
	/**
	 * Create a new ProgramProperties which uses the supplied header.
     * NOTE: will not be  written until you actually save a property!
	 * 
     * @param propertiesFileURL location of the program properties resource.
	 * @see #getInstance(URL)
	 * @param fileHeader new header for the ProgramProperties
	 * @return the application ProgramProperties
	 */
	public static ProgramProperties getInstance( URL propertiesFileURL, String fileHeader ){
	    
	    ProgramProperties propFile = getInstance( propertiesFileURL );
	    propFile.setHeader( fileHeader );
	    
	    return propFile;
	}
	

    /**
     * Set the header for the properties file. <br>
     * NOTE: will not be  written until you actually save a property!
     * 
     * @param header the header to set
     */
    public abstract void setHeader(String header);
    
    
    /** 
     * Returns an enumeration of all the keys in this property list, including 
     * distinct keys in the default property list if a key of the same name has 
     * not already been found from the main properties list.
     * 
     * @return enumeration
     */
    public abstract Enumeration getPropertyNames();
    
    
    /**
     * Get the property names in a String array.
     * 
     * @return String array of property names.
     */
    public abstract String[] getPropertyNamesAsArray();
    

    /**
     * This uses the properties values to set the order for the property names.
     * <br>
     * We assume the each property has a number as its value. This number is
     * used to order the results.
     * <br>
     * No assumptions are made a correct properties file is assumed.
     * 
     * @return array of ordered property names.
     * @throws Exception on any error.
     */
    public abstract String[] getPropertyNamesAsOrderedArray() throws Exception;
    
} // ProgramProperties