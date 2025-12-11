# Real-Time Image Segmentation (Android)

A real-time semantic segmentation Android app built with **Kotlin** and **Jetpack Compose**. It uses **CameraX** for live video capture and Google's **MediaPipe Tasks Vision** (DeepLab V3 model) to detect and overlay masks on foreground objects instantly.

## 📱 Features

*   **Real-Time Segmentation:** Separates foreground objects from the background in live camera feeds.
*   **Modern UI:** Built entirely with **Jetpack Compose**.
*   **Camera Integration:** Uses **CameraX** `ImageAnalysis` with `STRATEGY_KEEP_ONLY_LATEST` for efficiency.
*   **Optimized Performance:**
    *   Explicit CPU delegation to ensure stability across devices.
    *   Custom asset handling to bypass Android asset compression issues.
    *   Targeted resolution (640x480) for optimal inference speed.

## 🛠 Tech Stack

*   **Language:** Kotlin
*   **UI Framework:** Jetpack Compose (Material3)
*   **ML Library:** MediaPipe Tasks Vision (`0.10.9`)
*   **Camera:** CameraX (`1.4.1`)
*   **Minimum SDK:** 26 (Android 8.0)
*   **Compile SDK:** 35 (Android 15)
