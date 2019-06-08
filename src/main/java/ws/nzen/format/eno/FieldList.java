/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import static ws.nzen.format.eno.EnoType.MISSING;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**  */
public class FieldList extends Field
{
	private List<ListItem> values = new ArrayList<>();


	public FieldList()
	{
		super( EnoType.FIELD_LIST );
	}


	public FieldList( String nameToHave )
	{
		this( nameToHave, 0 );
	}


	public FieldList( String nameToHave, int escapes )
	{
		super( EnoType.FIELD_LIST, nameToHave, escapes );
	}


	public FieldList( Field likelyEmpty )
	{
		this( new String( likelyEmpty.getName() ), likelyEmpty.getNameEscapes() );
		cloneFrom( likelyEmpty );
	}


	protected FieldList( EnoType which )
	{
		super( which );
		if ( which != MISSING )
		{
			// warn or die
		}
	}


	public List<ListItem> items()
	{
		return values;
	}


	public void addItem( String justValue )
	{
		if ( justValue == null )
			justValue = "";
		ListItem another = new ListItem( justValue );
		another.setName( getName() );
		values.add( another );
	}


	public void addItem( ListItem fullValue )
	{
		if ( fullValue == null )
			fullValue = new ListItem( "" );
		fullValue.setName( getName() );
		values.add( fullValue );
	}


	/** @throws NoSuchElementException if has no value */
	public List<String> requiredStringValues()
	{
		return getOnlyValues( true );
	}


	public List<String> optionalStringValues()
	{
		return getOnlyValues( false );
	}


	/** @throws NoSuchElementException if has no value and complaing */
	protected List<String> getOnlyValues( final boolean complain )
	{
		List<String> justValues = new ArrayList<>( values.size() );
		for ( ListItem child : values )
		{
			if ( complain )
			{
				justValues.add( child.requiredStringValue() );
			}
			else
			{
				justValues.add( child.optionalStringValue() );
			}
		}
		return justValues;
	}


	public void setTemplate( FieldList baseInstance )
	{
		template = baseInstance;
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
						ExceptionStore.ANALYSIS, localeComplaint ) );
		throw new RuntimeException( problem.format( new Object[]{ baseInstance.getName() } ) );
	}


	public StringBuilder toString( StringBuilder aggregator )
	{
		aggregator = super.toString( aggregator );
		for ( ListItem line : values )
		{
			aggregator = line.toString( aggregator );
		}
		return aggregator;
	}


	@Override
	// for subclasses
	protected StringBuilder toString( StringBuilder aggregator, String declaration )
	{
		return super.toString( aggregator, declaration );
	}


}
























































