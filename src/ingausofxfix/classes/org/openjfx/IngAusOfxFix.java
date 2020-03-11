/*
  * Copyright (C) 2020 Chris Good
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * 18/07/2016 cgood Created.
 */

package org.openjfx;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This project creates a new .ofx file from the file downloaded from
 *  ING Australia bank with the following modifications:
 *  1. add missing BANKACCTFROM xml entity before BANKTRANLIST.
 *      This is needed because GnuCash doesn't find any transactions to import
 *       without this.
 *  2. ensure Financial Institution Transaction ID (FITID) is unique for every
 *      transaction by appending "." + DTPOSTED + "." + TRNAMT to the
 *      input file FITID.
 *      This is because FITID's are supposed to be unique for every transaction
 *      within each bank account and GnuCash will treat transactions with
 *      duplicate FITID's as already imported.
 *
 * Platform: Windows or Linux
 * 
 * 30/04/2016 CRG Created
 * 
 * @author cgood
 */

public class IngAusOfxFix extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("IngAusOfxFix.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("IngAusOfxFix.css").toExternalForm());

        stage.setTitle("ING Australia OFX Fix");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
