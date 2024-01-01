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
import okhttp3.internal.notify
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ManageApplyViewModel @Inject constructor(private val manageApplyRepositoryImpl: ManageApplyRepositoryImpl) :
    ViewModel() {

    // 크루원 리스트 불러오기
    private var _getCrewInfoList = MutableLiveData<List<ManageApplyMemberModel.CrewInfoDto>>()
    val getCrewInfoList: LiveData<List<ManageApplyMemberModel.CrewInfoDto>> get() = _getCrewInfoList

    // 크루원 정보 불러오기
    private var _getCrewDetail = MutableLiveData<ManageApplyMemberModel.GetCrewDetailResponse>()
    val getCrewDetail: LiveData<ManageApplyMemberModel.GetCrewDetailResponse> get() = _getCrewDetail

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
                if (it.isSuccessful) {
                    it.body()?.response?.crewInfo?.let { crewInfoList ->
                        withContext(Dispatchers.Default) {
                            crewInfoList.forEachIndexed { index, getCrewInfoListDto ->
                                ManageApplyMemberModel.applyCrewList.add(
                                    ManageApplyMemberModel.CrewInfoDto(
                                        crewInfoList[index].applicationId,
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

    // 크루원 정보 불러오기
    fun getCrewDetail(applicationId: String) {
        viewModelScope.launch {
            manageApplyRepositoryImpl.getCrewDetail(applicationId).let {
                if (it.isSuccessful) {
                    it.body()?.response?.let { data ->
                        _getCrewDetail.postValue(
                            ManageApplyMemberModel.GetCrewDetailResponse(
                                ManageApplyMemberModel.GetCrewDetailActivityInfoDto(
                                    data.activityInfo.activity,
                                    data.activityInfo.hosting,
                                    data.activityInfo.noShow
                                ),
                                ManageApplyMemberModel.GetCrewDetailApplicationDto(
                                    data.application.dayOfBirth,
                                    data.application.email,
                                    data.application.id1365,
                                    data.application.name,
                                    data.application.phoneNumber,
                                    data.application.question,
                                    data.application.startPoint,
                                    data.application.transportation
                                ),
                                ManageApplyMemberModel.GetCrewDetailUserInfoDto(
                                    data.userInfo.nickname, data.userInfo.profile
                                )
                            )
                        )
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
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                ManageApplyMemberModel.applyCrewList.forEach {
                    ManageApplyMemberModel.tempApplyCrewList.add(it.copy())
                }

                ManageApplyMemberModel.tempApplyCrewList.forEachIndexed { index, crewInfoDto ->
                    if(crewInfoDto.crewStatus != "REJECT") {
                        ManageApplyMemberModel.tempApplyCrewList[index].isClicked = !flag
                    }
                }
            }

            withContext(Dispatchers.Default) {
                setAllChecked(!flag)
                _allClicked.postValue(!flag)
            }
        }
    }

    // 크루원이 선택된 상태인지 체크
    fun isClickedEmpty(): Boolean {
        return getClickedCrewList().isEmpty()
    }

    // 클릭된 유저 불러오기
    fun getClickedCrewList(): List<ManageApplyMemberModel.CrewInfoDto> {
        val tempCrewArr = arrayListOf<ManageApplyMemberModel.CrewInfoDto>()

        ManageApplyMemberModel.applyCrewList.forEach {
            if(it.isClicked) {
                tempCrewArr.add(it)
            }
        }

        return tempCrewArr
    }

    // 저장된 crewlist 불러오기
    fun getTempApplyCrewList(): List<ManageApplyMemberModel.CrewInfoDto> {
        return ManageApplyMemberModel.tempApplyCrewList
    }

    fun setAllChecked(flag: Boolean) {
        ManageApplyMemberModel.isAllChecked = flag
    }

    fun getIsAllChecked(): Boolean {
        return ManageApplyMemberModel.isAllChecked
    }
}