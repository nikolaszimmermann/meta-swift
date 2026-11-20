#include "Greeter.h"

namespace greeter {
std::string greeting(const std::string& name)
{
    return "Hello, " + name + ", from C++";
}

std::vector<int> fibonacci(int count)
{
    std::vector<int> result;
    int a = 0, b = 1;
    for (int i = 0; i < count; ++i) {
        result.push_back(a);
        int next = a + b;
        a = b;
        b = next;
    }
    return result;
}
}
