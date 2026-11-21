require llvm-integration/common/swift-llvm-source.inc

# The fork's lldb can embed Swift support, which needs the Swift compiler's
# build. Build a plain lldb (and lldb-server) here. The Swift-aware lldb is a
# separate recipe built on top of swift.
EXTRA_OECMAKE:append:swiftllvm = " -DLLDB_ENABLE_SWIFT_SUPPORT=OFF"

# Swift is built against this LLVM and relies on its private, generated
# llvm/Config/config.h for host feature checks. Install it for the classes
# Swift is built for (the build host and the SDK host).
swift_install_llvm_config_h() {
    install -Dm 0644 ${B}/include/llvm/Config/config.h ${D}${includedir}/llvm/Config/config.h
}
do_install:append:class-native:swiftllvm() {
    swift_install_llvm_config_h
}
do_install:append:class-nativesdk:swiftllvm() {
    swift_install_llvm_config_h
}

# The fork's IndexStore headers are installed below ${prefix}/local/include;
# put them with the other headers. Only for the packaged classes (the native
# sysroot does not care, and changing clang-native would rebuild all of Swift).
swift_move_indexstore_headers() {
    if [ -d ${D}${prefix}/local/include/indexstore ]; then
        install -d ${D}${includedir}
        mv ${D}${prefix}/local/include/indexstore ${D}${includedir}/
        rmdir --ignore-fail-on-non-empty ${D}${prefix}/local/include ${D}${prefix}/local
    fi
}
do_install:append:class-nativesdk:swiftllvm() {
    swift_move_indexstore_headers
}
do_install:append:class-target:swiftllvm() {
    swift_move_indexstore_headers
}
