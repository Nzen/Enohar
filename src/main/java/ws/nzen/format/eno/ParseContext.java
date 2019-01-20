package ws.nzen.format.eno;

public enum ParseContext
{
	DOCUMENT, // which is just section level 0
	SECTION,
	BLOCK,
	SET,
	LIST,
	FIELD,
	VALUE,

	SECTION_INTERIOR,
	FIELD_ANY,
	FIELD_VALUE,
	LIST_VALUE,
	MAP_VALUE;
}


















