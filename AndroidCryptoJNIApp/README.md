# AndroidCryptoJNIApp

Android app (Java) with C++ backend (JNI) for encryption/decryption.

- C++ backend: `app/src/main/cpp/crypto_jni.cpp` (replace with real crypto code)
- Java frontend: `app/src/main/java/com/example/androidcryptojniapp/MainActivity.java`
- JNI interface: Encrypt/decrypt text using selected algorithm

## How to build/run
1. Open in Android Studio (with NDK/LLDB/CMake installed)
2. Build and run on device/emulator (API 23+)
3. Enter text, select algorithm, tap Encrypt/Decrypt

## Extending
- Replace the C++ placeholder with real crypto (e.g., OpenSSL, see ChatApp for C++ code)
- JNI interface is ready for integration
