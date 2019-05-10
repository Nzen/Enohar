/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno.parse;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import ws.nzen.format.eno.*;
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


	// no template checks, that's later
	private void shouldSingleElementBody()
	{
		docStr.clear();
		// empty field
		docStr.add( dict[ dFieldInd ] + Lexeme.FIELD_START_OP.getChar() );
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
				+ dict[ dEscapeInd ] + Lexeme.FIELD_START_OP.getChar() );
		docStr.add( Lexeme.CONTINUE_OP_EMPTY.getChar() + dict[ dOrphInd ] );
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
		docStr.add( dict[ dFieldInd ] + Lexeme.FIELD_START_OP.getChar() );
		docStr.add( dict[ dOrphInd ] + Lexeme.SET_OP.getChar() + dict[ dFieldInd ] );
		doc = knowy.analyze( docStr );
		FieldSet nonMap = doc.fieldset( dict[ dFieldInd ] );
		assertTrue( "actually a fieldset", EnoType.FIELD_SET == nonMap.getType() );
		assertEquals( "right name", dict[ dFieldInd ], nonMap.getName() );
		SetEntry pairing = nonMap.entry( dict[ dOrphInd ] );
		assertTrue( "right type", EnoType.SET_ELEMENT == pairing.getType() );
		assertEquals( "one entry", nonMap.entries().size(), 1 );
		assertEquals( "entry value", dict[ dFieldInd ], pairing.requiredStringValue() );
		// list
		docStr.clear();
		docStr.add( dict[ dFieldInd ] + Lexeme.FIELD_START_OP.getChar() );
		docStr.add( Lexeme.LIST_OP.getChar() +" "+ dict[ dFormatInd ] );
		docStr.add( Lexeme.LIST_OP.getChar() +" "+ dict[ dOrphInd ] );
		docStr.add( Lexeme.LIST_OP.getChar() +" " );
		doc = knowy.analyze( docStr );
		FieldList anArray = doc.list( dict[ dFieldInd ] );
		assertTrue( "list type", EnoType.FIELD_LIST == anArray.getType() );
		List<ListItem> strPointer = anArray.items();
		assertEquals( "list size", docStr.size() -1, strPointer.size() );
		ListItem listWord = strPointer.get( 0 );
		assertTrue( "l item type", EnoType.LIST_ITEM == listWord.getType() );
		assertEquals( "items have the parent name", anArray.getName(), listWord.getName() );
		assertEquals( "list value 0", dict[ dFormatInd ], listWord.requiredStringValue() );
		listWord = strPointer.get( 1 );
		assertEquals( "list value 1", dict[ dOrphInd ], listWord.requiredStringValue() );
		listWord = strPointer.get( 2 );
		assertTrue( "blank value null 2", listWord.optionalStringValue() == null );
		// empty section
		docStr.clear();
		docStr.add( Lexeme.SECTION_OP.getChar(), dict[ dMultiInd ] );
		doc = knowy.analyze( docStr );
		Section subsection = doc.section( dict[ dMultiInd ] );
		assertTrue( "section type", EnoType.SECTION == subsection.getType() );
		 assertEquals( "section depth", 1, subsection.getDepth() );
	}


	private void compareAsElement( EnoElement expected, EnoElement result )
	{
		if ( expected.equals( result ) )
			return; // relevant things will match or it will be the literal same
		// if ( expected == null ) assert paranoid
		assertEquals( "ae line", expected.getLine(), result.getLine() );
		assertEquals( "ae name", expected.getName(), result.getName() );
		assertEquals( "ae name esc", expected.getNameEscapes(), result.getNameEscapes() );
		assertEquals( "ae type", expected.getType(), result.getType() );
		assertEquals( "ae pel", expected.getPreceedingEmptyLines(), result.getPreceedingEmptyLines() );
		List<String> expectedComments = expected.getComments();
		List<String> resultComments = result.getComments();
		if ( ! expectedComments.isEmpty() )
		{
			for ( int ind = 0; ind < expectedComments.size(); ind++ )
			{
				assertEquals( "ae comment "+ ind, expectedComments.get( ind ),
						resultComments.get( ind ));
			}
		}
		if ( expected.getTemplate() == null )
		{
			assertNull( result.getTemplate(), "ae t nul" );
		}
		else
		{
			compareAsElement( expected.getTemplate(), result.getTemplate() );
		}
	}

	

}





































