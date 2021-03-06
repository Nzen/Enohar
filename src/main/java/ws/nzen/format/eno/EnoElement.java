/* see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import ws.nzen.format.eno.EnoType;
import ws.nzen.format.eno.parse.Lexeme;

/** Using a composite rather than insisting on casting */
public class EnoElement
{
	protected final EnoType type;
	protected String name = "";
	protected int nameEscapes = 0;
	protected int preceedingEmptyLines = 0;
	protected List<String> comments = new LinkedList<>();
	protected boolean firstCommentPreceededName = false;
	protected EnoElement template = null;
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
		name = nameToHave.trim();
		if ( escapes < 0 )
			throw new RuntimeException( "escapes must be positive" ); // FIX find canon
		else
			nameEscapes = escapes;
	}


	public EnoType getType()
	{
		return type;
	}
	public boolean yieldsSection()
	{
		return type == EnoType.SECTION;
	}
	public boolean yieldsFieldsetEntry()
	{
		return type == EnoType.SET_ELEMENT;
	}
	public boolean yieldsListItem()
	{
		return type == EnoType.LIST_ITEM;
	}
	public boolean yieldsEmpty()
	{
		return type == EnoType.FIELD_EMPTY;
	}
	public boolean yieldsFieldset()
	{
		return type == EnoType.FIELD_SET;
	}
	public boolean yieldsList()
	{
		return type == EnoType.FIELD_LIST;
	}
	public boolean yieldsField()
	{
		return yieldsEmpty() || yieldsFieldset()
				|| yieldsList()
				|| type == EnoType.MULTILINE
				|| type == EnoType.FIELD_VALUE;
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
			this.comments.add( new String( one ) );
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
		if ( template != null )
		{
			return template.getName();
		}
		else
		{
			return ""; // ASK or null
		}
	}
	public EnoElement getTemplate()
	{
		return template;
	}

	public void setTemplate( EnoElement baseInstance )
	{
		// NOTE subclasses should check, to keep the combination here small
		throw new RuntimeException( "missed a more specific template opportunity to complain" ); // IMPROVE use canon complaint
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


	// ide version
	public String toString()
	{
		return type.name() +" "+ name;
	}


	protected boolean indentForToString()
	{
		return type == EnoType.LIST_ITEM
				|| type == EnoType.SET_ELEMENT;
	}


	protected StringBuilder nameWithEscapes( StringBuilder declaration )
	{
		if ( nameEscapes > 0 )
		{
			for ( int ind = nameEscapes; ind > 0; ind-- )
			{
				declaration.append( Lexeme.ESCAPE_OP.getChar() );
			}
			declaration.append( " " );
			// NOTE ensuring a leading escape isn't misinterpreted, this'll get trimmed
		}
		declaration.append( name );
		if ( nameEscapes > 0 )
		{
			declaration.append( " " );
			for ( int ind = nameEscapes; ind > 0; ind-- )
			{
				declaration.append( Lexeme.ESCAPE_OP.getChar() );
			}
		}
		return declaration;
	}


	protected StringBuilder toString( StringBuilder aggregator, String declaration )
	{
		if ( aggregator == null )
			aggregator = new StringBuilder();
		for ( int ind = preceedingEmptyLines; ind > 0; ind-- )
		{
			aggregator.append( System.lineSeparator() );
		}
		if ( ! comments.isEmpty() && firstCommentPreceededName )
		{
			for ( String comment : comments.get( 0 ).split( System.lineSeparator() ) )
			{
				if ( indentForToString() )
					aggregator.append( "\t" );
				aggregator.append( Lexeme.COMMENT_OP.getChar() );
				aggregator.append( " " );
				aggregator.append( comment );
				aggregator.append( System.lineSeparator() );
			}
		}
		// checking on behalf of list element
		if ( ! declaration.isEmpty() )
		{
			aggregator.append( declaration );
			if ( template != null )
			{
				aggregator.append( " " );
				aggregator.append( Lexeme.COPY_OP_THIN.getChar() );
				if ( ! shallowTemplate )
					aggregator.append( Lexeme.COPY_OP_DEEP.getChar() );
				aggregator.append( " " );
				aggregator.append( template.getName() );
			}
			aggregator.append( System.lineSeparator() );
		}
		if ( ! comments.isEmpty() )
		{
			Iterator<String> forSkippingFirst = comments.iterator();
			if ( firstCommentPreceededName )
				forSkippingFirst.next(); // NOTE handled above
			while( forSkippingFirst.hasNext() )
			{
				if ( indentForToString() )
					aggregator.append( "\t" );
				aggregator.append( Lexeme.COMMENT_OP.getChar() );
				aggregator.append( " " );
				aggregator.append( forSkippingFirst.next() );
				aggregator.append( System.lineSeparator() );
			}
		}
		return aggregator;
	}

}
































