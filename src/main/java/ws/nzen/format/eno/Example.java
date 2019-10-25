/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import ws.nzen.format.eno.parse.Lexeme;
import ws.nzen.format.eno.parse.Parser;
import ws.nzen.format.eno.parse.Syntaxeme;

/** A class for testing outside of junit */
public class Example
{
	private static final String cl = "t.";


	public static void main( String[] args )
	{
		// checkHardcodedDocument();
		// checkCanonFiles();
		checkEmitting();
	}


	static void checkHardcodedDocument()
	{
		List<String> file = new ArrayList<>();
		file.add( "# banana" );
		file.add( " faf:  anna " );
		file.add( "`bro ken` < comment" );
		file.add( "> comment" );
		// file.add( "-222" );
		file.add( "-- `bello`" );
		file.add( " #=-- \\//" );
		file.add( "" );
		file.add( "-- bello" );
		file.add( " fef: " );
		file.add( "-eve" );
		file.add( " - adam" );
		file.add( " fif:" );
		file.add( " gib= ana " );
		file.add( " gyb= nna " );
		file.add( " fof:  anna " );
		file.add( "> comment" );
		file.add( " | large " );
		file.add( "> comment" );
		file.add( " \\ large " );
		Parser epp = new Parser();
		epp.parse( file );
	}


	static void checkCanonFiles()
	{
		String here = cl +"fi ";
		Parser epp = new Parser();
		try
		{
			DirectoryStream<Path> filePipe;
			Path draftRoot = Paths.get( "usr" );
			Queue<Path> waitingRoom = new ArrayDeque<>();
			waitingRoom.add( draftRoot );
			while ( ! waitingRoom.isEmpty() )
			{
				filePipe = Files.newDirectoryStream( waitingRoom.poll() );
				for ( Path visitee : filePipe )
				{
					if ( Files.isDirectory( visitee ) )
					{
						waitingRoom.add( visitee );
						// continue;
					}
					else if ( Files.isRegularFile( visitee )
							&& visitee.getFileName().toString().endsWith( ".eno" ) )
					{
						System.out.println( "\t"+ here +"trying to recognize "+ visitee );
						for ( List<Parser.Word> line : epp.parse( Files.readAllLines( visitee ) ) )
						{
							for ( Parser.Word aWord : line )
							{
								System.out.println( "\t"+ here + aWord );
							}
							System.out.println( "\t"+ here +"(next line)" );
						}
					}
					else
					{
						System.out.println( here +"ignoring "+ visitee );
					}
				}
			}
		}
		catch ( InvalidPathException | IOException ie )
		{
			System.err.println( here + ie );
		}
	}


	static void checkEmitting()
	{
		Section document = new Section();
		//
		FieldList notList = new FieldList( "list name", 0 );
		notList.setPreceedingEmptyLines( 2 );
		notList.addComment( "before" );
		notList.addComment( "after" );
		notList.setFirstCommentPreceededName( true );
		ListItem bullet = new ListItem( "item" );
		bullet.addComment( "li c" );
		notList.addItem( bullet );
		ListItem magazine = new ListItem( "another item" );
		magazine.addComment( "c o li" );
		notList.addItem( magazine );
		document.addChild( notList );
		//
		Section subSibFirst = new Section( "section 1" );
		subSibFirst.setDepth( 1 );
		subSibFirst.addComment( "sec comm"+ System.lineSeparator() +"  indented comment" );
		subSibFirst.setFirstCommentPreceededName( true );
		subSibFirst.setPreceedingEmptyLines( 1 );
		//
		FieldSet nonMap = new FieldSet( "set-name-key" );
		nonMap.setPreceedingEmptyLines( 1 );
		SetEntry pairOne = new SetEntry( "entry one", 1, "value1" );
		nonMap.addEntry( pairOne );
		nonMap.addEntry( "entry 2", "value2" );
		subSibFirst.addChild( nonMap );
		//
		Empty bare = new Empty( "empty" );
		bare.setPreceedingEmptyLines( 1 );
		subSibFirst.addChild( bare );
		//
		Value bla = new Value(
				"field name of",
				0,
				"value assigned" );
		bla.setPreceedingEmptyLines( 1 );
		subSibFirst.addChild( bla );
		//
		document.addChild( subSibFirst );
		//
		Section subSibSecond = new Section( "section 2", 0 );
		subSibSecond.setDepth( 1 );
		subSibSecond.setPreceedingEmptyLines( 1 );
		subSibSecond.setTemplate( subSibFirst );
		//
		Field notBare = new Field( "lonely field" );
		notBare.setPreceedingEmptyLines( 1 );
		subSibSecond.addChild( notBare );
		//
		Multiline formatted = new Multiline( "multi ` line", 3 );
		formatted.setStringValue(
				'\t'+ Lexeme.MULTILINE_OP.name() + System.lineSeparator()
				+'\t'+ Syntaxeme.MULTILINE_BOUNDARY.name() + System.lineSeparator()
				+'\t'+  Syntaxeme.MULTILINE_TEXT.name() +'\t' );
		formatted.setPreceedingEmptyLines( 1 );
		subSibSecond.addChild( formatted );
		//
		document.addChild( subSibSecond );

		StringBuilder sink = new StringBuilder( 100 );
		String asText = document.toString( sink ).toString();
		System.out.println( asText );
		/*
		new ws.nzen.format.eno.ShouldSemantics()
				.compareAsElement( document, new Eno().deserialize( asText ) );
		*/
	}


}
























































