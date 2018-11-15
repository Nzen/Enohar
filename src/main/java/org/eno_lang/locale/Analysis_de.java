/* 1 is the package ; 2 is version comment ; 3 is class name
	caller supplies body and closes the three braces */

package org.eno_lang.locale;

/*
	Generated v3.0 at 2018-11-10T20:21:59.830626
	Using eno-locale messages.json v0.8.0 buillt 2018-07-21T07:50:30.125Z
*/

import java.util.ListResourceBundle;

public class Analysis_de extends ListResourceBundle
{
	protected Object[][] getContents()
	{
		return new Object[][] {

			// Analysis
			{"List Item In Field", "Zeile {0,integer} enthält einen Listeneintrag in einem Feld"},
			{"Fieldset Entry In List", "Zeile {0,integer} enthält einen Fieldset Eintrag in einer Liste."},
			{"Missing Element For Continuation", "Zeile {0,integer} enthält eine Fortsetzung ohne dass davor ein fortsetzbares Element angegeben wurde."},
			{"List Item In Fieldset", "Zeile {0,integer} enthält einen Listeneintrag in einem Fieldset"},
			{"Fieldset Entry In Field", "Zeile {0,integer} enthält einen Fieldset Eintrag in einem Feld."},
			{"Missing Name For List Item", "Zeile {0,integer} enthält einen Listeneintrag ohne dass davor ein Name für eine Liste angegeben wurde."},
			{"Duplicate Fieldset Entry Name", "Das Fieldset ''{0}'' enthält zwei Einträge mit dem Namen ''{1}''."},
			{"Missing Name For Fieldset Entry", "Zeile {0,integer} enthält einen Fieldset Eintrag ohne dass davor ein Name für ein Fieldset angegeben wurde."},
			{"Section Hierarchy Layer Skip", "Zeile {0,integer} beginnt eine Sektion die mehr als eine Ebene tiefer liegt als die aktuelle."},
			{"IMPROVE","b.e.g. should handle better"}
		};
	}
}


