module com.example.serverapi {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens Entry to javafx.fxml;
    exports Entry;
}