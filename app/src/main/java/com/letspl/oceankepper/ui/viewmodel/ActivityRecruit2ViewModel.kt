package com.letspl.oceankepper.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letspl.oceankepper.data.dto.ActivityRegisterLocationDto
import com.letspl.oceankepper.data.model.ActivityRecruit2Model
import com.letspl.oceankepper.data.model.ActivityRecruitModel
import com.letspl.oceankepper.data.model.UserModel
import com.letspl.oceankepper.data.repository.ActivityRepositoryImpl
import com.letspl.oceankepper.util.ContextUtil
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.net.URI
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class ActivityRecruit2ViewModel @Inject constructor(
    private val activityRecruitViewModel: ActivityRecruitViewModel,
    private val activityRepositoryImpl: ActivityRepositoryImpl
) : ViewModel() {

    // 키퍼 소개 텍스트 길이
    private var _keeperIntroduceLength = MutableLiveData<Int>(0)
    val keeperIntroduceLength: LiveData<Int> get() = _keeperIntroduceLength

    // 활동 스토리 텍스트 길이
    private var _activityStoryLength = MutableLiveData<Int>(0)
    val activityStoryLength: LiveData<Int> get() = _activityStoryLength

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
        transportation: String
    ) {

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                uploadThumbnailImage()
            }
            withContext(Dispatchers.IO) {
                uploadKeeperImage()
            }
            withContext(Dispatchers.IO) {
                uploadStoryImage()
            }

            Timber.e("activityStory $activityStory")
            Timber.e("etc $etc")
            Timber.e("garbageCategory $garbageCategory")
            Timber.e("keeperIntroduction $keeperIntroduction")
            Timber.e("locationTag $locationTag")
            Timber.e("preparation $preparation")
            Timber.e("programDetails $programDetails")
            Timber.e("quota $quota")
            Timber.e("rewards $rewards")
            Timber.e("title $title")
            Timber.e("transportation $transportation")

            Timber.e("activityRecruitViewModel.getRecruitEndDate() ${activityRecruitViewModel.getRecruitEndDate()}")
            Timber.e("activityRecruitViewModel.getRecruitStartDate() ${activityRecruitViewModel.getRecruitStartDate()}")
            Timber.e("activityRecruitViewModel.getActivityStartDate() ${activityRecruitViewModel.getActivityStartDate()}")
            withContext(Dispatchers.IO) {
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
            }
        }
    }

    private suspend fun uploadThumbnailImage() {
        withContext(Dispatchers.IO) {
            if (ActivityRecruit2Model.thumbnailImgFile != null) {
                activityRepositoryImpl.uploadThumbnailImage(ActivityRecruit2Model.thumbnailImgFile)
                    .let {
                        if (it.isSuccessful) {
                            ActivityRecruit2Model.thumbnailImgStr = it.body()?.url.toString()
                        }
                    }
            }
        }
    }

    private suspend fun uploadKeeperImage() {
        withContext(Dispatchers.IO) {
            if (ActivityRecruit2Model.keeperIntroduceImgFile != null) {
                activityRepositoryImpl.uploadThumbnailImage(ActivityRecruit2Model.keeperIntroduceImgFile)
                    .let {
                        if (it.isSuccessful) {
                            ActivityRecruit2Model.keeperIntroduceImgStr = it.body()?.url.toString()
                        }
                    }
            }
        }
    }

    private suspend fun uploadStoryImage() {
        withContext(Dispatchers.IO) {
            if (ActivityRecruit2Model.activityStoryImgFile != null) {
                activityRepositoryImpl.uploadStoryImage(ActivityRecruit2Model.activityStoryImgFile)
                    .let {
                        if (it.isSuccessful) {
                            ActivityRecruit2Model.activityStoryImgStr = it.body()?.url.toString()
                        }
                    }
            }
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
        _keeperIntroduceLength.postValue(str.length)
    }

    // 활동 스토리 텍스트 길이 변경
    fun onChangedActivityStoryEditText(str: String) {
        _activityStoryLength.postValue(str.length)
    }

    // 필수 정보가 모두 들어갔는지 여부 체크
    fun isExistNeedData(): Boolean {
        return ActivityRecruit2Model.thumbnailImgFile != null && activityStoryLength.value != 0 && keeperIntroduceLength.value != 0
    }
}