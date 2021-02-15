

package com.skripsi.berbincang.utils

import java.text.DateFormat
import java.util.*


object DateUtils {
    fun getLocalDateTimeStringFromTimestamp(timestamp: Long): String {
        val cal = Calendar.getInstance()
        val tz = cal.timeZone

        /* date formatter in local timezone */
        val format = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT, Locale
                .getDefault())
        format.timeZone = tz

        return format.format(Date(timestamp))
    }

    fun getLocalDateStringFromTimestampForLobby(timestamp: Long): String {
        return getLocalDateTimeStringFromTimestamp(timestamp * 1000);
    }
}
