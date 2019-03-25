/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno.parse;

import static ws.nzen.format.eno.parse.Lexeme.*;
import static ws.nzen.format.eno.parse.Syntaxeme.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import ws.nzen.format.eno.EnoLocaleKey;
import ws.nzen.format.eno.ExceptionStore;

/** Convert list of String to List List of Parser.Word */
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
	/** Word.modifier value for empty continuation, ie | */
	public static final int WORD_MOD_CONT_EMPTY = 1;
	/** Word.modifier value for space continuation, ie \ */
	public static final int WORD_MOD_CONT_SPACE
			= WORD_MOD_CONT_EMPTY +1;
	public class Word
	{
		public Syntaxeme type;
		public String value = "";
		public int modifier = 0;
		public int line = 0;
		@Override
		public String toString()
		{
			return "W: t-"+ type.name() +" "+ modifier +" w-"+ value;
		}
	}


	public Parser()
	{
		prepFieldDelimiters();
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
							ExceptionStore.getStore().getExceptionMessage(
									ExceptionStore.TOKENIZATION, EnoLocaleKey.INVALID_LINE ) );
					throw new RuntimeException( problem.format( new Object[]{ currLine } ) );
				}
				case FIELD_START_OP :
				{
					// NOTE names can't start with :
					MessageFormat problem = new MessageFormat(
							ExceptionStore.getStore().getExceptionMessage(
									ExceptionStore.TOKENIZATION, EnoLocaleKey.INVALID_LINE ) );
					throw new RuntimeException( problem.format( new Object[]{ currLine } ) );
				}
				case SET_OP :
				{
					MessageFormat problem = new MessageFormat(
							ExceptionStore.getStore().getExceptionMessage(
									ExceptionStore.ANALYSIS, EnoLocaleKey
										.MISSING_NAME_FOR_FIELDSET_ENTRY ) );
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
					currWord.line = currLine;
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
					currWord.line = currLine;
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
					currWord.value = alphabet.restOfLine();
					currWord.line = currLine;
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
			sectionOperator.line = currLine;
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
					ExceptionStore.getStore().getExceptionMessage(
							ExceptionStore.TOKENIZATION, EnoLocaleKey.INVALID_LINE ) );
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
		if ( currToken.type != ESCAPE_OP )
		{
			throw new RuntimeException( here
					+"parser should not have moved past escape chars" );
		}
		if (line == null )
		{
			line = new LinkedList<>();
		}
		Word name = new Word();
		name.type = Syntaxeme.FIELD;
		name.modifier = currToken.word.length();
		name.line = currLine;
		nextToken();
		skipWhitespace();
		StringBuilder namePieces = new StringBuilder();
		String lastNibble = "";
		Lexeme lastLex = null;
		do
		{
			if ( currToken.type == END )
			{
				MessageFormat problem = new MessageFormat(
						ExceptionStore.getStore().getExceptionMessage(
								ExceptionStore.TOKENIZATION, EnoLocaleKey
									.UNTERMINATED_ESCAPED_NAME ) );
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
		name.line = currLine;
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
						ExceptionStore.getStore().getExceptionMessage(
								ExceptionStore.VALIDATION, EnoLocaleKey.EXCESS_NAME ) );
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
		boundary.line = currLine;
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
				ExceptionStore.getStore().getExceptionMessage(
						ExceptionStore.TOKENIZATION, EnoLocaleKey
							.UNTERMINATED_BLOCK ) );
		String complaint = problem.format( new Object[]{ multilineIdentifier.value, blockStartedAt } );
		nextLine( true, complaint );
		do
		{
			skipWhitespace();
			if ( currToken.type == MULTILINE_OP
					&& currToken.word.length() == boundary.modifier )
			{
				nextToken();
				// NOTE this is just for line proxy, we'll get the whole line if this isn't a boundary
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
				if ( multilineIdentifier.value.equals( secondIdentifier.value )
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
		copyOperator.type = COPY;
		copyOperator.modifier = currToken.word.length();
		copyOperator.line = currLine;
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
		if ( currToken.type == ESCAPE_OP )
		{
			line = escapedName( line );
			nextToken();
		}
		else
		{
			line = unescapedName( line, DELIM_SET_FIELD_COPY );
		}
		skipWhitespace();
		if ( currToken.type == SET_OP )
		{
			nextToken();
			if ( currToken.type != END )
			{
				// NOTE distinguishing field from set, as not all will have a value
				line.get( line.size() -1 ).type = SET_ELEMENT;
				Word value = new Word();
				value.type = VALUE;
				value.value = currToken.word.trim() + alphabet.restOfLine().trim();
				value.line = currLine;
				line.add( value );
			}
		}
		else if ( currToken.type == FIELD_START_OP )
		{
			nextToken();
			skipWhitespace();
			if ( isCopyOperator( currToken.type ) )
			{
				line = template( line );
			}
			else if ( currToken.type != END )
			{
				Word value = new Word();
				value.type = VALUE;
				value.value = currToken.word.trim() + alphabet.restOfLine().trim();
				value.line = currLine;
				line.add( value );
			}
		}
		else if ( isCopyOperator( currToken.type ) )
		{
			line = template( line );
		}
		else if ( currToken.type != END )
		{
			throw new RuntimeException( here +"shouldn't happen" );
		}
		return line;
	}


	protected Word emptyLines( int preceedingEmptyLines )
	{
		Word combined = new Word();
		combined.type = Syntaxeme.EMPTY;
		combined.modifier = preceedingEmptyLines;
		combined.line = currLine - preceedingEmptyLines;
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





































