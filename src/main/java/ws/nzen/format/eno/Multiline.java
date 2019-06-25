/* see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import java.text.MessageFormat;

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

}


















