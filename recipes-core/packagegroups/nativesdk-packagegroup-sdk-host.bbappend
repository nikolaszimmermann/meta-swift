SWIFTSDK ??= "1"
RDEPENDS:${PN}:append:swiftllvm = "${@bb.utils.contains('SWIFTSDK', '1', ' nativesdk-swift nativesdk-swift-driver nativesdk-swift-foundation nativesdk-swift-dispatch nativesdk-libdispatch', '', d)}"
