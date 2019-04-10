package ws.nzen.format.eno;

import java.util.Set;
import java.util.TreeSet;

public enum EnoType
{
	SECTION,
	FIELD_EMPTY,
	FIELD_VALUE,
	FIELD_SET,
	FIELD_LIST,
	MULTILINE,
	LIST_ITEM,
	SET_ELEMENT,
	MISSING,
	FIELD_GENERIC,
	UNKNOWN;


	public static Set<EnoType> singleRelationFieldTypes()
	{
		Set<EnoType> phylogeny = new TreeSet<>();
		phylogeny.add( FIELD_EMPTY );
		phylogeny.add( FIELD_VALUE );
		phylogeny.add( MULTILINE );
		return phylogeny;
	}

}


















