// Compiled with the SDK by ci/test-sdk.sh: Swift, Dispatch and Foundation of
// the target, linked by the SDK toolchain. The swift_sdk runtime test runs it
// in QEMU and checks the output.
import Dispatch
import Foundation

let queue = DispatchQueue(label: "sdk-test")
let epoch = queue.sync {
    ISO8601DateFormatter().string(from: Date(timeIntervalSince1970: 0))
}
print("Hello from the Swift SDK, \(epoch)")
