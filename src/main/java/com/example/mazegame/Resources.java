package com.example.mazegame;

import java.net.URL;
import java.util.Objects;

/** Loads bundled assets independently of the current working directory. */
final class Resources {
    private Resources() {
    }

    static URL get(String path) {
        return Objects.requireNonNull(Resources.class.getResource("/" + path),
                "Missing resource: " + path);
    }
}
