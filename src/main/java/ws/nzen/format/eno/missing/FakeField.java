/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno.missing;

import java.util.NoSuchElementException;

import ws.nzen.format.eno.EnoType;
import ws.nzen.format.eno.Field;

/**  */
public class FakeField extends Field implements Bomb
{
	private String trigger = "";


	public FakeField()
	{
		super( EnoType.MISSING );
	}

	public FakeField( String problem )
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


	/** no op */
	public void append( String more )
	{
	}


	/** returns null */
	public String optionalStringValue()
	{
		return null;
	}


	/** Congrats, you just cut the wrong wire.
	 *  @throws NoSuchElementException inflexibly */
	public String requiredStringValue()
	{
			throw new NoSuchElementException( trigger );
	}


	/** no op */
	public void setStringValue( String newValue )
	{
	}


	/** nope; null */
	public boolean isEmpty()
	{
		return false;
	}

}


















