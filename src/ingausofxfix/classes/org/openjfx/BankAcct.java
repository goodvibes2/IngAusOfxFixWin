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

package org.openjfx;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author cgood
 */
public class BankAcct {

    /// instance variables
    private final SimpleStringProperty bankAcctName;
    private final SimpleStringProperty bankAcctBankId;
    private final SimpleStringProperty bankAcctNo;
    private final SimpleStringProperty bankAcctType;
    private final SimpleStringProperty bankAcctOfxDir; // directory holding OFX files
    private final SimpleBooleanProperty bankSplitMemo;

    // class variables
    private static String defaultBankAcct = "";
    static final StringProperty defaultProp = new SimpleStringProperty();

    // constructor
    public BankAcct(String startBankAcctName,
                    String startBankId,
                    String startBankAcctNo,
                    String startBankAcctType,
                    String startBankAcctOfxDir,
                    boolean startBankSplitMemo
    ) {
        this.bankAcctName = new SimpleStringProperty(startBankAcctName);
        this.bankAcctBankId = new SimpleStringProperty(startBankId);
        this.bankAcctNo = new SimpleStringProperty(startBankAcctNo);
        this.bankAcctType = new SimpleStringProperty(startBankAcctType);
        this.bankAcctOfxDir = new SimpleStringProperty(startBankAcctOfxDir);
        this.bankSplitMemo = new SimpleBooleanProperty(startBankSplitMemo);
    }

    // class methods
    public static String getDefaultBankAcct() {
        return defaultBankAcct;
}

    public static void setDefaultBankAcct(String newDefaultBankAcct) {
        BankAcct.defaultBankAcct = newDefaultBankAcct;
        BankAcct.defaultProp.set(newDefaultBankAcct);
}

    // instance methods
    public String getBankAcctName() {
        return bankAcctName.get();
    }
    public void setBankAcctName(String bName) {
        bankAcctName.set(bName);
    }

    public String getBankAcctBankId() {
        return bankAcctBankId.get();
    }
    public void setBankAcctBankId(String bankIdStr) {
        bankAcctBankId.set(bankIdStr);
    }

    public String getBankAcctNo() {
        return bankAcctNo.get();
    }
    public void setBankAcctNo(String bankAcctNoStr) {
        bankAcctNo.set(bankAcctNoStr);
    }

    public String getBankAcctType() {
        return bankAcctType.get();
    }
    public void setBankAcctType(String bankAcctTypeStr) {
        bankAcctType.set(bankAcctTypeStr);
    }

    public String getBankAcctOfxDir() {
        return bankAcctOfxDir.get();
    }
    public void setBankAcctDir(String bankAcctOfxDirStr) {
        bankAcctOfxDir.set(bankAcctOfxDirStr);
    }
    public boolean getBankSplitMemo() {
        return bankSplitMemo.get();
    }
    public void setBankSplitMemo(boolean bankSplitMemoBoo) {
        bankSplitMemo.set(bankSplitMemoBoo);
    }

}