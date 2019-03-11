/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import static ws.nzen.format.eno.Syntaxeme.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import ws.nzen.format.eno.Parser.Word;

/**  */
public class Semantologist
{
	private static final String cl = "s.";
	private List<List<Word>> parsedLines;
	private List<EnoElement> fields = new ArrayList<>();
	private List<Section> sections = new ArrayList<>();
	private List<Dependence> transitiveFields = new LinkedList<>();
	private List<Dependence> transitiveSections = new LinkedList<>();
	private int lineChecked = 0;
	private int wordIndOfLine = 0;

	private class Dependence
	{
		EnoElement hasReference = null;
		EnoElement isReferredTo = null;
		// minimum needed in case this is a forward reference
		String nameOfReferredTo = "";
	}


	public Semantologist()
	{
	}


	@Deprecated
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


	public Section analyze( List<String> fileLines )
	{
		parsedLines = new Parser().parse( fileLines );
		Section entireResult = buildDocument();
		resolveForwardReferences();
		return entireResult;
	}


	private Section buildDocument()
	{
		Section theDocument = new Section();
		EnoElement currElem = null;
		lineChecked = -1;
		int sectionDepth = 0;
		while ( advanceLine() )
		{
			String firstComment = getPreceedingComment();
			// ASK already dissonant about line number here, perhaps subtract or have gPrecComm() use do while
			if ( ! firstComment.isEmpty() )
				advanceLine();
			List<Word> line = parsedLines.get( lineChecked );
			Word currWord = line.get( wordIndOfLine );
			if ( currWord.type == EMPTY )
			{
				wordIndOfLine++;
				currWord = line.get( wordIndOfLine );
			}
			switch ( currWord.type )
			{
				case SECTION :
				{
					currElem = section( firstComment, sectionDepth );
					break;
				}
				case FIELD :
				{
					currElem = field( firstComment );
					break;
				}
				case MULTILINE_BOUNDARY :
				{
					currElem = multiline( firstComment );
					break;
				}
				case COMMENT :
				{
					if ( currElem == null )
					{
						theDocument.addComment( currWord.value.trim() );
					}
					else
					{
						currElem.addComment( currWord.value.trim() );
					}
					continue;
				}
				case VALUE :
				{
					MessageFormat problem = new MessageFormat(
							ExceptionStore.getStore().getExceptionMessage(
									ExceptionStore.ANALYSIS, EnoLocaleKey
										.MISSING_ELEMENT_FOR_CONTINUATION ) );
					throw new RuntimeException( problem.format( new Object[]{ currWord.line } ) );
				}
				case LIST_ELEMENT :
				{
					MessageFormat problem = new MessageFormat(
							ExceptionStore.getStore().getExceptionMessage(
									ExceptionStore.ANALYSIS, EnoLocaleKey
										.MISSING_NAME_FOR_LIST_ITEM ) );
					throw new RuntimeException( problem.format( new Object[]{ currWord.line } ) );
				}
				case SET_ELEMENT :
				{
					MessageFormat problem = new MessageFormat(
							ExceptionStore.getStore().getExceptionMessage(
									ExceptionStore.ANALYSIS, EnoLocaleKey
										.MISSING_NAME_FOR_FIELDSET_ENTRY ) );
					throw new RuntimeException( problem.format( new Object[]{ currWord.line } ) );
				}
				case MULTILINE_TEXT :
				case COPY :
				default :
				{
					MessageFormat problem = new MessageFormat(
							ExceptionStore.getStore().getExceptionMessage(
									ExceptionStore.TOKENIZATION,
									EnoLocaleKey.INVALID_LINE ) );
					// NOTE likely a Parser implementation problem, not user error
					throw new RuntimeException( problem.format( new Object[]{ currWord.line } ) );
				}
			}
			if ( currElem != null ) // ASK do I need this ?
			{
				theDocument.addChild( currElem );
				// ASK I'm assuming section handles dependence
			}
		}
		return theDocument;
	}


	private Section section( String firstComment, int parentDepth )
	{
		
		/*
			-- from above
					if ( currWord.modifier > sectionDepth +1 )
					{
						MessageFormat problem = new MessageFormat(
							ExceptionStore.getStore().getExceptionMessage(
									ExceptionStore.ANALYSIS, EnoLocaleKey.SECTION_HIERARCHY_LAYER_SKIP ) );
						throw new RuntimeException( problem.format( new Object[]{ currWord.line } ) );
					}
					else
					{
						int currSecDepth = currWord.modifier;
						wordIndOfLine++;
						currWord = line.get( wordIndOfLine );
						if ( currWord.type != FIELD )
						{
							MessageFormat problem = new MessageFormat(
							ExceptionStore.getStore().getExceptionMessage(
									ExceptionStore.ANALYSIS, EnoLocaleKey.MISSING_SECTION ) );
							throw new RuntimeException( problem.format( new Object[]{ "name" } ) );
							// NOTE likely a Parser implementation problem, not user error
						}
						currElem = new Section( currWord.value, currWord.modifier );
						currElem.addComment( firstComment );
						currElem.setFirstCommentPreceededName( true );
					}

				== pseudo

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
		*/
		return null; // FIX todo
	}


