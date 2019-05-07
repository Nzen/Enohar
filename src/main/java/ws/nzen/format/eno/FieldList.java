/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import static ws.nzen.format.eno.EnoType.MISSING;

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
		values.add( new ListItem( justValue ) );
	}


	public void addItem( ListItem fullValue )
	{
		if ( fullValue == null )
			fullValue = new ListItem( "" );
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
























































