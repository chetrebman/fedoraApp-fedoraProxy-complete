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

package edu.du.penrose.systems.fedoraApp.batchIngest.data;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;

import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestOptions;

public class BatchOptionsValidator  implements Validator
{

	public boolean supports(Class arg0) {
		return BatchIngestOptions.class.isAssignableFrom(arg0);
	}

	public void validate(Object arg0, Errors errors) {
		   
		BatchIngestOptions command = ( BatchIngestOptions ) arg0;
	    
		if ( command.isSplitXMLinWorkDirToMets() )
		{
			this.validateSplitMetsFileName( command.getSplitXMLfileName() , errors );
		}
	    
	}

	private void validateSplitMetsFileName(String splitXMLfileName, Errors errors) 
	{
		if ( ! (splitXMLfileName.toLowerCase().endsWith( ".xml") || splitXMLfileName.toLowerCase().endsWith( ".zip")) )
		{
			 errors.rejectValue( "workFile", "form.batchIndexer.splitXMLinWorkDirToMets" );
			 return;
		}
		
		if ( splitXMLfileName.equals( FedoraAppConstants.FORM_DEFAULT_SELECT_VALUE ) )
		{
			errors.rejectValue( "workFile", "form.batchIndexer.splitXMLinWorkDirToMets" );
			return;
		}
		
	}

} // BatchOptionsValidator
