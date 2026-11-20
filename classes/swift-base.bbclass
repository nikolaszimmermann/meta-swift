# Compile Swift for the target with swift-native.
#
# The compiler and the C/C++ toolchain are the same swiftlang LLVM (see
# conf/distro/include/swift-toolchain.inc), so swiftc's embedded clang finds the
# Yocto sysroot, the gcc install and libstdc++ exactly like clang-cross does.
#
# This class only sets up the compiler. Recipes that use the Swift standard
# library should inherit swift (or swift-cmake), which also adds the runtime.

TOOLCHAIN = "clang"

DEPENDS:append:class-target = " swift-native"

# The Swift triple: Yocto's TARGET_SYS, except on 32-bit arm where the bare
# "arm" arch is not a Swift platform and the hard-float ABI has to be in the
# triple (Yocto conveys it through -mfloat-abi=hard instead).
SWIFT_TARGET_ARCH = "${@oe.utils.conditional('TARGET_ARCH', 'arm', 'armv7', d.getVar('TARGET_ARCH'), d)}"
SWIFT_TARGET = "${@oe.utils.conditional('TARGET_ARCH', 'arm', d.getVar('TARGET_SYS').replace('arm-', 'armv7-', 1) + 'hf', d.getVar('TARGET_SYS'), d)}"

SWIFTC = "${STAGING_BINDIR_NATIVE}/swiftc"

# The Swift runtime, its modules and the matching clang resource headers live
# in the target sysroot (installed by swift-stdlib), not next to swiftc.
SWIFT_RESOURCE_DIR = "${STAGING_DIR_TARGET}${libdir}/swift"

def swift_xcc_flags(d):
    import shlex
    return ' '.join('-Xcc ' + f for f in shlex.split(d.getVar('TARGET_CC_ARCH') or ''))

SWIFT_DEBUG_PREFIX_MAP = "\
    -file-prefix-map ${S}=${TARGET_DBGSRC_DIR} \
    -file-prefix-map ${B}=${TARGET_DBGSRC_DIR} \
    -file-prefix-map ${STAGING_DIR_HOST}= \
    -file-prefix-map ${STAGING_DIR_NATIVE}= \
"

SWIFTFLAGS ?= "\
    -sdk ${STAGING_DIR_TARGET} \
    -resource-dir ${SWIFT_RESOURCE_DIR} \
    -use-ld=lld \
    ${@swift_xcc_flags(d)} \
    ${SWIFT_DEBUG_PREFIX_MAP} \
"
