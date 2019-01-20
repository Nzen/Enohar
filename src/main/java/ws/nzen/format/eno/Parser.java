/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import static ws.nzen.format.eno.Lexeme.COPY_OP_DEEP;
import static ws.nzen.format.eno.Lexeme.COPY_OP_THIN;
import static ws.nzen.format.eno.Lexeme.END;
import static ws.nzen.format.eno.Lexeme.ESCAPE_OP;
import static ws.nzen.format.eno.Lexeme.FIELD_START_OP;
import static ws.nzen.format.eno.Lexeme.MULTILINE_OP;
import static ws.nzen.format.eno.Lexeme.SET_OP;
import static ws.nzen.format.eno.Lexeme.WHITESPACE;
import static ws.nzen.format.eno.Syntaxeme.MULTILINE_BOUNDARY;
import static ws.nzen.format.eno.Syntaxeme.MULTILINE_TEXT;
import static ws.nzen.format.eno.Syntaxeme.COMMENT;
import static ws.nzen.format.eno.Syntaxeme.DEEP_COPY;
import static ws.nzen.format.eno.Syntaxeme.FIELD;
import static ws.nzen.format.eno.Syntaxeme.FIELD_ESCAPE;
import static ws.nzen.format.eno.Syntaxeme.SHALLOW_COPY;
import static ws.nzen.format.eno.Syntaxeme.VALUE;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

/**  */
public class Parser
{
	private static final String cl = "p.";
	static final Set<Lexeme> DELIM_END_COPY = new TreeSet<>();
	static final Set<Lexeme> DELIM_SET_FIELD_COPY = new TreeSet<>();
	static final Set<Lexeme> DELIM_FIELD_COPY = new TreeSet<>();
	static final Set<Lexeme> DELIM_END = new TreeSet<>();
	// eventually handle exception store localization, output formatter
	protected Queue<String> allLines = new LinkedList<>();
	protected Lexer alphabet = new Lexer();
	protected Lexer.Token currToken;
	protected int currLine = 0;
	protected ParseContext currState = ParseContext.SECTION_INTERIOR;
	@Deprecated
	private Stack<Lexer.Token> lexed = new Stack<>(); // ASK or a deque ?
	public class Phrase
	{
		public Syntaxeme type;
		public String words = "";
		// public Phrase child; // or does it need to be a list ?
		@Override
		public String toString()
		{
			return "PT: t-"+ type.name() +" w-"+ words;
		}
	}


	public Parser()
	{
		prepFieldDelimiters();
	}


	private void prepFieldDelimiters()
	{
		DELIM_END_COPY.add( END );
		DELIM_END_COPY.add( COPY_OP_DEEP );
		DELIM_END_COPY.add( COPY_OP_THIN );
		DELIM_SET_FIELD_COPY.add( SET_OP );
		DELIM_SET_FIELD_COPY.add( FIELD_START_OP );
		DELIM_SET_FIELD_COPY.add( COPY_OP_DEEP );
		DELIM_SET_FIELD_COPY.add( COPY_OP_THIN );
		DELIM_FIELD_COPY.add( FIELD_START_OP );
		DELIM_FIELD_COPY.add( COPY_OP_DEEP );
		DELIM_FIELD_COPY.add( COPY_OP_THIN );
		DELIM_END.add( END );
	}


	/** ensures this is a valid Eno document.
	 * @throws RuntimeException otherwise */
	public void recognize( List<String> fileLines )
	{
		if ( fileLines == null || fileLines.isEmpty() )
		{
			return;
		}
		else
		{
			allLines = new LinkedList<>( fileLines );
		}
		alphabet.setLine( allLines.poll() );
		nextToken();
		currLine++;
		sectionInteriorOld();
	}
	/** various whole eno elements */
	private void sectionInterior()
	{
		String here = cl +"si ";
		while ( true )
		{
			switch ( currToken.type )
			{
				case WHITESPACE :
				{
					nextToken();
					break;
				}
				case END :
				{
					if ( ! nextLine() )
					{
						System.out.println( here +"recognized a valid eno document" );
						return;
					}
					break;
				}
				case CONTINUE_OP_BREAK :
				case CONTINUE_OP_SAME :
				{
					// FIX use canon complaint
					throw new RuntimeException( here +"continuation started without assignment "+ currToken );
				}
				case COPY_OP_DEEP :
				case COPY_OP_THIN :
				{
					// FIx use canon complaint
					throw new RuntimeException( here +"template started without name "+ currToken );
				}
				case FIELD_START_OP :
				{
					// FIx use canon complaint
					throw new RuntimeException( here +"field assignment started without name "+ currToken );
				}
				case LIST_OP :
				{
					// FIx use canon complaint
					throw new RuntimeException( here +"list started without name "+ currToken );
				}
				case SET_OP :
				{
					// FIx use canon complaint
					throw new RuntimeException( here +"set started without name "+ currToken );
				}
				case COMMENT_OP :
				{
					comment();
					break;
				}
				case MULTILINE_OP :
				{
					multilineBoundary();
					break;
				}
				case SECTION_OP :
				{
					sectionBeginning();
					break;
				}
				case ESCAPE_OP :
				{
					fieldAny( escapedName() );
					break;
				}
				default :
				{
					fieldAny( unescapedName( DELIM_FIELD_COPY ) );
					break;
				}
			}
		}
	}


