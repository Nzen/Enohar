/* 1 is the package ; 2 is version comment ; 3 is class name
	caller supplies body and closes the three braces */

package org.eno_lang.locale;

/*
	Generated v3.0 at 2018-11-10T20:21:59.830626
	Using eno-locale messages.json v0.8.0 buillt 2018-07-21T07:50:30.125Z
*/

import java.util.ListResourceBundle;

public class Loaders_es extends ListResourceBundle
{
	protected Object[][] getContents()
	{
		return new Object[][] {

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
			{"IMPROVE","b.e.g. should handle better"}
		};
	}
}


