package ws.nzen.format.eno.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ShouldParseViaCucumber
{
	private String entireInput = "";
	private List<List<Parser.Word>> result;

	@Given( "one line input is {string}" )
	public void oneLineInputIs( String string )
	{
		entireInput = string;
	}


	@When( "I parse input" )
	public void iParseInput()
	{
		List<String> splitInput = Arrays.asList(
				entireInput.split( System.lineSeparator() ) );
		Parser mvp = new Parser();
		result = mvp.parse( splitInput );
	}


	@Then( "one line output is _bare name_" )
	public void oneLineOutputIsBareName()
	{
		List<List<Parser.Word>> expected = new ArrayList<>( 1 );
		List<Parser.Word> resultLine = new ArrayList<>( 1 );
		Parser mvp = new Parser();
		Parser.Word bare = mvp.new Word();
		bare.line = 0;
		bare.modifier = 0;
		bare.type = Syntaxeme.BARE;
		bare.value = entireInput.trim();
		resultLine.add( bare );
		expected.add( resultLine );
		elementsMatch( expected, result );
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
}


