	/** various whole eno elements */
	private void sectionInteriorOld()
	{
		String here = cl +"si ";
		while ( true )
		{
			switch ( currToken.type )
			{
				case COMMENT_OP :
				{
					comment();
					break;
				}
				case WHITESPACE :
				{
					nextToken();
					break;
				}
				case SECTION_OP :
				{
					sectionBeginning();
					break;
				}
				case ESCAPE_OP :
				{
					fieldBeginningOld( escapedName() );
					break;
				}
				case MULTILINE_OP :
				{
					multilineBoundary();
					break;
				}
				case END :
				{
					if ( ! nextLine() )
					{
						System.out.println( here +"recognized a valid eno document" );
						return;
					}
					break;
				}
				case CONTINUE_OP_BREAK :
				case CONTINUE_OP_SAME :
				{
					// FIX use canon complaint
					throw new RuntimeException( here +"continuation started without assignment "+ currToken );
				}
				case COPY_OP_DEEP :
				case COPY_OP_THIN :
				{
					// FIx use canon complaint
					throw new RuntimeException( here +"template started without name "+ currToken );
				}
				case FIELD_START_OP :
				{
					// FIx use canon complaint
					throw new RuntimeException( here +"field assignment started without name "+ currToken );
				}
				case LIST_OP :
				{
					// FIx use canon complaint
					throw new RuntimeException( here +"list started without name "+ currToken );
				}
				case SET_OP :
				{
					// FIx use canon complaint
					throw new RuntimeException( here +"set started without name "+ currToken );
				}
				case TEXT :
				default :
				{
					fieldBeginningOld( unescapedName( DELIM_FIELD_COPY ) );
					break;
				}
			}
		}
	}


	/** the name and maybe a copy instruction */
	private void sectionBeginning()
	{
		String here = cl +"section ";
		Phrase declaration = new Phrase();
		declaration.type = Syntaxeme.SECTION;
		declaration.words = currToken.word;
		nextToken();
		if ( currToken.type == WHITESPACE )
			nextToken();
		if ( currToken.type == ESCAPE_OP )
		{
			escapedName();
		}
		else if ( ! DELIM_END_COPY.contains( currToken.type ) )
		{
			unescapedName( DELIM_END_COPY );
		}
		else
		{
			// FIX use canon complaint
			throw new RuntimeException( here +"section name should not start with "+ currToken );
		}
		if ( currToken.type == WHITESPACE )
			nextToken();
		if ( isCopyOperator( currToken.type ) )
		{
			templateInstruction();
		}
		currState = ParseContext.SECTION_INTERIOR;
	}


	/** A line with first non whitespace is > , ex > comment.
	 * Advances the line */
	private void comment()
	{
		String here = cl +"comment ";
		nextToken();
		Phrase fullComment = new Phrase();
		fullComment.type = COMMENT;
		fullComment.words = alphabet.restOfLine().trim();
		System.out.println( here +"recognized "+ fullComment );
		nextLine();
	}


	/** ex `banana` || `` aba`ana`` */
	private Phrase escapedName()
	{
		String here = cl +"escaped ";
		Phrase escape = new Phrase();
		escape.type = FIELD_ESCAPE;
		escape.words = currToken.word;
		// get the name
		nextToken();
		if ( currToken.type == WHITESPACE )
			nextToken();
		StringBuilder namePieces = new StringBuilder();
		String lastNibble = "";
		Lexeme lastLex = null;
		Phrase name = new Phrase();
		name.type = FIELD;
		do
		{
			if ( currToken.type == END )
			{
				// FIX use canon complaint
				throw new RuntimeException( here +"opened escape name without closing before eol" );
			}
			else if ( currToken.type == ESCAPE_OP
					&& currToken.word.length() == escape.words.length()
					&& ( namePieces.length() >= 1 || ! lastNibble.isEmpty() ) )
			{
				// this is it
				if ( lastLex != WHITESPACE )
				{
					namePieces.append( lastNibble );
				}
				// lastNibble is never white on first pass, as it will stay empty that round
				name.words = namePieces.toString();
				nextToken();
				break;
			}
			else
			{
				namePieces.append( lastNibble );
				lastNibble = currToken.word;
				nextToken();
			}
		}
		while ( true );
		System.out.println( here +"recognized "+ escape );
		System.out.println( here +"recognized "+ name );
		return name;
	}


