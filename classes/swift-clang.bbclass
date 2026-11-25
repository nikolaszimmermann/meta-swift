# Compile the C and C++ code of a recipe with the private clang of the
# swiftlang fork (swift-llvm), the clang that swiftc embeds, in every class.
#
# For the target and the SDK host through the swiftclang toolchain
# (conf/distro/include/swift-clang-toolchain.inc). The class overrides win over
# a TOOLCHAIN:class-* = "gcc", as meta-clang sets it. Native recipes compile
# with BUILD_CC, which native.bbclass exports as CC.

TOOLCHAIN:class-target = "swiftclang"
TOOLCHAIN:class-nativesdk = "swiftclang"

BUILD_CC:class-native = "${CCACHE}${SWIFT_LLVM_BINDIR}/clang ${BUILD_CC_ARCH}"
BUILD_CXX:class-native = "${CCACHE}${SWIFT_LLVM_BINDIR}/clang++ ${BUILD_CC_ARCH}"
BUILD_CPP:class-native = "${SWIFT_LLVM_BINDIR}/clang ${BUILD_CC_ARCH} -E"
BUILD_CCLD:class-native = "${SWIFT_LLVM_BINDIR}/clang ${BUILD_CC_ARCH}"

DEPENDS:append = " swift-llvm-native"
