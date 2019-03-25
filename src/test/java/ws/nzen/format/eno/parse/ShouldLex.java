/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ws.nzen.format.eno.parse.Lexeme;
import ws.nzen.format.eno.parse.Lexer;

/**  */
class ShouldLex
{

	/**
	 * Test method for {@link ws.nzen.format.eno.parse.Lexer#nextToken()}.
	 */
	@Test
	void testNextToken()
	{
		// ju is assert( expected, actual )
		shouldHandleEmptyLine();
		shouldRecognizeSingleLexeme();
		shouldRecognizeLongLexeme();
		shouldHandlePhrases();
	}


	void shouldHandleEmptyLine()
	{
		Lexer llk = new Lexer();
		final String nullbase = null;
		assertThrows( NullPointerException.class,
				() -> { llk.setLine( nullbase ); },
				"didn't npe on invalid" );

		String base = "";
		Lexer.Token struct;
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( Lexeme.END, struct.type, "empty should become null" );
	}


	void shouldRecognizeSingleLexeme()
	{
		Lexer llk = new Lexer();
		Lexer.Token struct;
		String base = "-";
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( Lexeme.LIST_OP, struct.type, "list" );
		struct = llk.nextToken();
		assertEquals( Lexeme.END, struct.type, "nothing left" );
		struct = llk.nextToken();
		assertEquals( Lexeme.END, struct.type, "still nothing left" );
		base = ":";
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( Lexeme.FIELD_START_OP, struct.type, "field assign" );
		base = "\\";
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( Lexeme.CONTINUE_OP_SPACE, struct.type, "backslash" );
		base = "<";
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( Lexeme.COPY_OP_THIN, struct.type, "shallow copy" );
		base = ">";
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( Lexeme.COMMENT_OP, struct.type, "comment" );
		base = "|";
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( Lexeme.CONTINUE_OP_EMPTY, struct.type, "pipe" );
		base = "=";
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( Lexeme.SET_OP, struct.type, "equal" );
	}


	void shouldRecognizeLongLexeme()
	{
		Lexer llk = new Lexer();
		Lexer.Token struct;
		String base = "`";
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( Lexeme.ESCAPE_OP, struct.type, "single escape" );
		assertEquals( base.length(), struct.word.length() );
		base = "`````";
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( Lexeme.ESCAPE_OP, struct.type, "long escape" );
		assertEquals( base.length(), struct.word.length() );
		base = "--";
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( Lexeme.MULTILINE_OP, struct.type, "min block" );
		assertEquals( base.length(), struct.word.length() );
		base = "------";
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( Lexeme.MULTILINE_OP, struct.type, "long block" );
		assertEquals( base.length(), struct.word.length() );
		base = "#";
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( Lexeme.SECTION_OP, struct.type, "single section" );
		base = "####";
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( Lexeme.SECTION_OP, struct.type, "long section" );
		assertEquals( base.length(), struct.word.length() );
		base = " ";
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( Lexeme.WHITESPACE, struct.type, "single space" );
		base = "    ";
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( Lexeme.WHITESPACE, struct.type, "long space" );
		assertEquals( base.length(), struct.word.length() );
		base = "\t";
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( Lexeme.WHITESPACE, struct.type, "tab" );
		assertEquals( base.length(), struct.word.length() );
		base = "\t\t\t";
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( Lexeme.WHITESPACE, struct.type, "long tab" );
		assertEquals( base.length(), struct.word.length() );
		base = "\t ";
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( Lexeme.WHITESPACE, struct.type, "mixed tab space" );
		assertEquals( base.length(), struct.word.length() );
		base = "<<";
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( Lexeme.COPY_OP_DEEP, struct.type, "" );
		assertEquals( base.length(), struct.word.length() );
		// base = "<<<<";
		base = "a";
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( Lexeme.TEXT, struct.type, "text" );
		base = "\u3413";
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( Lexeme.TEXT, struct.type, "non ascii text" );
		base = "alphabet";
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( Lexeme.TEXT, struct.type, "long text" );
		assertEquals( base.length(), struct.word.length() );
	}


	void shouldHandlePhrases()
	{
		Lexer llk = new Lexer();
		Lexer.Token struct;
		String base = "- ";
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( Lexeme.LIST_OP, struct.type, "list" );
		struct = llk.nextToken();
		assertEquals( Lexeme.WHITESPACE, struct.type, "space" );
		struct = llk.nextToken();
		assertEquals( Lexeme.END, struct.type, "end" );

		base = "a :";
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( Lexeme.TEXT, struct.type, "text" );
		struct = llk.nextToken();
		assertEquals( Lexeme.WHITESPACE, struct.type, "space" );
		struct = llk.nextToken();
		assertEquals( Lexeme.FIELD_START_OP, struct.type, "field assign" );

		base = "\t\t\\ banana";
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( Lexeme.WHITESPACE, struct.type, "space" );
		struct = llk.nextToken();
		assertEquals( Lexeme.CONTINUE_OP_SPACE, struct.type, "backslash" );
		struct = llk.nextToken();
		assertEquals( Lexeme.WHITESPACE, struct.type, "space" );
		struct = llk.nextToken();
		assertEquals( Lexeme.TEXT, struct.type, "long text" );
		assertEquals( "banana", struct.word, "banana" );
	}


	void shouldGetRest()
	{
		Lexer llk = new Lexer();
		Lexer.Token struct;
		String base = "- ";
		llk.setLine( base );
		assertEquals( base.length(), llk.charsLeft(), "entire length" );
		struct = llk.nextToken();
		assertEquals( Lexeme.LIST_OP, struct.type, "list" );
		assertEquals( base.length() -1, llk.charsLeft(), "single char" );
		String rest = " ";
		assertEquals( rest, llk.restOfLine(), "rest is space" );

		rest = ":alba";
		base = "<<--"+ rest;
		llk.setLine( base );
		struct = llk.nextToken();
		assertEquals( Lexeme.COPY_OP_DEEP, struct.type, "<<" );
		assertEquals( base.length() -2, llk.charsLeft(), "block plus alba len_"+ llk.restOfLine() );
		struct = llk.nextToken();
		assertEquals( Lexeme.MULTILINE_OP, struct.type, "block" );
		assertEquals( rest, llk.restOfLine(), "colon alba" );
	}

}





































