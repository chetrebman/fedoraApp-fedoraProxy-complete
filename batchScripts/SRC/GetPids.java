import org.apache.axis.types.NonNegativeInteger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
 
import edu.du.penrose.systems.exceptions.FatalException;
import edu.du.penrose.systems.fedoraApp.util.FedoraAppUtil;

public class GetPids {
	/**
	 * @param args
	 * @throws FatalException 
	 * @throws NumberFormatException 
	 */
	public static void main(String[] args) throws Exception {

		if ( args.length < 6 )
		{
			System.out.println( "\n\nUsage: host port user password fedoraContext numPids \n\n" );
			return;
		}

		String host = args[0];
		String port = args[1];
		String userName = args[2];
		String password = args[3];
		String fedoraContext = args[4];
		String numPids = args[5];

// switched to append sept 3 12
//      File outFile = new File( "reservedPids.txt" );		
//		if ( outFile.exists() ){
//			System.out.println( "Error: reservedPids.txt already exists!!" );
//			return;
//		}
//		FileOutputStream fos = new FileOutputStream( outFile );
//		OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");

		  // Create file 
		FileWriter fstream = new FileWriter("reservedPids.txt",true);
		BufferedWriter out = new BufferedWriter(fstream);
		  
		SimpleDateFormat dateFormater = new SimpleDateFormat( "MMMMM dd, yyyy : kk:mm" );
		String header = null;
		if ( Integer.valueOf( numPids ) == 1 )
		{
			header =  numPids + "\n\nPid Reserved \t" + dateFormater.format( new Date() ) + "\n" ;
		}
		else 
		{
			header =  numPids + "\n\nPids Reserved \t" + dateFormater.format( new Date() ) + "\n" ;
		}
		
		System.out.println();
		out.write( header );

		String[] pids = FedoraAppUtil.getPIDs(  host, Integer.valueOf( port ).intValue(), userName, password, fedoraContext, new NonNegativeInteger( numPids ) );

		for ( int i =0; i<pids.length; i++)
		{
			String line = pids[i];
			System.out.println( line );
			out.write( line+"\n" );
		}		
		out.close();
	}

}
