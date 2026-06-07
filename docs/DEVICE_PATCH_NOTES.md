# v0.2.0 实机补丁记录

## 结论

v0.2.0 是设备专用 SystemUI 补丁包，不是通用模块。它把实机调试成功的 `SystemUI.apk` 放入 Magisk / KernelSU / APatch 模块，通过系统分区镜像挂载实现开机自动生效。

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
| 一键包 SHA256 | db48ba042e472a922ae767db65e4201b7c6b4e59a41c44d5bdbfe8d1de005b0d |

## 绝对不要跨版本刷入

SystemUI APK 和机型、系统版本、资源表、签名环境、oat 缓存强绑定。不同版本直接刷入可能导致：

- SystemUI 反复重启。
- 黑屏。
- 解锁卡住。
- 息屏显示完全不可用。
- 资源 ID 错位。

如果设备信息不完全一致，请使用 v0.1.0 LSPosed 原型自行适配，不要刷入 v0.2.0 一键包。

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
ColorOSAodLyrics-SystemUI-MagiskModule-device.zip
├── module.prop
├── post-fs-data.sh
├── service.sh
├── README.txt
└── system_ext/priv-app/SystemUI/
    ├── SystemUI.apk
    └── oat/.replace
```

`oat/.replace` 和 `post-fs-data.sh` 用来避免旧 oat 缓存干扰补丁 APK。
