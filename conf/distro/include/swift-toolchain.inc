# Opt-in for the Swift toolchain.
#
# Including this file makes the swiftlang LLVM fork the one LLVM/clang of the
# build: the meta-clang (or OE-core) clang recipes keep doing all the work but
# build from swiftlang/llvm-project at the tag that matches SWIFT_VERSION, with
# the OE patch series rebased onto it. swiftc embeds that very clang, so the C,
# C++ and Swift compilers, lld and the clang resource headers all agree.
#
# Only including the layer changes nothing. The Swift recipes are skipped until
# this file is included, e.g. from local.conf or a distro configuration:
#
#   require conf/distro/include/swift-toolchain.inc

SWIFT_UNIFIED_LLVM = "1"
OVERRIDES .= ":swiftllvm"

# Ship the (now swiftlang) clang in the SDK, next to swiftc.
CLANGSDK = "1"

# Make the SDK target sysroot work with Swift's C++ interoperability.
INHERIT += "swift-sdk"
