/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**  */
public class EnoParser
{
	// eventually handle exception store localization, output formatter


	public Eno parse( String wholeFile )
	{
		return parse( Arrays.asList( wholeFile.split( System.lineSeparator() ) ) );
	}


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
		for ( int flInd = 0; flInd < fileLines.size(); flInd++ )
		{
			String currLine = fileLines.get( flInd );
			if ( currLine == null )
			{
				currLine = "";
			}
			naiveAlphabet.setLine( currLine );
			wordsOfLine.add( naiveAlphabet.nextToken() );
			word = wordsOfLine.poll();
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


















