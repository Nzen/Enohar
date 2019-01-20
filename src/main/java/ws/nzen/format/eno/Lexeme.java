package ws.nzen.format.eno;

public enum Lexeme
{
	/** space or tab */
	WHITESPACE( ' ' ),
	/** \ backslash */
	CONTINUE_OP_SAME( '\\' ),
	/** | pipe */
	CONTINUE_OP_BREAK( '|' ),
	/** # octothorpe */
	SECTION_OP( '#' ),
	/** &gt; greater than */
	COPY_OP_THIN( '<' ),
	/** &gt; greater than, twice */
	COPY_OP_DEEP( '<' ),
	/** - dash */
	LIST_OP( '-' ),
	/** - hyphen */
	MULTILINE_OP( '-' ),
	/** : colon */
	FIELD_START_OP( ':' ),
	/** &lt; greater than */
	COMMENT_OP( '>' ),
	/** = equal */
	SET_OP( '=' ),
	/** ` aka backtick */
	ESCAPE_OP( '`' ),
	/** anything else */
	TEXT( 'a' ),
	/** newline */
	END( '\n' );

	private char typical;

	private Lexeme( char canonLetter )
	{
		typical = canonLetter;
	}


	public boolean match( char input )
	{
		return typical == input;
	}


	public char getChar()
	{
		return typical;
	}

}


















