package com.letspl.oceankepper.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letspl.oceankepper.data.dto.MessageItemDto
import com.letspl.oceankepper.data.dto.MessageResponseDto
import com.letspl.oceankepper.data.model.MessageModel
import com.letspl.oceankepper.data.model.UserModel
import com.letspl.oceankepper.data.repository.MessageRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val messageRepositoryImpl: MessageRepositoryImpl
): ViewModel() {

    private var _getMessageResult = MutableLiveData<List<MessageItemDto>?>()
    val getMessageResult: LiveData<List<MessageItemDto>?>
        get() = _getMessageResult

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
            val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.US)
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

    fun getMessage() {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.e("isMessageLast() ${!isMessageLast()}")
            if(!isMessageLast()) {
                messageRepositoryImpl.getMessage(
                    "Bearer ${UserModel.userInfo.token.accessToken}",
                    MessageModel.messageId,
                    5,
                    "ALL",
                    UserModel.userInfo.user.id
                ).let {
                    if (it.isSuccessful) {
                        it.body()?.let { item ->
                            MessageModel.messageList.addAll(item.response.messages)
                            _getMessageResult.postValue(MessageModel.messageList)
                            MessageModel.messageId =
                                item.response.messages[item.response.messages.size - 1].id
                            MessageModel.isLast = item.response.meta.last
                        }
                    } else {
                        // 실패 시 토스트 표시
                        _getMessageResult.postValue(null)
                    }
                }
            }
        }
    }

    private fun isMessageLast() = MessageModel.isLast
}