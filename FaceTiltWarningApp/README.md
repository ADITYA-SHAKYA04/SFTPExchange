# FaceTiltWarningApp

Android app using Google ML Kit for face detection and C++ (NDK) for tilt logic.

- Live camera preview (CameraX)
- Detects face tilt (yaw/roll) using ML Kit
- If face is tilted left/right more than 30°, triggers a beep warning (C++ logic)

## Usage
1. Open in Android Studio (API 23+)
2. Build and run on device
3. Grant camera permission
4. Tilt your head left/right >30° to trigger beep
