package ws.nzen.format.eno;

public enum Syntaxeme
{
	/** operator length and various elements */
	SECTION,
	/** operator type and name */
	TEMPLATE,
	/** a value */
	LIST_ELEMENT,
	/** a field followed by one or more set elements */
	SET,
	/** field = value */
	SET_ELEMENT,
	/** the field name and operator length */
	BLOCK,
	/** uninterpreted value in a multiline */
	MULTILINE_TEXT,
	/** the operator, represented as a length, maybe includes name */
	MULTILINE_BOUNDARY,
	/** a name maybe escape length */
	FIELD,
	/** length */
	FIELD_ESCAPE,
	/** trimmed text */
	VALUE,
	/** ? */
	SHALLOW_COPY,
	/** ? */
	DEEP_COPY,
	/** comment text */
	COMMENT;
}


















