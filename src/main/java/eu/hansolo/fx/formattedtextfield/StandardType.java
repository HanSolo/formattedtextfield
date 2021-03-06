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


public enum StandardType {
    NONE("0", "", ""),
    KM("#,###,##0.00", "KM", "in Kilometers"),
    PERCENTAGE("0.0", "%", "in percentage"),
    YEARS("0", "Year", " Years", "in years"),
    MONTHS("0", "Month", " Months", "in months"),
    EURO("#,###,##0.00", "EUR", "in Euro"),
    DOLLAR("#,###,##0.00", "USD", "in Dollar"),
    MM("0", "mm", "in mm"),
    F("0.0", "f", "in f-stops");

    private final String pattern;
    private final String unit;
    private final String units;
    private final String prompt;


    // ******************** Constructors **************************************
    StandardType(final String pattern, final String unit, final String prompt) {
        this(pattern, unit, unit, prompt);
    }
    StandardType(final String pattern, final String unit, final String units, final String prompt) {
        this.pattern = pattern;
        this.unit    = unit;
        this.units   = units;
        this.prompt  = prompt;
    }


    // ******************** Public Methods ************************************
    public String getPattern() {
        return pattern;
    }

    public String getUnit() {
        return unit;
    }

    public String getUnits() {
        return units;
    }

    public String getPrompt() {
        return prompt;
    }
}
