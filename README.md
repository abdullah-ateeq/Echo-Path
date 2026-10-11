# Echo Path 🌀

[![Android API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat&logo=android)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4.svg?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-orange.svg)](LICENSE)

**Echo Path** is an intense, reflex-driven temporal arcade game built completely with **Android Jetpack Compose**, custom **Canvas graphics**, and real-time path recording. 

Your greatest enemy isn't an AI or a random obstacle—it is **your past self**.

---

## 🎮 Game Concept

In **Echo Path**, you control a glowing orb in a futuristic neon arena. Each round lasts **5 seconds**.

1. **Round 1:** Move around freely and record your movement path.
2. **Round 2:** A "Ghost Echo" spawns that replays your exact movement from Round 1.
3. **Round 3:** A second Ghost Echo spawns replaying Round 2, while the first Ghost continues replaying Round 1!
4. **Round N:** Dodge an ever-increasing horde of your own past movements ("Echoes").

If you touch any Ghost Echo or collide with the arena boundaries, the game ends. How many rounds can you survive before your past catches up to you?

---

## ✨ Features

- **🌀 Temporal Echo Mechanics:** Every round records your movement and creates a new Ghost Echo that duplicates your past trajectory.
- **⚡ High-Performance Compose Canvas:** Custom rendering pipeline handling sub-pixel movement, ghost trails, glow effects, and particle physics at 60+ FPS.
- **💥 Dynamic Particle & Physics Engine:** Explosion particles, close-call spark indicators, and responsive movement physics.
- **🔊 Procedural Sound & Haptics:** Custom sound effects and tactile feedback engineered with Android's `Vibrator` & `SoundPool` APIs for immersive gameplay.
- **🎨 Custom Themes & Visual Styles:**
  - **Dark Void** (OLED / Cyberpunk aesthetic)
  - **Light Cyber**
  - **System Default**
- **⚙️ Graphics & Gameplay Options:** Toggle ghost trails, particle density, sound effects, haptic vibrations, or enable reduced effects for battery saving.
- **💾 Local Persistence:** Automated saving of best records, statistics, and game settings powered by **Jetpack DataStore Preferences**.

---

## 📱 Tech Stack & Architecture

- **Language:** 100% Kotlin
- **UI Framework:** Jetpack Compose with Material 3
- **Graphics Engine:** Jetpack Compose `DrawScope` Canvas with `GraphicsLayer` acceleration
- **State Management:** Reactive state flows using Kotlin `StateFlow` & `Coroutines`
- **Persistence:** Jetpack DataStore Preferences
- **Audio & Haptics:** `SoundPool` sound synthesizer & system `Vibrator` / `VibrationEffect` integration
- **Target SDK:** API 37 | **Min SDK:** Android 7.0 (API 24)

---

## 📂 Project Structure

```
com.example.echopath
├── data/              # DataStore repository for persistent settings & scores
├── game/              # Core game engines & physics (Collision, Particles, Audio, Haptics)
├── model/             # Game states, path coordinates, settings data models
├── ui/
│   ├── components/    # Reusable UI widgets (ArenaCanvas, GlassCards, Buttons, Displays)
│   ├── navigation/    # App screen navigation definitions
│   ├── screens/       # Game screens (Splash, Home, Game Arena, Result, Settings, About)
│   └── theme/         # Color palettes, neon schemes, Typography, MaterialTheme wrapper
└── MainActivity.kt    # Root activity & game state orchestrator
```

---

## 🛠️ Building & Running

### Prerequisites

- **Android Studio** (Ladybug / Meerkat or higher recommended)
- **JDK 11** or higher
- Android Device or Emulator running **API 24+**

### Steps

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/abdullah-ateeq/Echo-Path.git
   cd Echo-Path
   ```

2. **Open in Android Studio:**
   Open Android Studio and select **Open an existing project**, navigating to the `Echo-Path` directory.

3. **Build & Install:**
   Using Gradle wrapper via terminal or Android Studio run button:
   ```bash
   ./gradlew installDebug
   ```

---

## 📜 License

```
MIT License

Copyright (c) 2025 Abdullah Ateeq

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
