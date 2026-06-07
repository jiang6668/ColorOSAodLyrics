# 实机补丁记录

## 已验证设备上的成功方案

成功方案是临时挂载修改后的 SystemUI APK：

- 原 SystemUI 不永久替换。
- 使用 bind mount 挂载到 `/system_ext/priv-app/SystemUI/SystemUI.apk`。
- 同时挂载空目录隐藏 `/system_ext/priv-app/SystemUI/oat`。
- 重启手机即可清除临时挂载。

## 成功视觉状态

- AOD 音乐卡片显示实时歌词。
- 隐藏专辑图。
- 隐藏 QQ 音乐图标。
- 隐藏小频谱/引导图标。
- 隐藏歌手行。
- 标题 TextView 显示歌词，最多 2 行。
- 为避免两行上沿裁切，保留 3 行高度。

## 关键 SystemUI 类

```text
com.oplusos.systemui.aod.mediapanel.AodMediaControlPanel
com.oplusos.systemui.aod.mediapanel.AodMediaViewHolder
```

## 为什么不建议通刷 SystemUI APK

SystemUI APK 与系统版本、签名、资源表、oat 缓存都强绑定。不同机型直接替换很容易导致：

- SystemUI 重启。
- 黑屏。
- 解锁卡顿。
- 资源 ID 错位。

因此给其他玩家复用时，优先尝试 LSPosed 模块。
