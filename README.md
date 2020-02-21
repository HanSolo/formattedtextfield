## Formatted TextField
A JavaFX custom TextField that allows formatting numbers with custom formats and units.

Donations are welcome at [Paypal](https://paypal.me/hans0l0)

## Description
If you need a text field that shows for example the international short form for US dollars behind the value you can create the field as follows:

`new FormattedTextField(new Format(StandardType.DOLLAR), 5, 2, Locale.US, true)`

This field now is allowed to have 5 digits before the decimal separator and 2 digits after the decimal separator.
Because we set the locale to Locale.US you are only allowed to use the "." as decimal separator.
In this field also negative numbers are allowed which is indicated by the last boolean value.

If you need a text field that shows the same but for Euros it could look like follows:

`new FormattedTextField(new Format(StandardType.EURO), 5, 2, Locale.GERMANY, false)`

This field now will show EUR behind the value when not focused and only the "," is allowed as decimal separator
because we choose Locale.GERMANY. In this field you cannot type in negative numbers.

The field also supports the plural form for units. Let's say you would like to have the units be months.
In case you type in 1 the field will show "1 Month" on focus lost.
If you type in 2 the field will now show "2 Months" on focus lost.

There are some standard types defined in the class StandardType which are the following:
* NONE("0", "", "")
* KM("#,###,##0.00", " KM", "in Kilometers")
* PERCENTAGE("0.0", " %", "in percentage")
* YEARS("0", " Year", " Years", "in years")
* MONTHS("0", " Month", " Months", "in months")
* EURO("0.00", " EUR", "in Euro")
* DOLLAR("0.00", " USD", "in Dollar")

Define your own format as follows, lets say you would like to create a format for 
Gallons it could look like this:
`new Format("0.0", "Gallon", "Gallons", "in Gallons", Locale.US)` 

If you now would like to create a FormattedTextField with this Gallon Format it should look as follows:

`new FormattedTextField(new Format("0.0", "Gallon", "Gallons", "in Gallons", Locale.US), 3, 2, Locale.US, false)`

## Overview
![Overview](https://raw.githubusercontent.com/HanSolo/formattedtextfield/master/FormattedTextField.png)
