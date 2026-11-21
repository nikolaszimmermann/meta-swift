# Compile Swift for the target and link against the Swift runtime.
inherit swift-base

DEPENDS:append:class-target = " swift-stdlib libdispatch"
DEPENDS:append:class-nativesdk = " nativesdk-swift-stdlib nativesdk-libdispatch"
