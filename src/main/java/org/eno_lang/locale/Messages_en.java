/* 1 is the package ; 2 is version comment ; 3 is class name
	caller supplies body and closes the three braces */

package org.eno_lang.locale;

/*
	Generated v3.0 at 2018-11-10T17:03:37.373376
	Using eno-locale messages.json v0.8.0 buillt 2018-07-21T07:50:30.125Z
*/

import java.util.ListResourceBundle;

public class Messages_en extends ListResourceBundle
{
	protected Object[][] getContents()
	{
		return new Object[][] {

			// Validation
			{"Expected Field Got Fieldset", "Instead of the expected field ''{0}'' a fieldset with this name was found."},
			{"Expected Field Got Section", "Instead of the expected field ''{0}'' a section with this name was found."},
			{"Expected Fields Got List", "Only fields with the name ''{0}'' were expected, but a list with this name was found."},
			{"Expected Fieldset Got Fieldsets", "Instead of the expected single fieldset ''{0}'' several fieldsets with this name were found."},
			{"Expected Sections Got Field", "Only sections with the name ''{0}'' were expected, but a field with this name was found."},
			{"Expected Fields Got Section", "Only fields with the name ''{0}'' were expected, but a section with this name was found."},
			{"Expected Lists Got Section", "Only lists with the name ''{0}'' were expected, but a section with this name was found."},
			{"Expected List Got Fieldset", "Instead of the expected list ''{0}'' a fieldset with this name was found."},
			{"Expected Lists Got Fieldset", "Only lists with the name ''{0}'' were expected, but a fieldset with this name was found."},
			{"Missing Element", "The element ''{0}'' is missing - in case it has been specified look for typos and also check for correct capitalization."},
			{"Expected Fieldsets Got Section", "Only fieldsets with the name ''{0}'' were expected, but a section with this name was found."},
			{"Expected List Got Field", "Instead of the expected list ''{0}'' a field with this name was found."},
			{"Expected Section Got Empty", "Instead of the expected section ''{0}'' an empty element with this name was found."},
			{"Min Count Not Met", "The list ''{0}'' contains {1,number} items, but must contain at least {2,number} items."},
			{"Missing Field Value", "The field ''{0}'' must contain a value."},
			{"Expected List Got Section", "Instead of the expected list ''{0}'' a section with this name was found."},
			{"Expected Field Got List", "Instead of the expected field ''{0}'' a list with this name was found."},
			{"Expected Section Got List", "Instead of the expected section ''{0}'' a list with this name was found."},
			{"Expected Field Got Fields", "Instead of the expected single field ''{0}'' several fields with this name were found."},
			{"Exact Count Not Met", "The list ''{0}'' contains {1,number} items, but must contain exactly {2,number} items."},
			{"Expected Fieldset Got Field", "Instead of the expected fieldset ''{0}'' a field with this name was found."},
			{"Expected Fieldset Got Section", "Instead of the expected fieldset ''{0}'' a section with this name was found."},
			{"Generic Error", "There is a problem with the value of the element ''{0}''."},
			{"Expected Section Got Sections", "Instead of the expected single section ''{0}'' several sections with this name were found."},
			{"Missing List Item Value", "The list ''{0}'' may not contain empty items."},
			{"Missing Field", "The field ''{0}'' is missing - in case it has been specified look for typos and also check for correct capitalization."},
			{"Missing Fieldset Entry Value", "The fieldset entry ''{0}'' must contain a value."},
			{"Missing Fieldset Entry", "The fieldset entry ''{0}'' is missing - in case it has been specified look for typos and also check for correct capitalization."},
			{"Expected Lists Got Field", "Only lists with the name ''{0}'' were expected, but a field with this name was found."},
			{"Expected Fields Got Fieldset", "Only fields with the name ''{0}'' were expected, but a fieldset with this name was found."},
			{"Expected Fieldsets Got List", "Only fieldsets with the name ''{0}'' were expected, but a list with this name was found."},
			{"Excess Name", "An excess element named ''{0}'' was found, is it possibly a typo?"},
			{"Expected Fieldsets Got Field", "Only fieldsets with the name ''{0}'' were expected, but a field with this name was found."},
			{"Expected Element Got Elements", "Instead of the expected single element ''{0}'' several elements with this name were found."},
			{"Expected Section Got Field", "Instead of the expected section ''{0}'' a field with this name was found."},
			{"Expected Sections Got List", "Only sections with the name ''{0}'' were expected, but a list with this name was found."},
			{"Missing Fieldset", "The fieldset ''{0}'' is missing - in case it has been specified look for typos and also check for correct capitalization."},
			{"Missing List", "The list ''{0}'' is missing - in case it has been specified look for typos and also check for correct capitalization."},
			{"Expected Fieldset Got List", "Instead of the expected fieldset ''{0}'' a list with this name was found."},
			{"Expected Sections Got Fieldset", "Only sections with the name ''{0}'' were expected, but a fieldset with this name was found."},
			{"Missing Section", "The section ''{0}'' is missing - in case it has been specified look for typos and also check for correct capitalization."},
			{"Expected List Got Lists", "Instead of the expected single list ''{0}'' several lists with this name were found."},
			{"Expected Sections Got Empty", "Only sections with the name ''{0}'' were expected, but an empty element with this name was found."},
			{"Max Count Not Met", "The list ''{0}'' contains {1,number} items, but may only contain a maximum of {2,number} items."},
			{"Expected Section Got Fieldset", "Instead of the expected section ''{0}'' a fieldset with this name was found."},

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

			// Tokenization
			{"Invalid Line", "Line {0,integer} does not follow any specified pattern."},
			{"Unterminated Escaped Name", "In line {0,integer} the name of an element is escaped, but the escape sequence is not terminated until the end of the line."},
			{"Unterminated Block", "The block ''{0}'' starting in line {1,integer} is not terminated until the end of the document."},

			// Elements
			{"Field", "Field"},
			{"Empty", "Empty Element"},
			{"Fieldset", "Fieldset"},
			{"List Item", "List Item"},
			{"Value", "Value"},
			{"List", "List"},
			{"Document", "Document"},
			{"Fieldset Entry", "Fieldset Entry"},
			{"Section", "Section"},

			// Reporting
			{"Gutter Header", "Line"},
			{"Content Header", "Content"},

			// Resolution
			{"Copying Block Into List", "In line {0,integer} a block is copied into a list."},
			{"Copying Block Into Section", "In line {0,integer} a block is copied into a section."},
			{"Copying Fieldset Into Field", "In line {0,integer} a fieldset is copied into a field."},
			{"Copying List Into Field", "In line {0,integer} a list is copied into a field."},
			{"Copying Field Into List", "In line {0,integer} a field is copied into a list."},
			{"Copying Section Into Field", "In line {0,integer} a section is copied into a field."},
			{"Copying Section Into Fieldset", "In line {0,integer} a section is copied into a fieldset."},
			{"Cyclic Dependency", "In line {0,integer} ''{1}'' is copied into itself."},
			{"Copying Block Into Fieldset", "In line {0,integer} a block is copied into a fieldset."},
			{"Copying Section Into Empty", "In line {0,integer} a section is copied into an empty element."},
			{"Copying Field Into Fieldset", "In line {0,integer} a field is copied into a fieldset."},
			{"Copying Fieldset Into Section", "In line {0,integer} a fieldset is copied into a section."},
			{"Copying List Into Fieldset", "In line {0,integer} a list is copied into a fieldset."},
			{"Copying Fieldset Into List", "In line {0,integer} a fieldset is copied into a list."},
			{"Copying Section Into List", "In line {0,integer} a section is copied into a list."},
			{"Copying Field Into Section", "In line {0,integer} a field is copied into a section."},
			{"Template Not Found", "In line {0,integer} the element ''{1}'' should be copied, but it was not found."},
			{"Copying List Into Section", "In line {0,integer} a list is copied into a section."},
			{"Multiple Templates Found", "In line {0,integer} it is not clear which of the elements named ''{1}'' should be copied."},
			{"IMPROVE","b.e.g. should handle better"}
		};
	}
}


