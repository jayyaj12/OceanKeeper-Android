package com.letspl.oceankepper.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.letspl.oceankepper.data.model.JoinModel
import com.letspl.oceankepper.data.model.LoginModel
import com.letspl.oceankepper.data.model.UserModel
import com.letspl.oceankepper.data.repository.JoinRepositoryImpl
import com.letspl.oceankepper.di.AppApplication
import com.letspl.oceankepper.util.ParsingErrorMsg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.io.*
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class JoinViewModel @Inject constructor(private val joinRepositoryImpl: JoinRepositoryImpl) :
    ViewModel() {

    // 서버로 받은 프로필 url 을 file 로 생성이 완료 되었는지 구분하는 변수
    private var _profileTempFileCreated = MutableLiveData<File>()
    val profileTempFileCreated: LiveData<File>
        get() = _profileTempFileCreated

    private var _signUpResult = MutableLiveData<Boolean>()
    val signUpResult: LiveData<Boolean>
        get() = _signUpResult

    // 서버로 내려받은 url 을 파일로 생성
    fun createProfileImageFile() {
        viewModelScope.launch {
            val tempFile = withContext(Dispatchers.IO) {
                val inputStream = URL(LoginModel.login.profile).openStream()
                val tempFile = File.createTempFile(inputStream.hashCode().toString(), ".jpg")

                inputStreamToFile(inputStream, tempFile)

                tempFile
            }

            _profileTempFileCreated.postValue(tempFile)
        }
    }

    fun onClickedSignup(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                uploadImageFile(getProfileImageFile())
            }
        }
    }

    // inputStream 을 file로 변환
    private fun inputStreamToFile(inputStream: InputStream, file: File) {
        try {
            val outputStream = FileOutputStream(file)
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
            outputStream.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // 프로필 이미지 업로드
    fun uploadImageFile(file: File?) {
        viewModelScope.launch(Dispatchers.IO) {
            file?.let {
                joinRepositoryImpl.uploadProfileImage(file).let {
                    if (it.isSuccessful) {
                        it.body()?.let { body ->
                            // 회원가입 진행
                            signUpUser(body.url)
                        }
                    }
                }
            }
        }
    }

    // 회원가입
    private fun signUpUser(profileUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            LoginModel.login.run {
                joinRepositoryImpl.signUpUser(
                    this.deviceToken,
                    this.provider,
                    this.providerId,
                    this.nickname,
                    this.email,
                    profileUrl
                ).let {
                    Timber.e("response: $it")
                    if (it.isSuccessful) {
                        it.body()?.let { body ->
                            _signUpResult.postValue(true)
                            // 회원가입 진행
                            UserModel.userInfo.user.id = body.response.id
                            UserModel.userInfo.user.nickname = body.response.nickname
                        }
                    } else {
                        _signUpResult.postValue(false)
                        val errorJson = ParsingErrorMsg.parsingFromStringToJson(it.errorBody()?.string() ?: "")
                        Timber.e("asdad ${errorJson?.get("status")}")
                        Timber.e("asdad ${errorJson?.get("message")}")
                    }
                }
            }
        }
    }

    fun setTakePhotoUri(uri: Uri?) {
        Timber.e("setTakePhotoUri $uri")
        JoinModel.takePhotoUri = uri
    }
    fun getTakePhotoUri(): Uri? {
        return JoinModel.takePhotoUri
    }
    fun setProfileImageFile(file: File?) {
        JoinModel.profileImageFile = file
    }
    fun getProfileImageFile(): File? {
        return JoinModel.profileImageFile
    }
}