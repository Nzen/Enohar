/* 1 is the package ; 2 is version comment ; 3 is class name
	caller supplies body and closes the three braces */

package org.eno_lang.locale;

/*
	Generated v3.0 at 2018-11-10T17:03:37.373376
	Using eno-locale messages.json v0.8.0 buillt 2018-07-21T07:50:30.125Z
*/

import java.util.ListResourceBundle;

public class Messages_es extends ListResourceBundle
{
	protected Object[][] getContents()
	{
		return new Object[][] {

			// Validation
			{"Expected Field Got Fieldset", "En lugar de la casilla ''{0}'' esperada se encontró una collecíon de casillas con este nombre."},
			{"Expected Field Got Section", "En lugar de la casilla ''{0}'' esperada se encontró una sección con este nombre."},
			{"Expected Fields Got List", "Solo se esperaban casillas con el nombre ''{0}'', pero se encontró una lista con este nombre."},
			{"Expected Fieldset Got Fieldsets", "En lugar de la única esperada collecíon de casillas ''{0}'' se encontraron varias collecíones de casillas con este nombre."},
			{"Expected Sections Got Field", "Solo se esperaban secciones con el nombre ''{0}'', pero se encontró una casilla con este nombre."},
			{"Expected Fields Got Section", "Solo se esperaban casillas con el nombre ''{0}'', pero se encontró una sección con este nombre."},
			{"Expected Lists Got Section", "Solo se esperaban listas con el nombre ''{0}'', pero se encontró una sección con este nombre."},
			{"Expected List Got Fieldset", "En lugar de la lista ''{0}'' esperada se encontró una collecíon de casillas con este nombre."},
			{"Expected Lists Got Fieldset", "Solo se esperaban listas con el nombre ''{0}'', pero se encontró una collecíon de casillas con este nombre."},
			{"Missing Element", "Falta el elemento ''{0}'' - si se proporcionó, mira por errores ortográficos y también distingue entre mayúsculas y minúsculas."},
			{"Expected Fieldsets Got Section", "Solo se esperaban collecíones de casillas con el nombre ''{0}'', pero se encontró una sección con este nombre."},
			{"Expected List Got Field", "En lugar de la lista ''{0}'' esperada se encontró una casilla con este nombre."},
			{"Expected Section Got Empty", "En lugar de la sección ''{0}'' esperada se encontró un elemento vacío con este nombre."},
			{"Min Count Not Met", "La lista ''{0}'' contiene {1,number} entradas, pero debe contener al menos {2,number} entradas."},
			{"Missing Field Value", "La casilla ''{0}'' debe contener un valor."},
			{"Expected List Got Section", "En lugar de la lista ''{0}'' esperada se encontró una sección con este nombre."},
			{"Expected Field Got List", "En lugar de la casilla ''{0}'' esperada se encontró una lista con este nombre."},
			{"Expected Section Got List", "En lugar de la sección ''{0}'' esperada se encontró una lista con este nombre."},
			{"Expected Field Got Fields", "En lugar de la única casilla esperada ''{0}'' se encontraron varias casillas con este nombre."},
			{"Exact Count Not Met", "La lista ''{0}'' contiene {1,number} entradas, pero debe contener exactamente {2,number} entradas."},
			{"Expected Fieldset Got Field", "En lugar de la collecíon de casillas ''{0}'' esperada se encontró una casilla con este nombre."},
			{"Expected Fieldset Got Section", "En lugar de la collecíon de casillas ''{0}'' esperada se encontró una sección con este nombre."},
			{"Generic Error", "Hay un problema con el valor del elemento ''{0}''."},
			{"Expected Section Got Sections", "En lugar de la única sección esperada ''{0}'' se encontraron varias secciones con este nombre."},
			{"Missing List Item Value", "La lista ''{0}'' no debe contener entradas vacías."},
			{"Missing Field", "Falta la casilla ''{0}'' - si se proporcionó, mira por errores ortográficos y también distingue entre mayúsculas y minúsculas."},
			{"Missing Fieldset Entry Value", "La casilla de collecíon ''{0}'' debe contener un valor."},
			{"Missing Fieldset Entry", "Falta la casilla de collecíon ''{0}'' - si se proporcionó, mira por errores ortográficos y también distingue entre mayúsculas y minúsculas."},
			{"Expected Lists Got Field", "Solo se esperaban listas con el nombre ''{0}'', pero se encontró una casilla con este nombre."},
			{"Expected Fields Got Fieldset", "Solo se esperaban casillas con el nombre ''{0}'', pero se encontró una collecíon de casillas con este nombre."},
			{"Expected Fieldsets Got List", "Solo se esperaban collecíones de casillas con el nombre ''{0}'', pero se encontró una lista con este nombre."},
			{"Excess Name", "Un elemento sobrante con el nombre ''{0}'' se encontró, ¿es posiblemente un error tipográfico?"},
			{"Expected Fieldsets Got Field", "Solo se esperaban collecíones de casillas con el nombre ''{0}'', pero se encontró una casilla con este nombre."},
			{"Expected Element Got Elements", "En lugar del único esperado elemento ''{0}'' se encontraron varios elementos con este nombre."},
			{"Expected Section Got Field", "En lugar de la sección ''{0}'' esperada se encontró una casilla con este nombre."},
			{"Expected Sections Got List", "Solo se esperaban secciones con el nombre ''{0}'', pero se encontró una lista con este nombre."},
			{"Missing Fieldset", "Falta la collecíon de casillas ''{0}'' - si se proporcionó, mira por errores ortográficos y también distingue entre mayúsculas y minúsculas."},
			{"Missing List", "Falta la lista ''{0}'' - si se proporcionó, mira por errores ortográficos y también distingue entre mayúsculas y minúsculas."},
			{"Expected Fieldset Got List", "En lugar de la collecíon de casillas ''{0}'' esperada se encontró una lista con este nombre."},
			{"Expected Sections Got Fieldset", "Solo se esperaban secciones con el nombre ''{0}'', pero se encontró una collecíon de casillas con este nombre."},
			{"Missing Section", "Falta la sección ''{0}'' - si se proporcionó, mira por errores ortográficos y también distingue entre mayúsculas y minúsculas."},
			{"Expected List Got Lists", "En lugar de la única lista esperada ''{0}'' se encontraron varias listas con este nombre."},
			{"Expected Sections Got Empty", "Solo se esperaban secciones con el nombre ''{0}'', pero se encontró un elemento vacío con este nombre."},
			{"Max Count Not Met", "La lista ''{0}'' contiene {1,number} entradas, pero debe contener un máximo de {2,number} entradas."},
			{"Expected Section Got Fieldset", "En lugar de la sección ''{0}'' esperada se encontró una collecíon de casillas con este nombre."},

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

			// Loaders
			{"Invalid Email", "''{0}'' debe contener una valida dirección electrónica, por ejemplo ''jane.doe@eno-lang.org''."},
			{"Invalid Color", "''{0}'' debe contener un color, por ejemplo ''#B6D918'', ''#fff'' o ''#01b''."},
			{"Invalid Float", "''{0}'' debe contener un número decimal, por ejemplo ''13.0'', ''-9.159'' o ''42''."},
			{"Invalid Datetime", "''{0}'' debe contener una valida fecha o fecha y hora, por ejemplo ''1961-01-22'' o ''1989-11-09T19:17Z'' (vea https://www.w3.org/TR/NOTE-datetime)."},
			{"Invalid Url", "''{0}'' debe contener un URL valido, por ejemplo ''https://eno-lang.org''."},
			{"Invalid Integer", "''{0}'' debe contener un número entero, por ejemplo ''42'' o ''-21''."},
			{"Invalid Lat Lng", "''{0}'' debe contener una valida pareja de coordenadas latitud/longitud, por ejemplo ''48.2093723, 16.356099''."},
			{"Invalid Json", "''{0}'' debe contener JSON valido - el mensaje del parser fue ''[ERROR]''."},
			{"Invalid Boolean", "''{0}'' debe contener un valor booleano - valores permitidos son ''true'', ''false'', ''yes'' y ''no''."},
			{"Invalid Date", "''{0}'' debe contener una valida fecha, por ejemplo ''1993-11-18''."},

			// Tokenization
			{"Invalid Line", "Línea {0,integer} no sigue un patrón especificado."},
			{"Unterminated Escaped Name", "En la línea {0,integer}, el nombre de un elemento se escapa, pero esta secuencia de escape no termina hasta el final de la línea."},
			{"Unterminated Block", "El bloque ''{0}'' que comienza en la línea {1,integer} no termina hasta el final del documento."},

			// Elements
			{"Field", "Casilla"},
			{"Empty", "Elemento Vacío"},
			{"Fieldset", "Collecíon de Casillas"},
			{"List Item", "Entrada de Lista"},
			{"Value", "Valor"},
			{"List", "Lista"},
			{"Document", "Documento"},
			{"Fieldset Entry", "Casilla de Collecíon"},
			{"Section", "Sección"},

			// Reporting
			{"Gutter Header", "Línea"},
			{"Content Header", "Contenido"},

			// Resolution
			{"Copying Block Into List", "En la línea {0,integer}, un bloque se copia en una lista."},
			{"Copying Block Into Section", "En la línea {0,integer}, un bloque se copia en una sección."},
			{"Copying Fieldset Into Field", "En la línea {0,integer}, una collecíon de casillas se copia en una casilla."},
			{"Copying List Into Field", "En la línea {0,integer}, una lista se copia en una casilla."},
			{"Copying Field Into List", "En la línea {0,integer} una casilla se copia en una lista."},
			{"Copying Section Into Field", "En la línea {0,integer}, una sección se copia en una casilla."},
			{"Copying Section Into Fieldset", "En la línea {0,integer}, una sección se copia en una collecíon de casillas."},
			{"Cyclic Dependency", "En la línea {0,integer} ''{1}'' se copia en sí mismo."},
			{"Copying Block Into Fieldset", "En la línea {0,integer}, un bloque se copia en una collecíon de casillas."},
			{"Copying Section Into Empty", "En la línea {0,integer} una sección se copia en un elemento vacío."},
			{"Copying Field Into Fieldset", "En la línea {0,integer}, una casilla se copia en una collecíon de casillas."},
			{"Copying Fieldset Into Section", "En la línea {0,integer}, una collecíon de casillas se copia en una sección."},
			{"Copying List Into Fieldset", "En la línea {0,integer}, una lista se copia en una collecíon de casillas."},
			{"Copying Fieldset Into List", "En la línea {0,integer}, una collecíon de casillas se copia en una lista."},
			{"Copying Section Into List", "En la línea {0,integer} una sección se copia en una lista."},
			{"Copying Field Into Section", "En la línea {0,integer}, una casilla se copia en una sección."},
			{"Template Not Found", "En la línea {0,integer} debe ser copiado el elemento ''{1}'', pero no se encontró."},
			{"Copying List Into Section", "En la línea {0,integer}, una lista se copia en una sección."},
			{"Multiple Templates Found", "En la línea {0,integer} no está claro cual de los elementos con el nombre ''{1}'' debe ser copiado."},
			{"IMPROVE","b.e.g. should handle better"}
		};
	}
}


