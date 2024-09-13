package org.studyhawk;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.studyhawk.Controllers.MainController;

import java.io.IOException;

public class App extends Application {

    public void start(Stage stage) throws IOException {

//        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Views/MainView.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 1520, 720);
//        stage.setTitle("StudyHawk");
//        stage.getIcons().add(new Image(String.valueOf(Main.class.getResource("Images/icon.png"))));
//        stage.setScene(scene);

//        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }

}
