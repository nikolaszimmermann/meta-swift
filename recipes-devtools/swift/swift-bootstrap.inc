# The bootstrap compilers.
#
# Swift's compiler is partly written in Swift. Since Swift 6.4 it can no
# longer be built from C++ alone (lib/SILOptimizer/PassManager/PassManager.cpp
# errors out without SWIFT_ENABLE_SWIFT_IN_SWIFT), and the Swift sources of a
# release only build against that release's LLVM fork. So, like nixpkgs, this
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

SRC_URI[swift.sha256sum] = "3b29b8aecfd6f401a2f46b28947850a53ac1cda46e54380fbd6d391c4f718fad"
SRC_URI[cmark.sha256sum] = "6012b59d842b2864623901b1e9d527d5b314af87858e0af8661e191d92d14922"
SRC_URI[stringprocessing.sha256sum] = "b0ab328fe9f80f08b71ee6c5e81c7c3a001c5d924b207b5a16d8027815ba1998"
SRC_URI[syntax.sha256sum] = "53e644fb9c824cea726775e7b48ee5217323922917a7e1bc54fdc86e13058f80"
SRC_URI[libdispatch.sha256sum] = "09707e4ccdd19d4f23e356bf032b28fbf2b447c3645aff26775e25a97e314486"

inherit native

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
