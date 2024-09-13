module org.studyhawk {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;
    requires org.slf4j.simple;
    requires org.xerial.sqlitejdbc;
    requires java.desktop;
    requires jdk.jshell;

    opens org.studyhawk to javafx.fxml;
//    opens org.studyhawk.Controllers to javafx.fxml;
    exports org.studyhawk;
//    exports org.studyhawk.Controllers;
}