SUMMARY = "Swift standard library and runtime"
DESCRIPTION = "The Swift runtime, standard library, Concurrency, Synchronization, \
C++ interoperability and the other core modules, built for the target (or \
the SDK host) by swift-native and the private swiftlang clang (swift-llvm)."
HOMEPAGE = "https://www.swift.org"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=f6c482a0548ea60d6c2e015776534035"

require swift-version.inc

DEPENDS += "libdispatch"
# The module map of musl includes <fts.h>, which is not part of musl, and
# links its library (see 0008).
DEPENDS:append:libc-musl = " fts"

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
# The Musl SDK for a distribution built on musl, see EXTRA_OECMAKE:libc-musl.
SRC_URI:append:libc-musl = " file://0008-cmake-Support-a-shared-musl-runtime-for-Musl-distrib.patch"
S = "${SWIFT_SRCDIR}"

inherit swift-base cmake python3native linuxloader

# Upstream points the C++ overlay at the build host's gcc (--gcc-toolchain=/usr,
# SWIFT_SDK_LINUX_CXX_OVERLAY_SWIFT_COMPILE_FLAGS). Here the clang driver finds
# the gcc install and libstdc++ of the target sysroot by itself.
#
# The runtime is built without C++ exceptions, like upstream. Say it
# explicitly instead of relying on the LLVM_ENABLE_EH of the LLVM build.
# Otherwise the Objective-C++ sources of the runtime pull in
# __gnu_objc_personality_v0, which nothing provides on Linux.
#
# The runtime pieces are written in C++ and compiled by the private swiftlang
# clang, the Swift parts by swift-native. Both are the same swiftlang LLVM.
#
# Swift's CMake does not use SWIFTFLAGS: pass it ConciseMagicFile (see
# swift-base.bbclass), so that #file does not put build paths into the
# runtime libraries and the inlinable code of the modules.
SWIFT_THREADING_PACKAGE = "${@oe.utils.conditional('SWIFT_TARGET_ARCH', 'armv7', 'c11', 'linux', d)}"

EXTRA_OECMAKE = "\
    -DCMAKE_BUILD_TYPE=Release \
    -DLLVM_DIR=${SWIFT_LLVM_PREFIX_NATIVE}/lib/cmake/llvm \
    -DClang_DIR=${SWIFT_LLVM_PREFIX_NATIVE}/lib/cmake/clang \
    -DLLVM_TABLEGEN=${SWIFT_LLVM_BINDIR}/llvm-tblgen \
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
    -DSWIFT_NATIVE_CLANG_TOOLS_PATH=${SWIFT_LLVM_BINDIR} \
    -DSWIFT_NATIVE_LLVM_TOOLS_PATH=${SWIFT_LLVM_BINDIR} \
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
    -DSWIFT_STDLIB_EXTRA_SWIFT_COMPILE_FLAGS='${@';'.join(d.getVar('SWIFT_CONCISE_MAGIC_FILE').split())}' \
"

# On musl (target only, the SDK host stays on glibc), build the Musl SDK
# (LINUX_STATIC) instead, which Swift itself only builds as the static Linux
# SDK. SWIFT_MUSL_DISTRIBUTION builds it shared, for the triple and sysroot of
# the distribution, in lib/swift/musl (SWIFT_PLATFORM), where the compiler
# looks for the runtime of a triple such as aarch64-poky-linux-musl (see
# 0008). These definitions come after those of the Linux SDK above, and CMake
# takes the last one. The host SDK stays LINUX: Swift only configures the Musl
# SDK next to it, and no host tools are built.
EXTRA_OECMAKE:append:libc-musl = " \
    -DSWIFT_MUSL_DISTRIBUTION=ON \
    -DSWIFT_SDKS=LINUX_STATIC \
    -DSWIFT_PRIMARY_VARIANT_SDK=LINUX_STATIC \
    -DSWIFT_SDK_LINUX_STATIC_ARCHITECTURES=${SWIFT_TARGET_ARCH} \
    -DSWIFT_SDK_LINUX_STATIC_ARCH_${SWIFT_TARGET_ARCH}_PATH=${STAGING_DIR_HOST} \
    -DSWIFT_SDK_LINUX_STATIC_ARCH_${SWIFT_TARGET_ARCH}_TRIPLE=${SWIFT_TARGET} \
    -DSWIFT_SDK_LINUX_STATIC_CXX_OVERLAY_SWIFT_COMPILE_FLAGS= \
    -DSWIFT_STANDARD_LIBRARY_SWIFT_FLAGS='${@swift_xcc_flags_cmake_list(d)};${@';'.join(d.getVar('SWIFT_MUSL_XCC_FLAGS').split())}' \
"

