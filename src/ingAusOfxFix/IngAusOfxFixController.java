/*
 * Copyright (C) 2016 Chris Good
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

/* Date       Vers Comment
   18/07/2016 1.00 Created.
   03/08/2016 1.01 Fix Missing carriage returns in BANKACCTFROM xml entity
                    (doesn't matter to GnuCash either way).
*/

package ingAusOfxFix;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Callback;

/**
 *
 * @author cgood
 */
public class IngAusOfxFixController implements Initializable {

    /* class variables (static) */

    @FXML private GridPane grid;
    @FXML private Text     sceneTitle;
    @FXML private Text     versionNo;
    @FXML private Label     lblName;
    @FXML private ComboBox  bankAcctComboBox;
    @FXML private CheckBox  defaultChb;
    @FXML private Button    btnDelete;
    @FXML private Label     lblBankId;
    @FXML private TextField txtBankId;
    @FXML private Label     lblAcctNo;
    @FXML private TextField txtAcctNo;
    @FXML private Label     lblType;
    @FXML private ComboBox  bankAcctTypeComboBox;
    @FXML private Label     lblOfxDir;
    @FXML private TextField txtOfxDir;
    @FXML private Button    btnChooseOfxDir;
    @FXML private Button    btnSaveSettings;
    @FXML private Label     lblOfxFile;
    @FXML private TextField txtOfxFile;
    @FXML private Button    btnChooseOfxFile;
    @FXML private Label     lblModDate;
    @FXML private TextField txtDateFrom;
    @FXML private TextField txtDateTo;
    @FXML private Button    btnStart;
    @FXML private Label     lblLog;
    @FXML private TextArea  taLog;

    final private static int MAX_BANKS = 100;
//  final private ObservableList<Book> bankAcctComboBoxData = FXCollections.observableArrayList();
    final private ObservableList       bankAcctComboBoxData = FXCollections.observableArrayList();
    final private Map bankAcctMap = new HashMap(MAX_BANKS); // key=bankAcctName, value=ref to BankAcct instance

    final ObservableList bankAcctTypeComboBoxData = FXCollections.observableArrayList(
                "CHECKING", "CREDITLINE", "MONEYMRKT", "SAVINGS");
    final private static String OS_NAME = System.getProperty("os.name" );
    private static String USER_NAME;
    // character that separates folders from files in paths
    //  i.e. Windows = backslash, Linux/OSX = /
    private static final char FILE_SEPARATOR =
        System.getProperty("file.separator").charAt(0);
    private static final String LINE_SEPARATOR =
        System.getProperty("line.separator");
    private static final String HOME_DIR = System.getProperty("user.home");

    private static String baName = "MyBankAcct";        // current bankAcct name
    private static String baBankId = "1234";            // initial default BankAcct Id
    private static String baNo = "12345678";            // initial default Account No
    private static String baType = "SAVINGS";           // initial default Account Type SAVINGS, CHECKING
    private static String baOfxDir = HOME_DIR + FILE_SEPARATOR + "ofx"; // initial default Ofx Directory
    private static final String baOfxFile = "MyOfxFile.ofx";  // initial default Ofx filename

    private static Path pathOfxDirFilStr;
    private final Charset CHAR_SET = Charset.forName("US-ASCII");

    // Saved Settings
    private static final String DEFAULT_PROP = "defaultBankAcct";
    private static final String ACCTNAME_PROP = "bankAcctName.";
    private static final String BANKID_PROP = "bankAcctBankId.";
    private static final String ACCTNO_PROP = "bankAcctNo.";
    private static final String ACCTTYPE_PROP = "bankAcctType.";
    private static final String OFXDIR_PROP = "bankAcctOfxDir.";

    private static final String DEF_PROP = HOME_DIR + FILE_SEPARATOR
            + ".IngAusOfxFix" + FILE_SEPARATOR + "defaultProperties";
    //  default properties
    private static final Properties defaultProps = new Properties();

    private static boolean firstTime = true;
    private static String bankAcctSelectionTarget = "";

    private static final Font BOLD_FONT = Font.font("System", FontWeight.BOLD, 14);
    private static final Font NORMAL_FONT = Font.font("System", FontWeight.NORMAL, 14);

    private static String dateMin = ""; // min date in input ofx file yyyymmdd
    private static String dateMax = ""; // max date in input ofx file yyyymmdd

    @FXML
    public void handleBtnActionDelete(Event e) throws IOException {

        // Note: In Java, there is no way to immediately destroy an instance.
        //  Instances are automatically cleaned up by garbage collection
        //   sometime after they are no longer referenced so there is no
        //   need to dispose of the deleted BankAcct instance here.

        // Note: The deleteBtn is disabled when on the default bankAcct as
        //  this user interface does not permit deleting the default bankAcct

        String tmpBook = (String) bankAcctComboBox.getValue();
        if (((tmpBook != null) &&  (! tmpBook.isEmpty()))) {
            if (bankAcctMap.size() > 1) {
                bankAcctMap.remove(tmpBook);
                bankAcctComboBoxData.remove(tmpBook);
                bankAcctComboBox.setValue(BankAcct.getDefaultBankAcct());
//              bankAcctComboBox.getItems().remove(tmpBook); // Always remove from bankAcctComboBoxData instead!
                bankAcctComboBoxData.remove(tmpBook);
                enable_or_disable_buttons();
            }
        }
    }

