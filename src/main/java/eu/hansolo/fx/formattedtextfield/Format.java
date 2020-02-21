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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;


public class Format {
    private StandardType  type;
    private String        pattern;
    private String        unit;
    private String        units;
    private String        prompt;
    private boolean       hasMultipleUnits;
    private DecimalFormat decimalFormat;


    // ******************** Constructors **************************************
    public Format() {
        this(StandardType.NONE, "0", "", "", "", Locale.getDefault());
    }
    public Format(final StandardType type) {
        this(type, type.getPattern(), type.getUnit(), type.getUnits(), type.getPrompt(), Locale.getDefault());
    }
    public Format(final StandardType type, final Locale locale) {
        this(type, type.getPattern(), type.getUnit(), type.getUnits(), type.getPrompt(), locale);
    }
    public Format(final String pattern, final String unit, final String prompt) {
        this(StandardType.NONE, pattern, unit, unit, prompt, Locale.getDefault());
    }
    public Format(final String pattern, final String unit, final String prompt, final Locale locale) {
        this(StandardType.NONE, pattern, unit, unit, prompt, locale);
    }
    public Format(final String pattern, final String unit, final String units, final String prompt, final Locale locale) {
        this(StandardType.NONE, pattern, unit, units, prompt, locale);
    }
    public Format(final StandardType type, final String pattern, final String unit, final String units, final String prompt, final Locale locale) {
        this.type             = type;
        this.pattern          = pattern;
        this.unit             = unit;
        this.units            = units;
        this.prompt           = prompt;
        this.hasMultipleUnits = !unit.equals(units);
        this.decimalFormat    = new DecimalFormat(pattern, new DecimalFormatSymbols(locale));
    }


    // ******************** Public Methods ************************************
    public StandardType getType() {
        return type;
    }

    public String getPattern() {
        return this.pattern;
    }
    public void setPattern(final String pattern) {
        this.pattern = pattern;
        decimalFormat.applyPattern(pattern);
    }

    public String getUnit() {
        return unit;
    }

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
        decimalFormat.setDecimalFormatSymbols(new DecimalFormatSymbols(locale));
    }

    public DecimalFormat getDecimalFormatForLocale(final Locale locale) {
        setLocale(locale);
        return decimalFormat;
    }
}
