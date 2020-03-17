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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;


public class FormattedTextFieldBuilder<B extends FormattedTextFieldBuilder<B>> {
    private final StandardType              type;
    private       HashMap<String, Property> properties = new HashMap<>();


    // ******************** Constructors **************************************
    protected FormattedTextFieldBuilder(final StandardType type) {
        this.type = type;
    }


    // ******************** Public Methods ************************************
    public static final FormattedTextFieldBuilder create(final StandardType type) {
        return new FormattedTextFieldBuilder(type);
    }

    public final B value(final float value) {
        properties.put("value", new SimpleObjectProperty<>(BigDecimal.valueOf(value)));
        return (B)this;
    }
    public final B value(final double value) {
        properties.put("value", new SimpleObjectProperty<>(BigDecimal.valueOf(value)));
        return (B)this;
    }
    public final B value(final int value) {
        properties.put("value", new SimpleObjectProperty<>(BigDecimal.valueOf(value)));
        return (B)this;
    }
    public final B value(final long value) {
        properties.put("value", new SimpleObjectProperty<>(BigDecimal.valueOf(value)));
        return (B)this;
    }
    public final B value(final BigDecimal value) {
        properties.put("value", new SimpleObjectProperty<>(value));
        return (B)this;
    }

    public final B preDecimals(final int preDecimals) {
        properties.put("preDecimals", new SimpleIntegerProperty(preDecimals));
        return (B)this;
    }

    public final B decimals(final int decimals) {
        properties.put("decimals", new SimpleIntegerProperty(decimals));
        return (B)this;
    }

    public final B locale(final Locale locale) {
        properties.put("locale", new SimpleObjectProperty<>(locale));
        return (B)this;
    }

    public final B promptText(final String promptText) {
        properties.put("promptText", new SimpleStringProperty(promptText));
        return (B)this;
    }

    public final B negativeNumbersAllowed(final boolean allowed) {
        properties.put("negativeNumbersAllowed", new SimpleBooleanProperty(allowed));
        return (B)this;
    }

    public final B unitPosition(final UnitPos unitPosition) {
        properties.put("unitPosition", new SimpleObjectProperty(unitPosition));
        return (B)this;
    }


    public final FormattedTextField build() {
        final FormattedTextField formattedTextField = new FormattedTextField(new Format(type), 0);
        for (String key : properties.keySet()) {
            if("value".equals(key)) {
                formattedTextField.setValue(((ObjectProperty<BigDecimal>) properties.get(key)).get());
            } else if ("preDecimals".equals(key)) {
                formattedTextField.setPreDecimals(((IntegerProperty) properties.get(key)).get());
            } else if("decimals".equals(key)) {
                formattedTextField.setDecimals(((IntegerProperty) properties.get(key)).get());
            } else if("locale".equals(key)) {
                formattedTextField.setLocale(((ObjectProperty<Locale>) properties.get(key)).get());
            } else if ("promptText".equals(key)) {
                formattedTextField.setPromptText(((StringProperty) properties.get(key)).get());
            } else if ("negativeNumbersAllowed".equals(key)) {
                formattedTextField.setNegativeNumbersAllowed(((BooleanProperty) properties.get(key)).get());
            } else if ("unitPosition".equals(key)) {
                formattedTextField.setUnitPosition(((ObjectProperty<UnitPos>) properties.get(key)).get());
            }
        }
        return formattedTextField;
    }
}