    @FXML
    public void handleBtnActionSaveSettings(Event e) throws IOException {

        int i = 0;
        String suffix;
        String tmpBook;

        defaultProps.setProperty(DEFAULT_PROP, BankAcct.getDefaultBankAcct());

        // Until problem in Java 8u92 with adding items to ComboBox which uses SortedList is fixed,
        //  sort the bankAccts before saving

//      Set bankAcctSet = bankAcctMap.keySet();
//      Iterator itr = bankAcctSet.iterator();

        SortedList sortedBookList = new SortedList<>(bankAcctComboBoxData, Collator.getInstance());
        Iterator itr = sortedBookList.iterator();

        while (itr.hasNext()) {
            tmpBook = (String) itr.next();
            BankAcct refBook = (BankAcct) bankAcctMap.get(tmpBook);
            suffix = String.valueOf(i++);
            defaultProps.setProperty(ACCTNAME_PROP + suffix, refBook.getBankAcctName());
            defaultProps.setProperty(BANKID_PROP + suffix, refBook.getBankAcctBankId());
            defaultProps.setProperty(ACCTNO_PROP + suffix, refBook.getBankAcctNo());
            defaultProps.setProperty(ACCTTYPE_PROP + suffix, refBook.getBankAcctType());
            defaultProps.setProperty(OFXDIR_PROP + suffix, refBook.getBankAcctOfxDir());
        }

        try (FileOutputStream out = new FileOutputStream(DEF_PROP)) {
            defaultProps.store(out, "---Backup GnuCash Settings---");
            taLog.setText("Settings successfully saved to " + DEF_PROP);
        } catch (IOException ex) {
            //System.out.println("My Exception Message " + ex.getMessage());
            //System.out.println("My Exception Class " + ex.getClass());
            Logger.getLogger(IngAusOfxFixController.class.getName()).log(Level.SEVERE, null, ex);
            taLog.setText("Error: Cannot Save Settings to : " + DEF_PROP);
        }
    }

    // NOTE: There was a bug which was fixed in Java 1.8.0_72 (or maybe 1.8.0_74 ?)
    //        https://bugs.openjdk.java.net/browse/JDK-8136838
    //      which meant the value of ComboBox.getValue() was not correct.
    //   Therefore, For BackupGnuCash V#1.20 or later, which now uses
    //    an editable combobox for the bankAcct settings,
    //   MUST use Java 1.8.0_72 or later!
    //
    //   Ubuntu 16.04 openjfx 8u60-b27-4 seems to work OK except must press
    //    ENTER after typing new bankAcct name into bankAcctComboBox to get OnAction
    //    to fire. OnAction does not fire if focus changes away.

    // Handle selections in editable bankAcctComboBox

    // This event occurs whenever a new item is selected.
    // This can be because a new item as clicked in the dropdown list
    //  or a new item was keyed into the combobox (and action key ENTER typed or focus lost)
    //  or a new item is automatically selected because the previous
    //   selected item has been removed from the item list.

    // This event does nothing if:
    //  bankAcctSelectionTarget is not Empty AND != the new selection (selected).
    // This is to avoid loops from when a new bankAcct becomes selected
    //  after a bankAcct is intentionally removed from the combobox
    //   (in order to force the dropdown list to be re-rendered),
    //  then added again.
    // 14/07/2016: Now that bankAccts are not intentionally removed, then added again
    //   to force re-rendering, bankAcctSelectionTarget may no longer be needed but
    //   has been left in here just in case...

    @FXML
    public void handleBankAcctComboBoxOnAction(Event event) {
        String selected;
        if (bankAcctComboBox.getValue() == null) {
            selected = "";
        } else {
            selected = bankAcctComboBox.getValue().toString();
        }
        //String editted = bankAcctComboBox.getEditor().getText();
        //System.out.println("handleBookComboBoxOnAction(): selected: " + selected
        //    + " editted=" + editted);

        if
        ( ( (selected != null) && (! selected.isEmpty()))
          &&
          ((bankAcctSelectionTarget.isEmpty()) || (bankAcctSelectionTarget.equals(selected)))
        ) {
            // If new bankAcct (selected) already exists
            //   change to it and show related fields
            // else
            //   add the new bankAcct instance to BankAcct class, bankAcctMap and
            //   bankAcctComboBoxData and make it the
            //   selected combobox item
            if (bankAcctMap.containsKey(selected)) {
                // Get ref to bankAcct object from bankAcctMap
                BankAcct bankAcct = (BankAcct) bankAcctMap.get(selected);
                //bankAcctComboBox.setValue(selecteded); // set selected Value - do NOT do here - causes loop
//                System.out.println("bankAcctComboBox.setOnAction: txtGcDatFilStr.setText to " + bankAcct.getGcDat());
                txtBankId.setText(bankAcct.getBankAcctBankId());
                txtAcctNo.setText(bankAcct.getBankAcctNo());
                bankAcctTypeComboBox.setValue(bankAcct.getBankAcctType());
                txtOfxDir.setText(bankAcct.getBankAcctOfxDir());
//              txtOfxFile.setText("");
            } else {
                BankAcct bankAcct = new BankAcct(
                    selected,
                    txtBankId.getText(),
                    txtAcctNo.getText(),
                    bankAcctTypeComboBox.getValue().toString(),
                    txtOfxDir.getText());
                bankAcctMap.put(selected, bankAcct);
                //bankAcctComboBox.setValue(selected);     // set selected Value - do NOT do here causes loop
                bankAcctComboBoxData.add(selected);
            }
            if (BankAcct.getDefaultBankAcct().equals(selected)) {
                if (! defaultChb.isSelected()) {
                    defaultChb.setSelected(true);
                }
            } else {
                if (defaultChb.isSelected()) {
                    defaultChb.setSelected(false);
                }
            }
        }
    }

