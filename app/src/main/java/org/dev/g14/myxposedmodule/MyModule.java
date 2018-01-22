package org.dev.g14.myxposedmodule;

import android.app.Notification;
import android.os.UserHandle;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by gia on 21/01/18.
 */

public class MyModule implements IXposedHookLoadPackage {
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        /** https://github.com/aosp-mirror/platform_frameworks_base/tree/master/core */

        /*
        if (!lpparam.packageName.equals("android"))
            return;

        XposedBridge.log("We are in the android package (core)!");
        */

        // http://api.xposed.info/reference/de/robv/android/xposed/XposedHelpers.html#findAndHookMethod(java.lang.Class%3C?%3E,%20java.lang.String,%20java.lang.Object...)
        // Hooking method: public void notifyAsUser(String tag, int id, Notification notification, UserHandle user)
        findAndHookMethod("android.app.NotificationManager", lpparam.classLoader, "notifyAsUser", String.class, int.class, Notification.class, UserHandle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                String nTag = (String)param.args[0];
                int nId = (int)param.args[1];
                Notification n = (Notification)param.args[2];
                UserHandle nUserHandle = (UserHandle)param.args[3];

                String nTitle = n.extras.getString(Notification.EXTRA_TITLE);
                String nText = n.extras.getString(Notification.EXTRA_TEXT);

                XposedBridge.log("New notification! --> " + nTitle + ": " + nText);
                // Signal?? https://github.com/WhisperSystems/Signal-Android/blob/e7a9893e94659779680cedcfc3398a664e12abad/src/org/thoughtcrime/securesms/service/MessageRetrievalService.java
            }
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                // this will be called after the clock was updated by the original method
            }
        });
    }
}

