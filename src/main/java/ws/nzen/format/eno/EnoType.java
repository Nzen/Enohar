package ws.nzen.format.eno;

import java.util.Set;
import java.util.TreeSet;

public enum EnoType
{
	SECTION( false ),
	FIELD_EMPTY( true ),
	FIELD_VALUE( true ),
	FIELD_SET( true ),
	FIELD_LIST( true ),
	MULTILINE( false ),
	LIST_ITEM( false ),
	SET_ELEMENT( false ),
	EMPTY( false ),
	MISSING( false ),
	FIELD_GENERIC( true ),
	UNKNOWN( false );

	private boolean amFieldVariant;


	private EnoType( boolean fieldBased )
	{
		amFieldVariant = fieldBased;
	}


	public static Set<EnoType> singleRelationFieldTypes()
	{
		Set<EnoType> phylogeny = new TreeSet<>();
		phylogeny.add( FIELD_EMPTY );
		phylogeny.add( FIELD_VALUE );
		phylogeny.add( MULTILINE );
		return phylogeny;
	}


	public boolean templateAsField()
	{
		return amFieldVariant;
	}

}


















