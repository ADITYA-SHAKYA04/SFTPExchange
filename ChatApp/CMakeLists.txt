cmake_minimum_required(VERSION 3.16)
project(ChatApp LANGUAGES CXX)

include(${CMAKE_BINARY_DIR}/dependencies/conan_toolchain.cmake)

add_library(chat_server src/chat_server.cpp)
add_library(chat_client src/chat_client.cpp)

target_include_directories(chat_server PUBLIC include)
target_include_directories(chat_client PUBLIC include)

find_package(asio REQUIRED)
find_package(spdlog REQUIRED)
find_package(nlohmann_json REQUIRED)

target_link_libraries(chat_server PUBLIC asio::asio spdlog::spdlog nlohmann_json::nlohmann_json)
target_link_libraries(chat_client PUBLIC asio::asio spdlog::spdlog nlohmann_json::nlohmann_json)

add_executable(example examples/example_main.cpp)
target_link_libraries(example PRIVATE chat_server chat_client)

find_package(Catch2 REQUIRED)

add_executable(test_server tests/test_server.cpp)
add_executable(test_client tests/test_client.cpp)

target_link_libraries(test_server PRIVATE chat_server Catch2::Catch2WithMain)
target_link_libraries(test_client PRIVATE chat_client Catch2::Catch2WithMain)