/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import static ws.nzen.format.eno.Lexeme.*;
import static ws.nzen.format.eno.Syntaxeme.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import org.eno_lang.locale.EnoAlias;

/**  */
public class Parser
{
	private static final String cl = "p.";
	protected static final Set<Lexeme> DELIM_END_COPY = new TreeSet<>();
	protected static final Set<Lexeme> DELIM_SET_FIELD_COPY = new TreeSet<>();
	protected static final Set<Lexeme> DELIM_FIELD_COPY = new TreeSet<>();
	protected static final Set<Lexeme> DELIM_END = new TreeSet<>();
	protected Queue<String> allLines = new LinkedList<>();
	protected Lexer alphabet = new Lexer();
	protected Lexer.Token currToken;
	protected int currLine = 0;
	protected ParseContext currState = ParseContext.SECTION_INTERIOR;
	// eventually handle exception store localization, output formatter
	protected ResourceBundle rbToken;
	protected ResourceBundle rbAnalysis;
	protected ResourceBundle rbValidation;
	/** Word.modifier value for empty continuation, ie | */
	public static final int WORD_MOD_CONT_EMPTY = 1;
	/** Word.modifier value for space continuation, ie \ */
	public static final int WORD_MOD_CONT_SPACE
			= WORD_MOD_CONT_EMPTY +1;
	public class Phrase
	{
		public Syntaxeme type;
		public String words = "";
		@Override
		public String toString()
		{
			return "PT: t-"+ type.name() +" w-"+ words;
		}
	}
	public class Word
	{
		public Syntaxeme type;
		public String value = "";
		public int modifier = 0;
		@Override
		public String toString()
		{
			return "W: t-"+ type.name() +" "+ modifier +" w-"+ value;
		}
	}


	public Parser()
	{
		prepFieldDelimiters();
		prepExceptionMessages();
	}


	protected void prepFieldDelimiters()
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


	protected void prepExceptionMessages()
	{
		String filePrefixTokenize = "Tokenization";
		rbToken = ResourceBundle.getBundle( filePrefixTokenize );
		String filePrefixAnalysis = "Analysis";
		rbAnalysis = ResourceBundle.getBundle( filePrefixAnalysis );
		String filePrefixValidation = "Validation";
		rbValidation = ResourceBundle.getBundle( filePrefixValidation );
	}


