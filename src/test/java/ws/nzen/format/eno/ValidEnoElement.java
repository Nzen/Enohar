/* see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import static org.junit.jupiter.api.Assertions.*;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

/**  */
class ValidEnoElement
{

	/**
	 * Test method for {@link ws.nzen.format.eno.EnoElement#yieldsField()}.
	 */
	@Test
	void testYieldsField()
	{
		EnoElement thing;
		thing = new EnoElement( EnoType.SECTION );
		assertFalse( thing.yieldsField() );
		thing = new EnoElement( EnoType.FIELD_EMPTY );
		assertTrue( thing.yieldsField() );
		thing = new EnoElement( EnoType.MULTILINE );
		assertTrue( thing.yieldsField() );
		thing = new EnoElement( EnoType.FIELD_LIST );
		assertTrue( thing.yieldsField() );
		thing = new EnoElement( EnoType.FIELD_SET );
		assertTrue( thing.yieldsField() );
		thing = new EnoElement( EnoType.FIELD_VALUE );
		assertTrue( thing.yieldsField() );
	}


	/**
	 * Test method for {@link ws.nzen.format.eno.EnoElement#stringKey()}.
	 */
	@Test
	void testStringKey()
	{
		EnoElement thing = new EnoElement(
				EnoType.SECTION, Integer.toString( 0 ), 0 );
		assertEquals( Integer.toString( 0 ), thing.stringKey(),
				"name does not include escapes" );
		assertEquals( 0, thing.getNameEscapes() );
	}


	/**
	 * Test method for {@link ws.nzen.format.eno.EnoElement#requiredStringComment()}.
	 */
	@Test
	void testRequiredStringComment()
	{
		EnoElement thing = new EnoElement(
				EnoType.FIELD_LIST, Integer.toString( 0 ), 0 );
		//
		assertThrows( NoSuchElementException.class, 
				() -> {
					thing.getComments().clear();
					thing.setFirstCommentPreceededName( false );
					assertNull( thing.requiredStringComment(), "should throw" );
				}
		);
		//
		thing.addComment( " " );
		thing.setFirstCommentPreceededName( true );
		assertEquals( " ", thing.requiredStringComment() );
	}


	/**
	 * Test method for {@link ws.nzen.format.eno.EnoElement#optionalStringComment()}.
	 */
	@Test
	void testOptionalStringComment()
	{
		EnoElement thing = new EnoElement(
				EnoType.FIELD_SET, Integer.toString( 0 ), 0 );
		//
		assertNull( thing.optionalStringComment() );
		//
		thing.addComment( " " );
		thing.setFirstCommentPreceededName( true );
		assertEquals( " ", thing.requiredStringComment() );
	}


	/**
	 * Test method for {@link ws.nzen.format.eno.EnoElement#getFirstComment(boolean)}.
	 */
	@Test
	void testGetFirstComment()
	{
		EnoElement thing;
		thing = new EnoElement(
				EnoType.FIELD_VALUE, Integer.toString( 0 ), 0 );
		thing.addComment( Integer.toString( 0 ) );
		thing.addComment( Integer.toString( 1 ) );
		assertThrows( NoSuchElementException.class, 
				() -> {
					assertNull( thing.getFirstComment( true ), "should throw" );
				}
		);
		assertNull( thing.getFirstComment( false ), "should throw" );
		thing.setFirstCommentPreceededName( true );
		assertEquals( Integer.toString( 0 ), thing.getFirstComment( true ) );
	}

}


















