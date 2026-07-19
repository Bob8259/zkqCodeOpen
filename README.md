# 介绍

紫孔雀旨在提供无障碍服务，帮助行动不便者、视觉障碍者等人群完成简易的游戏操作。

若您认为紫孔雀存在侵权问题，请携带您的版权证据与紫孔雀联系。紫孔雀将在第一时间处理。

# 开箱即用

目前该软件仅对少部分通过挑选的用户开放。如果想使用的话可以联系紫孔雀，并告知使用目的，通过筛选后即可使用。本软件为自用软件，禁止用于商业行为，禁止用于违反游戏规定的行为。

# 开发

如果你想自己本地构建，可以参考下面的步骤。因为这个项目非常小，只有作者一个人在开发，所以一般会先在本地开发一段时间，然后再把代码上传。作者会尽量上传最新的代码，但不保证一定能确保代码是最新的。不过大家还是能从这个仓库里学到不少东西，比如怎么做不重启的热更新、怎么在安卓上跑 Rust 代码、怎么实现自动化操作之类的。

本项目依赖 https://github.com/Bob8259/ZKQserver 来提供关键操作，没有服务器文件跑不起来。
部分额外功能依赖建筑检测插件仓库 https://github.com/Bob8259/building_plugin ，不过没有也能正常运行。

1. **准备环境**
   下载最新版 Android Studio，然后配置好 Rust 环境，并构建 Rust 代码。
2. **修改代码**
   开一个AI助理，把以下提示词给AI：帮我修改代码，删除Kotlin和Rust代码的完整性校验。然后删除所有和广告，用户登录，用户验证相关的代码。同时删掉Rust里所有反调试或反hook相关代码。
3. **构建**
   构建完就能跑了。

# 小技巧

本项目支持像按键精灵/懒人精灵那样一键运行脚本，不用启动界面。步骤如下：

1. 把 `app\src\main\java\com\coc\zkqcode\jar` 文件夹备份到别的地方，然后从项目里删掉。
2. 构建项目，装到模拟器上。
3. 把第一步备份的 jar 文件夹放回原来的位置。
4. 运行 `deployAndReload`，就能一键跑脚本了。

# 致谢

本项目的开发离不开以下开源项目和库的支持，在此表示感谢。

## 参考项目

| 项目 | 作者 | 说明 | 许可证 |
|------|------|------|--------|
| [scrcpy](https://github.com/Genymobile/scrcpy) | Genymobile | 屏幕镜像与控制，本项目参考了其设计思路 | Apache 2.0 |
| [libsu](https://github.com/topjohnwu/libsu) | topjohnwu | Android Root Shell 库 | Apache 2.0 |
| [Reorderable](https://github.com/Calvin-LL/Reorderable) | Calvin Liang | Compose 拖拽排序组件，代码直接引用于本项目 | Apache 2.0 |

## 使用的开源库

### Android / Kotlin

| 库 | 作者 | 说明 |
|----|------|------|
| [OkHttp](https://github.com/square/okhttp) | Square | HTTP 客户端 |
| [Gson](https://github.com/google/gson) | Google | JSON 序列化/反序列化 |
| [Ktor](https://github.com/ktorio/ktor) | JetBrains | 异步服务器框架（WebSocket 等） |
| [Timber](https://github.com/JakeWharton/timber) | Jake Wharton | 日志工具 |
| [ML Kit Text Recognition](https://developers.google.com/ml-kit) | Google | OCR 文字识别 |

### Rust

| 库 | 说明 |
|----|------|
| [jni](https://crates.io/crates/jni) | Rust 与 Java/Kotlin 的 JNI 交互 |
| [x25519-dalek](https://crates.io/crates/x25519-dalek) | X25519 Diffie-Hellman 密钥交换 |
| [chacha20](https://crates.io/crates/chacha20) | ChaCha20 流加密（RustCrypto） |
| [blake2](https://crates.io/crates/blake2) | BLAKE2 哈希算法（RustCrypto） |
| [android_logger](https://crates.io/crates/android_logger) | Android 平台日志输出 |
| [obfstr](https://crates.io/crates/obfstr) | 编译期字符串混淆 |
| [rustix](https://crates.io/crates/rustix) | 底层 POSIX API 绑定 |
| [rand](https://crates.io/crates/rand) | 随机数生成 |

# 免责声明

本项目已在 Gitee 平台开源，所有源代码公开透明，接受社区审查。本软件仅通过图色识别与模拟点击的方式，复现新手玩家的基本操作流程，不涉及任何内存读写、协议篡改或数据注入等违规/作弊行为。所有权限（即Root权限）均由用户手动授予，合法合规。本质原理和用户对游戏进行录屏，或者开鼠标宏操作一样，完全绿色安全。如果本软件违规，那么所有游戏录屏软件以及鼠标宏/安卓模拟器均违规。本软件无侵入性功能，不会对游戏内的对战环境产生任何影响，不影响游戏公平性。

严禁将本项目用于任何违反法律法规或游戏服务条款的用途。若因使用者自身行为导致任何法律纠纷或账号处罚，一切后果由使用者本人承担，与本项目开发者无关。
