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

package edu.du.penrose.systems.fedoraApp.web.data;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class GetFedorObjValidator  implements Validator {

	public boolean supports(Class clazz ) {
		
		return GetFedoraObjCmd.class.isAssignableFrom(clazz);
	}

	public void validate(Object arg0, Errors errors) {

		if ( false == true ){
			                             
			errors.rejectValue( "objectName", "form.getFedoraObj.invalidPID" );
			
		}
		
	} // validate()

} // GetFedorObjValidator
