package com.letspl.oceankepper.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.user.model.User
import com.letspl.oceankepper.data.dto.GetActivityInfoResponseDto
import com.letspl.oceankepper.data.model.JoinModel
import com.letspl.oceankepper.data.model.MyActivityModel
import com.letspl.oceankepper.data.model.UserModel
import com.letspl.oceankepper.data.repository.ActivityRepositoryImpl
import com.letspl.oceankepper.util.ParsingErrorMsg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MyActivityViewModel @Inject constructor(private val activityViRepositoryImpl: ActivityRepositoryImpl): ViewModel() {

    // 에러 토스트 메세지 text
    private var _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> get() = _errorMsg

    // 에러 토스트 메세지 text
    private var _getActivityInfoResult = MutableLiveData<GetActivityInfoResponseDto>()
    val getActivityInfoResult: LiveData<GetActivityInfoResponseDto> get() = _getActivityInfoResult

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

    fun uploadEditProfileImage() {
        viewModelScope.launch(Dispatchers.IO) {
            activityViRepositoryImpl.uploadEditProfileImage(getProfileImageFile()!!).let {
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

    private fun getProfileImageFile(): File? {
        return MyActivityModel.profileImageFile
    }
    fun setProfileImageFile(file: File?) {
        MyActivityModel.profileImageFile = file
    } fun setTakePhotoUri(uri: Uri?) {
        MyActivityModel.takePhotoUri = uri
    }
    fun getTakePhotoUri(): Uri? {
        return MyActivityModel.takePhotoUri
    }

}