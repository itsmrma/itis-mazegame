package com.example.mazegame;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;

/** Application entry point, menu, keyboard input and animation loop. */
public final class MazeGameApplication extends Application {
    private static final double MOVEMENT_SPEED = 180; // Pixels per second.
    private final Set<KeyCode> pressedKeys = EnumSet.noneOf(KeyCode.class);
    private AnimationTimer timer;
    private GameAudio audio;

    @Override
    public void start(Stage stage) {
        Button play = new Button("MAPPA 1");
        play.setOnAction(event -> startGame(stage));
        ImageView preview = new ImageView(new Image(Resources.get("images/map1.png").toExternalForm()));
        HBox menu = new HBox(play, preview);
        menu.setAlignment(Pos.TOP_LEFT);
        stage.setTitle("Harry Potter e il calice di fuoco");
        stage.setResizable(false);
        stage.setScene(new Scene(menu));
        stage.focusedProperty().addListener((observable, wasFocused, isFocused) -> {
            if (!isFocused) {
                pressedKeys.clear();
            }
        });
        stage.show();
    }

    private void startGame(Stage stage) {
        try {
            Level level = Level.load("levels/1.txt");
            GameState state = new GameState(level, new Random());
            TileManager view = new TileManager(level, state);
            Label fps = new Label("FPS: 0");
            fps.setTextFill(Color.WHITE);
            view.getChildren().add(fps);
            Scene scene = new Scene(view, level.columns() * GameState.TILE_SIZE,
                    level.rows() * GameState.TILE_SIZE, Color.BLACK);
            scene.setOnKeyPressed(event -> pressedKeys.add(event.getCode()));
            scene.setOnKeyReleased(event -> pressedKeys.remove(event.getCode()));
            stage.setScene(scene);
            stage.sizeToScene();
            pressedKeys.clear();
            audio = new GameAudio();
            audio.playMusic();

            timer = new AnimationTimer() {
                private long lastFrame;

                @Override
                public void handle(long now) {
                    if (lastFrame == 0) {
                        lastFrame = now;
                        return;
                    }
                    long delta = now - lastFrame;
                    lastFrame = now;
                    // Cap long pauses to avoid jumping through walls when resuming.
                    double distance = MOVEMENT_SPEED * Math.min(delta / 1_000_000_000.0, 0.05);
                    double dx = direction(KeyCode.D, KeyCode.A) * distance;
                    double dy = direction(KeyCode.S, KeyCode.W) * distance;
                    GameState.MoveResult result = state.move(dx, dy);
                    view.update(state);
                    fps.setText("FPS: " + (delta > 0 ? 1_000_000_000L / delta : 0));

                    if (result == GameState.MoveResult.KEY_COLLECTED) {
                        audio.playKeyEffect();
                    } else if (result == GameState.MoveResult.LOCKED_EXIT) {
                        pressedKeys.clear();
                        stop();
                        showMessage(stage, Alert.AlertType.ERROR, "HAI BISOGNO DI UNA CHIAVE!");
                        lastFrame = 0;
                        start();
                    } else if (result == GameState.MoveResult.WON) {
                        stop();
                        showMessage(stage, Alert.AlertType.INFORMATION, "HAI VINTO!");
                        stage.close();
                    }
                }
            };
            timer.start();
        } catch (IOException | RuntimeException exception) {
            exception.printStackTrace();
            showMessage(stage, Alert.AlertType.ERROR, "Impossibile caricare il gioco: " + exception.getMessage());
        }
    }

    private int direction(KeyCode positive, KeyCode negative) {
        return (pressedKeys.contains(positive) ? 1 : 0) - (pressedKeys.contains(negative) ? 1 : 0);
    }

    private static void showMessage(Stage stage, Alert.AlertType type, String text) {
        Alert alert = new Alert(type);
        alert.initOwner(stage);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    @Override
    public void stop() {
        if (timer != null) {
            timer.stop();
        }
        if (audio != null) {
            audio.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
