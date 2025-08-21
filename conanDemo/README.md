# conanDemo C++ Project

This project demonstrates building a simple C++ application using Conan for dependency management and CMake for building.

## Prerequisites
- Conan (v2.x)
- CMake
- Ninja (recommended)
- C++ compiler (e.g., GCC, Clang, MSVC)

## Directory Structure
```
conanDemo/
├── CMakeLists.txt
├── conanfile.txt
├── include/
│   └── factorial.h
├── src/
│   ├── factorial.cpp
│   └── main.cpp
└── build/
```

## Step-by-Step Build Instructions

### 1. Install Conan (if not already installed)
```sh
pip install conan
```

### 2. Set Up Conan Profile (first time only)
```sh
conan profile detect
```

### 3. Install Project Dependencies and Generate Toolchain
```sh
conan install . --output-folder=build --build=missing
```

### 4. Configure the Project with CMake
Using Ninja (recommended):
```sh
cmake -S . -B build -G Ninja -DCMAKE_TOOLCHAIN_FILE=build/conan_toolchain.cmake
```
Or using default generator (e.g., Makefiles):
```sh
cmake -S . -B build -DCMAKE_TOOLCHAIN_FILE=build/conan_toolchain.cmake
```

### 5. Build the Project
```sh
cmake --build build
```

### 6. Run the Executable
On Linux/macOS:
```sh
./build/conanDemo
```
On Windows:
```pwsh
.\build\conanDemo.exe
```

## Notes
- The binary name is `conanDemo` (or `conanDemo.exe` on Windows).
- If you add dependencies, update `conanfile.txt` and repeat the Conan and CMake steps.
- For troubleshooting, check the `build` directory for logs and error messages.

## Clean Build (optional)
To remove the build directory and start fresh:
```sh
rm -rf build
```

---
