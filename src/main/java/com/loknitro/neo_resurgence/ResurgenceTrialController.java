package com.loknitro.neo_resurgence;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;


public class ResurgenceTrialController {
    private int actionCounter = 1;
    double[] squareMoveSegments = new double[4];
    boolean horizontal = false;
    boolean squareDragEnabled = true;
    private final AudioClip errorSound = new AudioClip(Objects.requireNonNull(getClass().getResource("sounds/erou.mp3")).toExternalForm());
    private final AudioClip scoreSound = new AudioClip(Objects.requireNonNull(getClass().getResource("sounds/point.mp3")).toExternalForm());
    private final AudioClip circleHoldSound = new AudioClip(Objects.requireNonNull(getClass().getResource("sounds/circle_hold.wav")).toExternalForm());
    private boolean circleSuccess = false;
    private boolean squareSuccess = false;
    private boolean squareHoldExceeded = false;

    @FXML
    private AnchorPane blackOverlay;

    @FXML
    private Rectangle square;

    @FXML
    private Circle circle;

    @FXML
    private Label scoreLabel;
    private final Random random = new Random();
    private int score = 0;
    private List<String> actionsTaken;

    private static final PauseTransition testTimer = new PauseTransition(Duration.minutes(30));
    private static final PauseTransition squareDragTimer = new PauseTransition(Duration.seconds(1));
    private static final PauseTransition circleHoldTimer = new PauseTransition(Duration.seconds(3));
    private static final PauseTransition blackScreenTimer = new PauseTransition(Duration.seconds(3));
    @FXML
    private AnchorPane innerPane;

    @FXML
    public void initialize() {
        actionsTaken = new ArrayList<>();
        positionShapesRandomly();
        blackScreenTimer.setOnFinished(e -> {
            blackOverlay.setVisible(false);
            positionShapesRandomly();
        });
        circleHoldTimer.setOnFinished(e -> {
            registerAction(true, "Círculo", "Sucesso normal.");
            circleSuccess = true;
            score();
            positionShapesRandomly();
        });
        squareDragTimer.setOnFinished(e -> {
            if (!squareSuccess) {
                squareHoldExceeded = true;
                squareDragEnabled = false;
                blackScreen();
            }
        });
        testTimer.play();
    }
    //Circle behavior - start
    @FXML
    private void onCircleExit(MouseEvent event) {
        circle.setCursor(Cursor.DEFAULT);
        circleHoldSound.stop();
        if(event.isPrimaryButtonDown() && !circleSuccess) {
            registerAction(false, "Círculo", "Tentou arrastar o círculo (comportamento do quadrado).");
            circleHoldTimer.stop();
            blackScreen();
        }
    }
    @FXML
    private void onCirclePressed() {
        if (actionCounter >= 27) {
           actionCounter++;
           registerAction(false, "Círculo", "Tentou interagir com Círculo em R3.");
           blackScreen();
           return;
        }
        if(actionCounter >= 13) {
            actionCounter++;
            registerAction(false, "Círculo", "Tentou interagir com Círculo em R2.");
            blackScreen();
            return;
        }
        actionCounter++;
        circleHoldSound.play();
        circleSuccess = false;
        circleHoldTimer.playFromStart();
    }
    @FXML
    private void onCircleReleased(MouseEvent event) {
        circleHoldSound.stop();
        circleHoldTimer.stop();
        if (!circleSuccess) {
            registerAction(false, "Círculo", "Soltou antes do tempo.");
            blackScreen();
        }
    }
    @FXML
    private void onCircleEnter() {
        circle.setCursor(Cursor.HAND);
    }
    //Circle behavior - end

