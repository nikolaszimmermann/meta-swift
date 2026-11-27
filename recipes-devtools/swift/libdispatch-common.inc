# The build settings that libdispatch and swift-dispatch share: both build
# swift-corelibs-libdispatch, without and with its Swift overlay.

# musl declares program_invocation_short_name in errno.h only with
# _GNU_SOURCE, which the CMake check of libdispatch leaves out, although it is
# what the check turns on. Without it, getprogname() has no implementation.
EXTRA_OECMAKE:append:libc-musl = " -DHAVE_DECL_PROGRAM_INVOCATION_SHORT_NAME=1"
# On musl, sys/cdefs.h comes from bsd-headers, which warns that it is
# non-standard, and libdispatch builds with -Werror. It needs the BSD macros
# of the header, so only drop the warning.
CFLAGS:append:libc-musl = " -Wno-#warnings"
CXXFLAGS:append:libc-musl = " -Wno-#warnings"
