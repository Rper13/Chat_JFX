package GlobalScope;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class FXfunctions {

    /** Uploads image from file chooser (.jpg, .jpeg, .png).
     * @param stage current stage, where the filechooser is opening.
     * @return returns the uploaded file if it is image.
     */
    public static File uploadImage(Stage stage){

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Upload Image");

        FileChooser.ExtensionFilter imageFilter =
                new FileChooser.ExtensionFilter("Photos", "*.jpg", "*.jpeg", "*.png");

        fileChooser.getExtensionFilters().add(imageFilter);

        File file = fileChooser.showOpenDialog(stage);

        if(file != null){
            System.out.println("\nImage uploaded: " + file.getAbsolutePath() + "\n");
            return file;
        }

        return null;
    }

}
