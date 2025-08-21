# MediaStreamApp

Android app to stream and download video/audio from other devices on the same WiFi network.

## Features
- Discover or manually enter device/stream URL
- Stream video/audio with zero/low latency (ExoPlayer)
- Download media to local storage

## Usage
1. Open in Android Studio (API 23+)
2. Build and run on device
3. Enter or discover a stream URL (e.g., http://<ip>:<port>/video.mp4)
4. Tap Stream to play, Download to save

## Server
- Use a simple HTTP server (Python, VLC, or other) on the source device to share media files or live stream.
