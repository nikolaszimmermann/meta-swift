SUMMARY = "Swift compiler, bootstrap stage 3 (first Swift 6.4)"
DESCRIPTION = "Swift 6.4 built by the Swift 6.2 swift-stage2-native, against \
swift-llvm-native. It builds swift-native and is never packaged."

require swift-compiler.inc

inherit_defer native

DEPENDS += "swift-stage2-native"

SRC_URI += "${SRC_URI_SWIFT_IN_SWIFT}"

SWIFT_STAGE_PREFIX = "${libexecdir}/swift-stage3"

SWIFT_BUILD_COMPILER_DIR = "${STAGING_DIR_NATIVE}${libexecdir_native}/swift-stage2"
EXTRA_OECMAKE += "\
    ${SWIFT_IN_SWIFT_OECMAKE} \
    -DSWIFT_ENABLE_BACKTRACING=ON \
    -DSWIFT_ENABLE_EXPERIMENTAL_DISTRIBUTED=ON \
"

SWIFT_INSTALL_COMPONENTS = "${SWIFT_IN_SWIFT_INSTALL_COMPONENTS}"
