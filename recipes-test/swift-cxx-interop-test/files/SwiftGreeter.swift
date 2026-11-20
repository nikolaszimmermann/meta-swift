import CxxStdlib
import Greeter

public func greet(_ name: String) -> String {
    let fromCxx = String(greeter.greeting(std.string(name)))
    let fib = greeter.fibonacci(10).map { String($0) }.joined(separator: ",")
    return "\(fromCxx) via Swift [fib \(fib)]"
}
