SUMMARY = "Swift standard library and runtime"
DESCRIPTION = "The Swift runtime, standard library, Concurrency, Synchronization, \
C++ interoperability and the other core modules, built for the target (or \
the SDK host) by swift-native and the swiftlang clang of this build."
HOMEPAGE = "https://www.swift.org"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=f6c482a0548ea60d6c2e015776534035"

require swift-version.inc

PV = "${SWIFT_VERSION}"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI = "\
    ${SRC_URI_SWIFT} \
    ${SRC_URI_SWIFT_STRING_PROCESSING} \
    ${SRC_URI_SWIFT_SYNTAX} \
    ${SRC_URI_SWIFT_LIBDISPATCH} \
    file://0001-cmake-Support-standalone-builds-against-an-installed.patch \
    file://0002-SwiftConfigureSDK-Allow-overriding-the-Linux-target-.patch \
    file://0007-runtime-Fix-the-crash-handler-build-for-32-bit-ARM-L.patch \
"
S = "${SWIFT_SRCDIR}"

inherit swift-base cmake python3native

DEPENDS += "libdispatch clang-native"

# Upstream points the C++ overlay at the build host's gcc (--gcc-toolchain=/usr,
# SWIFT_SDK_LINUX_CXX_OVERLAY_SWIFT_COMPILE_FLAGS). Here the clang driver finds
# the gcc install and libstdc++ of the target sysroot by itself.
#
# The runtime is built without C++ exceptions, like upstream. Upstream gets
# that from LLVM's CMake (LLVM_ENABLE_EH=OFF); meta-clang's LLVM has EH on, so
# say it explicitly. Otherwise the Objective-C++ sources of the runtime pull in
# __gnu_objc_personality_v0, which nothing provides on Linux.
#
# The runtime pieces are written in C++ and compiled by clang-cross; the
# Swift parts by swift-native. Both are the same swiftlang LLVM.
SWIFT_THREADING_PACKAGE = "${@oe.utils.conditional('SWIFT_TARGET_ARCH', 'armv7', 'c11', 'linux', d)}"

EXTRA_OECMAKE = "\
    -DCMAKE_BUILD_TYPE=Release \
    -DLLVM_DIR=${STAGING_LIBDIR_NATIVE}/cmake/llvm \
    -DClang_DIR=${STAGING_LIBDIR_NATIVE}/cmake/clang \
    -DLLVM_TABLEGEN=${STAGING_BINDIR_NATIVE}/llvm-tblgen \
    -DSWIFT_INCLUDE_TOOLS=OFF \
    -DSWIFT_INCLUDE_TESTS=OFF \
    -DSWIFT_INCLUDE_DOCS=OFF \
    -DSWIFT_BUILD_EXAMPLES=OFF \
    -DSWIFT_BUILD_REMOTE_MIRROR=OFF \
    -DSWIFT_BUILD_SOURCEKIT=OFF \
    -DSWIFT_BUILD_STDLIB_EXTRA_TOOLCHAIN_CONTENT=OFF \
    -DSWIFT_BUILD_RUNTIME_WITH_HOST_COMPILER=ON \
    -DSWIFT_PREBUILT_CLANG=ON \
    -DSWIFT_NATIVE_SWIFT_TOOLS_PATH=${STAGING_BINDIR_NATIVE} \
    -DSWIFT_NATIVE_CLANG_TOOLS_PATH=${STAGING_BINDIR_NATIVE} \
    -DSWIFT_NATIVE_LLVM_TOOLS_PATH=${STAGING_BINDIR_NATIVE} \
    -DSWIFT_SDKS=LINUX \
    -DSWIFT_HOST_VARIANT_SDK=LINUX \
    -DSWIFT_HOST_VARIANT_ARCH=${SWIFT_TARGET_ARCH} \
    -DSWIFT_PRIMARY_VARIANT_SDK=LINUX \
    -DSWIFT_PRIMARY_VARIANT_ARCH=${SWIFT_TARGET_ARCH} \
    -DSWIFT_SDK_LINUX_ARCH_${SWIFT_TARGET_ARCH}_PATH=${STAGING_DIR_HOST} \
    -DSWIFT_SDK_LINUX_ARCH_${SWIFT_TARGET_ARCH}_TRIPLE=${SWIFT_TARGET} \
    -DSWIFT_SDK_LINUX_ARCH_${SWIFT_TARGET_ARCH}_LIBC_INCLUDE_DIRECTORY=${STAGING_INCDIR} \
    -DSWIFT_SDK_LINUX_ARCH_${SWIFT_TARGET_ARCH}_LIBC_ARCHITECTURE_INCLUDE_DIRECTORY=${STAGING_INCDIR} \
    -Ddispatch_DIR=${STAGING_LIBDIR}/cmake/dispatch \
    -DSWIFT_PATH_TO_LIBDISPATCH_SOURCE=${SWIFT_LIBDISPATCH_SRCDIR} \
    -DSWIFT_THREADING_PACKAGE=${SWIFT_THREADING_PACKAGE} \
    -DSWIFT_SDK_LINUX_CXX_OVERLAY_SWIFT_COMPILE_FLAGS= \
    -DSWIFT_STDLIB_EXTRA_C_COMPILE_FLAGS=-fno-exceptions \
    -DSWIFT_BUILD_DYNAMIC_STDLIB=ON \
    -DSWIFT_BUILD_DYNAMIC_SDK_OVERLAY=ON \
    -DSWIFT_BUILD_STATIC_STDLIB=OFF \
    -DSWIFT_BUILD_STATIC_SDK_OVERLAY=OFF \
    -DSWIFT_SHOULD_BUILD_EMBEDDED_STDLIB=OFF \
    -DSWIFT_PATH_TO_STRING_PROCESSING_SOURCE=${SWIFT_STRING_PROCESSING_SRCDIR} \
    -DSWIFT_PATH_TO_SWIFT_SYNTAX_SOURCE=${SWIFT_SYNTAX_SRCDIR} \
    -DSWIFT_ENABLE_EXPERIMENTAL_CONCURRENCY=ON \
    -DSWIFT_ENABLE_EXPERIMENTAL_STRING_PROCESSING=ON \
    -DSWIFT_ENABLE_EXPERIMENTAL_OBSERVATION=ON \
    -DSWIFT_ENABLE_EXPERIMENTAL_DISTRIBUTED=ON \
    -DSWIFT_ENABLE_EXPERIMENTAL_DIFFERENTIABLE_PROGRAMMING=ON \
    -DSWIFT_ENABLE_EXPERIMENTAL_CXX_INTEROP=ON \
    -DSWIFT_ENABLE_SYNCHRONIZATION=ON \
    -DSWIFT_ENABLE_VOLATILE=ON \
    -DSWIFT_ENABLE_BACKTRACING=ON \
    -DSWIFT_ENABLE_RUNTIME_MODULE=ON \
    -DSWIFT_STDLIB_INSTALL_ONLY_CLANG_RESOURCE_HEADERS=ON \
    -DSWIFT_INSTALL_COMPONENTS='stdlib;sdk-overlay;clang-builtin-headers;license' \
    -DSWIFT_STANDARD_LIBRARY_SWIFT_FLAGS='${@swift_xcc_flags_cmake_list(d)}' \
"

