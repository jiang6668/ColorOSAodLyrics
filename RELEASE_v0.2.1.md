# ColorOS AOD Lyrics v0.2.1

# ⚠️ 设备专用一键包：只确认适用于 OPPO PME110 / PME110_16.0.7.202(CN01) / SystemUI 16.99.12

v0.2.1 修正 v0.2.0 的打包问题：v0.2.0 由 Windows 打包，目录分隔符在手机上被识别成反斜杠文件名，Magisk 无法按目录覆盖 SystemUI。v0.2.1 使用正确 ZIP 目录结构，并在启动脚本里显式 bind mount 补丁 APK。

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

## 严重提醒

如果你的机型、系统版本、SystemUI 版本任意一项不同，请不要刷入这个 ZIP。不同版本直接替换 SystemUI 可能导致 SystemUI 崩溃、黑屏或解锁异常。

其他 ColorOS 版本请看 v0.1.0 的 LSPosed 原型和源码，自行编译适配。

## 校验

- `ColorOSAodLyrics-SystemUI-MagiskModule-device-v0.2.1-fixed.zip`
- SHA256：`7addebb0e2d10dbff2443da9a8b80da1368f1ad4406fc137188211b0a2f824bd`
