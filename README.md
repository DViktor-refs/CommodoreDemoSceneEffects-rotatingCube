 Rotating 3D Cube - Android Jetpack Compose Demo
<div align="center">

<p align="center"> <img src="https://raw.githubusercontent.com/yourusername/rotating-cube/main/screenshots/demo.gif" alt="Rotating Cube Demo" width="300"/> </p><h3>✨ Real-time 3D rotating cube with custom rendering engine</h3></div>
📖 Overview

This project demonstrates pure software 3D rendering on Android using Jetpack Compose Canvas. No OpenGL, no external 3D libraries - just math, matrices, and pixel-perfect drawing.

The cube features a checkerboard pattern across all 6 faces, creating a visually striking effect reminiscent of classic Amiga demos from the 80s/90s.
🎯 Features
<table> <tr> <td width="50%">
🚀 Core Features

    Real-time 3D rotation on all 3 axes (X, Y, Z)

    Perspective projection for realistic depth

    Painter's algorithm for proper face sorting

    24 triangulated faces with checkerboard coloring

    12 dark edge outlines for definition

    Smooth 60fps animation using Compose transitions

</td> <td width="50%">
🛠 Technical Highlights

    Pure software rendering - No GPU/OpenGL

    Custom 3D math engine from scratch

    Euler angle rotation (Z → Y → X order)

    Dynamic depth sorting every frame

    Resolution-independent rendering

    Minimal dependencies - only Jetpack Compose

</td> </tr> </table>
🔬 How It Works
📐 The 3D Pipeline
🎨 Color Scheme
Face	Color	Hex
Left, Right, Back, Bottom	<span style="color:#0033D9">Blue</span>	#0033D9
Front, Top	<span style="color:#4DA6FF">Light Blue</span>	#4DA6FF
Edges	<span style="color:#000000">Dark Outline</span>	#000000 at 55% alpha
🧮 Mathematical Foundation
Rotation Matrices (applied in order Z → Y → X)
text

Z-rotation:
|x'|   |cos(θz)  -sin(θz)  0|   |x|
|y'| = |sin(θz)   cos(θz)  0| × |y|
|z'|   |   0        0      1|   |z|

Perspective Projection
kotlin

zInv = focalLength / (focalLength - z)
screenX = rotatedX × zInv + centerX
screenY = rotatedY × zInv + centerY

    Focal length = 400 → Creates natural perspective distortion

📊 Point System

The cube uses 14 strategic points:
text

    7-------8         14 Points Total:
   /|      /|         • 8 Corners (0-3, 7-10)
  0-------1 |         • 6 Face Centers (4-6, 11-13)
  | 10----|-9         
  |/      |/          Face centers allow fan-triangulation
  3-------2           (4 triangles per face)

🚀 Getting Started
Prerequisites

    Android Studio Hedgehog (2023.1.1) or later

    Kotlin 1.9.0+

    Android SDK 24+

Installation

1️⃣ Clone the repository
bash

git clone https://github.com/yourusername/rotating-cube.git
cd rotating-cube

2️⃣ Open in Android Studio
bash

# Open the project directory
# Let Gradle sync complete

3️⃣ Run on device/emulator
bash

# Press Shift+F10 or click Run button
# Minimum API 24 (Android 7.0)

📁 Project Structure
text

app/src/main/java/com/example/rotatingcubeamigademo/
│
├── MainActivity.kt          # Entry point & 3D rendering
│   ├── Vector3              # 3D point data class
│   ├── TransformedPoint     # Projected point data class
│   └── Vector3DScene()      # Main composable with:
│       ├── Point definitions (14 points)
│       ├── Face triangulation (24 triangles)
│       ├── Edge definitions (12 edges)
│       ├── Rotation calculations
│       ├── Perspective projection
│       └── Painter's algorithm sorting
│
└── res/
    └── values/
        ├── strings.xml
        ├── colors.xml
        └── themes.xml

🎮 Customization
<table> <tr> <td>
Change Rotation Speed
kotlin

// In Vector3DScene()
val angleSkipX = 1f  // 🔄 X-axis speed
val angleSkipY = 2f  // 🔄 Y-axis speed  
val angleSkipZ = 3f  // 🔄 Z-axis speed

</td> <td>
Adjust Perspective
kotlin

val proj = 400f  
// 🔍 Lower = more distortion
// 🔭 Higher = more orthographic

</td> </tr> <tr> <td>
Modify Colors
kotlin

private val colorBlue = Color(0.00f, 0.20f, 0.85f)
// 💙 Dark blue faces
private val colorLightBlue = Color(0.30f, 0.65f, 1.00f)
// 💎 Light blue faces

