package Entry;

import API.Service;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class DashboardController {

    final Service ser = Service.getInstance();
    @FXML
    private TextFlow chatFlow;

    @FXML
    private TextFlow logsFlow;

    @FXML
    private Button upload;

    @FXML
    public void initialize() {

    }



    public void updateLogs(String log){
        logsFlow.getChildren().add(new Text(log));
    }


    public void updateChat(String msg){
        chatFlow.getChildren().add(new Text(msg));
    }

    public void uploadClicked(){


//            FileChooser fileChooser = new FileChooser();
//            fileChooser.setTitle("Upload Image");
//
//            FileChooser.ExtensionFilter imageFilter =
//                    new FileChooser.ExtensionFilter("Photos", "*.jpg", "*.jpeg", "*.png");
//
//            fileChooser.getExtensionFilters().add(imageFilter);
//
//            File file = fileChooser.showOpenDialog(upload.getScene().getWindow());
//
//            if(file != null){
//                System.out.println("Image uploaded: " + file.getAbsolutePath());
//                byte[] img = fileToByteArray(file);
//                MySQLservice.getInstance().addProfilePicture(1, img);
//            }

        Service.printClients();

    }

    private byte[] fileToByteArray(File file)  {
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }

            return bos.toByteArray();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

}