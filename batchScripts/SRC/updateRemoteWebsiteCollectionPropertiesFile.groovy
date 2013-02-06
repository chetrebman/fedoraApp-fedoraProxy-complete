
import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraApp.util.MetsBatchFileSplitter.IslandoraElementFilter;

class updateRemoteWebsiteCollectionPropertiesFile {
	
	static final SPACE = " ";
	
	static final NONE     = "none"
	static final EQ_TRUE  = "=true"
	static final EQ_FALSE = "=false"
	static final EQ_ANYTHING = "=anything"
	static final EQUAL = "="
	static final T = "T"
	static final F = "f"
	static final COMMA = ","
	
	/**
	 * Updates  WEB-INF/config/webSitCollection.properties 
	 * @param args
	 */
	static main(args)
	{

		if ( args.length < 4 )
		{
			println "\nUsage: topLevelDirectory institution(nameSpace) batchSet(collection) collectionPid"
			return
		}
		
		println "Running\n"
		
		def console = System.console()

		def topLevelDirectory = args[0]
		
		if ( ! topLevelDirectory.endsWith( ""+File.separatorChar ) )
		{
			topLevelDirectory = topLevelDirectory + File.separatorChar
		}
		
		def institution            = args[1]
		def batchSetName           = args[2]
		def islandoraCollectionPID = args[3]
	
		/*
		 * This will update the feodraProxy/WEB-INF/config/webSiteCollection.properties file
		 */
		new File( topLevelDirectory ).eachDirRecurse() { dir ->
			dir.eachFileMatch(~/WEB-INF/) { webInfDir ->
				def configDir =  new File( webInfDir.getAbsolutePath() + "/config" )
				if ( configDir.exists() )
					configDir.eachFileMatch(~/webSiteCollection.properties/)
					{ file ->
						this.setWebSiteCollection( file, institution, batchSetName, islandoraCollectionPID )
					}
			}
		}
		
		 println "Done!\n"

	} // end main

	
	
	
	static def setWebSiteCollection( File inputFile, institution, batchSetName, islandoraCollectionPID )
	{
		def outputFile = new File( inputFile.getAbsolutePath() +".tmp" );
		outputFile.write("") // If .tmp file already exists, remove any previous contents
		
		List lines = inputFile.readLines()
		
		def boolean collection_PID_lineFound = false // line of type {batchSet(collection)={collectionPID}
		def boolean institution_collection_lineFound = false // line of the {institution}={batchSet_1},{batchSet_2}
		def boolean new_collection_PID_line_addedd = false
		
		// first let's see if the institution has an entry yet.
		for ( String line in lines )
		{
			if ( line.startsWith( institution+EQUAL ) )
			{
				institution_collection_lineFound = true;
			}
		}		
		
		lines = inputFile.readLines()
		for ( String line in lines )
		{
			if ( ! institution_collection_lineFound )
			{		
				// create a new line
					if ( ! line.startsWith("#") && ! new_collection_PID_line_addedd )
					{ 
						line=institution+EQUAL+batchSetName+"\n" + line
						new_collection_PID_line_addedd = true;
					}
			}
			else 
			{
				if ( line.startsWith( institution+EQUAL ) )
				{
					// append to the end of the existing line
					if ( ! line.contains( batchSetName ) )
					{
						line=line+COMMA+SPACE+batchSetName		
					}
				}
			}
			
			if ( line.startsWith( batchSetName+EQUAL ) )
			{
				line = batchSetName+EQUAL+islandoraCollectionPID
				collection_PID_lineFound = true;
			}
			outputFile << line+"\n"
		}
		
		if ( ! collection_PID_lineFound )
		{
			outputFile << batchSetName+EQUAL+islandoraCollectionPID+"\n"
			
		}
		println "Updated "+ inputFile
		inputFile.delete()
		outputFile.renameTo( inputFile.getAbsolutePath() )	
	}
	



} // class
