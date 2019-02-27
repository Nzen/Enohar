/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/** Record the uses of characters in a limited alphabet */
public class NaiveTrie
{
	NtrieNode rootNode;
	Map<Character, Integer> letterIndicies;

	public NaiveTrie( Set<Character> alphabet )
	{
		letterIndicies = new TreeMap<>();
		int ind = 0;
		for ( Character letter : alphabet )
		{
			letterIndicies.put( letter, Integer.valueOf( ind ) );
			ind++;
		}
	}


	public void add( String word )
	{
		if ( rootNode == null )
		{
			rootNode = new NtrieNode();
		}
		rootNode.add( word );
	}


	public boolean contains( String lost )
	{
		if ( rootNode == null )
		{
			return false;
		}
		else
		{
			return rootNode.contains( lost );
		}
	}


	public String longestCommonPrefix()
	{
		if ( rootNode == null )
		{
			return "";
		}
		else
		{
			return rootNode.longestCommonPrefix( "", -1 );
		}
	}


	public void renderToSdtOut()
	{
		// Improve use StringBuilder to reduce i/o
			// jk, I'm expecting a small alphabet, else I'd have used a real Trie
		System.out.print( " Alphabet : " );
		for ( Character letter : letterIndicies.keySet() )
		{
			System.out.print( letter +", " );
		}
		System.out.println();
		rootNode.renderToSdtOut( 0 );
	}


	public void clear()
	{
		rootNode = null;
	}


	protected class NtrieNode
	{
		int[] uses = new int[ letterIndicies.size() ];
		NtrieNode[] children = new NtrieNode[ letterIndicies.size() ];

		NtrieNode()
		{
			for ( int ind = 0; ind < uses.length; ind++ )
			{
				uses[ ind ] = 0;
			}
		}

		void add( String substring )
		{
			if ( ! substring.isEmpty() )
			{
				Integer ind = letterIndicies.get( Character
						.valueOf( substring.charAt( 0 ) ) );
				uses[ ind ] += 1;
				if ( substring.length() > 1 )
				{
					if ( children[ ind ] == null )
					{
						children[ ind ] = new NtrieNode();
					}
					children[ ind ].add( substring.substring( 1 ) );
				}
			}
		}

		boolean contains( String substring )
		{
			if ( ! substring.isEmpty() )
			{
				Integer ind = letterIndicies.get( Character
						.valueOf( substring.charAt( 0 ) ) );
				if ( uses[ ind ] > 0 )
				{
					if ( substring.length() > 1 )
					{
						if ( children[ ind ] != null )
						{
							return children[ ind ].contains( substring.substring( 1 ) );
						}
						else
						{
							return false;
						}
					}
					else
					{
						return true;
					}
				}
				else
				{
					return false;
				}
			}
			else
			{
				return true; // ASK assert paranoid
			}
		}

		String longestCommonPrefix( String prefix, int prefixUsage )
		{
			
			int maxInd = -1;
			boolean hasOne = false;
			for ( int ind = 0; ind < uses.length; ind++ )
			{
				if ( uses[ ind ] > 0 && ! hasOne )
				{
					maxInd = ind;
					hasOne = true;
				}
				else if ( uses[ ind ] > 0 )
				{
					// NOTE multiple options, this is no longer common
					return prefix;
				}
			}
			if ( hasOne )
			{
				// NOTE handle the first time
				if ( prefixUsage < 0 )
				{
					prefixUsage = uses[ maxInd ];
				}
				else if ( uses[ maxInd ] != prefixUsage )
				{
					// presumably more means it came from another path somehow
					return prefix;
				}
				Character which = null;
				for ( Character letter : letterIndicies.keySet() )
				{
					Integer ind = letterIndicies.get( letter );
					if ( maxInd == ind.intValue() )
					{
						which = letter;
						break;
					}
				}
				if ( children[ maxInd ] != null )
				{
					return children[ maxInd ].longestCommonPrefix(
							prefix + which, uses[ maxInd ] );
				}
				else
				{
					return prefix + which;
				}
			}
			else
			{
				// this is an empty node
				return prefix;
			}
		}

		void renderToSdtOut( int offset )
		{
			for ( int ind = 0; ind < offset; ind++ )
			{
				System.out.print( " " );
			}
			for ( int ind = 0; ind < uses.length; ind++ )
			{
				System.out.print( uses[ ind ] +"_" );
			}
			System.out.println();
			for ( int ind = 0; ind < children.length; ind++ )
			{
				if ( children[ ind ] != null )
				{
					children[ ind ].renderToSdtOut( offset +1 );
				}
			}
		}
	}

}





































