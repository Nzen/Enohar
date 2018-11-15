/* 1 is the package ; 2 is version comment ; 3 is class name
	caller supplies body and closes the three braces */

package org.eno_lang.locale;

/*
	Generated v3.0 at 2018-11-10T20:21:59.830626
	Using eno-locale messages.json v0.8.0 buillt 2018-07-21T07:50:30.125Z
*/

import java.util.ListResourceBundle;

public class Loaders_en extends ListResourceBundle
{
	protected Object[][] getContents()
	{
		return new Object[][] {

			// Loaders
			{"Invalid Email", "''{0}'' must contain a valid email address, for instance ''jane.doe@eno-lang.org''."},
			{"Invalid Color", "''{0}'' must contain a color, for instance ''#B6D918'', ''#fff'' or ''#01b''."},
			{"Invalid Float", "''{0}'' must contain a decimal number, for instance ''13.0'', ''-9.159'' or ''42''."},
			{"Invalid Datetime", "''{0}'' must contain a valid date or date and time, for instance ''1961-01-22'' or ''1989-11-09T19:17Z'' (see https://www.w3.org/TR/NOTE-datetime)."},
			{"Invalid Url", "''{0}'' must contain a valid URL, for instance ''https://eno-lang.org''."},
			{"Invalid Integer", "''{0}'' must contain an integer, for instance ''42'' or ''-21''."},
			{"Invalid Lat Lng", "''{0}'' must contain a valid latitude/longitude coordinate pair, for instance ''48.2093723, 16.356099''."},
			{"Invalid Json", "''{0}'' must contain valid JSON - the parser returned: ''[ERROR]''."},
			{"Invalid Boolean", "''{0}'' must contain a boolean - allowed values are ''true'', ''false'', ''yes'' and ''no''."},
			{"Invalid Date", "''{0}'' must contain a valid date, for instance ''1993-11-18''."},
			{"IMPROVE","b.e.g. should handle better"}
		};
	}
}


