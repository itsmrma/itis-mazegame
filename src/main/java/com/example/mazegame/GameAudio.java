package com.example.mazegame;

import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/** Keeps media players alive for the game and releases them on exit. */
final class GameAudio implements AutoCloseable {
    private final MediaPlayer music = createPlayer("music.mp3", 0.1);
    private final MediaPlayer keyEffect = createPlayer("key_collect.mp3", 0.05);

    void playMusic() {
        if (music != null) {
            music.play();
        }
    }

    void playKeyEffect() {
        if (keyEffect != null) {
            keyEffect.seek(Duration.ZERO);
            keyEffect.play();
        }
    }

    private static MediaPlayer createPlayer(String name, double volume) {
        try {
            MediaPlayer player = new MediaPlayer(new Media(Resources.get("audio/" + name).toExternalForm()));
            player.setVolume(volume);
            player.setOnError(() -> System.err.println("Audio unavailable: " + player.getError()));
            return player;
        } catch (MediaException exception) {
            // Missing system codecs or audio devices should not prevent playing.
            System.err.println("Audio unavailable (" + name + "): " + exception.getMessage());
            return null;
        }
    }

    @Override
    public void close() {
        if (music != null) {
            music.dispose();
        }
        if (keyEffect != null) {
            keyEffect.dispose();
        }
    }
}
