# Compile Swift for the target with swift-native.
#
# swiftc embeds the clang of the private swiftlang LLVM (swift-llvm), and the
# C/C++ code of the recipe is compiled by that very clang: TOOLCHAIN =
# "swiftclang" (conf/distro/include/swift-clang-toolchain.inc). So the
# importer of swiftc and the C++ compiler agree, and both find the Yocto
# sysroot, the gcc install and libstdc++ the same way.
#
# This class only sets up the compiler. Recipes that use the Swift standard
# library should inherit swift (or swift-cmake), which also adds the runtime.

inherit swift-triple swift-clang

# A TOOLCHAIN set for a single recipe, such as TOOLCHAIN:pn-<recipe> = "clang"
# in local.conf, would win over swift-clang and mix two clangs.
python () {
    if bb.data.inherits_class('native', d):
        return
    if d.getVar('TOOLCHAIN') != 'swiftclang':
        bb.fatal('%s inherits a Swift class, but TOOLCHAIN is "%s". Swift needs the '
                 'swiftclang toolchain: remove the TOOLCHAIN setting for it.'
                 % (d.getVar('PN'), d.getVar('TOOLCHAIN')))
}

DEPENDS:append:class-target = " swift-native"
DEPENDS:append:class-nativesdk = " swift-native"
DEPENDS:append:class-native = " swift-native"

SWIFTC = "${STAGING_BINDIR_NATIVE}/swiftc"

# The Swift runtime, its modules and the matching clang resource headers live
# in the target sysroot (installed by swift-stdlib), not next to swiftc.
SWIFT_RESOURCE_DIR = "${STAGING_DIR_HOST}${libdir}/swift"
SWIFT_RESOURCE_DIR:class-native = "${STAGING_LIBDIR_NATIVE}/swift"

SWIFT_DEBUG_PREFIX_MAP = "\
    -file-prefix-map ${S}=${TARGET_DBGSRC_DIR} \
    -file-prefix-map ${B}=${TARGET_DBGSRC_DIR} \
    -file-prefix-map ${STAGING_DIR_HOST}= \
    -file-prefix-map ${STAGING_DIR_NATIVE}= \
    ${SWIFT_CONCISE_MAGIC_FILE} \
"

# In the Swift 5 language mode #file is the absolute source path, which
# -file-prefix-map does not remap: it ends up in the binary wherever #file is
# used, including as the default argument of fatalError(), precondition() and
# assert(). Give #file its Swift 6 meaning, "Module/File.swift", like #fileID.
# Code that needs the full path can still use #filePath.
SWIFT_CONCISE_MAGIC_FILE = "-enable-upcoming-feature ConciseMagicFile"

# On the target the Swift runtime lives in ${libdir}/swift/linux, which
# swift-stdlib adds to the dynamic loader's search path. Don't let swiftc embed
# an rpath into the build sysroot.
SWIFTFLAGS ?= "\
    -no-toolchain-stdlib-rpath \
    -sdk ${STAGING_DIR_HOST} \
    -resource-dir ${SWIFT_RESOURCE_DIR} \
    -use-ld=lld \
    -tools-directory ${SWIFT_LLVM_BINDIR} \
    ${SWIFT_DYLD_PREFIX_FLAGS} \
    ${@swift_xcc_flags(d)} \
    ${SWIFT_DEBUG_PREFIX_MAP} \
"

# SDK host executables must be linked by ld.bfd of binutils-crosssdk: it
# reserves room in the ELF interpreter path, which the SDK installer rewrites
# when relocating the SDK (lld does not). The interpreter itself comes from
# LDFLAGS (conf/distro/include/swift-clang-toolchain.inc), which the Swift
# link does not see.
def swift_sdk_dynamic_linker_flags(d):
    import re
    # LDFLAGS may already be in swiftc form (swift-cmake-tool.bbclass).
    m = re.search(r'(?:-Wl,|-Xlinker\s+)-{1,2}dynamic-linker(?:[,=]|\s+-Xlinker\s+)(\S+)', d.getVar('LDFLAGS') or '')
    return '-Xlinker -dynamic-linker -Xlinker %s' % m.group(1) if m else ''
# Native tools run from the build sysroot, so they keep swiftc's rpath to the
# runtime there (OE makes it relative when staging) and need no -sdk.
SWIFTFLAGS:class-native = "\
    -resource-dir ${SWIFT_RESOURCE_DIR} \
    -use-ld=lld \
    -tools-directory ${SWIFT_LLVM_BINDIR} \
    ${@swift_xcc_flags(d)} \
    ${SWIFT_DEBUG_PREFIX_MAP} \
"

SWIFTFLAGS:remove:class-nativesdk = "-use-ld=lld"
SWIFTFLAGS:append:class-nativesdk = " -use-ld=bfd ${@swift_sdk_dynamic_linker_flags(d)}"

# The C++ headers that swiftc generates for Swift code include the C++ interop
# support headers (swiftToCxx). They look for them in the include path first,
# then relative to the clang resource directory, where a Swift toolchain has
# them. The private clang has its resource directory in its own prefix, so
# add the swiftToCxx headers of the runtime in the sysroot to the include
# path, last, so that they can not shadow other headers.
SWIFT_CXX_INTEROP_INCLUDES = "-idirafter ${STAGING_LIBDIR}/swift"
SWIFT_CXX_INTEROP_INCLUDES:class-native = "-idirafter ${STAGING_LIBDIR_NATIVE}/swift"
CXXFLAGS:append = " ${SWIFT_CXX_INTEROP_INCLUDES}"

# Libraries built with Swift depend on the Swift runtime, which is not in the
# default library path: let the linker find it when linking their users.
TARGET_LDFLAGS:append:class-target = " -Wl,-rpath-link,${STAGING_LIBDIR}/swift/linux"
LDFLAGS:append:class-nativesdk = " -Wl,-rpath-link,${STAGING_LIBDIR}/swift/linux"
LDFLAGS:append:class-native = " -Wl,-rpath-link,${STAGING_LIBDIR_NATIVE}/swift/linux"

# Like clang.bbclass, link with the usrmerge dynamic loader path on the target.
SWIFT_DYLD_PREFIX_FLAGS = ""
SWIFT_DYLD_PREFIX_FLAGS:class-target = "${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge', '-Xclang-linker --dyld-prefix=/usr', '', d)}"
