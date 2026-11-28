RDEPENDS:${PN}:append:swiftllvm = "${@bb.utils.contains('SWIFTSDK', '1', ' swift-clang-cross-canadian-${TRANSLATED_TARGET_ARCH}', '', d)}"
