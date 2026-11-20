SWIFTSDK ??= "1"
RDEPENDS:${PN}:append:swiftllvm = "${@bb.utils.contains('SWIFTSDK', '1', ' swift-stdlib-dev libdispatch-dev swift-dispatch-dev swift-foundation-dev', '', d)}"
