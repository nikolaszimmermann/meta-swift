# nooelint: oelint.var.mandatoryvar.SRC_URI
SUMMARY = "Links named after the target to the swiftlang clang of the SDK"
DESCRIPTION = "Links like ${TARGET_PREFIX}clang to the private clang of \
nativesdk-swift-llvm, for SWIFT_CLANGCC and SWIFT_CLANGCXX of the SDK \
environment. clang takes the target from its name, like the links of \
meta-clang. So tools that keep only the first word of the compiler, like \
CMake for assembly, still compile for the target."
HOMEPAGE = "https://github.com/swiftlang/llvm-project"
LICENSE = "MIT"
# nooelint: oelint.var.licenseremotefile
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PN = "swift-clang-cross-canadian-${TRANSLATED_TARGET_ARCH}"

inherit cross-canadian

do_configure[noexec] = "1"
do_compile[noexec] = "1"

# Next to clang, which finds its resource directory and the other tools of
# swift-llvm relative to itself.
SWIFT_SDK_CLANG_DIR = "${prefix}/libexec/swift-llvm/bin"

# cross-canadian sets TARGET_OS to linux (linux-gnueabi for arm), so that the
# package is the same for every C library, and lists the others in
# CANADIANEXTRAOS. meta-environment keeps the TARGET_SYS of the target, like
# aarch64-poky-linux-musl. Link each of them, as cross_canadian_bindirlinks
# does for the bindir of a cross-canadian recipe.
do_install() {
    install -d ${D}${SWIFT_SDK_CLANG_DIR}
    for os in ${TARGET_OS} ${CANADIANEXTRAOS}; do
        for tool in clang clang++; do
            ln -sf $tool ${D}${SWIFT_SDK_CLANG_DIR}/${TARGET_ARCH}${TARGET_VENDOR}-$os-$tool
        done
    done
}

FILES:${PN} = "${SWIFT_SDK_CLANG_DIR}"
RDEPENDS:${PN} = "nativesdk-swift-llvm"
