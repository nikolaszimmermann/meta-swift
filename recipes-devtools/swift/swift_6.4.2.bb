SUMMARY = "Swift programming language compiler"
DESCRIPTION = "The Swift compiler (swiftc/swift-frontend) and its host standard \
library, built by swift-stage3-native against the private LLVM of the swiftlang \
fork (swift-llvm), which also provides the clang and lld it uses."

require swift-compiler.inc

DEPENDS:append:class-native = " swift-stage3-native"

SRC_URI += "\
    ${SRC_URI_SWIFT_IN_SWIFT} \
    file://0006-ClangImporter-Do-not-track-files-injected-into-the-C.patch \
"

EXTRA_OECMAKE += "\
    -DSWIFT_ENABLE_SWIFT_IN_SWIFT=ON \
    -DSWIFT_BUILD_SWIFT_SYNTAX=ON \
    -DSWIFT_PATH_TO_SWIFT_SYNTAX_SOURCE=${SWIFT_SYNTAX_SRCDIR} \
    -DSWIFT_PATH_TO_STRING_PROCESSING_SOURCE=${SWIFT_STRING_PROCESSING_SRCDIR} \
    -DSWIFT_ENABLE_EXPERIMENTAL_STRING_PROCESSING=ON \
    -DSWIFT_ENABLE_EXPERIMENTAL_OBSERVATION=ON \
    -DSWIFT_ENABLE_EXPERIMENTAL_DISTRIBUTED=ON \
    -DSWIFT_ENABLE_EXPERIMENTAL_DIFFERENTIABLE_PROGRAMMING=ON \
    -DSWIFT_ENABLE_EXPERIMENTAL_CXX_INTEROP=ON \
    -DSWIFT_ENABLE_SYNCHRONIZATION=ON \
    -DSWIFT_ENABLE_VOLATILE=ON \
    -DSWIFT_ENABLE_BACKTRACING=ON \
    -DSWIFT_ENABLE_RUNTIME_MODULE=ON \
"
SWIFT_BUILD_COMPILER_DIR:class-native = "${STAGING_DIR_NATIVE}${libexecdir_native}/swift-stage3"
EXTRA_OECMAKE:append:class-native = " -DCMAKE_Swift_COMPILER=${SWIFT_BUILD_COMPILER_DIR}/bin/swiftc"

SWIFT_INSTALL_COMPONENTS = "autolink-driver;compiler;clang-resource-dir-symlink;stdlib;sdk-overlay;toolchain-tools;swift-syntax-lib;compiler-swift-syntax-lib;license"

