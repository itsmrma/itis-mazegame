package com.example.mazegame;

import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

/** Draws the level, player, key and limited field of view. */
final class TileManager extends Group {
    private static final int CAMERA_SIZE = 150;
    private final List<Rectangle> fog = new ArrayList<>();
    private final Canvas keyCanvas;
    private final ImageView player;
    private boolean keyVisible = true;

    TileManager(Level level, GameState state) {
        int size = GameState.TILE_SIZE;
        Canvas terrain = new Canvas(level.columns() * size, level.rows() * size);
        keyCanvas = new Canvas(terrain.getWidth(), terrain.getHeight());
        GraphicsContext graphics = terrain.getGraphicsContext2D();
        Image ground = image("ground.jpg");
        Image wall = image("wall.png");
        Image door = image("door.png");

        for (int row = 0; row < level.rows(); row++) {
            for (int column = 0; column < level.columns(); column++) {
                Image tile = switch (level.tileAt(column, row)) {
                    case Level.WALL -> wall;
                    case Level.EXIT -> door;
                    default -> ground;
                };
                graphics.drawImage(tile, column * size, row * size, size, size);
                fog.add(new Rectangle(column * size, row * size, size, size));
            }
        }
        keyCanvas.getGraphicsContext2D().drawImage(image("key.png"),
                state.key().column() * size, state.key().row() * size, size, size);
        player = new ImageView(image("character.png"));
        player.setFitWidth(size);
        player.setFitHeight(size);
        player.setSmooth(false);

        getChildren().addAll(terrain, keyCanvas);
        getChildren().addAll(fog);
        getChildren().add(player);
        update(state);
    }

    void update(GameState state) {
        player.relocate(state.playerX(), state.playerY());
        if (state.hasKey() && keyVisible) {
            keyCanvas.getGraphicsContext2D().clearRect(0, 0, keyCanvas.getWidth(), keyCanvas.getHeight());
            keyVisible = false;
        }
        double cameraX = state.playerX() + GameState.TILE_SIZE / 2.0 - CAMERA_SIZE / 2.0;
        double cameraY = state.playerY() + GameState.TILE_SIZE / 2.0 - CAMERA_SIZE / 2.0;
        for (Rectangle tile : fog) {
            boolean visible = state.won()
                    || tile.getBoundsInParent().intersects(cameraX, cameraY, CAMERA_SIZE, CAMERA_SIZE);
            tile.setFill(visible ? Color.TRANSPARENT : Color.BLACK);
        }
    }

    private static Image image(String name) {
        return new Image(Resources.get("images/" + name).toExternalForm());
    }
}
