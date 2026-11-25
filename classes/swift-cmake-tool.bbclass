# Configure and build with meta-swift's CMake 3.31 (cmake-swift-native) instead
# of the release's cmake-native. Swift CMake projects need the CMP0157 build
# model; see cmake-swift-native for why.
DEPENDS:append = " cmake-swift-native"
PATH:prepend = "${STAGING_DIR_NATIVE}${libexecdir_native}/cmake-swift/bin:"

# Use that build model also in projects that do not opt in themselves.
EXTRA_OECMAKE:append = " -DCMAKE_POLICY_DEFAULT_CMP0157=NEW"

# With that build model CMake hands the linker flags (LDFLAGS) to swiftc when
# Swift links a target. swiftc understands neither the clang-only -Wl,a,b
# spelling nor the compile-time -f*-prefix-map flags that OE also puts into
# LDFLAGS. Rewrite -Wl,a,b as -Xlinker a -Xlinker b, which clang and swiftc
# both accept, and drop the prefix maps, which do nothing at link time. Also
# drop -pthread (part of glibc) and an explicit -rtlib=/--unwindlib=, which
# distro settings for toolchain-clang may add and which only restate the
# defaults for these classes.
def swift_linker_neutral_ldflags(flags):
    import shlex
    out = []
    for f in shlex.split(flags or ''):
        if f.startswith('-Wl,'):
            for a in f[4:].split(','):
                out += ['-Xlinker', a]
        elif f.startswith(('-fdebug-prefix-map=', '-fmacro-prefix-map=', '-ffile-prefix-map=', '-fcanon-prefix-map', '-rtlib=', '--unwindlib=')) or f == '-pthread':
            continue
        else:
            out.append(f)
    return ' '.join(out)

# Rewrite lazily. Storing the expanded value at parse time would freeze values
# set by event handlers (such as the uninative dynamic linker) into LDFLAGS,
# and make the task signatures differ between the cooker and the workers.
python () {
    d.setVar('SWIFT_ORIG_LDFLAGS', d.getVar('LDFLAGS', False))
    d.setVar('LDFLAGS', '${@swift_linker_neutral_ldflags(d.getVar("SWIFT_ORIG_LDFLAGS"))}')
}
