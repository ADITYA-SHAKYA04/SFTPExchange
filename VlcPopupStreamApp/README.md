# VlcPopupStreamApp

Android app to stream and download video/audio from another device on the same WiFi, using a floating popup player (libVLC, no intent).

## Features
- Enter stream URL (e.g., http://<ip>:<port>/video.mp4)
- Play in a floating popup using VLC's native library (libVLC)
- Download media to local storage

## Usage
1. Open in Android Studio (API 23+)
2. Build and run on device
3. Enter a stream URL
4. Tap Stream to play in popup, Download to save

## Server
- Use a simple HTTP server (Python, VLC, or other) on the source device to share media files or live streams.
