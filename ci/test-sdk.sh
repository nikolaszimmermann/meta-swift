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
sh "$installer" -y -d "$sdk"

# The environment scripts reference unset variables, so no set -u.
for env in "$sdk"/environment-setup-*; do
    . "$env"
done

work=$(mktemp -d)
trap 'rm -rf "$work"' EXIT
# SWIFTFLAGS holds several flags, split on purpose.
# shellcheck disable=SC2086
"$SWIFTC" -target "$SWIFT_TARGET" $SWIFTFLAGS -O \
    -o "$work/hello" "$layer/ci/sdk-test/hello.swift"
$READELF -h "$work/hello" | grep -E 'Class|Machine'
echo "SDK test passed: $SWIFT_TARGET"
