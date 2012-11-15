
import edu.du.penrose.systems.fedoraApp.FedoraAppConstants;
import edu.du.penrose.systems.fedoraProxy.FedoraProxyConstants; 
import edu.du.penrose.systems.fedoraApp.util.MetsBatchFileSplitter.IslandoraElementFilter;

class FedoraAppProxyDistInstall {
	
	static final SPACE = " ";
	
	static final NONE     = "none"
	static final EQ_TRUE  = "=true"
	static final EQ_FALSE = "=false"
	static final EQ_ANYTHING = "=anything"
	static final EQUAL = "="
	static final T = "T"
	static final F = "f"
	
	/**
	 * Updates batchScripts/getPids and fedoraApp, fedoraProxy /WEB-INF/config/batchIngest.properties
	 * @param args
	 */
	static main(args)
	{

		if ( args.length < 27 )
		{
			println "\nUsage: jettyDirectory batchSpaceDirectory fedoraHost fedoraPort fedoraUser fedoraPwd fedoraProxyFedoraUser fedoraProxyFedoraPwd islandoraIngest(T/F) disableHandleServer(T/F) worldHandlerServer handleServerHost, handleServerPort handleServerApp solrHost solrPort successEmail failureEmail smptHost smtpPort smtpUser smtpPassword enableRemoteTasks(T/f) fedoraNamespace\n useSmtpSSL batchSetName  "
			println "\n\tIf a setting =\"NOT_SET\" the value will be set to \"\" unless it is a boolean, in which case it will be set to \"f\" \n"
			return
		}
		
		println "Running\n"
		
		def console = System.console()

		def topLevelDirectory = args[0]
		def batchSpaceDirectory = args[1]
		
		if ( ! topLevelDirectory.endsWith( ""+File.separatorChar ) )
		{
			topLevelDirectory = topLevelDirectory + File.separatorChar
		}
		if ( ! batchSpaceDirectory.endsWith( ""+File.separatorChar ) )
		{
			batchSpaceDirectory = batchSpaceDirectory + File.separatorChar
		}
		
		def fedoraHost          = args[2]
			if ( fedoraHost == "NOT_SET" ){ fedoraHost = "" }
			
		def fedoraPort          = args[3]
			if ( fedoraPort == "NOT_SET" ){ fedoraPort = "" }
			
		def newUser             = args[4]
			if ( newUser == "NOT_SET" ){ newUser = "" }
			
		def newPassword         = args[5]
			if ( newPassword == "NOT_SET" ){ newPassword = "" }
			
		def fpNewUser           = args[6]
			if ( fpNewUser == "NOT_SET" ){ fpNewUser = "" }
			
		def fpNewPassword       = args[7]
			if ( fpNewPassword == "NOT_SET" ){ fpNewPassword = "" }	
		
		def islandora           = args[8] // t/f	
			if ( islandora == "NOT_SET" ){ islandora = "f" }
			
		def disableHandleServer = args[9] // t/f
			if ( disableHandleServer == "NOT_SET" ){ disableHandleServer = "f" }
		
		def worldHandleServer   = args[10]
			if ( worldHandleServer == "NOT_SET" ){ worldHandleServer = "" }
			
		def handleServerHost    = args[11]
			if ( handleServerHost == "NOT_SET" ){ handleServerHost = "" }
			
		def handleServerPort    = args[12]
			if ( handleServerPort == "NOT_SET" ){ handleServerPort = "" }
			
		def handleServerApp     = args[13]
			if ( handleServerApp == "NOT_SET" ){ handleServerApp = "" }
			
		def solrHost            = args[14]
			if ( solrHost == "NOT_SET" ){ solrHost = "" }
			
		def solrPort            = args[15]
			if ( solrPort == "NOT_SET" ){ solrPort = "" }
			
		def successEmail        = args[16]
			if ( successEmail == "NOT_SET" ){ successEmail = "" }
			
		def failureEmail        = args[17]
			if ( failureEmail == "NOT_SET" ){ failureEmail = "" }
			
		def smtpServer          = args[18]
			if ( smtpServer == "NOT_SET" ){ smtpServer = "" }
			
		def smtpPort            = args[19]
			if ( smtpPort == "NOT_SET" ){ smtpPort = "" }
			
		def smtpUser            = args[20]
			if ( smtpUser == "NOT_SET" ){ smtpUser = "" }
			
		def smtpPassword        = args[21]
			if ( smtpPassword == "NOT_SET" ){ smtpPassword = "" }
		
		def enableRemoteTasks   = args[22] // t/f
			if ( enableRemoteTasks == "NOT_SET" ){ enableRemoteTasks = "f" }			

		def fedoraNamespace     = args[23]		
			if ( fedoraNamespace == "NOT_SET" ){ fedoraNamespace = "" }
			
		def fromEmail           = args[24]
			if ( fromEmail == "NOT_SET" ){ fromEmail = "" }			
		
		def sslEmail            = args[25] // t/f
			if ( sslEmail == "NOT_SET" ){ sslEmail = "f" }
		
		def batchSetName            = args[26]
			if ( batchSetName == "NOT_SET" ){ batchSetName = "" }
				

		if ( ! islandora.equalsIgnoreCase( T ) )
		{
			islandora = F
		}
		
		if ( ! disableHandleServer.equalsIgnoreCase( T ) )
		{
			disableHandleServer= F
		}

		this.printInputs(topLevelDirectory, batchSpaceDirectory, fedoraHost, fedoraPort, newUser, newPassword, fpNewUser, fpNewPassword, islandora, disableHandleServer, worldHandleServer,
				handleServerHost, handleServerPort, handleServerApp, solrHost, solrPort, successEmail,  failureEmail, smtpServer, smtpPort,  smtpUser, smtpPassword, enableRemoteTasks, fedoraNamespace,
				 fromEmail, sslEmail, batchSetName   )
		
		this.substituteGetPids( topLevelDirectory+"batchScripts"+File.separatorChar+"getPids.sh", fedoraHost, fedoraPort, newUser, newPassword, fedoraNamespace )
		
		this.substituteCreatePidsInOBJID( topLevelDirectory+"batchScripts"+File.separatorChar+"createPid_in_OBJID_BatchFile.sh", fedoraHost, fedoraPort, newUser, newPassword, fedoraNamespace )

		/*
		 * This will update WEB-INF/config/batchIngest.properties in both feodraApp and fedoraProxy
		 */
		new File( topLevelDirectory ).eachDirRecurse() { dir ->
			dir.eachFileMatch(~/WEB-INF/) { webInfDir ->
				def configDir =  new File( webInfDir.getAbsolutePath() + "/config" )
				if ( configDir.exists() )
					configDir.eachFileMatch(~/.*.properties/)
					{ file ->
						this.substitute( file.getAbsolutePath(), batchSpaceDirectory, fedoraHost, fedoraPort, newUser, newPassword, fpNewUser, fpNewPassword, islandora, disableHandleServer, worldHandleServer,
								handleServerHost, handleServerPort, handleServerApp, solrHost, solrPort )
					}
	
			}
		}
		
		/*
		 * This will update the batch_space{institution}{batchSet}/{batchSet}.properties file
		 * 
		 */
		new File( batchSpaceDirectory ).eachDirRecurse() { dir ->
			dir.eachFileMatch(~/.*.properties/)
			{ file ->
				this.substitute( file.getAbsolutePath(), batchSpaceDirectory, fedoraHost, fedoraPort, newUser, newPassword, fpNewUser, fpNewPassword, islandora, disableHandleServer, worldHandleServer,
								handleServerHost, handleServerPort, handleServerApp, solrHost, solrPort)
			}
		}
	
		
		/*
		 * This will update the feodraProxy/WEB-INF/config/taskEnable.properties file
		 */
		new File( topLevelDirectory ).eachDirRecurse() { dir ->
			dir.eachFileMatch(~/WEB-INF/) { webInfDir ->
				def configDir =  new File( webInfDir.getAbsolutePath() + "/config" )
				if ( configDir.exists() )
					configDir.eachFileMatch(~/taskEnable.properties/)
					{ file ->
						this.setTaskEnable( file, fedoraNamespace, batchSetName, enableRemoteTasks  )
			
					}
	
			}
		}

		/*
		 * This will update the This will update the batch_space{institution}{batchSet}/{batchSet}_REMOTE.properties file
		 */
		new File( batchSpaceDirectory ).eachDirRecurse() { dir ->
			dir.eachFileMatch(~/.*_REMOTE.properties/)
			{ file ->
				this.substituteMail( file, successEmail, failureEmail, smtpServer, smtpPort, smtpUser, smtpPassword, fromEmail, sslEmail )
			}
		}
		
		/*
		 * This will update the This will update the batchScripts/TEMPLATES/batchSet_REMOTE.properties file
		 */
		new File( batchSpaceDirectory ).eachDirRecurse() { dir ->
			dir.eachFileMatch(~/.*_REMOTE.properties/)
			{ file ->
				this.substituteMail( file, successEmail, failureEmail, smtpServer, smtpPort, smtpUser, smtpPassword, fromEmail, sslEmail )
			}
		}
		
		 println "Done!\n"

	} // end main

	
	static def printInputs( topLevelDirectory, batchSpaceDirectory, fedoraHost, fedoraPort, newUser, newPassword, fpNewUser, fpNewPassword, islandora, disableHandleServer, worldHandleServer,
	handleServerHost, handleServerPort, handleServerApp, solrHost, solrPort, successEmail, failureEmail, smtpHost, smtpPort, smtpUser, smtpPassword, enableRemoteTasks, fedoraNamespace, fromEmail, sslEmail,
	batchSetName )
	{
		println "\n0 Toplevel directory="+topLevelDirectory
		println "1 Batch space directory="+batchSpaceDirectory
		
		println "\n*******NEW SETTINGS**********\n"
		println "\n2 Fedora Host="+fedoraHost
		println "3 Fedora Port="+fedoraPort
		println "4 fedoraApp Fedora User="+newUser
		println "5 fedoraApp Fedora Password="+newPassword
		println "6 fedoraProxy Fedora User="+fpNewUser
		println "7 fedoraProxy Fedora Password="+fpNewPassword
		println "8 Islandora ingest t/f" + islandora
		println "\n9 vi Disable Hander Server t/f" +disableHandleServer
		println "\n10 World Handles Server="+worldHandleServer
		println "11 Handle Server host="+handleServerHost
		println "12 Handle Server Port="+handleServerPort
		println "13 Handler Server App="+handleServerApp
		println "\n14 Solr Host="+solrHost
		println "15 Solr Port="+solrPort
		println "\n16 Success email="+successEmail
		println "17 Failure email="+failureEmail
		println "18 Smtp host="+smtpHost
		println "19 Smtp port="+smtpPort
		println "20 Smtp user="+smtpUser
		println "21 Smtp password="+smtpPassword
		println "22 Enable remote tasks" +enableRemoteTasks
		println "23 institution/fedoraNameSpace="+fedoraNamespace
		println "24 From email="+fromEmail
		println "25 Smtp SSL="+sslEmail
		println "26 BatchSet name = "+batchSetName
		println ""
	}

	
	static def setTaskEnable( File inputFile, fedoraNamespace, batchSetName, String enableRemoteTasks )
	{
		def outputFile = new File( inputFile.getAbsolutePath() +".tmp" );
		outputFile.write("") // If file already exists, remove any previous contents
		
		List lines = inputFile.readLines()
		
		def boolean lineFound = false
		for ( String line in lines )
		{
			
			if ( line.startsWith( FedoraAppConstants.TASK_ENABLE_PROPERTY + EQUAL ) )
			{
				if ( enableRemoteTasks.equalsIgnoreCase( T ) )
				{
					line = FedoraAppConstants.TASK_ENABLE_PROPERTY + EQ_TRUE 
				}
				else
				{
					line = FedoraAppConstants.TASK_ENABLE_PROPERTY + EQ_FALSE 
				}
			}
			
			// if the {institution}{batchspace}=xxxx was is found update it
			if ( line.startsWith( fedoraNamespace+"_"+batchSetName+EQUAL ) )
			{
				lineFound = true;
				if ( enableRemoteTasks.equalsIgnoreCase( T ) )
				{
					line=fedoraNamespace+"_"+batchSetName + EQ_TRUE 
				}
				else
				{
					line=fedoraNamespace+"_"+batchSetName+ EQ_FALSE 
				}
			}
			outputFile << line+"\n"
		}	
		
		// if the {institution}{batchspace}=xxxx was not found add it now
		if ( ! lineFound )
		{
				if ( enableRemoteTasks.equalsIgnoreCase( T ) )
				{
					outputFile << fedoraNamespace+"_"+batchSetName + EQ_TRUE 
				}
				else
				{
					outputFile << fedoraNamespace+"_"+batchSetName+ EQ_FALSE 
				}
		}
		println "Updated "+ inputFile
		inputFile.delete()
		outputFile.renameTo( inputFile.getAbsolutePath() )
	}
	

	
	static def substituteGetPids( inputFileName, fedoraHost, fedoraPort, fedoraUser, fedoraPassword, fedoraNamespace )
	{
		def inputFile = new File( inputFileName );

		if ( ! inputFile.exists() )	{
			println "File not found "+inputFile
			return
		}

		def outputFile = new File( inputFileName+".tmp" );

		outputFile.write("") // If file already exists, remove any previous contents

		List lines = inputFile.readLines()

		def boolean madeChange = false
		for ( String line in lines )
		{
			if ( line.contains( "getPids.jar" ) )
			{
				def temp = line.split()
				temp[3] = fedoraHost
				temp[4] = fedoraPort
				temp[5] = fedoraUser
				temp[6] = fedoraPassword
				temp[7] = fedoraNamespace

				line = temp[0]+SPACE+temp[1]+SPACE+temp[2]+SPACE+temp[3]+SPACE+temp[4]+SPACE+temp[5]+SPACE+temp[6]+SPACE+temp[7]+SPACE+temp[8]
				madeChange = true;
			}
			outputFile << line+"\n"
		}

		if ( madeChange )
		{
			println "Updated "+ inputFile
			inputFile.delete()
			outputFile.renameTo( inputFileName )
		}
		else {
			outputFile.delete()
		}

		
	} // substitute getPids()

	
	static def substituteCreatePidsInOBJID( inputFileName, fedoraHost, fedoraPort, fedoraUser, fedoraPassword, fedoraNamespace )
	{
		def inputFile = new File( inputFileName );

		if ( ! inputFile.exists() )	{
			println "File not found "+inputFile
			return
		}

		def outputFile = new File( inputFileName+".tmp" );

		outputFile.write("") // If file already exists, remove any previous contents

		List lines = inputFile.readLines()

		def boolean madeChange = false
		for ( String line in lines )
		{
			if ( line.contains( "createPid_in_OBJID_BatchFile.jar" ) )
			{
				def temp = line.split()
				temp[5] = fedoraHost
				temp[6] = fedoraPort
				temp[7] = fedoraUser
				temp[8] = fedoraPassword
				temp[9] = fedoraNamespace

				line = temp[0]+SPACE+temp[1]+SPACE+temp[2]+SPACE+temp[3]+SPACE+temp[4]+SPACE+temp[5]+SPACE+temp[6]+SPACE+temp[7]+SPACE+temp[8]+SPACE+temp[9]
				madeChange = true;
			}
			outputFile << line+"\n"
		}

		if ( madeChange )
		{
			println "Updated "+ inputFile
			inputFile.delete()
			outputFile.renameTo( inputFileName )
		}
		else {
			outputFile.delete()
		}

		
	} // substituteCreatePidsInOBJID()
	
