

package com.skripsi.berbincang.utils

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.Vibrator
import com.skripsi.berbincang.application.NextcloudTalkApplication

object DoNotDisturbUtils {
    fun shouldPlaySound(): Boolean {
        val context = NextcloudTalkApplication.sharedApplication?.applicationContext

        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        var shouldPlaySound = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (notificationManager.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL) {
                shouldPlaySound = false
            }
        }

        if (shouldPlaySound) {
            if (audioManager.ringerMode != AudioManager.RINGER_MODE_NORMAL) {
                shouldPlaySound = false
            }
        }

        return shouldPlaySound
    }

    fun hasVibrator(): Boolean {
        val context = NextcloudTalkApplication.sharedApplication?.applicationContext
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        return vibrator.hasVibrator()
    }

    fun shouldVibrate(vibrate: Boolean): Boolean {

        if (hasVibrator()) {
            val context = NextcloudTalkApplication.sharedApplication?.applicationContext
            val audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager

            return if (vibrate) {
                audioManager.ringerMode != AudioManager.RINGER_MODE_SILENT
            } else {
                audioManager.ringerMode == AudioManager.RINGER_MODE_VIBRATE
            }
        }

        return false
    }
}
