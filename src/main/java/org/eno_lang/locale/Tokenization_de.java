/* 1 is the package ; 2 is version comment ; 3 is class name
	caller supplies body and closes the three braces */

package org.eno_lang.locale;

/*
	Generated v3.0 at 2018-11-10T20:21:59.830626
	Using eno-locale messages.json v0.8.0 buillt 2018-07-21T07:50:30.125Z
*/

import java.util.ListResourceBundle;

public class Tokenization_de extends ListResourceBundle
{
	protected Object[][] getContents()
	{
		return new Object[][] {

			// Tokenization
			{"Invalid Line", "Zeile {0,integer} folgt keinem spezifierten Muster."},
			{"Unterminated Escaped Name", "In Zeile {0,integer} wird der Name eines Elements escaped, jedoch wird diese Escape Sequenz bis zum Ende der Zeile nicht mehr beendet."},
			{"Unterminated Block", "Der Block ''{0}'' der in Zeile {1,integer} beginnt wird bis zum Ende des Dokuments nicht mehr beendet."},
			{"IMPROVE","b.e.g. should handle better"}
		};
	}
}


