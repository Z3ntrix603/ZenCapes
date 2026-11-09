# ZenCapes (Official Capes & Cloth Physics) 🥋

> **ZenCapes** is a client-sided Minecraft Fabric mod featuring all 36 official Minecraft capes (including staff, developer, and event capes) with realistic, customizable Dawn / NoRisk cloth and wave physics.

[![Minecraft](https://img.shields.io/badge/Minecraft-26.2%20%7C%201.21.11-brightgreen.svg)](https://fabricmc.net/)
[![Fabric](https://img.shields.io/badge/ModLoader-Fabric-blue.svg)](https://fabricmc.net/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Release](https://img.shields.io/github/v/release/Z3ntrix603/ZenCapes?color=orange)](https://github.com/Z3ntrix603/ZenCapes/releases)

---

## ✨ Features

- **All 36 Official Minecraft Capes Included:**
  - **Minecon:** Minecon 2011, 2012, 2013, 2015, 2016, and Founder's Cape.
  - **Staff & Developers:** Mojang Classic, Mojang Studios, Mojang Spil, Mojang Developer Cape.
  - **Events & Anniversaries:** 15th Anniversary Cape, MCC 15 Year Cape, Cherry Blossom, TikTok, Twitch, Experience Cape, Vanilla Cape.
  - **Community & Special:** Translator, Chinese Translator, Turtle, Cobalt, Scrolls, Bug Tracker (Chubby), Millionth Customer, Valentine, Birthday, Bacon, Prismarine, Danny B, Julian Clark, Cheapskate, MrMessiah, Ray Cokes.
- **Dawn / NoRisk Cloth Physics:**
  - 16-segment Verlet integration cloth simulation.
  - Seamless waving while walking, sprinting, jumping, sneaking, and flying with an Elytra.
  - Dynamic wind and velocity-based sway.
- **Physics Customization:**
  - In-game tuning screen to adjust cloth stiffness, gravity, wind strength, wave magnitude, and damping in real time.
- **Authentic Vanilla Minecraft GUI:**
  - Classic Minecraft dirt/dark themed UI with cape previews, search filter, and instant equip.
- **Controls & Keybinding Section:**
  - Dedicated keybind category in Minecraft Controls: *"Official Capes & Physics"*.

---

## ⌨️ Controls & Keybinds

| Action | Default Key | Configurable in Controls |
|---|---|---|
| Open Cape GUI | `H` | Yes (Options → Controls → Key Binds → Official Capes & Physics) |
| Toggle Cape Visibility | `Y` | Yes (Options → Controls → Key Binds → Official Capes & Physics) |

### 💬 Chat Commands
- `/cape` or `/capes` — Opens the cape selection GUI directly.

---

## 📥 Download & Installation

1. Install **Fabric Loader** for Minecraft **1.21.11** or **26.2**.
2. Download the matching `.jar` file from [**Releases**](https://github.com/Z3ntrix603/ZenCapes/releases):
   - `zencapes-1.0.0-1.21.11.jar` for Minecraft 1.21.11
   - `zencapes-1.0.0-26.2.jar` for Minecraft 26.2
3. Place the `.jar` into your `.minecraft/mods` directory.
4. Launch Minecraft!

---

## 🛠️ Building from Source

### Prerequisites
- **JDK 21+** (for 1.21.11) and **JDK 25** (for 26.2)
- Git

### Build Instructions
```bash
git clone https://github.com/Z3ntrix603/ZenCapes.git
cd ZenCapes

# Build Minecraft 1.21.11
./gradlew :mc-1.21.11:build

# Build Minecraft 26.2
./gradlew :mc-26.2:build
```
Compiled `.jar` files will be in `mc-1.21.11/build/libs/` and `mc-26.2/build/libs/`.

---

## 📜 License
This project is licensed under the [MIT License](LICENSE).
