/*
 * Copyright 2012 University of Denver
 * Author Fernando Reyes
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
package edu.du.penrose.systems.etd
import java.util.Calendar; 
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import org.codehaus.groovy.runtime.TimeCategory

@SuppressWarnings("deprecation")
/*
 * Copyright 2012 University of Denver
 * Author Fernando Reyes
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
class EtdEmbargo {
    
    /**
    * Handles embargo codes
    * @param string embargoCode
    * @param string acceptDate
    * @return string setEmbargo
    */
    public checkEmargoCode( embargoCode, acceptDate ) {
        
        //("0", "No embargo")
        //("1", "6 month embargo")
        //("2", "1 year embargo") 
        //("3", "2 year embargo") 
        
        //println "Embargo: " + embargoCode
        //println "Accept Date: " + acceptDate
        
        def embargo
        def setEmbargo = ""
        Date now = new Date();
        DateFormat CurrentDate = new SimpleDateFormat("MM/dd/yyyy") 
        def docDate
        def currentDate
        def dateParts = acceptDate.split("/")
        
        if (embargoCode.equals("1")) {
           
            use(TimeCategory) {
                docDate = new Date(acceptDate) + 6.months
            }
            
            embargo = compareDates( acceptDate, docDate, CurrentDate, now )
            
            if (embargo) {
               setEmbargo += "[6 MONTH EMBARGO from " + dateParts[0] + "/" + dateParts[2][2..3] + "] " 
            }
            
            
        } else if (embargoCode.equals("2")) {
           
            use(TimeCategory) {
                docDate = new Date(acceptDate) + 1.year
            }
            
            embargo = compareDates( acceptDate, docDate, CurrentDate, now )
            
            if (embargo) {
                setEmbargo += "[1 YEAR EMBARGO from " + dateParts[0] + "/" + dateParts[2][2..3] + "] "
            }
            
        } else if (embargoCode.equals("3")) {
            
            use(TimeCategory) {
                docDate = new Date(acceptDate) + 2.years
            }
            
            
            embargo = compareDates( acceptDate, docDate, CurrentDate, now )
            
            if (embargo) {
                setEmbargo += "[2 YEAR EMBARGO from " + dateParts[0] + "/" + dateParts[2][2..3] + "] "
            }
        } 
       
        return setEmbargo
    }
    
    /**
    * Compare dates
    * @param string acceptDate
    * @param string docDate
    * @param Date CurrentDate
    * @param Date now
    * @return string setEmbargo
    */
   public compareDates( acceptDate, docDate, CurrentDate, now) {
        
        def setEmbargo = false
        def currentDate = CurrentDate.format(now)
        docDate = CurrentDate.format(docDate)
        
        def date1 = CurrentDate.parse(docDate)
        def date2 = CurrentDate.parse(currentDate)

        if (date1.compareTo(date2)>0) {
            setEmbargo = true
        }
        
        return setEmbargo
   }
     
}