cmake_minimum_required(VERSION 3.4.1)

# GLES2NativeLib file
file (GLOB GLES2-native-lib
      GLES2Wrapper.cpp
      GLES2Utils.cpp
      Triangle.cpp
      Rectangle.cpp)

# SOIL library
file (GLOB stb-image-lib
    "./stb/stb_image.cpp")

add_library(GLES2NativeLib SHARED
            ${GLES2-native-lib}
            ${stb-image-lib})

# Include library for GLES2NativeLib
target_link_libraries(GLES2NativeLib
                      android
                      EGL
                      GLESv2
                      log)