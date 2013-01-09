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
package edu.du.penrose.systems.util;

import java.io.File;
import java.net.URL;
import java.util.HashMap;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

public class XmlUtil {

	/**
	 * Check a xml file against a schema.
	 * 
	 * @param fileToCheck
	 * @param schemURL
	 * @throws Exception
	 */
	static public void schemaCheck( File fileToCheck, URL schemURL ) throws Exception 
	{
	
		final  String W3C_SCHEMA_SPEC = "http://www.w3.org/2001/XMLSchema";
		HashMap<String, Schema> MetsSchema = null;
		
		if ( MetsSchema == null )
		{
			MetsSchema = new HashMap();
		}
		
		if ( MetsSchema.get( schemURL.toString() ) == null )
		{
	  	  // 1. Lookup a factory for the W3C XML Schema language
	      SchemaFactory factory =  SchemaFactory.newInstance( W3C_SCHEMA_SPEC );
	      
	      // 2. Compile the schema. 
	      MetsSchema.put( schemURL.toString(), factory.newSchema( schemURL ) );
		}
		
	    // 3. Get a validator from the schema.
	    Validator validator = MetsSchema.get( schemURL.toString() ).newValidator();
	    
	    // 4. Parse the document you want to check.
	    Source source = new StreamSource( fileToCheck );
	    
	    // 5. Check the document        
	    validator.validate(source);
	}

}
