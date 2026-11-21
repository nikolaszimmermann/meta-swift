SUMMARY = "CMake for building Swift projects (private to meta-swift)"
DESCRIPTION = "A newer CMake than the release's cmake-native, for the recipes \
that compile Swift with CMake. CMake 3.29 introduced the Swift build model \
(policy CMP0157) that compiles and links in separate steps; older CMake links \
Swift through one combined compiler invocation that only swift-driver \
understands, and swift-driver is itself one of these Swift CMake projects. \
Installed into a private prefix, so the distro's cmake-native is untouched."
HOMEPAGE = "https://cmake.org"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://Copyright.txt;md5=718f05155941b33862726348d3cd46ce"

SRC_URI = "https://github.com/Kitware/CMake/releases/download/v${PV}/cmake-${PV}.tar.gz"
SRC_URI[sha256sum] = "5f3fd5a54dfa65602bdbed64f981a72673cc19f2d304cc2955cf0dfa0cfd8272"

S = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}/cmake-${PV}"
B = "${WORKDIR}/build"

inherit native

DEPENDS = "zlib-native"

CMAKE_SWIFT_PREFIX = "${libexecdir}/cmake-swift"

do_configure() {
    cd ${B}
    ${S}/bootstrap --prefix=${CMAKE_SWIFT_PREFIX} --parallel=${@oe.utils.cpu_count()} \
        --no-qt-gui --no-debugger -- \
        -DCMAKE_BUILD_TYPE=Release -DBUILD_TESTING=OFF -DCMAKE_USE_OPENSSL=OFF
}

do_compile() {
    oe_runmake -C ${B}
}

do_install() {
    oe_runmake -C ${B} install DESTDIR=${D}
    rm -rf ${D}${CMAKE_SWIFT_PREFIX}/doc ${D}${CMAKE_SWIFT_PREFIX}/share/bash-completion \
           ${D}${CMAKE_SWIFT_PREFIX}/share/emacs ${D}${CMAKE_SWIFT_PREFIX}/share/vim \
           ${D}${CMAKE_SWIFT_PREFIX}/share/aclocal
}

python () {
    if d.getVar('SWIFT_UNIFIED_LLVM') != '1':
        raise bb.parse.SkipRecipe('requires conf/distro/include/swift-toolchain.inc')
}
