# CMake projects with Swift sources: make the generated toolchain.cmake
# describe swift-native as the Swift compiler for the target.
inherit swift cmake swift-cmake-tool

# Find modules (CURL, LibXml2, ...) report the system include directory of
# the sysroot. CMake drops it for C and C++ as an implicit include
# directory, but has none for Swift, so it would become a -I that shadows
# the Clang builtin headers in the Clang importer. Treat the C and C++
# implicit include directories as implicit for Swift as well, after every
# project() call (the Swift compiler information is loaded there).
swift_write_cmake_project_include() {
    cat > ${WORKDIR}/swift-project-include.cmake <<'SWIFTEOF'
if(CMAKE_Swift_COMPILER_LOADED)
  foreach(_swift_oe_dir IN LISTS CMAKE_C_IMPLICIT_INCLUDE_DIRECTORIES CMAKE_CXX_IMPLICIT_INCLUDE_DIRECTORIES)
    get_filename_component(_swift_oe_real "${_swift_oe_dir}" REALPATH)
    list(APPEND CMAKE_Swift_IMPLICIT_INCLUDE_DIRECTORIES "${_swift_oe_dir}" "${_swift_oe_real}")
  endforeach()
  list(REMOVE_DUPLICATES CMAKE_Swift_IMPLICIT_INCLUDE_DIRECTORIES)
endif()
SWIFTEOF
}

# nooelint: oelint.task.heredocs - the toolchain file takes the recipe's values
swift_extend_toolchain_file() {
    swift_write_cmake_project_include
    cat >> ${WORKDIR}/toolchain.cmake <<SWIFTEOF
set(CMAKE_Swift_COMPILER ${SWIFTC})
set(CMAKE_Swift_COMPILER_TARGET ${SWIFT_TARGET})
set(CMAKE_Swift_FLAGS_INIT "$(echo ${SWIFTFLAGS})")
set(CMAKE_PROJECT_INCLUDE ${WORKDIR}/swift-project-include.cmake)
SWIFTEOF
}

cmake_do_generate_toolchain_file:append() {
    swift_extend_toolchain_file
}

# Without swift-driver next to it, swiftc falls back to the deprecated C++
# driver, which behaves differently (for example where it writes dependency
# files). Native Swift recipes build swift-driver-native itself.
DEPENDS:append:class-target = " swift-driver-native"
DEPENDS:append:class-nativesdk = " swift-driver-native"
