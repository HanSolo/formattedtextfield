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
    private final Format                     format;
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
    public FormattedTextField(final Format format, final int decimals) {
        this(format, null, decimals, Locale.getDefault());
    }
    public FormattedTextField(final Format format, final int decimals, final Locale locale) {
        this(format, null, decimals, locale);
    }
    public FormattedTextField(final Format format, final double value, final int decimals) {
        this(format, BigDecimal.valueOf(value), decimals, Locale.getDefault());
    }
    public FormattedTextField(final Format format, final float value, final int decimals) {
        this(format, BigDecimal.valueOf(value), decimals, Locale.getDefault());
    }
    public FormattedTextField(final Format format, final long value) {
        this(format, BigDecimal.valueOf(value), 0, Locale.getDefault());
    }
    public FormattedTextField(final Format format, final double value, final int decimals, final Locale locale) {
        this(format, BigDecimal.valueOf(value), decimals, locale);
    }
    public FormattedTextField(final Format format, final float value, final int decimals, final Locale locale) {
        this(format, BigDecimal.valueOf(value), decimals, locale);
    }
    public FormattedTextField(final Format format, final long value, final Locale locale) {
        this(format, BigDecimal.valueOf(value), 0, locale);
    }
    public FormattedTextField(final Format format, final String value) {
        this(format, (null == value || value.isEmpty()) ? null : new BigDecimal(value), 0, Locale.getDefault());
    }
    public FormattedTextField(final Format format, final String value, final Locale locale) {
        this(format, (null == value || value.isEmpty()) ? null : new BigDecimal(value), 0, locale);
    }
    public FormattedTextField(final Format format, final BigDecimal value, final int decimals) {
        this(format, value, decimals, Locale.getDefault());
    }
    public FormattedTextField(final Format format, final BigDecimal value, final int decimals, final Locale locale) {
        super(null == value ? null : ( value.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO.toString() :
                                                                            DecimalFormatSymbols.getInstance(locale).getDecimalSeparator() != '.' ? value.toString().replace(".", Character.toString(DecimalFormatSymbols.getInstance(locale).getDecimalSeparator())) :
                                                                            value.toString()));

        this.locale               = locale;
        this.decimalFormatSymbols = new DecimalFormatSymbols(this.locale);
        this.decimals             = clamp(0, Integer.MAX_VALUE, decimals);
        this.decimalFormat        = new DecimalFormat();
        this.decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);

        StringBuilder patternBuilder = new StringBuilder(StandardType.KM == format.getType() ? "#,###,##0" : "0");

        if (this.decimals > 0) {
            patternBuilder.append(".");
            for (int i = 0 ; i < this.decimals ; i++) { patternBuilder.append("0"); }
        }

        this.format = format;
        this.format.setPattern(patternBuilder.toString());
        this.numberFormat  = format.getDecimalFormatForLocale(this.locale);
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
            this.decimalFormat.applyPattern(this.format.getPattern() + (this.format.getUnit().isEmpty() ? "" : ("'" + this.format.getUnit() + "'")));
        } else {
            if (format.hasMultipleUnits() && value.compareTo(BigDecimal.ONE) > 0) {
                this.decimalFormat.applyPattern(this.format.getPattern() + (this.format.getUnits().isEmpty() ? "" : ("' " + this.format.getUnits() + "'")));
            } else {
                this.decimalFormat.applyPattern(this.format.getPattern() + (this.format.getUnit().isEmpty() ? "" : ("' " + this.format.getUnit() + "'")));
            }
        }

        //this.pattern       = Pattern.compile("\\d*|\\d+\\,\\d{0," + decimals + "}");
        if (this.decimals > 0) {
            patternBuilder = new StringBuilder().append("\\d*|\\d+").append(decimalFormatSymbols.getDecimalSeparator()).append("\\d{0,").append(this.decimals).append("}");
            this.pattern = Pattern.compile(patternBuilder.toString());
        } else {
            patternBuilder = new StringBuilder().append("\\d*|\\d+\\d{0,").append(this.decimals).append("}");
            this.pattern = Pattern.compile(patternBuilder.toString());
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
        this.setPromptText(format.getPrompt());
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
                if (null != nv && !getFormat().getUnit().isEmpty()) {
                    if (!nv.contains(getFormat().getUnit())) {
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

            this.decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);

            if (format.hasMultipleUnits() && newValue.compareTo(BigDecimal.ONE) > 0) {
                this.decimalFormat.applyPattern(this.format.getPattern() + (this.format.getUnits().isEmpty() ? "" : ("' " + this.format.getUnits() + "'")));
            } else {
                this.decimalFormat.applyPattern(this.format.getPattern() + (this.format.getUnit().isEmpty() ? "" : ("' " + this.format.getUnit() + "'")));
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

    public Format getFormat() { return format; }

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
        StringBuilder patternBuilder = new StringBuilder(StandardType.KM == format.getType() ? "#,###,##0" : "0");
        if (decimals > 0) {
            patternBuilder.append(".");
            for (int i = 0 ; i < decimals ; i++) { patternBuilder.append("0"); }
        }
        this.format.setPattern(patternBuilder.toString());
        this.numberFormat  = format.getDecimalFormatForLocale(this.locale);

        if (this.decimals > 0) {
            patternBuilder = new StringBuilder().append("\\d*|\\d+").append(decimalFormatSymbols.getDecimalSeparator()).append("\\d{0,").append(this.decimals).append("}");
            this.pattern = Pattern.compile(patternBuilder.toString());
        } else {
            patternBuilder = new StringBuilder().append("\\d*|\\d+\\d{0,").append(this.decimals).append("}");
            this.pattern = Pattern.compile(patternBuilder.toString());
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
        this.numberFormat         = format.getDecimalFormatForLocale(this.locale);
        parseAndFormat();
    }

    public DecimalFormatSymbols getDecimalFormatSymbols() {
        return decimalFormatSymbols;
    }
}