	static def substitute( inputFileName, batchSpaceDirectory, fedoraHost, fedoraPort, newUser, newPassword, fpNewUser, fpNewPassword,
	String islandora, String disableHandleServer, worldHandleServer, handleServerHost, handleServerPort, handleServerApp, solrHost, solrPort )
	{
		def inputFile = new File( inputFileName );

		def outputFile = new File( inputFileName+".tmp" );
		
		outputFile.write("") // If file already exists, remove any previous contents

		List lines = inputFile.readLines()

		def boolean madeChange = false
		for ( String line in lines )
		{ 
			if ( line.startsWith( FedoraAppConstants.ISLANDORA_INGEST_PROPERTY + EQUAL ) )
			{
				if ( islandora.equalsIgnoreCase( T ) )
				{
					line = FedoraAppConstants.ISLANDORA_INGEST_PROPERTY + EQ_TRUE
				}
				else
				{
					line = FedoraAppConstants.ISLANDORA_INGEST_PROPERTY + EQ_FALSE
				}
				madeChange = true
			}
			
			if ( line.contains( FedoraAppConstants.BATCH_INGEST_DISABLE_GET_HANDLE_PROPERTY + EQUAL ) )
			{
				if ( disableHandleServer.equalsIgnoreCase( T ) )
				{
					line = FedoraAppConstants.BATCH_INGEST_DISABLE_GET_HANDLE_PROPERTY + EQ_ANYTHING
				}
				else
				{
					line = "#"+FedoraAppConstants.BATCH_INGEST_DISABLE_GET_HANDLE_PROPERTY + EQ_ANYTHING
				}
				madeChange = true
			}
			
			if ( line.startsWith( FedoraAppConstants.FEDORA_HOST_PROPERTY + EQUAL ) )
			{
				line = FedoraAppConstants.FEDORA_HOST_PROPERTY + ( fedoraHost.equals(NONE) ? EQUAL : EQUAL + fedoraHost )
				madeChange = true
			}
			
			
			if ( line.startsWith( FedoraAppConstants.FEDORA_HOST_PROPERTY + EQUAL ) )
			{
				line = FedoraAppConstants.FEDORA_HOST_PROPERTY + ( fedoraHost.equals(NONE) ? EQUAL : EQUAL + fedoraHost )
				madeChange = true
			}
			if ( line.startsWith( FedoraAppConstants.FEDORA_PORT_PROPERTY + EQUAL ) )
			{
				line = FedoraAppConstants.FEDORA_PORT_PROPERTY + ( fedoraPort.equals( NONE ) ? EQUAL : EQUAL + fedoraPort )
				madeChange = true
			}
			if ( line.startsWith( FedoraAppConstants.FEDORA_USER_PROPERTY + EQUAL ) )
			{
				line = FedoraAppConstants.FEDORA_USER_PROPERTY + ( newUser.equals( NONE ) ? EQUAL : EQUAL + newUser )
				madeChange = true
			}
			if ( line.startsWith( FedoraAppConstants.FEDORA_PWD_PROPERTY + EQUAL ) )
			{
				line = FedoraAppConstants.FEDORA_PWD_PROPERTY + ( newPassword.equals( NONE ) ? EQUAL : EQUAL + newPassword )
				madeChange = true
			}

			if ( line.startsWith( FedoraProxyConstants.FedoraProxy_FEDORA_USER_PROPERTY + EQUAL ) )
			{
				line = FedoraProxyConstants.FedoraProxy_FEDORA_USER_PROPERTY + ( fpNewUser.equals( NONE ) ? EQUAL : EQUAL + fpNewUser )
				madeChange = true
			}
			if ( line.startsWith( FedoraProxyConstants.FedoraProxy_FEDORA_PWD_PROPERTY + EQUAL ) )
			{
				line = FedoraProxyConstants.FedoraProxy_FEDORA_PWD_PROPERTY + ( fpNewPassword.equals( NONE ) ? EQUAL : EQUAL + fpNewPassword  )
				madeChange = true
			}


			if ( line.startsWith( FedoraAppConstants.BATCH_INGEST_WORLD_HANDLE_SERVER_PROPERTY + EQUAL ) )
			{
				line = FedoraAppConstants.BATCH_INGEST_WORLD_HANDLE_SERVER_PROPERTY + ( worldHandleServer.equals( NONE ) ? EQUAL : EQUAL + worldHandleServer )
				madeChange = true
			}
			if ( line.startsWith( FedoraAppConstants.BATCH_INGEST_HANDLE_SERVER_PROPERTY + EQUAL ) )
			{
				line = FedoraAppConstants.BATCH_INGEST_HANDLE_SERVER_PROPERTY + ( handleServerHost.equals( NONE ) ? EQUAL : EQUAL + handleServerHost )
				madeChange = true
			}
			if ( line.startsWith( FedoraAppConstants.BATCH_INGEST_HANDLE_SERVER_PORT_PROPERTY + EQUAL ) )
			{
				line = FedoraAppConstants.BATCH_INGEST_HANDLE_SERVER_PORT_PROPERTY + ( handleServerPort.equals( NONE ) ? EQUAL : EQUAL + handleServerPort  )
				madeChange = true
			}
			if ( line.startsWith( FedoraAppConstants.BATCH_INGEST_HANDLE_SERVER_APP_PROPERTY + EQUAL ) )
			{
				line = FedoraAppConstants.BATCH_INGEST_HANDLE_SERVER_APP_PROPERTY + ( handleServerApp.equals( NONE ) ? EQUAL : EQUAL + handleServerApp )
				madeChange = true
			}
			if ( line.startsWith( FedoraProxyConstants.SOLR_HOST_PROPERTY + EQUAL ) )
			{
				line = FedoraProxyConstants.SOLR_HOST_PROPERTY + ( solrHost.equals( NONE ) ? EQUAL : EQUAL + solrHost )
				madeChange = true
			}
			if ( line.startsWith( FedoraProxyConstants.SOLR_PORT_PROPERTY + EQUAL ) )
			{
				line = FedoraProxyConstants.SOLR_PORT_PROPERTY + ( solrPort.equals( NONE ) ? EQUAL : EQUAL + solrPort )
				madeChange = true
			}
			
			if ( line.startsWith( FedoraAppConstants.BATCH_INGEST_TOP_FOLDER_URL_PROPERTY + EQUAL ) )
			{
				line = FedoraAppConstants.BATCH_INGEST_TOP_FOLDER_URL_PROPERTY + ( batchSpaceDirectory.equals( NONE ) ? EQUAL : EQUAL + "file://localhost"+batchSpaceDirectory )
				madeChange = true
			}

			outputFile << line+"\n"
		
		} // for
		
		
		if ( madeChange )
		{
			println "Updated "+inputFile
			inputFile.delete()
			outputFile.renameTo( inputFileName )
		}
		else {
			outputFile.delete()
		}
			
	}


