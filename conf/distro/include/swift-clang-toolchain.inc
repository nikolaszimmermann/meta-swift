# The "swiftclang" toolchain: the private clang of the swiftlang LLVM fork
# (swift-llvm) as the C/C++ compiler of a recipe. A recipe opts in with
# TOOLCHAIN = "swiftclang". The Swift classes and the Swift recipes do that.
# Every other recipe is left alone.

SWIFT_LLVM_PREFIX_NATIVE = "${STAGING_DIR_NATIVE}${libexecdir_native}/swift-llvm"
SWIFT_LLVM_BINDIR = "${SWIFT_LLVM_PREFIX_NATIVE}/bin"

# The override for the settings below. It is only active for recipes with
# TOOLCHAIN = "swiftclang". meta-clang adds toolchain-${TOOLCHAIN} the same
# way. Where it does, the second entry does no harm.
OVERRIDES =. "${@'toolchain-swiftclang:' if d.getVar('TOOLCHAIN') == 'swiftclang' else ''}"
OVERRIDES[vardepsexclude] += "TOOLCHAIN"

# The cmake class only takes the first word of CC as the compiler and passes
# HOST_CC_ARCH as flags. So the target goes into HOST_CC_ARCH: a -target in CC
# would be lost, and clang would compile for the build host. For nativesdk,
# HOST_SYS is the SDK host.
HOST_CC_ARCH:prepend:toolchain-swiftclang:class-target = "-target ${HOST_SYS} "
HOST_CC_ARCH:prepend:toolchain-swiftclang:class-nativesdk = "-target ${HOST_SYS} "
CC:toolchain-swiftclang = "${CCACHE}${SWIFT_LLVM_BINDIR}/clang ${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}"
CXX:toolchain-swiftclang = "${CCACHE}${SWIFT_LLVM_BINDIR}/clang++ ${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}"
CPP:toolchain-swiftclang = "${CCACHE}${SWIFT_LLVM_BINDIR}/clang ${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS} -E"
CCLD:toolchain-swiftclang = "${CCACHE}${SWIFT_LLVM_BINDIR}/clang ${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}"
CCACHE_COMPILERCHECK:toolchain-swiftclang ?= "%compiler% -v"

# LD, AR, NM, STRIP, OBJCOPY and the other binutils stay the GNU tools of the
# distro. The default cross compiler (gcc-cross) stays a dependency: it brings
# binutils, and the gcc install, crt files, libgcc and libstdc++ headers in
# the sysroot that the driver of the private clang finds.
BASE_DEFAULT_DEPS:append:toolchain-swiftclang:class-target = " swift-llvm-native"
BASE_DEFAULT_DEPS:append:toolchain-swiftclang:class-nativesdk = " swift-llvm-native"

# gcc-only flags that the distro puts into the compiler flags.
DEBUG_PREFIX_MAP:remove:toolchain-swiftclang = "-fcanon-prefix-map"

# The rest of what meta-clang (classes/clang.bbclass) sets for
# toolchain-clang, for the architectures this layer supports.
#
# clang does not know the big.LITTLE tunes of gcc: use the LITTLE core. The
# dynamic loader is in /usr/lib on usrmerge systems. Silence the warnings
# about arguments that clang does not use in a given step. The SDK
# environment (meta-environment.bbappend) applies the same to the tune flags
# of its clang.
SWIFT_CLANG_TUNE_REMOVE = "\
    -mcpu=cortex-a57.cortex-a53${TUNE_CCARGS_MARCH_OPTS} \
    -mcpu=cortex-a72.cortex-a53${TUNE_CCARGS_MARCH_OPTS} \
    -mcpu=cortex-a15.cortex-a7${TUNE_CCARGS_MARCH_OPTS} \
    -mcpu=cortex-a17.cortex-a7${TUNE_CCARGS_MARCH_OPTS} \
    -mcpu=cortex-a72.cortex-a35${TUNE_CCARGS_MARCH_OPTS} \
    -mcpu=cortex-a73.cortex-a53${TUNE_CCARGS_MARCH_OPTS} \
    -mcpu=cortex-a75.cortex-a55${TUNE_CCARGS_MARCH_OPTS} \
    -mcpu=cortex-a76.cortex-a55${TUNE_CCARGS_MARCH_OPTS} \
"
SWIFT_CLANG_TUNE_APPEND = "\
    ${@bb.utils.contains_any('TUNE_FEATURES', 'cortexa72-cortexa53 cortexa57-cortexa53 cortexa73-cortexa53', '-mcpu=cortex-a53${TUNE_CCARGS_MARCH_OPTS}', '', d)} \
    ${@bb.utils.contains_any('TUNE_FEATURES', 'cortexa15-cortexa7 cortexa17-cortexa7', '-mcpu=cortex-a7${TUNE_CCARGS_MARCH_OPTS}', '', d)} \
    ${@bb.utils.contains_any('TUNE_FEATURES', 'cortexa72-cortexa35', '-mcpu=cortex-a35${TUNE_CCARGS_MARCH_OPTS}', '', d)} \
    ${@bb.utils.contains_any('TUNE_FEATURES', 'cortexa75-cortexa55 cortexa76-cortexa55', '-mcpu=cortex-a55${TUNE_CCARGS_MARCH_OPTS}', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge', '--dyld-prefix=/usr', '', d)} \
    -Qunused-arguments \
"
TUNE_CCARGS:remove:toolchain-swiftclang = "${SWIFT_CLANG_TUNE_REMOVE}"
TUNE_CCARGS:append:toolchain-swiftclang = " ${SWIFT_CLANG_TUNE_APPEND}"
# Work around https://github.com/llvm/llvm-project/issues/85699 for the
# 64-bit rpi3 and rpi4 tunes.
TUNE_CCARGS_MARCH_OPTS:append:toolchain-swiftclang = "${@bb.utils.contains_any('DEFAULTTUNE', 'cortexa72 cortexa53', '+nocrypto', '', d)}"

# SDK host binaries: clang does not know the loader of the SDK, gcc-crosssdk
# has it built in.
LDFLAGS:toolchain-swiftclang:class-nativesdk = "${BUILDSDK_LDFLAGS} \
    -Wl,-rpath-link,${STAGING_LIBDIR}/.. \
    -Wl,-rpath,${libdir}/.."
LDFLAGS:append:toolchain-swiftclang:class-nativesdk:x86-64 = " -Wl,-dynamic-linker,${base_libdir}/ld-linux-x86-64.so.2"
LDFLAGS:append:toolchain-swiftclang:class-nativesdk:aarch64 = " -Wl,-dynamic-linker,${base_libdir}/ld-linux-aarch64.so.1"
