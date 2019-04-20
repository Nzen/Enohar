/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import static ws.nzen.format.eno.EnoType.*;

/**  */
public class Field extends EnoElement
{

	public Field()
	{
		super( EnoType.FIELD_EMPTY );
	}


	protected Field( EnoType which )
	{
		super( which );
		if ( which == UNKNOWN
				|| which == SECTION )
		{
			throw new RuntimeException( "expected a field" );
		}
	}


	public Field( String nameToHave, int escapes )
	{
		super( EnoType.FIELD_EMPTY, nameToHave, escapes );
	}


	protected Field( EnoType which,
			String nameToHave, int escapes )
	{
		super( which, nameToHave, escapes );
	}


	public Field( Field likelyEmpty )
	{
		this( new String( likelyEmpty.getName() ), likelyEmpty.getNameEscapes() );
		cloneFrom( likelyEmpty );
	}


	protected void cloneFrom( Field likelyEmpty )
	{
		setPreceedingEmptyLines( likelyEmpty.getPreceedingEmptyLines() );
		cloneComments( likelyEmpty.getComments() );
		setFirstCommentPreceededName( likelyEmpty.firstCommentPreceededName() );
		setTemplateName( new String( likelyEmpty.getTemplateName()) );
		setTemplateEscapes( likelyEmpty.getTemplateEscapes() );
		setShallowTemplate( likelyEmpty.isShallowTemplate() );
		setLine( likelyEmpty.getLine() );
	}


	@Deprecated
	// not sure if I'll use this or not
	public Field adopt( EnoType which )
	{
		if ( which == type )
		{
			return this;
		}
		else if ( which == UNKNOWN
				|| which == SECTION )
		{
			return null;
		}
		else
			throw new RuntimeException( "unimplemented" );
	}


	public boolean isEmpty()
	{
		return type == FIELD_EMPTY;
	}

}

















