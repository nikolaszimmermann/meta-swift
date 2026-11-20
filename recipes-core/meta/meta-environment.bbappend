# Describe the Swift target in the SDK environment, next to meta-clang's
# CLANGCC/CLANGCXX. Build systems pass these to swiftc (CMake for example as
# CMAKE_Swift_COMPILER_TARGET and CMAKE_Swift_FLAGS).
inherit swift-triple

SWIFTSDK ??= "1"

create_sdk_files:append:swiftllvm() {
    if ${@bb.utils.contains('SWIFTSDK', '1', 'true', 'false', d)}; then
        script=${SDK_OUTPUT}/${SDKPATH}/environment-setup-${REAL_MULTIMACH_TARGET_SYS}
        echo 'export SWIFTC="swiftc"' >> $script
        echo 'export SWIFT_TARGET="${SWIFT_TARGET}"' >> $script
        echo 'export SWIFTFLAGS="-sdk $SDKTARGETSYSROOT -resource-dir $SDKTARGETSYSROOT${libdir}/swift -use-ld=lld ${@swift_xcc_flags(d)}"' >> $script
    fi
}
