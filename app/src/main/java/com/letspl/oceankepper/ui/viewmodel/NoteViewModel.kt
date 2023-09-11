package com.letspl.oceankepper.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(): ViewModel() {

    fun getGarbageCategory(type: String): String {
        return when(type) {
            "COASTAL" -> "연안쓰레기"
            "FLOATING" -> "부유쓰레기"
            "DEPOSITED" -> "침적쓰레기"
            "ETC" -> "기타"
            else -> ""
        }
    }

    fun convertIso8601ToCustomFormat(iso8601String: String): String {
        try {
            val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            isoFormatter.timeZone = TimeZone.getTimeZone("UTC")
            val date = isoFormatter.parse(iso8601String)

            val customFormatter = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US)
            customFormatter.timeZone = TimeZone.getDefault()

            return customFormatter.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
}