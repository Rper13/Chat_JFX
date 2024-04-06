package Controllers;

import API.APIservice;
import Creators.SceneBuilder;
import GlobalScope.Lockers;
import GlobalScope.Navigation;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginPageController {

    public static String response = null; //to modify from APIservice

    private final Object lock = new Object();

    @FXML
    private TextField userNameField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private Circle loginIconCircle;

    @FXML
    private Circle passwordIconCircle;

    private Stage currentStage;

    public void setStage(Stage currentStage){
        this.currentStage = currentStage;
    }

    @FXML
    public void initialize(){

        loginIconCircle.setFill(
                new ImagePattern(
                        new Image(
                                LoginPageController.class.getResource("/Images/login.png").toExternalForm())));
        passwordIconCircle.setFill(
                new ImagePattern(
                        new Image(
                                LoginPageController.class.getResource("/Images/password.png").toExternalForm())));
        APIservice.Connect();
    }

    @FXML
    private void LogInButton_Pressed() {

        APIservice.sendLoginRequest(userNameField.getText(), passwordField.getText());

        try {
            synchronized (Lockers.login_lock) {
                while (response == null) {
                    Lockers.login_lock.wait();
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Locking error while waiting for login: " + e.getMessage());
        }


        if(response.equals("Success")){

            APIservice.sendUserInfoRequest(userNameField.getText());
            Navigation.goToHomePage(currentStage);

        }
        else{
            new Alert(Alert.AlertType.ERROR, "Wrong Credentials");
        }

    }

    @FXML
    private void RegisterButton_Pressed() throws IOException {

        currentStage.hide();

        SceneBuilder sceneBuilder =
                new SceneBuilder(LoginPageController.class.getResource("register-page.fxml")).
                        setSize(400,600);

        Stage registerStage = new Stage();
        registerStage.setScene(sceneBuilder.build());

        RegisterPageController controller = (RegisterPageController) sceneBuilder.getController();
        controller.setStages(registerStage, currentStage);

        registerStage.show();
    }

    @FXML
    private void LogInField_Action(){
        if(passwordField.getText().length() > 0){
            LogInButton_Pressed();
        }
        else{
            passwordField.requestFocus();
        }

    }


}