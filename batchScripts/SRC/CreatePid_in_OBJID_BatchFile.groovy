import org.apache.axis.types.NonNegativeInteger;

import edu.du.penrose.systems.fedoraApp.batchIngest.bus.FedoraAppBatchIngestController;
import edu.du.penrose.systems.fedoraApp.util.FedoraAppUtil;

class CreatePid_in_OBJID_BatchFIle {

	static main(args) 
	{
		if ( args.length < 7 )
		{
			println "\nUsage: inputBatchFile, outputBatchfile, fedoraHost, fedoraPort, fedoraUser, fedoraPassword, fedoraNameSpace\n"
			return
		}
		
		def inputFile = new File( args[0] );
		def outputFile = new File( args[1] );
		def fedoraHost = args[2]
		def fedoraPort = args[3]
		def fedoraUser = args[4]
		def fedoraPassword = args[5]
		def fedoraNameSpace = args[6]
		
		int portAsInt = Integer.valueOf(fedoraPort).intValue();
		
		println "\nInput batch file  "+args[0]
		println "Output batch file "+args[1] 
		
		List lines = inputFile.readLines()		
		int objIdCount = 0;
		for ( String line in lines )
		{
			if ( line.contains( " OBJID") )
			{
				objIdCount += 1;
			}
		}
		
		println "\n"+objIdCount+" OBJID's Found in the input file, reserving PIDs"
		
		
		String[] pids = FedoraAppUtil.getPIDs(  fedoraHost, Integer.valueOf( fedoraPort ).intValue(), fedoraUser, fedoraPassword, fedoraNameSpace, new NonNegativeInteger( String.valueOf(objIdCount) ) );

//		def pids = new Object[ objIdCount ]
//		pids[0]="codu:1"
//		pids[1]="codu:2"
//		pids[2]="codu:3"
//		pids[3]="codu:4"
//		pids[4]="codu:5"
//		pids[5]="codu:6"
//		pids[6]="codu:7"
		
		println  objIdCount+" PIDs Reserverd\nWriting new batch file using the reserved PIDs in the <mets:mets> OBJID attributes"
		
		lines = inputFile.readLines()
		objIdCount = 0;
		outputFile.write("")
		for ( String line in lines )
		{
			if ( line.toLowerCase().contains( "ingestcontrol") )
			{
				line = '<ingestControl command="A" type="pidInOBJID"/>'
				outputFile << line+"\n"
				continue;
			}
			
			if ( line.contains( "OBJID") )
			{
				line = line.replaceAll( /OBJID=\".*" /, "OBJID=\"${pids[objIdCount]}\" " )				
				objIdCount += 1;
			}
			
			outputFile << line+"\n"
		}
		
		println "Done!\n"
	}

}
