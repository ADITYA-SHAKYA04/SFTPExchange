cmake_minimum_required(VERSION 3.10.2)
project(crypto_jni)

add_library(crypto_jni SHARED
    crypto_jni.cpp
)

find_library(log-lib log)

target_link_libraries(crypto_jni
    ${log-lib}
)
