#!/bin/bash
# Clean the work directories that rm_work left in the build directory, which
# persists across CI runs. rm_work only cleans the recipes that the targets
# reach through do_build. The SDK host recipes, which only do_populate_sdk
# pulls in, and the QEMU of testimage keep tens of GB. Runs in the kas
# container, where the build directory is /build:
#
#   kas-container shell ci/kas/<config>.yml -c /repo/ci/trim-build-dir.sh
#
# bitbake -c clean has no dependencies, unlike bitbake --runall=rm_work, which
# runs every task that do_build would, so it never builds anything. The next
# run restores the cleaned recipes from sstate.

set -euo pipefail

build=${BUILDDIR:-/build}

# What rm_work leaves (the logs, and the outputs of tasks that ran from sstate
# afterwards) is small. Only clean the work directories that are not.
min_mb=100

# The preferred version of every recipe, as <recipe>/<version> like the work
# directories, which leave out the epoch. bitbake -s prints
# "<recipe> [<epoch>]:<version>-<revision>" for the latest version and, if it
# differs, the preferred version after it.
declare -A current
while read -r entry; do
    current[$entry]=1
done < <(bitbake -s 2>/dev/null | awk '
    NF >= 2 && $2 ~ /:/ {
        v = (NF >= 3 && $3 ~ /:/) ? $3 : $2
        sub(/^[^:]*:/, "", v)
        sub(/-[^-]*$/, "", v)
        print $1 "/" v
    }')
if [ "${#current[@]}" -eq 0 ]; then
    echo "bitbake -s listed no recipes" >&2
    exit 1
fi

# The recipes of RM_WORK_EXCLUDE, the image, stay: cleaning the image also
# removes its deployed output, so every run would repeat do_rootfs and
# do_populate_sdk. Only drop its rootfs and sdk directories, like rm_work
# does for the other images. Its deployed image and stamps stay.
excluded=$(bitbake-getvar --value RM_WORK_EXCLUDE)
declare -A keep
for recipe in $excluded; do
    keep[$recipe]=1
done

recipes=()
stale=()
cd "$build/tmp/work"
# tmp/work/<package arch>/<recipe>/<version>
for dir in */*/*; do
    if [ "$(du -sm "$dir" | cut -f1)" -lt "$min_mb" ]; then
        continue
    fi
    recipe=$(echo "$dir" | cut -d/ -f2)
    if [ -z "${current[${dir#*/}]:-}" ]; then
        # A recipe or version that is gone: bitbake cannot clean it.
        stale+=("$dir")
    elif [ -n "${keep[$recipe]:-}" ]; then
        echo "Removing the rootfs and sdk directories of $dir"
        rm -rf "$dir/rootfs" "$dir/sdk"
    else
        recipes+=("$recipe")
    fi
done

if [ "${#stale[@]}" -gt 0 ]; then
    echo "Removing stale work directories: ${stale[*]}"
    rm -rf "${stale[@]}"
fi
if [ "${#recipes[@]}" -gt 0 ]; then
    mapfile -t recipes < <(printf '%s\n' "${recipes[@]}" | sort -u)
    echo "Cleaning: ${recipes[*]}"
    bitbake -c clean "${recipes[@]}"
fi
