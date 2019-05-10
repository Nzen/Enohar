/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import java.text.MessageFormat;
import java.util.NoSuchElementException;

import ws.nzen.format.eno.parse.Lexeme;

/**  */
public class Value extends Field
{
	protected String value = null; // per spec
	// IMPROVE Map<int, boolean> continuations :: index, empty/space style continuation
	// and then append() would add new records each time; setSV() would strip line separators 

	public Value( String nameToHave, int escapes )
	{
		super( EnoType.FIELD_VALUE, nameToHave, escapes );
	}


	protected Value( EnoType childType,
			String nameToHave, int escapes )
	{
		super( childType, nameToHave, escapes );
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


	public void setTemplate( Value baseInstance )
	{
		if ( baseInstance.getType() == EnoType.FIELD_VALUE )
			template = baseInstance;
		else
			setTemplate( (EnoElement)baseInstance );
			// NOTE this is a set entry or list item
	}

	@Override
	public void setTemplate( EnoElement baseInstance )
	{
		// FIX use real keys
		String localeComplaint = "";
		switch ( baseInstance.getType() )
		{
			case SECTION :
			{
				setTemplate( (Section)baseInstance );
				return;
			}
			case FIELD_EMPTY :
			{
				localeComplaint = EnoLocaleKey.EXPECTED_SECTION_GOT_EMPTY;
				break;
			}
			case FIELD_VALUE :
			case MULTILINE :
			{
				localeComplaint = EnoLocaleKey.EXPECTED_SECTION_GOT_FIELD;
				break;
			}
			case FIELD_LIST :
			case LIST_ITEM :
			{
				localeComplaint = EnoLocaleKey.EXPECTED_SECTION_GOT_LIST;
				break;
			}
			case FIELD_SET :
			case SET_ELEMENT :
			{
				localeComplaint = EnoLocaleKey.EXPECTED_SECTION_GOT_FIELDSET;
				break;
			}
			default :
			{
				localeComplaint = EnoLocaleKey.MISSING_ELEMENT;
				break;
			}
		}
		MessageFormat problem = new MessageFormat(
				ExceptionStore.getStore().getExceptionMessage(
						ExceptionStore.VALIDATION, localeComplaint ) );
		throw new RuntimeException( problem.format( new Object[]{ baseInstance.getName() } ) );
	}


	public StringBuilder toString( StringBuilder aggregator )
	{
		aggregator = super.toString( aggregator );
		aggregator.append( System.lineSeparator() );
		aggregator.append( Lexeme.CONTINUE_OP_EMPTY );
		aggregator.append( " " );
		aggregator.append( value );
		return aggregator;
	}


	@Override
	// for subclasses
	protected StringBuilder toString( StringBuilder aggregator, String declaration )
	{
		return super.toString( aggregator, declaration );
	}


}
























