	/** a name for section, field, or set item */
	private Phrase unescapedName( Set<Lexeme> delimiters )
	{
		String here = cl +"un ";
		if ( currToken.type == WHITESPACE )
			nextToken();
		Phrase name = new Phrase();
		name.type = FIELD;
		String lastPiece = currToken.word;
		Lexeme lastType = currToken.type;
		StringBuilder pieces = new StringBuilder();
		nextToken();
		do
		{
			if ( delimiters.contains( currToken.type ) )
			{
				if ( lastType != WHITESPACE )
				{
					pieces.append( lastPiece );
				}
				name.words = pieces.toString();
				break;
			}
			else
			{
				pieces.append( lastPiece );
				lastPiece = currToken.word;
				nextToken();
			}
		}
		while ( true );
		System.out.println( here +"recognized "+ name );
		return name;
	}


	/** consume lines until one matches the first line */
	private void multilineBoundary()
	{
		String here = cl +"multiline ";
		Phrase boundary = new Phrase();
		boundary.words = currToken.word;
		boundary.type = MULTILINE_BOUNDARY;
		int blockStartedAt = currLine;

		// get the name
		nextToken();
		if ( currToken.type == WHITESPACE )
			nextToken();
		Phrase leadingName;
		if ( currToken.type == ESCAPE_OP )
		{
			leadingName = escapedName();
		}
		else
		{
			leadingName = unescapedName( DELIM_END );
		}
		// save the rest as body until we match the block boundary
		nextLine( true, here +"opened multiline "+ blockStartedAt +" without closing before eof" );
		StringBuilder blockBodyWords = new StringBuilder();
		Phrase secondName = null;
		Phrase secondBoundary = new Phrase();
		secondBoundary.type = MULTILINE_BOUNDARY;
		do
		{
			if ( currToken.type == WHITESPACE )
				nextToken();
			if ( currToken.type == MULTILINE_OP
					&& boundary.words.length() == currToken.word.length() )
			{
				secondBoundary.words = currToken.word;
				nextToken();
				if ( currToken.type == WHITESPACE )
					nextToken();
				// try to get a name from it, check for match
				if ( currToken.type == END )
				{
					blockBodyWords.append( System.lineSeparator() );
					blockBodyWords.append( alphabet.getLine() );
					// FIX use canon complaint
					nextLine( true, here +"opened multiline "+ blockStartedAt +" without closing before eof" );
					continue;
				}
				else if ( currToken.type == ESCAPE_OP )
				{
					secondName = escapedName();
				}
				else
				{
					secondName = unescapedName( DELIM_END );
				}
				// check if the name matches
				if ( secondName.words.equals( leadingName.words ) )
				{
					// entire boundary matches, block is over
					break;
				}
				else
				{
					blockBodyWords.append( System.lineSeparator() );
					blockBodyWords.append( alphabet.getLine() );
					// FIX use canon complaint
					nextLine( true, here +"opened multiline "+ blockStartedAt +" without closing before eof" );
				}
			}
			else
			{
				blockBodyWords.append( System.lineSeparator() );
				blockBodyWords.append( alphabet.getLine() );
				// FIX use canon complaint
				nextLine( true, here +"opened multiline "+ blockStartedAt +" without closing before eof" );
			}
		}
		while ( true ); // leave by finding second boundary
		Phrase blockBody = new Phrase();
		blockBody.type = MULTILINE_TEXT;
		if ( blockBodyWords.length() > 0 )
		{
			blockBody.words = blockBodyWords.substring(
					System.lineSeparator().length() ); // NOTE remove extra prefix
		}
		currState = ParseContext.SECTION_INTERIOR;
		System.out.println( here +"recognized "+ boundary );
		System.out.println( here +"recognized "+ blockBody );
		nextToken();
	}


