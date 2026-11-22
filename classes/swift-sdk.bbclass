# SDK adjustments for Swift, inherited globally by swift-toolchain.inc.
#
# In a usrmerge sysroot, lib/ is a symlink to usr/lib. clang searches the
# sysroot before sysroot/usr for the GCC installation, so it reaches libstdc++
# through lib/<triple>/<version>/../../../include/c++, which lexically
# normalizes to <sysroot>/include/c++. Swift's C++ interoperability injects the
# libstdc++ module map at that normalized path while the headers are read
# through lib/, so the std module and textual includes see different files
# (redefinition errors). Give the SDK target sysroot the include/ that the
# normalized path expects, like the other usrmerge links.
swift_sdk_usrmerge_include_link() {
    sysroot="${SDK_OUTPUT}${SDKTARGETSYSROOT}"
    if [ -d "$sysroot/usr/include" ] && [ ! -e "$sysroot/include" ]; then
        ln -s usr/include "$sysroot/include"
    fi
}

POPULATE_SDK_POST_TARGET_COMMAND:append = "${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge', ' swift_sdk_usrmerge_include_link', '', d)}"
