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

package edu.du.penrose.systems.fedoraApp.web.bus.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.misc.BASE64Encoder;


/**
 * This class adds some security related utilities to encrypt passwords etc.
 * 
 * @author Chet
 *
 */
public final class PasswordService
{
    
    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    /**
     * SHA hash/encode a plain text password.
     * Plain text password.
     * NO CHECKING is done to validate input password except if null it is set to ""
     * @return String  the hash value or null if unsuccessful.
     *
     */
    public synchronized String encrypt(String plaintext) 
    {
        this.logger.info("Encrypt password");
        if (plaintext == null){
            plaintext = "";
        }
        final String hash;
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA");
            md.update(plaintext.getBytes("UTF-8")); 
        }
        catch (final NoSuchAlgorithmException e){
            return null;
        }
        catch (final UnsupportedEncodingException e){
            return null;     
        }
        
        final byte raw[] = md.digest(); 
        hash = (new BASE64Encoder()).encode(raw); 
        return hash; 
    }
    

    
} // PasswordService
   

