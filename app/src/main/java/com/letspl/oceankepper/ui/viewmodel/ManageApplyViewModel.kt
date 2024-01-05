package com.letspl.oceankepper.ui.viewmodel

import android.content.Context
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.letspl.oceankepper.BuildConfig
import com.letspl.oceankepper.data.dto.GetGuideListDto
import com.letspl.oceankepper.data.model.ManageApplyMemberModel
import com.letspl.oceankepper.data.model.UserModel
import com.letspl.oceankepper.data.network.ApiService
import com.letspl.oceankepper.data.repository.ManageApplyRepositoryImpl
import com.letspl.oceankepper.util.ParsingErrorMsg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.internal.notify
import okhttp3.logging.HttpLoggingInterceptor
import org.apache.commons.lang3.SerializationException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ManageApplyViewModel @Inject constructor(private val manageApplyRepositoryImpl: ManageApplyRepositoryImpl) :
    ViewModel() {

    // 크루원 리스트 불러오기
    private var _getCrewInfoList = MutableLiveData<List<ManageApplyMemberModel.CrewInfoDto>>()
    val getCrewInfoList: LiveData<List<ManageApplyMemberModel.CrewInfoDto>> get() = _getCrewInfoList

    // 크루원 정보 불러오기
    private var _getCrewDetail = MutableLiveData<ManageApplyMemberModel.GetCrewDetailResponse>()
    val getCrewDetail: LiveData<ManageApplyMemberModel.GetCrewDetailResponse> get() = _getCrewDetail

    // 전체 선택하기 이벤트 liveData
    private var _allClicked = MutableLiveData<Boolean>(false)
    val allClicked: LiveData<Boolean> get() = _allClicked

    // 에러 토스트 메세지 text
    private var _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> get() = _errorMsg

    // 에러 토스트 메세지 text
    private var _excelMakeResult = MutableLiveData<Boolean>()
    val excelMakeResult: LiveData<Boolean> get() = _excelMakeResult

    // 신청자 리스트 불러오기
    fun getCrewInfoList(activityId: String) {
        viewModelScope.launch {
            manageApplyRepositoryImpl.getCrewInfoList(activityId).let {
                if (it.isSuccessful) {
                    it.body()?.response?.crewInfo?.let { crewInfoList ->
                        withContext(Dispatchers.Default) {
                            crewInfoList.forEachIndexed { index, getCrewInfoListDto ->
                                ManageApplyMemberModel.applyCrewList.add(
                                    ManageApplyMemberModel.CrewInfoDto(
                                        crewInfoList[index].applicationId,
                                        crewInfoList[index].crewStatus,
                                        crewInfoList[index].nickname,
                                        crewInfoList[index].number,
                                        crewInfoList[index].username,
                                        false
                                    )
                                )
                            }
                        }

                        _getCrewInfoList.postValue(ManageApplyMemberModel.applyCrewList)
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
            }
        }
    }

    // 크루원 정보 불러오기
    fun getCrewDetail(applicationId: String) {
        viewModelScope.launch {
            manageApplyRepositoryImpl.getCrewDetail(applicationId).let {
                if (it.isSuccessful) {
                    it.body()?.response?.let { data ->
                        _getCrewDetail.postValue(
                            ManageApplyMemberModel.GetCrewDetailResponse(
                                ManageApplyMemberModel.GetCrewDetailActivityInfoDto(
                                    data.activityInfo.activity,
                                    data.activityInfo.hosting,
                                    data.activityInfo.noShow
                                ),
                                ManageApplyMemberModel.GetCrewDetailApplicationDto(
                                    data.application.dayOfBirth,
                                    data.application.email,
                                    data.application.id1365,
                                    data.application.name,
                                    data.application.phoneNumber,
                                    data.application.question,
                                    data.application.startPoint,
                                    data.application.transportation
                                ),
                                ManageApplyMemberModel.GetCrewDetailUserInfoDto(
                                    data.userInfo.nickname, data.userInfo.profile
                                )
                            )
                        )
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
            }
        }
    }

    // 크루원 정보 불러오기
    fun postCrewStatus(applicationId: List<String>, status: String, rejectReason: String? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            manageApplyRepositoryImpl.postCrewStatus(
                ManageApplyMemberModel.PostCrewStatusBody(
                    applicationId,
                    rejectReason,
                    status
                )
            ).let {
                if (it.isSuccessful) {

                } else {
                    val errorJsonObject =
                        ParsingErrorMsg.parsingFromStringToJson(it.errorBody()?.string() ?: "")
                    if (errorJsonObject != null) {
                        val errorMsg =
                            ParsingErrorMsg.parsingJsonObjectToErrorMsg(errorJsonObject)
                        _errorMsg.postValue(errorMsg)
                    }
                }
            }
        }
    }

    // 크루원 정보 엑셀 다운로드
    fun getCrewInfoFileDownloadUrl(activityId: String, fileName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            manageApplyRepositoryImpl.getCrewInfoFileDownloadUrl(activityId).let {
                if (it.isSuccessful) {
                    // 파일을 저장할 디렉토리 가져오기
                    val input = it.body()?.bytes()
                    makeFile(input, fileName)
                } else {
                    val errorJsonObject =
                        ParsingErrorMsg.parsingFromStringToJson(it.errorBody()?.string() ?: "")
                    if (errorJsonObject != null) {
                        val errorMsg =
                            ParsingErrorMsg.parsingJsonObjectToErrorMsg(errorJsonObject)
                        _errorMsg.postValue(errorMsg)
                    }
                }
            }
        }
    }

    private fun makeFile(byte: ByteArray?, fileName: String) {
        if(byte != null) {
            try {
                val directory = File(
                    Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS),
                    "OceanKeeper"
                )

                // 디렉토리가 없으면 생성
                if (!directory.exists()) {
                    directory.mkdirs()
                }
                var fos: FileOutputStream? = null
                val file = File(directory, "${fileName}.xlsx")

                if (file.exists()) {
                    file.delete()
                } else {
                    file.createNewFile()
                }
                fos = FileOutputStream(file)
                fos.write(byte)

                _excelMakeResult.postValue(true)
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMsg.postValue("파일 다운로드에 실패하였습니다. 잠시 후 다시 시도해주세요.")
            }
        } else {
            _errorMsg.postValue("파일 다운로드에 실패하였습니다. 잠시 후 다시 시도해주세요.")
        }
    }

    // 전체 선택하기 버튼
    fun setAllIsClickedApplyMember(flag: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                ManageApplyMemberModel.applyCrewList.forEach {
                    ManageApplyMemberModel.tempApplyCrewList.add(it.copy())
                }

                ManageApplyMemberModel.tempApplyCrewList.forEachIndexed { index, crewInfoDto ->
                    if (crewInfoDto.crewStatus != "REJECT") {
                        ManageApplyMemberModel.tempApplyCrewList[index].isClicked = !flag
                    }
                }
            }

            withContext(Dispatchers.Default) {
                setAllChecked(!flag)
                _allClicked.postValue(!flag)
            }
        }
    }

    fun isEndRecruitment(endRecruitmentDate: String): Boolean {
        // nowDate 2024-01-05
        // endDate 2024-01-04

        val date = Date()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        var nowDate = simpleDateFormat.format(date)

        Timber.e("nowDate $nowDate")

        val nowDateArr = nowDate.split("-")
        val endDateArr = endRecruitmentDate.split("-")
        return if(nowDateArr[0].toInt() <= endDateArr[0].toInt()) {
            // 연도가 이전임
            if(nowDateArr[1].toInt() < endDateArr[1].toInt()) {
                // 월이 같거나 큼
                false
            } else if(nowDateArr[1].toInt() == endDateArr[1].toInt()) {
                nowDateArr[2].toInt() > endDateArr[2].toInt()
            } else {
                // 같은 연도이지만 모집 종료됨
                true
            }
        } else {
            // 연도가 같거나 크므로, 모집 종료됨
            true
        }
    }

    // 크루원이 선택된 상태인지 체크
    fun isClickedEmpty(): Boolean {
        return getClickedCrewList().isEmpty()
    }

    // 클릭된 유저 불러오기
    fun getClickedCrewList(): List<ManageApplyMemberModel.CrewInfoDto> {
        val tempCrewArr = arrayListOf<ManageApplyMemberModel.CrewInfoDto>()

        ManageApplyMemberModel.applyCrewList.forEach {
            if (it.isClicked) {
                tempCrewArr.add(it)
            }
        }

        return tempCrewArr
    }

    // 클릭된 유저의 applicationId 불러오기
    fun getClickedCrewApplicationIdList(): List<String> {
        val tempApplicationIdArr = arrayListOf<String>()

        ManageApplyMemberModel.applyCrewList.forEach {
            if (it.isClicked) {
                tempApplicationIdArr.add(it.applicationId)
            }
        }

        return tempApplicationIdArr
    }

    // 클릭된 유저의 닉네임 불러오기
    fun getClickedCrewNicknameList(): List<String> {
        val tempApplicationIdArr = arrayListOf<String>()

        ManageApplyMemberModel.applyCrewList.forEach {
            if (it.isClicked) {
                tempApplicationIdArr.add(it.nickname)
            }
        }

        return tempApplicationIdArr
    }

    // 저장된 crewlist 불러오기
    fun getTempApplyCrewList(): List<ManageApplyMemberModel.CrewInfoDto> {
        return ManageApplyMemberModel.tempApplyCrewList
    }

    fun setAllChecked(flag: Boolean) {
        ManageApplyMemberModel.isAllChecked = flag
    }

    fun getIsAllChecked(): Boolean {
        return ManageApplyMemberModel.isAllChecked
    }

    fun clearData() {
        _excelMakeResult.postValue(false)
        ManageApplyMemberModel.applyCrewList.clear()
        ManageApplyMemberModel.tempApplyCrewList.clear()
    }
}