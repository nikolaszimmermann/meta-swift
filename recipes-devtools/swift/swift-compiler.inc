# The Swift compiler, built standalone against the clang/LLVM of this build
# (the swiftlang fork, see conf/distro/include/swift-toolchain.inc).
#
# Swift's compiler is partly written in Swift and has to be bootstrapped,
# see swift-bootstrap.inc. No prebuilt Swift is involved at any point.

SUMMARY = "Swift programming language compiler"
HOMEPAGE = "https://www.swift.org"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=f6c482a0548ea60d6c2e015776534035"

require swift-version.inc

PV = "${SWIFT_VERSION}"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI = "\
    ${SRC_URI_SWIFT} \
    ${SRC_URI_SWIFT_CMARK} \
    ${SRC_URI_SWIFT_LIBDISPATCH} \
    file://0001-cmake-Support-standalone-builds-against-an-installed.patch \
    file://0002-SwiftConfigureSDK-Allow-overriding-the-Linux-target-.patch \
"
SRC_URI_SWIFT_IN_SWIFT = "${SRC_URI_SWIFT_STRING_PROCESSING} ${SRC_URI_SWIFT_SYNTAX}"

S = "${SWIFT_SRCDIR}"

inherit cmake python3native

DEPENDS += "clang-native ninja-native libxml2-native util-linux-native zlib-native ncurses-native libedit-native libdispatch-native"

# Where this stage installs. Bootstrap stages stay out of the regular prefix.
SWIFT_STAGE_PREFIX ?= "${prefix}"

# The LLVM/clang this compiler is built against and embeds. By default the
# clang-native of this build (the swiftlang fork of SWIFT_VERSION).
SWIFT_LLVM_PREFIX ?= "${STAGING_DIR_NATIVE}${prefix}"

# cmark is a build-time only dependency of the compiler (doc comments).
SWIFT_CMARK_BUILDDIR = "${WORKDIR}/build-cmark"

do_configure:prepend() {
    cmake -G Ninja -S ${SWIFT_CMARK_SRCDIR} -B ${SWIFT_CMARK_BUILDDIR} \
        -DCMAKE_BUILD_TYPE=Release \
        -DCMAKE_C_COMPILER="${OECMAKE_C_COMPILER}" \
        -DCMAKE_CXX_COMPILER="${OECMAKE_CXX_COMPILER}" \
        -DCMAKE_C_FLAGS="${CFLAGS}" \
        -DCMAKE_CXX_FLAGS="${CXXFLAGS}" \
        -DCMAKE_POSITION_INDEPENDENT_CODE=ON \
        -DCMARK_TESTS=OFF \
        -DCMARK_SHARED=OFF
    ninja -C ${SWIFT_CMARK_BUILDDIR} ${PARALLEL_MAKE}
}

SWIFT_INSTALL_COMPONENTS ?= "autolink-driver;compiler;clang-resource-dir-symlink;stdlib;sdk-overlay;toolchain-tools;license"

EXTRA_OECMAKE = "\
    -DCMAKE_INSTALL_PREFIX=${SWIFT_STAGE_PREFIX} \
    -DCMAKE_LINKER_TYPE=LLD \
    -DLLVM_DIR=${SWIFT_LLVM_PREFIX}/lib/cmake/llvm \
    -DClang_DIR=${SWIFT_LLVM_PREFIX}/lib/cmake/clang \
    -DSWIFT_PREBUILT_CLANG=ON \
    -DSWIFT_NATIVE_CLANG_TOOLS_PATH=${SWIFT_LLVM_PREFIX}/bin \
    -DSWIFT_NATIVE_LLVM_TOOLS_PATH=${SWIFT_LLVM_PREFIX}/bin \
    -DSWIFT_CLANG_FEATURES_FILE=${SWIFT_LLVM_PREFIX}/share/clang/features.json \
    -DSWIFT_PATH_TO_CMARK_SOURCE=${SWIFT_CMARK_SRCDIR} \
    -DSWIFT_PATH_TO_CMARK_BUILD=${SWIFT_CMARK_BUILDDIR} \
    -DSWIFT_PATH_TO_LIBDISPATCH_SOURCE=${SWIFT_LIBDISPATCH_SRCDIR} \
    -Ddispatch_DIR=${STAGING_LIBDIR_NATIVE}/cmake/dispatch \
    -DBOOTSTRAPPING_MODE=HOSTTOOLS \
    -DSWIFT_INCLUDE_TESTS=OFF \
    -DSWIFT_INCLUDE_DOCS=OFF \
    -DSWIFT_BUILD_EXAMPLES=OFF \
    -DSWIFT_BUILD_SOURCEKIT=OFF \
    -DSWIFT_BUILD_REMOTE_MIRROR=OFF \
    -DSWIFT_BUILD_DYNAMIC_STDLIB=ON \
    -DSWIFT_BUILD_STATIC_STDLIB=OFF \
    -DSWIFT_BUILD_STATIC_SDK_OVERLAY=OFF \
    -DSWIFT_SHOULD_BUILD_EMBEDDED_STDLIB=OFF \
    -DSWIFT_ENABLE_EXPERIMENTAL_CONCURRENCY=ON \
    -DSWIFT_INSTALL_COMPONENTS='${SWIFT_INSTALL_COMPONENTS}' \
"
