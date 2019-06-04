/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno.missing;

import ws.nzen.format.eno.EnoType;
import ws.nzen.format.eno.Field;

/**  */
public class FakeSet extends Field implements Bomb
{
	private String trigger = "";


	public FakeSet()
	{
		super( EnoType.MISSING );
	}

	public FakeSet( String problem )
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

}


















