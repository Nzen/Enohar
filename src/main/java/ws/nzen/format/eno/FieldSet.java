/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import static ws.nzen.format.eno.EnoType.MISSING;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**  */
public class FieldSet extends Field
{
	private List<SetEntry> entries = new ArrayList<>();

	/**  */
	public FieldSet()
	{
		super( EnoType.FIELD_SET );
	}


	public FieldSet( String nameToHave, int escapes )
	{
		super( EnoType.FIELD_SET, nameToHave, escapes );
	}


	public FieldSet( Field likelyEmpty )
	{
		this( new String( likelyEmpty.getName() ), likelyEmpty.getNameEscapes() );
		cloneFrom( likelyEmpty );
	}


	protected FieldSet( EnoType which )
	{
		super( which );
		if ( which != MISSING )
		{
			// warn or die
		}
	}


	public void addEntry( SetEntry anotherChild )
	{
		if ( anotherChild != null )
		{
			entries.add( anotherChild );
		}
	}


	/** returns null if the name isn't in this list;
	 * returns the first with that name, when multiple added. */
	public SetEntry entry( String name )
	{
		if ( name == null )
		{
			return null;
		}
		for ( SetEntry child : entries )
		{
			if ( child.getName().equals( name ) )
			{
				return child;
			}
		}
		return null;
	}


	public List<SetEntry> entries()
	{
		return entries; // ASK consider a defensive copy
	}


	public void setTemplate( FieldSet baseInstance )
	{
		template = baseInstance;
	}

	@Override
	public void setTemplate( EnoElement baseInstance )
	{
		// FIX use real keys
		String localeComplaint = "";
		switch ( baseInstance.getType() )
		{
			case SECTION :
			{
				setTemplate( (Section)baseInstance );
				return;
			}
			case FIELD_EMPTY :
			{
				localeComplaint = EnoLocaleKey.EXPECTED_SECTION_GOT_EMPTY;
				break;
			}
			case FIELD_VALUE :
			case MULTILINE :
			{
				localeComplaint = EnoLocaleKey.EXPECTED_SECTION_GOT_FIELD;
				break;
			}
			case FIELD_LIST :
			case LIST_ITEM :
			{
				localeComplaint = EnoLocaleKey.EXPECTED_SECTION_GOT_LIST;
				break;
			}
			case FIELD_SET :
			case SET_ELEMENT :
			{
				localeComplaint = EnoLocaleKey.EXPECTED_SECTION_GOT_FIELDSET;
				break;
			}
			default :
			{
				localeComplaint = EnoLocaleKey.MISSING_ELEMENT;
				break;
			}
		}
		MessageFormat problem = new MessageFormat(
				ExceptionStore.getStore().getExceptionMessage(
						ExceptionStore.ANALYSIS, localeComplaint ) );
		throw new RuntimeException( problem.format( new Object[]{ baseInstance.getName() } ) );
	}


}


















