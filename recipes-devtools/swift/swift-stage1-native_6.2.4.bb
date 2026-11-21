SUMMARY = "Swift compiler, bootstrap stage 1 (Swift 6.2)"
DESCRIPTION = "Swift compiler built by swift-stage0-native, with swift-syntax and \
macro support. It exists to build swift-stage2-native and is never packaged."

require swift-compiler.inc
require swift-bootstrap.inc

DEPENDS += "swift-stage0-native"

SRC_URI += "\
    ${SRC_URI_SWIFT_IN_SWIFT} \
    file://0010-Remove-dependency-on-_StringProcessing-during-stage-.patch \
"

SWIFT_STAGE_PREFIX = "${libexecdir}/swift-stage1"

EXTRA_OECMAKE += "\
    -DCMAKE_Swift_COMPILER=${STAGING_DIR_NATIVE}${libexecdir_native}/swift-stage0/bin/swiftc \
    -DCMAKE_Swift_FLAGS='-Xfrontend -disable-sil-ownership-verifier' \
    -DSWIFT_ENABLE_SWIFT_IN_SWIFT=ON \
    -DSWIFT_BUILD_SWIFT_SYNTAX=ON \
    -DSWIFT_PATH_TO_SWIFT_SYNTAX_SOURCE=${SWIFT_SYNTAX_SRCDIR} \
    -DSWIFT_PATH_TO_STRING_PROCESSING_SOURCE=${SWIFT_STRING_PROCESSING_SRCDIR} \
    -DSWIFT_ENABLE_EXPERIMENTAL_STRING_PROCESSING=ON \
    -DSWIFT_ENABLE_EXPERIMENTAL_OBSERVATION=ON \
    -DSWIFT_ENABLE_SYNCHRONIZATION=ON \
"

SWIFT_INSTALL_COMPONENTS = "autolink-driver;compiler;clang-resource-dir-symlink;stdlib;sdk-overlay;toolchain-tools;swift-syntax-lib;compiler-swift-syntax-lib"
