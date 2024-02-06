package com.letspl.oceankeeper.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letspl.oceankeeper.data.dto.GetApplicationDetailResponse
import com.letspl.oceankeeper.data.dto.GetLastRecruitmentApplicationResponseDto
import com.letspl.oceankeeper.data.dto.PatchApplyApplicationBody
import com.letspl.oceankeeper.data.dto.PostApplyApplicationBody
import com.letspl.oceankeeper.data.model.ActivityRecruitModel
import com.letspl.oceankeeper.data.model.MainModel
import com.letspl.oceankeeper.data.model.UserModel
import com.letspl.oceankeeper.data.repository.ApplyActivityRepositoryImpl
import com.letspl.oceankeeper.util.NetworkUtils
import com.letspl.oceankeeper.util.ParsingErrorMsg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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


    // 개인정보 내용 불러오기
    private var _getPrivacyResult = MutableLiveData<String>()
    val getPrivacyResult: LiveData<String>
        get() = _getPrivacyResult

    // 지원 결과
    private var _applyResult = MutableLiveData<String>()
    val applyResult: LiveData<String> get() = _applyResult

    // 마지막 지원서 불러오기 결과
    private var _getLastRecruitmentApplicationResult =
        MutableLiveData<GetLastRecruitmentApplicationResponseDto?>()
    val getLastRecruitmentApplicationResult: LiveData<GetLastRecruitmentApplicationResponseDto?> get() = _getLastRecruitmentApplicationResult

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
        if (NetworkUtils.isNetworkConnected()) {
            CoroutineScope(Dispatchers.IO).launch {
                runCatching {
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
                    )
                }.fold(onSuccess = {
                    if (it.isSuccessful) {
                        _applyResult.postValue(getRecruitCompleteText(it.body()?.timestamp!!))
                    } else {
                        val errorJsonObject =
                            ParsingErrorMsg.parsingFromStringToJson(it.errorBody()?.string() ?: "")
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
        } else {
            _errorMsg.postValue("not Connect Network")
        }
    }

    // 마지막 지원서 불러오기
    fun getLastRecruitmentApplication() {
        if (NetworkUtils.isNetworkConnected()) {
            CoroutineScope(Dispatchers.IO).launch {
                runCatching {
                    applyActivityRepositoryImpl.getLastRecruitmentApplication()
                }.fold(onSuccess = {
                    if (it.isSuccessful) {
                        _getLastRecruitmentApplicationResult.postValue(it.body()?.response)
                    } else {
                        val errorJsonObject = ParsingErrorMsg.parsingFromStringToJson(
                            it.errorBody()?.string() ?: ""
                        )
                        if (errorJsonObject != null) {
                            val errorMsg =
                                ParsingErrorMsg.parsingJsonObjectToErrorMsg(errorJsonObject)
                            if (errorMsg == "해당 유저의 활동 지원서가 존재하지 않습니다.") {
                                _getLastRecruitmentApplicationResult.postValue(null)
                            } else {
                                _errorMsg.postValue(errorMsg)
                            }
                        }
                    }
                }, onFailure = {
                    _errorMsg.postValue(it.message)
                })
            }
        } else {
            _errorMsg.postValue("not Connect Network")
        }
    }

    // 특정 활동 지원서 불러오기
    fun getDetailApplication(applicationId: String) {
        if (NetworkUtils.isNetworkConnected()) {
            CoroutineScope(Dispatchers.IO).launch {
                runCatching {
                    applyActivityRepositoryImpl.getDetailApplication(applicationId)
                }.fold(onSuccess = {
                    if (it.isSuccessful) {
                        _getDetailApplication.postValue(it.body()?.response)
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
        } else {
            _errorMsg.postValue("not Connect Network")
        }
    }

    // 이용약관 가져오기
    fun getPrivacyPolicy() {
        if (NetworkUtils.isNetworkConnected()) {
            CoroutineScope(Dispatchers.IO).launch {
                runCatching {
                    applyActivityRepositoryImpl.getPrivacyPolicy()
                }.fold(
                    onSuccess = {
                        if(it.isSuccessful) {
                            _getPrivacyResult.postValue(it.body()?.response?.contents)
                        } else {
                            val errorJsonObject =
                                ParsingErrorMsg.parsingFromStringToJson(it.errorBody()?.string() ?: "")
                            if (errorJsonObject != null) {
                                val errorMsg = ParsingErrorMsg.parsingJsonObjectToErrorMsg(errorJsonObject)
                                _errorMsg.postValue(errorMsg)
                            }
                        }
                    },
                    onFailure = {
                        _errorMsg.postValue(it.message)
                    }
                )
            }
        } else {
            _errorMsg.postValue("not Connect Network")
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
        if (NetworkUtils.isNetworkConnected()) {
            CoroutineScope(Dispatchers.IO).launch {
                runCatching {
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
                    )
                }.fold(onSuccess = {
                    if (it.isSuccessful) {
                        _editApplyResult.postValue("활동 지원 수정이 완료되었습니다.")
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
        } else {
            _errorMsg.postValue("not Connect Network")
        }
    }

    // 지원한 활동에 대한 활동 시작일 가져오기
    fun getApplyActivityStartDate(): String {
        val date = ActivityRecruitModel.applyActivityStartDate.split("-")
        return "${date[0].substring(2,4)}년 ${date[1]}월 ${date[2].substring(0, 2)}일 활동에 대한 신청 완료!\n최종 선정 여부는 쪽지로 안내됩니다."
    }

    // 지원한 활동에 대한 활동 시작일 저장하기
    fun setApplyActivityStartDate(date: String) {
        ActivityRecruitModel.applyActivityStartDate = date
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

    fun clearErrorMsg() {
        _errorMsg.postValue("")
    }

}