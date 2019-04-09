/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import static ws.nzen.format.eno.EnoType.*;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

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


	/** Get the first element with the expected name. Otherwise,
	* provide the response dictated by punishment. Bomb corresponds
	* to the fake version of the expected type. Exception is a
	* runtime exception complaining about that type. Null is just
	* null of that type. */
	private EnoElement getSpecificChild( String nameOfExpected,
			EnoType expectedType, MissingChildConsequence punishment )
	{
		EnoElement candidate = null;
		for ( EnoElement actual : children )
		{
			if ( expectedType == actual.getType() )
			{
				candidate = actual;
				break;
			}
			// TODO handle field is all the types
			// else if (  ) multiline or value or empty, these are field
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


