	static def substituteMail( File inputFile, successEmail, failureEmail, smtpServer, smtpPort, smtpUser, smtpPassword, fromEmail, sslEmail   )
	{
		def outputFile = new File( inputFile.getAbsolutePath() +".tmp" )

		outputFile.write("") // If file already exists, remove any previous contents

		List lines = inputFile.readLines()

		lines = inputFile.readLines()
		def boolean madeChange = false
		for ( String line in lines )
		{
			if ( line.startsWith( FedoraAppConstants.REMOTE_SUCCESS_EMAIL_PROPERTIES + EQUAL ) )
			{
				line = FedoraAppConstants.REMOTE_SUCCESS_EMAIL_PROPERTIES + ( successEmail.equals( NONE ) ? EQUAL : EQUAL + successEmail )
				madeChange = true;
				
			}
			if ( line.startsWith( FedoraAppConstants.REMOTE_FAILURE_EMAIL_PROPERTIES + EQUAL ) )
			{
				line = FedoraAppConstants.REMOTE_FAILURE_EMAIL_PROPERTIES + ( failureEmail.equals( NONE ) ? EQUAL : EQUAL + failureEmail )
				madeChange = true;
			}
			if ( line.startsWith( FedoraAppConstants.REMOTE_SMTP_SERVER_PROPERTY + EQUAL ) )
			{
				line = FedoraAppConstants.REMOTE_SMTP_SERVER_PROPERTY + ( smtpServer.equals( NONE ) ? EQUAL : EQUAL + smtpServer )
				madeChange = true;
			}
			if ( line.startsWith( FedoraAppConstants.REMOTE_SMTP_SERVER_PORT_PROPERTY + EQUAL ) )
			{
				line = FedoraAppConstants.REMOTE_SMTP_SERVER_PORT_PROPERTY + ( smtpPort.equals( NONE ) ? EQUAL : EQUAL + smtpPort ) 
				madeChange = true;
			}
			if ( line.startsWith( FedoraAppConstants.REMOTE_SMTP_SERVER_USER_PROPERTY + EQUAL ) )
			{
				line = FedoraAppConstants.REMOTE_SMTP_SERVER_USER_PROPERTY + ( smtpUser.equals( NONE ) ? EQUAL : EQUAL + smtpUser )
				madeChange = true;
			}
			if ( line.startsWith( FedoraAppConstants.REMOTE_SMTP_SERVER_PWD_PROPERTY + EQUAL ) )
			{
				line = FedoraAppConstants.REMOTE_SMTP_SERVER_PWD_PROPERTY + ( smtpPassword.equals( NONE ) ? EQUAL : EQUAL + smtpPassword )
				madeChange = true;
			}
			
			if ( line.startsWith( FedoraAppConstants.REMOTE_EMAIL_FROM_ADDRESS_PROPERTIES + EQUAL ) )
			{
				line = FedoraAppConstants.REMOTE_EMAIL_FROM_ADDRESS_PROPERTIES + ( fromEmail.equals( NONE ) ? EQUAL : EQUAL + fromEmail )
				madeChange = true
			}
				
			if ( line.startsWith( FedoraAppConstants.REMOTE_SMTP_SERVER_SSL_PROPERTY + EQUAL ) )
			{
				line = FedoraAppConstants.REMOTE_SMTP_SERVER_SSL_PROPERTY + ( sslEmail.equals( NONE ) ? EQUAL : EQUAL + sslEmail )
				madeChange = true
			}
			
			outputFile << line+"\n"
		}
		
		if ( madeChange )
		{	
			println "Updated "+inputFile
			String inputFileName = inputFile.getAbsolutePath();
			inputFile.delete()
			outputFile.renameTo( inputFileName )
		}
		else {
			outputFile.delete()
		}

	} // substitueMail



} // class
