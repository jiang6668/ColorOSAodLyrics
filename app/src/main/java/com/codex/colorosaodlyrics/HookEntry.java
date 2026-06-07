package com.codex.colorosaodlyrics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class HookEntry implements IXposedHookLoadPackage {
    private static final String TAG = "ColorOSAodLyrics";
    private static WeakReference<TextView> targetText = new WeakReference<>(null);
    private static Object currentMediaData;
    private static MediaController currentController;
    private static Handler handler;
    private static Runnable poller;
    private static boolean receiverRegistered;
    private static String lastLyric;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam == null || !"com.android.systemui".equals(lpparam.packageName)) {
            return;
        }
        hookAodPanel(lpparam.classLoader);
    }

    private static void hookAodPanel(ClassLoader classLoader) {
        try {
            XposedHelpers.findAndHookMethod(
                    "com.oplusos.systemui.aod.mediapanel.AodMediaControlPanel",
                    classLoader,
                    "bindPlayer",
                    "com.android.systemui.media.controls.shared.model.MediaData",
                    String.class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            onBindPlayer(param);
                        }
                    });
            log("hook installed");
        } catch (Throwable t) {
            log("hook failed: " + t);
            XposedBridge.log(t);
        }
    }

    private static void onBindPlayer(XC_MethodHook.MethodHookParam param) {
        try {
            if (param == null || param.args == null || param.args.length == 0) {
                return;
            }
            Object panel = param.thisObject;
            currentMediaData = param.args[0];
            Object controller = getField(panel, "controller");
            currentController = controller instanceof MediaController ? (MediaController) controller : null;

            Object holder = getField(panel, "mediaViewHolder");
            TextView title = asTextView(call(holder, "getTitleText"));
            if (title == null) {
                return;
            }

            ensureText(title);
            applyMinimalStyle(holder);
            registerReceiver(title.getContext());
            refreshMediaLyric();
            startPoll();
        } catch (Throwable t) {
            log("bind failed: " + t);
        }
    }

    private static void ensureText(TextView textView) {
        targetText = new WeakReference<>(textView);
        int width = 1080;
        textView.setWidth(width);
        textView.setMaxWidth(width);
        textView.setMinWidth(width);
        textView.setGravity(Gravity.CENTER);
        textView.setMaxLines(2);
        textView.setLines(3);
        textView.setSingleLine(false);
        textView.setHorizontallyScrolling(false);
        if (lastLyric != null && !lastLyric.isEmpty()) {
            textView.setText(lastLyric);
        }
    }

    private static void applyMinimalStyle(Object holder) {
        if (holder == null) {
            return;
        }
        hide(call(holder, "getAlbumView"));
        hide(call(holder, "getAppIcon"));
        hide(call(holder, "getIcGuideView"));
        hide(call(holder, "getRecommendIcon"));
        Object artist = call(holder, "getArtistText");
        if (artist instanceof TextView) {
            ((TextView) artist).setText(null);
        }
        hide(artist);
        log("minimal style applied");
    }

    private static void hide(Object view) {
        if (view instanceof View) {
            ((View) view).setVisibility(View.GONE);
        }
    }

    private static void registerReceiver(Context context) {
        if (receiverRegistered || context == null) {
            return;
        }
        try {
            Context appContext = context.getApplicationContext();
            IntentFilter filter = new IntentFilter();
            filter.addAction("Lyric_Data");
            filter.addAction("Codex_AOD_LYRIC");
            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    handleLyricIntent(intent);
                }
            };
            if (Build.VERSION.SDK_INT >= 33) {
                appContext.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED);
            } else {
                appContext.registerReceiver(receiver, filter);
            }
            receiverRegistered = true;
            log("receiver registered");
        } catch (Throwable t) {
            log("receiver failed: " + t);
        }
    }

    private static void handleLyricIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        try {
            String lyric = intent.getStringExtra("lyric");
            if (lyric == null || lyric.isEmpty()) {
                Parcelable data = intent.getParcelableExtra("Data");
                Object reflected = call(data, "getLyric");
                if (reflected instanceof String) {
                    lyric = (String) reflected;
                }
            }
            update(lyric);
        } catch (Throwable t) {
            log("receive failed: " + t);
        }
    }

    private static void startPoll() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        if (poller == null) {
            poller = new Runnable() {
                @Override
                public void run() {
                    refreshMediaLyric();
                    if (handler != null) {
                        handler.postDelayed(this, 1000L);
                    }
                }
            };
        }
        handler.removeCallbacks(poller);
        handler.post(poller);
    }

    private static void refreshMediaLyric() {
        update(getCurrentMediaLyric());
    }

    private static String getCurrentMediaLyric() {
        try {
            Object ex = call(currentMediaData, "getMediaDataEx");
            Object lyricData = call(ex, "getLyricData");
            if (lyricData == null) {
                return null;
            }
            long position = 0L;
            if (currentController != null) {
                PlaybackState state = currentController.getPlaybackState();
                if (state != null) {
                    position = state.getPosition();
                }
            }
            Object lyric = call(lyricData, "getCurrentLyric", position);
            return lyric instanceof String ? (String) lyric : null;
        } catch (Throwable t) {
            return null;
        }
    }

    private static void update(String lyric) {
        if (lyric == null || lyric.isEmpty()) {
            return;
        }
        lastLyric = lyric;
        TextView textView = targetText.get();
        if (textView != null) {
            textView.setGravity(Gravity.CENTER);
            textView.setMaxLines(2);
            textView.setLines(3);
            textView.setSingleLine(false);
            textView.setHorizontallyScrolling(false);
            textView.setText(lyric);
        }
        log("update lyric " + lyric);
    }

    private static Object getField(Object object, String fieldName) {
        if (object == null) {
            return null;
        }
        try {
            return XposedHelpers.getObjectField(object, fieldName);
        } catch (Throwable t) {
            return null;
        }
    }

    private static Object call(Object object, String methodName, Object... args) {
        if (object == null) {
            return null;
        }
        try {
            return XposedHelpers.callMethod(object, methodName, args);
        } catch (Throwable t) {
            return null;
        }
    }

    private static TextView asTextView(Object object) {
        return object instanceof TextView ? (TextView) object : null;
    }

    private static void log(String message) {
        XposedBridge.log(TAG + ": " + message);
    }
}
