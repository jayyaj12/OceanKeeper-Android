package com.letspl.oceankeeper.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letspl.oceankeeper.data.dto.GetActivityInfoResponseDto
import com.letspl.oceankeeper.data.dto.GetUserActivityListDto
import com.letspl.oceankeeper.data.dto.PutNicknameBody
import com.letspl.oceankeeper.data.model.MyActivityModel
import com.letspl.oceankeeper.data.model.UserModel
import com.letspl.oceankeeper.data.repository.ActivityRepositoryImpl
import com.letspl.oceankeeper.data.repository.UserRepositoryImpl
import com.letspl.oceankeeper.util.NetworkUtils
import com.letspl.oceankeeper.util.ParsingErrorMsg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MyActivityViewModel @Inject constructor(
    private val activityViRepositoryImpl: ActivityRepositoryImpl,
    private val userRepositoryImpl: UserRepositoryImpl,
) : ViewModel() {

    // 에러 토스트 메세지 text
    private var _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> get() = _errorMsg

    // 에러 토스트 메세지 text
    private var _getActivityInfoResult = MutableLiveData<GetActivityInfoResponseDto>()
    val getActivityInfoResult: LiveData<GetActivityInfoResponseDto> get() = _getActivityInfoResult

    // 내활동보기 결과 (크루)
    private var _getUserActivityCrew = MutableLiveData<List<GetUserActivityListDto>?>()
    val getUserActivityCrew: LiveData<List<GetUserActivityListDto>?> get() = _getUserActivityCrew

    // 내활동보기 결과 (호스트)
    private var _getUserActivityHost = MutableLiveData<List<GetUserActivityListDto>?>()
    val getUserActivityHost: LiveData<List<GetUserActivityListDto>?> get() = _getUserActivityHost

    // 활동 지원 취소
    private var _deleteApplyCancel = MutableLiveData<Boolean>()
    val deleteApplyCancel: LiveData<Boolean> get() = _deleteApplyCancel

    // 닉네임 변경
    private var _changeNicknameResult = MutableLiveData<String>()
    val changeNicknameResult: LiveData<String> get() = _changeNicknameResult

    // 프로필 이미지 변경
    private var _changeProfileImageResult = MutableLiveData<Boolean>()
    val changeProfileImageResult: LiveData<Boolean> get() = _changeProfileImageResult

    // 모집 취소
    private var _deleteRecruitCancel = MutableLiveData<Boolean>()
    val deleteRecruitCancel: LiveData<Boolean> get() = _deleteRecruitCancel

    // 닉네임 중복 확인
    fun getDuplicateNickname(nickname: String) {
        if (NetworkUtils.isNetworkConnected()) {
            CoroutineScope(Dispatchers.IO).launch {
                runCatching {
                    userRepositoryImpl.getDuplicateNickname(nickname)
                }.fold(onSuccess = {
                    if (it.isSuccessful) {
                        putNickname(nickname)
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

    // 닉네임 수정
    private fun putNickname(nickname: String) {
        if (NetworkUtils.isNetworkConnected()) {
            CoroutineScope(Dispatchers.IO).launch {
                runCatching {
                    userRepositoryImpl.putNickname(
                        PutNicknameBody(nickname, UserModel.userInfo.user.id)
                    )
                }.fold(onSuccess = {
                    if (it.isSuccessful) {
                        _changeNicknameResult.postValue(nickname)
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

    // 나의 오션키퍼 활동정보 조회
    fun getMyActivityInfo() {
        if (NetworkUtils.isNetworkConnected()) {
            CoroutineScope(Dispatchers.IO).launch {
                runCatching {
                    activityViRepositoryImpl.getActivityInfo(UserModel.userInfo.user.id)
                }.fold(onSuccess = {
                    if (it.isSuccessful) {
                        _getActivityInfoResult.postValue(
                            GetActivityInfoResponseDto(
                                it.body()?.response?.activity ?: 0,
                                it.body()?.response?.hosting ?: 0,
                                it.body()?.response?.noShow ?: 0,
                            )
                        )
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

    // 수정 프로필 이미지 업로드
    fun uploadEditProfileImage(file: File) {
        if (NetworkUtils.isNetworkConnected()) {
            CoroutineScope(Dispatchers.IO).launch {
                runCatching {
                    activityViRepositoryImpl.uploadEditProfileImage(file)
                }.fold(onSuccess = {
                    if (it.isSuccessful) {
                        _changeProfileImageResult.postValue(true)
                        UserModel.userInfo.user.profile = it.body()?.url!!
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

    // 내활동 보기
    fun getUserActivity(role: String, activityId: String?) {
        if (NetworkUtils.isNetworkConnected()) {
            CoroutineScope(Dispatchers.IO).launch {
                if (!checkIsLast(role)) {
                    runCatching {
                        activityViRepositoryImpl.getUserActivity(
                            activityId, 10, role, UserModel.userInfo.user.id
                        )
                    }.fold(onSuccess = {
                        if (it.isSuccessful) {
                            val activities = it.body()?.response?.activities!!

                            if (role == "crew") {
                                MyActivityModel.crewActivities.addAll(activities)

                                _getUserActivityCrew.postValue(MyActivityModel.crewActivities)

                                if (activities.isNotEmpty()) {
                                    MyActivityModel.crewLast = it.body()?.response?.meta?.last!!
                                    MyActivityModel.lastCrewActivityId =
                                        activities[activities.size - 1].activityId
                                }
                            } else {
                                MyActivityModel.hostActivities.addAll(activities)

                                _getUserActivityHost.postValue(MyActivityModel.hostActivities)

                                if (activities.isNotEmpty()) {
                                    MyActivityModel.hostLast = it.body()?.response?.meta?.last!!
                                    MyActivityModel.lastHostActivityId =
                                        activities[activities.size - 1].activityId
                                }
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

    // 활동 지원 취소
    fun deleteApplyCancel(applicationId: String) {
        if (NetworkUtils.isNetworkConnected()) {
            CoroutineScope(Dispatchers.IO).launch {
                runCatching {
                    activityViRepositoryImpl.deleteApplyCancel(applicationId)
                }.fold(
                    onSuccess = {
                        if (it.isSuccessful) {
                            _deleteApplyCancel.postValue(true)
                        } else {
                            _deleteApplyCancel.postValue(false)
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

    // 모집 취소
    fun deleteRecruitmentCancel(activityId: String) {
        if (NetworkUtils.isNetworkConnected()) {
            CoroutineScope(Dispatchers.IO).launch {
                runCatching {
                    activityViRepositoryImpl.deleteRecruitmentCancel(activityId)
                }.fold(
                    onSuccess = {
                        if (it.isSuccessful) {
                            _deleteRecruitCancel.postValue(true)
                        } else {
                            _deleteRecruitCancel.postValue(false)
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

    // 각 역활 별 last 가 true 인지 체크
    private fun checkIsLast(role: String): Boolean {
        return when (role) {
            "crew" -> {
                MyActivityModel.crewLast
            }

            "host" -> {
                MyActivityModel.hostLast
            }

            else -> false
        }
    }

    fun getLastCrewActivityId(): String? {
        return MyActivityModel.lastCrewActivityId
    }

    fun getLastHostActivityId(): String? {
        return MyActivityModel.lastHostActivityId
    }

    fun setCrewLast(flag: Boolean) {
        MyActivityModel.crewLast = flag
        MyActivityModel.crewActivities.clear()
    }

    fun setHostLast(flag: Boolean) {
        MyActivityModel.hostLast = flag
        MyActivityModel.hostActivities.clear()
    }

    fun getUserProfile(): String {
        return UserModel.userInfo.user.profile
    }

    //유저 닉네임 불러오기
    fun getUserNickname(): String {
        return UserModel.userInfo.user.nickname
    }

    // 프로필 이미지 파일 불러오기
    private fun getProfileImageFile(): File? {
        return MyActivityModel.profileImageFile
    }

    // 프로필 이미지 파일 세팅하기
    fun setProfileImageFile(file: File?) {
        MyActivityModel.profileImageFile = file
    }

    // 사진 촬영 이미지 세팅하기
    fun setTakePhotoUri(uri: Uri?) {
        MyActivityModel.takePhotoUri = uri
    }

    // 사진 촬영 이미지 불러오기
    fun getTakePhotoUri(): Uri? {
        return MyActivityModel.takePhotoUri
    }

    // 신청자 관리 페이지 변경 시 클릭한 활동 정보 저장
    fun setClickItem(item: GetUserActivityListDto) {
        MyActivityModel.clickItem = item
    }

    // 신청자 관리 페이지 변경 시 클릭한 활동 정보 가져오기
    fun getClickItem(): GetUserActivityListDto {
        return MyActivityModel.clickItem
    }

    fun clearLivedata() {
        setCrewLast(false)
        setHostLast(false)
        _getUserActivityCrew.postValue(null)
        _getUserActivityHost.postValue(null)
        _changeProfileImageResult.postValue(false)
    }

    fun clearErrorMsg() {
        _errorMsg.postValue("")
    }
}