SUMMARY = "swift-driver, the Swift compiler driver written in Swift"
DESCRIPTION = "The driver swift.org toolchains use. swiftc hands over to it \
when it is installed next to it; the legacy C++ driver of swift-frontend can \
not link the way CMake (< 3.29) drives Swift, which the SDK's CMake does."
HOMEPAGE = "https://github.com/swiftlang/swift-driver"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=f6c482a0548ea60d6c2e015776534035"

require swift-version.inc

PV = "${SWIFT_VERSION}"

FILESEXTRAPATHS:prepend := "${THISDIR}/swift-driver:"

# argument-parser, llbuild and tools-support-core are only used by the driver
# and are linked into it statically, like the container SDK does.
SRC_URI = "\
    ${SWIFT_GH}/swift-driver/archive/refs/tags/${SWIFT_TAG}.tar.gz;name=driver;downloadfilename=swift-driver-${SWIFT_TAG}.tar.gz \
    ${SWIFT_GH}/swift-llbuild/archive/refs/tags/${SWIFT_TAG}.tar.gz;name=llbuild;downloadfilename=swift-llbuild-${SWIFT_TAG}.tar.gz \
    ${SWIFT_GH}/swift-tools-support-core/archive/refs/tags/${SWIFT_TAG}.tar.gz;name=tsc;downloadfilename=swift-tools-support-core-${SWIFT_TAG}.tar.gz \
    https://github.com/apple/swift-argument-parser/archive/refs/tags/1.6.1.tar.gz;name=argumentparser;downloadfilename=swift-argument-parser-1.6.1.tar.gz \
    file://swift-driver-install-rpath.patch \
    file://swift-driver-exclude-vendored-from-install.patch \
    file://swift-driver-do-not-install-internal-libraries.patch \
"
SRC_URI[driver.sha256sum] = "7511962f6f0aad7a288f1a3130e8edb5d98ff905545897d71edfe0d287aabf93"
SRC_URI[llbuild.sha256sum] = "74350fec385b7b88a2f225fc7029752afc2b32f3f7953481dc8aa16299afa128"
SRC_URI[tsc.sha256sum] = "e4157ca16a5a5b31788c7455a8dddc5b2ed1b747fd523acedba2dd4f735465bf"
SRC_URI[argumentparser.sha256sum] = "d2fbb15886115bb2d9bfb63d4c1ddd4080cbb4bfef2651335c5d3b9dd5f3c8ba"

S = "${SWIFT_UNPACKDIR}/swift-driver-${SWIFT_TAG}"

inherit swift-cmake

DEPENDS += "swift-foundation swift-dispatch sqlite3 ncurses"

EXTRA_OECMAKE = "\
    -DCMAKE_BUILD_TYPE=Release \
    -DBUILD_SHARED_LIBS=OFF \
    -DBUILD_TESTING=OFF \
    -DLLBUILD_SUPPORT_BINDINGS=Swift \
    -Ddispatch_DIR=${STAGING_LIBDIR}/cmake/dispatch \
    -DFETCHCONTENT_FULLY_DISCONNECTED=ON \
    -DFETCHCONTENT_SOURCE_DIR_ARGUMENTPARSER=${SWIFT_UNPACKDIR}/swift-argument-parser-1.6.1 \
    -DFETCHCONTENT_SOURCE_DIR_LLBUILD=${SWIFT_UNPACKDIR}/swift-llbuild-${SWIFT_TAG} \
    -DFETCHCONTENT_SOURCE_DIR_TOOLSSUPPORTCORE=${SWIFT_UNPACKDIR}/swift-tools-support-core-${SWIFT_TAG} \
"

FILES:${PN} += "${bindir}"

# llbuild installs its static Swift bindings, which are linked into the
# driver already.
do_install:append() {
    rm -rf ${D}${libdir}/swift/pm
    rmdir --ignore-fail-on-non-empty ${D}${libdir}/swift
}

# Only the SDK host needs it (see DESCRIPTION).
BBCLASSEXTEND = "nativesdk"

python () {
    if not bb.data.inherits_class('nativesdk', d):
        raise bb.parse.SkipRecipe('swift-driver is only built for the SDK host')
}
