/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ws.nzen.format.eno.Parser.Word;
import static ws.nzen.format.eno.Syntaxeme.*;

/**  */
public class Semantologist
{
	private static final String cl = "s.";
	private List<List<Word>> parsedLines;
	private Map<String, EnoElement> fieldNames = new HashMap<>();
	private Map<String, Section> sectionNames = new HashMap<>();
	private int lineChecked = 0;


	public Section analyze( List<String> fileLines )
	{
		parsedLines = new Parser().parse( fileLines );
		

		return null; // TODO
	}


	public void foolAround()
	{
		final String here = cl +"fa ";
		List<String> file = new ArrayList<>();
		file.add( "# banana" );
		file.add( " ## faf anna " );
		file.add( " #=-- \\//" );

		parsedLines = new Parser().parse( file );
		int sectionDepth = 0;
		int inLineInd = 0;
		List<Word> line = parsedLines.get( lineChecked );
		if ( line.isEmpty() )
		{
			throw new RuntimeException( here +"every line should have something" );
		}
		Word first = line.get( inLineInd );
		if ( first.type == SECTION )
		{
			inLineInd++;
			Word name = line.get( inLineInd );
			Section currElem = new Section( name.value, name.modifier );
			if ( currElem.getDepth() > sectionDepth +1 )
			{
				// FIX use canon complaint
				throw new RuntimeException( here +"section depth jumped too fa at "+ lineChecked );
			}
			sectionNames.put( currElem.getName(), currElem );
			/*
			get comments until we find a non comment (skip past empty, if need be
			handle template
			go deeper into something that returns the children ?
			*/
		}
	}


	private List<EnoElement> sectionInterior()
	{
		return null; // TODO
	}

}
























































