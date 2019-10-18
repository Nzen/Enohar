/* see ../../../../../LICENSE for release details */
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

import ws.nzen.format.eno.missing.FakeBareName;
import ws.nzen.format.eno.missing.FakeField;
import ws.nzen.format.eno.missing.FakeList;
import ws.nzen.format.eno.missing.FakeSection;
import ws.nzen.format.eno.missing.FakeSet;
import ws.nzen.format.eno.parse.Lexeme;

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


	/** Get the requested bare name if it exists; FakeFieldList otherwise.
	* Only provides the first, if multiple have this name */
	public Empty empty( String nameOfExpected )
	{
		return (Empty)getSpecificChild( nameOfExpected,
				EnoType.BARE, MissingChildConsequence.BOMB );
	}
	/** Get the requested bare name if it exists; RuntimeException otherwise.
	* Only provides the first, if multiple have this name */
	public Empty requiredEmpty( String nameOfExpected )
	{
		return (Empty)getSpecificChild( nameOfExpected,
				EnoType.BARE, MissingChildConsequence.EXCEPTION );
	}
	/** Get the requested bare name if it exists; null otherwise.
	* Only provides the first, if multiple have this name */
	public Empty optionalEmpty( String nameOfExpected )
	{
		return (Empty)getSpecificChild( nameOfExpected,
				EnoType.BARE, MissingChildConsequence.NULL );
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
			if ( ( expectedType == actual.getType()
					|| ( expectedType == FIELD_GENERIC
						&& fieldSpecific.contains( actual.getType() ) ) )
				&& actual.getName().equals( nameOfExpected ) )
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
				else if ( expectedType == EnoType.BARE )
				{
					return new FakeBareName( complaint );
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


	public List<Section> sections()
	{
		return sections( null );
	}


	public List<Section> sections( String nameOfExpected )
	{
		List<Section> duplicates = new LinkedList<>();
		boolean needsName = nameOfExpected != null;
		for ( EnoElement candidate : children )
		{
			if ( candidate.type == SECTION
					&& ( ( needsName && nameOfExpected.equals( candidate.getName() ) )
					|| ! needsName ) )
			{
				duplicates.add( (Section)candidate );
			}
		}
		return duplicates;
	}


	public List<FieldList> lists()
	{
		return lists( null );
	}


	public List<FieldList> lists( String nameOfExpected )
	{
		List<FieldList> duplicates = new LinkedList<>();
		boolean needsName = nameOfExpected != null;
		for ( EnoElement candidate : children )
		{
			if ( candidate.type == FIELD_LIST
					&& ( ( needsName && nameOfExpected.equals( candidate.getName() ) )
					|| ! needsName ) )
			{
				duplicates.add( (FieldList)candidate );
			}
		}
		return duplicates;
	}


	public List<FieldSet> fieldSets()
	{
		return fieldSets( null );
	}


	public List<FieldSet> fieldSets( String nameOfExpected )
	{
		List<FieldSet> duplicates = new LinkedList<>();
		boolean needsName = nameOfExpected != null;
		for ( EnoElement candidate : children )
		{
			if ( candidate.type == FIELD_SET
					&& ( ( needsName && nameOfExpected.equals( candidate.getName() ) )
					|| ! needsName ) )
			{
				duplicates.add( (FieldSet)candidate );
			}
		}
		return duplicates;
	}


	public List<Field> fields()
	{
		return fields( null );
	}


	/** returns list of field, value, multiline with the desired name.
	 * For compatability with EnoLib. Caller handles distinguishing
	 * the returned types. */
	public List<Field> fields( String nameOfExpected )
	{
		List<Field> duplicates = new LinkedList<>();
		Set<EnoType> applicable = EnoType.singleRelationFieldTypes();
		boolean needsName = nameOfExpected != null;
		for ( EnoElement candidate : children )
		{
			if ( applicable.contains( candidate.type ) // IMPROVE yieldsField()
					&& ( ( needsName && nameOfExpected.equals( candidate.getName() ) )
					|| ! needsName ) )
			{
				duplicates.add( (Field)candidate );
			}
		}
		return duplicates;
	}


	public List<Empty> empties()
	{
		return empties( null );
	}


	/** returns list of field, value, multiline with the desired name.
	 * For compatability with EnoLib. Caller handles distinguishing
	 * the returned types. */
	public List<Empty> empties( String nameOfExpected )
	{
		List<Empty> duplicates = new LinkedList<>();
		boolean needsName = nameOfExpected != null;
		for ( EnoElement candidate : children )
		{
			if ( candidate.type == EnoType.BARE
					&& ( ( needsName && nameOfExpected.equals( candidate.getName() ) )
					|| ! needsName ) )
			{
				duplicates.add( (Empty)candidate );
			}
		}
		return duplicates;
	}


	public void addChild( EnoElement another )
	{
		if ( another != null )
		{
			if ( another.getType() == EnoType.SECTION
					&& ((Section)another).getDepth() > depth +1 )
			{
				MessageFormat problem = new MessageFormat(
						ExceptionStore.getStore().getExceptionMessage(
								ExceptionStore.ANALYSIS, EnoLocaleKey
									.SECTION_HIERARCHY_LAYER_SKIP ) );
				throw new RuntimeException( problem.format(
						new Object[]{ another.getLine() } ) );
			}
			if ( another.getType() != EnoType.UNKNOWN
					&& another.getType() != EnoType.LIST_ITEM
					&& another.getType() != EnoType.SET_ELEMENT )
			{
				children.add( another );
			}
			else if ( another.getType() == EnoType.LIST_ITEM
					|| another.getType() == EnoType.SET_ELEMENT )
			{
				String localeKey = another.getType() == EnoType.LIST_ITEM
						? EnoLocaleKey.MISSING_NAME_FOR_LIST_ITEM
						: EnoLocaleKey.MISSING_NAME_FOR_FIELDSET_ENTRY;
				MessageFormat problem = new MessageFormat(
						ExceptionStore.getStore().getExceptionMessage(
								ExceptionStore.ANALYSIS, localeKey ) );
				throw new RuntimeException( problem.format(
						new Object[]{ another.getLine() } ) );
			}
			// ASK forgives unknown, apparently
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


	// ide version
	public String toString()
	{
		return type.name() +"-"+ depth +" "+ name;
	}


	public StringBuilder toString( StringBuilder aggregator )
	{
		if ( aggregator == null )
			aggregator = new StringBuilder();
		StringBuilder declaration = new StringBuilder();
		if ( depth > 0 )
		{
			for ( int ind = depth; ind > 0; ind-- )
			{
				declaration.append( Lexeme.SECTION_OP.getChar() );
			}
			declaration.append( " " );
			declaration = super.nameWithEscapes( declaration );
		}
		return toString( aggregator, declaration.toString() );
	}


	@Override
	protected StringBuilder toString( StringBuilder aggregator, String declaration )
	{
		aggregator = super.toString( aggregator, declaration );
		for ( EnoElement child : children )
		{
			switch ( child.getType() )
			{
				case SECTION :
				{	aggregator = ((Section)child).toString( aggregator ); break; }
				case BARE :
				{	aggregator = ((Empty)child).toString( aggregator ); break; }
				case FIELD_LIST :
				{	aggregator = ((FieldList)child).toString( aggregator ); break; }
				case FIELD_SET :
				{	aggregator = ((FieldSet)child).toString( aggregator ); break; }
				case FIELD_GENERIC :
				case FIELD_EMPTY :
				{	aggregator = ((Field)child).toString( aggregator ); break; }
				case FIELD_VALUE :
				{	aggregator = ((Value)child).toString( aggregator ); break; }
				case MULTILINE :
				{	aggregator = ((Multiline)child).toString( aggregator ); break; }
				default :
					aggregator = child.toString(
							aggregator,
							child.nameWithEscapes( new StringBuilder() ).toString() );
			}
		}
		return aggregator;
	}



}


















