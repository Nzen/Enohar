/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

/**  */
public class FieldSet extends Field
{

	/**  */
	public FieldSet()
	{
		super( EnoType.FIELD_SET );
	}


	public FieldSet( String nameToHave, int escapes )
	{
		super( EnoType.FIELD_SET, nameToHave, escapes );
	}


	public FieldSet( Field likelyEmpty )
	{
		this( new String( likelyEmpty.getName() ), likelyEmpty.getNameEscapes() );
		cloneFrom( likelyEmpty );
	}

}


















