/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import static ws.nzen.format.eno.EnoType.FIELD_GENERIC;
import static ws.nzen.format.eno.EnoType.FIELD_LIST;
import static ws.nzen.format.eno.EnoType.FIELD_SET;
import static ws.nzen.format.eno.EnoType.MISSING;
import static ws.nzen.format.eno.EnoType.SECTION;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ws.nzen.format.eno.missing.FakeField;
import ws.nzen.format.eno.missing.FakeList;
import ws.nzen.format.eno.missing.FakeSection;
import ws.nzen.format.eno.missing.FakeSet;

/**  */
public class Section extends EnoElement
{
	private int depth = 0;
	private List<EnoElement> children = new LinkedList<>();
	private enum MissingChildConsequence
	{
		EXCEPTION, NULL, BOMB;
	};


	public Section()
	{
		super( SECTION );
	}


	public Section( String nameToHave )
	{
		this( nameToHave, 0 );
	}


	public Section( String nameToHave, int escapes )
	{
		super( SECTION, nameToHave, escapes );
	}


	protected Section( EnoType which )
	{
		super( which );
		if ( which != MISSING )
		{
			// warn or die
		}
	}

	/** Get the requested section if it exists; FakeSection otherwise.
	* Only provides the first, if multiple have this name */
	public Section section( String nameOfExpected )
	{
		return (Section)getSpecificChild( nameOfExpected,
				SECTION, MissingChildConsequence.BOMB );
	}
	/** Get the requested section if it exists; RuntimeException otherwise.
	* Only provides the first, if multiple have this name */
	public Section requiredSection( String nameOfExpected )
	{
		return (Section)getSpecificChild( nameOfExpected,
				SECTION, MissingChildConsequence.EXCEPTION );
	}
	/** Get the requested section if it exists; null otherwise.
	* Only provides the first, if multiple have this name */
	public Section optionalSection( String nameOfExpected )
	{
		return (Section)getSpecificChild( nameOfExpected,
				SECTION, MissingChildConsequence.NULL );
	}


	/** Get the requested field-list if it exists; FakeFieldList otherwise.
	* Only provides the first, if multiple have this name */
	public FieldList list( String nameOfExpected )
	{
		return (FieldList)getSpecificChild( nameOfExpected,
				FIELD_LIST, MissingChildConsequence.BOMB );
	}
	/** Get the requested field-list if it exists; RuntimeException otherwise.
	* Only provides the first, if multiple have this name */
	public FieldList requiredList( String nameOfExpected )
	{
		return (FieldList)getSpecificChild( nameOfExpected,
				FIELD_LIST, MissingChildConsequence.EXCEPTION );
	}
	/** Get the requested field-list if it exists; null otherwise.
	* Only provides the first, if multiple have this name */
	public FieldList optionalList( String nameOfExpected )
	{
		return (FieldList)getSpecificChild( nameOfExpected,
				FIELD_LIST, MissingChildConsequence.NULL );
	}


	/** Get the requested field-set if it exists; FakeFieldList otherwise.
	* Only provides the first, if multiple have this name */
	public FieldSet fieldset( String nameOfExpected )
	{
		return (FieldSet)getSpecificChild( nameOfExpected,
				FIELD_SET, MissingChildConsequence.BOMB );
	}
	/** Get the requested field-set if it exists; RuntimeException otherwise.
	* Only provides the first, if multiple have this name */
	public FieldSet requiredFieldSet( String nameOfExpected )
	{
		return (FieldSet)getSpecificChild( nameOfExpected,
				FIELD_SET, MissingChildConsequence.EXCEPTION );
	}
	/** Get the requested field-set if it exists; null otherwise.
	* Only provides the first, if multiple have this name */
	public FieldSet optionalFieldset( String nameOfExpected )
	{
		return (FieldSet)getSpecificChild( nameOfExpected,
				FIELD_SET, MissingChildConsequence.NULL );
	}


	/** Get the requested field if it exists; FakeFieldList otherwise.
	* Only provides the first, if multiple have this name */
	public Field field( String nameOfExpected )
	{
		return (Field)getSpecificChild( nameOfExpected,
				FIELD_GENERIC, MissingChildConsequence.BOMB );
	}
	/** Get the requested field if it exists; RuntimeException otherwise.
	* Only provides the first, if multiple have this name */
	public Field requiredField( String nameOfExpected )
	{
		return (Field)getSpecificChild( nameOfExpected,
				FIELD_GENERIC, MissingChildConsequence.EXCEPTION );
	}
	/** Get the requested field if it exists; null otherwise.
	* Only provides the first, if multiple have this name */
	public Field optionalField( String nameOfExpected )
	{
		return (Field)getSpecificChild( nameOfExpected,
				FIELD_GENERIC, MissingChildConsequence.NULL );
	}


