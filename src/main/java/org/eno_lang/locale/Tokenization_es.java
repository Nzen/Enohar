/* 1 is the package ; 2 is version comment ; 3 is class name
	caller supplies body and closes the three braces */

package org.eno_lang.locale;

/*
	Generated v3.0 at 2018-11-10T20:21:59.830626
	Using eno-locale messages.json v0.8.0 buillt 2018-07-21T07:50:30.125Z
*/

import java.util.ListResourceBundle;

public class Tokenization_es extends ListResourceBundle
{
	protected Object[][] getContents()
	{
		return new Object[][] {

			// Tokenization
			{"Invalid Line", "Línea {0,integer} no sigue un patrón especificado."},
			{"Unterminated Escaped Name", "En la línea {0,integer}, el nombre de un elemento se escapa, pero esta secuencia de escape no termina hasta el final de la línea."},
			{"Unterminated Block", "El bloque ''{0}'' que comienza en la línea {1,integer} no termina hasta el final del documento."},
			{"IMPROVE","b.e.g. should handle better"}
		};
	}
}


