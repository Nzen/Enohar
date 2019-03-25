/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import java.util.ArrayList;
import java.util.List;

/**  */
public class FieldList extends Field
{
	List<String> values = new ArrayList<>();


	public FieldList()
	{
		super( EnoType.FIELD_LIST );
	}


	public FieldList( String nameToHave, int escapes )
	{
		super( EnoType.FIELD_LIST, nameToHave, escapes );
	}

}


















