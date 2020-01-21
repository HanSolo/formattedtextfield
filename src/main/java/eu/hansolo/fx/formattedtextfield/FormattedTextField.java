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
        YEARS("0", " Jahr(e)", "in Jahren"),
        CURRENCY("0.00", " EUR", "in Euro");

        private       String pattern;
        private final String unit;
        private final String prompt;
        private       DecimalFormat decimalFormat;


        Type(final String pattern, final String unit, String prompt) {
            this.pattern       = pattern;
            this.unit          = unit;
            this.prompt        = prompt;
            this.decimalFormat = new DecimalFormat(pattern, new DecimalFormatSymbols(Locale.getDefault()));
        }


        public String getPattern() {
            return pattern;
        }
        public void setPattern(final String pattern) {
            this.pattern = pattern;
        }

        public String getUnit() {
            return unit;
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
    private       ObjectProperty<BigDecimal> value;
    private       TextFormatter              textFormatter;
    private       Pattern                    pattern;
    private       UnaryOperator<Change>      filter;


    // ******************** Constructors **************************************
    public FormattedTextField(final Type type, final int decimals) {
        this(type, BigDecimal.ZERO, decimals, Locale.getDefault());
    }
    public FormattedTextField(final Type type, final int decimals, final Locale locale) {
        this(type, BigDecimal.ZERO, decimals, locale);
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
        this(type, null, 0, Locale.getDefault());
    }
    public FormattedTextField(final Type type, final String value, final Locale locale) {
        this(type, null, 0, locale);
    }
    public FormattedTextField(final Type type, final BigDecimal value, final int decimals) {
        this(type, value, decimals, Locale.getDefault());
    }
    public FormattedTextField(final Type type, final BigDecimal value, final int decimals, final Locale locale) {
        super();
        StringBuilder patternBuilder = new StringBuilder(decimals > 0 ? "0." : "0");
        for (int i = 0 ; i < decimals ; i++) { patternBuilder.append("0"); }

        this.type          = type;
        this.decimals      = clamp(0, Integer.MAX_VALUE, decimals);
        this.type.setPattern(patternBuilder.toString());
        this.locale        = locale;
        this.numberFormat  = type.getDecimalFormatForLocale(this.locale);
        this.decimalFormat = new DecimalFormat(this.type.getPattern() + (this.type.getUnit().isEmpty() ? "" : ("'" + this.type.getUnit() + "'")), new DecimalFormatSymbols(this.locale));
        this.value         = new ObjectPropertyBase<BigDecimal>() {
            @Override protected void invalidated() { setText(decimalFormat.format(get())); }
            @Override public Object getBean() { return FormattedTextField.this; }
            @Override public String getName() { return "FormattedTextField"; }
        };
        this.pattern       = Pattern.compile("\\d*|\\d+\\,\\d{0," + decimals + "}");
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
        setOnAction(e -> parseAndFormat());
        focusedProperty().addListener((o, ov, nv) -> {
            if (nv) {
                if (null != value.get()) {
                    setText(value.get().toString());
                }
                setTextFormatter(textFormatter);
            } else {
                setTextFormatter(null);
                parseAndFormat();
            }
        });
    }

    private void parseAndFormat() {
        try {
            String text = getText();
            if (text == null || text.length() == 0) {
                return;
            }
            Number     parsedNumber = numberFormat.parse(text);
            BigDecimal newValue     = new BigDecimal(parsedNumber.toString());
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

    private int clamp(final int min, final int max, final int value) {
        if (value < min) { return min; }
        if (value > max) { return max; }
        return value;
    }


    // ******************** Public Methods ************************************
    public Type getType() { return type; }

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
        StringBuilder patternBuilder = new StringBuilder(decimals > 0 ? "0." : "0");
        for (int i = 0 ; i < decimals ; i++) { patternBuilder.append("0"); }
        this.decimals      = clamp(0, Integer.MAX_VALUE, decimals);
        this.type.setPattern(patternBuilder.toString());
        this.numberFormat  = type.getDecimalFormatForLocale(this.locale);
        this.decimalFormat = new DecimalFormat(this.type.getPattern() + (this.type.getUnit().isEmpty() ? "" : ("' " + this.type.getUnit() + "'")), new DecimalFormatSymbols(this.locale));
        this.pattern       = Pattern.compile("\\d*|\\d+\\,\\d{0," + decimals + "}");
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
        this.numberFormat  = type.getDecimalFormatForLocale(this.locale);
        this.decimalFormat = new DecimalFormat(this.type.getPattern() + "'" + this.type.getUnit() + "'", new DecimalFormatSymbols(this.locale));
        parseAndFormat();
    }
}
