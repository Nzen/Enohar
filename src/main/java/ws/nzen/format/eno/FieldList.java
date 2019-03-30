/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**  */
public class FieldList extends Field
{
	List<ListItem> values = new ArrayList<>();


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


}
























































