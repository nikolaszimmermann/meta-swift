# meta-swift

Yocto/OpenEmbedded layer that builds the [Swift](https://www.swift.org)
toolchain **from source**, on top of the clang/LLVM recipes of meta-clang
(scarthgap, walnascar) or OE-core (whinlatter and later).

No prebuilt Swift binaries are downloaded at any point. The compiler
bootstraps itself in three stages, like upstream's `bootstrap_stage{0,1,2}`
presets: a C++-only stage 0, a stage 1 with swift-syntax/macros built by
stage 0, and the final compiler built by stage 1.

## One LLVM

Swift embeds clang (the ClangImporter) and only builds against Apple's LLVM
fork, [swiftlang/llvm-project](https://github.com/swiftlang/llvm-project).
This layer therefore makes that fork the clang/LLVM of the whole build: the
meta-clang/OE-core recipes keep doing all the work, but fetch the fork at the
tag matching `SWIFT_VERSION`, with the OE patch series rebased onto it. C, C++
and Swift are then compiled by the very same clang, linked by the same lld,
and agree on the clang resource headers.

This is opt-in. Adding the layer alone changes nothing; enable it with

    require conf/distro/include/swift-toolchain.inc

in `local.conf` or a distro configuration. Without it, all Swift recipes are
skipped.

Note that the fork follows LLVM with some delay. Swift 6.4.2 is LLVM 21.1,
which is newer than scarthgap's meta-clang (20.1) and matches whinlatter, but
older than wrynose (22.1) and master. Enabling the layer there downgrades the
LLVM used by every clang-built recipe.

## Supported

| Release            | LLVM recipes from       | Status           |
| ------------------ | ----------------------- | ---------------- |
| scarthgap          | meta-clang (any branch) | built and tested |
| walnascar          | meta-clang              | parse only       |
| whinlatter, wrynose| OE-core                 | in progress      |

Targets: aarch64, armv7 (hard-float). Build/SDK hosts: x86_64, aarch64.

## Using Swift in recipes

    inherit swift-cmake

sets up `CMAKE_Swift_COMPILER`, the target triple and the sysroot in the
generated CMake toolchain file, and depends on the Swift runtime.

## CI

`.github/workflows/build.yml` builds `swift-cxx-interop-test` for the kas
configurations in `ci/kas/` (scarthgap; qemuarm64 and qemuarm, i.e. aarch64
and ARMv7 hard-float) on pushes to `main` and on pull requests against it.
The build is far too large for GitHub-hosted runners, so it runs on a
self-hosted runner labelled `meta-swift` with rootless podman. Downloads,
sstate and the build directory persist in `~/meta-swift-ci`, or in the
directory set by the repository variable `META_SWIFT_CI_CACHE`. Pull requests
from forks of external contributors only run once approved.

`.github/workflows/lint.yml` runs oelint-adv on GitHub-hosted runners. It only
reports findings for now and does not fail.

To reproduce a CI build locally:

    KAS_WORK_DIR=~/meta-swift-work DL_DIR=... SSTATE_DIR=... \
        kas-container build ci/kas/scarthgap-qemuarm64.yml

`KAS_WORK_DIR` must be outside this checkout, as kas clones poky and
meta-clang there.
