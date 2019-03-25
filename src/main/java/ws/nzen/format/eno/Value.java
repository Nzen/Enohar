/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import java.text.MessageFormat;
import java.util.NoSuchElementException;

/**  */
public class Value extends Field
{
	private String value = null; // per spec, I'd prefer empty string

	/** @param nameToHave
	/** @param escapes */
	public Value( String nameToHave, int escapes )
	{
		super( EnoType.FIELD_VALUE, nameToHave, escapes );
	}


	public Value( Field likelyEmpty )
	{
		this( new String( likelyEmpty.getName() ), likelyEmpty.getNameEscapes() );
		cloneFrom( likelyEmpty );
		if ( likelyEmpty.getType() == EnoType.FIELD_VALUE )
		{
			Value notEmpty = (Value)likelyEmpty;
			setStringValue( new String( notEmpty.optionalStringValue() ) );
		}
	}


	public void append( String more )
	{
		if ( value == null )
		{
			value = more;
		}
		else
		{
			value += more;
		}
	}


	/** returns null for uninitialized value */
	public String optionalStringValue()
	{
		return value;
	}


	/** @throws NoSuchElementException if has no value */
	public String requiredStringValue()
	{
		if ( value == null )
		{
			MessageFormat problem = new MessageFormat(
					ExceptionStore.getStore().getExceptionMessage(
							ExceptionStore.VALIDATION,
							EnoLocaleKey.MISSING_FIELD_VALUE ) );
			throw new NoSuchElementException( problem.format( new Object[]{ name } ) );
		}
		else
		{
			return value;
		}
	}

	public void setStringValue( String newValue )
	{
		value = newValue;
	}


}


















