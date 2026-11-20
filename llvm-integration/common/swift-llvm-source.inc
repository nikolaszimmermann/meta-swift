# Required by every bbappend to a recipe that builds from llvm-project-source.
# Under the swiftllvm override (conf/distro/include/swift-toolchain.inc) the
# source becomes the swiftlang LLVM fork at the tag matching SWIFT_VERSION.

SWIFT_LLVM_TARBALL = "llvm-project-${SWIFT_TAG}.tar.gz"
BASEURI:swiftllvm = "https://github.com/swiftlang/llvm-project/archive/refs/tags/${SWIFT_TAG}.tar.gz;name=swiftllvm;downloadfilename=${SWIFT_LLVM_TARBALL}"
SOURCEDIR:swiftllvm = "llvm-project-${SWIFT_TAG}"

MAJOR_VER:swiftllvm = "${SWIFT_LLVM_MAJOR_VER}"
MINOR_VER:swiftllvm = "${SWIFT_LLVM_MINOR_VER}"
PATCH_VER:swiftllvm = "${SWIFT_LLVM_PATCH_VER}"
VER_SUFFIX:swiftllvm = ""
