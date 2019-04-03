/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno.parse;

import static ws.nzen.format.eno.EnoType.*;
import static ws.nzen.format.eno.parse.Syntaxeme.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import ws.nzen.format.eno.EnoElement;
import ws.nzen.format.eno.EnoLocaleKey;
import ws.nzen.format.eno.EnoType;
import ws.nzen.format.eno.ExceptionStore;
import ws.nzen.format.eno.Field;
import ws.nzen.format.eno.FieldList;
import ws.nzen.format.eno.FieldSet;
import ws.nzen.format.eno.ListItem;
import ws.nzen.format.eno.Multiline;
import ws.nzen.format.eno.Section;
import ws.nzen.format.eno.SetEntry;
import ws.nzen.format.eno.Value;
import ws.nzen.format.eno.parse.Parser.Word;

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
		if ( first.type == Syntaxeme.SECTION )
		{
			inLineInd++;
			Word name = line.get( inLineInd );
			Section currElem = new Section( name.value, name.modifier );
			if ( currElem.getDepth() > sectionDepth +1 )
			{
				// FIX use canon complaint
				throw new RuntimeException( here +"section depth jumped too far at "+ lineChecked );
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
				if ( wordIndOfLine >= line.size() )
				{
					// complain about nonstandard parser
					continue;
				}
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
			if ( currElem != null )
			{
				theDocument.addChild( currElem );
			}
		}
		return theDocument;
	}


	private Section section( String firstComment, int parentDepth )
	{
		List<Word> line = parsedLines.get( lineChecked );
		Word emptyLines = null;
		Word currElem = line.get( wordIndOfLine );
		if ( currElem.type == EMPTY )
		{
			emptyLines = currElem;
			wordIndOfLine++;
			currElem = line.get( wordIndOfLine );
		}
		if ( currElem.type != Syntaxeme.SECTION )
			throw new RuntimeException( "expected section" ); // assert paranoid
		int ownDepth = currElem.modifier;
		if ( ownDepth > parentDepth +1 )
		{
			MessageFormat problem = new MessageFormat(
					ExceptionStore.getStore().getExceptionMessage(
							ExceptionStore.ANALYSIS, EnoLocaleKey
								.SECTION_HIERARCHY_LAYER_SKIP ) );
			throw new RuntimeException( problem.format( new Object[]{ currElem.line } ) );
		}
		// else if same or less than parent, return null
		wordIndOfLine++;
		currElem = line.get( wordIndOfLine );
		Section container = new Section( currElem.value, currElem.modifier );
		if ( emptyLines.modifier > 0 )
		{
			container.setPreceedingEmptyLines( emptyLines.modifier );
		}
		if ( ! firstComment.isEmpty() )
		{
			container.setFirstCommentPreceededName( true );
			container.addComment( firstComment );
		}
		wordIndOfLine++;
		if ( wordIndOfLine < line.size() +1 )
		{
			currElem = line.get( wordIndOfLine );
			if ( currElem.type != COPY )
				throw new RuntimeException( "expected copy" ); // assert paranoid
			container.setShallowTemplate( currElem.modifier < 2 );
			wordIndOfLine++;
			currElem = line.get( wordIndOfLine );
			if ( currElem.type != FIELD )
				throw new RuntimeException( "expected name" ); // assert paranoid
			container.setTemplateName( currElem.value );
			container.setTemplateEscapes( currElem.modifier );
		}
		
		/*
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
				continue; or currElem = null
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


	private Field field( String preceedingComment )
	{
		EnoType fieldType = FIELD_EMPTY;
		List<Word> line = parsedLines.get( lineChecked );
		Word emptyLines = null;
		Word currElem = line.get( wordIndOfLine );
		if ( currElem.type == EMPTY )
		{
			emptyLines = currElem;
			wordIndOfLine++;
		}
		Word fieldName = line.get( wordIndOfLine );
		Field emptySelf = new Field( fieldName.value, fieldName.modifier );
		emptySelf.setLine( fieldName.line );
		if ( ! preceedingComment.isEmpty() )
		{
			emptySelf.addComment( preceedingComment );
			emptySelf.setFirstCommentPreceededName( true );
		}
		if ( emptyLines != null )
		{
			emptySelf.setPreceedingEmptyLines( emptyLines.modifier );
		}
		Value lineSelf = null;
		FieldList listSelf = null;
		FieldSet pairedSelf = null;
		// list ; set
		wordIndOfLine++;
		if ( line.size() > wordIndOfLine )
		{
			// do I have value here ?
			currElem = line.get( wordIndOfLine );
			if ( currElem.type == VALUE )
			{
				lineSelf = new Value( emptySelf );
				lineSelf.append( currElem.value );
				fieldType = FIELD_VALUE;
			}
			else if ( currElem.type == COPY )
			{
				emptySelf.setShallowTemplate( currElem.modifier == 1 );
				wordIndOfLine++;
				if ( line.size() > wordIndOfLine )
				{
					currElem = line.get( wordIndOfLine );
					if ( currElem.type == FIELD )
					{
						emptySelf.setTemplateName( currElem.value );
						emptySelf.setTemplateEscapes( currElem.modifier );
					}
					// else malformed line, complain about parser; expected field
				}
				// else malformed line, complain about parser; expected field after copy
			}
			// else malformed line, complain about parser; expected only value or copy
		}
		Value currChild = null;
		String docComment = "";
		boolean nonChild = false; // NOTE encountered sibling field or parent section
		while ( true )
		{
			Syntaxeme nextType = nextLineType();
			switch ( nextType )
			{
				case COMMENT :
				{
					lineChecked++;
					wordIndOfLine = 0;
					// Improve, maybe don't assume that this is well formed ?
					line = parsedLines.get( lineChecked );
					currElem = line.get( wordIndOfLine );
					if ( currElem.type == EMPTY )
					{
						wordIndOfLine++;
						currElem = line.get( wordIndOfLine );
					}
					if ( currElem.type == COMMENT )
					{
						if ( fieldType == FIELD_EMPTY )
						{
							emptySelf.addComment( currElem.value );
						}
						else if ( fieldType == FIELD_VALUE )
						{
							lineSelf.addComment( currElem.value );
						}
						else if ( fieldType == FIELD_SET
								|| fieldType == FIELD_LIST )
						{
							currChild.addComment( currElem.value );
						}
					}
					// else complain about next line type, or check next line
					break;
				}
				case VALUE :
				{
					lineChecked++;
					wordIndOfLine = 0;
					// Improve, maybe don't assume that this is well formed ?
					line = parsedLines.get( lineChecked );
					currElem = line.get( wordIndOfLine );
					if ( currElem.type == EMPTY )
					{
						wordIndOfLine++;
						currElem = line.get( wordIndOfLine );
					}
					if ( currElem.type == VALUE )
					{
						String continuation = ( currElem.modifier == Parser
								.WORD_MOD_CONT_EMPTY ) ? "" : " ";
						if ( fieldType == FIELD_EMPTY )
						{
							lineSelf = new Value( emptySelf );
							lineSelf.append( currElem.value );
							fieldType = FIELD_VALUE;
						}
						else if ( fieldType == FIELD_VALUE )
						{
							lineSelf.append( continuation + currElem.value );
						}
						else if ( fieldType == FIELD_LIST
								|| fieldType == FIELD_SET )
						{
							currChild.append( continuation + currElem.value );
						}
					}
					break;
				}
				case LIST_ELEMENT :
				{
					docComment = getPreceedingComment();
					wordIndOfLine = 0;
					// Improve, maybe don't assume that this is well formed ?
					line = parsedLines.get( lineChecked );
					currElem = line.get( wordIndOfLine );
					if ( currElem.type == EMPTY )
					{
						emptyLines = currElem;
						wordIndOfLine++;
						currElem = line.get( wordIndOfLine );
					}
					if ( fieldType == FIELD_EMPTY )
					{
						fieldType = FIELD_LIST;
						listSelf = new FieldList( emptySelf );
						currChild = new ListItem( currElem.value );
						if ( ! docComment.isEmpty() )
						{
							currChild.addComment( docComment );
							currChild.setFirstCommentPreceededName( true );
						}
						currChild.setPreceedingEmptyLines( emptyLines.modifier );
						listSelf.addItem( (ListItem)currChild );
					}
					else if ( fieldType == FIELD_LIST )
					{
						currChild = new ListItem( currElem.value );
						currChild.setName( listSelf.getName() ); // per spec
						if ( ! docComment.isEmpty() )
						{
							currChild.addComment( docComment );
							currChild.setFirstCommentPreceededName( true );
						}
						currChild.setPreceedingEmptyLines( emptyLines.modifier );
						listSelf.addItem( (ListItem)currChild );
					}
					else if ( fieldType == FIELD_VALUE )
					{
						MessageFormat problem = new MessageFormat(
								ExceptionStore.getStore().getExceptionMessage(
										ExceptionStore.ANALYSIS, EnoLocaleKey
											.LIST_ITEM_IN_FIELD ) );
						throw new RuntimeException( problem.format( new Object[]{ currElem.line } ) );
					}
					else if ( fieldType == FIELD_SET )
					{
						MessageFormat problem = new MessageFormat(
								ExceptionStore.getStore().getExceptionMessage(
										ExceptionStore.ANALYSIS, EnoLocaleKey
											.LIST_ITEM_IN_FIELDSET ) );
						throw new RuntimeException( problem.format( new Object[]{ currElem.line } ) );
					}
					break;
				}
				case SET_ELEMENT :
				{
					docComment = getPreceedingComment();
					wordIndOfLine = 0;
					// Improve, maybe don't assume that this is well formed ?
					line = parsedLines.get( lineChecked );
					currElem = line.get( wordIndOfLine );
					if ( currElem.type == EMPTY )
					{
						wordIndOfLine++;
						currElem = line.get( wordIndOfLine );
					}
					if ( fieldType == FIELD_EMPTY )
					{
						fieldType = FIELD_SET;
						pairedSelf = new FieldSet( emptySelf );
						currChild = new SetEntry( currElem.value, currElem.modifier );
						wordIndOfLine++;
						currElem = line.get( wordIndOfLine );
						// if ( currElem.type != VALUE )
							// complain
						currChild.setStringValue( currElem.value );
						if ( ! docComment.isEmpty() )
						{
							currChild.addComment( docComment );
							currChild.setFirstCommentPreceededName( true );
						}
						currChild.setPreceedingEmptyLines( emptyLines.modifier );
						pairedSelf.addEntry( (SetEntry)currChild );
					}
					else if ( fieldType == FIELD_LIST )
					{
						MessageFormat problem = new MessageFormat(
								ExceptionStore.getStore().getExceptionMessage(
										ExceptionStore.ANALYSIS, EnoLocaleKey
											.FIELDSET_ENTRY_IN_LIST ) );
						throw new RuntimeException( problem.format( new Object[]{ currElem.line } ) );
					}
					else if ( fieldType == FIELD_VALUE )
					{
						MessageFormat problem = new MessageFormat(
								ExceptionStore.getStore().getExceptionMessage(
										ExceptionStore.ANALYSIS, EnoLocaleKey
											.FIELDSET_ENTRY_IN_FIELD ) );
						throw new RuntimeException( problem.format( new Object[]{ currElem.line } ) );
					}
					else if ( fieldType == FIELD_SET )
					{
						currChild = new SetEntry( currElem.value, currElem.modifier );
						wordIndOfLine++;
						currElem = line.get( wordIndOfLine );
						// if ( currElem.type != VALUE )
							// complain
						currChild.setStringValue( currElem.value );
						if ( ! docComment.isEmpty() )
						{
							currChild.addComment( docComment );
							currChild.setFirstCommentPreceededName( true );
						}
						currChild.setPreceedingEmptyLines( emptyLines.modifier );
						pairedSelf.addEntry( (SetEntry)currChild );
					}
					break;
				}
				default :
				{
					nonChild = true;
					break;
				}
			}
			if ( nonChild )
			{
				break;
			}
		}
		if ( fieldType == FIELD_LIST )
		{
			return listSelf;
		}
		else if ( fieldType == FIELD_VALUE )
		{
			return lineSelf;
		}
		else if ( fieldType == FIELD_SET )
		{
			return pairedSelf;
		}
		else
		{
			return emptySelf;
		}
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
		/*
		if list is empty, continue, nonstandard parser
		if the first of it is empty, check next word
		if not comment, return that
		if comment iterate until a line starts with empty or noncomment
			if empty return comment, else return noncomment
		*/
		boolean vettingComment = false, firstTime = true;
		int nextLineInd = lineChecked, wordInd = 0;
		Word currMeme = null;
		List<Word> line = null;
		while ( true )
		{
			nextLineInd += 1;
			if ( nextLineInd >= parsedLines.size() )
			{
				if ( vettingComment )
				{
					return COMMENT;
				}
				else
				{
					return EMPTY;
				}
			}
			wordInd = 0;
			line = parsedLines.get( wordInd );
			if ( line.isEmpty() )
			{
				// warn about nonstandard parser line
				continue;
			}
			while ( wordInd < line.size() )
			{
				currMeme = line.get( wordInd );
				if ( currMeme.type == Syntaxeme.EMPTY
						&& ! vettingComment )
				{
					wordInd++;
				}
				else if ( vettingComment )
				{
					return  Syntaxeme.COMMENT;
				}
				else
				{
					break;
				}
			}
			if ( wordInd >= line.size() )
			{
				continue;
			}
			if ( currMeme.type == COMMENT 
					&& firstTime )
			{
				vettingComment = true;
				firstTime = false;
				continue;
			}
			else if ( currMeme.type == COMMENT 
					&& vettingComment )
			{
				continue;
			}
			else
			{
				return currMeme.type;
			}
		}
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
























































