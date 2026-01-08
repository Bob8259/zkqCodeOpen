cmake_minimum_required(VERSION 3.22.1)

project("zkqcode")

add_library(
    native-lib
    SHARED
    native-lib.cpp
    login.cpp
)

find_library(
    log-lib
    log
)

target_link_libraries(
    native-lib
    ${log-lib}
)

# Strip the shared library to remove symbols and align to 16 KB page size
set_target_properties(native-lib PROPERTIES LINK_FLAGS "-Wl,--gc-sections -Wl,--strip-all -Wl,-z,max-page-size=16384")
