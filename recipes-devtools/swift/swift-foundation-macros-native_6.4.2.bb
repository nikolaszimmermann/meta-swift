SUMMARY = "Foundation's Swift macros, as a compiler plugin for the build host"
DESCRIPTION = "FoundationMacros (#Predicate, #Expression, ...) built as a shared \
library that swift-native loads while compiling swift-foundation for the target."
HOMEPAGE = "https://github.com/swiftlang/swift-foundation"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=2380e856fbdbc7ccae6bd699d53ec121"

require swift-version.inc

DEPENDS += "swift-native ninja-native"

PV = "${SWIFT_VERSION}"

SRC_URI = "${SRC_URI_SWIFT_FOUNDATION} ${SRC_URI_SWIFT_SYNTAX}"
S = "${SWIFT_UNPACKDIR}/swift-foundation-${SWIFT_TAG}"

inherit cmake swift-cmake-tool
inherit_defer native

OECMAKE_SOURCEPATH = "${S}/Sources/FoundationMacros"

EXTRA_OECMAKE = "\
    -DCMAKE_BUILD_TYPE=Release \
    -DCMAKE_Swift_COMPILER=${STAGING_BINDIR_NATIVE}/swiftc \
    -DCMAKE_LINKER_TYPE=LLD \
    -DFETCHCONTENT_FULLY_DISCONNECTED=ON \
    -DFETCHCONTENT_SOURCE_DIR_SWIFTSYNTAX=${SWIFT_SYNTAX_SRCDIR} \
"

# The plugin is only ever loaded by swift-native from the build tree of the
# target Foundation, so install it into a private directory.
do_install() {
    install -d ${D}${libdir}/swift/host/plugins
    install -m 0755 ${B}/lib/libFoundationMacros.so ${D}${libdir}/swift/host/plugins/
}