FILES:${PN} = "\
    ${libdir}/swift/${SWIFT_PLATFORM}/lib*${SOLIBSDEV} \
"
FILES:${PN}-dev = "\
    ${libdir}/swift/${SWIFT_PLATFORM}/*.swiftmodule \
    ${libdir}/swift/${SWIFT_PLATFORM}/*.modulemap \
    ${libdir}/swift/${SWIFT_PLATFORM}/*.h \
    ${libdir}/swift/${SWIFT_PLATFORM}/*.a \
    ${libdir}/swift/${SWIFT_PLATFORM}/${SWIFT_TARGET_ARCH} \
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
# nooelint: oelint.vars.insaneskip
INSANE_SKIP:${PN} = "dev-so"
# The C++ interop overlays (libswiftCxx*.a) are only usable with their modules.
# nooelint: oelint.vars.insaneskip
INSANE_SKIP:${PN}-dev = "dev-elf staticdev"

# Only the dynamic stdlib is built, but the static resource directory (clang
# headers, link arguments) is installed regardless.
do_install:append() {
    rm -rf ${D}${libdir}/swift_static
}

# gyb marks where the lines of its outputs come from with comments that carry
# the absolute path of the template in the build directory. The installed
# libc module map and header do not need them.
do_install:append() {
    find ${D}${libdir}/swift \( -name '*.modulemap' -o -name '*.h' \) -type f \
        -exec sed -i '/^\/\/ ###sourceLocation(file: /d' {} +
}

# The C++ interoperability overlays (Cxx, CxxStdlib and their static
# libraries) and the headers of the generated Swift-to-C++ headers
# (swiftToCxx) are not part of any install component; install them like the
# swift.org toolchains ship them.
do_install:append() {
    platform=${SWIFT_PLATFORM}
    install -d ${D}${libdir}/swift/$platform ${D}${libdir}/swift/swiftToCxx
    cp -R --no-dereference --preserve=mode,links ${B}/lib/swift/$platform/Cxx.swiftmodule ${B}/lib/swift/$platform/CxxStdlib.swiftmodule ${D}${libdir}/swift/$platform/
    install -m 0644 ${B}/lib/swift/$platform/libswiftCxx.a ${B}/lib/swift/$platform/libswiftCxxStdlib.a ${D}${libdir}/swift/$platform/
    for f in ${B}/lib/swift/$platform/libcxx*; do
        [ -e "$f" ] && install -m 0644 "$f" ${D}${libdir}/swift/$platform/
    done
    install -m 0644 ${S}/lib/PrintAsClang/_SwiftCxxInteroperability.h \
                    ${S}/lib/PrintAsClang/_SwiftStdlibCxxOverlay.h \
                    ${S}/lib/PrintAsClang/experimental-interoperability-version.json \
                    ${D}${libdir}/swift/swiftToCxx/
}

FILES:${PN}-dev += "${libdir}/swift/swiftToCxx"

# Make the Swift runtime libraries visible to the dynamic loader.
do_install:append:libc-glibc() {
    install -d ${D}${sysconfdir}/ld.so.conf.d
    echo "${libdir}/swift/${SWIFT_PLATFORM}" > ${D}${sysconfdir}/ld.so.conf.d/swift.conf
}
FILES:${PN}:append:libc-glibc = " ${sysconfdir}/ld.so.conf.d/swift.conf"
# The header of fts, which the module map of musl includes.
RDEPENDS:${PN}-dev:append:libc-musl = " fts-dev"

# musl reads the library path from /etc/ld-musl-<arch>.path, which the musl
# package owns.
SWIFT_MUSL_LDSO_PATH = "${sysconfdir}/ld-musl-${@get_musl_loader_arch(d)}.path"
pkg_postinst:${PN}:libc-musl() {
    grep -qxF '${libdir}/swift/${SWIFT_PLATFORM}' $D${SWIFT_MUSL_LDSO_PATH} ||
        echo '${libdir}/swift/${SWIFT_PLATFORM}' >> $D${SWIFT_MUSL_LDSO_PATH}
}
pkg_postrm:${PN}:libc-musl() {
    sed -i '\#^${libdir}/swift/${SWIFT_PLATFORM}$#d' $D${SWIFT_MUSL_LDSO_PATH}
}

BBCLASSEXTEND = "nativesdk"
