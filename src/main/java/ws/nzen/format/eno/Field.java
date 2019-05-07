/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import static ws.nzen.format.eno.EnoType.*;

import ws.nzen.format.eno.parse.Lexeme;

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


	public StringBuilder toString( StringBuilder aggregator )
	{
		StringBuilder declaration = new StringBuilder();
		if ( nameEscapes > 0 )
		{
			declaration.append( nameEscapes );
			declaration.append( "`" );
		}
		declaration.append( name );
		declaration.append( " " );
		declaration.append( Lexeme.FIELD_START_OP.getChar() );
		// if ( ! templateName )
		// declaration.append(  );
		return toString( aggregator, declaration.toString() );
	}


	@Override
	// for subclasses
	protected StringBuilder toString( StringBuilder aggregator, String declaration )
	{
		return super.toString( aggregator, declaration );
	}

}


















