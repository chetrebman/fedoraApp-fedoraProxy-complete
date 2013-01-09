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

package edu.du.penrose.systems.fedoraApp.web.exceptions;

/**
 * This unchecked exception is thrown when a request is made for a mapped page
 * but we are unable to retrieve it.
 * 
 * @author chet.rebman
 *
 */
public class PageNotFoundNonCheckedException extends RuntimeException {

	public PageNotFoundNonCheckedException() {
		super ("Application Error - Page not found");
	}

	public PageNotFoundNonCheckedException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public PageNotFoundNonCheckedException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public PageNotFoundNonCheckedException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

} // RuntimeException
