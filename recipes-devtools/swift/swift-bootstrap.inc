# The bootstrap compilers.
#
# The Swift compiler is partly written in Swift. Swift 6.2 is the last
# release that builds from C++ alone: upstream stopped maintaining the C++-only
# bootstrap during 6.3 development, see
# https://forums.swift.org/t/dropping-the-requirement-for-c-only-bootstrapping/87739
# Swift 6.4 errors out in lib/SILOptimizer/PassManager/PassManager.cpp without
# SWIFT_ENABLE_SWIFT_IN_SWIFT. The Swift sources of a release also only build
# against the LLVM fork of that release. So, like nixpkgs, this
# layer bootstraps through Swift 6.2, built against its own LLVM 19 fork
# (swift-bootstrap-llvm-native):
#   swift-stage0-native  6.2, C++ only
#   swift-stage1-native  6.2, with swift-syntax and macros, built by stage 0
#   swift-stage2-native  6.2, full compiler, built by stage 1
#   swift-stage3-native  6.4 against clang-native, built by stage 2
#   swift-native         6.4 against clang-native, built by stage 3
# None of the stages is packaged; only swift-native and nativesdk-swift are.

SWIFT_VERSION = "6.2.4"
SWIFT_SYNTAX_VERSION = "swift-6.2.4-RELEASE"

# swift-${SWIFT_VERSION}-RELEASE of each repository, swift-syntax included.
SRCREV_swift = "ee343b46aef81c3ac7c5d7960cb35a41a88c5a9b"
SRCREV_cmark = "5d9bdaa4228b381639fff09403e39a04926e2dbe"
SRCREV_stringprocessing = "91177e6225c63e885872b83d48e254b1270fa15a"
SRCREV_syntax = "5a87516fc3dddbd23cb76358eb489915ee86b444"
SRCREV_libdispatch = "2df91f94651f2d924d7506c9d14685929386d779"

# nooelint: oelint.var.inheritlast.native - the recipes require this file last
inherit_defer native

DEPENDS += "swift-bootstrap-llvm-native"

# Built by, and embedding, the bootstrap LLVM. Swift's runtime has to be built
# with the very clang it embeds, so this is also the C/C++ compiler.
SWIFT_LLVM_PREFIX = "${STAGING_DIR_NATIVE}${libexecdir_native}/swift-bootstrap-llvm"
OECMAKE_C_COMPILER = "${SWIFT_LLVM_PREFIX}/bin/clang"
OECMAKE_CXX_COMPILER = "${SWIFT_LLVM_PREFIX}/bin/clang++"

FILESEXTRAPATHS:prepend := "${THISDIR}/files/bootstrap-6.2:"
SRC_URI:remove = "file://0002-SwiftConfigureSDK-Allow-overriding-the-Linux-target-.patch file://0003-sil-missing-headers.patch"
SRC_URI += "\
    file://0004-sil-missing-headers.patch \
    file://0011-Fix-missing-cstdint.patch \
    file://0012-Fix-missing-null-terminator.patch \
"
