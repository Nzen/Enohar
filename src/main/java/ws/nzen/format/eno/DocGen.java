/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import java.util.Arrays;
import java.util.List;

import ws.nzen.format.eno.parse.Lexeme;

/** A convenience to create an eno document line by line.
 * This does not enforce creating valid documents. For that,
 * I recommdend building them up with EnoElements. */
public class DocGen
{
	private StringBuilder document = new StringBuilder();


	public void reset()
	{
		document = new StringBuilder();
	}


	/** @return whether this rejected the section for
	 * too deep relative to the previous. */
	public DocGen section( String name, int depth )
	{
		return section( name, depth, "", false );
	}


	public DocGen section( String name, int depth,
			String templateName, boolean shallowCopy )
	{
		for ( int ind = 0; ind < depth; ind++ )
			document.append( Lexeme.SECTION_OP.getChar() );
		document.append( name );
		if ( ! templateName.isEmpty() )
		{
			document.append( Lexeme.COPY_OP_THIN.getChar() );
			if ( ! shallowCopy )
				document.append( Lexeme.COPY_OP_THIN.getChar() );
			document.append( templateName );
		}
		document.append( System.lineSeparator() );
		return this;
	}


	public DocGen field( String name,
			String templateName, boolean shallowCopy )
	{
		document.append( name );
		document.append( Lexeme.FIELD_START_OP.getChar() );
		if ( ! templateName.isEmpty() )
		{
			document.append( Lexeme.COPY_OP_THIN.getChar() );
			if ( ! shallowCopy )
				document.append( Lexeme.COPY_OP_THIN.getChar() );
			document.append( templateName );
		}
		document.append( System.lineSeparator() );
		return this;
	}


	public DocGen field( String name )
	{
		return field( name, "" );
	}


	public DocGen field( String name, String value )
	{
		document.append( name );
		document.append( Lexeme.FIELD_START_OP.getChar() );
		document.append( value );
		document.append( System.lineSeparator() );
		return this;
	}


	public DocGen moreValue( String value, boolean noSpace )
	{
		document.append( ( ! noSpace )
				? Lexeme.CONTINUE_OP_EMPTY.getChar()
				: Lexeme.CONTINUE_OP_SPACE.getChar() );
		document.append( value );
		document.append( System.lineSeparator() );
		return this;
	}


	public DocGen comment( String value )
	{
		document.append( Lexeme.COMMENT_OP.getChar() );
		document.append( value );
		document.append( System.lineSeparator() );
		return this;
	}


	public DocGen listItem( String value )
	{
		document.append( Lexeme.LIST_OP.getChar() );
		document.append( value );
		document.append( System.lineSeparator() );
		return this;
	}


	public DocGen setPair( String name, String value )
	{
		document.append( name );
		document.append( Lexeme.SET_OP.getChar() );
		document.append( value );
		document.append( System.lineSeparator() );
		return this;
	}


	public DocGen multiline( String name, String value )
	{
		document.append( Lexeme.MULTILINE_OP.getChar() );
		document.append( Lexeme.MULTILINE_OP.getChar() );
		document.append( name );
		document.append( System.lineSeparator() );
		document.append( value );
		document.append( System.lineSeparator() );
		document.append( Lexeme.MULTILINE_OP.getChar() );
		document.append( Lexeme.MULTILINE_OP.getChar() );
		document.append( name );
		document.append( System.lineSeparator() );
		return this;
	}


	public DocGen empty( int emptyLines )
	{
		for ( int ind = 0; ind < emptyLines; ind++ )
			document.append( System.lineSeparator() );
		return this;
	}


	public String toString()
	{
		return document.toString();
	}


	public List<String> toStrList()
	{
		return Arrays.asList( toString().split( System.lineSeparator() ) );
	}

}
























































