package com.example.mazegame;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class LevelTest {
    @Test
    void loadsBundledMap() throws IOException {
        Level level = Level.load("levels/1.txt");
        assertEquals(20, level.columns());
        assertEquals(16, level.rows());
        assertEquals(new Level.Cell(19, 6), level.exit());
        assertEquals(Level.FLOOR, level.tileAt(8, 8));
    }

    @Test
    void acceptsWhitespaceBetweenTiles() throws IOException {
        Level level = Level.parse(new StringReader(" 1  0\t2 \n1 0 1\n"));
        assertEquals(3, level.columns());
        assertEquals(2, level.rows());
        assertEquals(2, level.floors().size());
    }

    @Test
    void rejectsMalformedMaps() {
        for (String map : new String[] { "", "0 2\n0\n", "0 2\n\n", "0 x 2", "0 3 2", "0 1", "0 2 2", "1 2" }) {
            assertThrows(IOException.class, () -> Level.parse(new StringReader(map)), map);
        }
    }

    @Test
    void allVisualAndAudioAssetsAreBundled() throws IOException {
        for (String path : new String[] {
                "images/character.png", "images/door.png", "images/ground.jpg",
                "images/key.png", "images/map1.png", "images/wall.png",
                "audio/music.mp3", "audio/key_collect.mp3"
        }) {
            try (var stream = Resources.get(path).openStream()) {
                assertNotEquals(-1, stream.read(), path);
            }
        }
    }
}
