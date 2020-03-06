/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

module ingausofxfix {
    requires javafx.controls;
    requires javafx.fxml;
    
    //requires java.util.logging.Level;
    requires java.logging;
    
    opens org.openjfx to javafx.fxml;
    exports org.openjfx;
}
