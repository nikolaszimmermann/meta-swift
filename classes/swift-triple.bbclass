# The Swift view of the target: triple, architecture and the tune flags that
# swiftc has to hand to its embedded clang. Shared by the recipe classes and
# the SDK environment.

# The Swift triple of the machine the code runs on (HOST_SYS: the target for
# target recipes, the SDK host for nativesdk ones). Yocto's triple is used as is,
# except on 32-bit arm where the bare
# "arm" arch is not a Swift platform and the hard-float ABI has to be in the
# triple (Yocto conveys it through -mfloat-abi=hard instead).
SWIFT_TARGET_ARCH = "${@oe.utils.conditional('HOST_ARCH', 'arm', 'armv7', d.getVar('HOST_ARCH'), d)}"
SWIFT_TARGET = "${@oe.utils.conditional('HOST_ARCH', 'arm', d.getVar('HOST_SYS').replace('arm-', 'armv7-', 1) + 'hf', d.getVar('HOST_SYS'), d)}"
# swift-native builds its standard library for the generic triple.
SWIFT_TARGET:class-native = "${BUILD_ARCH}-unknown-linux-gnu"

# The directory of the Swift runtime and its modules, lib/swift/<platform>,
# named by the compiler after the triple: musl for a musl triple (other than
# the swift vendor of the static Linux SDK), linux otherwise. Only the target
# can be on musl, the build and SDK hosts are on glibc.
SWIFT_PLATFORM = "linux"
SWIFT_PLATFORM:libc-musl = "musl"

# The module map of the C library of a musl target (musl-distribution.modulemap,
# see swift-stdlib) puts it into a single module, like the Glibc one. Both need
# the Clang builtin headers in the system modules, which the compiler only
# passes to the importer by itself for glibc.
SWIFT_MUSL_XCC_FLAGS = "-Xcc -Xclang -Xcc -fbuiltin-headers-in-system-modules"

# Only the tune (-mcpu, -mfloat-abi, ...) flags: they change the ABI and the
# available intrinsics, so the importer must see them. Optimization and
# hardening flags are for the C/C++ compilers.
# TUNE_CCARGS describes the target machine. The SDK host uses SDK_CC_ARCH.
SWIFT_TUNE_CCARGS = "${TUNE_CCARGS}"
SWIFT_TUNE_CCARGS:class-nativesdk = "${SDK_CC_ARCH}"
SWIFT_TUNE_CCARGS:class-native = "${BUILD_CC_ARCH}"

def swift_xcc_args(d):
    import re, shlex
    tune = shlex.split(d.getVar('SWIFT_TUNE_CCARGS') or '')
    args = []
    for f in tune:
        args += ['-Xcc', f]
    # With __SSE3__ defined, the bits/opt_random.h of libstdc++ includes
    # <pmmintrin.h>. Its clang module, _Builtin_intrinsics, includes
    # <stdlib.h> from mm_malloc.h, which resolves to the C++ <stdlib.h> and
    # with it to the std module that Swift builds for libstdc++:
    #   error: cyclic dependency in module 'std': std -> _Builtin_intrinsics -> std
    # Upstream Swift 6.4.2 fails the same way with -Xcc -msse3. The OE x86 tunes
    # enable SSE3 (core2 and newer), so hide the macro from the importer. The
    # code generation keeps SSE3, unlike with -mno-sse3, which would also turn
    # off SSSE3 and SSE4. TARGET_ARCH is the machine the tune describes, also in
    # meta-environment, which runs on the SDK host but describes the target.
    if tune and re.match(r'(x86_64|i[3-6]86)$', d.getVar('TARGET_ARCH') or ''):
        args += ['-Xcc', '-U__SSE3__']
    return args

def swift_xcc_flags(d):
    return ' '.join(swift_xcc_args(d))

def swift_xcc_flags_cmake_list(d):
    return ';'.join(swift_xcc_args(d))

