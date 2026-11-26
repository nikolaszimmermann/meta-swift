#!/bin/bash
# Install the SDK that the CI build produced and compile a Swift program with
# it, to check that the SDK works and not only that it builds. Runs in the kas
# container, where the build directory is /build and this layer is /repo:
#
#   kas-container shell ci/kas/<config>.yml -c /repo/ci/test-sdk.sh

set -e

layer=$(cd "$(dirname "$0")/.." && pwd)
build=${BUILDDIR:-/build}

# The build directory persists across CI runs: take the newest installer.
installer=$(ls -t "$build"/tmp/deploy/sdk/*-toolchain-*.sh | head -n 1)
sdk=$build/sdk-test
rm -rf "$sdk"
# The installed SDK takes several GB and is only needed here.
trap 'rm -rf "$sdk"' EXIT
sh "$installer" -y -d "$sdk"

# The environment scripts reference unset variables, so no set -u.
for env in "$sdk"/environment-setup-*; do
    . "$env"
done

# The programs stay in the build directory, outside the SDK: the swift_sdk
# runtime test runs hello in QEMU (SWIFT_SDK_TEST_PROGRAM in
# ci/kas/scarthgap.yml).
programs=$build/sdk-test-programs
rm -rf "$programs"
mkdir -p "$programs"
program=$programs/hello
# SWIFTFLAGS holds several flags, split on purpose.
# shellcheck disable=SC2086
"$SWIFTC" -target "$SWIFT_TARGET" $SWIFTFLAGS -O \
    -o "$program" "$layer/ci/sdk-test/hello.swift"
$READELF -h "$program" | grep -E 'Class|Machine'

# The C/C++ compiler for code that Swift imports: the private clang of the SDK.
# SWIFT_CLANGCXX holds several words, split on purpose.
# shellcheck disable=SC2086
$SWIFT_CLANGCXX -O2 -o "$programs/hello-cxx" "$layer/ci/sdk-test/hello.cpp"
$READELF -h "$programs/hello-cxx" | grep -E 'Machine'
$READELF -p .comment "$programs/hello-cxx" | grep -q 'clang version' ||
    { echo "hello-cxx was not compiled by clang" >&2; exit 1; }
echo "SDK test passed: $SWIFT_TARGET"
