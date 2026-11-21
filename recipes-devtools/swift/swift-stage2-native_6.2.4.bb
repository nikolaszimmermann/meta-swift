SUMMARY = "Swift compiler, bootstrap stage 2 (Swift 6.2, full)"
DESCRIPTION = "Full Swift 6.2 compiler built by swift-stage1-native. It builds \
the first Swift 6.4 (swift-stage3-native) and is never packaged."

require swift-compiler.inc
require swift-bootstrap.inc

DEPENDS += "swift-stage1-native"

SRC_URI += "${SRC_URI_SWIFT_IN_SWIFT}"

SWIFT_STAGE_PREFIX = "${libexecdir}/swift-stage2"

EXTRA_OECMAKE += "\
    -DCMAKE_Swift_COMPILER=${STAGING_DIR_NATIVE}${libexecdir_native}/swift-stage1/bin/swiftc \
    -DSWIFT_ENABLE_SWIFT_IN_SWIFT=ON \
    -DSWIFT_BUILD_SWIFT_SYNTAX=ON \
    -DSWIFT_PATH_TO_SWIFT_SYNTAX_SOURCE=${SWIFT_SYNTAX_SRCDIR} \
    -DSWIFT_PATH_TO_STRING_PROCESSING_SOURCE=${SWIFT_STRING_PROCESSING_SRCDIR} \
    -DSWIFT_ENABLE_EXPERIMENTAL_STRING_PROCESSING=ON \
    -DSWIFT_ENABLE_EXPERIMENTAL_OBSERVATION=ON \
    -DSWIFT_ENABLE_SYNCHRONIZATION=ON \
    -DSWIFT_ENABLE_BACKTRACING=ON \
    -DSWIFT_ENABLE_EXPERIMENTAL_DISTRIBUTED=ON \
"

SWIFT_INSTALL_COMPONENTS = "autolink-driver;compiler;clang-resource-dir-symlink;stdlib;sdk-overlay;toolchain-tools;swift-syntax-lib;compiler-swift-syntax-lib"
