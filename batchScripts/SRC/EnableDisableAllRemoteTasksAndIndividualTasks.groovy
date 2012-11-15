import java.awt.geom.Line2D;
import java.io.File;

import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;

class EnableDisableAllRemoteTasksAndIndividualTasks {

	static final EQ_TRUE  = "=true"
	static final EQ_FALSE = "=false"
	static final EQUAL = "="
	static final T = "T"
	static final UNDERSCORE="_"

	static main(args)
	{
		def institution = null
		def batchSet    = null

		if ( args.length < 2 )
		{
			println "Usage: toplevelDirectory, true(T)/false(F) to enable/disable tasks, institution(optional) batchSet(optinal) )"
			println "\t: updates enableTasks=xxx unless the institution and batchSet parameters exist"
			println "\t: if istitution and batchset are set, only the batchSet is effectes the global enableTask=xxxx is not changed"
			println "\t: if istitution and batchset ='ALL', all settings are set to true/false"
			println "\t: NOTE: If a institution and batchSet are spedified but they don't exist in the properties file, they are added to the end.\n"

			return
		}

		if ( args.length == 3 )
		{
			println "Usage: toplevelDirectory, true(T)/false(F) to enable/disable tasks, institution(optional) batchSet(optinal) )"
			return
		}
		def topLevelDirectory = args[0]
		def String enableTasksString = (String) args[1]

		if ( args.length == 4 )
		{
			institution = args[2]
			batchSet    = args[3]
		}

		def boolean enableRemoteTasks = false
		if ( enableTasksString.length() == 1 && enableTasksString.compareToIgnoreCase( "t" ) == 0 )
		{
			enableRemoteTasks = true;
		}
		if ( enableTasksString.compareToIgnoreCase( "true" ) == 0 )
		{
			enableRemoteTasks = true
		}

		// This will set the enableTasks=TRUE/FALSE properties AND ALL of the individual {institution}_{batchSet}=TRUE/FALSE entries
		new File( topLevelDirectory ).eachDirRecurse() { dir ->
			dir.eachFileMatch(~/WEB-INF/) { webInfDir ->
				def configDir =  new File( webInfDir.getAbsolutePath() + "/config" )
				if ( configDir.exists() )
					configDir.eachFileMatch(~/taskEnable.properties/)
				{ file ->
					this.setTaskEnable( file, enableRemoteTasks, institution, batchSet )

				}

			}
		}
	}


	static def setTaskEnable( File inputFile, enableRemoteTasks, String institution,  String batchSet )
	{
		def outputFile = new File( inputFile.getAbsolutePath() +".tmp" );
		outputFile.write("") // If file already exists, remove any previous contents

		List lines = inputFile.readLines()

		def boolean lineFound = false
		for ( String line in lines )
		{
			// if the institution and batchSet we just set enableTasks=xxx
			
			if ( institution == null && batchSet == null )
			{
				if ( line.startsWith( FedoraAppConstants.TASK_ENABLE_PROPERTY + EQUAL ) )
				{
					if ( enableRemoteTasks )
					{
						line = FedoraAppConstants.TASK_ENABLE_PROPERTY + EQ_TRUE
					}
					else
					{
						line = FedoraAppConstants.TASK_ENABLE_PROPERTY + EQ_FALSE
					}
				}
			}
			else
			{
					if ( institution.compareToIgnoreCase( "all" ) == 0 && batchSet.compareToIgnoreCase( "all" ) == 0 )
					{
						// set enableTasks={enableRemoteTasks}
						if ( line.startsWith( FedoraAppConstants.TASK_ENABLE_PROPERTY + EQUAL ) )
						{
							if ( enableRemoteTasks )
							{
								line = FedoraAppConstants.TASK_ENABLE_PROPERTY + EQ_TRUE
							}
							else
							{
								line = FedoraAppConstants.TASK_ENABLE_PROPERTY + EQ_FALSE
							}
						}
	
						// set ALL XXXXX_YYYY={enableRemoteTasks}
						if ( line.contains( UNDERSCORE ) && line.contains( EQUAL ) && (line.endsWith(EQ_TRUE) || line.endsWith(EQ_FALSE)))
						{
							def String[] tokens = line.split( EQUAL )
							lineFound = true;
							if ( enableRemoteTasks )
							{
								line=tokens[0] + EQ_TRUE
							}
							else
							{
								line=tokens[0]+ EQ_FALSE
							}
						}
					}
					else
					{
						// set the specific {institution}_{batchSet}={enableRemoteTasks}
						if ( line.startsWith( institution+UNDERSCORE+batchSet ) && line.contains( EQUAL ) && (line.endsWith(EQ_TRUE) || line.endsWith(EQ_FALSE)))
						{
							def String[] tokens = line.split( EQUAL )
							lineFound = true;
							if ( enableRemoteTasks )
							{
								line=tokens[0] + EQ_TRUE
							}
							else
							{
								line=tokens[0]+ EQ_FALSE
							}
						}
					}
			}

			outputFile << line+"\n"
		}
		
		// If an institution and batchSet were specified and they don't exist in the file add it to the end.		
		if ( institution != null && batchSet != null && lineFound == false )
		{
			def lastLine
			if ( enableRemoteTasks )
			{
				lastLine=institution+UNDERSCORE+batchSet+EQ_TRUE
			}
			else
			{
				lastLine=institution+UNDERSCORE+batchSet+EQ_FALSE
			}
			outputFile << lastLine+"\n"
		}
		
		println "Updated "+ inputFile
		inputFile.delete()
		outputFile.renameTo( inputFile.getAbsolutePath() )
	}

}