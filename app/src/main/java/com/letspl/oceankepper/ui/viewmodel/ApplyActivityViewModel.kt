package com.letspl.oceankepper.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letspl.oceankepper.data.dto.GetApplicationDetailResponse
import com.letspl.oceankepper.data.dto.PatchApplyApplicationBody
import com.letspl.oceankepper.data.dto.PostApplyApplicationBody
import com.letspl.oceankepper.data.model.MainModel
import com.letspl.oceankepper.data.model.UserModel
import com.letspl.oceankepper.data.repository.ApplyActivityRepositoryImpl
import com.letspl.oceankepper.util.ParsingErrorMsg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ApplyActivityViewModel @Inject constructor(private val applyActivityRepositoryImpl: ApplyActivityRepositoryImpl) :
    ViewModel() {

    // 운동 수단 종류 선택
    private var _transportPosition = MutableLiveData<Int>(0)
    val transportPosition: LiveData<Int>
        get() = _transportPosition

    // 개인정보 동의 여부
    private var _privacyAgreement = MutableLiveData<Boolean>(false)
    val privacyAgreement: LiveData<Boolean>
        get() = _privacyAgreement

    // 지원 결과
    private var _applyResult = MutableLiveData<String>()
    val applyResult: LiveData<String> get() = _applyResult

    // 지원 수정 결과
    private var _editApplyResult = MutableLiveData<String>()
    val editApplyResult: LiveData<String> get() = _editApplyResult

    // 에러 토스트 메세지 text
    private var _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> get() = _errorMsg

    // 에러 토스트 메세지 text
    private var _getDetailApplication = MutableLiveData<GetApplicationDetailResponse>()
    val getDetailApplication: LiveData<GetApplicationDetailResponse> get() = _getDetailApplication

    // 활동 지원서 등록
    fun postApplyActivity(
        dayOfBirth: String,
        email: String,
        id1365: String,
        name: String,
        phoneNumber: String,
        question: String,
        startPoint: String
    ) {
        viewModelScope.launch {
            applyActivityRepositoryImpl.postRecruitmentApplication(
                "Bearer ${UserModel.userInfo.token.accessToken}", PostApplyApplicationBody(
                    MainModel.clickedActivityId,
                    dayOfBirth,
                    email,
                    id1365,
                    name,
                    phoneNumber,
                    isPrivacyAgreement(),
                    question,
                    startPoint,
                    getClickedTransport(),
                    UserModel.userInfo.user.id
                )
            ).let {
                if (it.isSuccessful) {
                    _applyResult.postValue(getRecruitCompleteText(it.body()?.timestamp!!))
                } else {
                    val errorJsonObject =
                        ParsingErrorMsg.parsingFromStringToJson(it.errorBody()?.string() ?: "")
                    if (errorJsonObject != null) {
                        val errorMsg = ParsingErrorMsg.parsingJsonObjectToErrorMsg(errorJsonObject)
                        _errorMsg.postValue(errorMsg)
                    }
                }
            }
        }
    }

    // 특정 활동 지원서 불러오기
    fun getDetailApplication(applicationId: String) {
        viewModelScope.launch {
            applyActivityRepositoryImpl.getDetailApplication(applicationId).let {
                if (it.isSuccessful) {
                    _getDetailApplication.postValue(it.body()?.response)
                } else {
                    val errorJsonObject =
                        ParsingErrorMsg.parsingFromStringToJson(it.errorBody()?.string() ?: "")
                    if (errorJsonObject != null) {
                        val errorMsg = ParsingErrorMsg.parsingJsonObjectToErrorMsg(errorJsonObject)
                        _errorMsg.postValue(errorMsg)
                    }
                }
            }
        }
    }

    // 활동 지원 수정하기
    fun patchApplication(
        applicationId: String,
        dayOfBirth: String,
        email: String,
        id1365: String,
        name: String,
        phoneNumber: String,
        question: String,
        startPoint: String,
    ) {
        viewModelScope.launch {
            applyActivityRepositoryImpl.patchApplication(
                applicationId, PatchApplyApplicationBody(
                    dayOfBirth.toLong(),
                    email,
                    id1365,
                    name,
                    privacyAgreement.value!!,
                    phoneNumber,
                    question,
                    startPoint,
                    getClickedTransport(),
                )
            ).let {
                if(it.isSuccessful) {
                    _editApplyResult.postValue("활동 지원 수정이 완료되었습니다.")
                } else {
                    val errorJsonObject =
                        ParsingErrorMsg.parsingFromStringToJson(it.errorBody()?.string() ?: "")
                    if (errorJsonObject != null) {
                        val errorMsg = ParsingErrorMsg.parsingJsonObjectToErrorMsg(errorJsonObject)
                        _errorMsg.postValue(errorMsg)
                    }
                }
            }
        }
    }

    // 대중교통 선택
    fun onClickedTransport(pos: Int) {
        _transportPosition.postValue(pos)
    }

    private fun getClickedTransport(): String {
        return when (transportPosition.value) {
            1 -> "도보"
            2 -> "대중교통"
            3 -> "자차 (카셰어링 불가능)"
            4 -> "자차 (카셰어링 가능)"
            else -> ""
        }
    }

    fun getClickedTransportStringToInt(transportation: String): Int {
        return when (transportation) {
            "도보" -> 1
            "대중교통" -> 2
            "자차 (카셰어링 불가능)" -> 3
            "자차 (카셰어링 가능)" -> 4
            else -> 0
        }
    }

    // 활동 모집 모달 띄울 텍스트 생성
    private fun getRecruitCompleteText(timestamp: String): String {
        val text = timestamp.split("T")[0]
        val year = text.substring(2, 4)
        val month = text.substring(5, 7)
        val date = text.substring(8, 10)
        return "${year}년 ${month}월 ${date}일 활동에 대한 신청 완료!\n최종 선정 여부는 쪽지로 안내됩니다."
    }

    // 개인정보 동의하기 클릭
    fun onClickedPrivacyAgreement() {
        _privacyAgreement.postValue(!privacyAgreement.value!!)
    }

    private fun isPrivacyAgreement(): Boolean {
        return privacyAgreement.value!!
    }

    // 연락처에 숫자만 포함되어 있는지 확인
    fun isPhoneNumberInt(number: String): Boolean {
        var checkResult = true

        for (i in number.indices) {
            if (!Character.isDigit(number.toCharArray()[i])) {
                // 숫자가 아닐경우를 체크함
                checkResult = false
            }
        }

        return checkResult
    }

    // 모든 필수 값이 입력되었는지 확인
    fun isInputNecessaryValue(name: String, phoneNumber: String, email: String): Boolean {
        return name != "" && phoneNumber != "" && email != "" && transportPosition.value != 0
    }

}