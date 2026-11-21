# Configure and build with meta-swift's CMake 3.31 (cmake-swift-native) instead
# of the release's cmake-native. Swift CMake projects need the CMP0157 build
# model; see cmake-swift-native for why.
DEPENDS:append = " cmake-swift-native"
PATH:prepend = "${STAGING_DIR_NATIVE}${libexecdir_native}/cmake-swift/bin:"

# Use that build model also in projects that do not opt in themselves.
EXTRA_OECMAKE:append = " -DCMAKE_POLICY_DEFAULT_CMP0157=NEW"