# The SDK host compiler is cross-compiled: swift-native compiles its Swift
# parts for the SDK host, the private clang (swiftclang toolchain) its C++
# parts, and it embeds (links) nativesdk-swift-llvm, the same swiftlang LLVM.
# Everything uses the SDK triple (${HOST_SYS}): clang only finds the SDK's
# GCC installation for OE vendor triples. With swift-syntax enabled, Swift
# forces BOOTSTRAPPING_MODE=HOSTTOOLS on Linux, so the compiler's Swift parts
# are built against an existing runtime: the SDK host runtime from
# nativesdk-swift-stdlib, which also ships it (SWIFT_PATH_TO_SWIFT_SDK and
# -resource-dir point there instead of next to swift-native). CMake's own Swift compiler
# check needs the runtime for the build host, so it is skipped. The system
# include directory of the SDK sysroot must not become a -I for Swift (0004).
# SDK host executables are linked by ld.bfd, which keeps them relocatable
# (see swift-base.bbclass).
SRC_URI:append:class-nativesdk = " \
    file://0004-cmake-Do-not-add-system-include-directories-of-an-in.patch \
    file://0005-SwiftCompilerSources-Use-SwiftShims-from-SWIFT_PATH_.patch \
"
SWIFT_INSTALL_COMPONENTS:class-nativesdk = "autolink-driver;compiler;toolchain-tools;swift-syntax-lib;compiler-swift-syntax-lib"
# nooelint: oelint.var.order.DEPENDS - with the other SDK host settings
DEPENDS:append:class-nativesdk = " swift-native nativesdk-swift-stdlib nativesdk-swift-llvm nativesdk-libdispatch nativesdk-libxml2 nativesdk-zlib nativesdk-ncurses nativesdk-libedit nativesdk-util-linux"
SWIFT_LLVM_PREFIX:class-nativesdk = "${STAGING_DIR_HOST}${libexecdir}/swift-llvm"
EXTRA_OECMAKE:remove:class-nativesdk = "\
    -DCMAKE_LINKER_TYPE=LLD \
    -DSWIFT_NATIVE_CLANG_TOOLS_PATH=${SWIFT_LLVM_PREFIX}/bin \
    -DSWIFT_NATIVE_LLVM_TOOLS_PATH=${SWIFT_LLVM_PREFIX}/bin \
"
# CMake honours CMAKE_LINKER_TYPE from 3.29 on (scarthgap has 3.28, wrynose
# 4.x), and it knows no BFD linker type for Swift. bfd is the default linker
# of the toolchain anyway, and Swift links through SWIFT_USE_LINKER.
SWIFT_SDK_HOST_LINKER_TYPE = "${@'' if 'wrynose' in (d.getVar('LAYERSERIES_CORENAMES') or '').split() else '-DCMAKE_LINKER_TYPE=BFD'}"
EXTRA_OECMAKE:append:class-nativesdk = "\
    -DSWIFT_NATIVE_CLANG_TOOLS_PATH=${SWIFT_LLVM_BINDIR} \
    -DSWIFT_NATIVE_LLVM_TOOLS_PATH=${SWIFT_LLVM_BINDIR} \
    -DSWIFT_NATIVE_SWIFT_TOOLS_PATH=${STAGING_BINDIR_NATIVE} \
    -DCMAKE_Swift_COMPILER=${STAGING_BINDIR_NATIVE}/swiftc \
    -DCMAKE_Swift_COMPILER_TARGET=${HOST_SYS} \
    -DSWIFT_SDK_LINUX_ARCH_${HOST_ARCH}_TRIPLE=${HOST_SYS} \
    -DSWIFT_SDK_LINUX_ARCH_${HOST_ARCH}_PATH=${STAGING_DIR_HOST} \
    -DCMAKE_Swift_COMPILER_WORKS=ON \
    -DCMAKE_Swift_FLAGS='-sdk ${STAGING_DIR_HOST} -resource-dir ${STAGING_LIBDIR}/swift' \
    -DSWIFT_BUILD_STDLIB=OFF \
    ${SWIFT_SDK_HOST_LINKER_TYPE} \
    -DSWIFT_USE_LINKER=bfd \
    -DSWIFT_PATH_TO_SWIFT_SDK=${STAGING_DIR_HOST} \
    -DSWIFT_COMPILER_SOURCES_SDK_FLAGS='-sdk;${STAGING_DIR_HOST};-resource-dir;${STAGING_LIBDIR}/swift' \
    -DSWIFT_BUILD_SDK_OVERLAY=OFF \
    -DLLVM_TABLEGEN=${SWIFT_LLVM_BINDIR}/llvm-tblgen \
    -DSWIFT_HOST_VARIANT_SDK=LINUX \
    -DSWIFT_HOST_VARIANT_ARCH=${HOST_ARCH} \
    -Ddispatch_DIR=${STAGING_LIBDIR}/cmake/dispatch \
"

# The runtime, including the C++ interop overlays and the swiftToCxx headers,
# is shipped by nativesdk-swift-stdlib. The compiler build installs the
# overlays regardless (also as static modules), drop them here.
do_install:append:class-nativesdk() {
    rm -rf ${D}${libdir}/swift_static ${D}${libdir}/swift/linux ${D}${libdir}/swift/swiftToCxx
}

# The compiler's shared libraries are unversioned and loaded by the tools,
# they are not development files.
FILES_SOLIBSDEV:class-nativesdk = ""
FILES:${PN}:append:class-nativesdk = " ${libdir}/lib*.so"
# nooelint: oelint.vars.insaneskip
INSANE_SKIP:${PN}:append:class-nativesdk = " dev-so"

# Build host tools that the cross-compiled SDK host compiler runs from
# SWIFT_NATIVE_SWIFT_TOOLS_PATH, but no install component ships.
SWIFT_NATIVE_HELPER_TOOLS = "swift-compatibility-symbols swift-def-to-strings-converter swift-serialize-diagnostics"
do_install:append:class-native() {
    for t in ${SWIFT_NATIVE_HELPER_TOOLS}; do
        install -m 0755 ${B}/bin/$t ${D}${bindir}/
    done
}

# The driver loads libSwiftScan from lib/swift/host, where upstream installs a
# symlink to the library in lib/swift/host/compiler. OE rewrites the library's
# rpath to the shared libLLVM relative to the directory of the file itself,
# which does not resolve when loaded through the symlink: the driver then warns
# about an incompatible libSwiftScan and falls back to running swift-frontend
# for the dependency scan. Install the library in lib/swift/host and the
# symlink in lib/swift/host/compiler; its upstream rpath works from both.
do_install:append() {
    host=${D}${libdir}/swift/host
    if [ -L $host/lib_InternalSwiftScan.so ]; then
        rm $host/lib_InternalSwiftScan.so
        mv $host/compiler/lib_InternalSwiftScan.so $host/
        ln -s ../lib_InternalSwiftScan.so $host/compiler/lib_InternalSwiftScan.so
    fi
}

BBCLASSEXTEND = "native nativesdk"

python () {
    if not (bb.data.inherits_class('native', d) or bb.data.inherits_class('nativesdk', d)):
        raise bb.parse.SkipRecipe('the Swift compiler is only built for the build host and the SDK host')
}
