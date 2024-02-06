package com.letspl.oceankeeper.ui.viewmodel

import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letspl.oceankeeper.data.dto.EditActivityRegisterDto
import com.letspl.oceankeeper.data.model.ActivityRecruit2Model
import com.letspl.oceankeeper.data.model.ActivityRecruitModel
import com.letspl.oceankeeper.data.model.UserModel
import com.letspl.oceankeeper.data.repository.ActivityRepositoryImpl
import com.letspl.oceankeeper.util.ContextUtil
import com.letspl.oceankeeper.util.NetworkUtils
import com.letspl.oceankeeper.util.ParsingErrorMsg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ActivityRecruit2ViewModel @Inject constructor(
    private val activityRecruitViewModel: ActivityRecruitViewModel,
    private val activityRepositoryImpl: ActivityRepositoryImpl,
) : ViewModel() {

    // 키퍼 소개 텍스트 길이
    private var _keeperIntroduceLength = MutableLiveData<Int>(0)
    val keeperIntroduceLength: LiveData<Int> get() = _keeperIntroduceLength

    // 활동 스토리 텍스트 길이
    private var _activityStoryLength = MutableLiveData<Int>(0)
    val activityStoryLength: LiveData<Int> get() = _activityStoryLength

    // 활동 모집 등록 성공 여부
    private var _recruitActivityIsSuccess = MutableLiveData<Boolean>()
    val recruitActivityIsSuccess: LiveData<Boolean> get() = _recruitActivityIsSuccess

    // 활동 모집 수정 성공 여부
    private var _editRecruitActivityIsSuccess = MutableLiveData<Boolean>()
    val editRecruitActivityIsSuccess: LiveData<Boolean> get() = _editRecruitActivityIsSuccess

    // 에러 토스트 메세지 text
    private var _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> get() = _errorMsg

    fun activityRegister(
        activityStory: String,
        etc: String,
        garbageCategory: String,
        keeperIntroduction: String,
        locationTag: String,
        preparation: String,
        programDetails: String,
        quota: Int,
        rewards: String,
        title: String,
        transportation: String,
    ) {
        if (NetworkUtils.isNetworkConnected()) {
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO) {
                    uploadThumbnailImage()
                }
                withContext(Dispatchers.IO) {
                    uploadKeeperImage()
                }
                withContext(Dispatchers.IO) {
                    uploadStoryImage()
                }
                withContext(Dispatchers.IO) {
                    runCatching {
                        activityRepositoryImpl.activityRegister(
                            activityStory,
                            etc,
                            garbageCategory,
                            ActivityRecruit2Model.keeperIntroduceImgStr ?: "",
                            keeperIntroduction,
                            ActivityRecruitModel.location,
                            locationTag,
                            preparation,
                            programDetails,
                            quota,
                            activityRecruitViewModel.getRecruitEndDate(),
                            activityRecruitViewModel.getRecruitStartDate(),
                            rewards,
                            activityRecruitViewModel.getActivityStartDate(),
                            ActivityRecruit2Model.activityStoryImgStr ?: "",
                            ActivityRecruit2Model.thumbnailImgStr ?: "",
                            title,
                            transportation,
                            UserModel.userInfo.user.id
                        )
                    }.fold(onSuccess = {
                        if (it.isSuccessful) {
                            _recruitActivityIsSuccess.postValue(true)
                        } else {
                            val errorJsonObject =
                                ParsingErrorMsg.parsingFromStringToJson(
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

    fun patchActivityRegister(
        activityId: String,
        activityStory: String,
        etc: String,
        garbageCategory: String,
        keeperIntroduction: String,
        locationTag: String,
        preparation: String,
        programDetails: String,
        quota: Int,
        rewards: String,
        title: String,
        transportation: String,
    ) {
        if (NetworkUtils.isNetworkConnected()) {
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO) {
                    uploadThumbnailImage()
                }
                withContext(Dispatchers.IO) {
                    uploadKeeperImage()
                }
                withContext(Dispatchers.IO) {
                    uploadStoryImage()
                }
                withContext(Dispatchers.IO) {
                    runCatching {
                        activityRepositoryImpl.patchActivity(
                            activityId,
                            EditActivityRegisterDto(
                                activityStory,
                                etc,
                                garbageCategory,
                                ActivityRecruit2Model.keeperIntroduceImgStr ?: "",
                                keeperIntroduction,
                                ActivityRecruitModel.location,
                                locationTag,
                                preparation,
                                programDetails,
                                quota,
                                activityRecruitViewModel.getRecruitEndDate(),
                                activityRecruitViewModel.getRecruitStartDate(),
                                rewards,
                                activityRecruitViewModel.getActivityStartDate(),
                                ActivityRecruit2Model.activityStoryImgStr ?: "",
                                ActivityRecruit2Model.thumbnailImgStr ?: "",
                                title,
                                transportation
                            )
                        )
                    }.fold(onSuccess = {
                        if (it.isSuccessful) {
                            _editRecruitActivityIsSuccess.postValue(true)
                        } else {
                            val errorJsonObject =
                                ParsingErrorMsg.parsingFromStringToJson(
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

    private suspend fun uploadThumbnailImage() {
        if(NetworkUtils.isNetworkConnected()) {
            withContext(Dispatchers.IO) {
                if (ActivityRecruit2Model.thumbnailImgFile != null) {
                    runCatching {
                        activityRepositoryImpl.uploadThumbnailImage(ActivityRecruit2Model.thumbnailImgFile)
                    }.fold(
                        onSuccess = {
                            if (it.isSuccessful) {
                                ActivityRecruit2Model.thumbnailImgStr = it.body()?.url.toString()
                            } else {
                                val errorJsonObject =
                                    ParsingErrorMsg.parsingFromStringToJson(
                                        it.errorBody()?.string()
                                            ?: ""
                                    )
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
            }
        } else {
            _errorMsg.postValue("not Connect Network")
        }
    }

    private suspend fun uploadKeeperImage() {
        if(NetworkUtils.isNetworkConnected()){
            withContext(Dispatchers.IO) {
                if (ActivityRecruit2Model.keeperIntroduceImgFile != null) {
                    runCatching {
                        activityRepositoryImpl.uploadThumbnailImage(ActivityRecruit2Model.keeperIntroduceImgFile)
                    }.fold(
                        onSuccess = {
                            if (it.isSuccessful) {
                                ActivityRecruit2Model.keeperIntroduceImgStr = it.body()?.url.toString()
                            } else {
                                val errorJsonObject =
                                    ParsingErrorMsg.parsingFromStringToJson(
                                        it.errorBody()?.string()
                                            ?: ""
                                    )
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
            }
        } else {
            _errorMsg.postValue("not Connect Network")
        }
    }

    private suspend fun uploadStoryImage() {
        if(NetworkUtils.isNetworkConnected()) {
            withContext(Dispatchers.IO) {
                if (ActivityRecruit2Model.activityStoryImgFile != null) {
                    runCatching {
                        activityRepositoryImpl.uploadStoryImage(ActivityRecruit2Model.activityStoryImgFile)
                    }.fold(
                        onSuccess = {
                            if (it.isSuccessful) {
                                ActivityRecruit2Model.activityStoryImgStr = it.body()?.url.toString()
                            } else {
                                val errorJsonObject =
                                    ParsingErrorMsg.parsingFromStringToJson(
                                        it.errorBody()?.string()
                                            ?: ""
                                    )
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
            }
        } else {
            _errorMsg.postValue("not Connect Network")
        }
    }

    // 이미지 실제 경로 반환
    fun getRealPathFromURI(uri: Uri): String {
        val buildName = Build.MANUFACTURER
        if (buildName.equals("Xiaomi")) {
            return uri.path!!
        }

        var columnIndex = 0
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = ContextUtil.context.contentResolver.query(uri, proj, null, null, null)
        if (cursor!!.moveToFirst()) {
            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }

        val result = cursor.getString(columnIndex)
        cursor.close()

        return result
    }

    fun getRecruitCompleteText(): String {
        val text = activityRecruitViewModel.getActivityStartDate().split("T")[0]
        val year = text.substring(2, 4)
        val month = text.substring(5, 7)
        val date = text.substring(8, 10)
        return "${year}년 ${month}월 ${date}일 활동에 대한 신청 완료!\n최종 선정 여부는 쪽지로 안내됩니다."
    }

    fun getRecruitEditCompleteText(): String {
        val text = activityRecruitViewModel.getActivityStartDate().split("T")[0]
        val year = text.substring(2, 4)
        val month = text.substring(5, 7)
        val date = text.substring(8, 10)
        return "활동이 ${year}년 ${month}월 ${date}일에 진행됩니다."
    }

    // 썸네일 이미지 파일 저장
    fun setThumbnailImageFile(file: File) {
        ActivityRecruit2Model.thumbnailImgFile = file
    }

    // 썸네일 이미지 파일 가져오기
    fun getThumbnailImageFile(): File? {
        return ActivityRecruit2Model.thumbnailImgFile
    }

    // 키퍼 소개 이미지 파일 저장
    fun setKeeperIntroduceImageFile(file: File) {
        ActivityRecruit2Model.keeperIntroduceImgFile = file
    }

    // 키퍼 소개 이미지 파일 가져오기
    fun getKeeperIntroduceImageFile(): File? {
        return ActivityRecruit2Model.keeperIntroduceImgFile
    }

    // 활동 스토리 이미지 파일 저장
    fun setActivityStoryImageFile(file: File) {
        ActivityRecruit2Model.activityStoryImgFile = file
    }

    // 활동 스토리 이미지 파일 가져오기
    fun getActivityStoryImageFile(): File? {
        return ActivityRecruit2Model.activityStoryImgFile
    }

    // 키퍼 소개 텍스트 길이 변경
    fun onChangedKeeperIntroduceEditText(str: String) {
        ActivityRecruit2Model.keeperIntroduceContent = str
        _keeperIntroduceLength.postValue(str.length)
    }

    // 활동 스토리 텍스트 길이 변경
    fun onChangedActivityStoryEditText(str: String) {
        ActivityRecruit2Model.activityStoryContent = str
        _activityStoryLength.postValue(str.length)
    }

    // 필수 정보가 모두 들어갔는지 여부 체크
    fun isExistNeedData(): Boolean {
        return ActivityRecruit2Model.thumbnailImgFile != null && activityStoryLength.value != 0 && keeperIntroduceLength.value != 0
    }

    // 수정 시 필수 정보가 모두 들어갔는지 여부 체크
    fun isEditExistNeedData(): Boolean {
        return activityStoryLength.value != 0 && keeperIntroduceLength.value != 0
    }

    fun clearData() {
        ActivityRecruit2Model.thumbnailImgFile = null
        ActivityRecruit2Model.keeperIntroduceImgFile = null
        ActivityRecruit2Model.activityStoryImgFile = null
        ActivityRecruit2Model.thumbnailImgStr = null
        ActivityRecruit2Model.keeperIntroduceImgStr = null
        ActivityRecruit2Model.activityStoryImgStr = null
        ActivityRecruit2Model.keeperIntroduceContent = ""
        ActivityRecruit2Model.activityStoryContent = ""
        _recruitActivityIsSuccess.postValue(false)
        _editRecruitActivityIsSuccess.postValue(false)
    }

    fun clearErrorMsg() {
        _errorMsg.postValue("")
    }
}