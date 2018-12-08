/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import java.util.ArrayList;
import java.util.List;

/** A class for testing outside of junit */
public class Toss
{

	/** @param args */
	public static void main( String[] args )
	{
		List<String> file = new ArrayList<>();
		// file.add( "# banana" );
		// file.add( " fof:  anna " );
		file.add( "`bro ken` < comment" );
		file.add( "> comment" );
		// file.add( "-222" );
		file.add( "-- `bello`" );
		file.add( " #=-- \\//" );
		file.add( "" );
		file.add( "-- bello" );
		Parser epp = new Parser();
		// epp.parse( file );
		epp.recognize( file );
	}

}


















