

package com.skripsi.berbincang.utils

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.service.notification.StatusBarNotification
import com.skripsi.berbincang.R
import com.skripsi.berbincang.models.database.UserEntity
import com.skripsi.berbincang.utils.bundle.BundleKeys

object NotificationUtils {
    val NOTIFICATION_CHANNEL_CALLS = "NOTIFICATION_CHANNEL_CALLS"
    val NOTIFICATION_CHANNEL_MESSAGES = "NOTIFICATION_CHANNEL_MESSAGES"
    val NOTIFICATION_CHANNEL_CALLS_V2 = "NOTIFICATION_CHANNEL_CALLS_V2"
    val NOTIFICATION_CHANNEL_MESSAGES_V2 = "NOTIFICATION_CHANNEL_MESSAGES_V2"
    val NOTIFICATION_CHANNEL_MESSAGES_V3 = "NOTIFICATION_CHANNEL_MESSAGES_V3"
    val NOTIFICATION_CHANNEL_CALLS_V3 = "NOTIFICATION_CHANNEL_CALLS_V3"

    @TargetApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(context: Context,
                                  channelId: String, channelName: String,
                                  channelDescription: String, enableLights: Boolean,
                                  importance: Int) {

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O && notificationManager.getNotificationChannel(channelId) == null) {

            val channel = NotificationChannel(channelId, channelName,
                    importance)

            channel.description = channelDescription
            channel.enableLights(enableLights)
            channel.lightColor = R.color.colorPrimary
            channel.setSound(null, null)

            notificationManager.createNotificationChannel(channel)
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun createNotificationChannelGroup(context: Context,
                                       groupId: String, groupName: CharSequence) {
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notificationChannelGroup = NotificationChannelGroup(groupId, groupName)
            if (!notificationManager.notificationChannelGroups.contains(notificationChannelGroup)) {
                notificationManager.createNotificationChannelGroup(notificationChannelGroup)
            }
        }
    }

    fun cancelAllNotificationsForAccount(context: Context?, conversationUser: UserEntity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && conversationUser.id != -1L && context != null) {

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val statusBarNotifications = notificationManager.activeNotifications
            var notification: Notification?
            for (statusBarNotification in statusBarNotifications) {
                notification = statusBarNotification.notification

                if (notification != null && !notification.extras.isEmpty) {
                    if (conversationUser.id == notification.extras.getLong(BundleKeys.KEY_INTERNAL_USER_ID)) {
                        notificationManager.cancel(statusBarNotification.id)
                    }
                }
            }
        }

    }

    fun cancelExistingNotificationWithId(context: Context?, conversationUser: UserEntity, notificationId: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && conversationUser.id != -1L &&
                context != null) {

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val statusBarNotifications = notificationManager.activeNotifications
            var notification: Notification?
            for (statusBarNotification in statusBarNotifications) {
                notification = statusBarNotification.notification

                if (notification != null && !notification.extras.isEmpty) {
                    if (conversationUser.id == notification.extras.getLong(BundleKeys.KEY_INTERNAL_USER_ID) && notificationId == notification.extras.getLong(BundleKeys.KEY_NOTIFICATION_ID)) {
                        notificationManager.cancel(statusBarNotification.id)
                    }
                }
            }
        }
    }

    fun findNotificationForRoom(context: Context?,
                                conversationUser: UserEntity,
                                roomTokenOrId: String): StatusBarNotification? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && conversationUser.id != -1L &&
                context != null) {

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val statusBarNotifications = notificationManager.activeNotifications
            var notification: Notification?
            for (statusBarNotification in statusBarNotifications) {
                notification = statusBarNotification.notification

                if (notification != null && !notification.extras.isEmpty) {
                    if (conversationUser.id == notification.extras.getLong(BundleKeys.KEY_INTERNAL_USER_ID) && roomTokenOrId == statusBarNotification.notification.extras.getString(BundleKeys.KEY_ROOM_TOKEN)) {
                        return statusBarNotification
                    }
                }
            }
        }

        return null
    }

    fun cancelExistingNotificationsForRoom(context: Context?, conversationUser: UserEntity,
                                           roomTokenOrId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && conversationUser.id != -1L &&
                context != null) {

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val statusBarNotifications = notificationManager.activeNotifications
            var notification: Notification?
            for (statusBarNotification in statusBarNotifications) {
                notification = statusBarNotification.notification

                if (notification != null && !notification.extras.isEmpty) {
                    if (conversationUser.id == notification.extras.getLong(BundleKeys.KEY_INTERNAL_USER_ID) && roomTokenOrId == statusBarNotification.notification.extras.getString(BundleKeys.KEY_ROOM_TOKEN)) {
                        notificationManager.cancel(statusBarNotification.id)
                    }
                }
            }
        }
    }
}
