# Sources of one Swift release. The github archive tarballs of all swiftlang
# repositories unpack to <repo>-${SWIFT_TAG}. Version and LLVM fork pins live
# in conf/swift-version.conf.

SWIFT_GH = "https://github.com/swiftlang"

SWIFT_SYNTAX_VERSION = "604.0.0"

SRC_URI_SWIFT = "${SWIFT_GH}/swift/archive/refs/tags/${SWIFT_TAG}.tar.gz;name=swift;downloadfilename=swift-${SWIFT_TAG}.tar.gz"
SRC_URI_SWIFT_CMARK = "${SWIFT_GH}/swift-cmark/archive/refs/tags/${SWIFT_TAG}.tar.gz;name=cmark;downloadfilename=swift-cmark-${SWIFT_TAG}.tar.gz"
SRC_URI_SWIFT_LIBDISPATCH = "${SWIFT_GH}/swift-corelibs-libdispatch/archive/refs/tags/${SWIFT_TAG}.tar.gz;name=libdispatch;downloadfilename=swift-corelibs-libdispatch-${SWIFT_TAG}.tar.gz"
SRC_URI_SWIFT_STRING_PROCESSING = "${SWIFT_GH}/swift-experimental-string-processing/archive/refs/tags/${SWIFT_TAG}.tar.gz;name=stringprocessing;downloadfilename=swift-experimental-string-processing-${SWIFT_TAG}.tar.gz"
SRC_URI_SWIFT_SYNTAX = "${SWIFT_GH}/swift-syntax/archive/refs/tags/${SWIFT_SYNTAX_VERSION}.tar.gz;name=syntax;downloadfilename=swift-syntax-${SWIFT_SYNTAX_VERSION}.tar.gz"
SRC_URI_SWIFT_CORELIBS_FOUNDATION = "${SWIFT_GH}/swift-corelibs-foundation/archive/refs/tags/${SWIFT_TAG}.tar.gz;name=corelibsfoundation;downloadfilename=swift-corelibs-foundation-${SWIFT_TAG}.tar.gz"
SRC_URI_SWIFT_FOUNDATION = "${SWIFT_GH}/swift-foundation/archive/refs/tags/${SWIFT_TAG}.tar.gz;name=foundation;downloadfilename=swift-foundation-${SWIFT_TAG}.tar.gz"
SRC_URI_SWIFT_FOUNDATION_ICU = "${SWIFT_GH}/swift-foundation-icu/archive/refs/tags/${SWIFT_TAG}.tar.gz;name=foundationicu;downloadfilename=swift-foundation-icu-${SWIFT_TAG}.tar.gz"
SRC_URI_SWIFT_COLLECTIONS = "https://github.com/apple/swift-collections/archive/refs/tags/1.1.6.tar.gz;name=collections;downloadfilename=swift-collections-1.1.6.tar.gz"

SRC_URI[swift.sha256sum] = "c9c0c37995aa69994807eeb62c1e7e89bdd9f1a366dd8fc98a4bf65753dc6cbb"
SRC_URI[cmark.sha256sum] = "e9fb077781f4511103d5e404b7760ee07b688dc95e32f52616883c10ae3e669a"
SRC_URI[libdispatch.sha256sum] = "133f011259b8d25efedfa4ac1fa99b9ed340cb5010d8b10f9ba2dbf31b02335e"
SRC_URI[stringprocessing.sha256sum] = "8452bfdeac7f1afb9fe91faeba34f09ad46eb8e3085d345d3839f9ad4b69032b"
SRC_URI[syntax.sha256sum] = "a2968b85f4fbac228bfbbab7122ea69154d326cbf666e0bce6d8e9050f39a19d"
SRC_URI[corelibsfoundation.sha256sum] = "68f72893885bb6b1b5c3bd8118e6796889c51210e6fa82c1103eecb9ecc166e0"
SRC_URI[foundation.sha256sum] = "c258b3a2c1916fa42ea5cf47a7ff16661c0d4e3968eb57c8a4fab87d651d861b"
SRC_URI[foundationicu.sha256sum] = "6337f0365f6a76b328488f370ffe88fa974e984487b377c4caceb8892a54fdae"
SRC_URI[collections.sha256sum] = "2f558b33b6eba5b0c263110d7cb1a11b59d63059e845dc1984c65359e36f29da"

SWIFT_UNPACKDIR = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}"
SWIFT_SRCDIR = "${SWIFT_UNPACKDIR}/swift-${SWIFT_TAG}"
SWIFT_CMARK_SRCDIR = "${SWIFT_UNPACKDIR}/swift-cmark-${SWIFT_TAG}"
SWIFT_LIBDISPATCH_SRCDIR = "${SWIFT_UNPACKDIR}/swift-corelibs-libdispatch-${SWIFT_TAG}"
SWIFT_STRING_PROCESSING_SRCDIR = "${SWIFT_UNPACKDIR}/swift-experimental-string-processing-${SWIFT_TAG}"
SWIFT_SYNTAX_SRCDIR = "${SWIFT_UNPACKDIR}/swift-syntax-${SWIFT_SYNTAX_VERSION}"

# Swift recipes only make sense on top of the swiftlang LLVM fork.
python () {
    if d.getVar('SWIFT_UNIFIED_LLVM') != '1':
        raise bb.parse.SkipRecipe('requires conf/distro/include/swift-toolchain.inc')
}
