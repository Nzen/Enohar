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
	private List<EnoElement> fields = new ArrayList<>();
	private List<Section> sections = new ArrayList<>();
	private int lineChecked = 0;
	private int wordIndOfLine = 0;


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
			/*
			get comments until we find a non comment (skip past empty, if need be
			handle template
			go deeper into something that returns the children ?
			*/
		}
	}


	/*
	private List words;
	private int lineNumber;
	private int wordIndOfLine;
	private List fields;
	private List sections; // or map<string, list<section> >
	

	 private Section analyze( L<S> )
	 {
	 	build document
	 	resolve forward references
	 }

	private Section buildDocument()
	{
		Section theDocument = new Section();
		EnoElement currElem;
		lineNumber = -1;
		while( advanceLine() )
		{
			String firstComment = getPreceedingComment();
			if ( ! firstComment.isEmpty() )
				advanceLine();
			if ( wordOfLine is section )
				if section depth > 1
					canon complaint about depth
				else
				currElem = section( firstComment )
			else if wordOfLine is field
				currElem = field( firstComment )
			else if wordOfLine is multi boundary
				currElem = multiLine( firstComment )
			else if wordOfLine is comment
				theDocument.addComment( word.trim );
				continue;
			else
				throw new RuntimeException( use canon complaint about
					orphan fieldset, list, unknown thing )
			if ( currElem != null )
				theDocument.addElement( currElem )
		}
	}

	private String getPreceedingComment()
	{
		if first non empty element of actual current line is not comment
		  return blank
		advance past contiguous comments to non comment or end
		if next elment starts with empty or there isn't a non comment
			return blank
		else
			save the list line range
			// find the common whitespace, le sigh, probably exact, not 'number of spaces'
			Lexer kindergartener = new Lexer();
			Set<C> whitespace = new TreeSet<>();
			whitespace.add( space tab );
			NaiveTrie notDynamic = new NaiveTrie( whitespace )
			 for ( L<P.W> line in range )
				kg.lex( line )
				l.t first = kg.nextToken();
				if ( first.type == space )
					notDynamic.add( first.word );
			String commentPrefix = notDynamic.longestCommonPrefix();
			StringBuilder wholeComment
			for ( L<P.W> line in range )
				wC.append( commentPrefix );
				wC.append( line.get( comment.word.trim() ) )
			return wC.toString()
	}

	/ will handle getting own children
	private Section section( String preceedingComment )
	{
		// if wordOfLine past empty is not section, return null
		// no need to track prevailing depth, only get child if the depth is +1; hmm, who complains ?
		Section currSection = new Section()
		if ( ! preceedingComment.isEmpty() )
			currSection.addComment( preceedingComment );
			currSection.set first comment preceeds;
		if ( advanceWordOfLine() and wordOfLine is copy op )
			if find name in sections
				// initially copy naively, independent of copy type, later, reach for the children
			else complain
		// NOTE get children
		EnoElement currElem;
		while( advanceLine() )
		{
			String firstComment = getPreceedingComment();
			if ( ! firstComment.isEmpty() )
				advanceLine();
			if ( wordOfLine is section
				and section depth == currSection +1 )
					currElem = section( firstComment )
				else if sec depth > currSection +1
					canon complaint about section depth advancement
				else
					break, sibling or uncle/aunt
			else if wordOfLine is field
				currElem = field( firstComment )
			else if wordOfLine is multi boundary
				currElem = multiLine( firstComment )
			else if wordOfLine is comment
				currSection.addComment( word.trim );
				continue;
			else
				throw new RuntimeException( use canon complaint about
					orphan fieldset, list, unknown thing )
			if ( currElem != null )
				currSection.addElement( currElem )
		}
		return currSection;
	}

	private Field field( preceedingComment )
	{
		fill immediate values, decide initial type
		find children, first non comment corroborates type, mix provokes complaint
		um maybe let each get a preceeding comment, so list items have comment and so on
		return currField;
	}

	*/


	private boolean advanceLine()
	{
		if ( lineChecked < parsedLines.size() )
		{
			lineChecked++;
			wordIndOfLine = 0;
			return true;
		}
		else
		{
			return false;
		}
	}

}
























































