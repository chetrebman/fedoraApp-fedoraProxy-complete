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

package edu.du.penrose.systems.exceptions;

/**
 * Errors that are reported but do not stop the application.
 * 
 * @author chet.rebman
 *
 */
public class NonFatalException extends RuntimeException {


    /**
     * Serial ID for serialization
     */
    private static final long serialVersionUID = 1L;

    public NonFatalException( String errorMsg ) {
        
        super( errorMsg );
    }
    
    public NonFatalException( String errorMsg, Exception e ) {
        
        super( errorMsg, e );
    }
    
    
} // NonFatalException
