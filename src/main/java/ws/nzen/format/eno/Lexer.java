/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import java.util.Set;
import java.util.TreeSet;

/** Produces Tokens from the current line */
public class Lexer
{
	private String line;
	private int cursorInd = 0;
	private static final char ALPHA_SECTION = '#',
			ALPHA_LIST = '-', ALPHA_SET = '=',
			ALPHA_FIELD = ':', ALPHA_REM = '>',
			ALPHA_COPY = '<', ALPHA_ESCAPE = '`',
			ALPHA_LC_SAME = '\\', ALPHA_LC_BREAK = '|',
			ALPHA_SPACE = ' ', ALPHA_TAB = '\t';
	private Set<Character> alphabet;
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

	public Lexer()
	{
		alphabet = new TreeSet<>();
		alphabet.add( ALPHA_COPY );
		alphabet.add( ALPHA_ESCAPE );
		alphabet.add( ALPHA_FIELD );
		alphabet.add( ALPHA_LC_BREAK );
		alphabet.add( ALPHA_LC_SAME );
		alphabet.add( ALPHA_LIST );
		alphabet.add( ALPHA_REM );
		alphabet.add( ALPHA_SECTION );
		alphabet.add( ALPHA_SET );
		alphabet.add( ALPHA_SPACE );
		alphabet.add( ALPHA_TAB );
	}


	public Token nextToken()
	{
		Token result = new Token();
		if ( line.isEmpty() || cursorInd >= line.length() )
		{
			result.type = Lexeme.END;
			result.word = "";
			return result;
		}
		char nibble = line.charAt( cursorInd );
		cursorInd++;
		boolean hasNext = cursorInd < line.length(); // ASK am I going to ignore last ?
		int bookmark;
		switch ( nibble )
		{
			case ALPHA_COPY :
			{
				if ( hasNext && line.charAt( cursorInd ) == ALPHA_COPY )
				{
					cursorInd++;
					result.type = Lexeme.COPY_OP_DEEP;
					result.word = "<<";
				}
				else
				{
					result.type = Lexeme.COPY_OP_THIN;
					result.word = "<";
				}
				break;
			}
			case ALPHA_ESCAPE :
			{
				result.type = Lexeme.ESCAPE_OP;
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
				result.type = Lexeme.FIELD_START_OP;
				result.word = ":";
				break;
			}
			case ALPHA_LC_BREAK :
			{
				result.type = Lexeme.CONTINUE_OP_BREAK;
				result.word = "|";
				break;
			}
			case ALPHA_LC_SAME :
			{
				result.type = Lexeme.CONTINUE_OP_SAME;
				result.word = "\\";
				break;
			}
			case ALPHA_LIST :
			{
				// is it list or block or generic ?
				if ( hasNext && line.charAt( cursorInd ) == ALPHA_LIST )
				{
					result.type = Lexeme.BLOCK_OP;
					bookmark = cursorInd -1;
					cursorInd = indexOfDivergenceFrom( ALPHA_ESCAPE );
					result.word = line.substring( bookmark, cursorInd );
				}
				else
				{
					result.type = Lexeme.LIST_OP;
					result.word = "-";
				}
				break;
			}
			case ALPHA_REM :
			{
				result.type = Lexeme.COMMENT_OP;
				result.word = ">";
				break;
			}
			case ALPHA_SECTION :
			{
				result.type = Lexeme.SECTION_OP;
				bookmark = cursorInd -1;
				cursorInd = indexOfDivergenceFrom( ALPHA_ESCAPE );
				result.word = line.substring( bookmark, cursorInd );
				break;
			}
			case ALPHA_SET :
			{
				result.type = Lexeme.SET_OP;
				result.word = "=";
				break;
			}
			case ALPHA_SPACE :
			case ALPHA_TAB :
			{
				result.type = Lexeme.WHITESPACE;
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
				result.type = Lexeme.TEXT;
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


	// ASK vet these against end of string case
	// |
	/** assumes cursorInd is on an untested value */
	private int indexOfDivergenceFrom( char match )
	{
		char nibble;
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
	/** resets Lexer, throws NullPointerException */
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


















