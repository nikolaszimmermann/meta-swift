SUMMARY = "LLVM, clang and lld of the swiftlang fork, the private toolchain of Swift"
DESCRIPTION = "The swiftlang LLVM fork at the tag of SWIFT_VERSION, with the OE \
patch series of meta-clang rebased onto it, built into a private prefix. swiftc \
embeds this LLVM, and the C/C++ code it imports or links against is compiled by \
this clang (TOOLCHAIN = \"swiftclang\"). It does not replace the clang or LLVM \
of the distro."
HOMEPAGE = "https://github.com/swiftlang/llvm-project"
LICENSE = "Apache-2.0-with-LLVM-exception"
LIC_FILES_CHKSUM = "file://llvm/LICENSE.TXT;md5=8a15a0759ef07f2682d2ba4b893c9afe"

DEPENDS += "ninja-native zlib"
# The SDK host LLVM runs the build host tools of swift-llvm-native.
DEPENDS:append:class-nativesdk = " swift-llvm-native"

PV = "${SWIFT_LLVM_MAJOR_VER}.${SWIFT_LLVM_MINOR_VER}.${SWIFT_LLVM_PATCH_VER}"

FILESEXTRAPATHS:prepend := "${THISDIR}/swift-llvm:"

# The OE patch series of meta-clang (LLVM 20), rebased onto the fork. 0029
# (MIPS O32 stat sizes, no Swift target) and 0036 (ppc64 musl build fix,
# already part of the fork) are dropped. The order is the one the series was
# rebased in. The patches from 0100 on belong to this layer. Patches for
# subprojects that are not built here (compiler-rt, libcxx, lldb) do nothing.
SRC_URI = "\
    git://github.com/swiftlang/llvm-project.git;protocol=https;nobranch=1;destsuffix=llvm-project-${SWIFT_TAG} \
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
SRCREV = "${SWIFT_LLVM_SRCREV}"

S = "${@d.getVar('UNPACKDIR') or d.getVar('WORKDIR')}/llvm-project-${SWIFT_TAG}"
OECMAKE_SOURCEPATH = "${S}/llvm"

inherit cmake python3native

SWIFT_LLVM_PREFIX_INSTALL = "${libexecdir}/swift-llvm"

# The architectures of the build host, the SDK hosts and the targets that the
# layer supports.
SWIFT_LLVM_TARGETS ?= "AArch64;ARM;X86"

EXTRA_OECMAKE = "\
    -DCMAKE_BUILD_TYPE=Release \
    -DCMAKE_INSTALL_PREFIX=${SWIFT_LLVM_PREFIX_INSTALL} \
    -DLLVM_ENABLE_PROJECTS='clang;lld' \
    -DLLVM_TARGETS_TO_BUILD='${SWIFT_LLVM_TARGETS}' \
    -DLLVM_ENABLE_ASSERTIONS=OFF \
    -DLLVM_INCLUDE_TESTS=OFF \
    -DLLVM_INCLUDE_EXAMPLES=OFF \
    -DLLVM_INCLUDE_BENCHMARKS=OFF \
    -DLLVM_INCLUDE_DOCS=OFF \
    -DCLANG_INCLUDE_TESTS=OFF \
    -DLLVM_ENABLE_ZSTD=OFF \
    -DLLVM_ENABLE_LIBXML2=OFF \
    -DLLVM_ENABLE_LIBEDIT=OFF \
    -DLLVM_ENABLE_TERMINFO=OFF \
    -DLLVM_ENABLE_BINDINGS=OFF \
"

SWIFT_LLVM_NATIVE_BINDIR = "${STAGING_DIR_NATIVE}${libexecdir_native}/swift-llvm/bin"
EXTRA_OECMAKE:append:class-nativesdk = "\
    -DLLVM_HOST_TRIPLE=${HOST_SYS} \
    -DLLVM_NATIVE_TOOL_DIR=${SWIFT_LLVM_NATIVE_BINDIR} \
    -DLLVM_TABLEGEN=${SWIFT_LLVM_NATIVE_BINDIR}/llvm-tblgen \
    -DCLANG_TABLEGEN=${SWIFT_LLVM_NATIVE_BINDIR}/clang-tblgen \
"

# The OE vendor and distro names, for the driver patches (0019 adds the
# anchors). Like meta-clang, but a native recipe only sees its own
# TARGET_VENDOR, so take the target vendor from SDK_VENDOR: the vendor of the
# distro is the one of its SDK without the "sdk" suffix (-poky, -pokysdk).
SWIFT_LLVM_OE_VENDORS ?= "${@(d.getVar('SDK_VENDOR') or '').removesuffix('sdk')} ${SDK_VENDOR}"
SWIFT_LLVM_OE_DISTRO ?= "poky:poky"
SWIFT_LLVM_MULTILIB_VARIANTS = "lib32 lib64 libx32"

