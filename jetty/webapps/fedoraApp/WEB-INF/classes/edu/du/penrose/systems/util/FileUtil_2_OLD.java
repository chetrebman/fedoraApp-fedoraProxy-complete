/*
 * Copyright 2008 University of Denver
 * Author chet.rebman
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

import java.util.*; 
import java.io.*;
import java.security.MessageDigest;
import java.math.BigInteger;

/**
 * Sone generic file naming utilities.
 * 
 * @author chet.rebman
 *
 */
public class FileUtil_2_OLD {

    static protected int MAX_TRIES = 100000;
    
    static String lastFileName = "";
    
    /**     
     * Return a string containing the current date and time to the second of type april-30-2008:093301 that can be used 
     * for a file name. If the file already exists keep trying until the file name is unique up to MAX_TRIES. This routine 
     * therefore can only return one file name per java millisecond at best.
     * <br>
     * @see #MAX_TRIES
     * @return String with current/unique date and time to the millisecond
     * @throws RuntimeException
     */
    static public String getDateTimeMilliSecondEnsureUnique() {
        
        String fileName = TimeDateUtils.getDateTimeMilliSecondFileName();
        boolean newFileCreated = false;
        int count = 0;
        
        while ( ! newFileCreated ){
            fileName = TimeDateUtils.getDateTimeMilliSecondFileName();
            newFileCreated = ! fileName.equals(lastFileName );
            count++;
            if ( count >= MAX_TRIES ) {
               throw new RuntimeException( "Unable to get unique file :"+fileName );
            }
        } 
        
        lastFileName = fileName;
        
        return fileName;
    }
    
    static public String getMD5( File inputFile ) throws Exception {

        final int radix = 16;
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] buffer = new byte[8192];
        
        InputStream fis = new FileInputStream( inputFile );
        int bytesRead = 0;
        digest.reset();
        while ( (bytesRead = fis.read(buffer)) > 0) {
           digest.update( buffer, 0, bytesRead ); 
        }
       
        byte[] md5sum = digest.digest();
        BigInteger bigInt = new BigInteger(1, md5sum);
        String output = bigInt.toString( radix );
        
        return output;
    }
    
    /**
     * Return a unique number string. The string is based on current millisecond time and will be 13 digits.
     * 
     * @Returns: unique 13 digit string.
     */

    static public String getMillisAsString() {
        
        return String.valueOf( Calendar.getInstance().getTimeInMillis() );
    }
    
    /**
     * Return a unique number string, based on current time in milliseconds.
     * <br>
     * NOTE: If this routine is called quicker then the java millisecond time 
     * changes the number will not be unique!
     * 
     * @see #getMillisAsString()
     * @return unique 13 digit string.
     */
    static public String getUniqueNumber() {
        
        return getMillisAsString();
    }
    
    
} // FileUtil
