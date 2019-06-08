/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import java.text.MessageFormat;
import java.util.NoSuchElementException;

/** A field that preserves the formatting */
public class Multiline extends Field
{
	private int boundaryLength = 2;
	private String formattedValue = "";

	/**  */
	public Multiline()
	{
		super( EnoType.MULTILINE );
	}


	public Multiline( String nameToHave )
	{
		this( nameToHave, 0 );
	}


	public Multiline( String nameToHave, int escapes )
	{
		super( EnoType.MULTILINE, nameToHave, escapes );
	}


	public void setValue( String updated )
	{
		formattedValue = updated;
	}

	/** returns null for uninitialized value */
	public String optionalStringValue()
	{
		return formattedValue;
	}

	/** @throws NoSuchElementException if has no value */
	public String requiredStringValue()
	{
		if ( formattedValue == null || formattedValue.isEmpty() ) // ASK is empty null for canon ?
		{
			MessageFormat problem = new MessageFormat(
					ExceptionStore.getStore().getExceptionMessage(
							ExceptionStore.VALIDATION,
							EnoLocaleKey.MISSING_FIELD_VALUE ) );
			throw new NoSuchElementException( problem.format( new Object[]{ name } ) );
		}
		else
		{
			return formattedValue;
		}
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


















