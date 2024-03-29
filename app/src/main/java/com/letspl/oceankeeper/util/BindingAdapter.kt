package com.letspl.oceankeeper.util

import android.view.View
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import com.bumptech.glide.Glide
import com.letspl.oceankeeper.R
import timber.log.Timber

object BindingAdapter {
    @BindingAdapter("app:backgroundUrl")
    @JvmStatic
    fun setBackgroundUrl(imageView: ImageView, url: String?) {
        if(url != "" && url != null) {
            Glide.with(ContextUtil.context).load(url).into(imageView)
        }
    }
    @BindingAdapter("app:isBackgroundGone")
    @JvmStatic
    fun setBackgroundGone(cardView: CardView, url: String?) {
        if(url == "" || url == null) {
            cardView.visibility = View.GONE
        } else {
            cardView.visibility = View.VISIBLE
        }
    }

    // 신청자 관리 리스트 텍스트 세팅
    @BindingAdapter("app:setNumberImageBox", "app:setFlag")
    @JvmStatic
    fun setApplyListText(imageView: ImageView, allChecked: LiveData<Boolean>, flag: Boolean) {
        Timber.e("setApplyListText flag: $flag")
        if(flag) {
            imageView.setBackgroundResource(R.drawable.checkbox_checked)
        } else {
            imageView.setBackgroundResource(R.drawable.checkbox_default)
        }
    }
}