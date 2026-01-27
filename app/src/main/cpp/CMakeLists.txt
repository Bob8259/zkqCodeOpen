cmake_minimum_required(VERSION 3.22.1)

project("zkqcode")

# ==============================================================================
# 1. 核心安全防护：编译参数 (Compiler Flags)
# 作用范围：native-lib.cpp 和 login.cpp 都会应用这些参数
# ==============================================================================

# -fvisibility=hidden:
#   [防破解核心] 默认隐藏所有函数符号。
#   除了你明确标记为 JNIEXPORT 或 JNI_OnLoad 的函数外，
#   login.cpp 里的内部算法函数将完全无法在导出表中找到。
#
# -ffunction-sections -fdata-sections:
#   将每个函数和数据放入独立的段中。
#   这是为了配合后面链接器的 --gc-sections，把没用到的代码彻底删掉，防止残留。
#
# -Oz (或 -Os):
#   优化等级。-Oz 是针对体积的极限优化。
#   它生成的汇编代码往往不按常理出牌（指令重排），比默认的 -O0 更难看懂。
#
# -fno-rtti -fno-exceptions:
#   禁用运行时类型信息和异常机制。
#   这可以去除大量 C++ 元数据，让逆向工具（如 IDA）难以还原类结构和继承关系。
#
# -fstack-protector-all:
#   开启所有函数的栈保护。
#   会在函数头尾插入检测代码（Canary），稍微干扰反编译的控制流分析。

set(SAFE_FLAGS "-fvisibility=hidden -ffunction-sections -fdata-sections -Oz -fno-rtti -fno-exceptions -fstack-protector-all")

# 将标志应用到 C 和 C++ 编译器
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} ${SAFE_FLAGS}")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${SAFE_FLAGS}")

# ==============================================================================
# 2. 定义库文件
# ==============================================================================

add_library(
        native-lib
        SHARED
        native-lib.cpp
        security/login.cpp
        security/cypherhelper.cpp
        security/keys.cpp
        security/anti_debug.cpp
        dex/dex_loader.cpp
        colors/multi_colors.cpp
        colors/multi_colors_raw.cpp
        monocypher/monocypher.c
)

find_library(
        log-lib
        log
)

target_include_directories(native-lib PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}
        ${CMAKE_CURRENT_SOURCE_DIR}/security
        ${CMAKE_CURRENT_SOURCE_DIR}/dex
        ${CMAKE_CURRENT_SOURCE_DIR}/colors
        ${CMAKE_CURRENT_SOURCE_DIR}/common
)

target_link_libraries(
        native-lib
        ${log-lib}
        jnigraphics
)

# ==============================================================================
# 3. 核心安全防护：链接参数 (Linker Flags)
# ==============================================================================

# -Wl,--gc-sections:
#   垃圾回收。删除没有被调用的死代码（配合上面的 -ffunction-sections 生效）。
#
# -Wl,--strip-all:
#   [重要] 移除所有符号表和调试信息。
#
# -Wl,-s:
#   是 strip-all 的缩写，双重保险。

set_target_properties(native-lib PROPERTIES
        LINK_FLAGS "-Wl,--gc-sections -Wl,--strip-all -Wl,-s -Wl,-z,max-page-size=16384"
)