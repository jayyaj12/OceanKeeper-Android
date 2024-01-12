package com.letspl.oceankeeper.util

import org.json.JSONObject
import timber.log.Timber

object ParsingErrorMsg {
    fun parsingFromStringToJson(errorMsg:String): JSONObject? {
        return if(errorMsg == "") {
            null
        } else {
            Timber.e("errorMsg $errorMsg")
            JSONObject(errorMsg)
        }
    }

    fun parsingJsonObjectToErrorMsg(jsonObject: JSONObject): String {
        return jsonObject.getJSONObject("response").getString("errorDetail")
    }
}