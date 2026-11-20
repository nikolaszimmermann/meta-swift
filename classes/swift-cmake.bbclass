# CMake projects with Swift sources: make the generated toolchain.cmake
# describe swift-native as the Swift compiler for the target.
inherit swift cmake

cmake_do_generate_toolchain_file:append:class-target() {
    cat >> ${WORKDIR}/toolchain.cmake <<SWIFTEOF
set(CMAKE_Swift_COMPILER ${SWIFTC})
set(CMAKE_Swift_COMPILER_TARGET ${SWIFT_TARGET})
set(CMAKE_Swift_FLAGS_INIT "$(echo ${SWIFTFLAGS})")
SWIFTEOF
}
