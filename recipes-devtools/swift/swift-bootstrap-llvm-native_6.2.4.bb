SUMMARY = "LLVM, clang and lld of Swift 6.2, only used to bootstrap the Swift compiler"
DESCRIPTION = "Swift 6.4 can not be built from C++ alone, and Swift 6.2 (the \
last release that can) only builds against its own LLVM fork. This is that \
fork, built for the build host into a private prefix. It is never packaged \
and nothing but swift-stage{0,1,2}-native uses it."
HOMEPAGE = "https://github.com/swiftlang/llvm-project"
LICENSE = "Apache-2.0-with-LLVM-exception"
LIC_FILES_CHKSUM = "file://llvm/LICENSE.TXT;md5=8a15a0759ef07f2682d2ba4b893c9afe"

SWIFT_BOOTSTRAP_TAG = "swift-${PV}-RELEASE"

SRC_URI = "https://github.com/swiftlang/llvm-project/archive/refs/tags/${SWIFT_BOOTSTRAP_TAG}.tar.gz;downloadfilename=llvm-project-${SWIFT_BOOTSTRAP_TAG}.tar.gz"
SRC_URI[sha256sum] = "8e02cf861773b8b5b829b208ba892f240a8c3c6d336190c5eca32e393b783e4a"

S = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}/llvm-project-${SWIFT_BOOTSTRAP_TAG}"
OECMAKE_SOURCEPATH = "${S}/llvm"

inherit cmake python3native native

DEPENDS += "ninja-native zlib-native"

SWIFT_BOOTSTRAP_LLVM_PREFIX = "${libexecdir}/swift-bootstrap-llvm"

def swift_bootstrap_llvm_target(d):
    arch = d.getVar('BUILD_ARCH')
    return {'x86_64': 'X86', 'aarch64': 'AArch64'}.get(arch, 'X86;AArch64')

EXTRA_OECMAKE = "\
    -DCMAKE_BUILD_TYPE=Release \
    -DCMAKE_INSTALL_PREFIX=${SWIFT_BOOTSTRAP_LLVM_PREFIX} \
    -DLLVM_ENABLE_PROJECTS='clang;lld' \
    -DLLVM_TARGETS_TO_BUILD='${@swift_bootstrap_llvm_target(d)}' \
    -DLLVM_ENABLE_ASSERTIONS=OFF \
    -DLLVM_INCLUDE_TESTS=OFF \
    -DLLVM_INCLUDE_EXAMPLES=OFF \
    -DLLVM_INCLUDE_BENCHMARKS=OFF \
    -DLLVM_INCLUDE_DOCS=OFF \
    -DCLANG_INCLUDE_TESTS=OFF \
    -DLLVM_ENABLE_ZSTD=OFF \
    -DLLVM_ENABLE_LIBXML2=OFF \
    -DLLVM_ENABLE_LIBEDIT=OFF \
    -DLLVM_ENABLE_TERMINFO=OFF \
    -DLLVM_ENABLE_BINDINGS=OFF \
"

do_install:append() {
    # Swift relies on LLVM's private, generated config.h.
    install -Dm 0644 ${B}/include/llvm/Config/config.h \
        ${D}${SWIFT_BOOTSTRAP_LLVM_PREFIX}/include/llvm/Config/config.h
}

python () {
    if d.getVar('SWIFT_UNIFIED_LLVM') != '1':
        raise bb.parse.SkipRecipe('requires conf/distro/include/swift-toolchain.inc')
}
