package com.loknitro.neo_resurgence;

import javafx.animation.*;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.AudioClip;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.Objects;
import java.util.Random;


public class TestController {

    double[] squareMoveSegments = new double[4];
    boolean horizontal = false;
    boolean squareDragEnabled = true;
    private final AudioClip errorSound = new AudioClip(Objects.requireNonNull(getClass().getResource("sounds/erou.mp3")).toExternalForm());
    private final AudioClip scoreSound = new AudioClip(Objects.requireNonNull(getClass().getResource("sounds/point.mp3")).toExternalForm());
    private final AudioClip circleHoldSound = new AudioClip(Objects.requireNonNull(getClass().getResource("sounds/circle_hold.wav")).toExternalForm());
    private boolean success = false;

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

    //TODO:Implement squareDragTimer
    private static final PauseTransition squareDragTimer = new PauseTransition(Duration.seconds(1.5));
    private static final PauseTransition circleHoldTimer = new PauseTransition(Duration.seconds(3));
    private static final PauseTransition blackScreenTimer = new PauseTransition(Duration.seconds(3));
    @FXML
    private AnchorPane innerPane;

    @FXML
    public void initialize() {
        positionShapesRandomly();
        circleHoldTimer.setOnFinished(e -> {
            success = true;
            scoreSound.play();
            score++;
            scoreLabel.setText("Pontos: " + score);
            positionShapesRandomly();
        });
    }
    //Circle behavior - start
    @FXML
    private void onCircleExit(MouseEvent event) {
        circle.setCursor(Cursor.DEFAULT);
            circleHoldSound.stop();
            if(event.isPrimaryButtonDown() && !success) {
                circleHoldTimer.stop();
                blackScreen();
            }
    }
    @FXML
    private void onCirclePressed() {
        circleHoldSound.play();
        success = false;
        circleHoldTimer.playFromStart();
    }
    @FXML
    private void onCircleReleased(Event event) {
        circleHoldSound.stop();
        circleHoldTimer.stop();
        if (!success) {
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
            scoreSound.play();
            score++;
            scoreLabel.setText("Pontos: " + score);
            positionShapesRandomly();
            squareDragEnabled = false;
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
        blackScreenTimer.setOnFinished(e -> {
            blackOverlay.setVisible(false);
            positionShapesRandomly();
        });
        blackScreenTimer.play();
    }



    private void positionShapesRandomly() {
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
}