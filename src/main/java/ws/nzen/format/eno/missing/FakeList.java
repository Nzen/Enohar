/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno.missing;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import ws.nzen.format.eno.EnoType;
import ws.nzen.format.eno.FieldList;
import ws.nzen.format.eno.ListItem;

/**  */
public class FakeList extends FieldList implements Bomb
{
	private String trigger = "";


	public FakeList()
	{
		super( EnoType.MISSING );
	}

	public FakeList( String problem )
	{
		super( EnoType.MISSING );
		setComplaint( problem );
	}


	@Override
	public String getComplaint()
	{
		return trigger;
	}

	@Override
	public void setComplaint( String why )
	{
		if ( why != null )
			trigger = why;
		else
			trigger = "";
	}


	@Override
	public void addItem( String justValue )
	{
	}


	@Override
	public void addItem( ListItem fullValue )
	{
	}


	public List<ListItem> items()
	{
		return new LinkedList<>();
	}


	@Override
	public List<String> requiredStringValues()
	{
		throw new NoSuchElementException( trigger );
	}


	@Override
	public List<String> optionalStringValues()
	{
		return new LinkedList<>();
	}

}


















