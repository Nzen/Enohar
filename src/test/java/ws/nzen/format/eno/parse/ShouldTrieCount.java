/** see ../../../../../LICENSE for release details */
package ws.nzen.format.eno.parse;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import ws.nzen.format.eno.parse.NaiveTrie;

/**  */
class ShouldTrieCount
{

	/**
	 * Test method for {@link ws.nzen.format.eno.parse.NaiveTrie#contains(java.lang.String)}.
	 */
	@Test
	void testContains()
	{
		Set<Character> alphabet = new TreeSet<>();
		alphabet.add( Character.valueOf( 'a' ) );
		alphabet.add( Character.valueOf( 'Z' ) );
		NaiveTrie preficies = new NaiveTrie( alphabet );
		String threeSpaces = "aaa";
		preficies.add( threeSpaces );
		preficies.renderToSdtOut();
		assertTrue( preficies.contains( "a" ) );
		assertTrue( preficies.contains( threeSpaces ) );
		assertFalse( preficies.contains( "Z" ) );

		String threeTabs = "ZZZ";
		preficies.add( threeTabs );
		assertTrue( preficies.contains( "a" ) );
		assertTrue( preficies.contains( "ZZ" ) );
		preficies.renderToSdtOut();
	}


	/**
	 * Test method for {@link ws.nzen.format.eno.parse.NaiveTrie#longestCommonPrefix()}.
	 */
	@Test
	void testLongestCommonPrefix()
	{
		Set<Character> alphabet = new TreeSet<>();
		alphabet.add( Character.valueOf( '+' ) );
		alphabet.add( Character.valueOf( '^' ) );
		NaiveTrie preficies = new NaiveTrie( alphabet );
		String twoSpaces = "++", threeSpaces = "+++";
		String spaceTab = "+^";
		String threeTabs = "^^^";
		preficies.add( twoSpaces );
		assertTrue(
				preficies.longestCommonPrefix()
					.equals( twoSpaces ) );
		preficies.add( threeSpaces );
		assertTrue( preficies.longestCommonPrefix().equals( twoSpaces ) );
		preficies.add( spaceTab );
		assertTrue( preficies.longestCommonPrefix().equals( "+" ) );
		preficies.add( threeTabs );
		assertTrue( preficies.longestCommonPrefix().isEmpty() );
		preficies.renderToSdtOut();
	}

}


















