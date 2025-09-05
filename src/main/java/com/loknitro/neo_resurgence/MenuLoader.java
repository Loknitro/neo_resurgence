package com.loknitro.neo_resurgence;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx   .stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class MenuLoader extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Image icon = new Image(Objects.requireNonNull(MenuLoader.class.getResource("images/resurgence_icon.png")).toExternalForm());
        FXMLLoader fxmlLoader = new FXMLLoader(MenuLoader.class.getResource("main-menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.getIcons().add(icon);
        stage.setFullScreenExitHint("");
        stage.setFullScreenExitKeyCombination(KeyCombination.valueOf("Ctrl+p"));
        stage.setScene(scene);
        stage.setTitle("Recovery 1.0");
        stage.show();
    }
    public static void main(String[] args) { launch(args); }
}