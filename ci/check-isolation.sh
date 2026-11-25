#!/bin/bash
# Check that conf/distro/include/swift-toolchain.inc leaves every recipe that
# does not use Swift alone: the task hashes of the recipes in
# SWIFT_ISOLATION_TARGETS (set by the kas configuration of the release) must be
# the same with and without the include. Runs in the kas container, where the
# build directory is /build:
#
#   kas-container shell ci/kas/<config>.yml -c /repo/ci/check-isolation.sh

set -euo pipefail

build=${BUILDDIR:-/build}
targets=${SWIFT_ISOLATION_TARGETS:?set SWIFT_ISOLATION_TARGETS}
conf=$build/conf/local.conf
include='require conf/distro/include/swift-toolchain.inc'

grep -qxF "$include" "$conf" || { echo "$conf does not contain: $include" >&2; exit 1; }

work=$(mktemp -d)
cp "$conf" "$work/local.conf"
trap 'cp "$work/local.conf" "$conf"; rm -rf "$work"' EXIT

# bitbake -S lockedsigs writes the task hashes of all tasks that the targets
# depend on to locked-sigs.inc in the current directory.
sigs() {
    (cd "$work" && rm -f locked-sigs.inc && bitbake -S lockedsigs $targets >/dev/null &&
        grep -E '^ +[^ ]+:do_[a-z_]+:[0-9a-f]{64}' locked-sigs.inc | sort) > "$work/$1"
}

sigs with
grep -vxF "$include" "$work/local.conf" > "$conf"
sigs without

if ! diff -u "$work/without" "$work/with"; then
    echo "swift-toolchain.inc changes the task hashes above" >&2
    exit 1
fi
echo "Isolation check passed: $(wc -l < "$work/with") task hashes unchanged for $targets"
