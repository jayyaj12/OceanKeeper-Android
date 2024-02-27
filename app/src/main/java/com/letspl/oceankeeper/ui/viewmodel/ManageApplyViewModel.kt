package com.letspl.oceankeeper.ui.viewmodel

import android.content.ContentResolver
import android.content.ContentValues
import android.database.Cursor
import android.os.Build
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letspl.oceankeeper.data.model.ManageApplyMemberModel
import com.letspl.oceankeeper.data.repository.ManageApplyRepositoryImpl
import com.letspl.oceankeeper.util.NetworkUtils
import com.letspl.oceankeeper.util.ParsingErrorMsg
import com.navercorp.nid.NaverIdLoginSDK.applicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    // 엑셀 저장 결과
    private var _excelMakeResult = MutableLiveData<String>()
    val excelMakeResult: LiveData<String> get() = _excelMakeResult

    // 신청자 리스트 불러오기
    fun getCrewInfoList(activityId: String) {
        if (NetworkUtils.isNetworkConnected()) {
            CoroutineScope(Dispatchers.IO).launch {
                runCatching {
                    manageApplyRepositoryImpl.getCrewInfoList(activityId)
                }.fold(
                    onSuccess = {
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

    // 크루원 정보 불러오기
    fun getCrewDetail(applicationId: String) {
        if (NetworkUtils.isNetworkConnected()) {
            CoroutineScope(Dispatchers.IO).launch {
                runCatching {
                    manageApplyRepositoryImpl.getCrewDetail(applicationId)
                }.fold(
                    onSuccess = {
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

    // 크루원 정보 불러오기
    fun postCrewStatus(applicationId: List<String>, status: String, rejectReason: String? = null) {
        if (NetworkUtils.isNetworkConnected()) {
            CoroutineScope(Dispatchers.IO).launch {
                runCatching {
                    manageApplyRepositoryImpl.postCrewStatus(
                        ManageApplyMemberModel.PostCrewStatusBody(
                            applicationId,
                            rejectReason,
                            status
                        )
                    )
                }.fold(
                    onSuccess = {
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

    // 크루원 정보 엑셀 다운로드
    fun getCrewInfoFileDownloadUrl(activityId: String, fileName: String) {
        if (NetworkUtils.isNetworkConnected()) {
            CoroutineScope(Dispatchers.IO).launch {
                runCatching {
                    manageApplyRepositoryImpl.getCrewInfoFileDownloadUrl(activityId)
                }.fold(
                    onSuccess = {
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

    private fun makeFile(byte: ByteArray?, fileName: String) {
        if(byte != null) {
            val directory = File(
                Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS),
                "OceanKeeper"
            )

            // 디렉토리가 없으면 생성
            if (!directory.exists()) {
                directory.mkdirs()
            }

            val date = Date()
            val file = File(directory, "${fileName}_${SimpleDateFormat("HH_mm_ss").format(date)}.xlsx")


            var fos: FileOutputStream? = null
            try {
                Timber.e("file absoluteFile${file.absoluteFile}")
                Timber.e("file exists ${file.exists()}")
                if (file.exists()) {
                    Timber.e("file.delete() ${file.delete()}")
                    file.delete()
                } else {
                    Timber.e("file.createNewFile() ${file.createNewFile()}")
                    file.createNewFile()
                }

                fos = FileOutputStream(file)
                Timber.e("file ${file.absoluteFile}")
                Timber.e("byte  ${byte.size}")
                fos.write(byte)

                // MediaStore에 파일 정보 추가
                val contentResolver: ContentResolver = applicationContext.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "${fileName}_${SimpleDateFormat("HH_mm_ss").format(date)}.xlsx")
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/OceanKeeper")
                }

                val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val cursor: Cursor? = contentResolver.query(
                        MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                        arrayOf(MediaStore.MediaColumns.DISPLAY_NAME),
                        "${MediaStore.MediaColumns.DISPLAY_NAME} = ?",
                        arrayOf("$fileName.xlsx"),
                        null
                    )

                    if (cursor != null && cursor.count > 0) {
                        // 이미 존재하는 파일이므로 추가하지 않음
                        cursor.close()
                        null
                    } else {
                        // 파일이 존재하지 않는 경우 삽입
                        cursor?.close()
                        contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    }
                } else {
                    null
                }
                fos.close()

                // MediaStore에 성공적으로 추가되었는지 확인
                if (uri != null) {
                    // 성공적으로 추가되었을 때의 처리
                    _excelMakeResult.postValue("${fileName}_${SimpleDateFormat("HH_mm_ss").format(date)}.xlsx")
                } else {
                    // 추가에 실패했을 때의 처리
                    _errorMsg.postValue("파일 다운로드에 실패하였습니다.\n휴대전화 소프트웨어 버전을 업데이트 하시거나 잠시 후 시도해주세요.")
                }

            } catch (e: Exception) {
                // 파일 생성 및 쓰기 중 예외 처리
                Timber.e("e.print ${e.message}")
            } finally {
                fos?.close()
            }
        } else {
            _errorMsg.postValue("파일 다운로드에 실패하였습니다. 잠시 후 다시 시도해주세요.")
        }
    }
//    private fun makeFile(byte: ByteArray?, fileName: String) {
//        if(byte != null) {
//            try {
//                val directory = File(
//                    Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS),
//                    "OceanKeeper"
//                )
//
//                // 디렉토리가 없으면 생성
//                if (!directory.exists()) {
//                    directory.mkdirs()
//                }
//                var fos: FileOutputStream? = null
//                val file = File(directory, "${fileName}.xlsx")
//
//                if (file.exists()) {
//                    file.delete()
//                } else {
//                    file.createNewFile()
//                }
//                fos = FileOutputStream(file)
//                fos.write(byte)
//
//                _excelMakeResult.postValue(true)
//            } catch (e: Exception) {
//                e.printStackTrace()
//                _errorMsg.postValue("파일 다운로드에 실패하였습니다. 잠시 후 다시 시도해주세요.")
//            }
//        } else {
//            _errorMsg.postValue("파일 다운로드에 실패하였습니다. 잠시 후 다시 시도해주세요.")
//        }
//    }

    // 전체 선택하기 버튼
    fun setAllIsClickedApplyMember(flag: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
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
        val date = Date()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        var nowDate = simpleDateFormat.format(date)

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

    // 저장된 temp crewlist 불러오기
    fun getTempApplyCrewList(): List<ManageApplyMemberModel.CrewInfoDto> {
        return ManageApplyMemberModel.tempApplyCrewList
    }

    // 저장된 crewlist 불러오기
    fun getApplyCrewList(): List<ManageApplyMemberModel.CrewInfoDto> {
        return ManageApplyMemberModel.applyCrewList
    }

    fun setAllChecked(flag: Boolean) {
        ManageApplyMemberModel.isAllChecked = flag
    }

    fun getIsAllChecked(): Boolean {
        return ManageApplyMemberModel.isAllChecked
    }

    fun clearData() {
        _excelMakeResult.postValue("")
        ManageApplyMemberModel.applyCrewList.clear()
        ManageApplyMemberModel.tempApplyCrewList.clear()
    }

    fun clearErrorMsg() {
        _errorMsg.postValue("")
    }
}