BBCLASSEXTEND = "nativesdk"

FILES:${PN} = "\
    ${libdir}/swift/linux/lib*${SOLIBSDEV} \
"
FILES:${PN}-dev = "\
    ${libdir}/swift/linux/*.swiftmodule \
    ${libdir}/swift/linux/*.modulemap \
    ${libdir}/swift/linux/*.h \
    ${libdir}/swift/linux/*.a \
    ${libdir}/swift/linux/${SWIFT_TARGET_ARCH} \
    ${libdir}/swift/shims \
    ${libdir}/swift/apinotes \
    ${libdir}/swift/clang \
    ${libdir}/swift/module.modulemap \
    ${libdir}/swift/_InternalSwiftStaticMirror \
    ${libdir}/swift/os \
    ${includedir} \
    ${datadir} \
"
# The Swift runtime libraries are unversioned .so files loaded by SONAME.
INSANE_SKIP:${PN} = "dev-so"
# The C++ interop overlays (libswiftCxx*.a) are only usable with their modules.
INSANE_SKIP:${PN}-dev = "dev-elf staticdev"

# Only the dynamic stdlib is built, but the static resource directory (clang
# headers, link arguments) is installed regardless.
do_install:append() {
    rm -rf ${D}${libdir}/swift_static
}

# The C++ interoperability overlays (Cxx, CxxStdlib and their static
# libraries) and the headers of the generated Swift-to-C++ headers
# (swiftToCxx) are not part of any install component; install them like the
# swift.org toolchains ship them.
do_install:append() {
    install -d ${D}${libdir}/swift/linux ${D}${libdir}/swift/swiftToCxx
    cp -R --no-dereference --preserve=mode,links ${B}/lib/swift/linux/Cxx.swiftmodule ${B}/lib/swift/linux/CxxStdlib.swiftmodule ${D}${libdir}/swift/linux/
    install -m 0644 ${B}/lib/swift/linux/libswiftCxx.a ${B}/lib/swift/linux/libswiftCxxStdlib.a ${D}${libdir}/swift/linux/
    for f in ${B}/lib/swift/linux/libcxx*; do
        [ -e "$f" ] && install -m 0644 "$f" ${D}${libdir}/swift/linux/
    done
    install -m 0644 ${S}/lib/PrintAsClang/_SwiftCxxInteroperability.h \
                    ${S}/lib/PrintAsClang/_SwiftStdlibCxxOverlay.h \
                    ${S}/lib/PrintAsClang/experimental-interoperability-version.json \
                    ${D}${libdir}/swift/swiftToCxx/
}

FILES:${PN}-dev += "${libdir}/swift/swiftToCxx"

# Make the Swift runtime libraries visible to the dynamic loader.
do_install:append() {
    install -d ${D}${sysconfdir}/ld.so.conf.d
    echo "${libdir}/swift/linux" > ${D}${sysconfdir}/ld.so.conf.d/swift.conf
}
FILES:${PN} += "${sysconfdir}/ld.so.conf.d/swift.conf"
