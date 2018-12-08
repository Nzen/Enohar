/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import java.util.Arrays;
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
		currToken = alphabet.nextToken();
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
				currToken = alphabet.nextToken();
				sectionInterior();
				break;
			}
			case SECTION_OP :
			{
				// if section depth more than allowed ;; complain
				lexed.push( currToken );
				currToken = alphabet.nextToken();
				sectionBeginning();
				break;
			}
			case TEXT :
			{
				currToken = alphabet.nextToken();
				unescapedName();
				break;
			}
			case ESCAPE_OP :
			{
				lexed.push( currToken );
				currToken = alphabet.nextToken();
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
				temp = allLines.poll();
				if ( temp == null )
				{
					alphabet.setLine( "" );
					currToken = alphabet.nextToken();
					return; // we've exhausted the input, it's a valid document
				}
				else
				{
					alphabet.setLine( temp );
					currToken = alphabet.nextToken();
					currLine++;
					sectionInterior();
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
		if ( currToken.type == Lexeme.WHITESPACE )
		{
			currToken = alphabet.nextToken();
		}
		// get the name
		if ( currToken.type == Lexeme.ESCAPE_OP )
		{
			escapedName();
		}
		else if ( currToken.type == Lexeme.TEXT )
		{
			unescapedName();
		}
		else
		{
			// FIX use canon complaint
			throw new RuntimeException( here +"section name should not start with "+ currToken );
		}
		if ( currToken.type == Lexeme.WHITESPACE )
		{
			currToken = alphabet.nextToken();
		}
		if ( currToken.type == Lexeme.COPY_OP_THIN
				|| currToken.type == Lexeme.COPY_OP_DEEP )
		{
			templateInstruction();
		}
		sectionInterior();
	}


	private void comment()
	{
		String here = cl +"comment ";
		currToken = alphabet.nextToken();
		Phrase fullComment = new Phrase();
		if ( currToken.type == Lexeme.WHITESPACE )
		{
			currToken = alphabet.nextToken();
		}
		if ( currToken.type == Lexeme.END )
		{
			fullComment.type = Syntaxeme.COMMENT;
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
				currToken = alphabet.nextToken();
			}
			while ( currToken.type != Lexeme.END );
			if ( lastNibbleType != Lexeme.WHITESPACE )
			{
				vettedComment += lastNibble;
			}
			fullComment.type = Syntaxeme.COMMENT;
			fullComment.words = vettedComment;
		}
		System.out.println( here +"recognized "+ fullComment );
	}


	private Phrase escapedName()
	{
		String here = cl +"multiline ";
		Phrase escape = new Phrase();
		escape.type = Syntaxeme.FIELD_ESCAPE;
		escape.words = currToken.word;
		// get the name
		currToken = alphabet.nextToken();
		if ( currToken.type == Lexeme.WHITESPACE )
		{
			currToken = alphabet.nextToken();
		}
		StringBuilder namePieces = new StringBuilder();
		String lastNibble = "";
		Lexeme lastLex = null;
		Phrase name = new Phrase();
		name.type = Syntaxeme.FIELD;
		do
		{
			if ( currToken.type == Lexeme.END )
			{
				// FIX use canon complaint
				throw new RuntimeException( here +"opened escape name without closing before eol" );
			}
			if ( currToken.type == Lexeme.ESCAPE_OP
					&& currToken.word.length() == escape.words.length()
					&& ! name.words.isEmpty() )
			{
				// this is it
				if ( lastLex != Lexeme.WHITESPACE )
				{
					namePieces.append( lastNibble );
					name.words = namePieces.toString();
					break;
				}
			}
		}
		while ( true );
		
		// check for whitespace and match it, gather otherwise
		// TODO
		return null;
	}


	private Phrase unescapedName()
	{
		// gather text until we hit end or assignment operator
		StringBuilder name = new StringBuilder();
		while ( currToken.type == Lexeme.TEXT
				|| currToken.type == Lexeme.WHITESPACE )
		{
			name.append( currToken.word );
			currToken = alphabet.nextToken();
			// TODO
		}
		return null;
	}


	private void multilineBoundary()
	{
		String here = cl +"multiline ";
		Phrase boundary = new Phrase();
		boundary.words = currToken.word;
		boundary.type = Syntaxeme.BLOCK_BOUNDARY;
		int blockStartedAt = currLine;

		// get the name
		currToken = alphabet.nextToken();
		if ( currToken.type == Lexeme.WHITESPACE )
		{
			currToken = alphabet.nextToken();
		}
		Phrase leadingName;
		if ( currToken.type == Lexeme.ESCAPE_OP )
		{
			leadingName = escapedName();
		}
		else if ( currToken.type == Lexeme.TEXT )
		{
			leadingName = unescapedName();
		}
		else
		{
			// FIX use canon complaint ASK can names be -:\ etc ?
			throw new RuntimeException( here +"weird name" );
		}

		// save the rest as body until we match the block boundary
		if ( currToken.type == Lexeme.WHITESPACE )
		{
			currToken = alphabet.nextToken();
		}
		if ( currToken.type == Lexeme.END )
		{
			String temp = allLines.poll();
			if ( temp == null )
			{
				// FIX use canon complaint
				throw new RuntimeException( here +"opened multiline without closing before eof" );
			}
			else
			{
				alphabet.setLine( temp );
				currToken = alphabet.nextToken();
				currLine++;
			}
		}
		else
		{
			// ASK is copy op okay here ?
			// assert unreachable
		}
		StringBuilder blockBodyWords = new StringBuilder();
		Phrase secondName;
		Phrase secondBoundary = new Phrase();
		secondBoundary.type = Syntaxeme.BLOCK_BOUNDARY;
		do
		{
			if ( currToken.type == Lexeme.WHITESPACE )
			{
				currToken = alphabet.nextToken();
			}
			if ( currToken.type == Lexeme.BLOCK_OP
					&& boundary.words.length() == currToken.word.length() )
			{
				secondBoundary.words = currToken.word;
				if ( currToken.type == Lexeme.WHITESPACE )
				{
					currToken = alphabet.nextToken();
				}
				// try to get a name from it, check for match
				currToken = alphabet.nextToken();
				if ( currToken.type == Lexeme.ESCAPE_OP )
				{
					secondName = escapedName();
				}
				else if ( currToken.type == Lexeme.TEXT )
				{
					secondName = unescapedName();
				}
				else
				{
					blockBodyWords.append( System.lineSeparator() );
					blockBodyWords.append( alphabet.getLine() );
					String temp = allLines.poll();
					if ( temp == null )
					{
						// FIX use canon complaint
						throw new RuntimeException( here +"opened multiline without closing before eof" );
					}
					else
					{
						alphabet.setLine( temp );
						currToken = alphabet.nextToken();
					}
					continue;
				}
				if ( secondName.equals( leadingName ) )
				{
					break;
				}
				else
				{
					blockBodyWords.append( System.lineSeparator() );
					blockBodyWords.append( alphabet.getLine() );
					String temp = allLines.poll();
					if ( temp == null )
					{
						// FIX use canon complaint
						throw new RuntimeException( here +"opened multiline without closing before eof" );
					}
					else
					{
						alphabet.setLine( temp );
						currToken = alphabet.nextToken();
					}
				}
			}
			else
			{
				blockBodyWords.append( System.lineSeparator() );
				blockBodyWords.append( alphabet.getLine() );
				String temp = allLines.poll();
				if ( temp == null )
				{
					// FIX use canon complaint
					throw new RuntimeException( here +"opened multiline without closing before eof" );
				}
				else
				{
					alphabet.setLine( temp );
					currToken = alphabet.nextToken();
				}
			}
		}
		while ( true ); // leave by finding second boundary
		Phrase blockBody = new Phrase();
		blockBody.type = Syntaxeme.BLOCK_TEXT;
		blockBody.words = blockBodyWords.substring(
				System.lineSeparator().length() ); // NOTE remove extraneous prefix
		// TODO
		// if haven't finished block, match existing ;; else start one
		// call block text
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
		return null;
	}


	

}





































