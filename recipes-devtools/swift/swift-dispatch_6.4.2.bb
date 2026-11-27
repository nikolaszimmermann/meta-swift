SUMMARY = "Dispatch, the Swift overlay of libdispatch"
DESCRIPTION = "The Dispatch Swift module and libswiftDispatch, built on top of \
the C libdispatch from the libdispatch recipe."
HOMEPAGE = "https://github.com/swiftlang/swift-corelibs-libdispatch"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1cd73afe3fb82e8d5c899b9d926451d0"

require swift-version.inc

PV = "${SWIFT_VERSION}"

FILESEXTRAPATHS:prepend := "${THISDIR}/libdispatch:"

SRC_URI = "\
    ${SRC_URI_SWIFT_LIBDISPATCH} \
    file://0001-benchmark-Make-the-integer-to-floating-point-convers.patch \
"
S = "${SWIFT_LIBDISPATCH_SRCDIR}"

inherit swift-cmake

EXTRA_OECMAKE = "\
    -DCMAKE_BUILD_TYPE=Release \
    -DENABLE_SWIFT=ON \
    -DBUILD_TESTING=OFF \
    -DBUILD_SHARED_LIBS=ON \
"
# musl declares program_invocation_short_name in errno.h only with
# _GNU_SOURCE, which the CMake check of libdispatch leaves out, although it is
# what the check turns on. Without it, getprogname() has no implementation.
# Same as in libdispatch.
EXTRA_OECMAKE:append:libc-musl = " -DHAVE_DECL_PROGRAM_INVOCATION_SHORT_NAME=1"
# On musl, sys/cdefs.h comes from bsd-headers, which warns that it is
# non-standard, and libdispatch builds with -Werror. It needs the BSD macros
# of the header, so only drop the warning.
CFLAGS:append:libc-musl = " -Wno-#warnings"
CXXFLAGS:append:libc-musl = " -Wno-#warnings"

# Only ship the Swift overlay and the module maps in lib/swift/{dispatch,os,
# Block} it imports; the C library and its usr/include headers come from
# libdispatch, which this build links against at runtime.
do_install:append() {
    rm -f ${D}${libdir}/libdispatch.so* ${D}${libdir}/libBlocksRuntime.so*
    rm -f ${D}${libdir}/swift/linux/libdispatch.so* ${D}${libdir}/swift/linux/libBlocksRuntime.so*
    rm -rf ${D}${includedir} ${D}${datadir}/man
}
do_install:append:libc-musl() {
    rm -f ${D}${libdir}/swift/${SWIFT_PLATFORM}/libdispatch.so* ${D}${libdir}/swift/${SWIFT_PLATFORM}/libBlocksRuntime.so*
}

FILES:${PN} = "${libdir}/swift/${SWIFT_PLATFORM}/libswiftDispatch.so"
# Everything but the runtime libraries: modules, module maps and headers.
# lib/swift/[!lm]* is all of lib/swift but the platform directory (linux or
# musl).
FILES:${PN}-dev = "\
    ${libdir}/swift/${SWIFT_PLATFORM}/*.swiftmodule \
    ${libdir}/swift/${SWIFT_PLATFORM}/*.swiftdoc \
    ${libdir}/swift/${SWIFT_PLATFORM}/*.swiftinterface \
    ${libdir}/swift/${SWIFT_PLATFORM}/${SWIFT_TARGET_ARCH} \
    ${libdir}/swift/[!lm]* \
    ${libdir}/swift/lib*.a \
    ${libdir}/swift/${SWIFT_PLATFORM}/*.a \
    ${libdir}/cmake \
"
# nooelint: oelint.vars.insaneskip
INSANE_SKIP:${PN} = "dev-so"

# The build and SDK hosts need them to run swift-driver.
BBCLASSEXTEND = "native nativesdk"
