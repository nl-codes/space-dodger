Space Dodger 🚀🌌
=================

Space Dodger is an arcade-style Java GUI game 🎮 where you control a player 🧑‍🚀 and dodge incoming asteroids ☄️ to survive as long as possible ⏱️. Built with a Java Swing-based UI 🖥️, the project demonstrates core object-oriented design 🧠 and simple real-time game mechanics ⚙️.

Table of Contents 📚
--------------------

-   [Overview](#overview) 🌠
-   [Features](#features) ✨
-   [Directory Structure](#directory-structure) 🗂️
-   [How to Run](#how-to-run) 🚀
-   [Future Improvements](#future-improvements) 🔮

Overview 🌌
-----------

You control a player character 🧑‍🚀 that must avoid collisions 💥 with randomly appearing asteroids ☄️. The game loop runs in `GamePanel`, which updates game objects 🔄 and renders frames 🎞️; collisions end the run 💀 and the player's score is recorded 🧾. The objective is simple: survive as long as you can ⏳ and earn the highest score 🏆.

Key elements: 🔑

-   Player 🧑‍🚀: controlled by the user 🎮
-   Asteroids ☄️: obstacles that spawn and move toward the player
-   Collision detection 💥 and survival mechanics determine run end

Features ⚡✨
-----------

-   Real-time gameplay 🕹️ with continuous updates 🔄 and collision detection 💥
-   Object-oriented structure 🧠: `Player`, `Asteroid`, `GameObject`, `GamePanel`, `SpaceDodger`
-   Score tracking 📊 persisted to `output/scores.csv` 🧾
-   Simple, interactive Java Swing UI 🖥️ for quick play 🎮 and testing 🧪
-   Generated API documentation 📖 available in `javadocs/`
-   Resources and design/docs 📁 stored in `res/`

Directory Structure 🗂️
-----------------------

```
space-dodger/
├── src/ 📂
│   └── spacedodger/
│       ├── Asteroid.java ☄️
│       ├── GameObject.java 🧱
│       ├── GamePanel.java 🎮
│       ├── Player.java 🧑‍🚀
│       └── SpaceDodger.java 🚀
├── res/ 📁
│   ├── Final Requirements.pdf 📄
│   └── Game.pdf 📄
├── javadocs/ 📖
├── output/ 📊
│   └── scores.csv 🧾
└── .gitignore 🙈
```

How to Run 🚀💻

Requirements: JDK 8+ installed ☕. Run from the project root 📍.

1. Compile ⚙️:

```bash
javac -d out src/spacedodger/*.java
```

2. Run ▶️:

```bash
java -cp out spacedodger.SpaceDodger
```

Notes 📝:

-   The `-d out` flag writes compiled classes to the `out` directory 📦
-   Scores are appended to `output/scores.csv` after each run 📊 --- ensure the `output/` directory is writable ✍️

Future Improvements 🔮✨
-----------------------

-   Add sound effects 🔊 and music 🎵 to increase immersion 🎧
-   Implement levels 📈 or dynamic difficulty scaling ⚖️
-   Improve graphics 🎨 and sprite artwork 🖼️ (replace basic shapes with assets)
-   Add pause ⏸️ / restart 🔁 controls and an in-game UI 🎛️ for lives/health ❤️
-   Add a persistent leaderboard 🏆 or online high scores 🌐

Enjoy playing 🎮 --- feel free to fork 🍴 or adapt this project for learning 📚 or portfolio use 💼