	private EnoElement field( String preceedingComment )
	{
		/*
		fill immediate values, decide initial type
		find children, first non comment corroborates type, mix provokes complaint
		um maybe let each get a preceeding comment, so list items have comment and so on
		return currField;
		*/
		return null; // FIX todo
	}


	private EnoElement multiline( String preceedingComment )
	{
		wordIndOfLine = 0;
		List<Word> line = parsedLines.get( lineChecked );
		Word currWord = line.get( wordIndOfLine );
		int emptyLines = 0;
		if ( currWord.type == EMPTY )
		{
			emptyLines = currWord.modifier;
			wordIndOfLine++;
			currWord = line.get( wordIndOfLine );
		}
		if ( currWord.type != MULTILINE_BOUNDARY )
		{
			MessageFormat problem = new MessageFormat(
					ExceptionStore.getStore().getExceptionMessage(
							ExceptionStore.TOKENIZATION,
							EnoLocaleKey.INVALID_LINE ) );
			// NOTE likely a Parser implementation problem, not user error
			throw new RuntimeException( problem.format( new Object[]{ currWord.line } ) );
		}
		int boundaryHyphens = currWord.modifier;
		wordIndOfLine++;
		currWord = line.get( wordIndOfLine );
		if ( currWord.type != FIELD )
		{
			MessageFormat problem = new MessageFormat(
					ExceptionStore.getStore().getExceptionMessage(
							ExceptionStore.TOKENIZATION,
							EnoLocaleKey.INVALID_LINE ) );
			// NOTE likely a Parser implementation problem, not user error
			throw new RuntimeException( problem.format( new Object[]{ currWord.line } ) );
		}
		Multiline currElem = new Multiline( currWord.value, currWord.modifier );
		currElem.setPreceedingEmptyLines( emptyLines );
		currElem.setBoundaryLength( boundaryHyphens );
		wordIndOfLine++;
		currWord = line.get( wordIndOfLine );
		if ( currWord.type != MULTILINE_TEXT )
		{
			MessageFormat problem = new MessageFormat(
					ExceptionStore.getStore().getExceptionMessage(
							ExceptionStore.TOKENIZATION,
							EnoLocaleKey.INVALID_LINE ) );
			// NOTE likely a Parser implementation problem, not user error
			throw new RuntimeException( problem.format( new Object[]{ currWord.line } ) );
		}
		currElem.setValue( currWord.value );
		// NOTE look for succeeding comments
		
		/*
		get child comments up to a preceeding comment or different element
		*/
		fields.add( currElem );
		return null; // FIX todo
	}


	/** Copy contiguous, immediately-preceeding comments into a block
	 * with the minimum common whitespace, blank otherwise.
	 * Loses the preceeding empty line count. */
	private String getPreceedingComment()
	{
		List<String> comments = new ArrayList<>();
		boolean firstTime = true;
		while ( advanceLine() )
		{
			advanceLine();
			List<Word> line = parsedLines.get( lineChecked );
			if ( line.get( wordIndOfLine ).type == EMPTY )
			{
				if ( firstTime )
				{
					// NOTE only allow empty to preceed this comment block, not separate it
					wordIndOfLine++;
					firstTime = false;
				}
				else
				{
					break;
				}
			}
			if ( line.get( wordIndOfLine ).type == COMMENT )
			{
				comments.add( line.get( wordIndOfLine ).value );
			}
			else
			{
				break;
			}
		}
		if ( comments.isEmpty() || lineChecked == parsedLines.size() )
		{
			// NOTE didn't find any or separated or last comments of document
			return "";
		}
		Set<Character> whitespace = new TreeSet<>();
		whitespace.add( Character.valueOf( ' ' ) );
		whitespace.add( Character.valueOf( '\t' ) );
		NaiveTrie sequenceAware = new NaiveTrie( whitespace );
		Lexer reader = new Lexer();
		String commonPrefix = null;
		for ( String entire : comments )
		{
			reader.setLine( entire );
			Lexer.Token first = reader.nextToken();
			if ( first.type != Lexeme.WHITESPACE )
			{
				commonPrefix = "";
				break;
			}
			else
			{
				sequenceAware.add( first.word );
			}
		}
		if ( commonPrefix == null )
		{
			commonPrefix = sequenceAware.longestCommonPrefix();
		}
		StringBuilder wholeBlock = new StringBuilder( comments.size() * 10 );
		for ( String entire : comments )
		{
			wholeBlock.append( commonPrefix );
			wholeBlock.append( entire.trim() );
			wholeBlock.append( System.lineSeparator() );
		}
		return wholeBlock.substring( 0, wholeBlock.length()
				- System.lineSeparator().length() ); // NOTE remove trailing \n
	}


	private Syntaxeme nextLineType()
	{
		int nextLineInd = lineChecked +1;
		if ( nextLineInd >= parsedLines.size() )
		{
			return EMPTY;
		}
		int wordInd = 0;
		List<Word> line = parsedLines.get( wordInd );
		if ( line.isEmpty() )
		{
			// warn about nonstandard parser line
		}
		/*
		if list is empty, continue, nonstandard parser
		if the first of it is empty, check next word
		if not comment, return that
		if comment iterate until a line starts with empty or noncomment
			if empty return comment, else return noncomment
		*/
		return null; // TODO
	}


	private void resolveForwardReferences()
	{
		// FIX todo
	}


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
























































