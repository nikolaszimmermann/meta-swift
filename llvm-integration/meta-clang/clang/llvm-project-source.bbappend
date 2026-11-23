require llvm-integration/common/swift-llvm-source.inc

# The meta-clang (LLVM 20) OE patch series, rebased onto the swiftlang LLVM
# fork. Two patches are dropped: 0029 (MIPS O32 stat sizes, no Swift target)
# and 0036 (ppc64 musl build fix, already part of the fork). The order below
# is the order the series was rebased in. Patches from 0101 on belong to this
# layer.
FILESEXTRAPATHS:prepend:swiftllvm := "${THISDIR}/files-scarthgap:"

SRC_URI:swiftllvm = "\
    ${BASEURI} \
    file://0001-libcxxabi-Find-libunwind-headers-when-LIBCXXABI_LIBU.patch \
    file://0002-compiler-rt-support-a-new-embedded-linux-target.patch \
    file://0003-compiler-rt-Simplify-cross-compilation.-Don-t-use-na.patch \
    file://0004-llvm-TargetLibraryInfo-Undefine-libc-functions-if-th.patch \
    file://0006-clang-driver-Check-sysroot-for-ldso-path.patch \
    file://0007-clang-Driver-tools.cpp-Add-lssp_nonshared-on-musl.patch \
    file://0008-clang-Prepend-trailing-to-sysroot.patch \
    file://0010-clang-Define-releative-gcc-installation-dir.patch \
    file://0011-clang-Add-lpthread-and-ldl-along-with-lunwind-for-st.patch \
    file://0013-Check-for-atomic-double-intrinsics.patch \
    file://0015-clang-Fix-resource-dir-location-for-cross-toolchains.patch \
    file://0016-clang-driver-Add-dyld-prefix-when-checking-sysroot-f.patch \
    file://0017-clang-Use-python3-in-python-scripts.patch \
    file://0018-For-x86_64-set-Yocto-based-GCC-install-search-path.patch \
    file://0020-compiler-rt-Do-not-use-backtrace-APIs-on-non-glibc-l.patch \
    file://0021-clang-Fix-x86-triple-for-non-debian-multiarch-linux-.patch \
    file://0022-libunwind-Added-unw_backtrace-method.patch \
    file://0023-clang-Do-not-use-install-relative-libc-headers.patch \
    file://0025-Correct-library-search-path-for-OpenEmbedded-Host.patch \
    file://0026-lldb-Link-with-libatomic-on-x86.patch \
    file://0027-compiler-rt-Enable-__int128-for-ppc32.patch \
    file://0028-llvm-Do-not-use-cmake-infra-to-detect-libzstd.patch \
    file://0030-compiler-rt-Undef-_TIME_BITS-along-with-_FILE_OFFSET.patch \
    file://0031-ToolChains-Gnu.cpp-ARMLibDirs-search-also-in-lib32.patch \
    file://0032-clang-llvm-Add-OE-specific-ABI-triple-for-N32-ABI.patch \
    file://0033-llvm-Add-libunwind.pc.in-and-llvm-config-scripts.patch \
    file://0034-scan-build-py-respect-LLVM_LIBDIR_SUFFIX-like-other-.patch \
    file://0035-compiler-rt-Do-not-pass-target-to-clang-compiler.patch \
    file://0009-clang-Look-inside-the-target-sysroot-for-compiler-ru.patch \
    file://0012-Pass-PYTHON_EXECUTABLE-when-cross-compiling-for-nati.patch \
    file://0014-cmake-Fix-configure-for-packages-using-find_package.patch \
    file://0019-llvm-Insert-anchor-for-adding-OE-distro-vendor-names.patch \
    file://0024-Fix-lib-paths-for-OpenEmbedded-Host.patch \
    file://0005-llvm-allow-env-override-of-exe-and-libdir-path.patch \
    file://0100-lldb-Only-install-the-copied-clang-resource-director.patch \
    file://0101-clang-Prefer-sysroot-usr-for-GCC-in-usrmerge-sysroots.patch \
    file://0102-lldb-Fix-the-VirtualDataExtractor-build-for-32-bit-t.patch \
    file://0103-clang-Find-the-OpenEmbedded-GCC-for-32-bit-hard-floa.patch \
"
