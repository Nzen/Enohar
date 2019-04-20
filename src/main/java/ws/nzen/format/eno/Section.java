/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import static ws.nzen.format.eno.EnoType.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ws.nzen.format.eno.missing.FakeSection;

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
						&& fieldSpecific.contains( actual.getType() ) ) )
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
				if ( expectedType == SECTION )
					localeKey =  EnoLocaleKey.MISSING_SECTION;
				else // TODO
					localeKey = "one for each type"; // choose the corresponding missing locale key
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
				else // TODO
					return null; // return corresponding fake type
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


	public List<EnoElement> getChildren()
	{
		return children;
	}
	public void setChildren( List<EnoElement> children )
	{
		this.children = children;
	}



}

















