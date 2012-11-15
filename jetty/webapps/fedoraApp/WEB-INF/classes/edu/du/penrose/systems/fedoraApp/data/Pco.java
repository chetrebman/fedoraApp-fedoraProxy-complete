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

package edu.du.penrose.systems.fedoraApp.data;

import java.io.File;

public class Pco {

	private String mimeType = null;
	private File file     = null;
	private String dsID   = null;
	
	public Pco( File file, String dataStreamID, String mimeType )
	{
		this.mimeType = mimeType;
		this.file     = file;
		this.dsID     = dataStreamID;
	}

	public String getMimeType() {
		return mimeType;
	}

	public File getFile() {
		return file;
	}

	public String getDsID() {
		return dsID;
	}
	
	
}
