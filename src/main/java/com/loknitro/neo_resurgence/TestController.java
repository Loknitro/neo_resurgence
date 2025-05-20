package com.loknitro.neo_resurgence;

import javafx.animation.*;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
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

    double[] init = new double[4];
    boolean horizontal = false;
    boolean squareDragEnabled = true;
    boolean squareDirectionDefined = false;
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
        init[0] = event.getSceneX();
        init[1] = event.getSceneY();
        init[2] = square.getTranslateX();
        init[3] = square.getTranslateY();
        horizontal = false;
        squareDragEnabled = true;
        squareDirectionDefined = false;
    }
    @FXML
    private void onSquareDrag(MouseEvent event) {
        if(!squareDragEnabled) {return;}
        double dx = event.getSceneX() - init[0];
        double dy = event.getSceneY() - init[1];

        if(Math.abs(dx) > Math.abs(dy)) {
            horizontal = true;
        }

        if(horizontal) {
            square.setTranslateX(init[2] + dx);
        }
        else {
            square.setTranslateY(init[3] + dy);
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
        double areaWidth = innerPane.getPrefWidth();
        double areaHeight = innerPane.getPrefHeight();
        double circleRadius = circle.getRadius();
        double squareSize = square.getWidth();




        double circleX;
        double circleY;
        double squareX;
        double squareY;
        //do {
            circleX = random.nextDouble() * (areaWidth - circleRadius) + circleRadius;
            circleY = random.nextDouble() * (areaHeight - circleRadius) + circleRadius;
            squareX = random.nextDouble() * (areaWidth - squareSize);
            squareY = random.nextDouble() * (areaHeight - squareSize);

        //} while (isOverlapping(circleX, circleY, circleRadius, squareX, squareY, squareSize));

        circle.setLayoutX(circleX);
        circle.setLayoutY(circleY);
        square.setLayoutX(squareX);
        square.setLayoutY(squareY);
    }

    private boolean isOverlapping(double cx, double cy, double cr, double sx, double sy, double ss) {
        //Calculates minimum distance between shapes to never overlap
//        double sqCenterX = sx + ss / 2;
//        double sqCenterY = sy + ss / 2;
//
//        double dx = cx - sqCenterX;
//        double dy = cy - sqCenterY;
//        double distance = Math.sqrt(dx * dx + dy * dy);
//        double minDistance = cr + ss;
//
//        return distance < minDistance;
    }
}