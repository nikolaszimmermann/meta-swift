# The Swift view of the target: triple, architecture and the tune flags that
# swiftc has to hand to its embedded clang. Shared by the recipe classes and
# the SDK environment.

# The Swift triple: Yocto's TARGET_SYS, except on 32-bit arm where the bare
# "arm" arch is not a Swift platform and the hard-float ABI has to be in the
# triple (Yocto conveys it through -mfloat-abi=hard instead).
SWIFT_TARGET_ARCH = "${@oe.utils.conditional('TARGET_ARCH', 'arm', 'armv7', d.getVar('TARGET_ARCH'), d)}"
SWIFT_TARGET = "${@oe.utils.conditional('TARGET_ARCH', 'arm', d.getVar('TARGET_SYS').replace('arm-', 'armv7-', 1) + 'hf', d.getVar('TARGET_SYS'), d)}"

# Only the tune (-mcpu, -mfloat-abi, ...) flags: they change the ABI and the
# available intrinsics, so the importer must see them. Optimization and
# hardening flags are for the C/C++ compilers.
def swift_xcc_args(d):
    import shlex
    args = []
    for f in shlex.split(d.getVar('TUNE_CCARGS') or ''):
        args += ['-Xcc', f]
    return args

def swift_xcc_flags(d):
    return ' '.join(swift_xcc_args(d))

def swift_xcc_flags_cmake_list(d):
    return ';'.join(swift_xcc_args(d))