python do_preconfigure() {
    import subprocess

    def sed(anchor, text, path):
        subprocess.check_output(['sed', '-i', 's#%s#%s#g' % (anchor, text), d.expand(path)],
                                stderr=subprocess.STDOUT)

    vendors = []
    for vendor in d.getVar('SWIFT_LLVM_OE_VENDORS').split():
        vendor = vendor.lstrip('-')
        vendors += [vendor + 'ml' + v for v in d.getVar('SWIFT_LLVM_MULTILIB_VARIANTS').split()]
        if vendor != 'oe':
            vendors.append(vendor)
    case = ''.join('\\n    .Case("%s", Triple::OpenEmbedded)' % v for v in vendors)
    triple = ''.join(' "x86_64-%s-linux",' % v for v in vendors)
    sed('//CLANG_EXTRA_OE_VENDORS_TRIPLES', triple, '${S}/clang/lib/Driver/ToolChains/Gnu.cpp')
    sed('//CLANG_EXTRA_OE_VENDORS_CASES', case, '${S}/llvm/lib/TargetParser/Triple.cpp')

    case = triple = name = check = ''
    oe_names = []
    for distro in d.getVar('SWIFT_LLVM_OE_DISTRO').split():
        distro_id, distro_triple = distro.split(':')
        distro_id = distro_id.replace('-', '_')
        upper = distro_id.upper()
        case += '\\n    .Case("%s", Distro::%s)' % (distro_id, upper)
        triple += '\\n   if (Distro.Is%s())\\n     return "x86_64-%s-linux",' % (upper, distro_triple)
        name += '\\n    %s,' % upper
        check += '\\nbool Is%s() const { return DistroVal == %s; }' % (upper, upper)
        oe_names.append(upper)
    check += '\\nbool IsOpenEmbedded() const { return DistroVal == %s; }' % ' || '.join(oe_names)
    sed('//CLANG_EXTRA_OE_DISTRO_NAME', name, '${S}/clang/include/clang/Driver/Distro.h')
    sed('//CLANG_EXTRA_OE_DISTRO_CHECK', check, '${S}/clang/include/clang/Driver/Distro.h')
    sed('//CLANG_EXTRA_OE_DISTRO_TRIPLES', triple, '${S}/clang/lib/Driver/ToolChains/Linux.cpp')
    sed('//CLANG_EXTRA_OE_DISTRO_CASES', case, '${S}/clang/lib/Driver/Distro.cpp')
}
addtask preconfigure after do_patch before do_configure

do_install:append() {
    prefix=${D}${SWIFT_LLVM_PREFIX_INSTALL}
    # Swift relies on the private, generated config.h of LLVM.
    install -Dm 0644 ${B}/include/llvm/Config/config.h $prefix/include/llvm/Config/config.h
    # The build host tools that swift-llvm for the SDK host and Swift run.
    for t in llvm-tblgen llvm-min-tblgen clang-tblgen; do
        [ -e $prefix/bin/$t ] || install -m 0755 ${B}/bin/$t $prefix/bin/
    done
    # The fork installs the IndexStore headers below local/include.
    if [ -d $prefix/local/include/indexstore ]; then
        mv $prefix/local/include/indexstore $prefix/include/
        rm -rf $prefix/local
    fi
}

# Only the SDK host variant is packaged: the clang, lld and resource headers
# for the SDK, the libraries and headers for building against it.
FILES:${PN} = "\
    ${SWIFT_LLVM_PREFIX_INSTALL}/bin \
    ${SWIFT_LLVM_PREFIX_INSTALL}/lib/clang \
    ${SWIFT_LLVM_PREFIX_INSTALL}/share \
"
FILES:${PN}-dev = "\
    ${SWIFT_LLVM_PREFIX_INSTALL}/include \
    ${SWIFT_LLVM_PREFIX_INSTALL}/lib/cmake \
    ${SWIFT_LLVM_PREFIX_INSTALL}/libexec \
"
FILES:${PN}-staticdev = "${SWIFT_LLVM_PREFIX_INSTALL}/lib/*.a"

BBCLASSEXTEND = "native nativesdk"

python () {
    if d.getVar('SWIFT_UNIFIED_LLVM') != '1':
        raise bb.parse.SkipRecipe('requires conf/distro/include/swift-toolchain.inc')
    if not (bb.data.inherits_class('native', d) or bb.data.inherits_class('nativesdk', d)):
        raise bb.parse.SkipRecipe('swift-llvm is only built for the build host and the SDK host')
}
