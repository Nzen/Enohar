/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import static ws.nzen.format.eno.Lexeme.*;
import static ws.nzen.format.eno.Syntaxeme.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

/**  */
public class Parser
{
	private static final String cl = "p.";
	// eventually handle exception store localization, output formatter
	Queue<String> allLines = new LinkedList<>();
	Lexer alphabet = new Lexer();
	Lexer.Token currToken;
	int currLine = 0;
	Stack<Lexer.Token> lexed = new Stack<>(); // ASK or a deque ?
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
		sectionInterior();
	}


	private void sectionInterior()
	{
		String here = cl +"si ";
		String temp;
		switch ( currToken.type )
		{
			case COMMENT_OP :
			{
				comment();
				sectionInterior();
				break;
			}
			case WHITESPACE :
			{
				nextToken();
				sectionInterior();
				break;
			}
			case SECTION_OP :
			{
				// if section depth more than allowed ;; complain
				lexed.push( currToken );
				nextToken();
				sectionBeginning();
				break;
			}
			case TEXT :
			{
				nextToken();
				unescapedName();
				break;
			}
			case ESCAPE_OP :
			{
				escapedName();
				fieldInterior();
				break;
			}
			case BLOCK_OP :
			{
				multilineBoundary();
				break;
			}
			case END :
			{
				if ( advanceLine() )
				{
					sectionInterior(); // 4TESTS
				}
				else
				{
					System.out.println( here +"recognized a valid eno document" );
					return;
				}
				break;
			}
			// ASK presumably I'll use these from within field body or whatever
			case CONTINUE_OP_BREAK :
			case CONTINUE_OP_SAME :
			{
				System.out.println( here +"continuation started without assignment "+ currToken );
				break;
			}
			case COPY_OP_DEEP :
			case COPY_OP_THIN :
			{
				System.out.println( here +"template started without name "+ currToken );
				break;
			}
			case FIELD_START_OP :
			{
				System.out.println( here +"field assignment started without name "+ currToken );
				break;
			}
			case LIST_OP :
			{
				System.out.println( here +"list started without name "+ currToken );
				break;
			}
			case SET_OP :
			{
				System.out.println( here +"set started without name "+ currToken );
				break;
			}
			default :
			{
				System.out.println( here +"unrecognized lexeme "+ currToken );
				break;
			}
		}
	}


	private void sectionBeginning()
	{
		String here = cl +"section name ";
		if ( currToken.type == WHITESPACE )
		{
			nextToken();
		}
		// get the name
		if ( currToken.type == ESCAPE_OP )
		{
			escapedName();
		}
		else if ( currToken.type == TEXT )
		{
			unescapedName();
		}
		else
		{
			// FIX use canon complaint
			throw new RuntimeException( here +"section name should not start with "+ currToken );
		}
		if ( currToken.type == WHITESPACE )
		{
			nextToken();
		}
		if ( currToken.type == COPY_OP_THIN
				|| currToken.type == COPY_OP_DEEP )
		{
			templateInstruction();
		}
		sectionInterior();
	}


	private void comment()
	{
		String here = cl +"comment ";
		nextToken();
		Phrase fullComment = new Phrase();
		if ( currToken.type == WHITESPACE )
		{
			nextToken();
		}
		if ( currToken.type == END )
		{
			fullComment.type = COMMENT;
			fullComment.words = "";
		}
		else
		{
			String vettedComment = "", lastNibble = "";
			Lexeme lastNibbleType;
			do
			{
				vettedComment += lastNibble;
				lastNibble = currToken.word;
				lastNibbleType = currToken.type;
				nextToken();
			}
			while ( currToken.type != END );
			if ( lastNibbleType != WHITESPACE )
			{
				vettedComment += lastNibble;
			}
			fullComment.type = COMMENT;
			fullComment.words = vettedComment;
		}
		System.out.println( here +"recognized "+ fullComment );
	}


	private Phrase escapedName()
	{
		String here = cl +"escaped ";
		Phrase escape = new Phrase();
		escape.type = FIELD_ESCAPE;
		escape.words = currToken.word;
		// get the name
		nextToken();
		if ( currToken.type == WHITESPACE )
		{
			nextToken();
		}
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


	private Phrase unescapedName()
	{
		// gather text until we hit end or assignment operator
		StringBuilder name = new StringBuilder();
		while ( currToken.type == TEXT
				|| currToken.type == WHITESPACE )
		{
			name.append( currToken.word );
			nextToken();
			// TODO
		}
		return null;
	}


	private void multilineBoundary()
	{
		String here = cl +"multiline ";
		Phrase boundary = new Phrase();
		boundary.words = currToken.word;
		boundary.type = BLOCK_BOUNDARY;
		int blockStartedAt = currLine;

		// get the name
		nextToken();
		if ( currToken.type == WHITESPACE )
		{
			nextToken();
		}
		Phrase leadingName;
		if ( currToken.type == ESCAPE_OP )
		{
			leadingName = escapedName();
		}
		else
		{
			leadingName = unescapedName();
		}
		// ASK handle copy here ? hopefully not
		// save the rest as body until we match the block boundary
		advanceLine( true, here +"opened multiline "+ blockStartedAt +" without closing before eof" );
		StringBuilder blockBodyWords = new StringBuilder();
		Phrase secondName = null;
		Phrase secondBoundary = new Phrase();
		secondBoundary.type = BLOCK_BOUNDARY;
		do
		{
			if ( currToken.type == WHITESPACE )
			{
				nextToken();
			}
			if ( currToken.type == BLOCK_OP
					&& boundary.words.length() == currToken.word.length() )
			{
				nextToken();
				secondBoundary.words = currToken.word;
				if ( currToken.type == WHITESPACE )
				{
					nextToken();
				}
				// try to get a name from it, check for match
				if ( currToken.type == END )
				{
					blockBodyWords.append( System.lineSeparator() );
					blockBodyWords.append( alphabet.getLine() );
					// FIX use canon complaint
					advanceLine( true, here +"opened multiline "+ blockStartedAt +" without closing before eof" );
					continue;
				}
				else if ( currToken.type == ESCAPE_OP )
				{
					secondName = escapedName();
				}
				else
				{
					secondName = unescapedName();
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
					advanceLine( true, here +"opened multiline "+ blockStartedAt +" without closing before eof" );
				}
			}
			else
			{
				blockBodyWords.append( System.lineSeparator() );
				blockBodyWords.append( alphabet.getLine() );
				// FIX use canon complaint
				advanceLine( true, here +"opened multiline "+ blockStartedAt +" without closing before eof" );
			}
		}
		while ( true ); // leave by finding second boundary
		Phrase blockBody = new Phrase();
		blockBody.type = BLOCK_TEXT;
		blockBody.words = blockBodyWords.substring(
				System.lineSeparator().length() ); // NOTE remove extraneous prefix
		System.out.println( here +"recognized "+ boundary );
		System.out.println( here +"recognized "+ blockBody );
		nextToken();
		sectionInterior(); // ASK perhaps iterative rather than recursive, ie how deep is the call stack getting ?
	}


	private void templateInstruction()
	{
		// TODO
		// get the name then dump into section interior; semantic analysis can handle templates
	}


	private Phrase fieldInterior()
	{
		String here = cl +"si ";
		String temp;
		// switch ( currToken.type )
		// value, set, list ;; maybe I'm continuing a value or is that a different version ?
		// TODO
		if ( advanceLine() )
		{
			sectionInterior(); // 4TESTS
		}
		return null;
	}


	private void nextToken()
	{
		currToken = alphabet.nextToken();
	}


	private boolean advanceLine()
	{
		return advanceLine( false, "" );
	}


	/** @return whether line has input */
	private boolean advanceLine( boolean complainWhenFileEnds, String endOfFileComplaint )
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





