	/** Classifies the lines of this document..
	 * @throws RuntimeException otherwise */
	public List<List<Word>> parse( List<String> fileLines )
	{
		String here = cl +"p ";
		if ( fileLines == null || fileLines.isEmpty() )
		{
			return new LinkedList<>();
		}
		else
		{
			allLines = new LinkedList<>( fileLines );
		}
		List<List<Word>> parsed = new ArrayList<>( fileLines.size() );
		int emptyLines = 0;
		Word currWord;
		List<Word> wordsOfLine;
		nextLine();
		do
		{
			wordsOfLine = new LinkedList<>();
			skipWhitespace();
			switch ( currToken.type )
			{
				case END :
				{
					emptyLines++;
					break;
				}
				case COPY_OP_DEEP :
				case COPY_OP_THIN :
				{
					// NOTE line looks like < nn ;; It needs a name
					MessageFormat problem = new MessageFormat(
							rbToken.getString( EnoAlias.INVALID_LINE ) );
					throw new RuntimeException( problem.format( new Object[]{ currLine } ) );
				}
				case FIELD_START_OP :
				{
					// NOTE names can't start with :
					MessageFormat problem = new MessageFormat(
							rbToken.getString( EnoAlias.INVALID_LINE ) );
					throw new RuntimeException( problem.format( new Object[]{ currLine } ) );
				}
				case SET_OP :
				{
					MessageFormat problem = new MessageFormat(
							rbAnalysis.getString( EnoAlias.MISSING_NAME_FOR_FIELDSET_ENTRY ) );
					throw new RuntimeException( problem.format( new Object[]{ currLine } ) );
				}
				case CONTINUE_OP_EMPTY :
				case CONTINUE_OP_SPACE :
				{
					if ( emptyLines > 0 )
					{
						wordsOfLine.add( emptyLines( emptyLines ) );
						emptyLines = 0;
					}
					currWord = new Word();
					currWord.type = Syntaxeme.VALUE;
					currWord.modifier = ( currToken.type == CONTINUE_OP_SPACE )
							? WORD_MOD_CONT_SPACE
							: WORD_MOD_CONT_EMPTY;
					currWord.value = alphabet.restOfLine().trim();
					wordsOfLine.add( currWord );
					parsed.add( wordsOfLine );
					break;
				}
				case LIST_OP :
				{
					if ( emptyLines > 0 )
					{
						wordsOfLine.add( emptyLines( emptyLines ) );
						emptyLines = 0;
					}
					currWord = new Word();
					currWord.type = Syntaxeme.LIST_ELEMENT;
					currWord.value = alphabet.restOfLine().trim();
					wordsOfLine.add( currWord );
					parsed.add( wordsOfLine );
					break;
				}
				case COMMENT_OP :
				{
					if ( emptyLines > 0 )
					{
						wordsOfLine.add( emptyLines( emptyLines ) );
						emptyLines = 0;
					}
					currWord = new Word();
					currWord.type = Syntaxeme.COMMENT;
					currWord.value = alphabet.restOfLine().trim();
					wordsOfLine.add( currWord );
					parsed.add( wordsOfLine );
					break;
				}
				case MULTILINE_OP :
				{
					if ( emptyLines > 0 )
					{
						wordsOfLine.add( emptyLines( emptyLines ) );
						emptyLines = 0;
					}
					parsed.add( multiline( wordsOfLine ) );
					break;
				}
				case SECTION_OP :
				{
					if ( emptyLines > 0 )
					{
						wordsOfLine.add( emptyLines( emptyLines ) );
						emptyLines = 0;
					}
					parsed.add( section( wordsOfLine ) );
					break;
				}
				case ESCAPE_OP :
				default :
				{
					if ( emptyLines > 0 )
					{
						wordsOfLine.add( emptyLines( emptyLines ) );
						emptyLines = 0;
					}
					parsed.add( fieldAny( wordsOfLine ) );
					break;
				}
			}
		}
		while ( nextLine() );
		if ( parsed.isEmpty() && emptyLines > 0 )
		{
			wordsOfLine = new LinkedList<>();
			wordsOfLine.add( emptyLines( emptyLines ) );
			parsed.add( wordsOfLine );
		}
		return parsed;
	}


	/** op, name, maybe template */
	private List<Word> section( List<Word> line )
	{
		String here = cl +"section ";
		if ( line == null )
		{
			line = new LinkedList<>();
		}
		if ( currToken.type == Lexeme.SECTION_OP )
		{
			Word sectionOperator = new Word();
			sectionOperator.type = SECTION;
			sectionOperator.modifier = currToken.word.length();
			line.add( sectionOperator );
			nextToken();
		}
		skipWhitespace();
		if ( currToken.type == ESCAPE_OP )
		{
			line = escapedName( line );
		}
		else if ( ! DELIM_END_COPY.contains( currToken.type ) )
		{
			line = unescapedName( line, DELIM_END_COPY );
		}
		else
		{
			// NOTE line looks like # < nn OR # /n;; It needs a name
			MessageFormat problem = new MessageFormat(
					rbToken.getString( EnoAlias.INVALID_LINE ) );
			throw new RuntimeException( problem.format( new Object[]{ currLine } ) );
		}
		skipWhitespace();
		if ( isCopyOperator( currToken.type ) )
		{
			line = template( line );
		}
		return line;
	}


