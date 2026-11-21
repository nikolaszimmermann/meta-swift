cmake_minimum_required(VERSION 3.26)
project(SwiftCxxInteropTest LANGUAGES C CXX Swift)

# A C++ library, imported into Swift through a module map.
add_library(Greeter STATIC Greeter.cpp)
target_include_directories(Greeter PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})

# A Swift library using the C++ library and exposing Swift to C++.
add_library(SwiftGreeter SHARED SwiftGreeter.swift)
target_link_libraries(SwiftGreeter PRIVATE Greeter)
target_compile_options(SwiftGreeter PRIVATE
    "SHELL:-cxx-interoperability-mode=default"
    "SHELL:-Xcc -I${CMAKE_CURRENT_SOURCE_DIR}"
    "SHELL:-emit-clang-header-path ${CMAKE_CURRENT_BINARY_DIR}/SwiftGreeter-Swift.h")
set_target_properties(SwiftGreeter PROPERTIES Swift_MODULE_NAME SwiftGreeter)

# A Swift executable.
add_executable(swift-hello hello.swift)

# A C++ executable calling into Swift through the generated header.
add_executable(swift-cxx-interop-test main.cpp)
# The Swift-to-C++ header is a by-product of compiling SwiftGreeter: compile
# main.cpp only once the library (and with it the header) exists.
set_source_files_properties(main.cpp PROPERTIES
    OBJECT_DEPENDS ${CMAKE_CURRENT_BINARY_DIR}/libSwiftGreeter.so)
target_include_directories(swift-cxx-interop-test PRIVATE ${CMAKE_CURRENT_BINARY_DIR})
# The generated header inlines calls into the Swift runtime, so a C++
# consumer links it directly.
target_link_directories(swift-cxx-interop-test PRIVATE ${CMAKE_SYSROOT}/usr/lib/swift/linux)
target_link_libraries(swift-cxx-interop-test PRIVATE SwiftGreeter swiftCore)

install(TARGETS swift-hello swift-cxx-interop-test SwiftGreeter
    RUNTIME DESTINATION bin LIBRARY DESTINATION lib)
