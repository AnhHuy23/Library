module ui.newui {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive java.sql;
    requires transitive com.jfoenix;
    requires java.desktop;
    requires com.google.gson;
    requires javafx.graphics;
    requires javafx.base;
    requires okhttp3;
    requires jakarta.mail;
    opens ui.newui to javafx.fxml;
    exports ui.newui;
    exports Models;
    exports Utils;
    exports App;
    exports Interface;
}