	/** Recognize token sequence of ` text ` with matching number of
	 * backticks at the border. Word for name has a modifier for the
	 * number of escape chars */
	protected List<Word> escapedName( List<Word> line )
	{
		String here = cl +"escaped name ";
		if (line == null )
		{
			line = new LinkedList<>();
		}
		if ( currToken.type == ESCAPE_OP )
		{
			nextToken();
		}
		skipWhitespace();
		StringBuilder namePieces = new StringBuilder();
		String lastNibble = "";
		Lexeme lastLex = null;
		Word name = new Word();
		name.type = Syntaxeme.FIELD;
		name.modifier = currToken.word.length();
		do
		{
			if ( currToken.type == END )
			{
				MessageFormat problem = new MessageFormat(
						rbToken.getString( EnoAlias.UNTERMINATED_ESCAPED_NAME ) );
				throw new RuntimeException( problem.format( new Object[]{ currLine } ) );
			}
			else if ( currToken.type == ESCAPE_OP
					&& currToken.word.length() == name.modifier
					&& ( namePieces.length() >= 1 || ! lastNibble.isEmpty() ) )
			{
				// NOTE this is the closing escape boundary
				if ( lastLex != WHITESPACE )
				{
					namePieces.append( lastNibble );
				}
				name.value = namePieces.toString();
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
		line.add( name );
		return line;
	}


	/** text trimmed of white, delimited by specified lexemes */
	protected List<Word> unescapedName( List<Word> line, Set<Lexeme> delimiters )
	{
		String here = cl +"un ";
		if ( line == null )
		{
			line = new LinkedList<>();
		}
		skipWhitespace();
		Word name = new Word();
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
				name.value = pieces.toString();
				break;
			}
			else if ( currToken.type == END )
			{
				// NOTE end isn't a delimiter right now, but we've exhausted input
				MessageFormat problem = new MessageFormat(
						rbToken.getString( EnoAlias.EXCESS_NAME ) );
				String complaint = problem.format( new Object[]{ pieces.toString() } );
				throw new RuntimeException( complaint );
			}
			else
			{
				pieces.append( lastPiece );
				lastPiece = currToken.word;
				lastType = currToken.type;
				nextToken();
			}
		}
		while ( true );
		line.add( name );
		return line;
	}


	/** consume lines until one matches the first line */
	private List<Word> multiline( List<Word>entireBlock )
	{
		String here = cl +"multiline ";
		if ( entireBlock == null )
		{
			entireBlock = new LinkedList<>();
		}
		Word boundary = new Word();
		boundary.type = Syntaxeme.MULTILINE_BOUNDARY;
		boundary.modifier = currToken.word.length();
		entireBlock.add( boundary );
		// NOTE get the name
		nextToken();
		skipWhitespace();
		if ( currToken.type == Lexeme.ESCAPE_OP )
		{
			entireBlock = escapedName( entireBlock );
		}
		else
		{
			entireBlock = unescapedName( entireBlock, DELIM_END );
		}
		// NOTE save the rest as body until we match the block boundary
		int blockStartedAt = currLine;
		Word multilineIdentifier = entireBlock.get( entireBlock.size() -1 );
		Word secondIdentifier;
		List<Word> lineProxy = new ArrayList<>( 2 );
		StringBuilder multilineText = new StringBuilder();
		MessageFormat problem = new MessageFormat(
				rbToken.getString( EnoAlias.UNTERMINATED_BLOCK ) );
		String complaint = problem.format( new Object[]{ multilineIdentifier.value, blockStartedAt } );
		nextLine( true, complaint );
		do
		{
			skipWhitespace();
			if ( currToken.type == MULTILINE_OP
					&& currToken.word.length() == boundary.modifier )
			{
				nextToken();
				skipWhitespace();
				if ( currToken.type == END )
				{
					multilineText.append( System.lineSeparator() );
					multilineText.append( alphabet.getLine() );
					nextLine( true, complaint );
					continue;
				}
				else if ( currToken.type == ESCAPE_OP
						&& multilineIdentifier.modifier > 0 )
				{
					lineProxy = escapedName( lineProxy );
				}
				else
				{
					lineProxy = unescapedName( lineProxy, DELIM_END );
				}
				// NOTE check if the name matches
				secondIdentifier = lineProxy.get( lineProxy.size() -1 );
				if ( multilineIdentifier.value == secondIdentifier.value
						&& multilineIdentifier.modifier == secondIdentifier.modifier )
				{
					// NOTE entire boundary matches, block is over, not saving the bottom
					break;
				}
				else
				{
					// NOTE not a match, continue
					multilineText.append( System.lineSeparator() );
					multilineText.append( alphabet.getLine() );
					lineProxy.clear();
					nextLine( true, complaint );
					continue;
				}
			}
			else
			{
				multilineText.append( System.lineSeparator() );
				multilineText.append( alphabet.getLine() );
				lineProxy.clear();
				nextLine( true, complaint );
			}
		}
		while ( true );
		Word mulilineValue = new Word();
		mulilineValue.type = Syntaxeme.MULTILINE_TEXT;
		if ( multilineText.length() > 0 )
		{
			// NOTE cut leading newline
			mulilineValue.value = multilineText
					.substring( System.lineSeparator().length() );
		}
		entireBlock.add( mulilineValue );
		return entireBlock;
	}


