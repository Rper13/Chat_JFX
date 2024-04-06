module com.example.chat {
    requires javafx.controls;
    requires javafx.fxml;
    requires jasypt;
    requires java.desktop;


    opens Main to javafx.fxml;
    exports Main;
    opens Controllers to javafx.fxml;
    exports Controllers;
}