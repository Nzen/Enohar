/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import java.util.LinkedList;
import java.util.List;

/**  */
public class Section extends EnoElement
{
	private int depth = 0;
	private List<EnoElement> children = new LinkedList<>();


	public Section()
	{
		super( EnoType.SECTION );
	}


	public Section( String nameToHave, int escapes )
	{
		super( EnoType.SECTION, nameToHave, escapes );
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


















