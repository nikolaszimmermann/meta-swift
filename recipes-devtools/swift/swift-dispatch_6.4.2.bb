SUMMARY = "Dispatch, the Swift overlay of libdispatch"
DESCRIPTION = "The Dispatch Swift module and libswiftDispatch, built on top of \
the C libdispatch from the libdispatch recipe."
HOMEPAGE = "https://github.com/swiftlang/swift-corelibs-libdispatch"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1cd73afe3fb82e8d5c899b9d926451d0"

require swift-version.inc

PV = "${SWIFT_VERSION}"

SRC_URI = "${SRC_URI_SWIFT_LIBDISPATCH}"
S = "${SWIFT_LIBDISPATCH_SRCDIR}"

inherit swift-cmake

EXTRA_OECMAKE = "\
    -DENABLE_SWIFT=ON \
    -DBUILD_TESTING=OFF \
    -DBUILD_SHARED_LIBS=ON \
"

# Only ship the Swift overlay; the C library and headers come from
# libdispatch, which this build links against at runtime.
do_install:append() {
    rm -f ${D}${libdir}/libdispatch.so* ${D}${libdir}/libBlocksRuntime.so*
    rm -f ${D}${libdir}/swift/linux/libdispatch.so* ${D}${libdir}/swift/linux/libBlocksRuntime.so*
    rm -rf ${D}${includedir}/dispatch ${D}${includedir}/os ${D}${includedir}/Block.h
    rm -rf ${D}${libdir}/swift/dispatch ${D}${libdir}/swift/os ${D}${libdir}/swift/Block
    rm -rf ${D}${datadir}/man
}

FILES:${PN} = "${libdir}/swift/linux/libswiftDispatch.so"
FILES:${PN}-dev = "${libdir}/swift"
INSANE_SKIP:${PN} = "dev-so"
