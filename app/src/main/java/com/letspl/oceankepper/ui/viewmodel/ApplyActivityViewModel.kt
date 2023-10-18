package com.letspl.oceankepper.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private var _transportPosition = MutableLiveData<Int>(0)
    val transportPosition: LiveData<Int>
        get() = _transportPosition
    private var _privacyAgreement = MutableLiveData<Boolean>(false)
    val privacyAgreement: LiveData<Boolean>
        get() = _privacyAgreement

    private var _applyResult = MutableLiveData<String>()
    val applyResult: LiveData<String> get() = _applyResult

    private var _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> get() = _errorMsg

    fun postApplyActivity(
        dayOfBirth: String,
        email: String,
        id1365: String,
        name: String,
        phoneNumber: String,
        privacyAgreement: Boolean,
        question: String,
        startPoint: String,
        transportation: String
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
                if(it.isSuccessful) {
                    _applyResult.postValue(getRecruitCompleteText(it.body()?.timestamp!!))
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

    // 대중교통 선택
    fun onClickedTransport(pos: Int) {
        _transportPosition.postValue(pos)
    }

    private fun getClickedTransport(): String {
        return when(transportPosition.value) {
            1 -> "도보"
            2 -> "대중교통"
            3 -> "자차 (카셰어링 불가능)"
            4 -> "자차 (카셰어링 가능)"
            else -> ""
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
        Timber.e("privacyAgreement.value ${privacyAgreement.value}")
        _privacyAgreement.postValue(!privacyAgreement.value!!)
    }

    private fun isPrivacyAgreement(): Boolean {
        return privacyAgreement.value!!
    }

    // 연락처에 숫자만 포함되어 있는지 확인
    fun isPhoneNumberInt(number: String): Boolean {
        var checkResult = true

        for(i in number.indices) {
            if(!Character.isDigit(number.toCharArray()[i])) {
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