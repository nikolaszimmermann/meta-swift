# CMake package for the C parts of swift-corelibs-libdispatch, so that the
# Swift runtime (dispatch_DIR) and Foundation find the installed library
# instead of building their own copy. Installed by meta-swift.
get_filename_component(_dispatch_prefix "${CMAKE_CURRENT_LIST_DIR}/../../.." ABSOLUTE)

if(NOT TARGET BlocksRuntime)
  add_library(BlocksRuntime SHARED IMPORTED)
  set_target_properties(BlocksRuntime PROPERTIES
    IMPORTED_LOCATION "${_dispatch_prefix}/@LIBDIR@/libBlocksRuntime.so"
    INTERFACE_INCLUDE_DIRECTORIES "${_dispatch_prefix}/@INCLUDEDIR@")
endif()

if(NOT TARGET dispatch)
  add_library(dispatch SHARED IMPORTED)
  set_target_properties(dispatch PROPERTIES
    IMPORTED_LOCATION "${_dispatch_prefix}/@LIBDIR@/libdispatch.so"
    INTERFACE_INCLUDE_DIRECTORIES "${_dispatch_prefix}/@INCLUDEDIR@"
    INTERFACE_LINK_LIBRARIES BlocksRuntime)
endif()

unset(_dispatch_prefix)

# The Swift overlay (Dispatch module, libswiftDispatch) is packaged separately
# by swift-dispatch; describe it when it is installed.
get_filename_component(_dispatch_prefix "${CMAKE_CURRENT_LIST_DIR}/../../.." ABSOLUTE)
if(NOT TARGET swiftDispatch AND EXISTS "${_dispatch_prefix}/@LIBDIR@/swift/linux/libswiftDispatch.so")
  add_library(swiftDispatch SHARED IMPORTED)
  set_target_properties(swiftDispatch PROPERTIES
    IMPORTED_LOCATION "${_dispatch_prefix}/@LIBDIR@/swift/linux/libswiftDispatch.so"
    INTERFACE_INCLUDE_DIRECTORIES "${_dispatch_prefix}/@LIBDIR@/swift;${_dispatch_prefix}/@LIBDIR@/swift/linux"
    INTERFACE_LINK_LIBRARIES dispatch)
endif()
unset(_dispatch_prefix)
