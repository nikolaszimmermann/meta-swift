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
add_dependencies(swift-cxx-interop-test SwiftGreeter)
target_include_directories(swift-cxx-interop-test PRIVATE ${CMAKE_CURRENT_BINARY_DIR})
target_link_libraries(swift-cxx-interop-test PRIVATE SwiftGreeter)

install(TARGETS swift-hello swift-cxx-interop-test SwiftGreeter
    RUNTIME DESTINATION bin LIBRARY DESTINATION lib)
