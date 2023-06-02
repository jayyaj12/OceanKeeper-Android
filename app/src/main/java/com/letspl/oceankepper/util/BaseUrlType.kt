package com.letspl.oceankepper.util

import com.letspl.oceankepper.BuildConfig
import timber.log.Timber

class BaseUrlType {
    companion object {
        // naver: 네이버
        // kakao: 카카오
        // google: 구글
        // ocean: 오션키퍼
        var type = ""
        var baseUrlStr = BuildConfig.NAVER_BASE_URL

        fun setBaseUrlType(type: String) {
            baseUrlStr = when(type) {
                "naver" -> BuildConfig.NAVER_BASE_URL
                "ocean" -> BuildConfig.SERVER_BASE_URL
                else -> BuildConfig.NAVER_BASE_URL
            }
        }

        fun getBaseUrl(): String {
            Timber.e("asdads $baseUrlStr")
            return baseUrlStr
        }
    }
}