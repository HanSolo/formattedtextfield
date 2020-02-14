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
import java.util.Locale;


public interface FieldType {

    StandardType getType();

    String getPattern();
    void setPattern(final String pattern);

    String getUnit();
    String getUnits();

    boolean hasMultipleUnits();

    String getPrompt();

    DecimalFormat getDecimalFormat();

    void setLocale(final Locale locale);

    DecimalFormat getDecimalFormatForLocale(final Locale locale);

}
