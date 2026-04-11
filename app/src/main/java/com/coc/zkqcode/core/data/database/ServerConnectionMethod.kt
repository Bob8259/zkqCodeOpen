package com.coc.zkqcode.core.data.database

/**
 * Describes the currently selected transport used to talk to the local server.
 */
enum class ServerConnectionMethod {
    WEBSOCKET,
    LIBSU_CURL,
    LIBSU_WGET
}
