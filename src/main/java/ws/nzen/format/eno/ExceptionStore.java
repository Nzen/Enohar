/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

/**  */
public class ExceptionStore
{

	public static ExceptionStore onlyInstance;


	private ExceptionStore()
	{
		
	}


	public String getExceptionMessage( String context )
	{
		return context; // IMPROVE actually search the active map
	}


}


















