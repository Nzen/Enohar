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
	private List<List<Word>> parsedLines = new ArrayList<>();
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


	public Section analyze( List<String> fileLines )
	{
		fields.clear();
		transitiveFields.clear();
		sections.clear();
		transitiveSections.clear();
		parsedLines = new Parser().parse( fileLines );
		// improve reset()
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
		// TODO vet these lines again with nextLineType()
		while ( advanceLine() )
		{
			Syntaxeme focusType = peekAtNextLineType( true );
			String firstComment;
			Word currToken;
			if ( focusType == EMPTY )
			{
				break; // end of input; parser tells me how much trailing space to include
			}
			else if ( focusType == COMMENT )
			{
				currToken = popCurrentWordOfLine();
				if ( currToken == null )
					continue;
				else if ( currToken.type == EMPTY )
				{
					currToken = popCurrentWordOfLine(); // NOTE assuming well formed parser lines
					if ( currToken == null )
						continue; // assert paranoid
				}
				if ( currElem == null )
				{
					theDocument.addComment( currToken.value.trim() );
				}
				else
				{
					currElem.addComment( currToken.value.trim() );
				}
				continue;
			}
			firstComment = getPreceedingComment();
			currToken = popCurrentWordOfLine();
			if ( currToken == null )
				continue;
			else if ( currToken.type == EMPTY )
			{
				currToken = popCurrentWordOfLine(); // NOTE assuming well formed parser lines
				if ( currToken == null )
					continue; // assert paranoid
			}
			switch ( currToken.type )
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
				case VALUE :
				{
					MessageFormat problem = new MessageFormat(
							ExceptionStore.getStore().getExceptionMessage(
									ExceptionStore.ANALYSIS, EnoLocaleKey
										.MISSING_ELEMENT_FOR_CONTINUATION ) );
					throw new RuntimeException( problem.format( new Object[]{ currToken.line } ) );
				}
				case LIST_ELEMENT :
				{
					MessageFormat problem = new MessageFormat(
							ExceptionStore.getStore().getExceptionMessage(
									ExceptionStore.ANALYSIS, EnoLocaleKey
										.MISSING_NAME_FOR_LIST_ITEM ) );
					throw new RuntimeException( problem.format( new Object[]{ currToken.line } ) );
				}
				case SET_ELEMENT :
				{
					MessageFormat problem = new MessageFormat(
							ExceptionStore.getStore().getExceptionMessage(
									ExceptionStore.ANALYSIS, EnoLocaleKey
										.MISSING_NAME_FOR_FIELDSET_ENTRY ) );
					throw new RuntimeException( problem.format( new Object[]{ currToken.line } ) );
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
					throw new RuntimeException( problem.format( new Object[]{ currToken.line } ) );
				}
			}
			if ( currElem != null )
			{
				theDocument.addChild( currElem );
			}
		}
		return theDocument;
	}


	/** Save own info, save child elements, punt up
	 * when encounters a sibling or parent section. */
	private Section section( String firstComment, int parentDepth )
	{
		Word currWord = popCurrentWordOfLine();
		Word emptyLines = null;
		if ( currWord.type == null )
			throw new RuntimeException( "expected section" ); // assert paranoid
		else if ( currWord.type == EMPTY )
		{
			emptyLines = currWord;
			currWord = popCurrentWordOfLine();
		}
		Word sectionOperator;
		if ( currWord.type == null || currWord.type != Syntaxeme.SECTION )
			throw new RuntimeException( "expected section operator" ); // assert paranoid
		else
		{
			sectionOperator = currWord;
			currWord = popCurrentWordOfLine();
		}
		int ownDepth = sectionOperator.modifier;
		// NOTE checking if it's too deep
		if ( ownDepth > parentDepth +1 )
		{
			MessageFormat problem = new MessageFormat(
					ExceptionStore.getStore().getExceptionMessage(
							ExceptionStore.ANALYSIS, EnoLocaleKey
								.SECTION_HIERARCHY_LAYER_SKIP ) );
			throw new RuntimeException( problem.format( new Object[]{ currWord.line } ) );
		}
		else if ( ownDepth <= parentDepth )
		{
			// NOTE if a sibling or parent, let another level construct this section
			wordIndOfLine = 0;
			return null;
		}
		if ( currWord.type == null || currWord.type != FIELD )
			throw new RuntimeException( "expected section name" ); // assert paranoid
		else
		{
			currWord = popCurrentWordOfLine();
		}
		Section container = new Section( currWord.value, currWord.modifier );
		if ( emptyLines.modifier > 0 )
		{
			container.setPreceedingEmptyLines( emptyLines.modifier );
		}
		if ( ! firstComment.isEmpty() )
		{
			container.setFirstCommentPreceededName( true );
			container.addComment( firstComment );
		}
		if ( currWord != null && currWord.type == COPY )
		{
			container.setShallowTemplate( currWord.modifier < 2 );
			currWord = popCurrentWordOfLine();
			if ( currWord.type == null || currWord.type != FIELD )
				throw new RuntimeException( "expected template name" ); // assert paranoid
			container.setTemplateName( currWord.value );
			container.setTemplateEscapes( currWord.modifier );
		}
		// advanceLine();
		Syntaxeme nextType;
		EnoElement currChild = null;
		boolean addingChildren = true;
		while ( addingChildren )
		{
			nextType = peekAtNextLineType();
			switch ( nextType )
			{
				case EMPTY :
				{
					currChild = null;
					break;
				}
				case COMMENT :
				{
					advanceLine();
					currWord = popCurrentWordOfLine();
					if ( currWord.type == EMPTY )
					{
						// NOTE not keeping comments separated, else we'd need to save them as Value
						currWord = popCurrentWordOfLine();
					}
					if ( currWord.type == COMMENT )
					{
						container.addComment( currWord.value );
					}
					// else complain
					break;
				}
				case FIELD :
				{
					currChild = field( getPreceedingComment() );
					break;
				}
				case MULTILINE_BOUNDARY :
				{
					currChild = multiline( getPreceedingComment() );
					break;
				}
				case SECTION :
				{
					// NOTE ensuring we don't lose the preceeding comment
					int currLine = lineChecked;
					currChild = section( getPreceedingComment(), ownDepth );
					if ( currChild == null )
						lineChecked = currLine; // ASK vet that this is the right line to save
					break;
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
			if ( currWord != null )
			{
				container.addChild( currChild );
			}
			else
			{
				addingChildren = false;
			}
		}
		return container;
	}


	/** Save own information and relevant children. */
	private Field field( String preceedingComment )
	{
		EnoType fieldType = FIELD_EMPTY;
		Word emptyLines = null;
		Word currWord = popCurrentWordOfLine();
		if ( currWord.type == EMPTY )
		{
			emptyLines = currWord;
			currWord = popCurrentWordOfLine();
		}
		Word fieldName = null;
		if ( currWord.type != FIELD )
			throw new RuntimeException( "expected field name" ); // assert paranoid
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
		currWord = popCurrentWordOfLine();
		if ( currWord != null )
		{
			// NOTE expecting value or template
			if ( currWord.type == VALUE )
			{
				lineSelf = new Value( emptySelf );
				lineSelf.append( currWord.value );
				fieldType = FIELD_VALUE;
			}
			else if ( currWord.type == COPY )
			{
				emptySelf.setShallowTemplate( currWord.modifier < 2 );
				currWord = popCurrentWordOfLine();
				if ( currWord.type == null || currWord.type != FIELD )
					throw new RuntimeException( "expected template name" ); // assert paranoid
				
			}
			// else malformed line, complain about parser; expected only value or copy
		}
		Value currChild = null;
		String docComment = "";
		boolean nonChild = false; // NOTE encountered sibling field or parent section
		while ( true )
		{
			Syntaxeme nextType = peekAtNextLineType();
			switch ( nextType )
			{
				case EMPTY :
				{
					nonChild = true;
					break;
				}
				case COMMENT :
				{
					advanceLine();
					currWord = popCurrentWordOfLine();
					if ( currWord.type == EMPTY )
					{
						// NOTE not keeping comments separated, else we'd need to save them as Value
						currWord = popCurrentWordOfLine();
					}
					if ( currWord.type == COMMENT )
					{
						if ( fieldType == FIELD_EMPTY )
						{
							emptySelf.addComment( currWord.value );
						}
						else if ( fieldType == FIELD_VALUE )
						{
							lineSelf.addComment( currWord.value );
						}
						else if ( fieldType == FIELD_SET
								|| fieldType == FIELD_LIST )
						{
							currChild.addComment( currWord.value );
						}
					}
					// else complain
					break;
				}
				// NP here?
			}
		}
		// NP here
		
		List<Word> line = null;
		while ( true )
		{
			Syntaxeme nextType = peekAtNextLineType();
			switch ( nextType )
			{
				case VALUE :
				{
					lineChecked++;
					wordIndOfLine = 0;
					// Improve, maybe don't assume that this is well formed ?
					line = parsedLines.get( lineChecked );
					currWord = line.get( wordIndOfLine );
					if ( currWord.type == EMPTY )
					{
						wordIndOfLine++;
						currWord = line.get( wordIndOfLine );
					}
					if ( currWord.type == VALUE )
					{
						String continuation = ( currWord.modifier == Parser
								.WORD_MOD_CONT_EMPTY ) ? "" : " ";
						if ( fieldType == FIELD_EMPTY )
						{
							lineSelf = new Value( emptySelf );
							lineSelf.append( currWord.value );
							fieldType = FIELD_VALUE;
						}
						else if ( fieldType == FIELD_VALUE )
						{
							lineSelf.append( continuation + currWord.value );
						}
						else if ( fieldType == FIELD_LIST
								|| fieldType == FIELD_SET )
						{
							currChild.append( continuation + currWord.value );
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
					currWord = line.get( wordIndOfLine );
					if ( currWord.type == EMPTY )
					{
						emptyLines = currWord;
						wordIndOfLine++;
						currWord = line.get( wordIndOfLine );
					}
					if ( fieldType == FIELD_EMPTY )
					{
						fieldType = FIELD_LIST;
						listSelf = new FieldList( emptySelf );
						currChild = new ListItem( currWord.value );
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
						currChild = new ListItem( currWord.value );
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
						throw new RuntimeException( problem.format( new Object[]{ currWord.line } ) );
					}
					else if ( fieldType == FIELD_SET )
					{
						MessageFormat problem = new MessageFormat(
								ExceptionStore.getStore().getExceptionMessage(
										ExceptionStore.ANALYSIS, EnoLocaleKey
											.LIST_ITEM_IN_FIELDSET ) );
						throw new RuntimeException( problem.format( new Object[]{ currWord.line } ) );
					}
					break;
				}
				case SET_ELEMENT :
				{
					docComment = getPreceedingComment();
					wordIndOfLine = 0;
					// Improve, maybe don't assume that this is well formed ?
					line = parsedLines.get( lineChecked );
					currWord = line.get( wordIndOfLine );
					if ( currWord.type == EMPTY )
					{
						wordIndOfLine++;
						currWord = line.get( wordIndOfLine );
					}
					if ( fieldType == FIELD_EMPTY )
					{
						fieldType = FIELD_SET;
						pairedSelf = new FieldSet( emptySelf );
						currChild = new SetEntry( currWord.value, currWord.modifier );
						wordIndOfLine++;
						currWord = line.get( wordIndOfLine );
						// if ( currElem.type != VALUE )
							// complain
						currChild.setStringValue( currWord.value );
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
						throw new RuntimeException( problem.format( new Object[]{ currWord.line } ) );
					}
					else if ( fieldType == FIELD_VALUE )
					{
						MessageFormat problem = new MessageFormat(
								ExceptionStore.getStore().getExceptionMessage(
										ExceptionStore.ANALYSIS, EnoLocaleKey
											.FIELDSET_ENTRY_IN_FIELD ) );
						throw new RuntimeException( problem.format( new Object[]{ currWord.line } ) );
					}
					else if ( fieldType == FIELD_SET )
					{
						currChild = new SetEntry( currWord.value, currWord.modifier );
						wordIndOfLine++;
						currWord = line.get( wordIndOfLine );
						// if ( currElem.type != VALUE )
							// complain
						currChild.setStringValue( currWord.value );
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


	private String getPreceedingComment()
	{
		return getPreceedingComment( false );
	}


	/** Copy contiguous, immediately-preceding comments into a block
	 * with the minimum common whitespace, blank otherwise.
	 * Loses the preceeding empty line count. */
	private String getPreceedingComment( boolean startAtCurrentLine )
	{
		List<String> comments = new ArrayList<>();
		boolean firstTime = true, lineHasContent = false;
		int initialGlobalLineCursor = lineChecked;
		if ( startAtCurrentLine )
			lineChecked -= 1;
		Word currToken;
		if ( advanceLine() )
		{
			do
			{
				currToken = popCurrentWordOfLine();
				if ( currToken == null )
					continue;
				else if ( currToken.type == EMPTY )
				{
					currToken = popCurrentWordOfLine();
					if ( currToken == null )
						continue;
					// loop if paranoid, I'll assume we're well formed here
					else if ( currToken.type != EMPTY )
					{
						lineHasContent = true;
						break;
					}
				}
				else
				{
					lineHasContent = true;
					break;
				}
			}
			while ( advanceLine() );
			// NOTE either no document left or no comment
			if ( ! lineHasContent || currToken.type != COMMENT )
			{
				lineChecked = initialGlobalLineCursor;
				wordIndOfLine = 0; // ASK potentially save,restore ?
				return "";
			}
			boolean amAssociated = false;
			comments.add( currToken.value );
			while ( advanceLine() )
			{
				currToken = popCurrentWordOfLine();
				if ( currToken == null || currToken.type == EMPTY )
				{
					break;
				}
				else if ( currToken.type != COMMENT )
				{
					amAssociated = true;
					break;
				}
				else
				{
					comments.add( currToken.value );
				}
			}
			if ( ! amAssociated )
			{
				lineChecked = initialGlobalLineCursor;
				wordIndOfLine = 0; // ASK potentially save,restore ?
				return "";
			}
			else
			{
				wordIndOfLine = 0; // NOTE reset because we popped rather than peeked
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
		}
		else
		{
			lineChecked = initialGlobalLineCursor;
			wordIndOfLine = 0; // ASK potentially save,restore ?
			return "";
		}
	}


	private Syntaxeme peekAtNextLineType()
	{
		return peekAtNextLineType( false );
	}


	private Syntaxeme peekAtNextLineType( boolean inclusive )
	{
		/*
		if list is empty, continue, nonstandard parser input
		if the first of it is empty, check next word
		if not comment, return that
		if comment iterate until a line starts with empty or noncomment
			if empty return comment, else return noncomment
		*/
		boolean vettingComment = false, firstTime = true;
		int nextLineInd = lineChecked + (( inclusive ) ? -1: 0), wordInd = 0;
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


	/** next word or null if none left. Advances wordIndOfLine. */
	private Word popCurrentWordOfLine()
	{
		if (  lineChecked < parsedLines.size() )
		{
			List<Word> line = parsedLines.get( lineChecked );
			if ( wordIndOfLine < line.size() )
			{
				Word result = line.get( wordIndOfLine );
				wordIndOfLine++;
				return result;
			}
			else
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}


	/** next word or null if none left. Advances wordIndOfLine. */
	private Word popNextWordOfLine()
	{
		if (  lineChecked < parsedLines.size() )
		{
			List<Word> line = parsedLines.get( lineChecked );
			wordIndOfLine++;
			if ( wordIndOfLine < line.size() )
			{
				return line.get( wordIndOfLine );
			}
			else
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}


	private Word peekAtCurrentWordOfLine()
	{
		if (  lineChecked < parsedLines.size() )
		{
			List<Word> line = parsedLines.get( lineChecked );
			if ( wordIndOfLine < line.size() )
			{
				return line.get( wordIndOfLine );
			}
			else
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}


	private Word peekAtNextWordOfLine()
	{
		if (  lineChecked < parsedLines.size() )
		{
			List<Word> line = parsedLines.get( lineChecked );
			if ( wordIndOfLine +1 < line.size() )
			{
				return line.get( wordIndOfLine +1 );
			}
			else
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}


	/** Increment line checked; reset word ind of line,
	 * report if this is out of bounds. */
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
























































