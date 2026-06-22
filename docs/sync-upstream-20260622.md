# 与上游 main 分支同步报告

**同步时间**: 2026-06-22  
**上游分支**: `upstream/main` (commit `9a2f5052`)  
**本地分支**: `feat/disable-main-base`  
**同步范围**: 仅 `app/src/main/java/com/coc/zkqcode/jar/` 目录

---

## 同步规则

| 情况 | 操作 |
|------|------|
| 本地有，上游没有 | ✅ 删除 |
| 本地没有，上游有 | ✅ 新增 |
| 本地和上游都有 | ✅ 覆盖为上游版本 |
| jar/ 以外的文件 | ❌ 不做任何修改 |

---

## 删除的文件（4 个）

这些文件来自之前合并的 `feat/push-notification` 分支，上游 `main` 尚未合入：

| 文件 | 说明 |
|------|------|
| `jar/code/auth/PushPlusHelper.kt` | PushPlus 推送助手 |
| `jar/code/auth/ServerChanHelper.kt` | Server酱³ 推送助手 |
| `jar/code/auth/TelegramHelper.kt` | Telegram 推送助手 |
| `jar/code/auth/WxPusherHelper.kt` | WxPusher 推送助手 |

---

## 覆盖的文件（10 个）

以下文件被上游 `main` 版本覆盖（内容不同）：

| 文件 | 变更说明 |
|------|----------|
| `jar/code/MainScript.kt` | 上游没有推送通知相关代码 |
| `jar/code/mainbase/MainBaseScript.kt` | 恢复到上游版本（后续重新加了关闭主世界守卫） |
| `jar/code/mainbase/attack/SearchOpponents.kt` | 上游有更新 |
| `jar/code/universal/network/WebDAVHelper.kt` | 上游有更新 |
| `jar/ui/Strings.kt` | 恢复到上游版本（后续重新加了 noMainBaseExplain） |
| `jar/ui/pages/builderbase/BuilderBaseConfig.kt` | 恢复到上游版本（后续重新加了互斥逻辑） |
| `jar/ui/pages/mainbase/MainBaseConfigs.kt` | 恢复到上游版本（后续重新加了关闭主世界 UI） |
| `jar/ui/pages/single/SpecialSettings.kt` | 上游有更新 |
| `jar/ui/schema/details/BuilderBaseSettings.kt` | 上游有更新 |
| `jar/ui/schema/details/MainBaseSettings.kt` | 恢复到上游版本（后续重新加了 NO_MAIN_BASE） |

---

## 新增的文件（0 个）

上游 `main` 没有新增 `jar/` 下的文件。

---

## 同步后重新应用的本地更改

同步后，以下「关闭主世界」改动被重新应用：

| 文件 | 改动 |
|------|------|
| `jar/code/mainbase/MainBaseScript.kt` | `playMainBase()` 入口添加 `noMainBase` 守卫 + import |
| `jar/ui/Strings.kt` | 添加 `noMainBaseExplain` 中英文字符串 |
| `jar/ui/pages/builderbase/BuilderBaseConfig.kt` | 关闭夜世界时自动开启主世界（互斥） |
| `jar/ui/pages/mainbase/MainBaseConfigs.kt` | 顶部新增 toggle，所有区块添加可见性守卫 |
| `jar/ui/schema/details/MainBaseSettings.kt` | 新增 `NO_MAIN_BASE` 设置定义 |

---

## 回退方法

如需回退到同步前的状态，执行：

```bash
git checkout backup-before-sync
```
