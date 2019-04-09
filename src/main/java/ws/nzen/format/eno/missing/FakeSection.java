package ws.nzen.format.eno.missing;

import ws.nzen.format.eno.EnoType;
import ws.nzen.format.eno.Section;

public class FakeSection extends Section implements Bomb
{
	private String trigger = "";


	public FakeSection()
	{
		super( EnoType.MISSING );
	}

	public FakeSection( String problem )
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
	public int getDepth()
	{
		return -1;
	}

}


















