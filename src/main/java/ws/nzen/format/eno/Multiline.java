/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

/**  */
public class Multiline extends Field
{
	private int boundaryLength = 2;
	private String formattedValue = "";

	/**  */
	public Multiline()
	{
		super( EnoType.MULTILINE );
	}


	public Multiline( String nameToHave, int escapes )
	{
		super( nameToHave, escapes );
	}


	public void setValue( String updated )
	{
		formattedValue = updated;
	}
	@Override
	public String getValue()
	{
		return formattedValue;
	}


	public int getBoundaryLength()
	{
		return boundaryLength;
	}
	public void setBoundaryLength( int boundaryLength )
	{
		if ( boundaryLength >= 2 )
		{
			this.boundaryLength = boundaryLength;
		}
	}


}


















