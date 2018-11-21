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
					return; // we've exhausted the input
				}
				else
				{
					alphabet.setLine( temp );
					currToken = alphabet.nextToken();
					sectionInterior();
				}
				break;
			}
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
		// save
		System.out.println( here +"recognized "+ alphabet.getLine() );
		String temp = allLines.poll();
		if ( temp == null )
		{
			alphabet.setLine( "" ); // we've exhausted the input
		}
		else
		{
			alphabet.setLine( temp );
		}
		currToken = alphabet.nextToken();
	}


	private void escapedName()
	{
		
	}


	private void unescapedName()
	{
		
	}


	private void blockBoundary()
	{
		// if haven't finished block, match existing ;; else start one
		// call block text
	}


	private void templateInstruction()
	{
		
	}
	


	public Eno parse( String wholeFile )
	{
		return parse( Arrays.asList( wholeFile.split( System.lineSeparator() ) ) );
	}


	@Deprecated // until recognize() works
	public Eno parse( List<String> fileLines )
	{
		Eno document = new Eno();
		if ( fileLines == null || fileLines.isEmpty() )
		{
			return document;
		}
		// TODO some sort of fsm tracking 
		Lexer naiveAlphabet = new Lexer();
		int sectionDepth = 0;
		ParseContext phase = ParseContext.DOCUMENT;
		Queue<Lexer.Token> wordsOfLine = new LinkedList<>();
		EnoElement currElem = new EnoElement();
		StringBuilder aggreg = new StringBuilder();
		Lexer.Token word;
		Stack<Lexer.Token> lexMemory = new Stack<>();
		Stack<Syntaxeme> parseMemory = new Stack<>();
		int escapeLen = 0;
		String tempWord = "";
		for ( int flInd = 0; flInd < fileLines.size(); flInd++ )
		{
			String currLine = fileLines.get( flInd );
			if ( currLine == null )
			{
				currLine = "";
			}
			naiveAlphabet.setLine( currLine );
			//wordsOfLine.add( naiveAlphabet.nextToken() );
			//word = wordsOfLine.poll();

			word = naiveAlphabet.nextToken();
			if ( parseMemory.isEmpty() )
			{
				// build one up
				switch ( word.type )
				{
					case COMMENT_OP :
					{
						System.out.println( "comment : "+ currLine );
						continue; // the lines loop
					}
					case SECTION_OP :
					{
						wordsOfLine.add( word );
						word = naiveAlphabet.nextToken();
						if ( word.type == Lexeme.WHITESPACE )
						{
							word = naiveAlphabet.nextToken();
						}
						if ( word.type == Lexeme.ESCAPE_OP )
						{
							escapeLen = word.word.length();
							word = naiveAlphabet.nextToken();
							if ( word.type == Lexeme.WHITESPACE )
							{
								word = naiveAlphabet.nextToken();
							}
						}
						if ( word.type == Lexeme.TEXT )
						{
							aggreg.append( word.word );
							if ( escapeLen > 0 )
							{
								word = naiveAlphabet.nextToken();
								if ( word.type == Lexeme.WHITESPACE )
								{
									word = naiveAlphabet.nextToken();
								}
								if ( word.type == Lexeme.ESCAPE_OP )
								{
									escapeLen = word.word.length();
									word = naiveAlphabet.nextToken();
									if ( word.type == Lexeme.WHITESPACE )
									{
										word = naiveAlphabet.nextToken();
									}
								}
							}
							
						}
						break;
					}
					case ESCAPE_OP :
					{
						break;
					}
					case TEXT :
					{
						break;
					}
					case WHITESPACE :
					{
						break;
					}
					default :
						break;
				}
			}
			else
			{
				// continue building
			}

			// ASK or does this duplicate the currElem's type ?
			switch ( phase )
			{
				case DOCUMENT :
				case SECTION :
				{
					/*
					comment -> comment
					text -> name
					escape -> name
					section -> new section
					whitespace -> any
					end -> continue
					 */
					break;
				}
				case BLOCK :
				{
					/*
					whitespace
					end -> continue
					
					 */
					break;
				}
				case SET :
				{
					/*
					whitespace
					end -> continue
					
					 */
					break;
				}
				case LIST :
				{
					/*
					whitespace
					end -> continue
					list -> more list
					 */
					break;
				}
				case FIELD :
				{
					/*
					whitespace
					end -> continue
					
					 */
					break;
				}
				case VALUE :
				{
					/*
					whitespace -> any
					continuation -> more value, but comment
					end -> continue
					comment -> comment
					 */
					break;
				}
				default :
				{
					throw new RuntimeException( ExceptionStore.onlyInstance
							.getExceptionMessage( "unknown ParseContext "+ phase.name() ) );
				}
			}

			// ASK probably ditch this version
			switch ( word.type )
			{
				case WHITESPACE :
				{
					// toss preceeding for now; later need for query stuff
					// or put this in a function add to wordsOfLine and recur
					break;
				}
				case CONTINUE_OP_SAME :
				case CONTINUE_OP_BREAK :
				{
					boolean wasSameContinue = word.type == Lexeme.CONTINUE_OP_SAME;
					if ( phase != ParseContext.VALUE )
					{
						throw new RuntimeException( ExceptionStore.onlyInstance
								.getExceptionMessage( "lexeme "+ word.type
										+" continue outside of value, currently "+ phase.name() ) );
					}
					while ( true )
					{
						word = naiveAlphabet.nextToken();
						if ( word.type == Lexeme.COMMENT_OP || word.type == Lexeme.END )
						{
							break;
						}
						else
						{
							wordsOfLine.add( word );
						}
					}
					if ( word.type == Lexeme.END )
					{
						// we collected a bunch of text
						aggreg.delete( 0, aggreg.length() );
						for ( Lexer.Token currLt : wordsOfLine )
						{
							aggreg.append( currLt.word );
						}
						currElem.setParsedValue( currElem.getParsedValue()
								+((wasSameContinue)? " " : System.lineSeparator())
								+ aggreg.toString() );
						currElem.setOriginalValue( currElem.getOriginalValue()
								+ System.lineSeparator() + currLine );
					}
					else
					{
						// todo the above stuff or a substring of currLine from an earlier bookmark
						currElem.addComment( naiveAlphabet.restOfLine() );
					}
					break;
				}
				case SECTION_OP :
				{
					break;
				}
				case LIST_OP :
				{
					break;
				}
				case BLOCK_OP :
				{
					break;
				}
				case COMMENT_OP :
				{
					break;
				}
				case SET_OP :
				{
					break;
				}
				case ESCAPE_OP :
				{
					break;
				}
				case TEXT :
				{
					break;
				}
				case END :
				{
					break;
				}
				case FIELD_START_OP :
				case COPY_OP_THIN :
				case COPY_OP_DEEP :
				{
					throw new RuntimeException( ExceptionStore.onlyInstance
							.getExceptionMessage( "lexeme "+ word.type
									+" should not start a line" ) );
				}
				default :
				{
					throw new RuntimeException( ExceptionStore.onlyInstance
							.getExceptionMessage( "unrecognized lexeme "+ word.type ) );
				}
			}
			/*
			do
			{
				System.out.print( " "+ word.type.name() +"_("+ word.word +") " );
				word = naiveAlphabet.nextToken();
			}
			while ( word.type != Lexeme.END );
			System.out.println();
			*/
		}

		return null; // todo
	}


	

}


















