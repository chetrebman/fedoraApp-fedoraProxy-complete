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

package edu.du.penrose.systems.fedoraApp.reports;

import java.io.BufferedWriter;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.du.penrose.systems.exceptions.FatalException;

public class PidReport {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());
    
	private BufferedWriter pidWriter = null;
	
	public PidReport( BufferedWriter pidReportStream )
	{
		pidWriter = pidReportStream;
	}

	/**
	 * Start a new PID report and obtain its writer.
	 * @return BufferedWriter for PID report
	 * @throws FatalException 
	 * @throws FatalException
	 */
	public BufferedWriter openPidReport(  ) throws FatalException  {

		try {
			pidWriter.write( "Fedora PID, Mets OBJID, File Name" );
			pidWriter.newLine(); 
			pidWriter.newLine();
		} catch (Exception e) {
			throw new FatalException("Unable to open PID Report:"+e);
		}

		return pidWriter;
	}

  public void newLine() throws FatalException
  {
	  try {
		this.pidWriter.newLine();
	} catch (IOException e) {
		throw new FatalException("Unable to write new line to PID Report:"+e);
	}
  }
  
  public void write( String output ) throws FatalException
  {
	  try {
		this.pidWriter.write( output );
	} catch (IOException e) {
		throw new FatalException("Unable to write to PID Report:"+e);
	}
  }
  


	/* Flush the PID report writer and leave it open..
	 */
	public void flushPidReport() throws FatalException{

		// TBD throw fatal exception here?

		try {
			this.pidWriter.flush();
		} catch (IOException e) {
			throw new FatalException( "Unable to flush log file!"+e );
		}

	} // closePidReport

	/**
	 * Close the PID report and it's write stream.
	 */
	public void closePidReport() throws FatalException{

		// TBD throw fatal exception here?

		try {
			this.pidWriter.close();
		} catch (IOException e) {
			throw new FatalException( "Unable to close pid report file!"+e );
		}

	} // closePidReport

}
