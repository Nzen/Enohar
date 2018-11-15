/* 1 is the package ; 2 is version comment ; 3 is class name
	caller supplies body and closes the three braces */

package org.eno_lang.locale;

/*
	Generated v3.0 at 2018-11-10T20:21:59.830626
	Using eno-locale messages.json v0.8.0 buillt 2018-07-21T07:50:30.125Z
*/

import java.util.ListResourceBundle;

public class Analysis_es extends ListResourceBundle
{
	protected Object[][] getContents()
	{
		return new Object[][] {

			// Analysis
			{"List Item In Field", "Línea {0,integer} contiene una entrada de lista en medio de una casilla."},
			{"Fieldset Entry In List", "Línea {0,integer} contiene una casilla de collecíon en medio de una lista."},
			{"Missing Element For Continuation", "Línea {0,integer} contiene una continuacíon sin un elemento que se puede continuar especificado antes."},
			{"List Item In Fieldset", "Línea {0,integer} contiene una entrada de lista en medio de una collecíon de casillas."},
			{"Fieldset Entry In Field", "Línea {0,integer} contiene una casilla de collecíon en medio de una casilla."},
			{"Missing Name For List Item", "Línea {0,integer} contiene una entrada de lista sin nombre para una lista especificada antes."},
			{"Duplicate Fieldset Entry Name", "La collecíon de casillas ''{0}'' contiene dos casillas llamadas ''{1}''."},
			{"Missing Name For Fieldset Entry", "Línea {0,integer} contiene una casilla de collecíon sin un nombre especificado para una collecíon de casillas antes."},
			{"Section Hierarchy Layer Skip", "Línea {0,integer} inicia una sección que es más de un nivel más bajo el actual."},
			{"IMPROVE","b.e.g. should handle better"}
		};
	}
}


