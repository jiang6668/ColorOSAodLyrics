# 构建和测试

## 本次产物

已构建调试 APK：

```text
ColorOSAodLyrics-lsposed-debug.apk
```

这是 LSPosed 模块原型，不是普通独立 App。

## 构建命令

在项目根目录执行：

```powershell
$env:JAVA_HOME='C:\Users\Administrator\.jdks\jbr-17.0.14'
$env:ANDROID_HOME='C:\Users\Administrator\AppData\Local\Android\Sdk'
C:\Users\Administrator\.gradle\wrapper\dists\gradle-8.7-bin\bhs2wmbdwecv87pi65oeuq5iu\gradle-8.7\bin\gradle.bat assembleDebug --offline
```

生成路径：

```text
app/build/outputs/apk/debug/app-debug.apk
```

## 测试步骤

1. 安装 APK。
2. 打开 LSPosed，启用模块。
3. 作用域选择 `com.android.systemui`。
4. 重启 SystemUI 或手机。
5. 播放音乐并进入息屏显示。
6. 查看 LSPosed 日志或 logcat：

```text
ColorOSAodLyrics
```

## 注意

当前 APK 是原型版本，尚未在其他 ColorOS 机型验证。  
如果启用后 SystemUI 异常，先在 LSPosed 中禁用模块并重启。
