# The swift.org host toolchain shipped in the SDK by nativesdk-swift is linked
# against the standard ncurses sonames (libtinfo.so.6, libncurses.so.6), but
# poky builds ncurses with the legacy ABI 5 soname (libtinfo.so.5, ...). Without
# a matching .so.6 in the SDK the dynamic loader falls back to the host's
# libtinfo.so.6, which pulls a newer glibc than the SDK ships and breaks swiftc
# with "libc.so.6: version GLIBC_2.xx not found". Add .so.6 compat symlinks for
# the SDK host libraries. ABI 5 and 6 export the same terminfo entry points the
# toolchain needs, so pointing .so.6 at the .so.5 built against the SDK glibc is
# enough. Only the nativesdk (SDK host) build needs this.
do_install:append:class-nativesdk() {
    for lib in libtinfo libncurses libncursesw; do
        if [ -e ${D}${base_libdir}/${lib}.so.5 ]; then
            ln -sf ${lib}.so.5 ${D}${base_libdir}/${lib}.so.6
        fi
    done
}
