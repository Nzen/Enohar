/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import java.util.Arrays;
import java.util.List;

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
		Lexer.Token word;
		for ( int flInd = 0; flInd < fileLines.size(); flInd++ )
		{
			String currLine = fileLines.get( flInd );
			if ( currLine == null )
			{
				currLine = "";
			}
			naiveAlphabet.setLine( currLine );
			word = naiveAlphabet.nextToken();
			do
			{
				System.out.print( " "+ word.type.name() );
				word = naiveAlphabet.nextToken();
			}
			while ( word.type != Lexeme.END );
			System.out.println();
		}

		return null; // todo
	}


	

}


















