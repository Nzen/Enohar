/* 1 is the package ; 2 is version comment ; 3 is class name
	caller supplies body and closes the three braces */

package org.eno_lang.locale;

/*
	Generated v3.0 at 2018-11-10T20:21:59.830626
	Using eno-locale messages.json v0.8.0 buillt 2018-07-21T07:50:30.125Z
*/

import java.util.ListResourceBundle;

public class Tokenization_en extends ListResourceBundle
{
	protected Object[][] getContents()
	{
		return new Object[][] {

			// Tokenization
			{"Invalid Line", "Line {0,integer} does not follow any specified pattern."},
			{"Unterminated Escaped Name", "In line {0,integer} the name of an element is escaped, but the escape sequence is not terminated until the end of the line."},
			{"Unterminated Block", "The block ''{0}'' starting in line {1,integer} is not terminated until the end of the document."},
			{"IMPROVE","b.e.g. should handle better"}
		};
	}
}


