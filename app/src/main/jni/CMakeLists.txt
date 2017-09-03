cmake_minimum_required(VERSION 3.4.1)

add_library(GLES2NativeLib SHARED
            GLES2Wrapper.cpp
            GLES2Utils.cpp
            Triangle.cpp)

# Include library for GLES2NativeLib
target_link_libraries(GLES2NativeLib
                      android
                      EGL
                      GLESv2
                      log)