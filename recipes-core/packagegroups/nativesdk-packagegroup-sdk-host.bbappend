SWIFTSDK ??= "1"
RDEPENDS:${PN}:append:swiftllvm = "${@bb.utils.contains('SWIFTSDK', '1', ' nativesdk-swift', '', d)}"