	/** get the name, basically */
	private Phrase templateInstruction()
	{
		Phrase operator = new Phrase();
		operator.words = currToken.word;
		if ( currToken.type == COPY_OP_THIN )
		{
			operator.type = SHALLOW_COPY;
		}
		else
		{
			operator.type = DEEP_COPY;
		}
		nextToken();
		if ( currToken.type == WHITESPACE )
			nextToken();
		Phrase targetName = new Phrase();
		if ( currToken.type == ESCAPE_OP )
		{
			targetName = escapedName();
		}
		else
		{
			targetName = unescapedName( DELIM_END );
		}
		return targetName;
	}


	private Phrase fieldAny( Phrase fieldName )
	{
		String here = cl +"fa ";
		if ( currToken.type == FIELD_START_OP )
			nextToken();
		if ( currToken.type == WHITESPACE )
			nextToken();
		if ( isCopyOperator( currToken.type ) )
		{
			templateInstruction();
		}
		Phrase value = new Phrase();
		value.type = VALUE;
		value.words = alphabet.restOfLine().trim();
		nextLine();
		if ( currToken.type == WHITESPACE )
			nextToken();
		if ( isContinuationOperator( currToken.type ) )
		{
			value = moreValue( value );
			System.out.println( here +"recognized "+ value );
		}
		else if ( currToken.type == Lexeme.SECTION_OP
				|| currToken.type == Lexeme.MULTILINE_OP )
		{
			return value;
		}
		else if ( currToken.type == Lexeme.LIST_OP )
		{
			listElements( fieldName );
		}
		else if ( isCopyOperator( currToken.type ) )
		{
			// FIx use canon complaint
			throw new RuntimeException( here +"template started without name "+ currToken );
		}
		else
		{
			Phrase nextName;
			if ( currToken.type == Lexeme.ESCAPE_OP )
			{
				nextName = escapedName();
			}
			else
			{
				nextName = unescapedName( DELIM_SET_FIELD_COPY );
			}
			// NP here ; maybe handle end of document ? or just keep punting up to section
			if ( currToken.type == Lexeme.SET_OP )
			{
				listElements( fieldName );
			}
			else if ( currToken.type == Lexeme.FIELD_START_OP )
			{
				return fieldAny( fieldName );
			} 
		}

		return null; // TODO fieldInteriorOld( value );
	}


	private Phrase moreValue( Phrase begunValue )
	{
		// list of comments = new list
		while ( true )
		{
			if ( currToken.type == Lexeme.CONTINUE_OP_SAME )
			{
				nextToken();
				begunValue.words += " "+ alphabet.restOfLine().trim();
			}
			else if ( currToken.type == Lexeme.CONTINUE_OP_SAME )
			{
				nextToken();
				// IMPROVE switch to empty string if rfc changes break
				begunValue.words += System.lineSeparator()+ alphabet.restOfLine().trim();
			}
			else if ( currToken.type == Lexeme.WHITESPACE )
			{
				nextToken();
			}
			else if ( currToken.type == Lexeme.END )
			{
				nextLine();
			}
			else if ( currToken.type == Lexeme.COMMENT_OP )
			{
				comment(); // Improve list of comments.add ( comment() );
			}
			else
			{
				break;
			}
		}
		return begunValue;
	}


	private Phrase listElements( Phrase name )
	{
		while ( true )
		{
			if ( currToken.type == WHITESPACE )
				nextToken();
			else if ( currToken.type == Lexeme.LIST_OP )
			{
				nextToken();
				Phrase listSubnode = new Phrase();
				listSubnode.type = Syntaxeme.LIST_ELEMENT;
				listSubnode.words = alphabet.restOfLine();
				listSubnode = moreValue( listSubnode );
			}
			else if ( currToken.type == Lexeme.END )
			{
				nextLine();
			}
			else
			{
				break;
			}
		}
		return name;
	}


	private Phrase fieldBeginningOld( Phrase fieldName )
	{
		String here = cl +"fb ";
		if ( currToken.type == FIELD_START_OP )
			nextToken();
		if ( currToken.type == WHITESPACE )
			nextToken();
		if ( isCopyOperator( currToken.type ) )
		{
			templateInstruction();
		}
		Phrase value = new Phrase();
		value.type = VALUE;
		String restOfFieldLine = alphabet.restOfLine().trim();
		if ( ! restOfFieldLine.isEmpty() )
		{
			value.words = restOfFieldLine;
			System.out.println( here +"recognized "+ value );
		}
		return fieldInteriorOld( value );
	}


