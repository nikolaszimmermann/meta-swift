SUMMARY = "Grand Central Dispatch (libdispatch), C library"
DESCRIPTION = "swift-corelibs-libdispatch without its Swift overlay. The Swift \
runtime's concurrency support links against it; the Dispatch Swift module is \
built on top of it by swift-dispatch."
HOMEPAGE = "https://github.com/swiftlang/swift-corelibs-libdispatch"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1cd73afe3fb82e8d5c899b9d926451d0"

require swift-version.inc

PV = "${SWIFT_VERSION}"

FILESEXTRAPATHS:prepend := "${THISDIR}/libdispatch:"

SRC_URI = "\
    ${SRC_URI_SWIFT_LIBDISPATCH} \
    file://dispatchConfig.cmake \
"
S = "${SWIFT_LIBDISPATCH_SRCDIR}"

inherit cmake


EXTRA_OECMAKE = "\
    -DCMAKE_BUILD_TYPE=Release \
    -DENABLE_SWIFT=OFF \
    -DBUILD_TESTING=OFF \
    -DBUILD_SHARED_LIBS=ON \
"

do_install:append() {
    install -d ${D}${libdir}/cmake/dispatch
    sed -e 's#@LIBDIR@#${@os.path.relpath(d.getVar('libdir'), d.getVar('prefix'))}#g' \
        -e 's#@INCLUDEDIR@#${@os.path.relpath(d.getVar('includedir'), d.getVar('prefix'))}#g' \
        ${SWIFT_UNPACKDIR}/dispatchConfig.cmake > ${D}${libdir}/cmake/dispatch/dispatchConfig.cmake
}

FILES:${PN}-dev += "${libdir}/cmake/dispatch"

# The libraries are unversioned and loaded by that name.
FILES_SOLIBSDEV = ""
FILES:${PN} += "${libdir}/lib*.so"
INSANE_SKIP:${PN} += "dev-so"

BBCLASSEXTEND = "native nativesdk"
