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
import ws.nzen.format.eno.parse.Grammarian;

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
	 * Test method for {@link ws.nzen.format.eno.parse.Grammarian#analyze(java.util.List)}.
	 */
	@Test
	void testAnalyze()
	{
		shouldHandleEmptyDocument();
		shouldCommentOnlyDocument();
		shouldSingleElementBody();
		shouldMultiElementBody();
		// templates
		// missing element api
	}


	private void shouldHandleEmptyDocument()
	{
		docStr.clear();
		Grammarian knowy = new Grammarian();
		Section doc = knowy.analyze( docStr );
		assertTrue( doc.getName().isEmpty() );
		assertTrue( doc.getDepth() == 0 );
	}


	private void shouldCommentOnlyDocument()
	{
		docStr.clear();
		docStr.add( Lexeme.COMMENT_OP.getChar() + dict[ dOrphInd ] );
		Grammarian knowy = new Grammarian();
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
		Grammarian knowy = new Grammarian();
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
		docStr.clear();
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
		assertEquals( "list value 0", dict[ dFormatInd ].trim(), listWord.requiredStringValue() );
		listWord = strPointer.get( 1 );
		assertEquals( "list value 1", dict[ dOrphInd ], listWord.requiredStringValue() );
		listWord = strPointer.get( 2 );
		// 4TESTS np disabled assertTrue( "blank value null 2", listWord.optionalStringValue() == null );
		// empty section
		docStr.clear();
		docStr.add( Lexeme.SECTION_OP.getChar() + dict[ dMultiInd ] );
		doc = knowy.analyze( docStr );
		Section subsection = doc.section( dict[ dMultiInd ] );
		assertTrue( "section type", EnoType.SECTION == subsection.getType() );
		assertEquals( "section depth", 1, subsection.getDepth() );
	}


	private void shouldMultiElementBody()
	{
		char secOp = Lexeme.SECTION_OP.getChar(),
				fieOp = Lexeme.FIELD_START_OP.getChar(),
				lstOp = Lexeme.LIST_OP.getChar(),
				setOp = Lexeme.SET_OP.getChar(),
				empOP = Lexeme.CONTINUE_OP_EMPTY.getChar(),
				spaOp = Lexeme.CONTINUE_OP_SPACE.getChar();
		DocGen synth = new DocGen();
		int line = 1;
		Grammarian knowy = new Grammarian();
		Section document = new Section( "", 0 );
		// section ;; section ;; section
		docStr.clear();
		docStr.add( ""+ secOp + dict[ dMultiInd ] );
		docStr.add( ""+ secOp + dict[ dMultiInd ] );
		docStr.add( ""+ secOp + dict[ dMultiInd ] );
		document.getChildren().clear();
		Section secSibling0 = new Section( dict[ dMultiInd ] );
		secSibling0.setDepth( 1 );
		secSibling0.setLine( line++ );
		Section secSibling1 = new Section( dict[ dMultiInd ] );
		secSibling1.setDepth( 1 );
		secSibling1.setLine( line++ );
		Section secSibling2 = new Section( dict[ dMultiInd ] );
		secSibling2.setDepth( 1 );
		secSibling2.setLine( line++ );
		document.addChild( secSibling0 );
		document.addChild( secSibling1 );
		document.addChild( secSibling2 );
		compareAsSection( document, knowy.analyze( docStr ) );
		// section > section > section > section > section
		docStr.clear();
		docStr.add( ""+ secOp + dict[ dMultiInd ] );
		docStr.add( ""+ secOp + secOp + dict[ dMultiInd ] );
		docStr.add( ""+ secOp + secOp + secOp + dict[ dMultiInd ] );
		docStr.add( ""+ secOp + secOp + secOp + secOp + dict[ dMultiInd ] );
		docStr.add( ""+ secOp + secOp + secOp + secOp + secOp + dict[ dMultiInd ] );
		document.getChildren().clear();
		line = 5;
		Section secChild4 = new Section( dict[ dMultiInd ] );
		secChild4.setDepth( 5 );
		secChild4.setLine( line-- );
		Section secChild3 = new Section( dict[ dMultiInd ] );
		secChild3.setDepth( 4 );
		secChild3.setLine( line-- );
		secChild3.addChild( secChild4 );
		Section secChild2 = new Section( dict[ dMultiInd ] );
		secChild2.setDepth( 3 );
		secChild2.setLine( line-- );
		secChild2.addChild( secChild3 );
		Section secChild1 = new Section( dict[ dMultiInd ] );
		secChild1.setDepth( 2 );
		secChild1.setLine( line-- );
		secChild1.addChild( secChild2 );
		Section secChild0 = new Section( dict[ dMultiInd ] );
		secChild0.setDepth( 1 );
		secChild0.setLine( line-- );
		secChild0.addChild( secChild1 );
		document.addChild( secChild0 );
		compareAsSection( document, knowy.analyze( docStr ) );
		// field ;; value ;; list ;; fset
		docStr.clear();
		docStr.add( dict[ dAssocInd ] + fieOp );
		docStr.add( dict[ dOrphInd ] + fieOp );
		docStr.add( empOP + dict[ dOrphInd ] + fieOp );
		docStr.add( dict[ dFieldInd ] + fieOp );
		docStr.add( lstOp + dict[ dOrphInd ] + fieOp );
		docStr.add( dict[ dAssocInd ].toUpperCase() + fieOp );
		docStr.add( dict[ dEscapeInd ] + dict[ dFieldInd ] + dict[ dEscapeInd ]
				+ setOp + dict[ dFieldInd ] );
		document.getChildren().clear();
		line = 1;
		Field bare = new Field( dict[ dAssocInd ] );
		bare.setLine( line++ );
		Value oneLine = new Value( dict[ dOrphInd ] );
		oneLine.setStringValue( dict[ dOrphInd ] + fieOp );
		oneLine.setLine( line++ );
		line++;
		FieldList list = new FieldList( dict[ dFieldInd ] );
		list.setLine( line++ );
		ListItem subValue = new ListItem( dict[ dOrphInd ] + fieOp );
		subValue.setLine( line++ );
		list.addItem( subValue );
		FieldSet fset = new FieldSet( dict[ dAssocInd ].toUpperCase() );
		fset.setLine( line++ );
		SetEntry pair = new SetEntry( dict[ dFieldInd ],
				dict[ dEscapeInd ].length(), dict[ dFieldInd ] );
		pair.setLine( line++ );
		fset.addEntry( pair );
		document.addChild( bare );
		document.addChild( oneLine );
		document.addChild( list );
		document.addChild( fset );
		compareAsSection( document, knowy.analyze( docStr ) );
		// section { value section { fset } } section { list }
		docStr = synth
			.section( Integer.toString( 1 ), 1 )
				.field( Integer.toString( 2 ) )
				.empty( 2 )
				.moreValue( dict[ dOrphInd ], true )
				.section( Integer.toString( 3 ), 2 )
					.field( Integer.toString( 4 ) )
					.setPair( Integer.toString( 5 ), dict[ dOrphInd ] )
			.empty( 2 )
			.section( Integer.toString( 6 ), 1 )
				.field( Integer.toString( 7 ) )
				.comment( dict[ dEscapeInd ] )
				.listItem( dict[ dFieldInd ] )
			.toStrList();
		document.getChildren().clear();
		line = 1;
		secSibling0.getChildren().clear();
		secSibling0.setName( Integer.toString( 1 ) );
		secSibling0.setLine( line++ );
		secSibling0.setDepth( 1 );
		oneLine.setName( Integer.toString( 2 ) );
		oneLine.setLine( line++ );
		oneLine.setStringValue( dict[ dOrphInd ] );
		secSibling0.addChild( oneLine );
		line++; line++; line++;
		secChild0.getChildren().clear();
		secChild0.setName( Integer.toString( 3 ) );
		secChild0.setLine( line++ );
		secChild0.setDepth( 2 );
		fset.entries().clear();
		fset.setName( Integer.toString( 4 ) );
		fset.setLine( line++ );
		pair.setName( Integer.toString( 5 ) );
		pair.setLine( line++ );
		pair.setNameEscapes( 0 );
		pair.setStringValue( dict[ dOrphInd ] );
		fset.addEntry( pair );
		secChild0.addChild( fset );
		secSibling0.addChild( secChild0 );
		document.addChild( secSibling0 );
		line++; line++;
		secSibling1.getChildren().clear();
		secSibling1.setName( Integer.toString( 6 ) );
		secSibling1.setNameEscapes( 0 );
		secSibling1.setLine( line++ );
		secSibling1.setDepth( 1 );
		secSibling1.setPreceedingEmptyLines( 2 );
		list.setName( Integer.toString( 7 ) );
		list.setLine( line++ );
		list.items().clear();
		line++;
		subValue.setStringValue( dict[ dFieldInd ] );
		subValue.setLine( line++ );
		subValue.addComment( dict[ dEscapeInd ] );
		subValue.setFirstCommentPreceededName( true );
		list.addItem( subValue );
		secSibling1.addChild( list );
		document.addChild( secSibling1 );
		compareAsSection( document, knowy.analyze( docStr ) );
	}


	private void compareAsElement( EnoElement expected, EnoElement result )
	{
		if ( expected.equals( result ) )
			return; // relevant things will match or it will be the literal same
		// if ( expected == null ) assert paranoid
		assertEquals( "ae name", expected.getName(), result.getName() );
		String name = expected.getName();
		assertEquals( "ae line of "+ name, expected.getLine(), result.getLine() );
		assertEquals( "ae name esc of "+ name, expected.getNameEscapes(), result.getNameEscapes() );
		assertEquals( "ae type of "+ name, expected.getType(), result.getType() );
		assertEquals( "ae pel of "+ name, expected.getPreceedingEmptyLines(), result.getPreceedingEmptyLines() );
		List<String> expectedComments = expected.getComments();
		List<String> resultComments = result.getComments();
		assertEquals( "// count of "+ name, expectedComments.size(), resultComments.size() );
		if ( ! expectedComments.isEmpty() )
		{
			assertEquals( "f c is assoc", expected.firstCommentPreceededName(),
					result.firstCommentPreceededName() );
			for ( int ind = 0; ind < expectedComments.size(); ind++ )
			{
				assertEquals( "ae comment "+ ind, expectedComments.get( ind ),
						resultComments.get( ind ) );
			}
		}
		if ( expected.getTemplate() == null )
		{
			assertNull( result.getTemplate(), "ae t nul" );
		}
		else
		{
			assertEquals( "ae copyop", expected.isShallowTemplate(),
					result.isShallowTemplate() );
			compareAsElement( expected.getTemplate(), result.getTemplate() );
		}
	}


	private void compareAsSection( Section expected, Section result )
	{
		compareAsElement( expected, result ); // NOTE not assuming, as these may be the document
		assertEquals( "depth differed", expected.getDepth(), result.getDepth() );
		List<EnoElement> expectedChildren = expected.elements();
		List<EnoElement> actualChildren = result.elements();
		assertEquals( "child count btwx "+ expected.getName() +"-"+ result.getName(),
				expectedChildren.size(), actualChildren.size() );
		for ( int ind = 0; ind < expectedChildren.size(); ind++ )
		{
			EnoElement expectedChild = expectedChildren.get( ind );
			EnoElement actualChild = actualChildren.get( ind );
			compareAsElement( expectedChild, actualChild );
			if ( expectedChild.getType() == EnoType.SECTION )
			{
				compareAsSection( (Section)expectedChild, (Section)actualChild );
			}
			else
			{
				compareAsField( (Field)expectedChild, (Field)actualChild );
			}
		}
		// improve compare templates, if they exist
	}


	private void compareAsField( Field expected, Field result )
	{
		// assert we already compared as elements
		if ( expected.getType() == EnoType.FIELD_LIST )
		{
			compareAsList( (FieldList)expected, (FieldList)result );
		}
		else if ( expected.getType() == EnoType.FIELD_SET )
		{
			compareAsFset( (FieldSet)expected, (FieldSet)result );
		}
	}


	private void compareAsValue( Value expected, Value result )
	{
		if ( result.optionalStringValue() == null )
		{
			assertNull( expected.optionalStringValue() );
		}
		else
		{
			assertTrue( result.optionalStringValue()
					.equals( expected.optionalStringValue() ) );
		}
	}


	private void compareAsList( FieldList expected, FieldList result )
	{
		List<ListItem> expectedItems = expected.items();
		List<ListItem> actualItems = result.items();
		assertEquals( "item count differed",
				expectedItems.size(), actualItems.size() );
		for ( int ind = 0; ind < expectedItems.size(); ind++ )
		{
			ListItem expectedChild = expectedItems.get( ind );
			ListItem actualChild = actualItems.get( ind );
			compareAsElement( expectedChild, actualChild );
			compareAsValue( expectedChild, actualChild );
		}
		// improve compare templates, if they exist
	}


	private void compareAsFset( FieldSet expected, FieldSet result )
	{
		List<SetEntry> expectedEntries = expected.entries();
		List<SetEntry> actualEntries = result.entries();
		assertEquals( "entry count differed",
				expectedEntries.size(), actualEntries.size() );
		for ( int ind = 0; ind < expectedEntries.size(); ind++ )
		{
			SetEntry expectedChild = expectedEntries.get( ind );
			SetEntry actualChild = actualEntries.get( ind );
			compareAsElement( expectedChild, actualChild );
			compareAsValue( expectedChild, actualChild );
		}
		// improve compare templates, if they exist
	}

	

}





































