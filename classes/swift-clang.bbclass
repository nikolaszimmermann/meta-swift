# Compile the C and C++ code of a recipe with the private clang of the
# swiftlang fork (swift-llvm), the clang that swiftc embeds, in every class.
#
# For the target and the SDK host through the swiftclang toolchain
# (conf/distro/include/swift-clang-toolchain.inc). The class overrides win over
# a TOOLCHAIN:class-* = "gcc", as meta-clang sets it. Native recipes compile
# with BUILD_CC, which native.bbclass exports as CC.

TOOLCHAIN:class-target = "swiftclang"
TOOLCHAIN:class-nativesdk = "swiftclang"

# The swiftclang toolchain knows the loader of the x86_64 and aarch64 SDK hosts
# only (LDFLAGS in conf/distro/include/swift-clang-toolchain.inc).
python () {
    if bb.data.inherits_class('nativesdk', d) and d.getVar('SDK_ARCH') not in ('x86_64', 'aarch64'):
        raise bb.parse.SkipRecipe('Swift supports the SDK hosts x86_64 and aarch64, not %s'
                                  % d.getVar('SDK_ARCH'))
}

BUILD_CC:class-native = "${CCACHE}${SWIFT_LLVM_BINDIR}/clang ${BUILD_CC_ARCH}"
BUILD_CXX:class-native = "${CCACHE}${SWIFT_LLVM_BINDIR}/clang++ ${BUILD_CC_ARCH}"
BUILD_CPP:class-native = "${SWIFT_LLVM_BINDIR}/clang ${BUILD_CC_ARCH} -E"
BUILD_CCLD:class-native = "${SWIFT_LLVM_BINDIR}/clang ${BUILD_CC_ARCH}"

DEPENDS:append = " swift-llvm-native"

# swiftc links through clang, which it looks up next to itself and in PATH,
# unless -tools-directory says otherwise. Builds that run swiftc directly, such
# as the bootstrap stages and their CMake checks, find the private clang and
# lld here.
PATH:prepend = "${SWIFT_LLVM_BINDIR}:"

# The private clang of an SDK host recipe gets --sysroot=${STAGING_DIR_HOST},
# but the SDK host libraries, headers, crt files and libgcc are staged below
# ${SDKPATHNATIVE} in there. Like nativesdk-clang-glue of meta-clang, link
# usr/lib, usr/include and lib of the sysroot to them. The links are made in
# the recipe sysroot instead of staged by a recipe, so that they do not
# collide with those of nativesdk-clang-glue, which are the same.
python swift_clang_nativesdk_sysroot_links() {
    sysroot = d.getVar('STAGING_DIR_HOST')
    # The link texts of nativesdk-clang-glue, exactly.
    links = [
        (d.getVar('prefix_nativesdk'), '..' + d.getVar('libdir'), d.getVar('libdir')),
        (d.getVar('prefix_nativesdk'), '..' + d.getVar('includedir'), d.getVar('includedir')),
        ('/', '.' + d.getVar('base_libdir'), d.getVar('base_libdir')),
    ]
    for parent, target, real in links:
        linkdir = os.path.join(sysroot, parent.lstrip('/'))
        link = os.path.join(linkdir, os.path.basename(real))
        bb.utils.mkdirhier(linkdir)
        if os.path.islink(link) and os.readlink(link) == target:
            continue
        if os.path.lexists(link):
            bb.fatal('%s exists and is not a link to %s' % (link, target))
        os.symlink(target, link)
}
do_prepare_recipe_sysroot[postfuncs] += "${@'swift_clang_nativesdk_sysroot_links' if bb.data.inherits_class('nativesdk', d) else ''}"
