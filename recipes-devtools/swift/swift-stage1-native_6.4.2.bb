SUMMARY = "Swift compiler, bootstrap stage 1"
DESCRIPTION = "Swift compiler built by swift-stage0-native, with swift-syntax and \
macro support. It exists to build swift-native and is never packaged or shipped."

require swift-compiler.inc

inherit native

DEPENDS += "swift-stage0-native"

SRC_URI += "${SRC_URI_SWIFT_IN_SWIFT}"

SWIFT_STAGE_PREFIX = "${libexecdir}/swift-stage1"

# Mirrors upstream's bootstrap_stage1 preset.
EXTRA_OECMAKE += "\
    -DCMAKE_Swift_COMPILER=${STAGING_DIR_NATIVE}${libexecdir}/swift-stage0/bin/swiftc \
    -DSWIFT_ENABLE_SWIFT_IN_SWIFT=ON \
    -DSWIFT_BUILD_SWIFT_SYNTAX=ON \
    -DSWIFT_PATH_TO_SWIFT_SYNTAX_SOURCE=${SWIFT_SYNTAX_SRCDIR} \
    -DSWIFT_PATH_TO_STRING_PROCESSING_SOURCE=${SWIFT_STRING_PROCESSING_SRCDIR} \
    -DSWIFT_ENABLE_EXPERIMENTAL_STRING_PROCESSING=ON \
    -DSWIFT_ENABLE_EXPERIMENTAL_OBSERVATION=ON \
    -DSWIFT_ENABLE_SYNCHRONIZATION=ON \
    -DSWIFT_ENABLE_BACKTRACING=ON \
"

SWIFT_INSTALL_COMPONENTS = "autolink-driver;compiler;clang-resource-dir-symlink;stdlib;sdk-overlay;toolchain-tools;swift-syntax-lib;compiler-swift-syntax-lib"
