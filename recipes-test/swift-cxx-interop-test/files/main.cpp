#include "SwiftGreeter-Swift.h"
#include <cstdio>
#include <string>

int main()
{
    std::string result = SwiftGreeter::greet("Yocto");
    std::printf("%s\n", result.c_str());
    return result.find("from C++ via Swift [fib 0,1,1,2,3,5,8,13,21,34]") == std::string::npos;
}
