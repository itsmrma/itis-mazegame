package com.example.mazegame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** A rectangular map: 0 = floor, 1 = wall, 2 = exit. */
final class Level {
    static final int FLOOR = 0;
    static final int WALL = 1;
    static final int EXIT = 2;

    record Cell(int column, int row) {
    }

    private final int[][] tiles;
    private final List<Cell> floors;
    private final Cell exit;

    private Level(int[][] tiles, List<Cell> floors, Cell exit) {
        this.tiles = tiles;
        this.floors = List.copyOf(floors);
        this.exit = exit;
    }

    static Level load(String path) throws IOException {
        try (Reader reader = new InputStreamReader(Resources.get(path).openStream(), StandardCharsets.UTF_8)) {
            return parse(reader);
        }
    }

    static Level parse(Reader reader) throws IOException {
        BufferedReader buffered = new BufferedReader(reader);
        List<int[]> rows = new ArrayList<>();
        List<Cell> floors = new ArrayList<>();
        Cell exit = null;
        String line;
        while ((line = buffered.readLine()) != null) {
            String[] values = line.trim().split("\\s+");
            if (line.isBlank() || (!rows.isEmpty() && values.length != rows.getFirst().length)) {
                throw new IOException("Each level row must contain the same number of tiles.");
            }
            int[] row = new int[values.length];
            for (int column = 0; column < values.length; column++) {
                try {
                    row[column] = Integer.parseInt(values[column]);
                } catch (NumberFormatException exception) {
                    throw new IOException("Invalid tile: " + values[column], exception);
                }
                Cell cell = new Cell(column, rows.size());
                switch (row[column]) {
                    case FLOOR -> floors.add(cell);
                    case WALL -> { }
                    case EXIT -> {
                        if (exit != null) {
                            throw new IOException("A level must have exactly one exit.");
                        }
                        exit = cell;
                    }
                    default -> throw new IOException("Unknown tile: " + row[column]);
                }
            }
            rows.add(row);
        }
        if (exit == null || floors.isEmpty()) {
            throw new IOException("A level must contain floor tiles and exactly one exit.");
        }
        return new Level(rows.toArray(int[][]::new), floors, exit);
    }

    int columns() {
        return tiles[0].length;
    }

    int rows() {
        return tiles.length;
    }

    int tileAt(int column, int row) {
        return tiles[row][column];
    }

    List<Cell> floors() {
        return floors;
    }

    Cell exit() {
        return exit;
    }
}
