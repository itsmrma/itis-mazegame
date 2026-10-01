package com.example.mazegame;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {
    private final Level level = Level.load("levels/1.txt");

    GameStateTest() throws IOException {
    }

    @Test
    void wallsBlockMovementWithoutChangingPosition() {
        GameState state = stateWithKey(new Level.Cell(1, 1));
        assertEquals(GameState.MoveResult.BLOCKED, state.move(-3, 0));
        assertEquals(GameState.START_X, state.playerX());
        assertEquals(GameState.START_Y, state.playerY());
        assertEquals(GameState.MoveResult.MOVED, state.move(3, 0));
        assertEquals(GameState.START_X + 3, state.playerX());
    }

    @Test
    void screenBoundariesBlockMovement() {
        GameState state = stateWithKey(new Level.Cell(1, 1));
        assertEquals(GameState.MoveResult.BLOCKED, state.move(-1000, 0));
        assertEquals(GameState.MoveResult.BLOCKED, state.move(1000, 0));
        assertEquals(GameState.MoveResult.BLOCKED, state.move(0, -1000));
        assertEquals(GameState.MoveResult.BLOCKED, state.move(0, 1000));
        assertEquals(GameState.START_X, state.playerX());
        assertEquals(GameState.START_Y, state.playerY());
    }

    @Test
    void exitWithoutKeyReturnsPlayerToStartAndLeavesKeyAvailable() {
        GameState state = stateWithKey(new Level.Cell(1, 1));
        alignToGrid(state);
        assertEquals(GameState.MoveResult.LOCKED_EXIT, walk(state, new Level.Cell(8, 8), level.exit()));
        assertFalse(state.hasKey());
        assertFalse(state.won());
        assertEquals(GameState.START_X, state.playerX());
        assertEquals(GameState.START_Y, state.playerY());
        alignToGrid(state);
        walk(state, new Level.Cell(8, 8), state.key());
        assertTrue(state.hasKey());
    }

    @Test
    void keyIsCollectedOnlyOnce() {
        GameState state = stateWithKey(new Level.Cell(9, 8));
        alignToGrid(state);
        int collections = 0;
        for (int step = 0; step < 16; step++) {
            if (state.move(3, 0) == GameState.MoveResult.KEY_COLLECTED) {
                collections++;
            }
        }
        assertEquals(1, collections);
        assertTrue(state.hasKey());
        assertEquals(GameState.MoveResult.MOVED, state.move(-3, 0));
    }

    @Test
    void everyPossibleKeyLocationAllowsWinningTheOriginalMaze() {
        for (Level.Cell key : level.floors()) {
            GameState state = stateWithKey(key);
            alignToGrid(state);
            walk(state, new Level.Cell(8, 8), key);
            assertTrue(state.hasKey(), "Cannot collect key at " + key);
            assertEquals(GameState.MoveResult.WON, walk(state, key, level.exit()), "Cannot win with key at " + key);
            assertTrue(state.won());
            assertEquals(GameState.MoveResult.BLOCKED, state.move(-3, 0));
        }
    }

    private GameState stateWithKey(Level.Cell key) {
        int index = level.floors().indexOf(key);
        assertTrue(index >= 0);
        return new GameState(level, new Random() {
            @Override
            public int nextInt(int bound) {
                return index;
            }
        });
    }

    private void alignToGrid(GameState state) {
        assertNotEquals(GameState.MoveResult.BLOCKED, state.move(8, 8));
    }

    /** Follows an actual route using the same small movement steps as the UI. */
    private GameState.MoveResult walk(GameState state, Level.Cell from, Level.Cell target) {
        GameState.MoveResult result = GameState.MoveResult.MOVED;
        List<Level.Cell> path = path(from, target);
        for (int index = 1; index < path.size(); index++) {
            Level.Cell previous = path.get(index - 1);
            Level.Cell next = path.get(index);
            for (int step = 0; step < 16; step++) {
                result = state.move((next.column() - previous.column()) * 3, (next.row() - previous.row()) * 3);
                assertNotEquals(GameState.MoveResult.BLOCKED, result, "Movement blocked towards " + next);
                if (result == GameState.MoveResult.WON || result == GameState.MoveResult.LOCKED_EXIT) {
                    return result;
                }
            }
        }
        return result;
    }

    private List<Level.Cell> path(Level.Cell from, Level.Cell target) {
        ArrayDeque<Level.Cell> queue = new ArrayDeque<>();
        Map<Level.Cell, Level.Cell> previous = new HashMap<>();
        queue.add(from);
        previous.put(from, null);
        while (!queue.isEmpty() && !previous.containsKey(target)) {
            Level.Cell cell = queue.remove();
            for (int[] direction : new int[][] { {1, 0}, {-1, 0}, {0, 1}, {0, -1} }) {
                Level.Cell next = new Level.Cell(cell.column() + direction[0], cell.row() + direction[1]);
                if (next.column() < 0 || next.row() < 0 || next.column() >= level.columns() || next.row() >= level.rows()) {
                    continue;
                }
                int tile = level.tileAt(next.column(), next.row());
                if ((tile == Level.FLOOR || next.equals(target)) && !previous.containsKey(next)) {
                    previous.put(next, cell);
                    queue.add(next);
                }
            }
        }
        assertTrue(previous.containsKey(target), "No route to " + target);
        List<Level.Cell> path = new ArrayList<>();
        for (Level.Cell cell = target; cell != null; cell = previous.get(cell)) {
            path.add(cell);
        }
        Collections.reverse(path);
        return path;
    }
}
