/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ws.nzen.format.eno.Lexeme.*;
import static ws.nzen.format.eno.Syntaxeme.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**  */
class ShouldParse
{

	/**
	 * Test method for {@link ws.nzen.format.eno.Parser#parse(java.util.List)}.
	 */
	@Test
	void testParse()
	{
		shouldIgnoreEmptyBody();
		shouldParseSingleLine();
	}


	void shouldIgnoreEmptyBody()
	{
		Parser mvp = new Parser();
		List<List<Parser.Word>> result = mvp.parse( null );
		assertTrue( result.isEmpty() );
	}


	void shouldParseSingleLine()
	{
		List<String> fileContent = new ArrayList<>();
		String name = "banana", esc = ""+ ESCAPE_OP.getChar() + ESCAPE_OP.getChar(),
				escaped = esc +" "+ name + esc, value = "harbor";
		// empty line
		fileContent.add( "" );
		Parser mvp = new Parser();
		List<List<Parser.Word>> entireResult = mvp.parse( fileContent );
		List<Parser.Word> resultLine;
		Parser.Word resultElement;
		assertEquals( 1, entireResult.size(), "exactly one 'line'" );
		resultLine = entireResult.get( 0 );
		assertEquals( 1, resultLine.size(),  "exactly one element" );
		resultElement = resultLine.get( 0 );
		assertEquals( EMPTY.ordinal(), resultElement.type.ordinal(),
				"type should be empty, not "+ resultElement.type.name() );
		// comment
		singleLineElementWithValue( fileContent, COMMENT_OP.getChar() +" "+ value,
				mvp, COMMENT, value );
		// continuation empty
		singleLineElementWithValue( fileContent, CONTINUE_OP_EMPTY.getChar() +"   "+ value,
				mvp, VALUE, value, Parser.WORD_MOD_CONT_EMPTY );
		// continuation space
		singleLineElementWithValue( fileContent, "\t"+ CONTINUE_OP_SPACE.getChar() +"   "+ escaped,
				mvp, VALUE, escaped, Parser.WORD_MOD_CONT_SPACE );
		// list bare, value
		singleLineElementWithValue( fileContent, "\t"+ LIST_OP.getChar() + "   ",
				mvp, LIST_ELEMENT, "" );
		singleLineElementWithValue( fileContent, "\t"+ LIST_OP.getChar() + name + name,
				mvp, LIST_ELEMENT, name + name );
		// section bare
		List<Parser.Word> expectedWords = new LinkedList<>();
		Parser.Word operator = mvp.new Word();
		operator.type = SECTION; operator.modifier = 2;
		Parser.Word pwName = mvp.new Word();
		pwName.type = FIELD; pwName.value = name;
		expectedWords.clear();
		expectedWords.add( operator );
		expectedWords.add( pwName );
		singleLineViaWords( mvp, fileContent, expectedWords,
				" "+ SECTION_OP.getChar() + SECTION_OP.getChar() +"  \t"+ name );
		// section with template
		operator.type = SECTION; operator.modifier = 2;
		pwName.type = FIELD; pwName.value = name;
		Parser.Word copyOp = mvp.new Word();
		Parser.Word copyName = mvp.new Word();
		copyOp.type = SHALLOW_COPY; copyOp.modifier = 1;
		copyName.type = FIELD; copyName.value = name;
		expectedWords.clear();
		expectedWords.add( operator );
		expectedWords.add( pwName );
		expectedWords.add( copyOp );
		expectedWords.add( copyName );
		singleLineViaWords( mvp, fileContent, expectedWords,
				" "+ SECTION_OP.getChar() + SECTION_OP.getChar() +"  \t"+ name
				+"\t"+ COPY_OP_THIN.getChar() + name );
		// field bare, unescaped
		singleLineElementWithValue( fileContent, name + FIELD_START_OP.getChar(),
				mvp, FIELD, name );
		// field bare, escaped
		pwName.type = FIELD; pwName.modifier = esc.length(); pwName.value = name;
		expectedWords.clear();
		expectedWords.add( operator );
		expectedWords.add( pwName );
		singleLineViaWords( mvp, fileContent, expectedWords,
				" "+ escaped  +"\t"+ FIELD_START_OP.getChar() +"\t" );
		// field with value
		pwName.type = FIELD; pwName.modifier = 0; pwName.value = name;
		Parser.Word pwValue = mvp.new Word();
		pwValue.type = VALUE; pwValue.value = value;
		expectedWords.clear();
		expectedWords.add( pwName );
		expectedWords.add( pwValue );
		singleLineViaWords( mvp, fileContent, expectedWords,
				name + FIELD_START_OP.getChar() + value );
		// field with copy
		pwName.type = FIELD; pwName.modifier = esc.length(); pwName.value = name;
		copyOp.type = SHALLOW_COPY; copyOp.modifier = 1;
		copyName.type = FIELD; copyName.value = name;
		expectedWords.clear();
		expectedWords.add( operator );
		expectedWords.add( pwName );
		expectedWords.add( copyOp );
		expectedWords.add( copyName );
		singleLineViaWords( mvp, fileContent, expectedWords,
				"     \t  "+ escaped +" "+ FIELD_START_OP.getChar()
				+"\t"+ COPY_OP_THIN.getChar() + name );
		// set bare
		singleLineElementWithValue( fileContent, name  +"  "+ SET_OP.getChar() +"  ",
				mvp, FIELD, name );
		// set with value
		pwName.type = FIELD; pwName.modifier = 0; pwName.value = name;
		pwValue.type = SET_ELEMENT; pwValue.value = value;
		expectedWords.clear();
		expectedWords.add( pwName );
		expectedWords.add( pwValue );
		singleLineViaWords( mvp, fileContent, expectedWords,
				name + SET_OP.getChar() +"  "+ value );
		// reject field op at start
		rejectInvalidLine( mvp, FIELD_START_OP.getChar() + escaped,
				"field op at beginning" );
		// reject set op at start
		rejectInvalidLine( mvp, SET_OP.getChar() +"\t"+ COMMENT_OP.getChar(),
				"set op at beginning" );
		// reject copy op at start
		rejectInvalidLine( mvp, "    "+ COPY_OP_DEEP.getChar() +"  "+ ESCAPE_OP.getChar() +" ",
				"copy op at beginning" );
	}


