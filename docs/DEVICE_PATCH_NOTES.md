# v0.2.2-safe 实机补丁记录

## 结论

v0.2.2-safe 是设备专用 SystemUI 补丁包，不是通用模块。它把实机调试成功的 `SystemUI.apk` 放入 Magisk / KernelSU / APatch 模块私有目录，不走开机早期 Magic Mount；等系统启动完成后，再由 `service.sh` 显式 bind mount 到 SystemUI 位置，复现手动调试成功的路径。

v0.2.0 的 ZIP 由 Windows 打包，目录分隔符在手机上被识别成反斜杠文件名，导致 Magisk 无法按目录覆盖。v0.2.1 虽修复目录结构，但开机早期处理 SystemUI 仍然偏激进。v0.2.2-safe 改成延迟挂载，是当前推荐版本。

## 已验证设备

| 项目 | 值 |
| --- | --- |
| 品牌 | OPPO |
| 机型 | PME110 |
| 设备代号 | OP61C1L1 |
| 系统显示版本 | PME110_16.0.7.202(CN01) |
| Android 版本 | 16 / SDK 36 |
| 构建增量 | B.4195ed2-1e255a5-1e2e1b8 |
| 构建指纹 | OPPO/PME110/OP61C1L1:16/BP2A.250605.015/B.4195ed2-1e255a5-1e2e1b8:user/release-keys |
| OPlus ROM | V16.1.0 |
| 安全补丁 | 2026-04-01 |
| SystemUI 包名 | com.android.systemui |
| SystemUI versionName | 16.99.12 |
| SystemUI versionCode | 169912 |
| SystemUI APK SHA256 | 50049b655f1c35c534d5d1814358c714a50145b7bb3f36216bd96bc89fcc36e1 |
| 一键包 SHA256 | 458b9a6918470265b6582a7239eceb839debadd7e067ca7e46fbebbed15c3ab7 |

## 启动时的正常现象

刷入 v0.2.2-safe 后，手机进系统约 20 多秒时会黑屏/闪屏一次，这是模块在系统启动完成后主动重启 SystemUI，用来从官方界面切换到歌词补丁界面。只要之后能正常进入桌面、锁屏和息屏显示，这不是设备故障。

## 绝对不要跨版本刷入

SystemUI APK 和机型、系统版本、资源表、签名环境、oat 缓存强绑定。不同版本直接刷入可能导致：

- SystemUI 反复重启。
- 黑屏。
- 解锁卡住。
- 息屏显示完全不可用。
- 资源 ID 错位。

如果设备信息不完全一致，请使用 v0.1.0 LSPosed 原型自行适配，不要刷入 v0.2.0 一键包。

## 歌词测试环境

当前实机验证的歌词来源环境是 QQ 音乐 14.11.0.8 旧版 + 词幕 1.3.3。更高版本 QQ 音乐和其他音乐 App 暂未测试，可能需要其他玩家继续验证或适配。

## 成功视觉状态

- AOD 音乐卡片显示实时歌词。
- 歌词可以随播放进度更新。
- 隐藏专辑图、App 图标、小频谱图标、引导图标、歌手行。
- 标题 TextView 承载歌词，最多 2 行。
- 保留 3 行高度，避免两行歌词上沿裁切。
- 保留稳定基线里的宽度和约束，避免重新出现右侧溢出或一行显示问题。

## 关键 SystemUI 类

```text
com.oplusos.systemui.aod.mediapanel.AodMediaControlPanel
com.oplusos.systemui.aod.mediapanel.AodMediaViewHolder
```

## 模块结构

```text
ColorOSAodLyrics-SystemUI-MagiskModule-device-v0.2.2-safe.zip
├── module.prop
├── post-fs-data.sh
├── service.sh
├── README.txt
└── files/
    ├── SystemUI.apk
    └── empty_oat/
```

`service.sh` 会等待 `sys.boot_completed=1` 后再延迟挂载补丁 APK 和空 oat 目录，然后重启一次 SystemUI。