    public void getUserDefaults() {

        bankAcctComboBox.setEditable(true);
        bankAcctComboBox.setVisibleRowCount(20);

        try (   // with resources
            FileInputStream in = new FileInputStream(DEF_PROP);
        )
        {
            int i = 0;
            String suffix;
            String tmpStr;

            defaultProps.load(in);

            tmpStr = defaultProps.getProperty(DEFAULT_PROP);
            if (tmpStr == null || tmpStr.isEmpty()) {
                tmpStr = baName;
            }
            BankAcct.setDefaultBankAcct(tmpStr);
            // Set the bankAcctComboBox selected item
            // Note: Java 1.8.0_05 : needed to set selected item BEFORE populating the combobox
            //          or the selected item is not displayed initially
            //       Java 1.8.0_92 : NO need to set selected item before populating the combobox
            //  As need to use Java 1.8.0_72 or later anyway due to
            //      https://bugs.openjdk.java.net/browse/JDK-8136838,
            //   don't worry about 1.8.0_05
            bankAcctComboBox.setValue(tmpStr);
//            System.out.println("getUserDefault(): Default bankAcct=" + tmpStr);

            // load bankAcct settings into collection bankAcctComboBoxData, then bankAcctComboBox
            while (i < MAX_BANKS) {
                suffix = String.valueOf(i++);
                tmpStr = defaultProps.getProperty(ACCTNAME_PROP + suffix);
                if (tmpStr == null) {
                    break;
                }
                baName = tmpStr;
                baBankId = defaultProps.getProperty(BANKID_PROP + suffix);
                baNo = defaultProps.getProperty(ACCTNO_PROP + suffix);
                baType = defaultProps.getProperty(ACCTTYPE_PROP + suffix);
                baOfxDir = defaultProps.getProperty(OFXDIR_PROP + suffix);

                BankAcct bankAcct = new BankAcct(baName, baBankId, baNo, baType, baOfxDir);
                bankAcctComboBoxData.add(baName);
                bankAcctMap.put(baName, bankAcct);  // save ref to bankAcct in hashmap

                if (BankAcct.getDefaultBankAcct().equals(baName)) {
                    txtBankId.setText(baBankId);
//                    System.out.println("getUserDefaults(): txtGcDatFilStr set to " + gcDatFil);
                    txtAcctNo.setText(baNo);
                    bankAcctTypeComboBox.setValue(baType);
                    txtOfxDir.setText(baOfxDir);
                    defaultChb.setSelected(true);
                }
                //i++;
            }
            if (bankAcctComboBoxData.isEmpty()) {
                BankAcct bankAcct = new BankAcct(baName, baBankId, baNo, baType, baOfxDir);
                bankAcctComboBoxData.add(baName);
                bankAcctMap.put(baName, bankAcct);
            }

            bankAcctComboBox.setItems(bankAcctComboBoxData);      // items are not sorted using this

            //in.close();  // done automatically when 'try with resources' ends
        } catch (IOException ex) {
            //System.out.println("My Exception Message " + ex.getMessage());
            //System.out.println("My Exception Class " + ex.getClass());

            if (ex.getClass().toString().equals("class java.io.FileNotFoundException")) {
                System.out.println("getUserDefaults: " + ex.getMessage());
                BankAcct.setDefaultBankAcct(baName);
                BankAcct bankAcct = new BankAcct(baName, baBankId, baNo, baType, baOfxDir);
                bankAcctComboBoxData.add(baName);
                bankAcctMap.put(baName, bankAcct);
//              bankAcctComboBox.setItems(new SortedList<>(bankAcctComboBoxData, Collator.getInstance()));  // JDK-8087838
                bankAcctComboBox.setItems(bankAcctComboBoxData);
                bankAcctComboBox.setValue(baName);
                defaultChb.setSelected(true);
            } else {
                Logger.getLogger(IngAusOfxFixController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // set up bankAcctTypeComboBox
        bankAcctTypeComboBox.setEditable(false);
        bankAcctTypeComboBox.setVisibleRowCount(20);
        bankAcctTypeComboBox.setItems(bankAcctTypeComboBoxData);
        bankAcctTypeComboBox.setValue("SAVINGS");   // Default bank acct type
    }

    /**
     * This function gets the range of transaction dates from the input OFX file
     *  and shows them in screen fields txtDateFrom and txtDateTo.
     * Called whenever focus leaves txtOfxDir or txtOfxFile,
     *  and after btnChooseOfxDir or btnChooseOfxFile is used.
     *
     * @author cgood
     */

    void getDatesFromFile() {
        dateMin = "";
        dateMax = "";
        txtDateFrom.setText("");
        txtDateTo.setText("");

        if ( (txtOfxDir.getText()  == null) || (txtOfxDir.getText().equals(""))
        ||   (txtOfxFile.getText() == null) || (txtOfxFile.getText().equals(""))) {
            return;
        }
        pathOfxDirFilStr = Paths.get(txtOfxDir.getText() + FILE_SEPARATOR + txtOfxFile.getText());
        if (Files.isReadable(pathOfxDirFilStr)) {
            try (BufferedReader reader = Files.newBufferedReader(pathOfxDirFilStr, CHAR_SET)) {
                String line;
                String tmpDate;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("<DTPOSTED>")) {
                        // DTPOSTED is yyyymmddhhmmss - get yyyymmdd
                        tmpDate = line.substring(10, 18);
//                      System.out.println("getDatesFromFile(): tmpDate=" + tmpDate);
                        if (!tmpDate.matches("\\d\\d\\d\\d\\d\\d\\d\\d")) {
                            taLog.appendText("getDatesFromFile(): tmpDate not yyyymmdd : " + tmpDate);
                        } else {
                            if (dateMin.isEmpty()) {
                                dateMin = tmpDate;
                                dateMax = tmpDate;
                            } else {
                                if (tmpDate.compareTo(dateMin) < 0) {
                                    dateMin = tmpDate;
                                }
                                if (tmpDate.compareTo(dateMax) > 0) {
                                    dateMax = tmpDate;
                                }
                            }
                        }
                    }
                }
            } catch (IOException x) {
                System.err.format("getDateFromFile(): IOException: %s%n", x);
                taLog.appendText("getDateFromFile(): IOException: " + x + "\n");
            }
        }
        txtDateFrom.setText(dateMin);
        txtDateTo.setText(dateMax);
    }

    boolean isValidBankAcctName() {
        return (bankAcctComboBox.getValue() != null) && (! bankAcctComboBox.getValue().toString().equals(""));
    }

    boolean isValidBankId() {
        return (txtBankId.getText() != null) && (! txtBankId.getText().equals(""));
    }

    boolean isValidAcctNo() {
        return (txtAcctNo.getText() != null) && (! txtAcctNo.getText().equals(""));
    }

//    boolean isValidAcctType() {
//        return (bankAcctTypeComboBox.getValue() != null) && (! bankAcctTypeComboBox.getValue().toString().equals(""));
//    }

    boolean isValidOfxDir() {
        if ((txtOfxDir.getText() == null) || (txtOfxDir.getText().equals(""))) {
            taLog.appendText("Please enter OFX Directory\n");
            return false;
        }
        
        if (! Files.isWritable(Paths.get(txtOfxDir.getText()))) {
            taLog.appendText("Error: OFX directory " + txtOfxDir.getText()
                    + " is not writable or does not exist\n");
            return false;
        }
        return true;
    }

    boolean isValidOfxFile() {
        if ((txtOfxFile.getText() == null) || (txtOfxFile.getText().equals(""))) {
            taLog.appendText("Please enter Input OFX File\n");
            return false;
        }
        pathOfxDirFilStr = Paths.get(txtOfxDir.getText() + FILE_SEPARATOR + txtOfxFile.getText());
        if (Files.isReadable(pathOfxDirFilStr)) {
            // show the Last Modified date/time
            //  FileTime epoch 1970-01-01T00:00:00Z (FileTime no longer used)
            try {
               SimpleDateFormat sdfFormat = new SimpleDateFormat("EEE, dd/MM/yyyy hh:mm aa");
               lblModDate.setText("Modified : " + sdfFormat.format(Files.getLastModifiedTime(pathOfxDirFilStr).toMillis()));
            } catch (IOException x) {
                System.err.println(x);
                lblModDate.setText("IOException getLastModifiedTime" + pathOfxDirFilStr.toString());
                return false;
            }
            Path pathOfxOut = Paths.get(pathOfxDirFilStr.toString().replace(".ofx", "New.ofx"));
            if (Files.isReadable(pathOfxOut)) {
                if (Files.isWritable(pathOfxOut)) {
                    taLog.appendText("Warning: Output file already exists : " + pathOfxOut.toString() + "\n");
                } else {
                    taLog.appendText("Error: Output file already exists but is not writable : " + pathOfxOut.toString() + "\n");
                    return false;
                }
            }
        } else {
            taLog.appendText("Error: Input OFX File " + pathOfxDirFilStr.toString() +
                " is not readable or does not exist\n");
            return false;
        }
        return true;
    }

    boolean isValidDateFrom() {
        if ((txtDateFrom.getText() == null)
        ||  (txtDateFrom.getText().equals(""))
        ||  (! txtDateFrom.getText().matches("\\d\\d\\d\\d\\d\\d\\d\\d"))) {
            taLog.appendText("Please enter Date From YYYYMMDD\n");
            return false;
        }
        if ((txtDateTo.getText() != null)
        &&  (txtDateFrom.getText().compareTo(txtDateTo.getText()) > 0)) {
            taLog.appendText("Error: Date From cannot be greater than Date To\n");
            return false;
        }
        return true;
    }

    boolean isValidDateTo() {
        if ( (txtDateTo.getText() == null)
        ||   (txtDateTo.getText().equals(""))
        ||   (! txtDateTo.getText().matches("\\d\\d\\d\\d\\d\\d\\d\\d"))) {
            taLog.appendText("Please enter Date To YYYYMMDD\n");
            return false;
        }
        if ((txtDateFrom.getText() != null)
        &&  (txtDateFrom.getText().compareTo(txtDateTo.getText()) > 0)) {
            taLog.appendText("Error: Date To cannot be less than Date From\n");
            return false;
        }
        return true;
    }

    void setTooltips() {

        // Create Tooltips (mouse-over text)

        bankAcctComboBox.setTooltip(new Tooltip(
            "Bank account name to be used for this bank account's backup settings.\n" +
            "For example: MyBankAccount\n" +
            "To add a new bank account name:\n" +
            " Type the new bank account name in this combobox, then press ENTER,\n"  +
            " then change the other fields (Bank Id, Account No, Account Type and Ofx Directory).\n" +
            "Use the Save Settings button to save the settings for all bank accounts."
        ));

        defaultChb.setTooltip(new Tooltip(
            "Default:\n" +
            "If ticked, this bank account is the default shown when this program starts.\n" +
            "To make another bank account the default:\n" +
            " First select the new default bank account, then tick this checkbox."
        ));

        btnDelete.setTooltip(new Tooltip(
            "Delete:\nDelete settings for the current bank account.\n" +
            "The settings for the last remaining bank account and the default bank account cannot be deleted.\n" +
            "To delete the default bank account, first make another bank account the default."
        ));

        btnSaveSettings.setTooltip(new Tooltip(
            "Save Settings:\nSave all bank account settings in\n" + DEF_PROP + "."
        ));

        txtOfxDir.setTooltip(new Tooltip(
            "OFX Directory:\n" +
            "The directory for the OFX data files for this bank account.\n"
        ));

        txtOfxFile.setTooltip(new Tooltip(
            "OFX File:\n" +
            "The input file name within the OFX Directory.\n" +
            "The output file name will be the input name suffixed with 'New' " +
            " before the .ofx extension."
        ));

        txtDateFrom.setTooltip(new Tooltip(
            "Date From YYYYMMDD:\n" +
            "After Input OFX File is entered or selected, the file is read\n" +
            "and the date range of transactions in the file is updated to the\n" +
            "Date From and Date To screen fields.\n" +
            "Modify Date From and/or Date To if only transactions for a\n" +
            "particular date range are required to be output."
        ));

        txtDateTo.setTooltip(new Tooltip(
            "Date To YYYYMMDD:\n" +
            "After Input OFX File is entered or selected, the file is read\n" +
            "and the date range of transactions in the file is updated to the\n" +
            "Date From and Date To screen fields.\n" +
            "Modify Date From and/or Date To if only transactions for a\n" +
            "particular date range are required to be output."
        ));
    }

    void enable_or_disable_buttons() {
//        System.out.println("Start enable_or_disable_buttons" +
//            " bankAcctComboBox.getValue()=" + bankAcctComboBox.getValue() +
//            " txtGcDatFilStr.getText()=" + txtGcDatFilStr.getText());

        boolean boolBankAcctOK = false;
        boolean boolBankIdOk = false;
        boolean boolAcctNoOk = false;
//      boolean boolAcctTypeOk = false;     // Not needed - combobox will always have a valid value
        boolean boolOfxDirOk = false;
        boolean boolOfxFileOK = false;
        boolean boolDatesOk = false;

//        ObservableList<String> items = bankAcctComboBox.getItems();
//        items.stream().forEach((item) -> {
//            System.out.println("bankAcctComboBox " + item);
//        });

//        for (Iterator it = bankAcctComboBoxData.iterator(); it.hasNext();) {
//            String item = (String) it.next();
//            System.out.println("bankAcctComboBoxData " + item);
//        }

        taLog.clear();

        // Note:    Disable  property : Defines the individual disabled state of this Node.
        //                              'Disable' may be false and 'Disabled true' if a parent node is Disabled.
        //          Disabled propery  : Indicates whether or not this Node is disabled. Could be 'Disabled' because parent node is disabled.

        // Test: isDisabled(), Set: setDisable() NOT setDisabled()

        if (isValidBankAcctName()) {
            boolBankAcctOK = true;
        } else {
            taLog.appendText("Please enter Bank Account Name\n");
        }

        if (isValidBankId()) {
            boolBankIdOk = true;
        } else {
            taLog.appendText("Please enter Bank Id\n");
        }

        if (isValidAcctNo()) {
            boolAcctNoOk = true;
        } else {
            taLog.appendText("Please enter Account No\n");
        }

        if (isValidOfxDir()) {
            boolOfxDirOk = true;
        }

        if (boolOfxDirOk && isValidOfxFile()) {
            boolOfxFileOK = true;
            if (isValidDateFrom() && isValidDateTo()) {
                boolDatesOk = true;
            }
        }

        // Note:
        //   To Test: use isDisabled(),
        //     as includes a node being disabled due to a parent being disabled
        //     whereas isDisable() only applies to the current node
        //   To actually enable or disable: setDisable()

        // enable or disable defaultChb
        // You can only change a non-default bank acct to be the default,
        // you cannot make the default bank acct not the default.
        // This way you have to choose the new default.
        if ((boolBankAcctOK) && (bankAcctComboBoxData.size() > 1)
        && (! bankAcctComboBox.getValue().equals(BankAcct.getDefaultBankAcct()))) {
            if (defaultChb.isDisabled()) {
                defaultChb.setDisable(false);       // Enable
            }
        } else {
            if (! defaultChb.isDisabled()) {
                defaultChb.setDisable(true);        // Disable
            }
        }

        // enable or disable btnDelete
        // default bankAcct cannot be deleted
        if (((boolBankAcctOK) && (bankAcctComboBoxData.size() > 1)
        && (! bankAcctComboBox.getValue().equals(BankAcct.getDefaultBankAcct())))) {
            if (btnDelete.isDisabled()) {
                btnDelete.setDisable(false);       // Enable
            }
        } else {
            if (! btnDelete.isDisabled()) {
                btnDelete.setDisable(true);        // Disable
            }
        }

        // enable or disable btnStart
        if (boolBankAcctOK && boolBankIdOk && boolAcctNoOk && boolOfxDirOk
        &&  boolOfxFileOK && boolDatesOk) {
            if (btnStart.isDisabled()) {        // if Disabled
                btnStart.setDisable(false);     //     Enable
            }
        } else {
            if (! btnStart.isDisabled()) {      // if Enabled
                btnStart.setDisable(true);     //      Disable
            }
        }

        // enable or disable btnSaveSettings
        if (boolBankAcctOK && boolBankIdOk && boolAcctNoOk && boolOfxDirOk) {
            if (btnSaveSettings.isDisabled()) {        // if Disabled
                btnSaveSettings.setDisable(false);     //     Enable
                //System.out.println("btnSaveSettings Enabled");
            }
            // If BankAcct already exists
            //   Update BankAcct instance from screen fields
            // else
            //   Add current settings to BankAcct, bankAcctMap and bankAcctComboBoxData
            if (bankAcctMap.containsKey(bankAcctComboBox.getValue())) {
                BankAcct bankAcct = (BankAcct)bankAcctMap.get(bankAcctComboBox.getValue());
                bankAcct.setBankAcctBankId(txtBankId.getText());
                bankAcct.setBankAcctNo(txtAcctNo.getText());
                bankAcct.setBankAcctType(bankAcctTypeComboBox.getValue().toString());
                bankAcct.setBankAcctDir(txtOfxDir.getText());
//                System.out.println("enable_or_disable:"
//                    + "set bankAcct=" + bankAcctComboBox.getValue()
//                    + " BankId=" + txtBankId.getText()
//                    + " AcctNo=" + txtAcctNo.getText()
//                    + " Type=" + bankAcctTypeComboBox.getValue());
            } else {
                BankAcct bankAcct = new BankAcct(bankAcctComboBox.getValue().toString(),
                                     txtBankId.getText(),
                                     txtAcctNo.getText(),
                                     bankAcctTypeComboBox.getValue().toString(),
                                     txtOfxDir.getText());
                bankAcctMap.put(bankAcctComboBox.getValue(), bankAcct);
                bankAcctComboBoxData.add(bankAcctComboBox.getValue().toString());
            }
        } else {
            if (! btnSaveSettings.isDisabled()) {      // if Enabled
                btnSaveSettings.setDisable(true);     //      Disable
            }
        }

        // Change Focus to OFX File if everything else OK
        if ((boolBankAcctOK && boolBankIdOk && boolAcctNoOk && boolOfxDirOk) && (! boolOfxFileOK) ) {
            if (! txtOfxFile.isFocused()) {
                if (firstTime) {
                    firstTime = false;
                    // When run from initialize(), controls are not yet ready to handle focus
                    //  so delay first execution of requestFocus until later
                    //  Refer http://stackoverflow.com/questions/12744542/requestfocus-in-textfield-doesnt-work-javafx-2-1
                    Platform.runLater(() -> {
                        txtOfxFile.requestFocus();
                    });
//                } else {
//                    txtOfxFile.requestFocus();
//                    //System.out.println("enable_or_disable_buttons: txtPswd.requestFocus");
                }
            }
        }
    }

    /**
     * This function creates a new .ofx file from the file downloaded from
     *  ING Australia bank with the following modifications:
     *  1. add missing BANKACCTFROM xml entity before BANKTRANLIST.
     *      This is needed because GnuCash doesn't find any transactions to
     *      import without this.
     *  2. ensure Financial Institution Transaction ID (FITID) is unique for
     *      every transaction by appending "." + DTPOSTED + "." + TRNAMT to the
     *      input file FITID.
     *      This is because FITID's are supposed to be unique for every
     *      transaction within each bank account and GnuCash will treat
     *      transactions with duplicate FITID's as already imported.
     *
     * @author cgood
     * @param e
     * @throws java.io.IOException
     */

    /* Here is an example OFX Input file :
        OFXHEADER:100
        DATA:OFXSGML
        VERSION:102
        SECURITY:NONE
        ENCODING:USASCII
        CHARSET:1252
        COMPRESSION:NONE
        OLDFILEUID:NONE
        NEWFILEUID:NONE
        <OFX>
        <SIGNONMSGSRSV1>
        <SONRS>
        <STATUS>
        <CODE>0
        <SEVERITY>INFO
        </STATUS>
        <DTSERVER>20160708104407
        <LANGUAGE>ENG
        </SONRS>
        </SIGNONMSGSRSV1>
        <BANKMSGSRSV1>
        <STMTTRNRS>
        <TRNUID>1
        <STATUS>
        <CODE>0
        <SEVERITY>INFO
        </STATUS>
        <STMTRS>
        <CURDEF>AUD
        <BANKACCTFROM>                        THIS SECTION MAY NEED TO BE ADDED
        <BANKID>1234                                    "
        <ACCTID>12345678                                "
        <ACCTTYPE>SAVINGS                               "
        </BANKACCTFROM>                                 "
        <BANKTRANLIST>
        <STMTTRN>                                   Start of 1st Transaction
        <TRNTYPE>CREDIT
//////////        <DTPOSTED>20160630000000
        <TRNAMT>5.23
        <FITID>903889                                       MUST BE UNIQUE
        <MEMO>Bonus Interest Credit - Receipt 903889
        </STMTTRN>
        <STMTTRN>                                   Start of 2nd Transaction
        <TRNTYPE>CREDIT
        <DTPOSTED>20160630000000
        <TRNAMT>10.47
        <FITID>903889                                       MUST BE UNIQUE
        <MEMO>Interest Credit
        </STMTTRN>
        </BANKTRANLIST>
        </STMTRS>
        </STMTTRNRS>
        </BANKMSGSRSV1>
        </OFX>
    */

    @FXML
    public void handleBtnActionStart(Event e) throws IOException
    {
        boolean boolBankAcctFrom_found = false;
        boolean boolbankTranList_found = false;
        boolean boolInTransaction = false;
        boolean boolTrnDateInRange = false;
        String trnType = "";
        String dtPosted = "";
        String trnAmt = "";
        String fitId = "";
        final String bankAcctFromElement = "<BANKACCTFROM>" + LINE_SEPARATOR
            + "<BANKID>" + txtBankId.getText() + LINE_SEPARATOR
            + "<ACCTID>" + txtAcctNo.getText() + LINE_SEPARATOR
            + "<ACCTTYPE>" + bankAcctTypeComboBox.getValue().toString() + LINE_SEPARATOR
            + "</BANKACCTFROM>" + LINE_SEPARATOR;
        Integer transMod = 0;
        Integer transIn = 0;
        Integer transDrop = 0;
        Integer transOut = 0;
        Integer linesIn = 0;
        Integer linesOut = 0;

        Path pathOfxIn = Paths.get(txtOfxDir.getText() + FILE_SEPARATOR + txtOfxFile.getText());
        Path pathOfxOut = Paths.get(txtOfxDir.getText() + FILE_SEPARATOR + txtOfxFile.getText().replace(".ofx", "New.ofx"));

        taLog.clear();
        taLog.appendText("Reading: " + pathOfxIn.toString() + "\n");
        taLog.appendText("Writing: " + pathOfxOut.toString() + "\n");
        try (BufferedReader reader = Files.newBufferedReader(pathOfxIn, CHAR_SET);
             BufferedWriter writer = Files.newBufferedWriter(pathOfxOut, CHAR_SET)) {

            String line;
            while ((line = reader.readLine()) != null) {
                // note line termination chars have been stripped by readLine()
//                System.out.println("Line read: " + line);
                linesIn++;
                if (line.startsWith("<BANKACCTFROM>")) {
                    boolBankAcctFrom_found = true;
                }
                if (line.startsWith("<BANKTRANLIST>")) {
                    if (boolBankAcctFrom_found) {
                        taLog.appendText("Input file already has BANKACCTFROM details\n");
                    } else {
                        taLog.appendText("Adding BANKACCTFROM details (5 lines)\n");
                        writer.write(bankAcctFromElement, 0, bankAcctFromElement.length());
                        linesOut = linesOut + 5;
                    }
                }
                if (line.startsWith("<STMTTRN>")) {     // start of a transaction
                    boolInTransaction = true;
                    transIn++;
                    trnType = "";
                    dtPosted = "";
                    trnAmt = "";
                    fitId = "";
                    boolTrnDateInRange = false;
                    continue; // don't write until we have checked date within range
                }
                if (boolInTransaction) {
                    if ((line.startsWith("<TRNTYPE>"))) {
                        trnType = line.replace("<TRNTYPE>", "");
                        continue;  // don't write until we have checked date within range
                    } else {
                        if (line.startsWith("<DTPOSTED>")) {
                            // DTPOSTED is yyyymmddhhmmss
                            //  (hhmmss is zeroes for ING Australia)
                            dtPosted = line.substring(10, 18);   // get yyyymmdd
        //                              System.out.println("handleBtnActionStart(): dtPosted=" + dtPosted);
                            if (dtPosted.matches("\\d\\d\\d\\d\\d\\d\\d\\d")) {
                                if ((dtPosted.compareTo(txtDateFrom.getText()) >= 0)
                                &&  (dtPosted.compareTo(txtDateTo.getText()) <= 0)) {
                                    boolTrnDateInRange = true;
                                    writer.write("<STMTTRN>" + LINE_SEPARATOR);
                                    writer.write("<TRNTYPE>" + trnType + LINE_SEPARATOR);
                                    linesOut = linesOut + 2;
                                    transOut++;
                                } else {
                                    transDrop++;
                                    continue;
                                }
                            } else {
                                taLog.appendText("Invalid DTPOSTED - transaction dropped: " + line + "\n");
                                transDrop++;
                                continue;
                            }
                        } else {
                            if (line.startsWith("<TRNAMT>")) {
                                if (boolTrnDateInRange) {
                                    trnAmt = line.replace("<TRNAMT>", "");
                                } else {
                                    continue;
                                }
                            } else {
                                if (line.startsWith("<FITID>")) {
                                    if (boolTrnDateInRange) {
                                        if (line.contains(".")) {
                                            taLog.appendText("FITID has already been fixed: " + line + "\n");
                                        } else {
                                            line = line + "." + dtPosted + "." + trnAmt;
                                            transMod++;
                //                          taLog.appendText("New FITID: " + line + "\n");
                                        }
                                    } else {
                                        continue;
                                    }
                                } else {
                                    if (line.startsWith("<MEMO>")) {
                                        if (!boolTrnDateInRange) {
                                            continue;
                                        }
                                    } else {
                                        if (line.startsWith("</STMTTRN>")) {
                                            boolInTransaction = false;
                                            if (!boolTrnDateInRange) {
                                                continue;
                                            }
                                        } else {
                                            taLog.appendText("Unknown record type within STMTTRN: " + line + "\n");
                                            if (!boolTrnDateInRange) {
                                                continue;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                writer.write(line + LINE_SEPARATOR);
                linesOut++;
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
            taLog.appendText("IOException: " + x + "\n");
        }
        taLog.appendText("Transactions In      : " + transIn.toString() + "\n");
        taLog.appendText("Transactions Dropped : " + transDrop.toString() + "\n");
        taLog.appendText("Transactions Out     : " + transOut.toString() + "\n");
        taLog.appendText("Transactions Modified: " + transMod.toString() + "\n");
        taLog.appendText("Lines In             : " + linesIn.toString() + "\n");
        taLog.appendText("Lines Out            : " + linesOut.toString() + "\n");
    }

    @FXML
    public void handleBtnActionChooseOfxFile(Event e) throws IOException {

        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose OFX file");

        if ((txtOfxFile.getText() == null)
        ||  (txtOfxFile.getText().isEmpty())) {
            txtOfxFile.setText(baOfxFile);
            enable_or_disable_buttons();
        }

        final File file = new File(txtOfxDir.getText() + FILE_SEPARATOR + txtOfxFile.getText());
        final String strDir = file.getParent();
        final Path pathOfxDir = Paths.get(strDir);
        if (Files.isReadable(pathOfxDir)) {
            fileChooser.setInitialDirectory(new File(strDir));
        } else {
            fileChooser.setInitialDirectory(new File(HOME_DIR));
        }
        fileChooser.setInitialFileName(file.getName());
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("OFX files (*.ofx)", "*.ofx");
        fileChooser.getExtensionFilters().add(extFilter);

        // get a reference to the current stage for use with showOpenDialog
        //  so it is modal

        Scene scene = btnChooseOfxFile.getScene(); // any control would do
        if (scene != null) {
            //System.out.println("scene!=null");
            Window window = scene.getWindow();
            File fileSel = fileChooser.showOpenDialog(window);
            if (fileSel != null) {
                txtOfxFile.setText(fileSel.getName());
                getDatesFromFile();
                enable_or_disable_buttons();
            }
        } else {
            //System.out.println("scene=null");
            taLog.appendText("Error: Cannot open modal fileChooser - scene is null\n");
        }
    }

    @FXML
    public void handleBtnActionChooseOfxDir() {

        // Choose the OFX Directory

        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose OFX Directory");

        if ((txtOfxDir.getText() == null)
        ||  (txtOfxDir.getText().isEmpty())) {
            txtOfxDir.setText(baOfxDir);
            enable_or_disable_buttons();
        }
        final File file = new File(txtOfxDir.getText());
        final String strDir = file.getPath();
        final Path pathOfxDir = Paths.get(strDir);
        if (Files.isReadable(pathOfxDir)) {
            directoryChooser.setInitialDirectory(file);
        } else {
            directoryChooser.setInitialDirectory(new File(HOME_DIR));
        }

        Scene scene = btnChooseOfxDir.getScene(); // any control would do
        if (scene != null) {
            //System.out.println("scene!=null");
            Window window = scene.getWindow();
            final File selectedDirectory = directoryChooser.showDialog(window);
            if (selectedDirectory != null) {
                selectedDirectory.getAbsolutePath();
                try {
                    txtOfxDir.setText(selectedDirectory.getCanonicalPath());
                } catch (IOException ex) {
                    Logger.getLogger(IngAusOfxFixController.class.getName()).log(Level.SEVERE, null, ex);
                }
                getDatesFromFile();
                enable_or_disable_buttons();
            }
        } else {
            //System.out.println("scene=null");
            taLog.appendText("Error: Cannot open modal directoryChooser - scene is null\n");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        throw new UnsupportedOperationException("Not supported yet.");
//        To change body of generated methods, choose Tools | Templates.

        setTooltips();

        // create dir $HOME/.IngAusOfxFix if doesn't already exist
        Boolean boolPropDirOK = true;
        Path pthProp = Paths.get(DEF_PROP).getParent();
        if (! Files.exists(pthProp)) {
            try {
                Files.createDirectory(pthProp);
            } catch (IOException ex) {
                //Logger.getLogger(IngAusOfxFixController.class.getName()).log(Level.SEVERE, null, ex);
                boolPropDirOK = false;
                taLog.setText("Error: Cannot create folder: " + pthProp.toString());
            }
        }
        getUserDefaults();

        // bankAcctComboBox CellFactory : Make default bankAcct bold in dropdown list

        bankAcctComboBox.setCellFactory(
        new Callback<ListView<String>, ListCell<String>>() {
            @Override public ListCell<String> call(ListView<String> param) {
            final ListCell<String> cell = new ListCell<String>() {
                {   // instance initializer
                    super.setPrefWidth(100);
                    fontProperty().bind(Bindings.when(itemProperty().isEqualTo(BankAcct.defaultProp))
                    .then(BOLD_FONT)
                    .otherwise(NORMAL_FONT));
                }
                @Override public void updateItem(String item,
                    boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item);
//                                  if (item.equals(BankAcct.getDefaultBankAcct())) {
//                                      setFont(Font.font("System", FontWeight.BOLD, 14));
//                                      System.out.println("bankAcctComboBox.setCellFactory: set BOLD item=" + item);
//                                  }
//                                  else {
//                                      setFont(Font.font("System", FontWeight.NORMAL, 14));
//                                      System.out.println("bankAcctComboBox.setCellFactory: set NORMAL item=" + item);
//                                  }
                        }
                        else {
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        });

        // handle changes to checkbox defaultChb

        defaultChb.selectedProperty().addListener((ObservableValue<? extends Boolean> o, Boolean wasSelected, Boolean isNowSelected) -> {
//                System.out.println("defaultChb.selectedProperty has changed" +
//                    " oldVal=" + wasSelected + " newVal=" + isNowSelected + " o=" + o);

            final String oldDefault = (String) BankAcct.getDefaultBankAcct();
            bankAcctSelectionTarget = (String) bankAcctComboBox.getValue();

            if (isNowSelected) {
                // IS ticked so this bankAcct will become the new default
                BankAcct.setDefaultBankAcct((String) bankAcctSelectionTarget);
//                    System.out.println("defaultChb.selectedProperty: IsTicked: new defaultBook=" + BankAcct.getDefaultBankAcct());
            } else {
                // Current bankAcct is now NOT to be the default.
                // Current bankAcct is either the default bankAcct
                //   or a new bankAcct if default bankAcct name was changeed to a new bankAcct name
                //
                // If current bankAcct is not the default
                //   do nothing - default stays the same
                // else
                //   If 1st BankAcct is the default
                //     set default to 2nd bankAcct
                //   else
                //     set default to 1st bankAcct

                // Following is not needed anymore now bankAcctComboBox dropdown listView font
                //  is bound to defaultProp and cannot untick the default bankAcct
                //  as defaultChb is disabled when default bankAcct is currently selected.
//                    if (bankAcctSelectionTarget.equals(BankAcct.getDefaultBankAcct())) {
//                        if (bankAcctComboBoxData.get(0).equals(BankAcct.getDefaultBankAcct())) {
//                            BankAcct.setDefaultBankAcct((String) bankAcctComboBoxData.get(1));
//                        } else {
//                            BankAcct.setDefaultBankAcct((String) bankAcctComboBoxData.get(0));
//                        }
//                    }
//                  System.out.println("defaultChb.selectedProperty: IsNotTicked: new defaultBook=" + BankAcct.getDefaultBankAcct());
            }

            bankAcctSelectionTarget = "";
            enable_or_disable_buttons();
        });

        // handle changes to txtBankid so that a new value
        //  is updated into BankAcct.bankAcctBankId
        txtBankId.focusedProperty().addListener((ObservableValue<? extends Boolean> o, Boolean wasFocused, Boolean isNowFocused) -> {
//              System.out.println("txtBankId.focusedProperty has changed" +
//                  " oldVal=" + wasFocused + " newVal=" + isNowFocused + " o=" + o);
            if (wasFocused) {
                // has just lost focus
                enable_or_disable_buttons();
            }
        });

        // handle changes to txtAcctNo so that a new value
        //  is updated into BankAcct.bankAcctNo
        txtAcctNo.focusedProperty().addListener((ObservableValue<? extends Boolean> o, Boolean wasFocused, Boolean isNowFocused) -> {
//              System.out.println("txtAcctNo.focusedProperty has changed" +
//                  " oldVal=" + wasFocused + " newVal=" + isNowFocused + " o=" + o);
            if (wasFocused) {
                // has just lost focus
                enable_or_disable_buttons();
            }
        });

        // handle changes to txtOfxDir when it looses focus so that a new value
        //  is updated into BankAcct.bankAcctOfxDir
        txtOfxDir.focusedProperty().addListener((ObservableValue<? extends Boolean> o, Boolean wasFocused, Boolean isNowFocused) -> {
//              System.out.println("txtOfxDir.focusedProperty has changed" +
//                  " oldVal=" + wasFocused + " newVal=" + isNowFocused + " o=" + o);
            if (wasFocused) {
                // has just lost focus
                getDatesFromFile();
                enable_or_disable_buttons();
            }
        });

        // handle changes to txtOfxFile when it looses focus
        txtOfxFile.focusedProperty().addListener((ObservableValue<? extends Boolean> o, Boolean wasFocused, Boolean isNowFocused) -> {
//              System.out.println("txtOfxFile.focusedProperty has changed" +
//                  " oldVal=" + wasFocused + " newVal=" + isNowFocused + " o=" + o);
            if (wasFocused) {
                // has just lost focus
                getDatesFromFile();
                enable_or_disable_buttons();
            }
        });

        // handle changes to txtDateFrom when it looses focus
        txtDateFrom.focusedProperty().addListener((ObservableValue<? extends Boolean> o, Boolean wasFocused, Boolean isNowFocused) -> {
            if (wasFocused) {
                // has just lost focus
                enable_or_disable_buttons();
            }
        });

        // handle changes to txtDateTo when it looses focus
        txtDateTo.focusedProperty().addListener((ObservableValue<? extends Boolean> o, Boolean wasFocused, Boolean isNowFocused) -> {
            if (wasFocused) {
                // has just lost focus
                enable_or_disable_buttons();
            }
        });


        if (boolPropDirOK) {
            if ((txtBankId.getText() == null) || (txtBankId.getText().isEmpty())) {
//                    System.out.println("initialize(): txtBankId.setText to " + getBaBankId());
                txtBankId.setText(baBankId);
            }
            if ((txtAcctNo.getText() == null) || (txtAcctNo.getText().isEmpty())) {
                txtAcctNo.setText(baNo);
            }
            if ((bankAcctTypeComboBox.getValue() == null) || (bankAcctTypeComboBox.getValue().equals(""))) {
                bankAcctTypeComboBox.setValue(baType);
            }

            if ((txtOfxDir.getText() == null) || (txtOfxDir.getText().equals(""))) {
                txtOfxDir.setText(baOfxDir);
            }

//                pathOfxDirFilStr = Paths.get(txtGcDatFilStr.getText());

            enable_or_disable_buttons();
        }
    }
}