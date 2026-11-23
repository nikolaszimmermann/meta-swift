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
    ${SWIFT_GIT}/swift-driver.git;${SWIFT_GIT_PARAMS};name=driver;destsuffix=swift-driver-${SWIFT_TAG} \
    ${SWIFT_GIT}/swift-llbuild.git;${SWIFT_GIT_PARAMS};name=llbuild;destsuffix=swift-llbuild-${SWIFT_TAG} \
    ${SWIFT_GIT}/swift-tools-support-core.git;${SWIFT_GIT_PARAMS};name=tsc;destsuffix=swift-tools-support-core-${SWIFT_TAG} \
    git://github.com/apple/swift-argument-parser.git;${SWIFT_GIT_PARAMS};name=argumentparser;destsuffix=swift-argument-parser-1.6.1 \
    file://swift-driver-install-rpath.patch \
    file://swift-driver-exclude-vendored-from-install.patch \
    file://swift-driver-do-not-install-internal-libraries.patch \
    file://swift-driver-valid-module-names.patch \
"
# swift-${SWIFT_TAG} of the swiftlang repositories, swift-argument-parser 1.6.1
SRCREV_driver = "174567a5681a9a949bcd52f821deb8fa65105434"
SRCREV_llbuild = "ab6421207b9e4971c94e97c5832a3d8a4cae9092"
SRCREV_tsc = "d45c8b38d2824498b7863d3d5f0227937a53c177"
SRCREV_argumentparser = "309a47b2b1d9b5e991f36961c983ecec72275be3"
SRCREV_FORMAT = "driver"

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

# Only the build and SDK hosts need it (see DESCRIPTION).
BBCLASSEXTEND = "native nativesdk"

python () {
    if not (bb.data.inherits_class('native', d) or bb.data.inherits_class('nativesdk', d)):
        raise bb.parse.SkipRecipe('swift-driver is only built for the build and SDK hosts')
}
