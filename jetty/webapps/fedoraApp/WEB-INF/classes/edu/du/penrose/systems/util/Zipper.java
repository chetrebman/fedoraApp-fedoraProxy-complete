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

import java.io.*;
import java.util.zip.*;

public class Zipper {

	public void compress_gzip( String inputFileName ) throws Exception 
	{	
		File inputFile = new File( inputFileName );
		BufferedInputStream in = new BufferedInputStream(  new FileInputStream( inputFile )   );

		BufferedOutputStream out = new BufferedOutputStream( new GZIPOutputStream(new FileOutputStream( inputFile+".gz") ) );

		int c;
		while ( (c = in.read()) != -1) out.write( c );

		in.close();
		out.close();
	}

	/**
	 * NOT FULLY TESTED
	 * 
	 * @param outpuDirectory
	 * @param inputPathAndFileName
	 * @throws Exception
	 */
	public void uncompress_gzip(  String outpuDirectory, String inputPathAndFileName ) throws Exception
	{	
		File inputFile = new File( inputPathAndFileName );
		File outputFile = new File(  outpuDirectory +"/"+ inputFile.getAbsolutePath().replace( ".gz", "") );

		BufferedInputStream in2 = new BufferedInputStream(new GZIPInputStream( new FileInputStream( inputFile )  ) );

		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream( outputFile ) );

		int c;
		while ( (c = in2.read()) != -1) out.write( c );		

		in2.close();
		out.close();
	}

	public String getZipTopLevelDirectory( String inputPathAndFileName ) throws Exception
	{
		File inputFile = new File( inputPathAndFileName );
		
		String topLevelDirectoryName = null; 
		
		FileInputStream fis = new FileInputStream( inputFile );
		ZipInputStream zis = new 
		ZipInputStream(new BufferedInputStream(fis));
		ZipEntry entry;
		entry = zis.getNextEntry();
		
		File tempFile = new File( entry.toString() );
		if ( entry.isDirectory() )
		{
			topLevelDirectoryName = tempFile.getName();
		}
		else
		{
			topLevelDirectoryName = tempFile.getParent();
		}
		
		return topLevelDirectoryName;
	}
	
    /**
     * Extract input file to specified directory, WILL NOT OVERWRITE, exception is thrown.
     * 
     * @param outpuDirectory
     * @param inputPathAndFileName
     * @return the topLevel directory name.
     * @throws Exception
     */
	public void uncompress_zip( String outpuDirectory, String inputPathAndFileName ) throws Exception
	{
		File inputFile = new File( inputPathAndFileName );
			
		int BUFFER = 2048;
		
		BufferedOutputStream dest = null;
		FileInputStream fis = new FileInputStream( inputFile );
		ZipInputStream zis = new 
		ZipInputStream(new BufferedInputStream(fis));
		ZipEntry entry;
		while((entry = zis.getNextEntry()) != null) {
			System.out.println("Extracting: " +entry);
			int count;
			byte data[] = new byte[BUFFER];
			
			if ( entry.isDirectory() )
			{
				File testFile =  new File ( outpuDirectory +"/"+ entry.getName() );
				if ( testFile.exists() )
				{
					String errorMsg = "Error: extracting zip "+outpuDirectory +"/"+ entry.getName()+" already exists!";
					
					System.out.println( errorMsg ); // TBD should be log file.
					
					throw new Exception( errorMsg );
				}
				new File( outpuDirectory +"/"+ entry.getName()  ).mkdir();	
				continue;
			}
			else {
				File tempFile = new File( entry.getName() );
				String path = tempFile.getPath();
				File dirPart = new File( outpuDirectory+path.replace( tempFile.getName(), "" ) );
				if ( ! dirPart.exists() ){
					dirPart.mkdir();
				}
			}	
			
			
			// write the files to the disk
			FileOutputStream fos = new 	FileOutputStream( outpuDirectory +"/"+ entry.getName() );
			dest = new 	BufferedOutputStream(fos, BUFFER);
			
			while ((count = zis.read(data, 0, BUFFER)) 
					!= -1) {
				dest.write(data, 0, count);
			}
			dest.flush();
			dest.close();
		}
		
		zis.close();
	}


} // Zipper
