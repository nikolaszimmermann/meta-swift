import Dispatch

let group = DispatchGroup()
var message = ""
group.enter()
Task {
    message = "Hello from Swift \(#fileID.isEmpty ? "" : "on Yocto")"
    group.leave()
}
group.wait()
print(message)
