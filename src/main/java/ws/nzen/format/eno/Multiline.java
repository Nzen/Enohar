/* see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import java.text.MessageFormat;

import ws.nzen.format.eno.parse.Lexeme;

/** A field that preserves the formatting */
public class Multiline extends Value
{
	private int boundaryLength = 2;


	public Multiline( String nameToHave )
	{
		this( nameToHave, 0 );
	}


	public Multiline( String nameToHave, int escapes )
	{
		super( EnoType.MULTILINE, nameToHave, escapes );
	}


	public int getBoundaryLength()
	{
		return boundaryLength;
	}
	public void setBoundaryLength( int boundaryLength )
	{
		if ( boundaryLength >= 2 )
		{
			this.boundaryLength = boundaryLength;
		}
	}


	@Override
	public void setStringValue( String newValue )
	{
		value = newValue;
	}


	@Override
	public void setTemplate( EnoElement baseInstance )
	{
		if ( baseInstance == null )
		{
			template = null;
			return;
		}
		// else
		// FIX use real keys
		if ( baseInstance.type != EnoType.UNKNOWN )
		throw new RuntimeException( "FIX 4test NP change to match context" );
		// disallow template for multiline
	}


	public StringBuilder toString( StringBuilder aggregator )
	{
		StringBuilder declaration = new StringBuilder( value.length() +10 );
		String properName = super.nameWithEscapes(
				new StringBuilder() ).toString();
		for ( int ind = boundaryLength; ind > 0; ind-- )
		{
			declaration.append( Lexeme.MULTILINE_OP.getChar() );
		}
		declaration.append( " " );
		declaration.append( properName );
		declaration.append( System.lineSeparator() );
		declaration.append( value );
		declaration.append( System.lineSeparator() );
		for ( int ind = boundaryLength; ind > 0; ind-- )
		{
			declaration.append( Lexeme.MULTILINE_OP.getChar() );
		}
		declaration.append( " " );
		declaration.append( properName );
		if ( aggregator == null )
			aggregator = new StringBuilder();
		return toString( aggregator, declaration.toString() );
	}

}


















