# Describe the Swift target in the SDK environment, next to meta-clang's
# CLANGCC/CLANGCXX. Build systems pass these to swiftc (CMake for example as
# CMAKE_Swift_COMPILER_TARGET and CMAKE_Swift_FLAGS).
inherit swift-triple

SWIFTSDK ??= "1"

# meta-environment is built for the SDK host (HOST_SYS); the environment
# describes the target.
SWIFT_SDK_TARGET = "${@oe.utils.conditional('TARGET_ARCH', 'arm', d.getVar('TARGET_SYS').replace('arm-', 'armv7-', 1) + 'hf', d.getVar('TARGET_SYS'), d)}"
SWIFT_SDK_DYLD_PREFIX_FLAGS = "${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge', '-Xclang-linker --dyld-prefix=/usr', '', d)}"

create_sdk_files:append:swiftllvm() {
    if ${@bb.utils.contains('SWIFTSDK', '1', 'true', 'false', d)}; then
        script=${SDK_OUTPUT}/${SDKPATH}/environment-setup-${REAL_MULTIMACH_TARGET_SYS}
        echo 'export SWIFTC="swiftc"' >> $script
        echo 'export SWIFT_TARGET="${SWIFT_SDK_TARGET}"' >> $script
        echo 'export SWIFTFLAGS="-no-toolchain-stdlib-rpath -sdk $SDKTARGETSYSROOT -resource-dir $SDKTARGETSYSROOT${libdir}/swift -use-ld=lld ${SWIFT_SDK_DYLD_PREFIX_FLAGS} ${@swift_xcc_flags(d)}"' >> $script
    fi
}
