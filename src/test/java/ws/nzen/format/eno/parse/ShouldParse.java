/* see ../../../../../LICENSE for release details */
package ws.nzen.format.eno.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ws.nzen.format.eno.parse.Lexeme.*;
import static ws.nzen.format.eno.parse.Syntaxeme.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

import ws.nzen.format.eno.parse.Parser;
import ws.nzen.format.eno.parse.Syntaxeme;

/**  */
class ShouldParse
{

	/**
	 * Test method for {@link ws.nzen.format.eno.parse.Parser#parse(java.util.List)}.
	 */
	@Test
	void testParse()
	{
		shouldTrim();
		shouldIgnoreEmptyBody();
		shouldParseSingleLine();
		shouldParseSingleElementMultiLine();
		shouldParseHeterogeneousLines();
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
		singleLineElementWithValue( fileContent, ""+ COMMENT_OP.getChar() +" "+ value,
				mvp, COMMENT, " "+ value );
		// continuation empty
		singleLineElementWithValue( fileContent, ""+ CONTINUE_OP_EMPTY.getChar() +"   "+ value,
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
		copyOp.type = COPY; copyOp.modifier = 1;
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
		copyOp.type = COPY; copyOp.modifier = 1;
		copyName.type = FIELD; copyName.value = name;
		expectedWords.clear();
		expectedWords.add( pwName );
		expectedWords.add( copyOp );
		expectedWords.add( copyName );
		singleLineViaWords( mvp, fileContent, expectedWords,
				"     \t  "+ escaped +" "+ FIELD_START_OP.getChar()
				+"\t"+ COPY_OP_THIN.getChar() + name );
		// set bare
		singleLineElementWithValue( fileContent, name  +"  "+ SET_OP.getChar() +"  ",
				mvp, SET_ELEMENT, name );
		// set with value
		pwName.type = SET_ELEMENT; pwName.modifier = 0; pwName.value = name;
		pwValue.type = VALUE; pwValue.value = value;
		expectedWords.clear();
		expectedWords.add( pwName );
		expectedWords.add( pwValue );
		singleLineViaWords( mvp, fileContent, expectedWords,
				name + SET_OP.getChar() +"  "+ value );
		// reject field op at start
		rejectInvalidLine( mvp, ""+ FIELD_START_OP.getChar() + escaped,
				"field op at beginning" );
		// reject set op at start
		rejectInvalidLine( mvp, ""+ SET_OP.getChar() +"\t"+ COMMENT_OP.getChar(),
				"set op at beginning" );
		// reject copy op at start
		rejectInvalidLine( mvp, "    "+ COPY_OP_DEEP.getChar() +"  "+ ESCAPE_OP.getChar() +" ",
				"copy op at beginning" );
		// reject unclosed escaped name
		rejectInvalidLine( mvp, esc + name, "must match escape" );
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


	void shouldParseSingleElementMultiLine()
	{
		List<String> fileContent = new ArrayList<>();
		String name = "banana", esc = ""+ ESCAPE_OP.getChar() + ESCAPE_OP.getChar(),
				escaped = esc +" "+ name + esc, value = "harbor";
		Parser mvp = new Parser();
		List<List<Parser.Word>> expectedResult = new ArrayList<>();
		List<Parser.Word> expectedLine = new ArrayList<>();
		Parser.Word currWord;
		// empty lines before unescaped field and value
		fileContent.clear();
		expectedLine.clear();
		fileContent.add( "" );
		fileContent.add( "" );
		currWord = mvp.new Word();
		currWord.type = EMPTY;
		currWord.modifier = 2;
		expectedLine.add( currWord );
		fileContent.add( " "+ name +"\t"+ FIELD_START_OP.getChar() + value );
		currWord = mvp.new Word();
		currWord.type = FIELD;
		currWord.value = name;
		expectedLine.add( currWord );
		currWord = mvp.new Word();
		currWord.type = VALUE;
		currWord.value = value;
		expectedLine.add( currWord );
		expectedResult.clear();
		expectedResult.add( expectedLine );
		elementsMatch( mvp, fileContent, expectedResult );
		// multiline block, unescaped
		fileContent.clear();
		expectedLine.clear();
		fileContent.add( ""+ MULTILINE_OP.getChar() + MULTILINE_OP.getChar()
				+ MULTILINE_OP.getChar() +"     "+ name );
		currWord = mvp.new Word();
		currWord.type = MULTILINE_BOUNDARY;
		currWord.modifier = 3;
		expectedLine.add( currWord );
		currWord = mvp.new Word();
		currWord.type = FIELD;
		currWord.value = name;
		expectedLine.add( currWord );
		fileContent.add( " "+ value );
		currWord = mvp.new Word();
		currWord.type = MULTILINE_TEXT;
		currWord.value = " "+ value;
		expectedLine.add( currWord );
		fileContent.add( ""+ MULTILINE_OP.getChar() + MULTILINE_OP.getChar()
		+ MULTILINE_OP.getChar() +"     "+ name );
		// expectedResult.add( expectedLine );
		elementsMatch( mvp, fileContent, expectedResult );
		// reject unclosed multiline
		fileContent.clear();
		fileContent.add( "  "+ MULTILINE_OP.getChar() + MULTILINE_OP.getChar()
		+ MULTILINE_OP.getChar() +"     "+ name );
		fileContent.add( " "+ value );
		rejectInvalidDocument( mvp, fileContent, "must match multiline boundary" );
		// reject multiline with unescaped + escaped
		fileContent.clear();
		fileContent.add( ""+ MULTILINE_OP.getChar() + MULTILINE_OP.getChar()
		+ MULTILINE_OP.getChar() +"     "+ name );
		fileContent.add( " "+ value );
		fileContent.add( ""+ MULTILINE_OP.getChar() + MULTILINE_OP.getChar()
		+ MULTILINE_OP.getChar() +"     "+ escaped );
		rejectInvalidDocument( mvp, fileContent, "multiline boundary name escapes must match" );
	}


	void shouldParseHeterogeneousLines()
	{
		List<String> fileContent = new ArrayList<>();
		String name = "banana", esc = ""+ ESCAPE_OP.getChar() + ESCAPE_OP.getChar(),
				escaped = esc +" "+ name + esc, value = "harbor";
		Parser mvp = new Parser();
		List<List<Parser.Word>> expectedResult = new ArrayList<>();
		List<Parser.Word> expectedLine = new ArrayList<>();
		Parser.Word currWord;
		fileContent.clear();
		expectedResult.clear();
		expectedLine.clear();
		// section
		fileContent.add( SECTION_OP.getChar() +"     "+ name
				+ FIELD_START_OP.getChar() +" "+ value +"\t" );
		currWord = mvp.new Word();
		currWord.type = SECTION;
		currWord.modifier = 1;
		expectedLine.add( currWord );
		currWord = mvp.new Word();
		currWord.type = FIELD;
		currWord.value = name + FIELD_START_OP.getChar() +" "+ value;
		expectedLine.add( currWord );
		expectedResult.add( expectedLine );
		// bare escaped field
		expectedLine = new ArrayList<>();
		fileContent.add( "" );
		fileContent.add( "" );
		fileContent.add( escaped + FIELD_START_OP.getChar() +"   " );
		currWord = mvp.new Word();
		currWord.type = EMPTY;
		currWord.modifier = 2;
		expectedLine.add( currWord );
		currWord = mvp.new Word();
		currWord.type = FIELD;
		currWord.value = name;
		currWord.modifier = esc.length();
		expectedLine.add( currWord );
		expectedResult.add( expectedLine );
		expectedLine = new ArrayList<>();
		// list element
		fileContent.add( ""+ LIST_OP.getChar() +"\t"+ value );
		expectedLine = new ArrayList<>();
		currWord = mvp.new Word();
		currWord.type = LIST_ELEMENT;
		currWord.value = value;
		expectedLine.add( currWord );
		expectedResult.add( expectedLine );
		// bare name
		fileContent.add( "  "+ value );
		expectedLine = new ArrayList<>();
		currWord = mvp.new Word();
		currWord.type = BARE;
		currWord.value = value;
		expectedLine.add( currWord );
		expectedResult.add( expectedLine );
		// comment
		fileContent.add( ""+ COMMENT_OP.getChar() +" "+ value );
		expectedLine = new ArrayList<>();
		currWord = mvp.new Word();
		currWord.type = COMMENT;
		currWord.value = " "+ value;
		expectedLine.add( currWord );
		expectedResult.add( expectedLine );
		// list element
		fileContent.add( ""+ LIST_OP.getChar() +"\t"+ name );
		expectedLine = new ArrayList<>();
		currWord = mvp.new Word();
		currWord.type = LIST_ELEMENT;
		currWord.value = name;
		expectedLine.add( currWord );
		expectedResult.add( expectedLine );
		// continuation
		fileContent.add( ""+ CONTINUE_OP_EMPTY.getChar() +" "+ value );
		expectedLine = new ArrayList<>();
		currWord = mvp.new Word();
		currWord.type = Syntaxeme.VALUE;
		currWord.modifier = Parser.WORD_MOD_CONT_EMPTY;
		currWord.value = value;
		expectedLine.add( currWord );
		expectedResult.add( expectedLine );

		elementsMatch( mvp, fileContent, expectedResult );
		// org.junit.jupiter.api.Assertions.fail(" not over yet");
	}


	private void elementsMatch(  Parser mvp, List<String> fileContent,
			List<List<Parser.Word>> expectedWords )
	{
		List<List<Parser.Word>> entireResult = mvp.parse( fileContent );
		elementsMatch( expectedWords, entireResult );
	}


	private void elementsMatch(  List<List<Parser.Word>> expectedWords,
			List<List<Parser.Word>> actualWords )
	{
		assertTrue( expectedWords.size() == expectedWords.size(), "num lines: ew"
				+ expectedWords.size() +" er"+ expectedWords.size() );
		for ( int lineInd = 0; lineInd < expectedWords.size(); lineInd++ )
		{
			List<Parser.Word> resultLine = expectedWords.get( lineInd );
			List<Parser.Word> expectedLine = expectedWords.get( lineInd );
			assertTrue( expectedLine.size() == resultLine.size(), "line words should match" );
			for ( int wordInd = 0; wordInd < resultLine.size(); wordInd++ )
			{
				Parser.Word tester = expectedLine.get( wordInd );
				Parser.Word parsed = resultLine.get( wordInd );
				assertEquals( tester.type.ordinal(), parsed.type.ordinal(), "types should match" );
				assertTrue( tester.value.equals( parsed.value ), "value doesn't match" );
				assertEquals( tester.modifier, parsed.modifier, "modifiers differ" );
			}
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


	private void rejectInvalidDocument( Parser mvp, List<String> file, String why )
	{
		assertThrows(
				RuntimeException.class,
				() -> {
					mvp.parse( file );
				},
				why );
	}


	private void shouldParseMultiline()
	{
		// multi empty
		// multi with body
		// ensure multi body preserves formatting
		// reject unterminated via bare
		// reject unterminated via escaped start, unescaped end
	}


	private void shouldTrim()
	{
		Parser lang = new Parser();
		String allText = "vv ll",
				lpad = "  "+ allText,
				rpad = allText +"\t ";
		assertEquals( "", lang.ltrim( "" ) );
		assertEquals( "", lang.rtrim( "" ) );
		//
		assertEquals( "", lang.ltrim( "  " ) );
		assertEquals( "", lang.rtrim( "\t" ) );
		//
		assertEquals( allText, lang.ltrim( allText ) );
		assertEquals( allText, lang.rtrim( allText ) );
		//
		assertEquals( allText, lang.ltrim( lpad ) );
		assertEquals( lpad, lang.rtrim( lpad ) );
		//
		assertEquals( rpad, lang.ltrim( rpad ) );
		assertEquals( allText, lang.rtrim( rpad ) );
	}
	

}
























































