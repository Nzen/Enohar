/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import ws.nzen.format.eno.parse.Parser;

/** A class for testing outside of junit */
public class Example
{
	private static final String cl = "t.";

	/** @param args */
	public static void main( String[] args )
	{
		checkHardcodedDocument();
		checkCanonFiles();
	}


	static void checkHardcodedDocument()
	{
		List<String> file = new ArrayList<>();
		file.add( "# banana" );
		file.add( " faf:  anna " );
		file.add( "`bro ken` < comment" );
		file.add( "> comment" );
		// file.add( "-222" );
		file.add( "-- `bello`" );
		file.add( " #=-- \\//" );
		file.add( "" );
		file.add( "-- bello" );
		file.add( " fef: " );
		file.add( "-eve" );
		file.add( " - adam" );
		file.add( " fif:" );
		file.add( " gib= ana " );
		file.add( " gyb= nna " );
		file.add( " fof:  anna " );
		file.add( "> comment" );
		file.add( " | large " );
		file.add( "> comment" );
		file.add( " \\ large " );
		Parser epp = new Parser();
		epp.parse( file );
	}


	static void checkCanonFiles()
	{
		String here = cl +"fi ";
		Parser epp = new Parser();
		try
		{
			DirectoryStream<Path> filePipe;
			Path draftRoot = Paths.get( "usr" );
			Queue<Path> waitingRoom = new ArrayDeque<>();
			waitingRoom.add( draftRoot );
			while ( ! waitingRoom.isEmpty() )
			{
				filePipe = Files.newDirectoryStream( waitingRoom.poll() );
				for ( Path visitee : filePipe )
				{
					if ( Files.isDirectory( visitee ) )
					{
						waitingRoom.add( visitee );
						// continue;
					}
					else if ( Files.isRegularFile( visitee )
							&& visitee.getFileName().toString().endsWith( ".eno" ) )
					{
						System.out.println( "\t"+ here +"trying to recognize "+ visitee );
						for ( List<Parser.Word> line : epp.parse( Files.readAllLines( visitee ) ) )
						{
							for ( Parser.Word aWord : line )
							{
								System.out.println( "\t"+ here + aWord );
							}
							System.out.println( "\t"+ here +"(next line)" );
						}
					}
					else
					{
						System.out.println( here +"ignoring "+ visitee );
					}
				}
			}
		}
		catch ( InvalidPathException | IOException ie )
		{
			System.err.println( here + ie );
		}
	}

}
























































