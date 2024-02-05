package com.letspl.oceankeeper.ui.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.letspl.oceankeeper.R
import com.letspl.oceankeeper.data.model.JoinModel
import com.letspl.oceankeeper.data.model.LoginModel
import com.letspl.oceankeeper.data.model.UserModel
import com.letspl.oceankeeper.data.repository.JoinRepositoryImpl
import com.letspl.oceankeeper.util.ContextUtil
import com.letspl.oceankeeper.util.NetworkUtils
import com.letspl.oceankeeper.util.ParsingErrorMsg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.*
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class JoinViewModel @Inject constructor(private val joinRepositoryImpl: JoinRepositoryImpl) :
    ViewModel() {

    // 서버로 받은 프로필 url 을 file 로 생성이 완료 되었는지 구분하는 변수
    private var _profileTempFileCreated = MutableLiveData<File>()

    // 프로필 임시 이미지 생성 완료 여부
    val profileTempFileCreated: LiveData<File>
        get() = _profileTempFileCreated

    // 회원가입 결과
    private var _signUpResult = MutableLiveData<Boolean>()
    val signUpResult: LiveData<Boolean>
        get() = _signUpResult

    // 에러 토스트 메세지 text
    private var _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> get() = _errorMsg

    // 서버로 내려받은 url 을 파일로 생성
    fun createProfileImageFile() {
        CoroutineScope(Dispatchers.IO).launch {
            val tempFile = withContext(Dispatchers.IO) {
                val inputStream = URL(LoginModel.login.profile).openStream()
                val tempFile = File.createTempFile(inputStream.hashCode().toString(), ".jpg")

                inputStreamToFile(inputStream, tempFile)

                tempFile
            }

            _profileTempFileCreated.postValue(tempFile)
        }
    }

    fun onClickedSignup() {
        CoroutineScope(Dispatchers.IO).launch {
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
    private fun uploadImageFile(file: File?) {
        if (NetworkUtils.isNetworkConnected()) {
            CoroutineScope(Dispatchers.IO).launch {
                if(file != null) {
                    runCatching {
                        joinRepositoryImpl.uploadProfileImage(file)
                    }.fold(
                        onSuccess = {
                            if (it.isSuccessful) {
                                it.body()?.let { body ->
                                    // 회원가입 진행
                                    signUpUser(body.url)
                                }
                            } else {
                                val errorJsonObject =
                                    ParsingErrorMsg.parsingFromStringToJson(it.errorBody()?.string() ?: "")
                                errorJsonObject?.let {
                                    val errorMsg =
                                        ParsingErrorMsg.parsingJsonObjectToErrorMsg(errorJsonObject)
                                    _errorMsg.postValue(errorMsg)
                                }
                            }
                        },
                        onFailure = {
                            _errorMsg.postValue(it.message)
                        }
                    )
                }
            }
        } else {
            _errorMsg.postValue("not Connect Network")
        }
    }

    // 회원가입
    private fun signUpUser(profileUrl: String) {
        if (NetworkUtils.isNetworkConnected()) {
            CoroutineScope(Dispatchers.IO).launch {
                runCatching {
                    LoginModel.login.run {
                        joinRepositoryImpl.signUpUser(
                            this.deviceToken,
                            this.provider,
                            this.providerId,
                            this.nickname,
                            this.email,
                            profileUrl
                        )
                    }
                }.fold(
                    onSuccess = {
                        if (it.isSuccessful) {
                            it.body()?.let { body ->
                                _signUpResult.postValue(true)
                                // 회원가입 진행
                                UserModel.userInfo.user.id = body.response.id
                                UserModel.userInfo.user.nickname = body.response.nickname
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

    // 애플로그인의 경우 기본 이미지가 없으므로 생성
    fun makeTempProfileFile() {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                val bm = BitmapFactory.decodeResource(
                    ContextUtil.context.resources,
                    R.drawable.default_profile
                )

                val resizedBitmap = Bitmap.createScaledBitmap(bm, bm.width / 2, bm.height / 2, true)
                val byteArrayOutputStream = ByteArrayOutputStream()
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
                val tempFile =
                    File.createTempFile("resized_image", ".jpg", ContextUtil.context.cacheDir)
                val fileOutputStream = FileOutputStream(tempFile)
                fileOutputStream.write(byteArrayOutputStream.toByteArray())
                fileOutputStream.close()

                Timber.e("tempFile $tempFile")

                setProfileImageFile(tempFile)
            }
        }
    }

    fun setLoginNickname(nickname: String) {
        LoginModel.login.nickname = nickname
    }

    fun setTakePhotoUri(uri: Uri?) {
        JoinModel.takePhotoUri = uri
    }

    fun getTakePhotoUri(): Uri? {
        return JoinModel.takePhotoUri
    }

    fun setProfileImageFile(file: File?) {
        JoinModel.profileImageFile = file
    }

    private fun getProfileImageFile(): File? {
        return JoinModel.profileImageFile
    }

    fun clearErrorMsg() {
        _errorMsg.postValue("")
    }
}