/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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