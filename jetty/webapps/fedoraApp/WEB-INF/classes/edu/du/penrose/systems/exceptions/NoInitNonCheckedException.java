/*
 * Copyright 2011 University of Denver
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
 * This is unchecked exception that is thrown when application initialization
 * error is detected.
 * 
 * @author chet.rebman
 *
 */
public class NoInitNonCheckedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NoInitNonCheckedException() {
		super ( "Initialization Error" );
	}

	public NoInitNonCheckedException(String message) {
		super(message);
	}

	public NoInitNonCheckedException(Throwable cause) {
		super(cause);
	}

	public NoInitNonCheckedException(String message, Throwable cause) {
		super(message, cause);
	}

} // NoInitException
