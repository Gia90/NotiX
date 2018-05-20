package org.dev.g14.notix;

import android.app.Notification;
import android.content.Context;
import android.service.notification.NotificationListenerService.RankingMap;
import android.service.notification.StatusBarNotification;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * Created by gia on 21/01/18.
 */

public class NotixHook implements IXposedHookLoadPackage {

    private boolean isToBeKilled(Notification n, String pkgName)
    {
        String nTitle = n.extras.getString(Notification.EXTRA_TITLE);
        String nText = n.extras.getString(Notification.EXTRA_TEXT);

        XposedBridge.log("   from " + pkgName);
        XposedBridge.log("   " + nTitle + ": " + nText);
        // Signal?? https://github.com/WhisperSystems/Signal-Android/blob/e7a9893e94659779680cedcfc3398a664e12abad/src/org/thoughtcrime/securesms/service/MessageRetrievalService.java

        // TODO: check user defined filters to decide whether to kill this notification or not
        if( pkgName.equals("org.thoughtcrime.securesms") && nText.equals("Background connection enabled") ) {
            return true;
        }
        return false;

    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        /** https://github.com/aosp-mirror/platform_frameworks_base/tree/master/core */
        if (!lpparam.packageName.equals("com.android.systemui")) return;

        final Class<?> phoneStatusBarClass = findClass("com.android.systemui.statusbar.phone.PhoneStatusBar", lpparam.classLoader);

        // https://github.com/aosp-mirror/platform_frameworks_base/blob/nougat-release/packages/SystemUI/src/com/android/systemui/statusbar/phone/PhoneStatusBar.java
        // Hooking method: public void addNotification(StatusBarNotification notification, RankingMap ranking, Entry oldEntry)
        findAndHookMethod(phoneStatusBarClass, "addNotification", StatusBarNotification.class, RankingMap.class, "com.android.systemui.statusbar.NotificationData$Entry", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                final StatusBarNotification notif = (StatusBarNotification) param.args[0];
                final String pkg = notif.getPackageName();
                final boolean clearable = notif.isClearable();
                final int id = notif.getId();
                final Notification n = notif.getNotification();

                // Make any notification clearable (WHY IT DOESN'T WORK??)
                n.flags &= ~Notification.FLAG_FOREGROUND_SERVICE;

                //n.extras.putString(Notification.EXTRA_TITLE, "NOTIX: "+n.extras.getString(Notification.EXTRA_TITLE) );

                // to kill or not to kill?
                if( isToBeKilled(n, pkg) ) {
                    XposedBridge.log("   KILL IT!");
                    param.setResult(null);
                }
            }
        });
    }
}

