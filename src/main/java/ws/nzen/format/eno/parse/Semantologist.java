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
		System.out.println( here +"starting at -1" );
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
			if (lineChecked >= 0 && lineChecked < parsedLines.size() && wordIndOfLine < parsedLines.get(lineChecked).size() )
			System.out.println( here +"1 post al lc:"+ lineChecked +" wol:"+ wordIndOfLine
					+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
			else System.out.println( here +"1"
					+ " cursor overflow" );
			Syntaxeme focusType = peekAtNextLineType( -1 );
			System.out.println( here +"2 peeked type-"+ focusType );
			String firstComment;
			Word currToken;
			if ( focusType == EMPTY )
			{
				break; // end of input; parser tells me how much trailing space to include
			}
			else if ( focusType == COMMENT )
			{
				currToken = popCurrentWordOfLine();
				if (lineChecked >= 0 && lineChecked < parsedLines.size() && wordIndOfLine < parsedLines.get(lineChecked).size() )
				System.out.println( here +"3 comment lc:"+ lineChecked +" wol:"+ wordIndOfLine
						+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
				else System.out.println( here +"3"
						+ " cursor overflow" );
				System.out.println( here +"4 popped commment type-"+ currToken.type );
				if ( currToken == null )
					continue;
				else if ( currToken.type == EMPTY )
				{
					currToken = popCurrentWordOfLine(); // NOTE assuming well formed parser lines
					System.out.println( here +"not empty comment type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
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
			if (lineChecked >= 0 && lineChecked < parsedLines.size()
					&& wordIndOfLine < parsedLines.get(lineChecked).size() )
			System.out.println( here +"5 after gpc lc:"+ lineChecked +" wol:"+ wordIndOfLine
					+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
			currToken = peekAtCurrentWordOfLine();
			if ( currToken == null )
				continue;
			else if ( currToken.type == EMPTY )
			{
				if (lineChecked >= 0 && lineChecked < parsedLines.size()
						&& wordIndOfLine < parsedLines.get(lineChecked).size() )
				System.out.println( here +"6 empty lc:"+ lineChecked +" wol:"+ wordIndOfLine
						+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
				else System.out.println( here +"6"
						+ " cursor overflow" );
				wordIndOfLine += 1;
				currToken = peekAtCurrentWordOfLine(); // NOTE assuming well formed parser lines
				if (lineChecked >= 0 && lineChecked < parsedLines.size()
						&& wordIndOfLine < parsedLines.get(lineChecked).size() )
				System.out.println( here +"7 peeked lc:"+ lineChecked +" wol:"+ wordIndOfLine
						+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
				else System.out.println( here +"7"
						+ " cursor overflow" );
				if ( currToken == null )
					continue; // assert paranoid
				else
					wordIndOfLine -= 1; // NOTE reset word cursor
				if (lineChecked >= 0 && lineChecked < parsedLines.size()
						&& wordIndOfLine < parsedLines.get(lineChecked).size() )
				System.out.println( here +"8 after reset lc:"+ lineChecked +" wol:"+ wordIndOfLine
						+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
				else System.out.println( here +"8"
						+ " cursor overflow" );
			}
			switch ( currToken.type )
			{
				case SECTION :
				{
					if (lineChecked >= 0 && lineChecked < parsedLines.size()
							&& wordIndOfLine < parsedLines.get(lineChecked).size() )
					System.out.println( here +"9 pre s lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
					else System.out.println( here +"9"
							+ " cursor overflow" );
					currElem = section( firstComment, sectionDepth );
					if (lineChecked >= 0 && lineChecked < parsedLines.size()
							&& wordIndOfLine < parsedLines.get(lineChecked).size() )
					System.out.println( here +"10 post s lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
					else System.out.println( here +"10"
							+ " cursor overflow" );
					break;
				}
				case FIELD :
				{
					if (lineChecked >= 0 && lineChecked < parsedLines.size()
							&& wordIndOfLine < parsedLines.get(lineChecked).size() )
					System.out.println( here +"11  pre f lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
					else System.out.println( here +"11"
							+ " cursor overflow" );
					currElem = field( firstComment );
					if (lineChecked >= 0 && lineChecked < parsedLines.size()
							&& wordIndOfLine < parsedLines.get(lineChecked).size() )
					System.out.println( here +"12 post f lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
					else System.out.println( here +"12"
							+ " cursor overflow" );
					break;
				}
				case MULTILINE_BOUNDARY :
				{
					if (lineChecked >= 0 && lineChecked < parsedLines.size()
							&& wordIndOfLine < parsedLines.get(lineChecked).size() )
					System.out.println( here +"13 pre ml lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
					else System.out.println( here +"13"
							+ " cursor overflow" );
					currElem = multiline( firstComment );
					if (lineChecked >= 0 && lineChecked < parsedLines.size()
							&& wordIndOfLine < parsedLines.get(lineChecked).size() )
					System.out.println( here +"14 post ml lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
					else System.out.println( here +"14"
							+ " cursor overflow" );
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
		String here = cl +"s ";
		Word currWord = popCurrentWordOfLine();
		System.out.println( here +"15 popped lc:"+ lineChecked +" wol:"+ wordIndOfLine
				+" type-"+ currWord.type );
		if (lineChecked >= 0 && lineChecked < parsedLines.size()
				&& wordIndOfLine < parsedLines.get(lineChecked).size() )
		System.out.println( here +"16 after pop lc:"+ lineChecked +" wol:"+ wordIndOfLine
				+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
		else System.out.println( here +"16"
				+ " cursor overflow" );
		Word emptyLines = null;
		if ( currWord.type == null )
			throw new RuntimeException( "expected section" ); // assert paranoid
		else if ( currWord.type == EMPTY )
		{
			emptyLines = currWord;
			currWord = popCurrentWordOfLine();
			System.out.println( here +"17 after skip empty lc:"+ lineChecked +" wol:"+ wordIndOfLine
					+" type-"+ currWord.type );
		}
		Word sectionOperator;
		if ( currWord.type == null || currWord.type != Syntaxeme.SECTION )
			throw new RuntimeException( "expected section operator" ); // assert paranoid
		else
		{
			sectionOperator = currWord;
			currWord = popCurrentWordOfLine();
			System.out.println( here +"18 popped lc:"+ lineChecked +" wol:"+ wordIndOfLine
					+" type-"+ currWord.type );
			if (lineChecked >= 0 && lineChecked < parsedLines.size()
					&& wordIndOfLine < parsedLines.get(lineChecked).size() )
			System.out.println( here +"19 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
					+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
			else System.out.println( here +"19"
					+ " cursor overflow" );
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
			System.out.println( here +"20 popped lc:"+ lineChecked +" wol:"+ wordIndOfLine
					+" type-"+ currWord.type );
			if (lineChecked >= 0 && lineChecked < parsedLines.size()
					&& wordIndOfLine < parsedLines.get(lineChecked).size() )
			System.out.println( here +"21 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
					+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
			else System.out.println( here +"21"
					+ " cursor overflow" );
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
			System.out.println( here +"22 cw lc:"+ lineChecked +" wol:"+ wordIndOfLine
					+" type-"+ currWord.type );
			if (lineChecked >= 0 && lineChecked < parsedLines.size()
					&& wordIndOfLine < parsedLines.get(lineChecked).size() )
			System.out.println( here +"23 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
					+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
			else System.out.println( here +"23"
					+ " cursor overflow" );
			if ( currWord.type == null || currWord.type != FIELD )
				throw new RuntimeException( "expected template name" ); // assert paranoid
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
			System.out.println( here +"24 next peeked lc:"+ lineChecked +" wol:"+ wordIndOfLine
					+" type-"+ nextType );
			if (lineChecked >= 0 && lineChecked < parsedLines.size()
					&& wordIndOfLine < parsedLines.get(lineChecked).size() )
			System.out.println( here +"25 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
					+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
			else System.out.println( here +"25"
					+ " cursor overflow" );
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
					System.out.println( here +"26 cw lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ currWord.type );
					if (lineChecked >= 0 && lineChecked < parsedLines.size()
							&& wordIndOfLine < parsedLines.get(lineChecked).size() )
					System.out.println( here +"27 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
					else System.out.println( here +"27"
							+ " cursor overflow" );
					if ( currWord.type == EMPTY )
					{
						// NOTE not keeping comments separated, else we'd need to save them as Value
						currWord = popCurrentWordOfLine();
						System.out.println( here +"28 removed empty lc:"+ lineChecked +" wol:"+ wordIndOfLine
								+" type-"+ currWord.type );
						if (lineChecked >= 0 && lineChecked < parsedLines.size()
								&& wordIndOfLine < parsedLines.get(lineChecked).size() )
						System.out.println( here +"29 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
								+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
						else System.out.println( here +"29"
								+ " cursor overflow" );
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
					if (lineChecked >= 0 && lineChecked < parsedLines.size()
							&& wordIndOfLine < parsedLines.get(lineChecked).size() )
					System.out.println( here +"30 c pre f lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
					else System.out.println( here +"30"
							+ " cursor overflow" );
					currChild = field( getPreceedingComment() );
					if (lineChecked >= 0 && lineChecked < parsedLines.size()
							&& wordIndOfLine < parsedLines.get(lineChecked).size() )
					System.out.println( here +"31 c post f lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
					else System.out.println( here +"31"
							+ " cursor overflow" );
					break;
				}
				case MULTILINE_BOUNDARY :
				{
					if (lineChecked >= 0 && lineChecked < parsedLines.size()
							&& wordIndOfLine < parsedLines.get(lineChecked).size() )
					System.out.println( here +"32 c pre ml lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
					else System.out.println( here +"32"
							+ " cursor overflow" );
					currChild = multiline( getPreceedingComment() );
					if (lineChecked >= 0 && lineChecked < parsedLines.size()
							&& wordIndOfLine < parsedLines.get(lineChecked).size() )
					System.out.println( here +"33 c post ml lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
					else System.out.println( here +"33"
							+ " cursor overflow" );
					break;
				}
				case SECTION :
				{
					// NOTE ensuring we don't lose the preceeding comment
					int currLine = lineChecked;
					if (lineChecked >= 0 && lineChecked < parsedLines.size()
							&& wordIndOfLine < parsedLines.get(lineChecked).size() )
					System.out.println( here +"34 c pre s lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
					else System.out.println( here +"34"
							+ " cursor overflow" );
					currChild = section( getPreceedingComment(), ownDepth );
					if (lineChecked >= 0 && lineChecked < parsedLines.size()
							&& wordIndOfLine < parsedLines.get(lineChecked).size() )
					System.out.println( here +"35 c post s lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
					else System.out.println( here +"35"
							+ " cursor overflow" );
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
		if (lineChecked >= 0 && lineChecked < parsedLines.size()
				&& wordIndOfLine < parsedLines.get(lineChecked).size() )
		System.out.println( here +"36 c pre pop lc:"+ lineChecked +" wol:"+ wordIndOfLine
				+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
		else System.out.println( here +"36"
				+ " cursor overflow" );
		Word currWord = popCurrentWordOfLine();
		System.out.println( here +"37 cw lc:"+ lineChecked +" wol:"+ wordIndOfLine
				+" type-"+ currWord.type );
		if (lineChecked >= 0 && lineChecked < parsedLines.size()
				&& wordIndOfLine < parsedLines.get(lineChecked).size() )
		System.out.println( here +"38 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
				+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
		else System.out.println( here +"38"
				+ " cursor overflow" );
		if ( currWord.type == EMPTY )
		{
			emptyLines = currWord;
			currWord = popCurrentWordOfLine();
			System.out.println( here +"39 not empty lc:"+ lineChecked +" wol:"+ wordIndOfLine
					+" type-"+ currWord.type );
			if (lineChecked >= 0 && lineChecked < parsedLines.size()
					&& wordIndOfLine < parsedLines.get(lineChecked).size() )
			System.out.println( here +"40 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
					+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
			else System.out.println( here +"40"
					+ " cursor overflow" );
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
		if (currWord != null)
		System.out.println( here +"41 cw lc:"+ lineChecked +" wol:"+ wordIndOfLine
				+" type-"+ currWord.type );
		else System.out.println( here +"41"
				+ " null word from pcol" );
		if (lineChecked >= 0 && lineChecked < parsedLines.size()
				&& wordIndOfLine < parsedLines.get(lineChecked).size() )
		System.out.println( here +"42 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
				+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
		else System.out.println( here +"42"
				+ " cursor overflow" );
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
				System.out.println( here +"43 cw lc:"+ lineChecked +" wol:"+ wordIndOfLine
						+" type-"+ currWord.type );
				if (lineChecked >= 0 && lineChecked < parsedLines.size()
						&& wordIndOfLine < parsedLines.get(lineChecked).size() )
				System.out.println( here +"44 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
						+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
				else System.out.println( here +"44"
						+ " cursor overflow" );
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
			Syntaxeme nextType = peekAtNextLineType( 1 );
			System.out.println( here +"45 peek lc:"+ lineChecked +" wol:"+ wordIndOfLine
					+" type-"+ nextType );
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
					if (lineChecked >= 0 && lineChecked < parsedLines.size()
							&& wordIndOfLine < parsedLines.get(lineChecked).size() )
					System.out.println( here +"46 adv cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
					else System.out.println( here +"46"
							+ " cursor overflow" );
					currWord = popCurrentWordOfLine();
					System.out.println( here +"47 cw lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ currWord.type );
					if (lineChecked >= 0 && lineChecked < parsedLines.size()
							&& wordIndOfLine < parsedLines.get(lineChecked).size() )
					System.out.println( here +"48 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
					else System.out.println( here +"48"
							+ " cursor overflow" );
					if ( currWord.type == EMPTY )
					{
						// NOTE not keeping comments separated, else we'd need to save them as Value
						currWord = popCurrentWordOfLine();
						System.out.println( here +"49 cw lc:"+ lineChecked +" wol:"+ wordIndOfLine
								+" type-"+ currWord.type );
						if (lineChecked >= 0 && lineChecked < parsedLines.size()
								&& wordIndOfLine < parsedLines.get(lineChecked).size() )
						System.out.println( here +"50 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
								+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
						else System.out.println( here +"50"
								+ " cursor overflow" );
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
					if (lineChecked >= 0 && lineChecked < parsedLines.size()
							&& wordIndOfLine < parsedLines.get(lineChecked).size() )
					System.out.println( here +"51 al cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
					else System.out.println( here +"51"
							+ " cursor overflow" );
					currWord = popCurrentWordOfLine();
					System.out.println( here +"52 cw lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ currWord.type );
					if (lineChecked >= 0 && lineChecked < parsedLines.size()
							&& wordIndOfLine < parsedLines.get(lineChecked).size() )
					System.out.println( here +"53 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
					else System.out.println( here +"53"
							+ " cursor overflow" );
					if ( currWord.type == EMPTY )
					{
						// NOTE not keeping value substringss separated
						currWord = popCurrentWordOfLine();
						System.out.println( here +"54 cw lc:"+ lineChecked +" wol:"+ wordIndOfLine
								+" type-"+ currWord.type );
						if (lineChecked >= 0 && lineChecked < parsedLines.size()
								&& wordIndOfLine < parsedLines.get(lineChecked).size() )
						System.out.println( here +"55 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
								+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
						else System.out.println( here +"55"
								+ " cursor overflow" );
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
					if (lineChecked >= 0 && lineChecked < parsedLines.size()
							&& wordIndOfLine < parsedLines.get(lineChecked).size() )
					System.out.println( here +"56 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
					else System.out.println( here +"56"
							+ " cursor overflow" );
					if ( docComment.isEmpty() )
						advanceLine();
					System.out.println( here +"57 cw lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ currWord.type );
					if (lineChecked >= 0 && lineChecked < parsedLines.size()
							&& wordIndOfLine < parsedLines.get(lineChecked).size() )
					System.out.println( here +"58 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
					else System.out.println( here +"58"
							+ " cursor overflow" );
					currWord = popCurrentWordOfLine();
					System.out.println( here +"59 cw lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ currWord.type );
					if (lineChecked >= 0 && lineChecked < parsedLines.size()
							&& wordIndOfLine < parsedLines.get(lineChecked).size() )
					System.out.println( here +"60 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
					else System.out.println( here +"60"
							+ " cursor overflow" );
					if ( currWord.type == EMPTY )
					{
						emptyLines = currWord;
						currWord = popCurrentWordOfLine();
						System.out.println( here +"61 cw lc:"+ lineChecked +" wol:"+ wordIndOfLine
								+" type-"+ currWord.type );
						if (lineChecked >= 0 && lineChecked < parsedLines.size()
								&& wordIndOfLine < parsedLines.get(lineChecked).size() )
						System.out.println( here +"62 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
								+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
						else System.out.println( here +"62"
								+ " cursor overflow" );
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
					System.out.println( here +"63 cw lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ currWord.type );
					if (lineChecked >= 0 && lineChecked < parsedLines.size()
							&& wordIndOfLine < parsedLines.get(lineChecked).size() )
					System.out.println( here +"64 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
					else System.out.println( here +"64"
							+ " cursor overflow" );
					if ( docComment.isEmpty() )
						advanceLine();
					System.out.println( here +"65 cw lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ currWord.type );
					if (lineChecked >= 0 && lineChecked < parsedLines.size()
							&& wordIndOfLine < parsedLines.get(lineChecked).size() )
					System.out.println( here +"66 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
					else System.out.println( here +"66"
							+ " cursor overflow" );
					currWord = popCurrentWordOfLine();
					System.out.println( here +"67 cw lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ currWord.type );
					if (lineChecked >= 0 && lineChecked < parsedLines.size()
							&& wordIndOfLine < parsedLines.get(lineChecked).size() )
					System.out.println( here +"68 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
					else System.out.println( here +"68"
							+ " cursor overflow" );
					if ( currWord.type == EMPTY )
					{
						emptyLines = currWord;
						currWord = popCurrentWordOfLine();
						System.out.println( here +"69 cw lc:"+ lineChecked +" wol:"+ wordIndOfLine
								+" type-"+ currWord.type );
						if (lineChecked >= 0 && lineChecked < parsedLines.size()
								&& wordIndOfLine < parsedLines.get(lineChecked).size() )
						System.out.println( here +"70 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
								+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
						else System.out.println( here +"70"
								+ " cursor overflow" );
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
						System.out.println( here +"71 cw lc:"+ lineChecked +" wol:"+ wordIndOfLine
								+" type-"+ currWord.type );
						if (lineChecked >= 0 && lineChecked < parsedLines.size()
								&& wordIndOfLine < parsedLines.get(lineChecked).size() )
						System.out.println( here +"72 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
								+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
						else System.out.println( here +"72"
								+ " cursor overflow" );
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
		Word currWord = popCurrentWordOfLine();
		System.out.println( here +"73 cw lc:"+ lineChecked +" wol:"+ wordIndOfLine
				+" type-"+ currWord.type );
		if (lineChecked >= 0 && lineChecked < parsedLines.size()
				&& wordIndOfLine < parsedLines.get(lineChecked).size() )
		System.out.println( here +"74 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
				+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
		else System.out.println( here +"74"
				+ " cursor overflow" );
		if ( currWord.type == EMPTY )
		{
			emptyLines = currWord;
			currWord = popCurrentWordOfLine();
			System.out.println( here +"75 cw lc:"+ lineChecked +" wol:"+ wordIndOfLine
					+" type-"+ currWord.type );
			if (lineChecked >= 0 && lineChecked < parsedLines.size()
					&& wordIndOfLine < parsedLines.get(lineChecked).size() )
			System.out.println( here +"76 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
					+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
			else System.out.println( here +"76"
					+ " cursor overflow" );
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
		System.out.println( here +"77 cw lc:"+ lineChecked +" wol:"+ wordIndOfLine
				+" type-"+ currWord.type );
		if (lineChecked >= 0 && lineChecked < parsedLines.size()
				&& wordIndOfLine < parsedLines.get(lineChecked).size() )
		System.out.println( here +"78 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
				+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
		else System.out.println( here +"78"
				+ " cursor overflow" );
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
		System.out.println( here +"79 cw lc:"+ lineChecked +" wol:"+ wordIndOfLine
				+" type-"+ currWord.type );
		if (lineChecked >= 0 && lineChecked < parsedLines.size()
				&& wordIndOfLine < parsedLines.get(lineChecked).size() )
		System.out.println( here +"80 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
				+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
		else System.out.println( here +"80"
				+ " cursor overflow" );
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
			advanceLine();
			currWord = popCurrentWordOfLine();
			System.out.println( here +"81 cw lc:"+ lineChecked +" wol:"+ wordIndOfLine
					+" type-"+ currWord.type );
			if (lineChecked >= 0 && lineChecked < parsedLines.size()
					&& wordIndOfLine < parsedLines.get(lineChecked).size() )
			System.out.println( here +"82 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
					+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
			else System.out.println( here +"82"
					+ " cursor overflow" );
			if ( currWord.type == EMPTY )
			{
				currWord = popCurrentWordOfLine();
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
		if ( advanceLine() )
		{
			if (lineChecked >= 0 && lineChecked < parsedLines.size() && wordIndOfLine < parsedLines.get(lineChecked).size() )
			System.out.println( here +"83 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
					+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
			do
			{
				currToken = popCurrentWordOfLine();
				if (currToken != null)
				System.out.println( here +"84 cw lc:"+ lineChecked +" wol:"+ wordIndOfLine
						+" type-"+ currToken.type );
				else System.out.println( here +"84"
						+ " null word from pcol" );
				if (lineChecked >= 0 && lineChecked < parsedLines.size()
						&& wordIndOfLine < parsedLines.get(lineChecked).size() )
				System.out.println( here +"85 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
						+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
				else System.out.println( here +"85"
						+ " cursor overflow" );
				if ( currToken == null )
					continue;
				else if ( currToken.type == EMPTY )
				{
					currToken = popCurrentWordOfLine();
					System.out.println( here +"86 cw lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ currToken.type );
					if (lineChecked >= 0 && lineChecked < parsedLines.size()
							&& wordIndOfLine < parsedLines.get(lineChecked).size() )
					System.out.println( here +"87 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
							+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
					else System.out.println( here +"87"
							+ " cursor overflow" );
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
				if (lineChecked >= 0 && lineChecked < parsedLines.size()
						&& wordIndOfLine < parsedLines.get(lineChecked).size() )
				System.out.println( here +"88 reset cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
						+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
				else System.out.println( here +"88"
						+ " cursor overflow" );
				return "";
			}
			System.out.println( here +"89 cw lc:"+ lineChecked +" wol:"+ wordIndOfLine
					+" type-"+ currToken.type );
			if (lineChecked >= 0 && lineChecked < parsedLines.size()
					&& wordIndOfLine < parsedLines.get(lineChecked).size() )
			System.out.println( here +"90 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
					+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
			else System.out.println( here +"90"
					+ " cursor overflow" );
			boolean amAssociated = false;
			comments.add( currToken.value );
			while ( advanceLine() )
			{
				System.out.println( here +"91 cw lc:"+ lineChecked +" wol:"+ wordIndOfLine
						+" type-"+ currToken.type );
				if (lineChecked >= 0 && lineChecked < parsedLines.size()
						&& wordIndOfLine < parsedLines.get(lineChecked).size() )
				System.out.println( here +"92 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
						+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
				else System.out.println( here +"92"
						+ " cursor overflow" );
				currToken = popCurrentWordOfLine();
				System.out.println( here +"93 cw lc:"+ lineChecked +" wol:"+ wordIndOfLine
						+" type-"+ currToken.type );
				if (lineChecked >= 0 && lineChecked < parsedLines.size()
						&& wordIndOfLine < parsedLines.get(lineChecked).size() )
				System.out.println( here +"94 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
						+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
				else System.out.println( here +"94 cursor overflow" );
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
				if (lineChecked >= 0 && lineChecked < parsedLines.size()
						&& wordIndOfLine < parsedLines.get(lineChecked).size() )
				System.out.println( here +"95 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
						+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
				else System.out.println( here +"95 cursor overflow" );
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
				System.out.println( here +"96 cw lc:"+ lineChecked +" wol:"+ wordIndOfLine
						+" type-"+ currToken.type );
				if (lineChecked >= 0 && lineChecked < parsedLines.size()
						&& wordIndOfLine < parsedLines.get(lineChecked).size() )
				System.out.println( here +"97 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
						+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
				else System.out.println( here +"97 cursor overflow" );
				return wholeBlock.substring( 0, wholeBlock.length()
						- System.lineSeparator().length() ); // NOTE remove trailing \n
			}
		}
		else
		{
			lineChecked = initialGlobalLineCursor;
			wordIndOfLine = 0; // ASK potentially save,restore ?
			if (lineChecked >= 0 && lineChecked < parsedLines.size()
					&& wordIndOfLine < parsedLines.get(lineChecked).size() )
			System.out.println( here +"98 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
					+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
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
		Word currMeme = null;
		List<Word> line = null;
		while ( true )
		{
			if (currMeme!=null)
			System.out.println( here +"99 cw lc:"+ lineChecked +" wol:"+ wordIndOfLine
					+" type-"+ currMeme.type );
			if (lineChecked >= 0 && lineChecked < parsedLines.size()
					&& wordIndOfLine < parsedLines.get(lineChecked).size() )
			System.out.println( here +"A0 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
					+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
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
				System.out.println( here +"A1 cw lc:"+ lineChecked +" wol:"+ wordIndOfLine
						+" type-"+ currMeme.type );
				if (lineChecked >= 0 && lineChecked < parsedLines.size()
						&& wordIndOfLine < parsedLines.get(lineChecked).size() )
				System.out.println( here +"A2 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
						+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
				else System.out.println( here +"A2 cursor overflow" );
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
	}


	/** next word or null if none left. Advances wordIndOfLine. */
	private Word popCurrentWordOfLine()
	{
		if (  lineChecked < parsedLines.size() )
		{
			if (lineChecked >= 0 && lineChecked < parsedLines.size()
					&& wordIndOfLine < parsedLines.get(lineChecked).size() )
			System.out.println( cl +"pcwof\tA3 cursor lc:"+ lineChecked +" wol:"+ wordIndOfLine
					+" type-"+ parsedLines.get(lineChecked).get(wordIndOfLine).type );
			else System.out.println( cl +"pcwof\tA3 cursor overflow" );
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
























































