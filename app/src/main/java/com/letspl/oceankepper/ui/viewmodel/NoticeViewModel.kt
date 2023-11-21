package com.letspl.oceankepper.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letspl.oceankepper.data.dto.GetNoticeListDto
import com.letspl.oceankepper.data.model.NoticeModel
import com.letspl.oceankepper.data.model.UserModel
import com.letspl.oceankepper.data.repository.NoticeRepositoryImpl
import com.letspl.oceankepper.util.ParsingErrorMsg
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoticeViewModel @Inject constructor(private val noticeRepositoryImpl: NoticeRepositoryImpl): ViewModel() {

    // 공지사항 조회 결과
    private var _getNoticeResult = MutableLiveData<List<GetNoticeListDto>>()
    val getNoticeResult: LiveData<List<GetNoticeListDto>> get() = _getNoticeResult

    // 에러 토스트 메세지 text
    private var _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> get() = _errorMsg

    // 공지사항 조회
    fun getNotice(noticeId: Int?, size: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if(!NoticeModel.isLast) {
                noticeRepositoryImpl.getNotice("Bearer ${UserModel.userInfo.token.accessToken}", noticeId, size).let {
                    if(it.isSuccessful) {
                        val notices = it.body()?.response?.notices!!
                        _getNoticeResult.postValue(notices)

                        NoticeModel.isLast = it.body()?.response?.meta?.last!!
                        NoticeModel.lastNoticeId = notices[notices.size - 1].id
                    } else {
                        val errorJsonObject = ParsingErrorMsg.parsingFromStringToJson(it.errorBody()?.string() ?: "")
                        if(errorJsonObject != null) {
                            val errorMsg = ParsingErrorMsg.parsingJsonObjectToErrorMsg(errorJsonObject)
                            _errorMsg.postValue(errorMsg)
                        }
                    }
                }
            }
        }
    }

    fun getLastNoticeId(): Int {
        return NoticeModel.lastNoticeId
    }

    fun setIsLast(flag: Boolean) {
        NoticeModel.isLast = flag
    }
}