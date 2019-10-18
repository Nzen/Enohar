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
			// warn or die. probably die, as there's no subclass
		}
	}


	/** Provides a defensive copy if this list has a template */
	public List<ListItem> items()
	{
		if ( template == null )
		{
			return values;
		}
		else
		{
			List<ListItem> items = new ArrayList<>(
					((FieldList)template).items() );
			for ( ListItem own : values )
			{
				items.add( own );
			}
			return items;
		}
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
		List<ListItem> allItems = items();
		List<String> justValues = new ArrayList<>( allItems.size() );
		for ( ListItem child : allItems )
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
		if ( baseInstance.equals( this ) )
		{
			MessageFormat problem = new MessageFormat(
					ExceptionStore.getStore().getExceptionMessage(
							ExceptionStore.VALIDATION,
							EnoLocaleKey.CYCLIC_DEPENDENCY ) );
			throw new NoSuchElementException( problem.format(
					new Object[]{ getName() } ) );
		}
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
		if ( baseInstance.type != EnoType.FIELD_LIST )
		throw new RuntimeException( "FIX 4test NP change to match context" );
		String localeComplaint = "";
		switch ( baseInstance.getType() )
		{
			case FIELD_LIST :
			{
				setTemplate( (FieldList)baseInstance );
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
		if ( aggregator == null )
			aggregator = new StringBuilder();
		aggregator = super.toString( aggregator );
		for ( ListItem line : values )
		{
			aggregator = line.toString( aggregator );
		}
		return aggregator;
	}


}
























































