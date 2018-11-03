/* 1 is the package ; 2 is class name ; 3 is the class body */
package org.eno_lang.locale;


public class EnoAlias
{
		public static final String Analysis0 = "The fieldset '[FIELDSET_NAME]' contains two entries named '[ENTRY_NAME]'.";
		public static final String Analysis1 = "Line [LINE] contains a fieldset entry inside a field.";
		public static final String Analysis2 = "Line [LINE] contains a fieldset entry inside a list.";
		public static final String Analysis3 = "Line [LINE] contains a list item inside a field.";
		public static final String Analysis4 = "Line [LINE] contains a list item inside a fieldset.";
		public static final String Analysis5 = "Line [LINE] contains a continuation without any continuable element being specified before.";

		public static final String Analysis6 = "Line [LINE] contains a fieldset entry without a name for a fieldset being specified before.";
		public static final String Analysis7 = "Line [LINE] contains a list item without a name for a list being specified before.";
		public static final String Analysis8 = "Line [LINE] starts a section that is more than one level deeper than the current one.";
		public static final String Elements0 = "Document";
		public static final String Elements1 = "Empty Element";
		public static final String Elements2 = "Field";

		public static final String Elements3 = "Fieldset";
		public static final String Elements4 = "Fieldset Entry";
		public static final String Elements5 = "List";
		public static final String Elements6 = "List Item";
		public static final String Elements7 = "Section";
		public static final String Elements8 = "Value";

		public static final String Loaders0 = "'[NAME]' must contain a boolean - allowed values are 'true', 'false', 'yes' and 'no'.";
		public static final String Loaders1 = "'[NAME]' must contain a color, for instance '#B6D918', '#fff' or '#01b'.";
		public static final String Loaders2 = "'[NAME]' must contain a valid date, for instance '1993-11-18'.";
		public static final String Loaders3 = "'[NAME]' must contain a valid date or date and time, for instance '1961-01-22' or '1989-11-09T19:17Z' (see https://www.w3.org/TR/NOTE-datetime).";
		public static final String Loaders4 = "'[NAME]' must contain a valid email address, for instance 'jane.doe@eno-lang.org'.";
		public static final String Loaders5 = "'[NAME]' must contain a decimal number, for instance '13.0', '-9.159' or '42'.";

		public static final String Loaders6 = "'[NAME]' must contain an integer, for instance '42' or '-21'.";
		public static final String Loaders7 = "'[NAME]' must contain valid JSON - the parser returned: '[ERROR]'.";
		public static final String Loaders8 = "'[NAME]' must contain a valid latitude/longitude coordinate pair, for instance '48.2093723, 16.356099'.";
		public static final String Loaders9 = "'[NAME]' must contain a valid URL, for instance 'https://eno-lang.org'.";
		public static final String Reporting0 = "Content";
		public static final String Reporting1 = "Line";

		public static final String Resolution0 = "In line [LINE] a block is copied into a fieldset.";
		public static final String Resolution1 = "In line [LINE] a block is copied into a list.";
		public static final String Resolution10 = "In line [LINE] a list is copied into a fieldset.";
		public static final String Resolution11 = "In line [LINE] a list is copied into a section.";
		public static final String Resolution12 = "In line [LINE] a section is copied into an empty element.";
		public static final String Resolution13 = "In line [LINE] a section is copied into a field.";

		public static final String Resolution14 = "In line [LINE] a section is copied into a fieldset.";
		public static final String Resolution15 = "In line [LINE] a section is copied into a list.";
		public static final String Resolution16 = "In line [LINE] '[NAME]' is copied into itself.";
		public static final String Resolution17 = "In line [LINE] it is not clear which of the elements named '[NAME]' should be copied.";
		public static final String Resolution18 = "In line [LINE] the element '[NAME]' should be copied, but it was not found.";
		public static final String Resolution2 = "In line [LINE] a block is copied into a section.";

		public static final String Resolution3 = "In line [LINE] a field is copied into a fieldset.";
		public static final String Resolution4 = "In line [LINE] a field is copied into a list.";
		public static final String Resolution5 = "In line [LINE] a field is copied into a section.";
		public static final String Resolution6 = "In line [LINE] a fieldset is copied into a field.";
		public static final String Resolution7 = "In line [LINE] a fieldset is copied into a list.";
		public static final String Resolution8 = "In line [LINE] a fieldset is copied into a section.";

		public static final String Resolution9 = "In line [LINE] a list is copied into a field.";
		public static final String Tokenization0 = "Line [LINE] does not follow any specified pattern.";
		public static final String Tokenization1 = "The block '[NAME]' starting in line [LINE] is not terminated until the end of the document.";
		public static final String Tokenization2 = "In line [LINE] the name of an element is escaped, but the escape sequence is not terminated until the end of the line.";
		public static final String Validation0 = "The list '[NAME]' contains [ACTUAL] items, but must contain exactly [EXPECTED] items.";
		public static final String Validation1 = "An excess element named '[NAME]' was found, is it possibly a typo?";

