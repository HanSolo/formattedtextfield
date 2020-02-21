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

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Locale;


public class Demo extends Application {
    private FormattedTextField currencyTextField;
    private FormattedTextField percentageTextField;
    private FormattedTextField monthTextField;
    private FormattedTextField yearTextField;
    private FormattedTextField kmTextField;
    private FormattedTextField noneTextField;
    private FormattedTextField testTextField;
    private FormattedTextField gallonTextField;


    @Override public void init() throws ParseException {
        currencyTextField   = new FormattedTextField(new Format(StandardType.EURO), 2, Locale.GERMANY);
        percentageTextField = new FormattedTextField(new Format(StandardType.PERCENTAGE), BigDecimal.valueOf(20), 1);
        monthTextField      = new FormattedTextField(new Format(StandardType.MONTHS), "3");
        yearTextField       = new FormattedTextField(new Format(StandardType.YEARS), 0);
        kmTextField         = new FormattedTextField(new Format(StandardType.KM), 2);
        noneTextField       = new FormattedTextField(new Format(StandardType.NONE), null);
        testTextField       = new FormattedTextField(new Format(StandardType.DOLLAR), 5, 2, Locale.US, true);
        gallonTextField     = new FormattedTextField(new Format("0.0", "Gallon", "Gallons", "in Gallons", Locale.US), 3, 2, Locale.US, false);
    }

    @Override public void start(Stage stage) {
        VBox fieldBox = new VBox(10, currencyTextField, percentageTextField, monthTextField, yearTextField, kmTextField, noneTextField, testTextField, gallonTextField);

        StackPane pane = new StackPane(fieldBox);
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(pane);

        stage.setTitle("FormattedTextField");
        stage.setScene(scene);
        stage.show();

        //currencyTextField.setValue(new BigDecimal(500));

        percentageTextField.setDecimals(5);
        //yearTextField.setText("0");
        yearTextField.setDecimals(3);

        testTextField.setValue(new BigDecimal("123.45"));
    }

    @Override public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
