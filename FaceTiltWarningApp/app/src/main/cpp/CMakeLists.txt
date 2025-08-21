cmake_minimum_required(VERSION 3.10.2)
project(facetilt_jni)

add_library(facetilt_jni SHARED
    facetilt_jni.cpp
)

find_library(log-lib log)

target_link_libraries(facetilt_jni
    ${log-lib}
)
