module com.TeamPingui {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;
    requires org.apache.logging.log4j;
    requires org.xerial.sqlitejdbc;
    requires java.prefs;

    opens com.teampingui.controllers to javafx.fxml;
    opens com.teampingui.models to javafx.fxml;
    opens com.teampingui to org.apache.logging.log4j;
    exports com.teampingui.controllers to javafx.fxml, org.apache.logging.log4j;
    exports com.teampingui.models;
    exports com.teampingui;
    exports com.teampingui.interfaces;
    exports com.teampingui.exceptions;
    exports com.teampingui.dao;
    opens com.teampingui.interfaces to javafx.fxml;
    opens com.teampingui.exceptions to javafx.fxml;
}
