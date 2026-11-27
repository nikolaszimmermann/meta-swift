SUMMARY = "Swift compiler, bootstrap stage 2 (Swift 6.2, full)"
DESCRIPTION = "Full Swift 6.2 compiler built by swift-stage1-native. It builds \
the first Swift 6.4 (swift-stage3-native) and is never packaged."

require swift-compiler.inc
require swift-bootstrap.inc

DEPENDS += "swift-stage1-native"

SRC_URI += "${SRC_URI_SWIFT_IN_SWIFT}"

SWIFT_STAGE_PREFIX = "${libexecdir}/swift-stage2"

SWIFT_BUILD_COMPILER_DIR = "${STAGING_DIR_NATIVE}${libexecdir_native}/swift-stage1"
EXTRA_OECMAKE += "\
    ${SWIFT_IN_SWIFT_OECMAKE} \
    -DSWIFT_ENABLE_BACKTRACING=ON \
    -DSWIFT_ENABLE_EXPERIMENTAL_DISTRIBUTED=ON \
"

SWIFT_INSTALL_COMPONENTS = "${SWIFT_IN_SWIFT_INSTALL_COMPONENTS}"
