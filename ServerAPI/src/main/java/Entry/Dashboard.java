package Entry;

import API.Service;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Dashboard extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Service ser = Service.getInstance();
        ser.startServer();

        stage.setOnCloseRequest(event -> {
            System.out.println("Closing application...");
            ser.stop();
            Platform.exit();
        });

        FXMLLoader fxmlLoader = new FXMLLoader(Dashboard.class.getResource("Dashboard.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Server side output of the program");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}