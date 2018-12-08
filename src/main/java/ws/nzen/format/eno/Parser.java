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
	Stack<Lexer.Token> lexed = new Stack<>(); // ASK or a deque ?
	public class Phrase
	{
		public Syntaxeme type;
		public String words;
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
				break;
			}
			case BLOCK_OP :
			{
				blockBoundary();
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
		if ( currToken.type == Lexeme.WHITESPACE )
		{
			currToken = alphabet.nextToken();
			sectionBeginning();
		}
		if ( currToken.type == Lexeme.ESCAPE_OP )
		{
			escapedName();
		}
		else if ( currToken.type == Lexeme.TEXT )
		{
			unescapedName();
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


	private void escapedName()
	{
		// check for whitespace and match it, gather otherwise
		// TODO
	}


	private void unescapedName()
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
	}


	private void blockBoundary()
	{
		// TODO
		// if haven't finished block, match existing ;; else start one
		// call block text
	}


	private void templateInstruction()
	{
		// TODO
		// get the name then dump into section interior; semantic analysis can handle templates
	}


	

}


















