/* see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

/**  */
class ValidFieldList
{

	/**
	 * Test method for {@link ws.nzen.format.eno.FieldList#items()}.
	 */
	@Test
	void testItems()
	{
		FieldList base = new FieldList( "whatever" );
		base.addItem( " " );
		assertEquals( 1, base.items().size() );
		assertEquals( " ", base.items().get( 0 ).requiredStringValue() );
		//
		FieldList adopter = new FieldList( "nothing" );
		adopter.setTemplate( base );
		assertEquals( 1, adopter.items().size() );
		assertEquals( " ", adopter.items().get( 0 ).requiredStringValue(),
				"only base's elements" );
		//
		FieldList mix = new FieldList( "several" );
		mix.setTemplate( adopter );
		mix.addItem( "-" );
		List<ListItem> items = mix.items();
		assertEquals( 2, items.size() );
		assertEquals( " ", items.get( 0 ).requiredStringValue() );
		assertEquals( "-", items.get( 1 ).requiredStringValue() );
	}

}


















