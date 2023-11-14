package com.letspl.oceankepper.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.model.User
import com.letspl.oceankepper.data.dto.GetActivityInfoResponseDto
import com.letspl.oceankepper.data.dto.GetUserActivityListDto
import com.letspl.oceankepper.data.model.JoinModel
import com.letspl.oceankepper.data.model.MyActivityModel
import com.letspl.oceankepper.data.model.UserModel
import com.letspl.oceankepper.data.repository.ActivityRepositoryImpl
import com.letspl.oceankepper.util.ParsingErrorMsg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MyActivityViewModel @Inject constructor(private val activityViRepositoryImpl: ActivityRepositoryImpl): ViewModel() {

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

    // 모집 취소
    private var _deleteRecruitCancel = MutableLiveData<Boolean>()
    val deleteRecruitCancel: LiveData<Boolean> get() = _deleteRecruitCancel



    // 나의 오션키퍼 활동정보 조회
    fun getMyActivityInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            activityViRepositoryImpl.getActivityInfo(UserModel.userInfo.user.id).let {
                if(it.isSuccessful) {
                    _getActivityInfoResult.postValue(
                        GetActivityInfoResponseDto(
                            it.body()?.response?.activity ?: 0,
                            it.body()?.response?.hosting ?: 0,
                            it.body()?.response?.noShow ?: 0,
                        )
                    )
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

    // 수정 프로필 이미지 업로드
    fun uploadEditProfileImage(file: File) {
        CoroutineScope(Dispatchers.IO).launch {
            activityViRepositoryImpl.uploadEditProfileImage(file).let {
                if(it.isSuccessful) {
                    UserModel.userInfo.user.profile = it.body()?.url!!
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

    // 내활동 보기
    fun getUserActivity(role: String) {
        CoroutineScope(Dispatchers.IO).launch() {
            activityViRepositoryImpl.getUserActivity(null, 10, role, UserModel.userInfo.user.id).let {
                if(it.isSuccessful) {
                    if(role == "crew") {
                        _getUserActivityCrew.postValue(it.body()?.response?.activities)
                    } else {
                        _getUserActivityHost.postValue(it.body()?.response?.activities)
                    }
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

    // 활동 지원 취소
    fun deleteApplyCancel(applicationId: String) {
        viewModelScope.launch {
            activityViRepositoryImpl.deleteApplyCancel(applicationId).let {
                if(it.isSuccessful) {
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
            }
        }
    }

    // 모집 취소
    fun deleteRecruitmentCancel(activityId: String) {
        viewModelScope.launch {
            activityViRepositoryImpl.deleteRecruitmentCancel(activityId).let {
                if(it.isSuccessful) {
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
            }
        }
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

    fun clearLivedata() {
        _getUserActivityCrew.postValue(null)
        _getUserActivityHost.postValue(null)
    }
}