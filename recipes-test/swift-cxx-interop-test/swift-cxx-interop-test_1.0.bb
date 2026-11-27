SUMMARY = "Swift toolchain smoke test: Swift, Concurrency, Dispatch and C++ interop"
DESCRIPTION = "Builds a Swift executable, a Swift library that uses a C++ \
library (std::string, std::vector) and a C++ executable that calls into Swift \
through the generated header, mirroring how WebKit uses Swift."
HOMEPAGE = "https://github.com/nikolaszimmermann/meta-swift"
LICENSE = "MIT"
# The sources are part of this layer and carry no license file of their own.
# nooelint: oelint.var.licenseremotefile
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

DEPENDS += "swift-dispatch"

SRC_URI = "\
    file://CMakeLists.txt \
    file://Greeter.h \
    file://Greeter.cpp \
    file://module.modulemap \
    file://SwiftGreeter.swift \
    file://hello.swift \
    file://main.cpp \
    file://run-ptest \
"
S = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}"

inherit swift-cmake ptest

EXTRA_OECMAKE += "-DSWIFT_PLATFORM=${SWIFT_PLATFORM}"

FILES:${PN} += "${libdir}/libSwiftGreeter.so"
FILES_SOLIBSDEV = ""
RDEPENDS:${PN} += "swift-stdlib swift-dispatch"

# libSwiftGreeter.so is unversioned and loaded by that name.
# nooelint: oelint.vars.insaneskip
INSANE_SKIP:${PN} = "dev-so"
