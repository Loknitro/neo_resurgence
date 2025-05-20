package com.loknitro.neo_resurgence;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx   .stage.Stage;
import java.io.IOException;

public class ResurgenceTrial extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Image icon = new Image(ResurgenceTrial.class.getResource("images/resurgence_icon.png").toExternalForm());
        FXMLLoader fxmlLoader = new FXMLLoader(ResurgenceTrial.class.getResource("resurgence-test.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.getIcons().add(icon);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.valueOf("Ctrl+e"));
        stage.setScene(scene);
        stage.setTitle("");
        stage.show();
    }
    public static void main(String[] args) { launch(args); }
}