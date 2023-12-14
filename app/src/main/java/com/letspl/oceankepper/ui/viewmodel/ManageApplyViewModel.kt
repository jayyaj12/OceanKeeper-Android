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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ManageApplyViewModel @Inject constructor(private val manageApplyRepositoryImpl: ManageApplyRepositoryImpl): ViewModel(){

    // 공지사항 조회 결과
    private var _getCrewInfoList = MutableLiveData<List<ManageApplyMemberModel.CrewInfoDto>>()
    val getCrewInfoList: LiveData<List<ManageApplyMemberModel.CrewInfoDto>> get() = _getCrewInfoList

    // 전체 선택하기 이벤트 liveData
    private var _allClicked = MutableLiveData<Boolean>(false)
    val allClicked: LiveData<Boolean> get() = _allClicked

    // 에러 토스트 메세지 text
    private var _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> get() = _errorMsg

    // 신청자 리스트 불러오기
    fun getCrewInfoList(activityId: String) {
        viewModelScope.launch {
            manageApplyRepositoryImpl.getCrewInfoList(activityId).let {
                if(it.isSuccessful) {
                    it.body()?.response?.crewInfo?.let { crewInfoList ->
                        withContext(Dispatchers.Default) {
                            crewInfoList.forEachIndexed { index, getCrewInfoListDto ->
                                ManageApplyMemberModel.applyCrewList.add(
                                    ManageApplyMemberModel.CrewInfoDto(
                                        crewInfoList[index].crewStatus,
                                        crewInfoList[index].nickname,
                                        crewInfoList[index].number,
                                        crewInfoList[index].username,
                                        false
                                    )
                                )
                            }
                        }

                        _getCrewInfoList.postValue(ManageApplyMemberModel.applyCrewList)
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

    // 전체 선택하기 버튼
    fun setAllIsClickedApplyMember(flag: Boolean) {
        Timber.e("flag $flag")
        ManageApplyMemberModel.tempApplyCrewList.clear()

        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                ManageApplyMemberModel.applyCrewList.forEach {
                    ManageApplyMemberModel.tempApplyCrewList.add(it.copy())
                }

                ManageApplyMemberModel.tempApplyCrewList.forEachIndexed { index, crewInfoDto ->
                    ManageApplyMemberModel.tempApplyCrewList[index].isClicked = !flag
                    ManageApplyMemberModel.applyCrewList[index].isClicked = !flag
                }
            }

            withContext(Dispatchers.Default) {
                setAllChecked(!flag)
                _allClicked.postValue(!flag)
            }
        }
    }

    // 저장된 crewlist 불러오기
    fun getTempApplyCrewList(): List<ManageApplyMemberModel.CrewInfoDto>{
        return ManageApplyMemberModel.tempApplyCrewList
    }

    fun setAllChecked(flag: Boolean) {
        ManageApplyMemberModel.isAllChecked = flag
    }

    fun getIsAllChecked(): Boolean {
        return ManageApplyMemberModel.isAllChecked
    }
}