	private void singleLineViaWords( Parser mvp, List<String> fileContent,
			List<Parser.Word> expectedWords, String line )
	{
		fileContent.clear();
		fileContent.add( line );
		List<List<Parser.Word>> entireResult = mvp.parse( fileContent );
		List<Parser.Word> resultLine = entireResult.get( 0 );
		assertEquals( expectedWords.size(), resultLine.size(), "different number of Pwords for line" );
		for ( int ind = 0; ind < expectedWords.size(); ind++ )
		{
			Parser.Word tester = expectedWords.get( ind );
			Parser.Word parsed = resultLine.get( ind );
			assertEquals( tester.type.ordinal(), parsed.type.ordinal(), "types should match" );
			assertTrue( tester.value.equals( parsed.value ), "value doesn't match" );
			assertEquals( tester.modifier, parsed.modifier, "modifiers differ" );
		}
	}


	private void singleLineElementWithValue(  List<String> fileContent, String line,
			Parser mvp, Syntaxeme expectedType, String expectedValue )
	{
		singleLineElementWithValue( fileContent, line, mvp, expectedType, expectedValue, 0 );
	}


	private void singleLineElementWithValue(  List<String> fileContent, String line,
			Parser mvp, Syntaxeme expectedType, String expectedValue,
			int expectedModifier )
	{
		fileContent.clear();
		fileContent.add( line );
		List<List<Parser.Word>> entireResult = mvp.parse( fileContent );
		List<Parser.Word> resultLine = entireResult.get( 0 );
		Parser.Word resultElement = resultLine.get( 0 );
		assertEquals( expectedType.ordinal(), resultElement.type.ordinal(),
				"type didn't match" );
		if ( expectedValue.isEmpty() )
		{
			assertTrue( resultElement.value.isEmpty(), "value isn't empty" );
		}
		else
		{
			assertTrue( resultElement.value.equals( expectedValue ), "value didn't match" );
		}
		if ( expectedModifier != 0 )
		{
			assertEquals( expectedModifier, resultElement.modifier, "wrong mod val" );
		}
	}


	private void rejectInvalidLine( Parser mvp, String line, String why )
	{
		assertThrows( RuntimeException.class,
				() -> {
					List<String> file = new LinkedList<>();
					file.add( line );
					mvp.parse( file );
				},
			why );
	}


	void shouldParseMultiline()
	{
		// multi empty
		// multi with body
		// ensure multi body preserves formatting
		// reject unterminated via bare
		// reject unterminated via escaped start, unescaped end
	}

}


















