/* 1 is the package ; 2 is version comment ; 3 is class name
	caller supplies body and closes the three braces */

package org.eno_lang.locale;

/*
	Generated v3.0 at 2018-11-10T20:21:59.830626
	Using eno-locale messages.json v0.8.0 buillt 2018-07-21T07:50:30.125Z
*/

import java.util.ListResourceBundle;

public class Analysis_en extends ListResourceBundle
{
	protected Object[][] getContents()
	{
		return new Object[][] {

			// Analysis
			{"List Item In Field", "Line {0,integer} contains a list item inside a field."},
			{"Fieldset Entry In List", "Line {0,integer} contains a fieldset entry inside a list."},
			{"Missing Element For Continuation", "Line {0,integer} contains a continuation without any continuable element being specified before."},
			{"List Item In Fieldset", "Line {0,integer} contains a list item inside a fieldset."},
			{"Fieldset Entry In Field", "Line {0,integer} contains a fieldset entry inside a field."},
			{"Missing Name For List Item", "Line {0,integer} contains a list item without a name for a list being specified before."},
			{"Duplicate Fieldset Entry Name", "The fieldset ''{0}'' contains two entries named ''{1}''."},
			{"Missing Name For Fieldset Entry", "Line {0,integer} contains a fieldset entry without a name for a fieldset being specified before."},
			{"Section Hierarchy Layer Skip", "Line {0,integer} starts a section that is more than one level deeper than the current one."},
			{"IMPROVE","b.e.g. should handle better"}
		};
	}
}


