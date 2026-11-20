SUMMARY = "Swift programming language compiler"
DESCRIPTION = "The Swift compiler (swiftc/swift-frontend) and its host standard \
library, built by swift-stage1-native against the swiftlang LLVM fork that \
also provides clang, lld and compiler-rt in this build."

require swift-compiler.inc

SRC_URI += "${SRC_URI_SWIFT_IN_SWIFT}"

DEPENDS:append:class-native = " swift-stage1-native"

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
EXTRA_OECMAKE:append:class-native = " -DCMAKE_Swift_COMPILER=${STAGING_DIR_NATIVE}${libexecdir}/swift-stage1/bin/swiftc"

SWIFT_INSTALL_COMPONENTS = "autolink-driver;compiler;clang-resource-dir-symlink;stdlib;sdk-overlay;toolchain-tools;swift-syntax-lib;compiler-swift-syntax-lib;license"

BBCLASSEXTEND = "native nativesdk"

python () {
    if not (bb.data.inherits_class('native', d) or bb.data.inherits_class('nativesdk', d)):
        raise bb.parse.SkipRecipe('the Swift compiler is only built for the build host and the SDK host')
}
