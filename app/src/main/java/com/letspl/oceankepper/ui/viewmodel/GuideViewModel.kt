package com.letspl.oceankepper.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letspl.oceankepper.data.dto.GetGuideListDto
import com.letspl.oceankepper.data.dto.GetNoticeListDto
import com.letspl.oceankepper.data.model.GuideModel
import com.letspl.oceankepper.data.model.NoticeModel
import com.letspl.oceankepper.data.model.UserModel
import com.letspl.oceankepper.data.repository.GuideRepositoryImpl
import com.letspl.oceankepper.data.repository.NoticeRepositoryImpl
import com.letspl.oceankepper.util.ParsingErrorMsg
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GuideViewModel @Inject constructor(private val guideRepositoryImpl: GuideRepositoryImpl): ViewModel() {

    // 공지사항 조회 결과
    private var _getGuideResult = MutableLiveData<List<GetGuideListDto>>()
    val getGuideResult: LiveData<List<GetGuideListDto>> get() = _getGuideResult

    // 에러 토스트 메세지 text
    private var _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> get() = _errorMsg

    // 이용 가이드 조회
    fun getGuide(noticeId: Int?, size: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            if(!NoticeModel.isLast) {
                guideRepositoryImpl.getGuide("Bearer ${UserModel.userInfo.token.accessToken}", noticeId, size).let {
                    if(it.isSuccessful) {
                        val guides = it.body()?.response?.guides!!
                        _getGuideResult.postValue(guides)

                        GuideModel.isLast = it.body()?.response?.meta?.last!!
                        GuideModel.lastNoticeId = guides[guides.size - 1].id
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

    // 마지막 guideId 불러오기
    fun getLastGuideId(): Int {
        return GuideModel.lastNoticeId
    }

}