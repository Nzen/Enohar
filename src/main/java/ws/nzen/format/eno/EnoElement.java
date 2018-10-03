/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Using a composite rather than insisting on casting */
public class EnoElement
{
	private EnoType contains;
	private String name;
	private List<EnoElement> sequential;
	private Map<String, EnoElement> unique;
	private List<String> values;
	private String parsedValue;
	private String originalValue;
	private List<String> comments; // or Map<int,str> where int is relative offset from name


	public void addToSection( EnoElement node )
	{
		if ( contains == EnoType.UNKNOWN )
		{
			contains = EnoType.SECTION;
			sequential = new LinkedList<>();
		}
		if ( contains != EnoType.SECTION )
		{
			throw new RuntimeException( ExceptionStore.onlyInstance
					.getExceptionMessage( "non sections don't seem to contain other elements" ) );
		}
		if ( node != null )
		{
			sequential.add( node );
		}
	}
	public void addToMap( EnoElement field )
	{
		if ( field.getContains() != EnoType.FIELD_PLAIN )
		{
			throw new RuntimeException( ExceptionStore.onlyInstance
					.getExceptionMessage( "field sets don't seem to contain other elements" ) );
		}
		if ( contains == EnoType.UNKNOWN )
		{
			contains = EnoType.FIELD_SET;
			unique = new TreeMap<>();
		}
		if ( contains != EnoType.FIELD_SET )
		{
			throw new RuntimeException( ExceptionStore.onlyInstance
					.getExceptionMessage( "cant add fieldset to non map element" ) );
		}
		else if ( unique.containsKey( field.getName() ) )
		{
			// IMPROVE if context is 'copy or deep copy' replace rather than die
			throw new RuntimeException( ExceptionStore.onlyInstance
					.getExceptionMessage( "cant add duplicate fieldset" ) );
		}
		else
		{
			unique.put( field.getName(), field );
		}
	}


	public EnoType getContains()
	{
		return contains;
	}
	public void setContains( EnoType contains )
	{
		this.contains = contains;
	}
	public String getName()
	{
		return name;
	}
	public void setName( String name )
	{
		this.name = name;
	}
	public List<EnoElement> getSequential()
	{
		return sequential;
	}
	public void setSequential( List<EnoElement> sequential )
	{
		this.sequential = sequential;
	}
	public Map<String, EnoElement> getUnique()
	{
		return unique;
	}
	public void setUnique( Map<String, EnoElement> unique )
	{
		this.unique = unique;
	}
	public List<String> getValues()
	{
		return values;
	}
	public void setValues( List<String> values )
	{
		this.values = values;
	}
	public String getParsedValue()
	{
		return parsedValue;
	}
	public void setParsedValue( String parsedValue )
	{
		this.parsedValue = parsedValue;
	}
	public String getOriginalValue()
	{
		return originalValue;
	}
	public void setOriginalValue( String originalValue )
	{
		this.originalValue = originalValue;
	}
	public List<String> getComments()
	{
		return comments;
	}
	public void setComments( List<String> comments )
	{
		this.comments = comments;
	}


}


















