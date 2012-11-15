package edu.du.penrose.systems.util;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.domain.JSonBagger;
import gov.loc.repository.bagger.profile.BaggerProfileStore;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.verify.impl.CompleteVerifierImpl;
import gov.loc.repository.bagit.verify.impl.ParallelManifestChecksumVerifier;
import gov.loc.repository.bagit.verify.impl.ValidVerifierImpl;
import gov.loc.repository.bagit.writer.Writer;
import gov.loc.repository.bagit.writer.impl.FileSystemWriter;
import gov.loc.repository.bagit.writer.impl.TarBz2Writer;
import gov.loc.repository.bagit.writer.impl.TarGzWriter;
import gov.loc.repository.bagit.writer.impl.TarWriter;
import gov.loc.repository.bagit.writer.impl.ZipWriter;

import java.io.File;

public class BagitCreator {

	static final public String BAG_VERSION = "0.96";
	
	BagitCreator()
	{
		
	}
	
	/**
	 * Creates a bag amd then validates it, sending the results to standard out.
	 * 
	 * @param args
	 */
	public static void main(String[] args) 
	{
		 
		 if ( args.length < 2 )
		 {
			 System.out.println( "\nUsage: BagitCreator bagDir fetchUrl\n" );
			 System.out.println( "\t the directory parameter is of type 'xxxx'/'yyyy'/data" );
			 System.out.println( "\t the data directory MUST be named data and contain the files, ie images, batch file etc." );
			 System.out.println( "\t the parent directory containing 'yyyy' will also contain the bagit file called, 'yyyy'.zip"    );
			 System.out.println( "\t fetchUrl must point to a location that contains a directory called data with all of the files contained in the data dir above."  );
		
			 return;
		 }

		 String bagDir   = args[0];	        
		 String fetchUrl = args[1];
		 
		 BagitCreator myCreator = new BagitCreator();
		 
		 boolean result = myCreator.createBag(bagDir, fetchUrl);
		 
		 String resultString;
		 if ( result )
			{
				resultString = "valid bag";
			}
			else {
				resultString = "bad bag";
			}	
		 
		 System.out.println( resultString );
    } 
	
	/**
	 * The directory parameter is of type 'yyyy'/data the data directory MUST be named data and contain the files, ie images, 
	 * batch file etc. The parent directory containing 'yyyy' will also contain the bagit file called, 'yyyy'.zip" 
	 * <br><br>
	 * The fetchUrl must point to a location that contains a directory called data with all of the files contained in the data dir above.
	 * 
	 * @param bagDir 
	 * @param fetchUrl
	 * 
	 * @return return true if valid bag has been created, false otherwise.
	 */
	public boolean createBag( String bagDir, String fetchUrl )
	{
 		/*
		 * You must initialize the profileStore or DefaultBag will throw an exception.
		 * 
		 * FYI BaggerProfileStore initialization is defined in...
		 * bagger-business/src/main/resources/gov/loc/repository/bagger/ctx/common/business-layer-context.xmlbagger-business/src/main/resources/gov/loc/repository/bagger/ctx/common/business-layer-context.xml
		 * 
		 */
		JSonBagger json = new JSonBagger();	
		BaggerProfileStore profileStore = new BaggerProfileStore( json );
	
		// create a new bag

		BagFactory bagFactory = new BagFactory();
		
        DefaultBag bag = new DefaultBag();
        
       
        bag.createPreBag( new File( bagDir ), BAG_VERSION );
        
        bag.isHoley( true );
        
        bag.isSerial(true);
		bag.setSerialMode(DefaultBag.ZIP_MODE);
		Writer bagWriter = null;
		
		bag.getFetch().setBaseURL( fetchUrl );
	
		short mode = bag.getSerialMode();
		if (mode == DefaultBag.NO_MODE) {
			bagWriter = new FileSystemWriter(bagFactory);
		} else if (bag.getSerialMode() == DefaultBag.ZIP_MODE) {
			bagWriter = new ZipWriter(bagFactory);
		} else if (mode == DefaultBag.TAR_MODE) {
			bagWriter = new TarWriter(bagFactory);
		} else if (mode == DefaultBag.TAR_GZ_MODE) {
			bagWriter = new TarGzWriter(bagFactory);
		} else if (mode == DefaultBag.TAR_BZ2_MODE) {
			bagWriter = new TarBz2Writer(bagFactory);
		}
		
		bag.setRootDir( new File ( bagDir ) );
		
    	// bag.setName( "newBagTest.zip"); has no effect the file name is taken from the root directory name
		bag.write(bagWriter);
		    
		// now validate it
		boolean result = validateBag( bagDir );
			
		return result;
	}
	
	static public boolean validateBag( String bagDirectory ) 
	{
		DefaultBag bag = new DefaultBag( new File( bagDirectory ) ,"0.96" );
		
		CompleteVerifierImpl completeVerifier = new CompleteVerifierImpl();

		ParallelManifestChecksumVerifier manifestVerifier = new ParallelManifestChecksumVerifier();

		ValidVerifierImpl validVerifier = new ValidVerifierImpl(completeVerifier, manifestVerifier);

		String result = bag.validateBag( validVerifier );

		if ( result == null )
		{
			return true;
		}

		return false;	
	}
	
} // BagitCreator

