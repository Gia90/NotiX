package org.dev.g14.notix;

import android.app.Notification;
import android.content.Context;
import android.os.UserHandle;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

/**
 * Created by gia on 21/01/18.
 */

public class NotixHook implements IXposedHookZygoteInit {

    private void filterNotification(Notification n, Context nContext)
    {
        String nPackageName = nContext.getPackageName();
        String nTitle = n.extras.getString(Notification.EXTRA_TITLE);
        String nText = n.extras.getString(Notification.EXTRA_TEXT);

        XposedBridge.log("  [New notification] from " + nPackageName);
        XposedBridge.log("     " + nTitle + ": " + nText);
        // Signal?? https://github.com/WhisperSystems/Signal-Android/blob/e7a9893e94659779680cedcfc3398a664e12abad/src/org/thoughtcrime/securesms/service/MessageRetrievalService.java

    }
    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        /** https://github.com/aosp-mirror/platform_frameworks_base/tree/master/core */

        // https://github.com/aosp-mirror/platform_frameworks_base/blob/master/core/java/android/app/NotificationManager.java#L295
        // Hooking method: public void notifyAsUser(String tag, int id, Notification notification, UserHandle user)
        findAndHookMethod("android.app.NotificationManager", null, "notifyAsUser", String.class, int.class, Notification.class, UserHandle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Context nContext = (Context)getObjectField(param.thisObject, "mContext");
                String nTag = (String)param.args[0];
                int nId = (int)param.args[1];
                Notification n = (Notification)param.args[2];
                UserHandle nUserHandle = (UserHandle)param.args[3];

                filterNotification(n, nContext);
            }
        });

        // https://github.com/aosp-mirror/platform_frameworks_base/blob/master/core/java/android/app/Service.java#L697
        // Hooking method: public final void startForeground(int id, Notification notification)
        findAndHookMethod("android.app.Service", null, "startForeground", int.class, Notification.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Context nContext = (Context)param.thisObject;
                int nId = (int)param.args[0];
                Notification n = (Notification)param.args[1];

                filterNotification(n, nContext);
            }
        });
    }
}
