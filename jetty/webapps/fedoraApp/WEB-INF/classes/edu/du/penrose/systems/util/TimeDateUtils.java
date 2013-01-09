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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeDateUtils {

	static public String DATETIME_MILLISECOND_FORMAT = "MMMMM-dd-yyyy-HHmmssSS";
	static public String DATETIME_MINUTE_FORMAT      = "MMMMM-dd-yyyy hh:mma";

	/**
	 * Return a string containing the current date and time to the second of type april-30-2008-093301 
	 * 
	 * @return a string of type april-30-2008:0933
	 */
	static public String getDateTimeMilliSecondFileName() {
	    
	    Date now = new Date( Calendar.getInstance().getTimeInMillis() );
	    
	    SimpleDateFormat dateFormat = new SimpleDateFormat( TimeDateUtils.DATETIME_MILLISECOND_FORMAT );
	    
	    String fileName = dateFormat.format( now ).toLowerCase();
	    
	    return fileName;
	}

	/**
	 * Return the current time of type: June-01-2009 10:30PM
	 * @return
	 */
	static public String getCurrentTimeMonthDayYearHourMinute()
	{
		  
	    Date now = new Date( Calendar.getInstance().getTimeInMillis() );
	    
	    SimpleDateFormat dateFormat = new SimpleDateFormat( TimeDateUtils.DATETIME_MINUTE_FORMAT );
	    
	    String currentTime = dateFormat.format( now ).toLowerCase();
	    
	    return currentTime;
	}

}
