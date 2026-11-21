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
DEPENDS:append:class-nativesdk = " swift-native"

SWIFTC = "${STAGING_BINDIR_NATIVE}/swiftc"

# The Swift runtime, its modules and the matching clang resource headers live
# in the target sysroot (installed by swift-stdlib), not next to swiftc.
SWIFT_RESOURCE_DIR = "${STAGING_DIR_HOST}${libdir}/swift"

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
    -sdk ${STAGING_DIR_HOST} \
    -resource-dir ${SWIFT_RESOURCE_DIR} \
    -use-ld=lld \
    ${SWIFT_DYLD_PREFIX_FLAGS} \
    ${@swift_xcc_flags(d)} \
    ${SWIFT_DEBUG_PREFIX_MAP} \
"

# SDK host executables must be linked by ld.bfd of binutils-crosssdk: it
# reserves room in the ELF interpreter path, which the SDK installer rewrites
# when relocating the SDK (lld does not). The interpreter itself comes from
# LDFLAGS (meta-clang's clang.bbclass), which the Swift link does not see.
def swift_sdk_dynamic_linker_flags(d):
    import re
    # LDFLAGS may already be in swiftc form (swift-cmake-tool.bbclass).
    m = re.search(r'(?:-Wl,|-Xlinker\s+)-{1,2}dynamic-linker(?:[,=]|\s+-Xlinker\s+)(\S+)', d.getVar('LDFLAGS') or '')
    return '-Xlinker -dynamic-linker -Xlinker %s' % m.group(1) if m else ''
SWIFTFLAGS:remove:class-nativesdk = "-use-ld=lld"
SWIFTFLAGS:append:class-nativesdk = " -use-ld=bfd ${@swift_sdk_dynamic_linker_flags(d)}"

# Libraries built with Swift depend on the Swift runtime, which is not in the
# default library path: let the linker find it when linking their users.
TARGET_LDFLAGS:append:class-target = " -Wl,-rpath-link,${STAGING_LIBDIR}/swift/linux"
LDFLAGS:append:class-nativesdk = " -Wl,-rpath-link,${STAGING_LIBDIR}/swift/linux"

# Like clang.bbclass, link with the usrmerge dynamic loader path on the target.
SWIFT_DYLD_PREFIX_FLAGS = ""
SWIFT_DYLD_PREFIX_FLAGS:class-target = "${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge', '-Xclang-linker --dyld-prefix=/usr', '', d)}"
