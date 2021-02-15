

package com.skripsi.berbincang.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.skripsi.berbincang.BuildConfig

import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

object LoggingUtils {
    fun writeLogEntryToFile(context: Context, logEntry: String) {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val date = Date()
        val logEntryWithDateTime = dateFormat.format(date) + ": " + logEntry + "\n"

        try {
            val outputStream = context.openFileOutput("nc_log.txt",
                    Context.MODE_PRIVATE or Context.MODE_APPEND)
            outputStream.write(logEntryWithDateTime.toByteArray())
            outputStream.flush()
            outputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun sendMailWithAttachment(context: Context) {
        val logFile = context.getFileStreamPath("nc_log.txt")
        val emailIntent = Intent(Intent.ACTION_SEND)
        val mailto = "mario@nextcloud.com"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(mailto))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Talk logs")
        emailIntent.type = "text/plain"
        emailIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        val uri: Uri

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uri = Uri.fromFile(logFile)
        } else {
            uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, logFile)
        }

        emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
        emailIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(emailIntent)
    }
}
