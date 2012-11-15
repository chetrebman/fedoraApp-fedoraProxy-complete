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
