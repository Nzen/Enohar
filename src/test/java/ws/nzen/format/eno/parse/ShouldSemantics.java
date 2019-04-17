/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno.parse;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import ws.nzen.format.eno.EnoElement;
import ws.nzen.format.eno.EnoType;
import ws.nzen.format.eno.Field;
import ws.nzen.format.eno.Multiline;
import ws.nzen.format.eno.Section;
import ws.nzen.format.eno.Value;
import ws.nzen.format.eno.parse.Semantologist;

/**  */
class ShouldSemantics
{
	private List<String> docStr = new ArrayList<>();
	private String[] dict = { "orphan", "assoc", "field", "\ttext",
			"``", "--" };
	private static final int dOrphInd = 0, dAssocInd = dOrphInd +1,
			dFieldInd = dAssocInd +1, dFormatInd = dFieldInd +1,
			dEscapeInd = dFormatInd +1, dMultiInd = dEscapeInd +1;

	/**
	 * Test method for {@link ws.nzen.format.eno.parse.Semantologist#analyze(java.util.List)}.
	 */
	@Test
	void testAnalyze()
	{
		/*
		docStr.clear();
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
		assertTrue( field.optionalStringValue().equals( multiText ) );
		comments = field.getComments();
		assertTrue( field.firstCommentPreceededName() );
		assertTrue( field.optionalStringComment().equals( associatedComment ) );
		fail( "Not yet implemented" );
		*/
		shouldHandleEmptyDocument();
		shouldCommentOnlyDocument();
		shouldSingleElementBody();
	}


	private void shouldHandleEmptyDocument()
	{
		docStr.clear();
		Semantologist knowy = new Semantologist();
		Section doc = knowy.analyze( docStr );
		assertTrue( doc.getName().isEmpty() );
		assertTrue( doc.getDepth() == 0 );
	}


	private void shouldCommentOnlyDocument()
	{
		docStr.clear();
		docStr.add( Lexeme.COMMENT_OP.getChar() + dict[ dOrphInd ] );
		Semantologist knowy = new Semantologist();
		Section doc = knowy.analyze( docStr );
		assertTrue( "not associated", ! doc.firstCommentPreceededName() );
		List<String> comments = doc.getComments();
		assertTrue( "one comment", comments.size() == 1 );
		assertTrue( comments.get( 0 ).equals( dict[ dOrphInd ] ) );
	}


	// no template checks
	private void shouldSingleElementBody()
	{
		docStr.clear();
		// empty field
		docStr.add( dict[ dFieldInd ] + Lexeme.FIELD_START_OP );
		Semantologist knowy = new Semantologist();
		Section doc = knowy.analyze( docStr );
		assertTrue( doc.getComments().isEmpty() );
		Field baseField = doc.field( dict[ dFieldInd ] );
		assertTrue( "type is bare", baseField.getType() == EnoType.FIELD_EMPTY );
		assertEquals( "same name",dict[ dFieldInd ], baseField.getName() );
		List<Field> onlyChild = doc.fields( dict[ dFieldInd ] );
		assertEquals( "only one child", 1, onlyChild.size() );
		assertTrue( "exact object", baseField.equals( onlyChild.get( 0 ) ) );
		// value field
		docStr.clear();
		docStr.add( dict[ dEscapeInd ] + dict[ dFieldInd ]
				+ dict[ dEscapeInd ] + Lexeme.FIELD_START_OP );
		docStr.add( Lexeme.CONTINUE_OP_EMPTY + dict[ dOrphInd ] );
		doc = knowy.analyze( docStr );
		baseField = doc.field( dict[ dFieldInd ] );
		assertEquals( "same name",dict[ dFieldInd ], baseField.getName() );
		assertEquals( "correct escapes", dict[ dEscapeInd ].length(), baseField.getNameEscapes() );
		assertTrue( "type is value", baseField.getType() == EnoType.FIELD_VALUE );
		Value wordField = (Value)baseField;
		assertEquals( "value didn't match", dict[ dOrphInd ], wordField.optionalStringValue() );
		assertEquals( "value didn't match", dict[ dOrphInd ], wordField.requiredStringValue() );
		Field trash = doc.field( dict[ dEscapeInd ] );
		assertTrue( "trash is a missing", trash.getType() == EnoType.MISSING );
		trash = doc.optionalField( dict[ dEscapeInd ] );
		assertTrue( "optional missing null", trash == null );
		// empty multiline
		docStr.clear();
		docStr.add( dict[ dMultiInd ] + dict[ dFieldInd ] );
		docStr.add( dict[ dMultiInd ] + dict[ dFieldInd ] );
		doc = knowy.analyze( docStr );
		baseField = doc.field( dict[ dFieldInd ] );
		assertTrue( "type is multiline", baseField.getType() == EnoType.MULTILINE );
		Multiline emptyMultiline = (Multiline)baseField;
		assertThrows( NoSuchElementException.class,
				() -> { assertTrue( "dead code", emptyMultiline.requiredStringValue() == null ); } );
		// multiline
		docStr.add( dict[ dMultiInd ] + dict[ dFieldInd ] );
		docStr.add( dict[ dFormatInd ] );
		docStr.add( dict[ dMultiInd ] + dict[ dFieldInd ] );
		doc = knowy.analyze( docStr );
		Multiline formattedField = (Multiline)doc.field( dict[ dFieldInd ] );
		assertEquals( "not trimmed value", dict[ dFormatInd ], formattedField.optionalStringValue() );
		assertEquals( "boundary len", dict[ dMultiInd ].length(), formattedField.getBoundaryLength() );
		// fieldset
		docStr.clear();
		// list
		docStr.clear();
		// empty section
		docStr.clear();
	}


	

}





































