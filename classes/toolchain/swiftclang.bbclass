# The swiftclang toolchain for releases that select the toolchain with a class
# (base.bbclass: inherit_defer toolchain/${TOOLCHAIN}), from whinlatter on.
# The settings themselves are in conf/distro/include/swift-clang-toolchain.inc
# and apply through the toolchain-swiftclang override, which also works on
# releases without these classes.
#
# The binutils and the default cross compiler stay those of gcc, see
# swift-clang-toolchain.inc.
# nooelint: oelint.var.improperinherit - a class of the toolchain/ directory
inherit toolchain/gcc

# clang does not know -fcanon-prefix-map, which toolchain/gcc adds.
DEBUG_PREFIX_MAP_EXTRA = ""

TCOVERRIDE = "toolchain-swiftclang"
