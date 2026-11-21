SUMMARY = "Swift compiler, bootstrap stage 0 (Swift 6.2, C++ only)"
DESCRIPTION = "Minimal Swift compiler built from its C++ sources only. It exists \
to build swift-stage1-native and is never packaged or shipped."

require swift-compiler.inc
require swift-bootstrap.inc

SRC_URI += "\
    file://0008-revert-optimizer-changes.patch \
    file://0009-siloptimizer-bootstrap-workaround.patch \
"

SWIFT_STAGE_PREFIX = "${libexecdir}/swift-stage0"

EXTRA_OECMAKE += "\
    -DSWIFT_ENABLE_SWIFT_IN_SWIFT=OFF \
    -DSWIFT_BUILD_SWIFT_SYNTAX=OFF \
    -DSWIFT_BUILD_REGEX_PARSER_IN_COMPILER=OFF \
    -DSWIFT_ENABLE_EXPERIMENTAL_DISTRIBUTED=OFF \
    -DSWIFT_ENABLE_EXPERIMENTAL_OBSERVATION=OFF \
    -DSWIFT_ENABLE_EXPERIMENTAL_DIFFERENTIABLE_PROGRAMMING=OFF \
    -DSWIFT_BUILD_LIBEXEC=OFF \
"

# Stage 0 has no swift-syntax; its default target already covers everything.
OECMAKE_TARGET_COMPILE = "all"
