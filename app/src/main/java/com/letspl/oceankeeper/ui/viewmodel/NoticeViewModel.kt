package com.letspl.oceankeeper.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letspl.oceankeeper.data.dto.GetNoticeListDto
import com.letspl.oceankeeper.data.model.NoticeModel
import com.letspl.oceankeeper.data.model.UserModel
import com.letspl.oceankeeper.data.repository.NoticeRepositoryImpl
import com.letspl.oceankeeper.util.NetworkUtils
import com.letspl.oceankeeper.util.ParsingErrorMsg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoticeViewModel @Inject constructor(private val noticeRepositoryImpl: NoticeRepositoryImpl) :
    ViewModel() {

    // 공지사항 조회 결과
    private var _getNoticeResult = MutableLiveData<List<GetNoticeListDto>>()
    val getNoticeResult: LiveData<List<GetNoticeListDto>> get() = _getNoticeResult

    // 에러 토스트 메세지 text
    private var _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> get() = _errorMsg

    // 공지사항 조회
    fun getNotice(noticeId: Int?, size: Int) {
        if (NetworkUtils.isNetworkConnected()) {
            CoroutineScope(Dispatchers.IO).launch {
                if (!NoticeModel.isLast) {

                    runCatching {
                        noticeRepositoryImpl.getNotice(
                            "Bearer ${UserModel.userInfo.token.accessToken}", noticeId, size
                        )
                    }.fold(onSuccess = {
                        if (it.isSuccessful) {
                            val notices = it.body()?.response?.notices!!
                            _getNoticeResult.postValue(notices)

                            if(notices.isNotEmpty()) {
                                NoticeModel.isLast = it.body()?.response?.meta?.last!!
                                NoticeModel.lastNoticeId = notices[notices.size - 1].id
                            }
                        } else {
                            val errorJsonObject = ParsingErrorMsg.parsingFromStringToJson(
                                it.errorBody()?.string() ?: ""
                            )
                            if (errorJsonObject != null) {
                                val errorMsg =
                                    ParsingErrorMsg.parsingJsonObjectToErrorMsg(errorJsonObject)
                                _errorMsg.postValue(errorMsg)
                            }
                        }
                    }, onFailure = {
                        _errorMsg.postValue(it.message)
                    })
                }
            }
        } else {
            _errorMsg.postValue("not Connect Network")
        }
    }

    fun getLastNoticeId(): Int {
        return NoticeModel.lastNoticeId
    }

    fun setIsLast(flag: Boolean) {
        NoticeModel.isLast = flag
    }

    fun clearErrorMsg() {
        _errorMsg.postValue("")
    }
}