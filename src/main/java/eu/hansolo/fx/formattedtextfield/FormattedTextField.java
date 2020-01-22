/*
 * Copyright (c) 2020 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.fx.formattedtextfield;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;


public class FormattedTextField extends TextField {
    public enum Type {
        NONE("#", "", ""),
        PERCENTAGE("0.0", " %", "in Prozent"),
        YEARS("0", " Jahr", "Jahre", "in Jahren"),
        MONTHS("0", " Monat", "Monate", "in Monaten"),
        CURRENCY("0.00", " EUR", "in Euro");

        private       String        pattern;
        private final String        unit;
        private final String        units;
        private final boolean       hasMultipleUnits;
        private final String        prompt;
        private       DecimalFormat decimalFormat;


        Type(final String pattern, final String unit, final String prompt) {
            this(pattern, unit, unit, prompt);
        }
        Type(final String pattern, final String unit, final String units, final String prompt) {
            this.pattern          = pattern;
            this.unit             = unit;
            this.units            = units;
            this.hasMultipleUnits = !unit.equals(units);
            this.prompt           = prompt;
            this.decimalFormat    = new DecimalFormat(pattern, new DecimalFormatSymbols(Locale.getDefault()));
        }


        public String getPattern() {
            return pattern;
        }
        public void setPattern(final String pattern) {
            this.pattern = pattern;
        }

        // Unit for an amount of 1
        public String getUnit() {
            return unit;
        }

        // Unit for an amount larger than 1
        public String getUnits() {
            return units;
        }

        public boolean hasMultipleUnits() {
            return hasMultipleUnits;
        }

        public String getPrompt() {
            return prompt;
        }

        public DecimalFormat getDecimalFormat() {
            return decimalFormat;
        }

        public void setLocale(final Locale locale) {
            decimalFormat = new DecimalFormat(pattern, new DecimalFormatSymbols(locale));
        }

        public DecimalFormat getDecimalFormatForLocale(final Locale locale) {
            setLocale(locale);
            return decimalFormat;
        }
    }

    private final Type                       type;
    private       int                        decimals;
    private       NumberFormat               numberFormat;
    private       DecimalFormat              decimalFormat;
    private       Locale                     locale;
    private       DecimalFormatSymbols       decimalFormatSymbols;
    private       ObjectProperty<BigDecimal> value;
    private       TextFormatter              textFormatter;
    private       Pattern                    pattern;
    private       UnaryOperator<Change>      filter;


    // ******************** Constructors **************************************
    public FormattedTextField(final Type type, final int decimals) {
        this(type, null, decimals, Locale.getDefault());
    }
    public FormattedTextField(final Type type, final int decimals, final Locale locale) {
        this(type, null, decimals, locale);
    }
    public FormattedTextField(final Type type, final double value, final int decimals) {
        this(type, BigDecimal.valueOf(value), decimals, Locale.getDefault());
    }
    public FormattedTextField(final Type type, final float value, final int decimals) {
        this(type, BigDecimal.valueOf(value), decimals, Locale.getDefault());
    }
    public FormattedTextField(final Type type, final long value) {
        this(type, BigDecimal.valueOf(value), 0, Locale.getDefault());
    }
    public FormattedTextField(final Type type, final double value, final int decimals, final Locale locale) {
        this(type, BigDecimal.valueOf(value), decimals, locale);
    }
    public FormattedTextField(final Type type, final float value, final int decimals, final Locale locale) {
        this(type, BigDecimal.valueOf(value), decimals, locale);
    }
    public FormattedTextField(final Type type, final long value, final Locale locale) {
        this(type, BigDecimal.valueOf(value), 0, locale);
    }
    public FormattedTextField(final Type type, final String value) {
        this(type, (null == value || value.isEmpty()) ? null: new BigDecimal(value), 0, Locale.getDefault());
    }
    public FormattedTextField(final Type type, final String value, final Locale locale) {
        this(type, (null == value || value.isEmpty()) ? null: new BigDecimal(value), 0, locale);
    }
    public FormattedTextField(final Type type, final BigDecimal value, final int decimals) {
        this(type, value, decimals, Locale.getDefault());
    }
    public FormattedTextField(final Type type, final BigDecimal value, final int decimals, final Locale locale) {
        super(null == value ? null : ( value.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO.toString() :
                                                                            DecimalFormatSymbols.getInstance(locale).getDecimalSeparator() != '.' ? value.toString().replace(".", Character.toString(DecimalFormatSymbols.getInstance(locale).getDecimalSeparator())) :
                                                                            value.toString()));

        this.locale               = locale;
        this.decimalFormatSymbols = new DecimalFormatSymbols(this.locale);
        this.decimals             = clamp(0, Integer.MAX_VALUE, decimals);

        StringBuilder patternBuilder = new StringBuilder("0");
        if (this.decimals > 0) {
            patternBuilder.append(".");
            for (int i = 0 ; i < this.decimals ; i++) { patternBuilder.append("0"); }
        }

        this.type          = type;
        this.type.setPattern(patternBuilder.toString());
        this.numberFormat  = type.getDecimalFormatForLocale(this.locale);
        this.value         = new ObjectPropertyBase<>() {
            @Override protected void invalidated() {
                if (null == get()) { return; }
                if (get().compareTo(BigDecimal.ZERO) < 0) {
                    set(BigDecimal.ZERO);
                    setText(decimalFormat.format(BigDecimal.ZERO));
                } else {
                    setText(decimalFormat.format(get()));
                }
            }
            @Override public Object getBean() { return FormattedTextField.this; }
            @Override public String getName() { return "FormattedTextField"; }
        };

        if (null == value) {
            this.decimalFormat = new DecimalFormat(this.type.getPattern() + (this.type.getUnit().isEmpty() ? "" : ("'" + this.type.getUnit() + "'")), decimalFormatSymbols);
        } else {
            if (type.hasMultipleUnits() && value.compareTo(BigDecimal.ONE) > 0) {
                this.decimalFormat = new DecimalFormat(this.type.getPattern() + (this.type.getUnits().isEmpty() ? "" : ("' " + this.type.getUnits() + "'")), decimalFormatSymbols);
            } else {
                this.decimalFormat = new DecimalFormat(this.type.getPattern() + (this.type.getUnit().isEmpty() ? "" : ("' " + this.type.getUnit() + "'")), decimalFormatSymbols);
            }
        }

        //this.pattern       = Pattern.compile("\\d*|\\d+\\,\\d{0," + decimals + "}");
        if (decimals > 0) {
            this.pattern = Pattern.compile("\\d*|\\d+" + decimalFormatSymbols.getDecimalSeparator() + "\\d{0," + decimals + "}");
        } else {
            this.pattern = Pattern.compile("\\d*|\\d+\\d{0," + decimals + "}");
        }
        this.filter        = c -> {
            String text = c.getControlNewText();
            if (pattern.matcher(text).matches()) {
                return c;
            } else {
                return null;
            }
        };
        this.textFormatter = new TextFormatter(filter);
        this.setPromptText(type.getPrompt());
        registerListeners();
        if (null != value) { setValue(value); }
    }


    // ******************** Private Methods ***********************************
    private void registerListeners() {
        focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                if (null != value.get()) {
                    if (decimalFormatSymbols.getDecimalSeparator() != '.') {
                        setText(value.get().toString().replace(".", Character.toString(decimalFormatSymbols.getDecimalSeparator())));
                    }
                }
                setTextFormatter(textFormatter);
            } else {
                setTextFormatter(null);
                if (null == getText() || getText().isEmpty()) {
                    value.setValue(null);
                } else {
                    parseAndFormat();
                }
            }
        });
        textProperty().addListener((o, ov, nv) -> {
            if (!isFocused()) {
                if (null != nv && !getType().getUnit().isEmpty()) {
                    if (!nv.contains(getType().getUnit())) {
                        parseAndFormat();
                    }
                }
            }
        });
    }

    private int clamp(final int min, final int max, final int value) {
        if (value < min) { return min; }
        if (value > max) { return max; }
        return value;
    }


    // ******************** Public Methods ************************************
    public final void parseAndFormat() {
        try {
            String text = getText();
            if (text == null || text.isEmpty()) {
                return;
            }
            Number     parsedNumber = numberFormat.parse(text);
            BigDecimal newValue     = new BigDecimal(parsedNumber.toString());

            if (type.hasMultipleUnits() && newValue.compareTo(BigDecimal.ONE) > 0) {
                this.decimalFormat = new DecimalFormat(this.type.getPattern() + (this.type.getUnits().isEmpty() ? "" : ("' " + this.type.getUnits() + "'")), decimalFormatSymbols);
            } else {
                this.decimalFormat = new DecimalFormat(this.type.getPattern() + (this.type.getUnit().isEmpty() ? "" : ("' " + this.type.getUnit() + "'")), decimalFormatSymbols);
            }

            setValue(newValue);
            selectAll();
        } catch (ParseException e) {
            if (null == value.get()) {
                setText("");
            } else {
                setText(decimalFormat.format(value.get()));
            }
        }
    }

    public Type getType() { return type; }

    public String getValueAsText() {
        if (null == value.get()) {
            return null;
        } else if (decimalFormatSymbols.getDecimalSeparator() != '.') {
            return value.get().toString().replace(".", Character.toString(decimalFormatSymbols.getDecimalSeparator()));
        } else {
            return value.get().toString();
        }
    }

    public BigDecimal getValue() {
        return this.value.get();
    }
    public void setValue(final BigDecimal value) {
        this.value.set(value);
    }
    public ObjectProperty<BigDecimal> valueProperty() {
        return this.value;
    }

    public int getDecimals() {
        return decimals;
    }
    public void setDecimals(final int decimals) {
        this.decimals = clamp(0, Integer.MAX_VALUE, decimals);
        StringBuilder patternBuilder = new StringBuilder("0");
        if (decimals > 0) {
            patternBuilder.append(".");
            for (int i = 0 ; i < decimals ; i++) { patternBuilder.append("0"); }
        }
        this.type.setPattern(patternBuilder.toString());
        this.numberFormat  = type.getDecimalFormatForLocale(this.locale);
        if (decimals > 0) {
            this.pattern = Pattern.compile("\\d*|\\d+" + decimalFormatSymbols.getDecimalSeparator() + "\\d{0," + decimals + "}");
        } else {
            this.pattern = Pattern.compile("\\d*|\\d+\\d{0," + decimals + "}");
        }

        this.filter        = c -> {
            String text = c.getControlNewText();
            if (pattern.matcher(text).matches()) {
                return c;
            } else {
                return null;
            }
        };
        this.textFormatter = new TextFormatter(filter);
        parseAndFormat();
    }

    public Locale getLocale() {
        return locale;
    }
    public void setLocale(final Locale locale) {
        this.locale               = locale;
        this.decimalFormatSymbols = new DecimalFormatSymbols(this.locale);
        this.numberFormat         = type.getDecimalFormatForLocale(this.locale);
        parseAndFormat();
    }

    public DecimalFormatSymbols getDecimalFormatSymbols() {
        return decimalFormatSymbols;
    }
}
