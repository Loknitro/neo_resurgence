package com.loknitro.neo_resurgence;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.Objects;

public class MainMenuController {
    private boolean isRng;
    @FXML
    private Button r1Button;
    @FXML
    private Button r2Button;

    @FXML
    private Button nextScreenButton;
    @FXML
    private AnchorPane parentPane;

    @FXML
    private BorderPane mainMenu;

    @FXML
    private BorderPane introduction;

    @FXML
    void nextScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("resurgence-test.fxml")));
            Scene scene = new Scene(loader.load());
            ResurgenceTrialController testController = loader.getController();
            testController.setRng(isRng);
            Stage stage = (Stage) parentPane.getScene().getWindow();

            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.setResizable(false);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void r1ButtonOnAction() {
        isRng  = true;
        mainMenu.setVisible(false);
        introduction.setVisible(true);
    }
    @FXML
    void r2ButtonOnAction() {
        isRng  = false;
        mainMenu.setVisible(false);
        introduction.setVisible(true);
    }
}
