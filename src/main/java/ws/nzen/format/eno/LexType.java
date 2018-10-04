package ws.nzen.format.eno;

public enum LexType
{
	WHITESPACE,
	CONTINUE_OP_SAME, CONTINUE_OP_BREAK,
	SECTION_OP,
	COPY_OP_THIN, COPY_OP_DEEP,
	LIST_OP,
	BLOCK_OP,
	FIELD_START_OP,
	NAME,
	COMMENT_OP,
	SET_OP,
	ESCAPE_OP,
	TEXT,
	END;
}


