	/** get the set / list items or continued value */
	private Phrase fieldInteriorOld( Phrase valueFromInitial )
	{
		String here = cl +"fi ";
		nextLine();
		ParseContext styleOfValue = ( ! valueFromInitial.words.isEmpty() )
				? ParseContext.VALUE : ParseContext.DOCUMENT;
		Phrase tempName;
		while ( true )
		{
			switch ( currToken.type )
			{
				case END :
				{
					if ( ! nextLine() )
					{
						System.out.println( here +"recognized a valid eno document" );
						return null;
					}
					break;
				}
				case COMMENT_OP :
				{
					comment();
					// comments can separate field interior sections
					break;
				}
				case SECTION_OP :
				case MULTILINE_OP :
				{
					// handle at section level
					return valueFromInitial;
				}
				case WHITESPACE :
				{
					nextToken();
					break;
				}
				case CONTINUE_OP_BREAK :
				{
					nextToken();
					String restOfLine = alphabet.restOfLine().trim();
					valueFromInitial.words += System.lineSeparator()
							+ restOfLine;
					nextLine();
					break;
				}
				case CONTINUE_OP_SAME :
				{
					nextToken();
					boolean spaceIsAppropriate = true;
					String continuation = ( spaceIsAppropriate ) ? " " : "";
					String restOfLine = alphabet.restOfLine().trim();
					valueFromInitial.words += continuation + restOfLine;
					nextLine();
					break;
				}
				case LIST_OP :
				{
					if ( currToken.word.length() > 1 )
					{
						// NOTE it's a multiline boundary
						return valueFromInitial;
					}
					if ( styleOfValue == ParseContext.DOCUMENT )
						styleOfValue = ParseContext.LIST;
					else if ( styleOfValue != ParseContext.LIST )
						// FIX use canon complaint
						throw new RuntimeException( here +"non list found list "+ currLine );
					Phrase listItem = new Phrase();
					listItem.type = Syntaxeme.LIST_ELEMENT;
					listItem.words = alphabet.restOfLine().trim();
					System.out.println( here +"recognized li "+ listItem );
					nextLine();
					break;
				}
				case ESCAPE_OP :
				{
					tempName = nameInFieldInteriorOld( escapedName(), styleOfValue );
					if ( tempName.type == FIELD )
						return tempName; // NOTE a new field
					else
						nextLine();
					break;
				}
				default :
				{
					tempName = nameInFieldInteriorOld( unescapedName( DELIM_SET_FIELD_COPY ),
							styleOfValue );
					if ( tempName.type == FIELD )
						return tempName; // NOTE a new field
					else
						nextLine();
					break;
				}
			}
		}
	}


	private Phrase nameInFieldInteriorOld( Phrase nameFound, ParseContext context )
	{
		String here = cl +"nifi ";
		//nextToken();
		if ( currToken.type == WHITESPACE )
			nextToken();
		if ( currToken.type == SET_OP )
		{
			if ( context == ParseContext.VALUE )
			{
				System.out.println( here +"set started after a value "
						+ nameFound.words );
				// FIX use canon complaint
				throw new RuntimeException( here +"section name should not start with "+ currToken );
			}
			else
			{
				context = ParseContext.SET;
				nameFound.type = Syntaxeme.SET;
			}
			nextToken();
			Phrase setValue = new Phrase();
			setValue.type = Syntaxeme.SET_ELEMENT;
			setValue.words = alphabet.restOfLine().trim();
			System.out.println( here +"recognized "+ nameFound );
			System.out.println( here +"recognized "+ setValue );
		}
		else
		{
			fieldBeginningOld( nameFound );
		}
		return nameFound;
	}


	private boolean isCopyOperator( Lexeme something )
	{
		// ASK not clear whether that's a lex or parse domain
		return something == Lexeme.COPY_OP_DEEP
				|| something == Lexeme.COPY_OP_THIN;
	}


	private boolean isContinuationOperator( Lexeme something )
	{
		// ASK not clear whether that's a lex or parse domain
		return something == Lexeme.CONTINUE_OP_BREAK
				|| something == Lexeme.CONTINUE_OP_SAME;
	}


	/** get next token from lexer */
	private void nextToken()
	{
		currToken = alphabet.nextToken();
	}


	/** advance without complaining */
	private boolean nextLine()
	{
		return nextLine( false, "" );
	}


	/** @return whether line has input */
	private boolean nextLine( boolean complainWhenFileEnds, String endOfFileComplaint )
	{
		String temp = allLines.poll();
		if ( temp == null )
		{
			if ( complainWhenFileEnds )
			{
				throw new RuntimeException( endOfFileComplaint );
			}
			else
			{
				currToken.type = END;
				currToken.word = "";
				return false;
			}
		}
		else
		{
			alphabet.setLine( temp );
			nextToken();
			currLine++;
			return true;
		}
	}


	

}





