    //Square behavior - start
    @FXML
    private void onSquareEnter() {
        square.setCursor(Cursor.MOVE);
    }
    @FXML
    private void onSquareExit() {
        square.setCursor(Cursor.DEFAULT);
    }
    @FXML
    private void onSquarePressed(MouseEvent event) {
        if (actionCounter < 13) {
            actionCounter++;
            registerAction(false, "Quadrado", "Tentou interagir com Quadrado em R1.");
            blackScreen();
            return;
        }
        if(actionCounter >= 27) {
            actionCounter++;
            registerAction(false, "Quadrado", "Tentou interagir com Quadrado em R3.");
            blackScreen();
            return;
        }
        actionCounter++;
        squareHoldExceeded = false;
        squareSuccess = false;
        squareDragTimer.playFromStart();
        squareMoveSegments[0] = event.getSceneX();
        squareMoveSegments[1] = event.getSceneY();
        squareMoveSegments[2] = square.getTranslateX();
        squareMoveSegments[3] = square.getTranslateY();
        horizontal = false;
        squareDragEnabled = true;
    }
    @FXML
    private void onSquareDrag(MouseEvent event) {
        if(!squareDragEnabled) {return;}
        double dx = event.getSceneX() - squareMoveSegments[0];
        double dy = event.getSceneY() - squareMoveSegments[1];

        if(Math.abs(dx) > Math.abs(dy)) {
            horizontal = true;
        }

        if(horizontal) {
            square.setTranslateX(squareMoveSegments[2] + dx);
        }
        else {
            square.setTranslateY(squareMoveSegments[3] + dy);
        }

        double dist = horizontal ? Math.abs(dx) : Math.abs(dy);

        if (dist >= square.getWidth()) {
            score();
            positionShapesRandomly();
            squareDragEnabled = false;
            squareSuccess = true;
            registerAction(true, "Quadrado", "Sucesso normal.");
        }
    }
    @FXML
    private void onSquareReleased() {
        squareDragTimer.stop();
        if(!squareSuccess && !squareHoldExceeded) {
            if (Math.abs(square.getTranslateX()) >= 15 || Math.abs(square.getTranslateY()) >= 15) {
                registerAction(false, "Quadrado", "Não arrastou o suficiente.");
            }
            else
                registerAction(false, "Quadrado", "Tentou segurar o quadrado(comportamento do círculo).");
            blackScreen();
        }
    }
    //Square behavior - end
    @FXML
    private void blackScreen() {
        if(blackOverlay.isVisible()) {
            return;
        }
        errorSound.play();
        blackOverlay.setVisible(true);
        blackScreenTimer.play();
    }

    @FXML
    private void onOutOfBoundsClick(MouseEvent event) {
        if(event.getTarget() instanceof Circle || event.getTarget() instanceof Rectangle) {
            return;
        }
        registerAction(false, "Tela", "Clique fora de objetos interativos.");
        blackScreen();
    }

    private void positionShapesRandomly() {
        square.setTranslateX(0);
        square.setTranslateY(0);
        if (actionCounter == 37) {
            try {
                Parent resultRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("result-screen.fxml")));
                Stage stage = (Stage) innerPane.getScene().getWindow();
                stage.setScene(new javafx.scene.Scene(resultRoot));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        double areaWidth = innerPane.getMaxWidth();
        double areaHeight = innerPane.getMaxHeight();
        double circleRadius = circle.getRadius();
        double squareSize = square.getWidth();

        double circleX;
        double circleY;
        double squareX;
        double squareY;

        do {
            circleX = random.nextDouble() * (areaWidth - circleRadius) + circleRadius;
            circleY = random.nextDouble() * (areaHeight - circleRadius) + circleRadius;
            squareX = random.nextDouble() * (areaWidth - squareSize);
            squareY = random.nextDouble() * (areaHeight - squareSize);
        } while (isAtCorrectDistance(circleX, squareX));

        circle.setLayoutX(circleX);
        circle.setLayoutY(circleY);
        square.setLayoutX(squareX);
        square.setLayoutY(squareY);

    }

    private boolean isAtCorrectDistance(double cx, double sx) {
        double extraDistance = 25; //Maybe a good option for customization later.

        //These gaps reference the distance based on the shapes sizes and their position
        double positiveGap = square.getWidth() * 2 + extraDistance - circle.getRadius();
        double negativeGap = square.getWidth() * -2 - extraDistance - circle.getRadius();

        boolean checkOne =  sx - cx <= negativeGap;
        boolean checkTwo = sx - cx  >= positiveGap;

        return checkOne == checkTwo;
    }

    private void score() {
        scoreSound.play();
        score++;
        scoreLabel.setText("Pontos: " + score);
    }

    private void registerAction(boolean success, String object, String description) {
        actionsTaken.add(String.format("%s - Objeto: %s. Ação tomada em %.2f segundos: %s", success ? "ACERTO" : "ERRO", object, testTimer.getCurrentTime().toSeconds(), description));
    }
}