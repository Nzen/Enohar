/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import java.util.LinkedList;
import java.util.List;

/** Using a composite rather than insisting on casting */
public class EnoElement
{
	protected final EnoType type;
	protected String name = "";
	protected int nameEscapes = 0;
	protected List<String> comments = new LinkedList<>();
	protected boolean firstCommentPreceededName = false;


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
		this.nameEscapes = nameEscapes;
	}


	public void addComment( String another )
	{
		comments.add( another );
	}
	public List<String> getComments()
	{
		return comments;
	}
	public void setComments( List<String> comments )
	{
		this.comments = comments;
	}


	public boolean firstCommentPreceededName()
	{
		return firstCommentPreceededName;
	}
	public void setFirstCommentPreceededName( boolean firstCommentPreceededName )
	{
		this.firstCommentPreceededName = firstCommentPreceededName;
	}

}


















