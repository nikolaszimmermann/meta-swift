SUMMARY = "Foundation for Swift on Linux"
DESCRIPTION = "swift-corelibs-foundation with swift-foundation \
(FoundationEssentials, FoundationInternationalization) and its ICU, \
FoundationNetworking and FoundationXML."
HOMEPAGE = "https://github.com/swiftlang/swift-corelibs-foundation"
LICENSE = "Apache-2.0 & Unicode-3.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1cd73afe3fb82e8d5c899b9d926451d0"

require swift-version.inc

DEPENDS += "swift-dispatch swift-foundation-macros-native curl libxml2"

PV = "${SWIFT_VERSION}"

SRC_URI = "\
    ${SRC_URI_SWIFT_CORELIBS_FOUNDATION} \
    ${SRC_URI_SWIFT_FOUNDATION} \
    ${SRC_URI_SWIFT_FOUNDATION_ICU} \
    ${SRC_URI_SWIFT_COLLECTIONS} \
    ${SRC_URI_SWIFT_SYNTAX} \
"
S = "${SWIFT_UNPACKDIR}/swift-corelibs-foundation-${SWIFT_TAG}"

inherit swift-cmake

EXTRA_OECMAKE = "\
    -DCMAKE_BUILD_TYPE=Release \
    -DBUILD_SHARED_LIBS=ON \
    -DBUILD_TESTING=OFF \
    -DFOUNDATION_BUILD_TOOLS=OFF \
    -DFOUNDATION_BUILD_NETWORKING=ON \
    -Ddispatch_DIR=${STAGING_LIBDIR}/cmake/dispatch \
    -DFETCHCONTENT_FULLY_DISCONNECTED=ON \
    -DFETCHCONTENT_SOURCE_DIR_SWIFTSYNTAX=${SWIFT_SYNTAX_SRCDIR} \
    -D_SwiftFoundation_SourceDIR=${SWIFT_UNPACKDIR}/swift-foundation-${SWIFT_TAG} \
    -D_SwiftFoundationICU_SourceDIR=${SWIFT_UNPACKDIR}/swift-foundation-icu-${SWIFT_TAG} \
    -D_SwiftCollections_SourceDIR=${SWIFT_UNPACKDIR}/swift-collections-1.1.6 \
    -DSwiftFoundation_MACRO=${STAGING_LIBDIR_NATIVE}/swift/host/plugins \
"

FILES:${PN} = "${libdir}/swift/linux/lib*${SOLIBSDEV}"
# Everything but the runtime libraries: modules, module maps and headers.
FILES:${PN}-dev = "\
    ${libdir}/swift/linux/*.swiftmodule \
    ${libdir}/swift/linux/*.swiftdoc \
    ${libdir}/swift/linux/*.swiftinterface \
    ${libdir}/swift/linux/${SWIFT_TARGET_ARCH} \
    ${libdir}/swift/[!l]* \
    ${libdir}/swift/lib*.a \
    ${libdir}/swift/linux/*.a \
    ${libdir}/cmake \
"
# nooelint: oelint.vars.insaneskip
INSANE_SKIP:${PN} = "dev-so"

# The build and SDK hosts need them to run swift-driver.
BBCLASSEXTEND = "native nativesdk"
