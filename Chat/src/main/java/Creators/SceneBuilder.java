package Creators;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.URL;

public class SceneBuilder {

    private final FXMLLoader fxmlLoader;
    private double width = 400;
    private double height = 600;
    private String CSSpath;

    public SceneBuilder(URL fxml){

        fxmlLoader = new FXMLLoader(fxml);
       }

    public SceneBuilder setSize(double width, double height){
        this.width = width;
        this.height = height;
        return this;
    }

    public SceneBuilder setCSS(String path){
        CSSpath = path;
        return this;
    }

    public Scene build() throws IOException {
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        if (CSSpath != null && !CSSpath.isEmpty()) {
            String cssUrl = getClass().getResource(CSSpath).toExternalForm();
            scene.getStylesheets().add(cssUrl);
        }
        return scene;
    }

    public Object getController(){
        return fxmlLoader.getController();
    }
}
