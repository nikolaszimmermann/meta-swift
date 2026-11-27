# Compile Swift for the target and link against the Swift runtime.
inherit swift-base

DEPENDS:append:class-target = " swift-stdlib libdispatch"
DEPENDS:append:class-nativesdk = " nativesdk-swift-stdlib nativesdk-libdispatch"
DEPENDS:append:class-native = " libdispatch-native"

# The -dev package of a Swift library: everything but the runtime libraries,
# that is the modules, module maps and headers. lib/swift/[!lm]* is all of
# lib/swift but the platform directory (linux or musl).
SWIFT_MODULE_DEV_FILES = "\
    ${libdir}/swift/${SWIFT_PLATFORM}/*.swiftmodule \
    ${libdir}/swift/${SWIFT_PLATFORM}/*.swiftdoc \
    ${libdir}/swift/${SWIFT_PLATFORM}/*.swiftinterface \
    ${libdir}/swift/${SWIFT_PLATFORM}/${SWIFT_TARGET_ARCH} \
    ${libdir}/swift/[!lm]* \
    ${libdir}/swift/lib*.a \
    ${libdir}/swift/${SWIFT_PLATFORM}/*.a \
    ${libdir}/cmake \
"
