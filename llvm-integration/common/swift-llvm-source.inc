# Required by every bbappend to a recipe that builds from llvm-project-source.
# Under the swiftllvm override (conf/distro/include/swift-toolchain.inc) the
# source becomes the swiftlang LLVM fork at the tag matching SWIFT_VERSION.

BASEURI:swiftllvm = "git://github.com/swiftlang/llvm-project.git;protocol=https;nobranch=1;name=swiftllvm;destsuffix=${SOURCEDIR}"
SRCREV_swiftllvm = "${SWIFT_LLVM_SRCREV}"
SOURCEDIR:swiftllvm = "llvm-project-${SWIFT_TAG}"

MAJOR_VER:swiftllvm = "${SWIFT_LLVM_MAJOR_VER}"
MINOR_VER:swiftllvm = "${SWIFT_LLVM_MINOR_VER}"
PATCH_VER:swiftllvm = "${SWIFT_LLVM_PATCH_VER}"
VER_SUFFIX:swiftllvm = ""
