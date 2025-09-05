package com.loknitro.neo_resurgence;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
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
    private boolean circleExit;
    public void setRng(boolean rng) {
        isRng = rng;
    }

    private final Random rand = new Random();
    private boolean onR1;
    private boolean onR2;
    private boolean onRT;
    boolean isRng;
    int r1ActionCounter = 0;
    int r2ActionCounter = 0;

    int rtActionCounter = 0;

    int r1drawn = 0;
    int r2drawn = 0;
    double[] squareMoveSegments = new double[4];
    boolean horizontal = false;
    boolean squareDragEnabled = false;
    private final AudioClip errorSound = new AudioClip(Objects.requireNonNull(getClass().getResource("sounds/erou.mp3")).toExternalForm());
    private final AudioClip scoreSound = new AudioClip(Objects.requireNonNull(getClass().getResource("sounds/point.mp3")).toExternalForm());
    private final AudioClip circleHoldSound = new AudioClip(Objects.requireNonNull(getClass().getResource("sounds/circle_hold.wav")).toExternalForm());
    private boolean circleSuccess = false;
    private boolean squareSuccess = false;
    private boolean squareHoldExceeded = false;
    private static final PauseTransition latencyTimer = new PauseTransition(Duration.minutes(10));

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
    private static final PauseTransition squareDragTimer = new PauseTransition(Duration.seconds(3));
    private static final PauseTransition circleHoldTimer = new PauseTransition(Duration.seconds(1.8));
    private static final PauseTransition blackScreenTimer = new PauseTransition(Duration.seconds(1));
    @FXML
    private AnchorPane innerPane;

    @FXML
    public void initialize() {
        actionsTaken = new ArrayList<>();
        resurgenceDistribution();

        latencyTimer.play();

        positionShapesRandomly();
        blackScreenTimer.setOnFinished(e -> {
            blackOverlay.setVisible(false);
            positionShapesRandomly();
        });
        circleHoldTimer.setOnFinished(e -> {
            if(onR2 || onRT) {
                circleSuccess = true;
                writeAction("Círculo", "Pressionar", "Erro", "1", "0");
                registerAction();
                blackScreen();
            }
            else {
                circleSuccess = true;
                score();
                writeAction("Círculo", "Pressionar", "Acerto", "1", "0");
                registerAction();
                positionShapesRandomly();
            }
        });
        squareDragTimer.setOnFinished(e -> {
            if (!squareSuccess) {
                squareHoldExceeded = true;
                squareDragEnabled = false;
                writeAction("Quadrado", "Arrastar Insf.", "Erro", "0", "1");
                registerAction();
                blackScreen();
            }
        });
        testTimer.playFromStart();
    }
    //Circle behavior - start
    @FXML
    private void onCirclePressed() {
        circleExit = false;
        circleSuccess = false;
        circleHoldSound.play();
        circleHoldTimer.playFromStart();
    }
    @FXML
    private void onCircleExit(MouseEvent event) {
        circle.setCursor(Cursor.DEFAULT);
        circleHoldSound.stop();
        if(event.isPrimaryButtonDown() && !circleSuccess) {
            circleExit = true;
            circleHoldTimer.stop();
            writeAction("Círculo", "Pressionar Insf.", "Erro", "1", "0");
            registerAction();
            blackScreen();
        }
    }
    @FXML
    private void onCircleReleased(MouseEvent event) {
        circleHoldSound.stop();
        circleHoldTimer.stop();
        if (!circleSuccess && !circleExit) {
            writeAction("Círculo", "Pressionar Insf.", "Erro", "1", "0");
            registerAction();
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
            squareDragTimer.stop();
            if (onR1 || onRT) {
                squareSuccess = true;
                squareDragEnabled = false;
                writeAction("Quadrado", "Arrastar", "Erro", "0", "1");
                registerAction();
                blackScreen();
            } else {
                score();
                squareDragEnabled = false;
                squareSuccess = true;
                writeAction("Quadrado", "Arrastar", "Acerto", "0", "1");
                registerAction();
                positionShapesRandomly();
            }
        }
    }
    @FXML
    private void onSquareReleased(MouseEvent event) {
        squareDragTimer.stop();
        if(!squareSuccess && !squareHoldExceeded) {
            writeAction("Quadrado", "Arrastar Insf.", "Erro", "0", "1");
            registerAction();
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
        if(event.getTarget() instanceof Circle || event.getTarget() instanceof Rectangle || blackOverlay.isVisible()) {
            return;
        }
        writeAction("Tela", "Toque", "Erro", "0", "0");
        registerAction();
        blackScreen();
    }

    private void positionShapesRandomly() {
        square.setTranslateX(0);
        square.setTranslateY(0);
        if (rtActionCounter == 12) {
            actionsTaken.add("");
            actionsTaken.add(String.format("Teste concluído em %02d:%05.2f minutos.",(int)testTimer.getCurrentTime().toMinutes(), testTimer.getCurrentTime().toSeconds()));
            testTimer.stop();
            try {

                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("result-screen.fxml")));
                Scene scene = new Scene(loader.load());
                ResultScreenController resultScreenController = loader.getController();
                resultScreenController.setData(actionsTaken);
                Stage stage = (Stage) innerPane.getScene().getWindow();

                stage.setScene(scene);
                stage.setFullScreen(true);
                stage.setResizable(false);
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
        double extraDistance = square.getWidth()/2;

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

    private void registerAction() {
        resurgenceCounter();
        resurgenceDistribution();
    }

    private void writeAction(String object, String interaction, String result, String s1, String s2) {
        String latency = String.format("%05.2f",latencyTimer.getCurrentTime().toSeconds());
        latencyTimer.stop();
        latencyTimer.playFromStart();
        String time = String.format("%02d:%05.2f",(int)testTimer.getCurrentTime().toMinutes(), testTimer.getCurrentTime().toSeconds());
        String phase;
        if (onR1) {
            phase = "R1";
        }else if (onR2) {
            phase = "R2";
        }else
            phase = "RT";
        actionsTaken.add(String.format("%s;%s;%s;%s;%s;%s;%s;%s", phase, object, interaction, time, latency, result, s1, s2));
    }

    private void resurgenceDistribution() {
        //unpredictable
        if (isRng) {
            if (r1ActionCounter == 12 && r2ActionCounter == 12) {
                onR1 = false;
                onR2 = false;
                onRT = true;
                return;
            }

            if (r1ActionCounter == 12 && r2ActionCounter < 12) {
                onR1 = false;
                onR2 = true;
                return;
            }
            if (r2ActionCounter == 12 && r1ActionCounter < 12) {
                onR1 = true;
                onR2 = false;
                return;
            }

            int probability = 40 + r1drawn * 10 - r2drawn * 10;
            int rng = rand.nextInt(81);

            if (rng > probability ) {
                r2drawn = 0;
                r1drawn++;
                onR1 = true;
                onR2 = false;
            }
            else {
                r1drawn = 0;
                r2drawn++;
                onR1 = false;
                onR2 = true;
            }
        }
        //predictable
        else {
            if (r1ActionCounter == 12 && r2ActionCounter == 12) {
                onR1 = false;
                onR2 = false;
                onRT = true;
            } else if (r1ActionCounter == 12 && r2ActionCounter < 12) {
                onR1 = false;
                onR2 = true;
                onRT = false;
            } else {
                onR1 = true;
                onR2 = false;
                onRT = false;
            }
        }
    }

    private void resurgenceCounter () {
        if (onR1) {
            r1ActionCounter++;
        }
        else if (onR2) {
            r2ActionCounter++;
        }
        else
            rtActionCounter++;
    }
}