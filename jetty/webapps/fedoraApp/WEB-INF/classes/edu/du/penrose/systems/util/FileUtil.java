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

package edu.du.penrose.systems.util;

import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
import edu.du.penrose.systems.exceptions.FatalException;
import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraApp.batchIngest.data.BatchIngestOptions;

import java.security.MessageDigest;
import java.math.BigInteger;

/**
 * Sone generic file naming utilities.
 * 
 * @author chet.rebman
 *
 */
public class FileUtil {

	static public String fileDataStringMilliSecondFormat = "MMMMM-dd-yyyy_HH:mm:ss-SS";

	static protected int MAX_TRIES = 100000;

	static String lastFileName = "";

	/**
	 * Return a string containing the current date and time to the second of type april-30-2008:093301 that can be used 
	 * for a file name. 
	 * <br>
	 * <br>
	 * NOTE: ONLY ONE UNIQUE FILE NAME PER MILLISECOND!
	 * 
	 * @see #getDateTimeMilliSecondEnsureUnique()
	 * @return a string of type april-30-2008:0933
	 */
	static public String getDateTimeMilliSecondFileName() {

		Date now = new Date( Calendar.getInstance().getTimeInMillis() );

		SimpleDateFormat dateFormat = new SimpleDateFormat( fileDataStringMilliSecondFormat );

		String fileName = dateFormat.format( now ).toLowerCase();

		return fileName;
	}

	
	public static void deleteDirectoryTree(File f) throws IOException {
		  if (f.isDirectory()) {
		    for (File c : f.listFiles())
		    	deleteDirectoryTree(c);
		  }
		  if (!f.delete())
		    throw new FileNotFoundException("Failed to delete file: " + f);
		}

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

		String fileName = getDateTimeMilliSecondFileName();
		boolean newFileCreated = false;
		int count = 0;

