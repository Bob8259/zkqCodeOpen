package com.coc.zkqcode.core.system.checkpermissions

enum class RootStatus {
    CHECKING,
    ROOT_DENIED,      // 没有Root权限
    PERMISSION_DENIED, // 有Root但静默授权失败，需手动跳转
    WAITING_FOR_SERVER, // 等待服务器启动
    SERVER_ERROR,      // 连接服务器失败
    GRANTED           // 全部权限已就绪
}
