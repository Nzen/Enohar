/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno.parse;

import static ws.nzen.format.eno.EnoType.*;
import static ws.nzen.format.eno.parse.Syntaxeme.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
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
	private List<Field> fields = new ArrayList<>();
	private List<Section> sections = new ArrayList<>();
	private List<Dependence> transitiveFields = new LinkedList<>();
	private List<Dependence> transitiveSections = new LinkedList<>();
	private int lineChecked = 0;
	private int wordIndOfLine = 0;

	private class Dependence
	{
		EnoElement hasReference = null;
		EnoElement isReferredTo = null;
		// minimum needed, in case this is a forward reference
		String nameOfReferredTo = "";
		int escapesOfReferredTo = 0;
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
		String here = cl +"bd ";
		Section theDocument = new Section();
		EnoElement currElem = null;
		lineChecked = -1;
		int sectionDepth = 0;
		// TODO vet these lines again with nextLineType()
		System.out.println( "\n"+ here +"starting at -1 ----" );
		int ind = 0;
		for (List<Word> line : parsedLines)
		{
			System.out.print( here +"doc L-"+ ind +", " );
			for (Word cw : line)
				System.out.print( " "+ cw.type );
			ind++;
			System.out.println();
		}
		while ( advanceLine() )
		{
			stdoutHistoryDebugger( here, "1", null, false );
			Syntaxeme focusType = peekAtNextLineType( -1 );
			stdoutHistoryDebugger( here, "2", focusType );
			String firstComment;
			Word currWord;
			if ( focusType == EMPTY )
			{
				break; // end of input; parser tells me how much trailing space to include
			}
			else if ( focusType == COMMENT )
			{
				stdoutHistoryDebugger( here, "3", null, false );
				currWord = popCurrentWordOfLine();
				stdoutHistoryDebugger( here, "4", currWord, true );
				if ( currWord == null )
					continue;
				else if ( currWord.type == EMPTY )
				{
					currWord = popCurrentWordOfLine(); // NOTE assuming well formed parser lines
					stdoutHistoryDebugger( here, "5", currWord, true );
					if ( currWord == null )
						continue; // assert paranoid
				}
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
			firstComment = getPreceedingComment();
			stdoutHistoryDebugger( here, "5 post-gpc", null, false );
			currWord = peekAtCurrentWordOfLine();
			if ( currWord == null )
				continue;
			else if ( currWord.type == EMPTY )
			{
				stdoutHistoryDebugger( here, "6", currWord, true );
				wordIndOfLine += 1;
				currWord = peekAtCurrentWordOfLine(); // NOTE assuming well formed parser lines
				stdoutHistoryDebugger( here, "7", currWord, true );
				if ( currWord == null )
					continue; // assert paranoid
				else
					wordIndOfLine -= 1; // NOTE reset word cursor
				stdoutHistoryDebugger( here, "8", currWord, true );
			}
			switch ( currWord.type )
			{
				case SECTION :
				{
					stdoutHistoryDebugger( here, "9", currWord, false );
					currElem = section( firstComment, sectionDepth );
					stdoutHistoryDebugger( here, "10", currWord, false );
					break;
				}
				case FIELD :
				{
					stdoutHistoryDebugger( here, "11", currWord, true );
					currElem = field( firstComment );
					stdoutHistoryDebugger( here, "12", currWord, true );
					break;
				}
				case MULTILINE_BOUNDARY :
				{
					stdoutHistoryDebugger( here, "13", currWord, true );
					currElem = multiline( firstComment );
					stdoutHistoryDebugger( here, "14", currWord, true );
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
		String here = cl +"s ";
		stdoutHistoryDebugger(here, "15", null, false);
		Word currWord = popCurrentWordOfLine();
		stdoutHistoryDebugger( here, "16", currWord, true );
		Word emptyLines = null;
		if ( currWord.type == null )
			throw new RuntimeException( "expected section" ); // assert paranoid
		else if ( currWord.type == EMPTY )
		{
			emptyLines = currWord;
			currWord = popCurrentWordOfLine();
			stdoutHistoryDebugger( here, "17", currWord, true );
		}
		Word sectionOperator;
		if ( currWord.type == null || currWord.type != Syntaxeme.SECTION )
			throw new RuntimeException( "expected section operator" ); // assert paranoid
		else
		{
			sectionOperator = currWord;
			currWord = popCurrentWordOfLine();
			stdoutHistoryDebugger( here, "18", currWord, true );
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
			stdoutHistoryDebugger( here, "19", currWord, false );
			return null;
		}
		if ( currWord.type == null || currWord.type != FIELD )
			throw new RuntimeException( "expected section name" ); // assert paranoid
		Section container = new Section( currWord.value, currWord.modifier );
		if ( emptyLines != null && emptyLines.modifier > 0 )
		{
			container.setPreceedingEmptyLines( emptyLines.modifier );
		}
		if ( ! firstComment.isEmpty() )
		{
			container.setFirstCommentPreceededName( true );
			container.addComment( firstComment );
		}
		container.setDepth( ownDepth );
		currWord = popCurrentWordOfLine();
		if ( currWord != null && currWord.type == COPY )
		{
			container.setShallowTemplate( currWord.modifier < 2 );
			currWord = popCurrentWordOfLine();
			stdoutHistoryDebugger( here, "21", currWord, true );
			if ( currWord.type == null || currWord.type != FIELD )
				throw new RuntimeException( "expected template name" ); // assert paranoid, parser should catch
			Dependence reference = new Dependence();
			reference.hasReference = container;
			reference.nameOfReferredTo = currWord.value;
			reference.escapesOfReferredTo = currWord.modifier;
		}
		sections.add( container );
		// ASK advanceLine();
		Syntaxeme nextType;
		EnoElement currChild = null;
		boolean addingChildren = true;
		while ( addingChildren )
		{
			nextType = peekAtNextLineType( 1 );
			stdoutHistoryDebugger( here, "22", nextType );
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
					stdoutHistoryDebugger( here, "23", currWord, false );
					currWord = popCurrentWordOfLine();
					stdoutHistoryDebugger( here, "24", currWord, true );
					if ( currWord.type == EMPTY )
					{
						// NOTE not keeping comments separated, else we'd need to save them as Value
						currWord = popCurrentWordOfLine();
						stdoutHistoryDebugger( here, "25", currWord, true );
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
					stdoutHistoryDebugger( here, "26", currWord, false );
					currChild = field( getPreceedingComment() );
					stdoutHistoryDebugger( here, "27", currWord, false );
					break;
				}
				case MULTILINE_BOUNDARY :
				{
					stdoutHistoryDebugger( here, "28", currWord, false );
					currChild = multiline( getPreceedingComment() );
					stdoutHistoryDebugger( here, "29",
							currWord, false );
					break;
				}
				case SECTION :
				{
					// NOTE ensuring we don't lose the preceeding comment
					int currLine = lineChecked;
					stdoutHistoryDebugger( here, "30",
							currWord, false );
					currChild = section( getPreceedingComment(), ownDepth );
					stdoutHistoryDebugger( here, "31"
							, currWord, false );
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
		String here = cl +"f ";
		EnoType fieldType = FIELD_EMPTY;
		Word emptyLines = null;
		stdoutHistoryDebugger( here, "32", null, false );
		Word currWord = popCurrentWordOfLine();
		stdoutHistoryDebugger( here, "33", currWord, true );
		if ( currWord.type == EMPTY )
		{
			emptyLines = currWord;
			currWord = popCurrentWordOfLine();
			stdoutHistoryDebugger( here, "34", currWord, true );
		}
		if ( currWord.type != FIELD )
			throw new RuntimeException( "expected field name" ); // assert paranoid
		Word fieldName = currWord;
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
		Dependence reference = null;
		currWord = popCurrentWordOfLine();
		stdoutHistoryDebugger( here, "35", currWord, true );
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
				stdoutHistoryDebugger( here, "36"
						, currWord, true );
				if ( currWord.type == null || currWord.type != FIELD )
					throw new RuntimeException( "expected template name" ); // assert paranoid
				reference = new Dependence();
				reference.hasReference = emptySelf;
				reference.nameOfReferredTo = currWord.value;
				reference.escapesOfReferredTo = currWord.modifier;
			}
			else
				throw new RuntimeException( "expected nothing, not "+ currWord.type ); // assert paranoid
		}
		Value currChild = null;
		String docComment = "";
		boolean nonChild = false; // NOTE encountered sibling field or parent section
		while ( true )
		{
			Syntaxeme nextType = peekAtNextLineType( 0 ); // NP here this was 1; differs now
			stdoutHistoryDebugger( here, "37", nextType );
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
					stdoutHistoryDebugger( here, "38", currWord, true );
					currWord = popCurrentWordOfLine();
					stdoutHistoryDebugger( here, "39", currWord, true );
					if ( currWord.type == EMPTY )
					{
						// NOTE not keeping comments separated, else we'd need to save them as Value
						currWord = popCurrentWordOfLine();
						stdoutHistoryDebugger( here, "40"
								, currWord, true );
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
				case VALUE :
				{
					advanceLine();
					stdoutHistoryDebugger( here, "41"
							, currWord, true );
					currWord = popCurrentWordOfLine();
					stdoutHistoryDebugger( here, "42"
							, currWord, true );
					if ( currWord.type == EMPTY )
					{
						// NOTE not keeping value substrings separated
						currWord = popCurrentWordOfLine();
						stdoutHistoryDebugger( here, "43"
								, currWord, true );
					}
					if ( currWord.type != VALUE )
					{
						throw new RuntimeException( "expected value" ); // assert paranoid
					}
					String continuation = ( currWord.modifier == Parser
							.WORD_MOD_CONT_EMPTY ) ? "" : " ";
					if ( fieldType == FIELD_EMPTY )
					{
						lineSelf = new Value( emptySelf );
						lineSelf.append( currWord.value );
						fieldType = FIELD_VALUE;
						if ( reference != null )
						{
							reference.hasReference = lineSelf;
						}
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
					break;
				}
				case LIST_ELEMENT :
				{
					docComment = getPreceedingComment();
					stdoutHistoryDebugger( here, "44"
							, currWord, true );
					if ( docComment.isEmpty() )
						advanceLine();
					stdoutHistoryDebugger( here, "45"
							, currWord, true );
					currWord = popCurrentWordOfLine();
					stdoutHistoryDebugger( here, "46"
							, currWord, true );
					if ( currWord.type == EMPTY )
					{
						emptyLines = currWord;
						currWord = popCurrentWordOfLine();
						stdoutHistoryDebugger( here, "47"
								, currWord, true );
					}
					if ( currWord.type != LIST_ELEMENT )
					{
						throw new RuntimeException( "expected list element" ); // assert paranoid
					}
					else if ( fieldType == FIELD_LIST || fieldType == FIELD_EMPTY )
					{
						if ( fieldType == FIELD_EMPTY )
						{
							fieldType = FIELD_LIST;
							listSelf = new FieldList( emptySelf );
							if ( reference != null )
							{
								reference.hasReference = listSelf;
							}
						}
						currChild = new ListItem( currWord.value );
						currChild.setName( listSelf.getName() ); // per spec
						if ( ! docComment.isEmpty() )
						{
							currChild.addComment( docComment );
							currChild.setFirstCommentPreceededName( true );
						}
						if ( emptyLines != null && emptyLines.modifier != 0 )
						{
							currChild.setPreceedingEmptyLines( emptyLines.modifier );
							emptyLines.modifier = 0;
						}
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
					stdoutHistoryDebugger( here, "48"
							, currWord, true );
					if ( docComment.isEmpty() )
						advanceLine();
					stdoutHistoryDebugger( here, "49"
							, currWord, true );
					currWord = popCurrentWordOfLine();
					stdoutHistoryDebugger( here, "50"
							, currWord, true );
					if ( currWord.type == EMPTY )
					{
						emptyLines = currWord;
						currWord = popCurrentWordOfLine();
						stdoutHistoryDebugger( here, "51"
								, currWord, true );
					}
					else if ( fieldType == FIELD_SET || fieldType == FIELD_EMPTY )
					{
						if ( fieldType == FIELD_EMPTY )
						{
							fieldType = FIELD_SET;
							pairedSelf = new FieldSet( emptySelf );
							if ( reference != null )
							{
								reference.hasReference = pairedSelf;
							}
						}
						currChild = new SetEntry( currWord.value, currWord.modifier );
						currWord = popCurrentWordOfLine();
						stdoutHistoryDebugger( here, "52"
								, currWord, true );
						if ( currWord.type != VALUE )
							throw new RuntimeException( "expected set entry value token" ); // assert paranoid
						currChild.setStringValue( currWord.value );
						if ( ! docComment.isEmpty() )
						{
							currChild.addComment( docComment );
							currChild.setFirstCommentPreceededName( true );
						}
						if ( emptyLines != null && emptyLines.modifier != 0 )
						{
							currChild.setPreceedingEmptyLines( emptyLines.modifier );
							emptyLines.modifier = 0;
						}
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
			fields.add( listSelf );
			return listSelf;
		}
		else if ( fieldType == FIELD_VALUE )
		{
			fields.add( lineSelf );
			return lineSelf;
		}
		else if ( fieldType == FIELD_SET )
		{
			fields.add( pairedSelf );
			return pairedSelf;
		}
		else
		{
			fields.add( emptySelf );
			return emptySelf;
		}
	}


	private EnoElement multiline( String preceedingComment )
	{
		String here = cl +"ml ";
		Word emptyLines = null;
		stdoutHistoryDebugger( here, "53"
				, null, false );
		Word currWord = popCurrentWordOfLine();
		stdoutHistoryDebugger( here, "54"
				, currWord, true );
		if ( currWord.type == EMPTY )
		{
			emptyLines = currWord;
			currWord = popCurrentWordOfLine();
			stdoutHistoryDebugger( here, "55"
					, currWord, true );
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
		currWord = popCurrentWordOfLine();
		stdoutHistoryDebugger( here, "56"
				, currWord, true );
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
		currElem.setBoundaryLength( boundaryHyphens );
		if ( emptyLines != null )
		{
			currElem.setPreceedingEmptyLines( emptyLines.modifier );
		}
		currWord = popCurrentWordOfLine();
		stdoutHistoryDebugger( here, "57"
				, currWord, true );
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
		while ( peekAtNextLineType( 1 ) == COMMENT )
		{
			stdoutHistoryDebugger( here, "57"
					, currWord, true );
			advanceLine();
			stdoutHistoryDebugger( here, "58"
					, currWord, true );
			currWord = popCurrentWordOfLine();
			stdoutHistoryDebugger( here, "59"
					, currWord, true );
			if ( currWord.type == EMPTY )
			{
				currWord = popCurrentWordOfLine();
				stdoutHistoryDebugger( here, "60"
						, currWord, true );
			}
			if ( currWord.type != COMMENT )
				throw new RuntimeException( "expected comment" ); // assert paranoid
			else
				currElem.addComment( currWord.value );
		}
		fields.add( currElem );
		return currElem;
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
		String here = cl +"gpc\t";
		List<String> comments = new ArrayList<>();
		boolean firstTime = true, lineHasContent = false;
		int initialGlobalLineCursor = lineChecked;
		if ( startAtCurrentLine )
			lineChecked -= 1;
		Word currToken;
		stdoutHistoryDebugger( here, "61"
				, null, false );
		if ( advanceLine() )
		{
			stdoutHistoryDebugger( here, "62"
					, null, false );
			do
			{
				currToken = popCurrentWordOfLine();
				stdoutHistoryDebugger( here, "63"
						, currToken, true );
				if ( currToken == null )
					continue;
				else if ( currToken.type == EMPTY )
				{
					currToken = popCurrentWordOfLine();
					stdoutHistoryDebugger( here, "64"
							, currToken, true );
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
				stdoutHistoryDebugger( here, "65"
						, currToken, true );
				return "";
			}
			stdoutHistoryDebugger( here, "66"
					, currToken, true );
			boolean amAssociated = false;
			comments.add( currToken.value );
			while ( advanceLine() )
			{
				stdoutHistoryDebugger( here, "67"
						, currToken, true );
				currToken = popCurrentWordOfLine();
				stdoutHistoryDebugger( here, "68"
						, currToken, true );
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
				stdoutHistoryDebugger( here, "69"
						, currToken, true );
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
				stdoutHistoryDebugger( here, "70"
						, currToken, true );
				return wholeBlock.substring( 0, wholeBlock.length()
						- System.lineSeparator().length() ); // NOTE remove trailing \n
			}
		}
		else
		{
			lineChecked = initialGlobalLineCursor;
			wordIndOfLine = 0; // ASK potentially save,restore ?
			stdoutHistoryDebugger( here, "71"
					, null, false );
			return "";
		}
	}


	private Syntaxeme peekAtNextLineType()
	{
		return peekAtNextLineType( 0 );
	}


	private Syntaxeme peekAtNextLineType( int offsetToNext )
	{
		String here = cl +"panlt\t";
		/*
		if list is empty, continue, nonstandard parser input
		if the first of it is empty, check next word
		if not comment, return that
		if comment iterate until a line starts with empty or noncomment
			if empty return comment, else return noncomment
		*/
		boolean vettingComment = false, firstTime = true;
		int nextLineInd = lineChecked + offsetToNext, wordInd = 0;
		Word currWord = null;
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
			line = parsedLines.get( nextLineInd );
			if ( line.isEmpty() )
			{
				// warn about nonstandard parser line
				continue;
			}
			wordInd = 0;
			while ( wordInd < line.size() )
			{
				currWord = line.get( wordInd );
				if ( currWord.type == Syntaxeme.EMPTY
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
			if ( currWord.type == COMMENT 
					&& firstTime )
			{
				vettingComment = true;
				firstTime = false;
				continue;
			}
			else if ( currWord.type == COMMENT 
					&& vettingComment )
			{
				continue;
			}
			else
			{
				return currWord.type;
			}
		}
	}


	private void resolveForwardReferences()
	{
		// FIX todo
		for ( Dependence ref : transitiveSections )
		{
			if ( ref.hasReference.getName().equals( ref.nameOfReferredTo )
					&& ref.hasReference.getNameEscapes() == ref.escapesOfReferredTo )
			{
				MessageFormat problem = new MessageFormat(
						ExceptionStore.getStore().getExceptionMessage(
								ExceptionStore.VALIDATION,
								EnoLocaleKey.CYCLIC_DEPENDENCY ) );
				throw new NoSuchElementException( problem.format( new Object[]{ ref.nameOfReferredTo } ) ); // Improve may need to add escapes to distinguish
			}
			int indOfCandidate = -1;
			for ( int ind = 0; ind < sections.size(); ind++ )
			{
				Section candidate = sections.get( ind );
				if ( ref.nameOfReferredTo.equals( candidate.getName() )
						&& ref.escapesOfReferredTo == candidate.getNameEscapes() )
				{
					if ( indOfCandidate >= 0 )
					{
						// FIX canon complaint about multiple templates
					}
					else
					{
						indOfCandidate = ind;
					}
				}
			}
			if ( indOfCandidate < 0 )
			{
				MessageFormat problem = new MessageFormat(
						ExceptionStore.getStore().getExceptionMessage(
								null  ,//ExceptionStore., resolution FIX es doesn't know about r file, ensure it has all of them
								EnoLocaleKey.MISSING_FIELD_VALUE ) );
				throw new NoSuchElementException( problem.format( new Object[]{ ref.nameOfReferredTo } ) );
			}
			else
			{
				ref.isReferredTo = sections.get( indOfCandidate );
				ref.hasReference.setTemplate( ref.isReferredTo );
				// Improve check if there's a deeper cyclic dependency
			}
		}
		// check fields ;; also todo
		for ( Dependence ref : transitiveFields )
		{
			if ( ref.hasReference.getName().equals( ref.nameOfReferredTo )
					&& ref.hasReference.getNameEscapes() == ref.escapesOfReferredTo )
			{
				MessageFormat problem = new MessageFormat(
						ExceptionStore.getStore().getExceptionMessage(
								ExceptionStore.VALIDATION,
								EnoLocaleKey.CYCLIC_DEPENDENCY ) );
				throw new NoSuchElementException( problem.format( new Object[]{ ref.nameOfReferredTo } ) ); // Improve may need to add escapes to distinguish
			}
			int indOfCandidate = -1;
			for ( int ind = 0; ind < fields.size(); ind++ )
			{
				Field candidate = fields.get( ind );
				if ( ref.nameOfReferredTo.equals( candidate.getName() )
						&& ref.escapesOfReferredTo == candidate.getNameEscapes() )
				{
					if ( indOfCandidate >= 0 )
					{
						// FIX canon complaint about multiple templates
					}
					else
					{
						indOfCandidate = ind;
					}
				}
			}
			if ( indOfCandidate < 0 )
			{
				MessageFormat problem = new MessageFormat(
						ExceptionStore.getStore().getExceptionMessage(
								null  ,//ExceptionStore., resolution FIX es doesn't know about r file, ensure it has all of them
								EnoLocaleKey.MISSING_FIELD_VALUE ) );
				throw new NoSuchElementException( problem.format( new Object[]{ ref.nameOfReferredTo } ) );
			}
			else
			{
				ref.isReferredTo = fields.get( indOfCandidate );
				ref.hasReference.setTemplate( ref.isReferredTo );
				// Improve check if there's a deeper cyclic dependency
			}
		}
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


	/** 4TESTS */
	private void stdoutHistoryDebugger( String here, String otherId,
			Syntaxeme typeFound )
	{
		System.out.println( here + otherId +" peek-type-"+ typeFound );
		stdoutHistoryDebugger(here, otherId, null, false);
	}


	/** 4TESTS */
	private void stdoutHistoryDebugger( String here, String otherId,
			Word currWord, boolean checkCurrWord )
	{
		if ( checkCurrWord )
		{
			if ( currWord != null )
			{
				System.out.println( here + otherId +" cw-type-"+ currWord.type );
			}
			else
			{
				System.out.println( here + otherId +" currWord is null" );
			}
		}
		if (lineChecked >= 0 && lineChecked < parsedLines.size()
				&& wordIndOfLine < parsedLines.get(lineChecked).size() )
		{
			System.out.println( here + otherId +" lc:"+ lineChecked +" wol:"
					+ wordIndOfLine +" type-"+ parsedLines.get(lineChecked)
					.get(wordIndOfLine).type );
		}
		else
		{
			System.out.println( here + otherId +" cursor overflow" );
		}
	}

}
























































