package com.coc.zkqcode.nativehelper

object RustBridge {
    init {
        // 名字必须和 Cargo.toml 中的 name 一致
        System.loadLibrary("rust_logic")
    }

    // 声明 native 方法，名字必须和 Rust 里的对应
    external fun sayHello(input: String): String
}