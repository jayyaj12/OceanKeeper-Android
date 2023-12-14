package com.letspl.oceankepper.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letspl.oceankepper.data.dto.GetGuideListDto
import com.letspl.oceankepper.data.model.ManageApplyMemberModel
import com.letspl.oceankepper.data.repository.ManageApplyRepositoryImpl
import com.letspl.oceankepper.util.ParsingErrorMsg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageApplyViewModel @Inject constructor(private val manageApplyRepositoryImpl: ManageApplyRepositoryImpl): ViewModel(){

    // 공지사항 조회 결과
    private var _getCrewInfoList = MutableLiveData<List<ManageApplyMemberModel.GetCrewInfoListDto>>()
    val getCrewInfoList: LiveData<List<ManageApplyMemberModel.GetCrewInfoListDto>> get() = _getCrewInfoList

    // 에러 토스트 메세지 text
    private var _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> get() = _errorMsg

    // 신청자 리스트 불러오기
    fun getCrewInfoList(activityId: String) {
        viewModelScope.launch {
            manageApplyRepositoryImpl.getCrewInfoList(activityId).let {
                if(it.isSuccessful) {
                    it.body()?.response?.crewInfo?.let { crewInfoList ->
                        _getCrewInfoList.postValue(crewInfoList)
                    }
                } else {
                    val errorJsonObject =
                        ParsingErrorMsg.parsingFromStringToJson(it.errorBody()?.string() ?: "")
                    if (errorJsonObject != null) {
                        val errorMsg =
                            ParsingErrorMsg.parsingJsonObjectToErrorMsg(errorJsonObject)
                        _errorMsg.postValue(errorMsg)
                    }
                }
            }
        }
    }

}