		while ( ! newFileCreated ){
			fileName = getDateTimeMilliSecondFileName();
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
	 * @return unique 13 digit string.
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

	static public String[] getIngestReportList( File directory ){

		return directory.list( new Ingest_report_fileFilter() );
	}
	
	static public File[] getCsvFileList( File directory ){

		return directory.listFiles( new Ingest_report_fileFilter() );
	}

	static public File[] getXmlFileList( File directory ){

		return directory.listFiles( new XML_fileFilter() );
	}
	
	/** 
     * Look for an xml file with a '_REMOTE' marker showing that it was created by a remote ingest.
     * 
	 * @see edu.du.penrose.systems.fedoraApp.FedoraAppConstants#REMOTE_TASK_NAME_SUFFIX
     */
	static public File[] getRemoteXmlFileList( File directory ){

		return directory.listFiles( new XML_remote_fileFilter() );
	}
	
	/**
	 * Look for *.xml files
	 * 
	 * @param directory
	 * @return
	 */
	static public String[] getXmlList( File directory ){

		return directory.list( new XML_fileFilter() );
	}

	/** 
     * Look for an xml file with a '_TASK' marker showing that it was created by a background task
     * such as the one for etd processing of files deposited by Proquest
     * 
	 * @see edu.du.penrose.systems.fedoraApp.FedoraAppConstants#REMOTE_TASK_NAME_SUFFIX
     */
	static public File[] getTaskXmlFileList( File directory ){

		return directory.listFiles( new XML_task_fileFilter() );
	}
	
	/**
	 * Get batch files of type ectdBatchIngest_XXXXXXX.xml
	 * 
	 * @see edu.du.penrose.systems.fedoraApp.FedoraAppConstants#BATCH_FILE_IDENTIFIER
	 * @see edu.du.penrose.systems.fedoraApp.FedoraAppConstants#BATCH_FILE_SUFFIX
	 * 
	 * @param directory
	 * @return return array of batch files names that are part of a local ingest.
	 */
	static public String[] getBatchFileList( File directory ){

		return directory.list( new Batch_fileFilter() );
	}
	
	/**
	 * Get batch files of type batch_XXXXXXX_REMOTE.xml. These files are deposited by fedoraProxy when someone does a remote ingest.
	 * 
	 * @see edu.du.penrose.systems.fedoraApp.FedoraAppConstants#REMOTE_TASK_NAME_SUFFIX
	 * @see edu.du.penrose.systems.fedoraApp.FedoraAppConstants#BATCH_FILE_IDENTIFIER
	 * @see edu.du.penrose.systems.fedoraApp.FedoraAppConstants#BATCH_FILE_SUFFIX
	 * 
	 * @param directory
	 * @return array of batch files names, that are part of a remote ingest
	 */
	static public String[] getRemoteBatchFileList( File directory ){

		return directory.list( new Remote_Batch_fileFilter() );
	}

	/**
	 * Get batch files of type batch_XXXXXXX_TASK.xml
	 * 
	 * @see edu.du.penrose.systems.fedoraApp.FedoraAppConstants#BACKGROUND_TASK_NAME_SUFFIX
	 * @see edu.du.penrose.systems.fedoraApp.FedoraAppConstants#BATCH_FILE_IDENTIFIER
	 * @see edu.du.penrose.systems.fedoraApp.FedoraAppConstants#BATCH_FILE_SUFFIX
	 * 
	 * @param directory
	 * @return array of batch files names, that are part of a remote ingest
	 */
	static public String[] getTaskBatchFileList(  File directory ){

		return directory.list( new Task_Batch_fileFilter() );
	}

	/**
	 * Filter to find 'batch_' files, will match yyyy(batch_)XXXX.xml ie centralCity_batch_ingest.xml or centralCity_batch_overlay.xml 
	 * and without the remote file suffix.
	 * 
	 * @see edu.du.penrose.systems.fedoraApp.FedoraAppConstants#BATCH_FILE_IDENTIFIER
	 * @see edu.du.penrose.systems.fedoraApp.FedoraAppConstants#BATCH_FILE_SUFFIX
	 * @see edu.du.penrose.systems.fedoraApp.FedoraAppConstants#REMOTE_TASK_NAME_SUFFIX
	 * 
	 * @author chet
	 *
	 */
	static public class Batch_fileFilter implements FilenameFilter
	{
		public boolean accept(File dir, String name) 
		{			
			if ( ( name.toLowerCase().contains(FedoraAppConstants.BATCH_FILE_IDENTIFIER) || name.toLowerCase().contains(FedoraAppConstants.BATCH_FILE_IDENTIFIER_2) ) && name.toLowerCase().endsWith( ".xml" )) {
				
				if ( name.toLowerCase().contains( FedoraAppConstants.REMOTE_TASK_NAME_SUFFIX.toLowerCase() ) )
				{
					return false;
				}
				else {
					return true;
				}
			}
			else {
				return false;
			}
		}       	
	}
	
	/**
	 * Filter to find 'batch_' files, will match yyyy(batch_)XXXX_REMOTE.xml ie centralCity_batch_ingest_REMOTE.xml or 
	 * centralCity_batch_overlay_REMOTE.xml 
	 * 
	 * @see edu.du.penrose.systems.fedoraApp.FedoraAppConstants#REMOTE_TASK_NAME_SUFFIX
	 * @see edu.du.penrose.systems.fedoraApp.FedoraAppConstants#BATCH_FILE_IDENTIFIER
	 * @see edu.du.penrose.systems.fedoraApp.FedoraAppConstants#BATCH_FILE_SUFFIX
	 * 
	 * @author chet
	 *
	 */
	static public class Remote_Batch_fileFilter implements FilenameFilter
	{
		public boolean accept(File dir, String name) 
		{			
			if ( name.toLowerCase().contains( FedoraAppConstants.BATCH_FILE_IDENTIFIER ) 
					&& name.toLowerCase().contains( FedoraAppConstants.REMOTE_TASK_NAME_SUFFIX.toLowerCase() ) 
					&& (name.toLowerCase().endsWith( FedoraAppConstants.BATCH_FILE_SUFFIX ) ) || name.toLowerCase().endsWith( FedoraAppConstants.BAGIT_FILE_SUFFIX )) {
				return true;
			}
			else {
				return false;
			}
		}       	
	}


	/**
	 * Filter to find 'batch_' files, will match yyyy(batch_)XXXX_TASK.xml ie centralCity_batch_ingest_REMOTE.xml or 
	 * centralCity_batch_overlay_REMOTE.xml 
	 * 
	 * @see edu.du.penrose.systems.fedoraApp.FedoraAppConstants#BACKGROUND_TASK_NAME_SUFFIX
	 * @see edu.du.penrose.systems.fedoraApp.FedoraAppConstants#BATCH_FILE_IDENTIFIER
	 * @see edu.du.penrose.systems.fedoraApp.FedoraAppConstants#BATCH_FILE_SUFFIX
	 * 
	 * @author chet
	 *
	 */
	static public class Task_Batch_fileFilter implements FilenameFilter
	{
		public boolean accept(File dir, String name) 
		{			
			if ( name.toLowerCase().contains( FedoraAppConstants.BATCH_FILE_IDENTIFIER ) 
					&& name.toLowerCase().contains( FedoraAppConstants.BACKGROUND_TASK_NAME_SUFFIX.toLowerCase() ) 
					&& (name.toLowerCase().endsWith( FedoraAppConstants.BATCH_FILE_SUFFIX ) ) || name.toLowerCase().endsWith( FedoraAppConstants.BAGIT_FILE_SUFFIX )) {
				return true;
			}
			else {
				return false;
			}
		}       	
	}

	static public class XML_fileFilter implements FilenameFilter
	{
		public boolean accept(File dir, String name) 
		{			
			if ( name.endsWith( ".xml" )) {
				return true;
			}
			else {
				return false;
			}
		}       	
	}

    /** 
     * look for an xml file WITHOUT 'remote_' marker showing that it was created by a remote ingest.
     * 
	 * @see edu.du.penrose.systems.fedoraApp.FedoraAppConstants#REMOTE_TASK_NAME_SUFFIX
     * @author chet
     */
	static public class XML_NON_remote_fileFilter implements FilenameFilter
	{
		public boolean accept(File dir, String name) 
		{			
			if ( ! name.toLowerCase().contains( FedoraAppConstants.REMOTE_TASK_NAME_SUFFIX.toLowerCase() ) && name.toLowerCase().endsWith( ".xml" )) {
				return true;
			}
			else {
				return false;
			}
		}       	
	}
	
	
    /** 
     * look for an xml file with a '_REMOTE' marker showing that it was created by a remote ingest.
     * 
	 * @see edu.du.penrose.systems.fedoraApp.FedoraAppConstants#REMOTE_TASK_NAME_SUFFIX
     * @author chet
     */
	static public class XML_remote_fileFilter implements FilenameFilter
	{
		public boolean accept(File dir, String name) 
		{			
			if ( name.toLowerCase().contains( FedoraAppConstants.REMOTE_TASK_NAME_SUFFIX.toLowerCase() ) && name.toLowerCase().endsWith( ".xml" )) {
				return true;
			}
			else {
				return false;
			}
		}       	
	}


    /** 
     * look for an xml file with a '_TASK' marker showing that it was created by a background task, such as the etd processing of 
     * files deposited by Proquest.
     * 
	 * @see edu.du.penrose.systems.fedoraApp.FedoraAppConstants#BACKGROUND_TASK_NAME_SUFFIX
     * @author chet
     */
	static public class XML_task_fileFilter implements FilenameFilter
	{
		public boolean accept(File dir, String name) 
		{			
			if ( name.toLowerCase().contains( FedoraAppConstants.BACKGROUND_TASK_NAME_SUFFIX.toLowerCase() ) && name.toLowerCase().endsWith( ".xml" )) {
				return true;
			}
			else {
				return false;
			}
		}       	
	}

	/**
	 * File ends with zip or xml and does NOT HAVE remote task suffix
	 * 
	 * @author chet
	 *
	 */
	static public class XML_or_ZIP_NON_remote_fileFilter implements FilenameFilter
	{
		public boolean accept(File dir, String name) 
		{			
			if ( name.toLowerCase().endsWith( ".xml" ) || name.toLowerCase().endsWith( ".zip" ) ) 
			{
				if ( ! name.toLowerCase().contains( FedoraAppConstants.REMOTE_TASK_NAME_SUFFIX.toLowerCase() ) )
				{
					return true;
				}
				else {
					return false;
				}
			}
			else {
				return false;
			}
		}       	
	}
	
	/**
	 * File ends with zip or xml and has remote task suffix
	 * 
	 * @author chet
	 *
	 */
	static public class XML_or_ZIP_remote_fileFilter implements FilenameFilter
	{
		public boolean accept(File dir, String name) 
		{			
			if ( name.toLowerCase().endsWith( ".xml" ) || name.toLowerCase().endsWith( ".zip" ) ) 
			{
				if ( name.toLowerCase().contains( FedoraAppConstants.REMOTE_TASK_NAME_SUFFIX.toLowerCase() ) )
				{
					return true;
				}
				else {
					return false;
				}
			}
			else {
				return false;
			}
		}       	
	}
	
	/**
	 * File ends with zip
	 * 
	 * @author chet
	 *
	 */
	static public class ZIP_fileFilter implements FilenameFilter
	{
		public boolean accept(File dir, String name) 
		{			
			if ( name.toLowerCase().endsWith( ".zip" ) ) {
				return true;
			}
			else {
				return false;
			}
		}       	
	}
	
	
	/**
	 * Looks for ingest logs, these are .txt AND .csv files.
	 * 
	 * @author chet
	 */
	static private class Ingest_report_fileFilter implements FilenameFilter
	{
		public boolean accept(File dir, String name) 
		{			
			if ( name.startsWith( FedoraAppConstants.DU_INSTITUTION_NAME) && name.endsWith( FedoraAppConstants.BATCH_INGEST_REPORT_FILE_EXT )) 
			{
				return true;
			}
			else {
				return false;
			}
		}       	
	}


	/**
	 * Gets a unique file name, based on the fileName with the time in milliseconds added before the file extension., 
	 * This works with all file types including directories.
	 * 
	 * @return a unique file name based on current time.
	 * @param originalFileName  
	 */
	static public String getUniqueFileName( String originalFileName ){

		int index = originalFileName.lastIndexOf('.');
		
		if ( index < 0 ) index = originalFileName.length();
		
		String suffix = originalFileName.substring( index );
		
		originalFileName = originalFileName.replace( suffix, "" );
			
		String newFileName = originalFileName + "_" +getDateTimeMilliSecondEnsureUnique() + suffix;
		
		return 	newFileName;
	}
	
	/**
	 * Gets a unique file name, based on the fileName with the time in milliseconds added before the file extension., 
	 * This works with all file types including directories. If the batchIngestOptions say this is a remote or
	 * background ingest append the appropriate file suffix.
	 * 
	 * @return a unique file name based on current time.
	 * @param originalFileName  
	 * @param batchIngestOptions  if batchIngestOptions.getIngestThreadType() is    REMOTE append  the _REMOTE marker
	 * @param batchIngestOptions  if batchIngestOptions.getIngestThreadType() is BACKGROUND append the _TASK   marker
	 * @see FedoraAppConstants#REMOTE_TASK_NAME_SUFFIX 
	 * @see FedoraAppConstants#BACKGROUND_TASK_NAME_SUFFIX 
	 */
	static public String getBatchUniqueFileName( String originalFileName, BatchIngestOptions batchIngestOptions ){

		int index = originalFileName.lastIndexOf('.');
		
		if ( index < 0 ) index = originalFileName.length();
		
		String suffix = originalFileName.substring( index );
		
		originalFileName = originalFileName.replace( suffix, "" );
			
		String newFileName = originalFileName + "_" +getDateTimeMilliSecondEnsureUnique();
				
		if ( batchIngestOptions.getIngestThreadType() == BatchIngestOptions.INGEST_THREAD_TYPE.REMOTE )
		{
			newFileName = newFileName + FedoraAppConstants.REMOTE_TASK_NAME_SUFFIX;
		}
		
		if ( batchIngestOptions.getIngestThreadType() == BatchIngestOptions.INGEST_THREAD_TYPE.BACKGROUND )
		{
			newFileName = newFileName + FedoraAppConstants.BACKGROUND_TASK_NAME_SUFFIX;
		}
		
		return 	newFileName + suffix;
	}


	/**
	 * Returns the contents of the file in a byte array.
	 * 
	 * @param file
	 * @return file as an array of bytes
	 * @throws IOException
	 */
	public static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		// You cannot create an array using a long type.
		// It needs to be an int type.
		// Before converting to an int type, check
		// to ensure that file is not larger than Integer.MAX_VALUE.
		if (length > Integer.MAX_VALUE) {
			// File is too large
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int)length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "+file.getName());
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
	}
	
} // FileUtil
