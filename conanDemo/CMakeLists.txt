cmake_minimum_required(VERSION 3.10)

project(conanDemo)

set(CMAKE_CXX_STANDARD 11)

include_directories(include)

add_executable(conanDemo src/main.cpp src/factorial.cpp)
