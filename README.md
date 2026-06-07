# ColorOS AOD Lyrics

ColorOS AOD Lyrics 是一个实验性 LSPosed 模块原型，用来把 ColorOS 息屏显示里的音乐卡片改成极简实时歌词显示。

当前原型来自一台 ColorOS 设备上的实测 SystemUI 补丁：隐藏专辑图、App 图标、频谱/引导图标和歌手行，只保留 AOD 音乐卡片中的歌词文字，并允许歌词自动刷新。

## 当前状态

- 推荐形态：LSPosed 模块。
- 普通 App 单独安装：不能生效。普通 App 没有权限修改 SystemUI 的 AOD 视图。
- Magisk/SystemUI APK 补丁：本机已验证可行，但只适合完全相同的 SystemUI 版本，不推荐直接给其他机型通刷。
- 复用到其他 ColorOS 机型：需要同样存在 `com.oplusos.systemui.aod.mediapanel.AodMediaControlPanel` 这类 AOD 媒体面板类；如果类名或布局方法不同，需要改 hook 点。

## 依赖关系

- 需要 root + LSPosed。
- 需要启用模块作用域到 `系统界面` / `com.android.systemui`。
- 不强制依赖词幕模块，但建议搭配词幕或其他能向 SystemUI/状态栏提供歌词的模块。
- 当前兼容的歌词来源：
  - SystemUI 媒体数据里的歌词对象。
  - `Lyric_Data` 广播。
  - `Codex_AOD_LYRIC` 广播，extra 名为 `lyric`。

## 使用方法

1. 安装构建出的 APK。
2. 在 LSPosed 中启用模块。
3. 作用域选择 `系统界面` / `com.android.systemui`。
4. 重启 SystemUI 或重启手机。
5. 播放音乐，开启息屏显示，查看 AOD 音乐卡片。

## 已知限制

- ColorOS 各版本 AOD 类名和布局可能不同，模块可能需要适配。
- 普通 Android 安装权限无法单独完成这个功能。
- 如果系统音乐卡片没有出现，模块也没有可替换的 AOD 入口。
- 如果歌词来源只在独立 App 进程里显示、没有传入 SystemUI，则模块拿不到歌词。

## 本项目文件

- `app/src/main/java/com/codex/colorosaodlyrics/HookEntry.java`：LSPosed 入口和核心 hook 逻辑。
- `app/src/main/assets/xposed_init`：Xposed/LSPosed 入口声明。
- `docs/IMPLEMENTATION.md`：实现细节和适配说明。
- `docs/DEVICE_PATCH_NOTES.md`：本次实机 SystemUI 补丁方案记录。

## 致谢

这个原型由 Codex 和实机调试共同整理。欢迎其他 ColorOS 玩家继续改进、适配更多机型。