	/** op, name */
	protected List<Word> template( List<Word> line )
	{
		if ( line == null )
		{
			line = new LinkedList<>();
		}
		if ( ! isCopyOperator( currToken.type ) )
		{
			throw new RuntimeException( cl +"template unable to record copy depth;"
					+ " cursor has moved past it "+ currToken );
		}
		Word copyOperator = new Word();
		copyOperator.type = ( currToken.type == Lexeme.COPY_OP_THIN )
				? SHALLOW_COPY : DEEP_COPY;
		copyOperator.modifier = currToken.word.length();
		line.add( copyOperator );
		nextToken();
		skipWhitespace();
		if ( currToken.type == Lexeme.ESCAPE_OP )
		{
			line = escapedName( line );
		}
		else
		{
			line = unescapedName( line, DELIM_END );
		}
		return line;
	}


	/** name, value or template */
	protected List<Word> fieldAny( List<Word> line )
	{
		String here = cl +"field or set ";
		if ( line == null )
		{
			line = new LinkedList<>();
		}
		if ( currToken.type == Lexeme.ESCAPE_OP )
		{
			line = escapedName( line );
		}
		else
		{
			line = unescapedName( line, DELIM_SET_FIELD_COPY );
		}
		if ( currToken.type == Lexeme.SET_OP )
		{
			nextToken();
			Word setEntry = new Word();
			setEntry.type = Syntaxeme.SET_ELEMENT;
			setEntry.value = alphabet.restOfLine().trim();
			line.add( setEntry );
		}
		else if ( currToken.type == FIELD_START_OP )
		{
			nextToken();
			skipWhitespace();
			if ( isCopyOperator( currToken.type ) )
			{
				line = template( line );
			}
		}
		else if ( isCopyOperator( currToken.type ) )
		{
			line = template( line );
		}
		else
		{
			Word value = new Word();
			value.type = Syntaxeme.VALUE;
			value.value = alphabet.restOfLine().trim();
			line.add( value );
		}
		return line;
	}


	@Deprecated
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


