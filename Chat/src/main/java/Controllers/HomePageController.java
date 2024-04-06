package Controllers;

import API.APIservice;
import GlobalScope.FXfunctions;
import GlobalScope.Functions;
import Objects.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;

public class HomePageController {

    @FXML
    private ScrollPane chatScrollPane;
    @FXML
    private TextFlow chatTextFlow;

    @FXML
    private TextField messageTextField;

    @FXML
    private Circle userProfileCircle;

    @FXML
    private GridPane homePageGrid;
    @FXML
    private Button uploadPhotoButton;

    @FXML
    private Label nameLabel;
    @FXML
    private Label lastNameLabel;
    @FXML
    private Label usernameLabel;



    @FXML
    public void initialize(){

        try {
            APIservice.requestProfilePicture();

            userProfileCircle.getStyleClass().add("profile-pictures");

            nameLabel.setText(User.getInstance().getName());
            lastNameLabel.setText(User.getInstance().getLast_name());
            usernameLabel.setText(User.getInstance().getUsername());

        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    public void fillProfileCircle(byte[] imageBytes){

        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        Image image = new Image(bis);
        userProfileCircle.setFill(new ImagePattern(image));
    }
    @FXML
    private void uploadPhotoButtonClicked(){

        File image = FXfunctions.uploadImage((Stage) uploadPhotoButton.getScene().getWindow());

        if(image == null) return;

        byte[] img = Functions.fileToByteArray(image);
        APIservice.updateProfilePicture(img);

        fillProfileCircle(img);
    }

    @FXML
    private void messageTextFieldAction(){


        if(messageTextField.getText().equals("")) return;

        APIservice.sendMessage(User.getInstance().getId() + "," + messageTextField.getText());
        //Pair<String, byte[]> received = APIservice.receiveMessage();

//        byte[] pic = Functions.processPicture(received.getValue());
//
//        updateChatFlow(received.getKey(), pic);

        messageTextField.setText("");
    }

    public void updateChatFlow(String message, byte[] picture){

        ImagePattern imagePattern = new ImagePattern(new Image(new ByteArrayInputStream(picture))); //getting image for circle

        Circle profPic = new Circle(10, imagePattern); //getting circle to display picture in chat

        Text text = new Text(message); //text itself

        HBox hBox = new HBox(profPic, text); //add both in HBox to be on the same liens


        chatTextFlow.getChildren().addAll(hBox, new Text("\n"));

        Platform.runLater(() -> {
            chatScrollPane.setVvalue(1.0);
        });

    }

}
