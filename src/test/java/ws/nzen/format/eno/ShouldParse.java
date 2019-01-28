/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
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
		// FIX https://stackoverflow.com/questions/3727149/cant-load-resourcebundle-during-junit-test
		List<String> fileContent = new ArrayList<>();
		String name = "banana", escaped = "`` banana``", value = "harbor";
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
		assertEquals( Syntaxeme.EMPTY.ordinal(), resultElement.type.ordinal(),
				"type should be empty, not "+ resultElement.type.name() );
		// comment
		fileContent.set( 0, "> "+ value );
		entireResult = mvp.parse( fileContent );
		resultLine = entireResult.get( 0 );
		resultElement = resultLine.get( 0 );
		assertEquals( Syntaxeme.COMMENT.ordinal(), resultElement.type.ordinal(),
				"type should be comment, not "+ resultElement.type.name() );
		assertTrue( resultElement.value.equals( value ),
				"expected "+ value +" but comment is "+ resultElement.value );
		// continuation empty
		// continuation space
		// list bare, value
		// section bare
		// section with template
		// field bare, unescaped
		// field bare, escaped
		// field with value
		// field with copy
		// set bare
		// set with valuek
		// reject field op at start
		// reject set op at start
		// reject copy op at start
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


















