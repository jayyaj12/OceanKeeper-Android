package com.letspl.oceankepper.util

import org.json.JSONObject
import timber.log.Timber

object ParsingErrorMsg {
    fun parsingFromStringToJson(errorMsg:String): JSONObject? {
        return if(errorMsg == "") {
            null
        } else {
            JSONObject(errorMsg)
        }
    }
}