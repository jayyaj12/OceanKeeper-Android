package com.letspl.oceankeeper.util

import android.content.res.Resources

object Extension {

    // dp를 px 로 변환
    fun Float.fromDpToPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()
}