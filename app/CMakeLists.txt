cmake_minimum_required(VERSION 3.4.1)

set(pathPROJECT C:/Users/leech/Desktop/program/study/poopy/puppy/Puppy) #rewrite to your project path
set(pathOPENCV ${pathPROJECT}/sdk)
set(pathLIBOPENCV_JAVA ${pathOPENCV}/native/libs/${ANDROID_ABI}/libopencv_java4.so)

set(CMAKE_VERBOSE_MAKEFILE on)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

include_directories(${pathOPENCV}/native/jni/include)

add_library( # Sets the name of the library.
        imageprocessing

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ${pathPROJECT}/app/src/main/cpp/imageprocessing.cpp )

add_library( lib_opencv SHARED IMPORTED )

set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION ${pathLIBOPENCV_JAVA})

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log )

target_link_libraries( # Specifies the target library.
        imageprocessing

        lib_opencv

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib} )