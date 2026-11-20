SUMMARY = "Swift compiler, bootstrap stage 0 (C++ only)"
DESCRIPTION = "Minimal Swift compiler built from its C++ sources only. It exists \
to build swift-stage1-native and is never packaged or shipped."

require swift-compiler.inc

inherit native

SWIFT_STAGE_PREFIX = "${libexecdir}/swift-stage0"

# Mirrors upstream's bootstrap_stage0 preset: no Swift sources in the
# compiler, no swift-syntax, no macros.
EXTRA_OECMAKE += "\
    -DSWIFT_ENABLE_SWIFT_IN_SWIFT=OFF \
    -DSWIFT_BUILD_SWIFT_SYNTAX=OFF \
    -DSWIFT_BUILD_REGEX_PARSER_IN_COMPILER=OFF \
    -DSWIFT_ENABLE_EXPERIMENTAL_DISTRIBUTED=OFF \
    -DSWIFT_ENABLE_EXPERIMENTAL_OBSERVATION=OFF \
    -DSWIFT_ENABLE_EXPERIMENTAL_DIFFERENTIABLE_PROGRAMMING=OFF \
    -DSWIFT_BUILD_LIBEXEC=OFF \
"
