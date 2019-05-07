/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import ws.nzen.format.eno.parse.Lexeme;

/**  */
public class ListItem extends Value
{

	public ListItem( String value )
	{
		super( EnoType.LIST_ITEM, "", 0 );
		setStringValue( value );
	}


	public StringBuilder toString( StringBuilder aggregator )
	{
		aggregator = super.toString( aggregator, "" );
		aggregator.append( Lexeme.LIST_OP.getChar() );
		aggregator.append( " " );
		aggregator.append( value );
		return aggregator;
	}

}


















