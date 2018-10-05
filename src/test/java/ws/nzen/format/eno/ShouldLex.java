/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**  */
class ShouldLex
{

	/**
	 * Test method for {@link ws.nzen.format.eno.Lexer#nextToken()}.
	 */
	@Test
	void testNextToken()
	{
		// ju is assert( expected, actual )
		Lexer llk = new Lexer();
		final String nullbase = null;
		assertThrows( NullPointerException.class,
				() -> { llk.setLine( nullbase ); },
				"didn't npe on invalid" );
		// handle blank
		String base = "";
		Lexer.Token struct;
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( Lexeme.END, struct.type, "empty should become null" );
		// handle single letter line
		base = "```";
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( Lexeme.ESCAPE_OP, struct.type, "just escape" );
		struct = llk.nextToken();
		assertEquals( Lexeme.END, struct.type, "nothing left" );
		struct = llk.nextToken();
		assertEquals( Lexeme.END, struct.type, "still nothing left" );
		// handle typical section with name
		base = "# f";
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( Lexeme.SECTION_OP, struct.type, "section first" );
		struct = llk.nextToken();
		assertEquals( Lexeme.WHITESPACE, struct.type, "whitespace next" );
		struct = llk.nextToken();
		assertEquals( Lexeme.TEXT, struct.type, "text last" );
		// handle every operator
		base = "# \t-:--\\<>|<<=";
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( Lexeme.SECTION_OP, struct.type, "" );
		struct = llk.nextToken();
		assertEquals( Lexeme.WHITESPACE, struct.type, "" );
		struct = llk.nextToken();
		assertEquals( Lexeme.LIST_OP, struct.type, "" );
		struct = llk.nextToken();
		assertEquals( Lexeme.FIELD_START_OP, struct.type, "" );
		struct = llk.nextToken();
		assertEquals( Lexeme.BLOCK_OP, struct.type, "" );
		struct = llk.nextToken();
		assertEquals( Lexeme.CONTINUE_OP_SAME, struct.type, "" );
		struct = llk.nextToken();
		assertEquals( Lexeme.COPY_OP_THIN, struct.type, "" );
		struct = llk.nextToken();
		assertEquals( Lexeme.COMMENT_OP, struct.type, "" );
		struct = llk.nextToken();
		assertEquals( Lexeme.CONTINUE_OP_BREAK, struct.type, "" );
		struct = llk.nextToken();
		assertEquals( Lexeme.COPY_OP_DEEP, struct.type, "" );
		struct = llk.nextToken();
		assertEquals( Lexeme.SET_OP, struct.type, "" );
		/*
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( LexType., struct.type, "" );
		*/
	}

}


















