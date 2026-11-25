# Sources of one Swift release, fetched with git at the commits of the release
# tags. Each checkout goes to <repo>-<tag>, the directory the github archives
# used to unpack to. Version and LLVM fork pins live in
# conf/swift-version.conf.

SWIFT_GIT = "git://github.com/swiftlang"
SWIFT_GIT_PARAMS = "protocol=https;nobranch=1"

SWIFT_SYNTAX_VERSION = "604.0.0"

SRC_URI_SWIFT = "${SWIFT_GIT}/swift.git;${SWIFT_GIT_PARAMS};name=swift;destsuffix=swift-${SWIFT_TAG}"
SRC_URI_SWIFT_CMARK = "${SWIFT_GIT}/swift-cmark.git;${SWIFT_GIT_PARAMS};name=cmark;destsuffix=swift-cmark-${SWIFT_TAG}"
SRC_URI_SWIFT_LIBDISPATCH = "${SWIFT_GIT}/swift-corelibs-libdispatch.git;${SWIFT_GIT_PARAMS};name=libdispatch;destsuffix=swift-corelibs-libdispatch-${SWIFT_TAG}"
SRC_URI_SWIFT_STRING_PROCESSING = "${SWIFT_GIT}/swift-experimental-string-processing.git;${SWIFT_GIT_PARAMS};name=stringprocessing;destsuffix=swift-experimental-string-processing-${SWIFT_TAG}"
SRC_URI_SWIFT_SYNTAX = "${SWIFT_GIT}/swift-syntax.git;${SWIFT_GIT_PARAMS};name=syntax;destsuffix=swift-syntax-${SWIFT_SYNTAX_VERSION}"
SRC_URI_SWIFT_CORELIBS_FOUNDATION = "${SWIFT_GIT}/swift-corelibs-foundation.git;${SWIFT_GIT_PARAMS};name=corelibsfoundation;destsuffix=swift-corelibs-foundation-${SWIFT_TAG}"
SRC_URI_SWIFT_FOUNDATION = "${SWIFT_GIT}/swift-foundation.git;${SWIFT_GIT_PARAMS};name=foundation;destsuffix=swift-foundation-${SWIFT_TAG}"
SRC_URI_SWIFT_FOUNDATION_ICU = "${SWIFT_GIT}/swift-foundation-icu.git;${SWIFT_GIT_PARAMS};name=foundationicu;destsuffix=swift-foundation-icu-${SWIFT_TAG}"
SRC_URI_SWIFT_COLLECTIONS = "git://github.com/apple/swift-collections.git;${SWIFT_GIT_PARAMS};name=collections;destsuffix=swift-collections-1.1.6"

# swift-${SWIFT_VERSION}-RELEASE, swift-syntax 604.0.0, swift-collections 1.1.6
SRCREV_swift = "83eb12448e4323abbf81b0153dced2faff02d22f"
SRCREV_cmark = "924936d0427cb25a61169739a7660230bffa6ea6"
SRCREV_libdispatch = "2017c8a7ed86a201604cec55cd648b725afadab5"
SRCREV_stringprocessing = "7581e7b803d2ccc58409e45e1bfde40146cb8e41"
SRCREV_syntax = "050f1a346fbbac0ca2cfb15a95274f7bd1cf0ccf"
SRCREV_corelibsfoundation = "783c06a9327793da1e353bc69205628ff2520a9a"
SRCREV_foundation = "e4049b2d77e2d5d70c4cfcc99fd5eca32e1bcb06"
SRCREV_foundationicu = "f986d0728da0766cf81f371521b909d4a11681d2"
SRCREV_collections = "c11818f3cae0780656baa430b49e7f163f08dffd"

# Only needed if a recipe's PV ever uses the revisions.
SRCREV_FORMAT = "swift"

SWIFT_UNPACKDIR = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}"
SWIFT_SRCDIR = "${SWIFT_UNPACKDIR}/swift-${SWIFT_TAG}"
SWIFT_CMARK_SRCDIR = "${SWIFT_UNPACKDIR}/swift-cmark-${SWIFT_TAG}"
SWIFT_LIBDISPATCH_SRCDIR = "${SWIFT_UNPACKDIR}/swift-corelibs-libdispatch-${SWIFT_TAG}"
SWIFT_STRING_PROCESSING_SRCDIR = "${SWIFT_UNPACKDIR}/swift-experimental-string-processing-${SWIFT_TAG}"
SWIFT_SYNTAX_SRCDIR = "${SWIFT_UNPACKDIR}/swift-syntax-${SWIFT_SYNTAX_VERSION}"

# The Swift recipes need conf/distro/include/swift-toolchain.inc.
python () {
    if d.getVar('SWIFT_UNIFIED_LLVM') != '1':
        raise bb.parse.SkipRecipe('requires conf/distro/include/swift-toolchain.inc')
}

# Everything Swift is compiled by the private clang of the swiftlang fork,
# the clang that swiftc embeds, in every class. Recipes with the Swift classes
# inherit it through swift-base too.
# nooelint: oelint.var.multiinherit
inherit swift-clang
