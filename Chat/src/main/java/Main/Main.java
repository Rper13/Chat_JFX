package Main;

import API.APIservice;
import Controllers.LoginPageController;
import Creators.SceneBuilder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static boolean running = true;

    @Override
    public void start(Stage stage) throws IOException {

        SceneBuilder sceneBuilder = new SceneBuilder(LoginPageController.class.getResource("login-page.fxml"))
                .setSize(600,400);
        Scene scene = sceneBuilder.build();

        LoginPageController controller = (LoginPageController) sceneBuilder.getController();
        controller.setStage(stage);

        stage.setOnCloseRequest(event -> {
            System.out.println("Closing application...");
            running = false;
            APIservice.closeSocket();
            Platform.exit();
        });

        stage.setTitle("Welcome to login page");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}