	@Deprecated
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
				case CONTINUE_OP_EMPTY :
				case CONTINUE_OP_SPACE :
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
					fieldAny( recognizeEscapedName() );
					break;
				}
				default :
				{
					fieldAny( recognizeUnescapedName( DELIM_FIELD_COPY ) );
					break;
				}
			}
		}
	}


	@Deprecated
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
					fieldBeginningOld( recognizeEscapedName() );
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
				case CONTINUE_OP_EMPTY :
				case CONTINUE_OP_SPACE :
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
					fieldBeginningOld( recognizeUnescapedName( DELIM_FIELD_COPY ) );
					break;
				}
			}
		}
	}


	@Deprecated
	/** the name and maybe a copy instruction */
	private void sectionBeginning()
	{
		String here = cl +"section ";
		Phrase declaration = new Phrase();
		declaration.type = Syntaxeme.SECTION;
		declaration.words = currToken.word;
		nextToken();
		skipWhitespace();
		if ( currToken.type == ESCAPE_OP )
		{
			recognizeEscapedName();
		}
		else if ( ! DELIM_END_COPY.contains( currToken.type ) )
		{
			recognizeUnescapedName( DELIM_END_COPY );
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


	@Deprecated
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


	@Deprecated
	/** ex `banana` || `` aba`ana`` */
	private Phrase recognizeEscapedName()
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


	@Deprecated
	/** a name for section, field, or set item */
	private Phrase recognizeUnescapedName( Set<Lexeme> delimiters )
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


	@Deprecated
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
			leadingName = recognizeEscapedName();
		}
		else
		{
			leadingName = recognizeUnescapedName( DELIM_END );
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
					secondName = recognizeEscapedName();
				}
				else
				{
					secondName = recognizeUnescapedName( DELIM_END );
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


	@Deprecated
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
			targetName = recognizeEscapedName();
		}
		else
		{
			targetName = recognizeUnescapedName( DELIM_END );
		}
		return targetName;
	}


	@Deprecated
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
				nextName = recognizeEscapedName();
			}
			else
			{
				nextName = recognizeUnescapedName( DELIM_SET_FIELD_COPY );
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


	@Deprecated
	private Phrase moreValue( Phrase begunValue )
	{
		// list of comments = new list
		while ( true )
		{
			if ( currToken.type == Lexeme.CONTINUE_OP_SPACE )
			{
				nextToken();
				begunValue.words += " "+ alphabet.restOfLine().trim();
			}
			else if ( currToken.type == Lexeme.CONTINUE_OP_SPACE )
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


	@Deprecated
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


	@Deprecated
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


	@Deprecated
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
				case CONTINUE_OP_EMPTY :
				{
					nextToken();
					String restOfLine = alphabet.restOfLine().trim();
					valueFromInitial.words += System.lineSeparator()
							+ restOfLine;
					nextLine();
					break;
				}
				case CONTINUE_OP_SPACE :
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
					tempName = nameInFieldInteriorOld( recognizeEscapedName(), styleOfValue );
					if ( tempName.type == FIELD )
						return tempName; // NOTE a new field
					else
						nextLine();
					break;
				}
				default :
				{
					tempName = nameInFieldInteriorOld( recognizeUnescapedName( DELIM_SET_FIELD_COPY ),
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


	@Deprecated
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


	protected Word emptyLines( int preceedingEmptyLines )
	{
		Word combined = new Word();
		combined.type = Syntaxeme.EMPTY;
		combined.modifier = preceedingEmptyLines;
		return combined;
	}


	/** either copy operator */
	protected boolean isCopyOperator( Lexeme something )
	{
		// ASK not clear whether that's a lex or parse domain
		return something == Lexeme.COPY_OP_DEEP
				|| something == Lexeme.COPY_OP_THIN;
	}


	/** either continuation operator */
	protected boolean isContinuationOperator( Lexeme something )
	{
		return something == Lexeme.CONTINUE_OP_EMPTY
				|| something == Lexeme.CONTINUE_OP_SPACE;
	}


	protected void skipWhitespace()
	{
		if ( currToken.type == WHITESPACE )
			nextToken();
	}


	/** get next token from lexer */
	protected void nextToken()
	{
		currToken = alphabet.nextToken();
	}


	/** advance without complaining */
	protected boolean nextLine()
	{
		return nextLine( false, "" );
	}


	/** @return whether line has input */
	protected boolean nextLine( boolean complainWhenFileEnds, String endOfFileComplaint )
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





































