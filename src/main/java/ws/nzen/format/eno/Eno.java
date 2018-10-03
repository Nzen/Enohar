package ws.nzen.format.eno;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Eno
{
	List<EnoElement> elements;
	enum ParsePhase
	{
		NAME, VALUE, BLOCK, IN_BETWEEN;
	};


	public Eno()
	{
		elements = new LinkedList<>();
	}


	public Eno( String wholeFile )
	{
		this();
		parse( wholeFile );
	}


	public void parse( String wholeFile )
	{
		parse( Arrays.asList( wholeFile.split( System.lineSeparator() ) ) );
	}


	public void parse( List<String> fileLines )
	{
		ParsePhase currently = ParsePhase.IN_BETWEEN;
		for ( int lineInd = 0; lineInd < fileLines.size(); lineInd++ )
		{
			EnoElement currElement = new EnoElement(); // ASK or am I clearing ? 
			int phraseInd = 0;
			String currLine = fileLines.get( lineInd );
			switch ( currently )
			{
				case IN_BETWEEN :
				{
					phraseInd = indOfNonWhitespace( currLine, phraseInd );
					if ( phraseInd >= 0 )
					{
						char nibble = currLine.charAt( phraseInd );
						if ( nibble == '>' )
						{
							// add comment
						}
						else if ( nibble == '#' )
						{
							// section
						}
						else if ( nibble == '-' )
						{
							// peek if this is a text block
						}
					}
					break;
				}
				case NAME :
				{
					break;
				}
				case VALUE :
				{
					break;
				}
				case BLOCK :
				{
					break;
				}
				default :
				{
					throw new RuntimeException( "Unimplemented" );
				}
			}
		}
	}


	/** offset is inclusive */
	private int indOfNonWhitespace( String phrase, int offset )
	{
		int notFound = -1;
		if ( phrase == null || phrase.isEmpty() || offset >= phrase.length() )
		{
			return notFound;
		}
		for ( ; offset < phrase.length(); offset++ )
		{
			if ( phrase.charAt( offset ) != ' ' && phrase.charAt( offset ) != '\t' )
			{
				return offset;
			}
		}
		return notFound;
	}


}


