	/** Get the first element with the expected name. Otherwise,
	* provide the response dictated by punishment. Bomb corresponds
	* to the fake version of the expected type. Exception is a
	* runtime exception complaining about that type. Null is just
	* null of that type. */
	private EnoElement getSpecificChild( String nameOfExpected,
			EnoType expectedType, MissingChildConsequence punishment )
	{
		EnoElement candidate = null;
		Set<EnoType> fieldSpecific = ( expectedType == FIELD_GENERIC )
				? EnoType.singleRelationFieldTypes() : null;
		for ( EnoElement actual : children )
		{
			if ( expectedType == actual.getType()
					|| ( expectedType == FIELD_GENERIC
						&& fieldSpecific.contains( actual.getType() )
						&& actual.getName().equals( nameOfExpected ) ) )
			{
				candidate = actual;
				break;
			}
		}
		if ( candidate == null )
		{
			if ( punishment == MissingChildConsequence.BOMB
					|| punishment == MissingChildConsequence.EXCEPTION )
			{
				String localeKey;
				switch ( expectedType )
				{
					case SECTION :
					{
						localeKey = EnoLocaleKey.MISSING_SECTION;
						break;
					}
					case FIELD_EMPTY :
					case MULTILINE :
					{
						localeKey = EnoLocaleKey.MISSING_FIELD;
						break;
					}
					case FIELD_VALUE :
					{
						localeKey = EnoLocaleKey.MISSING_FIELD_VALUE;
						break;
					}
					case FIELD_LIST :
					{
						localeKey = EnoLocaleKey.MISSING_LIST;
						break;
					}
					case FIELD_SET :
					{
						localeKey = EnoLocaleKey.MISSING_FIELDSET;
						break;
					}
					case LIST_ITEM :
					{
						localeKey = EnoLocaleKey.MISSING_LIST_ITEM_VALUE;
						break;
					}
					case SET_ELEMENT :
					{
						localeKey = EnoLocaleKey.MISSING_FIELDSET_ENTRY_VALUE;
						break;
					}
					default :
					{
						localeKey = EnoLocaleKey.MISSING_ELEMENT;
						break;
					}
				}
				MessageFormat problem = new MessageFormat(
						ExceptionStore.getStore().getExceptionMessage(
								ExceptionStore.VALIDATION, localeKey ) );
				String complaint = problem.format( new Object[]{ nameOfExpected } );
				if ( punishment == MissingChildConsequence.EXCEPTION )
				{
					throw new RuntimeException( complaint );
				}
				else if ( expectedType == SECTION )
				{
					return new FakeSection( complaint );
				}
				else if ( expectedType == FIELD_LIST )
				{
					return new FakeList( complaint );
				}
				else if ( expectedType == FIELD_SET )
				{
					return new FakeSet( complaint );
				}
				else
				{
					return new FakeField( complaint );
				}
			}
			else
			{
				// NOTE consequence is NULL
				return null;
			}
		}
		else
		{
			return candidate;
		}
	}


	public List<Section> sections( String nameOfExpected )
	{
		List<Section> duplicates = new LinkedList<>();
		for ( EnoElement candidate : children )
		{
			if ( candidate.type == SECTION
					&& nameOfExpected.equals( candidate.getName() ) )
			{
				duplicates.add( (Section)candidate );
			}
		}
		return duplicates;
	}


	public List<FieldList> lists( String nameOfExpected )
	{
		List<FieldList> duplicates = new LinkedList<>();
		for ( EnoElement candidate : children )
		{
			if ( candidate.type == FIELD_LIST
					&& nameOfExpected.equals( candidate.getName() ) )
			{
				duplicates.add( (FieldList)candidate );
			}
		}
		return duplicates;
	}


	public List<FieldSet> fieldSets( String nameOfExpected )
	{
		List<FieldSet> duplicates = new LinkedList<>();
		for ( EnoElement candidate : children )
		{
			if ( candidate.type == FIELD_SET
					&& nameOfExpected.equals( candidate.getName() ) )
			{
				duplicates.add( (FieldSet)candidate );
			}
		}
		return duplicates;
	}


	/** returns list of field, value, multiline with the desired name.
	 * For compatability with EnoLib. Caller handles distinguishing
	 * the returned types. */
	public List<Field> fields( String nameOfExpected )
	{
		List<Field> duplicates = new LinkedList<>();
		Set<EnoType> applicable = EnoType.singleRelationFieldTypes();
		for ( EnoElement candidate : children )
		{
			if ( applicable.contains( candidate.type )
					&& nameOfExpected.equals( candidate.getName() ) )
			{
				duplicates.add( (Field)candidate );
			}
		}
		return duplicates;
	}


	public void addChild( EnoElement another )
	{
		if ( another != null
				&& another.getType() != EnoType.UNKNOWN )
		{
			children.add( another );
		}
	}


	public int getDepth()
	{
		return depth;
	}
	public void setDepth( int depth )
	{
		this.depth = depth;
	}


	public List<EnoElement> elements()
	{
		return children;
	}
	public List<EnoElement> getChildren()
	{
		return children;
	}
	public void setChildren( List<EnoElement> children )
	{
		this.children = children;
	}


	public void setTemplate( Section baseInstance )
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


















