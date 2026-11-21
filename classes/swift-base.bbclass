# Compile Swift for the target with swift-native.
#
# The compiler and the C/C++ toolchain are the same swiftlang LLVM (see
# conf/distro/include/swift-toolchain.inc), so swiftc's embedded clang finds the
# Yocto sysroot, the gcc install and libstdc++ exactly like clang-cross does.
#
# This class only sets up the compiler. Recipes that use the Swift standard
# library should inherit swift (or swift-cmake), which also adds the runtime.

inherit swift-triple

TOOLCHAIN = "clang"

DEPENDS:append:class-target = " swift-native"

SWIFTC = "${STAGING_BINDIR_NATIVE}/swiftc"

# The Swift runtime, its modules and the matching clang resource headers live
# in the target sysroot (installed by swift-stdlib), not next to swiftc.
SWIFT_RESOURCE_DIR = "${STAGING_DIR_TARGET}${libdir}/swift"

SWIFT_DEBUG_PREFIX_MAP = "\
    -file-prefix-map ${S}=${TARGET_DBGSRC_DIR} \
    -file-prefix-map ${B}=${TARGET_DBGSRC_DIR} \
    -file-prefix-map ${STAGING_DIR_HOST}= \
    -file-prefix-map ${STAGING_DIR_NATIVE}= \
"

# On the target the Swift runtime lives in ${libdir}/swift/linux, which
# swift-stdlib adds to the dynamic loader's search path. Don't let swiftc embed
# an rpath into the build sysroot.
SWIFTFLAGS ?= "\
    -no-toolchain-stdlib-rpath \
    -sdk ${STAGING_DIR_TARGET} \
    -resource-dir ${SWIFT_RESOURCE_DIR} \
    -use-ld=lld \
    ${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge', '-Xclang-linker --dyld-prefix=/usr', '', d)} \
    ${@swift_xcc_flags(d)} \
    ${SWIFT_DEBUG_PREFIX_MAP} \
"

# Libraries built with Swift depend on the Swift runtime, which is not in the
# default library path: let the linker find it when linking their users.
TARGET_LDFLAGS:append:class-target = " -Wl,-rpath-link,${STAGING_LIBDIR}/swift/linux"
