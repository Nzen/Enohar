/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import static ws.nzen.format.eno.Lexeme.*;

import java.util.Set;
import java.util.TreeSet;

/** Produces Tokens from the current line */
public class Lexer
{
	/** current input to lex */
	private String line;
	/** position within current line */
	private int cursorInd = 0;
	/** canon chars */
	private static final char ALPHA_SECTION = '#',
			ALPHA_LIST = '-', ALPHA_SET = '=',
			ALPHA_FIELD = ':', ALPHA_REM = '>',
			ALPHA_COPY = '<', ALPHA_ESCAPE = '`',
			ALPHA_LC_SAME = '\\', ALPHA_LC_BREAK = '|',
			ALPHA_SPACE = ' ', ALPHA_TAB = '\t';
	/** valid characters */
	private Set<Character> alphabet;
	/** a type and text */
	public class Token
	{
		public Lexeme type;
		public String word;
		@Override
		public String toString()
		{
			return "LT: t-"+ type.name() +" w-"+ word;
		}
	};

	/** an Eno Lexer waiting for input */
	public Lexer()
	{
		alphabet = new TreeSet<>();
		alphabet.add( COPY_OP_THIN.getChar() );
		alphabet.add( ESCAPE_OP.getChar() );
		alphabet.add( FIELD_START_OP.getChar() );
		alphabet.add( CONTINUE_OP_BREAK.getChar() );
		alphabet.add( CONTINUE_OP_SAME.getChar() );
		alphabet.add( LIST_OP.getChar() );
		alphabet.add( COMMENT_OP.getChar() );
		alphabet.add( SECTION_OP.getChar() );
		alphabet.add( SET_OP.getChar() );
		alphabet.add( WHITESPACE.getChar() );
		alphabet.add( ALPHA_TAB );
	}


	/** the type and text of the next token,
	 * or End until given a different line */
	public Token nextToken()
	{
		Token result = new Token();
		if ( line.isEmpty() || cursorInd >= line.length() )
		{
			result.type = END;
			result.word = "";
			return result;
		}
		char nibble = line.charAt( cursorInd );
		cursorInd++;
		boolean hasNext = cursorInd < line.length();
		int bookmark;
		switch ( nibble )
		{
			case ALPHA_COPY :
			{
				if ( hasNext && COPY_OP_DEEP.match( line.charAt( cursorInd ) ) )
				{
					cursorInd++;
					result.type = COPY_OP_DEEP;
					result.word = "<<";
				}
				else
				{
					result.type = COPY_OP_THIN;
					result.word = "<";
				}
				break;
			}
			case ALPHA_ESCAPE :
			{
				result.type = ESCAPE_OP;
				if ( ! hasNext )
				{
					result.word = "`";
				}
				else
				{
					bookmark = cursorInd -1;
					cursorInd = indexOfDivergenceFrom( ALPHA_ESCAPE );
					result.word = line.substring( bookmark, cursorInd );
				}
				break;
			}
			case ALPHA_FIELD :
			{
				result.type = FIELD_START_OP;
				result.word = ":";
				break;
			}
			case ALPHA_LC_BREAK :
			{
				result.type = CONTINUE_OP_BREAK;
				result.word = "|";
				break;
			}
			case ALPHA_LC_SAME :
			{
				result.type = CONTINUE_OP_SAME;
				result.word = "\\";
				break;
			}
			case ALPHA_LIST :
			{
				// is it list or block or generic ?
				if ( hasNext && line.charAt( cursorInd ) == ALPHA_LIST )
				{
					result.type = MULTILINE_OP;
					bookmark = cursorInd -1;
					cursorInd = indexOfDivergenceFrom( ALPHA_LIST );
					result.word = line.substring( bookmark, cursorInd );
				}
				else
				{
					result.type = LIST_OP;
					result.word = "-";
				}
				break;
			}
			case ALPHA_REM :
			{
				result.type = COMMENT_OP;
				result.word = ">";
				break;
			}
			case ALPHA_SECTION :
			{
				result.type = SECTION_OP;
				bookmark = cursorInd -1;
				cursorInd = indexOfDivergenceFrom( ALPHA_ESCAPE );
				result.word = line.substring( bookmark, cursorInd );
				break;
			}
			case ALPHA_SET :
			{
				result.type = SET_OP;
				result.word = "=";
				break;
			}
			case ALPHA_SPACE :
			case ALPHA_TAB :
			{
				result.type = WHITESPACE;
				if ( hasNext )
				{
					bookmark = cursorInd -1;
					cursorInd = indexOfNonWhitespace();
					result.word = line.substring( bookmark, cursorInd );
				}
				else
				{
					result.word = Character.toString( nibble );
				}
				break;
			}
			default :
			{
				result.type = TEXT;
				if ( hasNext )
				{
					bookmark = cursorInd -1;
					cursorInd = indexOfNextAlphabetChar();
					result.word = line.substring( bookmark, cursorInd );
				}
				else
				{
					result.word = Character.toString( nibble );
				}
			}
		}
		return result;
	}


	/** assumes cursorInd is on an untested value */
	private int indexOfDivergenceFrom( char match )
	{
		char nibble;
		if ( cursorInd == line.length() )
		{
			return cursorInd;
		}
		int peekInd = cursorInd;
		do
		{
			nibble = line.charAt( peekInd );
			if ( nibble != match )
			{
				return peekInd;
			}
			peekInd++;
		}
		while ( peekInd < line.length() );
		return peekInd;
	}


	/** assumes cursorInd is on an untested value */
	private int indexOfNonWhitespace()
	{
		char nibble;
		int peekInd = cursorInd;
		do
		{
			nibble = line.charAt( peekInd );
			if ( ! ( nibble == ALPHA_SPACE || nibble == ALPHA_TAB ) )
			{
				return peekInd;
			}
			peekInd++;
		}
		while ( peekInd < line.length() );
		return peekInd;
	}


	/** assumes cursorInd is on an untested value */
	private int indexOfNextAlphabetChar()
	{
		char nibble;
		int peekInd = cursorInd;
		do
		{
			nibble = line.charAt( peekInd );
			if ( alphabet.contains( nibble ) )
			{
				return peekInd;
			}
			peekInd++;
		}
		while ( peekInd < line.length() );
		return peekInd;
	}


	public int charsLeft()
	{
		return line.length() - cursorInd;
	}


	public String restOfLine()
	{
		return line.substring( cursorInd );
	}


	public String getLine()
	{
		return line;
	}


	/** resets Lexer
	 * @throws NullPointerException if line is null */
	public void setLine( String line )
	{
		if ( line == null )
		{
			throw new NullPointerException();
		}
		cursorInd = 0;
		this.line = line;
	}

}


















