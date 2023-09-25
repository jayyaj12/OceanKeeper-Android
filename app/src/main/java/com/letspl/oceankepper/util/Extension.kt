package com.letspl.oceankepper.util

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import android.widget.ScrollView
import com.bumptech.glide.load.engine.Resource
import java.lang.Math.abs

object Extension {

    // dp를 px 로 변환
    fun Float.fromDpToPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()
}