/* see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import static ws.nzen.format.eno.EnoType.BARE;

/** Just a bare name. Typically, this is to represent a
 * boolean by the name's presence or absence. */
public class Empty extends EnoElement
{

	/** @param typeToBe */
	public Empty()
	{
		super( BARE );
	}


	public Empty( String nameToHave )
	{
		super( BARE, nameToHave, 0 );
	}


	/** @param typeToBe
	/** @param nameToHave
	/** @param escapes */
	public Empty( String nameToHave, int escapes )
	{
		super( BARE, nameToHave, escapes );
	}

}


















