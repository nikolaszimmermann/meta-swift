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

SWIFT_STAGE_SWIFTFLAGS = "-Xfrontend -disable-sil-ownership-verifier"
SWIFT_BUILD_COMPILER_DIR = "${STAGING_DIR_NATIVE}${libexecdir_native}/swift-stage0"
EXTRA_OECMAKE += "${SWIFT_IN_SWIFT_OECMAKE}"

SWIFT_INSTALL_COMPONENTS = "${SWIFT_IN_SWIFT_INSTALL_COMPONENTS}"
