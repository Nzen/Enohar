/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import java.text.MessageFormat;

/**  */
public class SetEntry extends Value
{

	public SetEntry( String nameToHave )
	{
		this( nameToHave, 0 );
	}

	public SetEntry( String nameToHave, int escapes )
	{
		super( EnoType.SET_ELEMENT, nameToHave, escapes );
	}


	public SetEntry( String nameToHave, int escapes, String value )
	{
		this( nameToHave, escapes );
		setStringValue( value );
	}


	public void setTemplate( FieldSet baseInstance )
	{
		template = baseInstance;
	}

	@Override
	public void setTemplate( EnoElement baseInstance )
	{
		if ( baseInstance == null )
		{
			template = null;
			return;
		}
		// else
		// FIX use real keys
		if ( baseInstance.type != EnoType.UNKNOWN )
		throw new RuntimeException( "FIX 4test NP change to match context" );
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


















