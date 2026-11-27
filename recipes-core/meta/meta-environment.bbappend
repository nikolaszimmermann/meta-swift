# Describe the Swift target in the SDK environment. Build systems pass these
# to swiftc (CMake for example as CMAKE_Swift_COMPILER_TARGET and
# CMAKE_Swift_FLAGS). C and C++ code that Swift imports or links against must
# be compiled by the clang that swiftc embeds: the private clang of
# nativesdk-swift-llvm, which SWIFT_CLANGCC, SWIFT_CLANGCXX and SWIFT_CLANGCPP
# describe. CC and CXX stay the compilers of the distro. SWIFT_CLANGCXX also
# finds the C++ interop support headers of the runtime, like swift-base.
inherit swift-triple

SWIFTSDK ??= "1"

# meta-environment is built for the SDK host (HOST_SYS); the environment
# describes the target.
SWIFT_SDK_TARGET = "${@oe.utils.conditional('TARGET_ARCH', 'arm', d.getVar('TARGET_SYS').replace('arm-', 'armv7-', 1) + 'hf', d.getVar('TARGET_SYS'), d)}"
SWIFT_SDK_DYLD_PREFIX_FLAGS = "${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge', '-Xclang-linker --dyld-prefix=/usr', '', d)}"

# The tune flags for clang: without the gcc flags that clang does not take,
# like meta-clang does for its CLANGCC, and with the changes that the
# swiftclang toolchain makes (conf/distro/include/swift-clang-toolchain.inc).
# meta-environment itself is not built with that toolchain. The tune flags
# that swiftc passes to its clang (SWIFT_TUNE_CCARGS) get the same.
def swift_sdk_clang_flags(d, var):
    remove = set(['-mthumb-interwork', '-mmusl', '-muclibc', '-meb', '-mel'])
    remove.update((d.getVar('SWIFT_CLANG_TUNE_REMOVE') or '').split())
    flags = [f for f in (d.getVar(var) or '').split() if f not in remove]
    flags += (d.getVar('SWIFT_CLANG_TUNE_APPEND') or '').split()
    suffix = d.getVar('SWIFT_CLANG_MARCH_OPTS_APPEND') or ''
    return ' '.join(f + suffix if f.startswith('-mcpu=') else f for f in flags)
SWIFT_SDK_CLANGCC_ARCH = "${@swift_sdk_clang_flags(d, 'TARGET_CC_ARCH')}"
SWIFT_TUNE_CCARGS = "${@swift_sdk_clang_flags(d, 'TUNE_CCARGS')}"
SWIFT_SDK_CLANG = "$OECORE_NATIVE_SYSROOT${prefix_nativesdk}/libexec/swift-llvm/bin"
# The libc-musl override does not apply here: cross-canadian sets the libc
# overrides to the one of the SDK host. Ask the target C library directly.
SWIFT_SDK_XCC_FLAGS = "${@swift_xcc_flags(d)} ${@d.getVar('SWIFT_MUSL_XCC_FLAGS') if d.getVar('TCLIBC') == 'musl' else ''}"

create_sdk_files:append:swiftllvm() {
    if ${@bb.utils.contains('SWIFTSDK', '1', 'true', 'false', d)}; then
        script=${SDK_OUTPUT}/${SDKPATH}/environment-setup-${REAL_MULTIMACH_TARGET_SYS}
        echo 'export SWIFTC="swiftc"' >> $script
        echo 'export SWIFT_TARGET="${SWIFT_SDK_TARGET}"' >> $script
        echo 'export SWIFTFLAGS="-no-toolchain-stdlib-rpath -sdk $SDKTARGETSYSROOT -resource-dir $SDKTARGETSYSROOT${libdir}/swift -use-ld=lld -tools-directory ${SWIFT_SDK_CLANG} ${SWIFT_SDK_DYLD_PREFIX_FLAGS} ${SWIFT_SDK_XCC_FLAGS}"' >> $script
        echo 'export SWIFT_CLANGCC="${SWIFT_SDK_CLANG}/clang -target ${TARGET_SYS} ${SWIFT_SDK_CLANGCC_ARCH} --sysroot=$SDKTARGETSYSROOT"' >> $script
        echo 'export SWIFT_CLANGCXX="${SWIFT_SDK_CLANG}/clang++ -target ${TARGET_SYS} ${SWIFT_SDK_CLANGCC_ARCH} --sysroot=$SDKTARGETSYSROOT -idirafter $SDKTARGETSYSROOT${libdir}/swift"' >> $script
        echo 'export SWIFT_CLANGCPP="${SWIFT_SDK_CLANG}/clang -E -target ${TARGET_SYS} ${SWIFT_SDK_CLANGCC_ARCH} --sysroot=$SDKTARGETSYSROOT"' >> $script
    fi
}
