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


public class Demo extends Application {
    private FormattedTextField currencyTextField;
    private FormattedTextField percentageTextField;
    private FormattedTextField yearTextField;


    @Override public void init() {
        currencyTextField   = new FormattedTextField(FormattedTextField.Type.CURRENCY, 2);
        percentageTextField = new FormattedTextField(FormattedTextField.Type.PERCENTAGE, BigDecimal.valueOf(20), 1);
        yearTextField       = new FormattedTextField(FormattedTextField.Type.YEARS, "");

        //yearTextField       = FormattedTextFieldBuilder.create(Type.YEARS).promptText("in years").build();

    }

    @Override public void start(Stage stage) {
        VBox fieldBox = new VBox(10, currencyTextField, percentageTextField, yearTextField);

        StackPane pane = new StackPane(fieldBox);
        pane.setPadding(new Insets(10));

        Scene scene = new Scene(pane);

        stage.setTitle("FormattedTextField");
        stage.setScene(scene);
        stage.show();

        currencyTextField.setValue(new BigDecimal(500));

        //percentageTextField.setDecimals(5);
    }

    @Override public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
