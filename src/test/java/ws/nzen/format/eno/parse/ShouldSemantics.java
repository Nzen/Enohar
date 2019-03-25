/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno.parse;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import ws.nzen.format.eno.EnoElement;
import ws.nzen.format.eno.Multiline;
import ws.nzen.format.eno.Section;
import ws.nzen.format.eno.parse.Semantologist;

/**  */
class ShouldSemantics
{

	/**
	 * Test method for {@link ws.nzen.format.eno.parse.Semantologist#analyze(java.util.List)}.
	 */
	@Test
	void testAnalyze()
	{
		List<String> docStr = new ArrayList<>();
		String orphanComment = "orphan", associatedComment = " preceeding",
				multiName = "field", multiText = "\ttext";
		docStr.add( ">     "+ orphanComment );
		docStr.add( "" );
		docStr.add( "> "+ associatedComment );
		docStr.add( "-- "+ multiName );
		docStr.add( multiText );
		docStr.add( "--  "+ multiName +" " );
		Semantologist knowy = new Semantologist();
		Section doc = knowy.analyze( docStr );
		assertTrue( doc.getName().isEmpty() );
		assertTrue( doc.getDepth() == 0 );
		List<String> comments = doc.getComments();
		assertTrue( comments.size() == 1 );
		assertTrue( comments.get( 0 ).equals( orphanComment ) );
		List<EnoElement> fieldsOfDoc = doc.getChildren();
		assertTrue( fieldsOfDoc.size() == 1 );
		Multiline field = (Multiline)fieldsOfDoc.get( 0 );
		assertTrue( field.getNameEscapes() == 0 );
		assertTrue( field.getBoundaryLength() == 2 );
		assertTrue( field.getValue().equals( multiText ) );
		comments = field.getComments();
		assertTrue( field.firstCommentPreceededName() );
		assertTrue( field.getAssociatedComment().equals( associatedComment ) );
		fail( "Not yet implemented" );
	}

}


















