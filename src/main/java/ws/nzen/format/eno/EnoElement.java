/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/** Using a composite rather than insisting on casting */
public class EnoElement
{
	protected final EnoType type;
	protected String name = "";
	protected int nameEscapes = 0;
	protected int preceedingEmptyLines = 0;
	protected List<String> comments = new LinkedList<>();
	protected boolean firstCommentPreceededName = false;
	protected String templateName = "";
	protected int templateEscapes = 0;
	protected boolean shallowTemplate = false;
	protected int line = 0;


	protected EnoElement( EnoType typeToBe )
	{
		type = typeToBe;
	}

	public EnoElement( EnoType typeToBe,
			String nameToHave, int escapes )
	{
		type = typeToBe;
		name = nameToHave;
		nameEscapes = escapes;
	}


	public EnoType getType()
	{
		return type;
	}


	public String stringKey()
	{
		return getName();
	}
	public String getName()
	{
		return name;
	}
	public void setName( String name )
	{
		this.name = name;
	}


	public int getNameEscapes()
	{
		return nameEscapes;
	}
	public void setNameEscapes( int nameEscapes )
	{
		if ( nameEscapes >= 0 )
		{
			this.nameEscapes = nameEscapes;
		}
	}


	public void addComment( String another )
	{
		comments.add( another );
	}
	public String requiredStringComment()
	{
		return getFirstComment( true );
	}
	public String optionalStringComment()
	{
		return getFirstComment( false );
	}
	protected String getFirstComment( boolean complain )
	{
		if ( ! comments.isEmpty() && firstCommentPreceededName )
		{
			return comments.get( 0 );
		}
		else if ( complain )
		{
			MessageFormat problem = new MessageFormat(
					ExceptionStore.getStore().getExceptionMessage(
							ExceptionStore.VALIDATION,
							EnoLocaleKey.MISSING_FIELD_VALUE ) ); // FIX use required comment missing
			throw new NoSuchElementException( problem.format( new Object[]{ name } ) );
		}
		else
		{
			return null; // per spec
		}
	}

	public List<String> getComments()
	{
		return comments;
	}
	public void setComments( List<String> comments )
	{
		this.comments = comments;
	}
	public void cloneComments( List<String> comments )
	{
		this.comments.clear();
		for ( String one : comments )
		{
			comments.add( new String( one ) );
		}
	}


	public boolean firstCommentPreceededName()
	{
		return firstCommentPreceededName;
	}
	public void setFirstCommentPreceededName( boolean firstCommentPreceededName )
	{
		this.firstCommentPreceededName = firstCommentPreceededName;
	}


	public int getPreceedingEmptyLines()
	{
		return preceedingEmptyLines;
	}
	public void setPreceedingEmptyLines( int preceedingEmptyLines )
	{
		if ( preceedingEmptyLines >= 0 )
		{
			this.preceedingEmptyLines = preceedingEmptyLines;
		}
	}

	public String getTemplateName()
	{
		return templateName;
	}
	public void setTemplateName( String templateElementName )
	{
		this.templateName = templateElementName;
	}

	public int getTemplateEscapes()
	{
		return templateEscapes;
	}
	public void setTemplateEscapes( int templateEscapes )
	{
		this.templateEscapes = templateEscapes;
	}

	public boolean isShallowTemplate()
	{
		return shallowTemplate;
	}
	public void setShallowTemplate( boolean shallowTemplate )
	{
		this.shallowTemplate = shallowTemplate;
	}

	public int getLine()
	{
		return line;
	}
	public void setLine( int line )
	{
		this.line = line;
	}

}


















