# 实现说明

## 为什么不能做成普通 App

AOD 息屏界面由 `com.android.systemui` 进程绘制。普通 App 即使有悬浮窗权限，也不能稳定进入系统息屏层，也不能修改 SystemUI 内部的媒体卡片布局。因此可复用方案应走：

- LSPosed hook SystemUI，运行时修改 AOD 媒体卡片。
- 或 Magisk/KernelSU/APatch 挂载替换 SystemUI APK，但这个方案强依赖 SystemUI 版本。

## 当前 hook 点

模块在 `com.android.systemui` 进程加载后尝试 hook：

```text
com.oplusos.systemui.aod.mediapanel.AodMediaControlPanel#bindPlayer(MediaData, String)
```

实机补丁里这个方法每次 AOD 音乐卡片绑定媒体数据时都会调用。模块在 afterHookedMethod 中：

1. 取出 `mediaViewHolder`。
2. 调用 `getTitleText()` 得到标题 TextView。
3. 隐藏 `albumView`、`appIcon`、`icGuideView`、`recommendIcon`、`artistText`。
4. 把标题 TextView 配置成居中、最多 2 行、固定 3 行高度。
5. 保存当前 `MediaData` 和 `MediaController`，每秒刷新歌词。

## 歌词来源

### SystemUI 媒体歌词对象

反射读取：

```text
MediaData#getMediaDataEx()
OplusMediaDataEx#getLyricData()
OplusMediaLyricData#getCurrentLyric(position)
```

`position` 来自：

```text
MediaController#getPlaybackState().getPosition()
```

### 广播歌词

模块还注册 SystemUI 进程内广播接收器，监听：

```text
Lyric_Data
Codex_AOD_LYRIC
```

支持：

- `intent.getStringExtra("lyric")`
- `intent.getParcelableExtra("Data")` 后反射调用 `getLyric()`

## 适配其他 ColorOS 版本

如果模块无效，优先检查：

1. AOD 音乐卡片类名是否仍为 `com.oplusos.systemui.aod.mediapanel.AodMediaControlPanel`。
2. 方法是否仍为 `bindPlayer(MediaData, String)`。
3. holder 是否仍有这些方法：
   - `getTitleText`
   - `getAlbumView`
   - `getAppIcon`
   - `getIcGuideView`
   - `getRecommendIcon`
   - `getArtistText`
4. SystemUI 日志里是否有 `ColorOSAodLyrics`。

## 调试日志

过滤：

```text
ColorOSAodLyrics
AOD_MEDIA_PANEL
AndroidRuntime
```

如果看到 `hook installed`，说明 hook 点命中。
如果看到 `minimal style applied`，说明已经拿到 AOD 卡片视图。
如果看到 `update lyric ...`，说明歌词刷新链路已通。
