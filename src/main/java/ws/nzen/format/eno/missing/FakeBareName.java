/* see ../../../../../LICENSE for release details */
package ws.nzen.format.eno.missing;

import ws.nzen.format.eno.EnoElement;
import ws.nzen.format.eno.EnoType;

/**  */
public class FakeBareName extends EnoElement implements Bomb
{
	private String trigger = "";


	public FakeBareName()
	{
		super( EnoType.MISSING );
	}


	public FakeBareName( String problem )
	{
		this();
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


