		public static final String Validation10 = "Instead of the expected fieldset '[NAME]' a field with this name was found.";
		public static final String Validation11 = "Instead of the expected single fieldset '[NAME]' several fieldsets with this name were found.";
		public static final String Validation12 = "Instead of the expected fieldset '[NAME]' a list with this name was found.";
		public static final String Validation13 = "Instead of the expected fieldset '[NAME]' a section with this name was found.";
		public static final String Validation14 = "Only fieldsets with the name '[NAME]' were expected, but a field with this name was found.";
		public static final String Validation15 = "Only fieldsets with the name '[NAME]' were expected, but a list with this name was found.";

		public static final String Validation16 = "Only fieldsets with the name '[NAME]' were expected, but a section with this name was found.";
		public static final String Validation17 = "Instead of the expected list '[NAME]' a field with this name was found.";
		public static final String Validation18 = "Instead of the expected list '[NAME]' a fieldset with this name was found.";
		public static final String Validation19 = "Instead of the expected single list '[NAME]' several lists with this name were found.";
		public static final String Validation2 = "Instead of the expected single element '[NAME]' several elements with this name were found.";
		public static final String Validation20 = "Instead of the expected list '[NAME]' a section with this name was found.";

		public static final String Validation21 = "Only lists with the name '[NAME]' were expected, but a field with this name was found.";
		public static final String Validation22 = "Only lists with the name '[NAME]' were expected, but a fieldset with this name was found.";
		public static final String Validation23 = "Only lists with the name '[NAME]' were expected, but a section with this name was found.";
		public static final String Validation24 = "Instead of the expected section '[NAME]' an empty element with this name was found.";
		public static final String Validation25 = "Instead of the expected section '[NAME]' a field with this name was found.";
		public static final String Validation26 = "Instead of the expected section '[NAME]' a fieldset with this name was found.";

		public static final String Validation27 = "Instead of the expected section '[NAME]' a list with this name was found.";
		public static final String Validation28 = "Instead of the expected single section '[NAME]' several sections with this name were found.";
		public static final String Validation29 = "Only sections with the name '[NAME]' were expected, but an empty element with this name was found.";
		public static final String Validation3 = "Instead of the expected single field '[NAME]' several fields with this name were found.";
		public static final String Validation30 = "Only sections with the name '[NAME]' were expected, but a field with this name was found.";
		public static final String Validation31 = "Only sections with the name '[NAME]' were expected, but a fieldset with this name was found.";

		public static final String Validation32 = "Only sections with the name '[NAME]' were expected, but a list with this name was found.";
		public static final String Validation33 = "There is a problem with the value of the element '[NAME]'.";
		public static final String Validation34 = "The list '[NAME]' contains [ACTUAL] items, but may only contain a maximum of [MAXIMUM] items.";
		public static final String Validation35 = "The list '[NAME]' contains [ACTUAL] items, but must contain at least [MINIMUM] items.";
		public static final String Validation36 = "The element '[NAME]' is missing - in case it has been specified look for typos and also check for correct capitalization.";
		public static final String Validation37 = "The field '[NAME]' is missing - in case it has been specified look for typos and also check for correct capitalization.";

		public static final String Validation38 = "The field '[NAME]' must contain a value.";
		public static final String Validation39 = "The fieldset '[NAME]' is missing - in case it has been specified look for typos and also check for correct capitalization.";
		public static final String Validation4 = "Instead of the expected field '[NAME]' a fieldset with this name was found.";
		public static final String Validation40 = "The fieldset entry '[NAME]' is missing - in case it has been specified look for typos and also check for correct capitalization.";
		public static final String Validation41 = "The fieldset entry '[NAME]' must contain a value.";
		public static final String Validation42 = "The list '[NAME]' is missing - in case it has been specified look for typos and also check for correct capitalization.";

		public static final String Validation43 = "The list '[NAME]' may not contain empty items.";
		public static final String Validation44 = "The section '[NAME]' is missing - in case it has been specified look for typos and also check for correct capitalization.";
		public static final String Validation5 = "Instead of the expected field '[NAME]' a list with this name was found.";
		public static final String Validation6 = "Instead of the expected field '[NAME]' a section with this name was found.";
		public static final String Validation7 = "Only fields with the name '[NAME]' were expected, but a fieldset with this name was found.";
		public static final String Validation8 = "Only fields with the name '[NAME]' were expected, but a list with this name was found.";

		public static final String Validation9 = "Only fields with the name '[NAME]' were expected, but a section with this name was found.";

}

