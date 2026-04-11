package com.coc.zkqcode.core.data.database

/**
 * Describes the currently selected transport used to talk to the local server.
 */
enum class ServerConnectionMethod {
    LOCAL_WS_SERVER,
    LIBSU_CURL,
    LIBSU_WGET
}
