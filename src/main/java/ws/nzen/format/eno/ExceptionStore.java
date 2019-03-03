/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import java.util.ResourceBundle;

/** Only need to initialize all these Resource Bundles once. */
public class ExceptionStore
{
	public static final String ANALYSIS = "Analysis";
	public static final String TOKENIZATION = "Tokenization";
	public static final String VALIDATION = "Validation";
	private static ExceptionStore onlyInstance;
	private static ResourceBundle rbAnalysis, rbToken, rbValidation;


	private ExceptionStore()
	{
		prepExceptionMessages();
	}


	protected void prepExceptionMessages()
	{
		rbAnalysis = ResourceBundle.getBundle( ANALYSIS );
		rbToken = ResourceBundle.getBundle( TOKENIZATION );
		rbValidation = ResourceBundle.getBundle( VALIDATION );
	}


	public static ExceptionStore getStore()
	{
		if ( onlyInstance == null )
		{
			onlyInstance = new ExceptionStore();
		}
		return onlyInstance;
	}


	public String getExceptionMessage( String context, String messageId )
	{
		ResourceBundle toUse = null;
		switch ( context )
		{
			case ANALYSIS :
			{
				toUse = rbAnalysis;
				break;
			}
			case TOKENIZATION :
			{
				toUse = rbToken;
				break;
			}
			case VALIDATION :
			{
				toUse = rbValidation;
				break;
			}
			default :
			{
				return ""; // or throw some complaint
			}
		}
		return toUse.getString( messageId );
	}


}


















