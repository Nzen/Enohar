/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno.missing;

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

}


















