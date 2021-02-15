

package com.skripsi.berbincang.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import autodagger.AutoInjector
import com.skripsi.berbincang.application.NextcloudTalkApplication
import com.skripsi.berbincang.utils.NotificationUtils
import com.skripsi.berbincang.utils.database.user.UserUtils
import com.skripsi.berbincang.utils.preferences.AppPreferences

import javax.inject.Inject

@AutoInjector(NextcloudTalkApplication::class)
class PackageReplacedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var userUtils: UserUtils

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onReceive(context: Context, intent: Intent?) {
        NextcloudTalkApplication.sharedApplication!!.componentApplication.inject(this)

        if (intent != null && intent.action != null &&
                intent.action == "android.intent.action.MY_PACKAGE_REPLACED") {
            try {
                val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                if (packageInfo.versionCode > 43 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val notificationManager = context.getSystemService(Context
                            .NOTIFICATION_SERVICE) as NotificationManager

                    if (notificationManager != null) {
                        if (!appPreferences!!.isNotificationChannelUpgradedToV2) {
                            for (notificationChannelGroup in notificationManager
                                    .notificationChannelGroups) {
                                notificationManager.deleteNotificationChannelGroup(notificationChannelGroup.id)
                            }

                            notificationManager.deleteNotificationChannel(NotificationUtils.NOTIFICATION_CHANNEL_CALLS)
                            notificationManager.deleteNotificationChannel(NotificationUtils.NOTIFICATION_CHANNEL_MESSAGES)

                            appPreferences!!.setNotificationChannelIsUpgradedToV2(true)
                        }

                        if (!appPreferences!!.isNotificationChannelUpgradedToV3 && packageInfo.versionCode > 51) {
                            notificationManager.deleteNotificationChannel(NotificationUtils.NOTIFICATION_CHANNEL_MESSAGES_V2)
                            notificationManager.deleteNotificationChannel(NotificationUtils.NOTIFICATION_CHANNEL_CALLS_V2)
                            appPreferences!!.setNotificationChannelIsUpgradedToV3(true)
                        }
                    }

                }
            } catch (e: PackageManager.NameNotFoundException) {
                Log.e(TAG, "Failed to fetch package info")
            }

        }
    }

    companion object {
        private val TAG = "PackageReplacedReceiver"
    }
}
