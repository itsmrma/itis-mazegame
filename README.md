# MazeGame

A small JavaFX maze game originally created during my fourth year of high school. Inspired by *Harry Potter and the Goblet of Fire*, it challenges you to find a randomly placed key and reach the exit while exploring a maze with limited visibility.

The original graphics, map, soundtrack and Italian interface are preserved. The project now uses a standard Maven layout, with all game assets bundled as resources.

## Gameplay

Click **MAPPA 1** to start the game. Use the following keys to move:

| Key | Direction |
| --- | --- |
| W | Up |
| A | Left |
| S | Down |
| D | Right |

Walls block movement, and only the area around the player is visible. Collect the key, then find the door on the right side of the maze. Reaching the door without the key displays a message and returns you to the starting position. Winning reveals the maze and displays a confirmation; closing that confirmation ends the game.

There is currently one level, with 20 columns and 16 rows. Each tile is 48 pixels wide, giving a 960 × 768 game area.

## Requirements

- **JDK 21 or newer** (a full JDK, not just a JRE).
- A graphical desktop environment.
- An internet connection for the first build, to download Maven and dependencies.

The Maven Wrapper is included, so you do not need to install Maven separately. JavaFX Controls and Media are downloaded automatically; no local JavaFX SDK or manual IDE library setup is needed. See the [official JavaFX documentation](https://openjfx.io/openjfx-docs/) for platform setup details.

## Run the game

Open a terminal in the project directory.

**Linux / macOS**

```sh
./mvnw javafx:run
```

**Windows (PowerShell)**

```powershell
.\mvnw.cmd javafx:run
```

If Java cannot be found, set `JAVA_HOME` to your JDK installation and add its `bin` directory to `PATH`. If the Unix wrapper is not executable after copying the project, run `chmod +x mvnw`.

To work in an IDE, import `pom.xml` as a Maven project and select JDK 21 or newer. The entry point is `com.example.mazegame.MazeGameApplication`.

## Build and test

```sh
./mvnw clean verify
```

On Windows, use `.\mvnw.cmd clean verify` instead. Tests cover map parsing, bundled assets, wall collisions, screen boundaries, key collection, the locked exit and completing the maze with every possible key location. These tests do not require a graphical desktop.

The build creates `target/MazeGame-1.0-SNAPSHOT.jar`. This is a modular JAR, not a standalone executable JAR; use `javafx:run` to launch the game during development.

To create a runtime image that includes Java and JavaFX:

```sh
./mvnw clean javafx:jlink
```

Run `target/maze-game/bin/maze-game` on Linux/macOS, or `target\maze-game\bin\maze-game.bat` on Windows. The image and `target/maze-game.zip` are specific to the operating system and architecture used to build them.

## Project structure

```text
MazeGame/
├── .mvn/wrapper/             # Maven Wrapper configuration and bootstrap JAR
├── mvnw                     # Unix Maven launcher
├── mvnw.cmd                 # Windows Maven launcher
├── pom.xml                  # Dependencies, compilation and JavaFX configuration
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── module-info.java
│   │   │   └── com/example/mazegame/
│   │   │       ├── MazeGameApplication.java  # Menu, input and animation loop
│   │   │       ├── GameState.java            # Movement and game rules
│   │   │       ├── Level.java                # Map loading and validation
│   │   │       ├── TileManager.java          # Rendering and visibility
│   │   │       ├── GameAudio.java            # Music and sound effects
│   │   │       └── Resources.java            # Bundled resource lookup
│   │   └── resources/
│   │       ├── audio/
│   │       ├── images/
│   │       └── levels/1.txt
│   └── test/java/com/example/mazegame/
├── .gitattributes
├── .gitignore
└── README.md
```

Level files contain a rectangular grid of integers separated by whitespace:

| Value | Tile |
| --- | --- |
| 0 | Floor (a possible key location) |
| 1 | Wall |
| 2 | Exit |

Each map must contain floor tiles and exactly one exit. The current starting position is defined in `GameState`; changing the map requires keeping that position on a walkable tile. Adding another playable level also requires adding its menu entry and preview.

## Troubleshooting

- **Java version errors:** check `java -version` and `./mvnw -version`, and ensure Maven is using a full JDK 21 or newer.
- **No display / GTK initialization errors:** launch the game in a graphical desktop session. Linux needs the system GTK libraries required by JavaFX.
- **No sound:** check your audio output and system media support. On Linux, MP3 playback needs a version of FFmpeg/libavcodec supported by JavaFX 21 (for example, libavcodec 60). Newer distributions may require a compatibility package; see the [JavaFX supported configurations](https://www.oracle.com/java/technologies/javafx-supportedconfigurations.html). If JavaFX cannot initialize audio, the game continues without sound and prints a diagnostic message in the terminal.

## Background

This repository preserves a school project and its original theme. The bundled images and audio are carried over from that project; no asset authorship or redistribution license is asserted here.
