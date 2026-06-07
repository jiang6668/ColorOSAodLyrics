# ColorOS AOD Lyrics v0.2.2-safe

# ⚠️ 设备专用一键包：只确认适用于 OPPO PME110 / PME110_16.0.7.202(CN01) / SystemUI 16.99.12

v0.2.2-safe 是当前推荐版本。它不再在开机早期直接覆盖 SystemUI，而是先让官方 SystemUI 正常启动，等待系统完成启动后再延迟 bind mount 补丁 APK，并重启一次 SystemUI。

## 已验证版本

- 品牌：OPPO
- 机型：PME110
- 设备代号：OP61C1L1
- 系统显示版本：PME110_16.0.7.202(CN01)
- Android：16 / SDK 36
- OPlus ROM：V16.1.0
- 安全补丁：2026-04-01
- SystemUI：com.android.systemui
- SystemUI versionName：16.99.12
- SystemUI versionCode：169912

## 启动时的正常现象

刷入后，手机进系统约 20 多秒时会黑屏/闪屏一次。这是模块在系统启动完成后主动重启 SystemUI，用来从官方界面切换到歌词补丁界面。只要之后能正常进入桌面、锁屏和息屏显示，这不是设备故障。

## 歌词测试环境

当前实机验证的歌词来源环境是 QQ 音乐 14.11.0.8 旧版 + 词幕 1.3.3。更高版本 QQ 音乐和其他音乐 App 暂未测试，可能需要其他玩家继续验证或适配。

## 严重提醒

如果你的机型、系统版本、SystemUI 版本任意一项不同，请不要刷入这个 ZIP。不同版本直接替换 SystemUI 可能导致 SystemUI 崩溃、黑屏或解锁异常。

其他 ColorOS 版本请看 v0.1.0 的 LSPosed 原型和源码，自行编译适配。

## 校验

- `ColorOSAodLyrics-SystemUI-MagiskModule-device-v0.2.2-safe.zip`
- SHA256：`458b9a6918470265b6582a7239eceb839debadd7e067ca7e46fbebbed15c3ab7`
