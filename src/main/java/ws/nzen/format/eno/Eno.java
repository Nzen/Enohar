package ws.nzen.format.eno;

import java.util.LinkedList;
import java.util.List;

public class Eno
{
	List<EnoElement> elements;
	enum ParsePhase
	{
		NAME, VALUE, BLOCK, IN_BETWEEN;
	};


	public Eno()
	{
		elements = new LinkedList<>();
	}


	public Eno( String wholeFile )
	{
		this();
	}


}


















