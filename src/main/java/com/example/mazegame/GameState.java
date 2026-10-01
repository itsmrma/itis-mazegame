package com.example.mazegame;

import java.util.random.RandomGenerator;

/** Movement and game rules, independent of the JavaFX interface. */
final class GameState {
    static final int TILE_SIZE = 48;
    static final int HITBOX_SIZE = 30;
    static final int HITBOX_OFFSET = (TILE_SIZE - HITBOX_SIZE) / 2;
    static final double START_X = 376;
    static final double START_Y = 376;

    enum MoveResult { BLOCKED, MOVED, KEY_COLLECTED, LOCKED_EXIT, WON }

    private final Level level;
    private final Level.Cell key;
    private double playerX = START_X;
    private double playerY = START_Y;
    private boolean hasKey;
    private boolean won;

    GameState(Level level, RandomGenerator random) {
        this.level = level;
        key = level.floors().get(random.nextInt(level.floors().size()));
    }

    MoveResult move(double dx, double dy) {
        if (won || (dx == 0 && dy == 0)) {
            return MoveResult.BLOCKED;
        }
        double x = playerX + dx;
        double y = playerY + dy;
        if (x < 0 || y < 0 || x + TILE_SIZE > level.columns() * TILE_SIZE
                || y + TILE_SIZE > level.rows() * TILE_SIZE) {
            return MoveResult.BLOCKED;
        }
        for (int row = 0; row < level.rows(); row++) {
            for (int column = 0; column < level.columns(); column++) {
                if (level.tileAt(column, row) == Level.WALL && intersects(x, y, new Level.Cell(column, row))) {
                    return MoveResult.BLOCKED;
                }
            }
        }
        playerX = x;
        playerY = y;
        boolean collected = !hasKey && intersects(x, y, key);
        hasKey |= collected;
        if (intersects(x, y, level.exit())) {
            if (hasKey) {
                won = true;
                return MoveResult.WON;
            }
            playerX = START_X;
            playerY = START_Y;
            return MoveResult.LOCKED_EXIT;
        }
        return collected ? MoveResult.KEY_COLLECTED : MoveResult.MOVED;
    }

    private boolean intersects(double x, double y, Level.Cell cell) {
        double left = x + HITBOX_OFFSET;
        double top = y + HITBOX_OFFSET;
        double tileX = cell.column() * TILE_SIZE;
        double tileY = cell.row() * TILE_SIZE;
        return left < tileX + TILE_SIZE && left + HITBOX_SIZE > tileX
                && top < tileY + TILE_SIZE && top + HITBOX_SIZE > tileY;
    }

    double playerX() {
        return playerX;
    }

    double playerY() {
        return playerY;
    }

    Level.Cell key() {
        return key;
    }

    boolean hasKey() {
        return hasKey;
    }

    boolean won() {
        return won;
    }
}
