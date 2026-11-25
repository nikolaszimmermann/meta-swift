# Describe the Swift target in the SDK environment. Build systems pass these
# to swiftc (CMake for example as CMAKE_Swift_COMPILER_TARGET and
# CMAKE_Swift_FLAGS). C and C++ code that Swift imports or links against must
# be compiled by the clang that swiftc embeds: the private clang of
# nativesdk-swift-llvm, which SWIFT_CLANGCC, SWIFT_CLANGCXX and SWIFT_CLANGCPP
# describe. CC and CXX stay the compilers of the distro.
inherit swift-triple

SWIFTSDK ??= "1"

# meta-environment is built for the SDK host (HOST_SYS); the environment
# describes the target.
SWIFT_SDK_TARGET = "${@oe.utils.conditional('TARGET_ARCH', 'arm', d.getVar('TARGET_SYS').replace('arm-', 'armv7-', 1) + 'hf', d.getVar('TARGET_SYS'), d)}"
SWIFT_SDK_DYLD_PREFIX_FLAGS = "${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge', '-Xclang-linker --dyld-prefix=/usr', '', d)}"

# The tune flags of gcc that clang does not take, like meta-clang does for its
# CLANGCC.
SWIFT_SDK_CLANGCC_ARCH = "${TARGET_CC_ARCH}${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge', ' --dyld-prefix=/usr', '', d)}"
SWIFT_SDK_CLANGCC_ARCH:remove = "-mthumb-interwork -mmusl -muclibc -meb -mel"
SWIFT_SDK_CLANG = "$OECORE_NATIVE_SYSROOT${prefix_nativesdk}/libexec/swift-llvm/bin"

create_sdk_files:append:swiftllvm() {
    if ${@bb.utils.contains('SWIFTSDK', '1', 'true', 'false', d)}; then
        script=${SDK_OUTPUT}/${SDKPATH}/environment-setup-${REAL_MULTIMACH_TARGET_SYS}
        echo 'export SWIFTC="swiftc"' >> $script
        echo 'export SWIFT_TARGET="${SWIFT_SDK_TARGET}"' >> $script
        echo 'export SWIFTFLAGS="-no-toolchain-stdlib-rpath -sdk $SDKTARGETSYSROOT -resource-dir $SDKTARGETSYSROOT${libdir}/swift -use-ld=lld -tools-directory ${SWIFT_SDK_CLANG} ${SWIFT_SDK_DYLD_PREFIX_FLAGS} ${@swift_xcc_flags(d)}"' >> $script
        echo 'export SWIFT_CLANGCC="${SWIFT_SDK_CLANG}/clang -target ${TARGET_SYS} ${SWIFT_SDK_CLANGCC_ARCH} --sysroot=$SDKTARGETSYSROOT"' >> $script
        echo 'export SWIFT_CLANGCXX="${SWIFT_SDK_CLANG}/clang++ -target ${TARGET_SYS} ${SWIFT_SDK_CLANGCC_ARCH} --sysroot=$SDKTARGETSYSROOT"' >> $script
        echo 'export SWIFT_CLANGCPP="${SWIFT_SDK_CLANG}/clang -E -target ${TARGET_SYS} ${SWIFT_SDK_CLANGCC_ARCH} --sysroot=$SDKTARGETSYSROOT"' >> $script
    fi
}
