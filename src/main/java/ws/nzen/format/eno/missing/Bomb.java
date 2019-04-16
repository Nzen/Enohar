package ws.nzen.format.eno.missing;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/** to ensure all fake elements can complain */
public interface Bomb
{

	public String getComplaint();

	public void setComplaint( String why );


	/** Congrats, you just cut the safe wire. */
	public default String stringKey()
	{
		return getName();
	}
	/** Congrats, you just cut the safe wire. */
	public default String getName()
	{
		return null;
	}
	/** no op */
	public default void setName( String name )
	{
	}


	public default int getNameEscapes()
	{
		return 0; // snide is -1
	}
	public default void setNameEscapes( int nameEscapes )
	{
	}


	/** no op */
	public default void addComment( String another )
	{
	}
	/** Congrats, you just cut the wrong wire.
	 *  @throws NoSuchElementException inflexibly */
	public default String requiredStringComment()
	{
		throw new NoSuchElementException( getComplaint() );
	}
	/** Congrats, you just cut the safe wire. */
	public default String optionalStringComment()
	{
		return null;
	}

	/** Congrats, you just cut the safe wire. */
	public default List<String> getComments()
	{
		return new LinkedList<>();
	}
	/** no op */
	public default void setComments( List<String> comments )
	{
	}
	/** no op */
	public default void cloneComments( List<String> comments )
	{
	}


	public default boolean firstCommentPreceededName()
	{
		return false;
	}
	public default void setFirstCommentPreceededName( boolean firstCommentPreceededName )
	{
	}


	public default int getPreceedingEmptyLines()
	{
		return 0;
	}
	public default void setPreceedingEmptyLines( int preceedingEmptyLines )
	{
	}

	public default String getTemplateName()
	{
		return null;
	}
	public default void setTemplateName( String templateElementName )
	{
	}

	public default int getTemplateEscapes()
	{
		return 0;
	}
	public default void setTemplateEscapes( int templateEscapes )
	{
	}

	public default boolean isShallowTemplate()
	{
		return false;
	}
	public default void setShallowTemplate( boolean shallowTemplate )
	{
	}

	public default int getLine()
	{
		return 0;
	}
	public default void setLine( int line )
	{
	}


}


















