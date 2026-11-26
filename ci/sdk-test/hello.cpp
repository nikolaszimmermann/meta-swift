// Compiled with the private clang of the SDK ($SWIFT_CLANGCXX) by
// ci/test-sdk.sh: it has to find libstdc++ and the gcc install of the target
// sysroot, like the C++ code that Swift imports.
#include <iostream>
#include <string>
#include <vector>

int main()
{
    std::vector<std::string> words { "Hello", "from", "the", "Swift", "clang" };
    std::string line;
    for (const auto& word : words)
        line += (line.empty() ? "" : " ") + word;
    std::cout << line << std::endl;
    return 0;
}
