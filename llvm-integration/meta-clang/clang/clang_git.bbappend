require llvm-integration/common/swift-llvm-source.inc

# The fork's lldb can embed Swift support, which needs the Swift compiler's
# build. Build a plain lldb (and lldb-server) here. The Swift-aware lldb is a
# separate recipe built on top of swift.
EXTRA_OECMAKE:append:swiftllvm = " -DLLDB_ENABLE_SWIFT_SUPPORT=OFF"
