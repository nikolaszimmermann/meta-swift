# Opt-in for the Swift toolchain.
#
# The Swift compiler embeds clang, and the C/C++ code it imports or links
# against must be compiled by that very clang. This layer builds it from the
# swiftlang LLVM fork at the tag that matches SWIFT_VERSION, as a private
# toolchain under its own prefix (the swift-llvm recipe). Only the recipes
# that inherit the Swift classes and the Swift recipes themselves use it, with
# TOOLCHAIN = "swiftclang". Every other recipe keeps the compiler and the LLVM
# of the distro, unchanged.
#
# Only including the layer changes nothing. The Swift recipes are skipped until
# this file is included, e.g. from local.conf or a distro configuration:
#
#   require conf/distro/include/swift-toolchain.inc

SWIFT_UNIFIED_LLVM = "1"
OVERRIDES .= ":swiftllvm"

# Put Swift into the SDKs of the images: the compiler and the runtime with its
# -dev packages. Set it to "0" to leave the SDKs as they are.
SWIFTSDK ??= "1"

require conf/distro/include/swift-clang-toolchain.inc
