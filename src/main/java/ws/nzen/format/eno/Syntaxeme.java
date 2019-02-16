package ws.nzen.format.eno;

public enum Syntaxeme
{
	/** operator length and various elements */
	SECTION,
	/** a name maybe escape length */
	FIELD,
	/** trimmed text */
	VALUE,
	/** next name is to copy from */
	LIST_ELEMENT,
	/** field = value */
	SET_ELEMENT,
	/** uninterpreted value in a multiline */
	MULTILINE_TEXT,
	/** the operator, represented as a length, maybe includes name */
	MULTILINE_BOUNDARY,
	/** copy operator */
	COPY,
	/** comment text */
	COMMENT,
	/** blank lines */
	EMPTY;
}


















