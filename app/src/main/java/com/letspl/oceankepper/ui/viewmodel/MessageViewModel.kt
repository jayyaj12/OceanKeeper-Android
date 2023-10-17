package com.letspl.oceankepper.ui.viewmodel

import android.os.Build
import android.os.Message
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letspl.oceankepper.data.dto.GetActivityRecruitmentActivityNameList
import com.letspl.oceankepper.data.dto.MessageItemDto
import com.letspl.oceankepper.data.dto.MessageResponseDto
import com.letspl.oceankepper.data.model.MainModel
import com.letspl.oceankepper.data.model.MessageModel
import com.letspl.oceankepper.data.model.UserModel
import com.letspl.oceankepper.data.repository.ActivityRepositoryImpl
import com.letspl.oceankepper.data.repository.MessageRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.commons.lang3.mutable.Mutable
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val messageRepositoryImpl: MessageRepositoryImpl,
    private val activityRepositoryImpl: ActivityRepositoryImpl
) : ViewModel() {

    // 메세지 조회 결과
    private var _getMessageResult = MutableLiveData<List<MessageItemDto>?>()
    val getMessageResult: LiveData<List<MessageItemDto>?>
        get() = _getMessageResult

    // 프로젝트 크루 닉네임 불러오기
    private var _getCrewNicknameList = MutableLiveData<List<MessageModel.MessageSpinnerCrewNicknameItem>>()
    val getCrewNicknameList: LiveData<List<MessageModel.MessageSpinnerCrewNicknameItem>> get() = _getCrewNicknameList

    fun getGarbageCategory(type: String): String {
        return when (type) {
            "COASTAL" -> "연안쓰레기"
            "FLOATING" -> "부유쓰레기"
            "DEPOSITED" -> "침적쓰레기"
            "ETC" -> "기타"
            else -> ""
        }
    }

    // 누가 보낸것인지 체크하는 함수
    fun getFromWho(send: String): String {
        return if (UserModel.userInfo.user.nickname == send) {
            "내가 보낸 쪽지"
        } else {
            send
        }
    }

    // 활동 프로젝트명 불러오기
    fun getActivityNameList() {
        viewModelScope.launch {
            activityRepositoryImpl.getActivityProject().let {
                if(it.isSuccessful) {
                    val list = arrayListOf<MessageModel.MessageSpinnerProjectNameItem>()
                    it.body()?.response?.hostActivities.let { projectList ->
                        projectList?.forEach {data ->
                            list.add(MessageModel.MessageSpinnerProjectNameItem(
                                data.activityId, data.title, false
                            ))
                        }
                    }
                    MessageModel.projectNameList = list
                    Timber.e("getActivityNameList")

                    getCrewNickName(getActivityNameSpinnerClickActivityId())
                } else {

                }
            }
        }
    }

    // 크루원 닉네임 불러오기
    fun getCrewNickName(activityId: String) {
        viewModelScope.launch {
            Timber.e("getCrewNickName")
            activityRepositoryImpl.getCrewNickname(activityId).let {
                if(it.isSuccessful) {
                    val list = arrayListOf<MessageModel.MessageSpinnerCrewNicknameItem>()
                    it.body()?.response?.crewInformationList?.forEach {
                        list.add(MessageModel.MessageSpinnerCrewNicknameItem(it.nickname, false))
                    }

                    MessageModel.crewNicknameList = list
                    _getCrewNicknameList.postValue(list)
                } else {

                }
            }
        }
    }

    // 쪽지 보내기
    fun postMessage(content: String, receiveList: List<String>) {
        viewModelScope.launch {
            messageRepositoryImpl.postMessage(
                MessageModel.SendMessageRequestBody(
                    getActivityNameSpinnerClickActivityId(),
                    content,
                    receiveList,
                    getTypeSpinnerClickedItem()
                )
            )?.let {
                if(it.isSuccessful) {
                    Timber.e("성공 ")
                } else {
                    Timber.e("메세지 전송 실패")
                }
            }
        }
    }

    fun convertIso8601YYToCustomFormat(iso8601String: String): String {
        try {
            val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            isoFormatter.timeZone = TimeZone.getTimeZone("UTC")
            val date = isoFormatter.parse(iso8601String)

            val customFormatter = SimpleDateFormat("YY-MM-dd hh:mm:ss", Locale.US)
            customFormatter.timeZone = TimeZone.getDefault()

            return customFormatter.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun convertIso8601YYYYToCustomFormat(iso8601String: String): String {
        try {
            val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            isoFormatter.timeZone = TimeZone.getTimeZone("UTC")
            val date = isoFormatter.parse(iso8601String)

            val customFormatter = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US)
            customFormatter.timeZone = TimeZone.getDefault()

            return customFormatter.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun convertAMPMToCustomFormat(iso8601String: String): String {
        try {
            val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            isoFormatter.timeZone = TimeZone.getTimeZone("UTC")
            val date = isoFormatter.parse(iso8601String)

            val customFormatter = SimpleDateFormat("yyyy.MM.dd hh:mma", Locale.US)
            customFormatter.timeZone = TimeZone.getDefault()

            return customFormatter.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun getMessage(type: String) {
        Timber.e("getMessageResult")

        CoroutineScope(Dispatchers.IO).launch {
            messageRepositoryImpl.getMessage(
                "Bearer ${UserModel.userInfo.token.accessToken}",
                MessageModel.messageId,
                10,
                type,
                UserModel.userInfo.user.id
            ).let {
                if (it.isSuccessful) {
                    it.body()?.let { item ->
                        if (item.response.messages.isNotEmpty()) {
                            MessageModel.messageList.addAll(item.response.messages)
                            _getMessageResult.postValue(MessageModel.messageList)

                            MessageModel.messageId =
                                item.response.messages[item.response.messages.size - 1].id
                            MessageModel.isLast = item.response.meta.last
                        } else {
                            MessageModel.messageList.clear()
                            _getMessageResult.postValue(MessageModel.messageList)
                            MessageModel.messageId = null
                            MessageModel.isLast = true
                        }
                    }
                } else {
                    // 실패 시 토스트 표시
                    _getMessageResult.postValue(null)
                }
            }
        }
    }

    // 받을 사람 초기화
    fun clearReceiveList() {
        MessageModel.receiveList.clear()
    }

    // 받을 사람 세팅
    fun setReceiveList(list: List<String>) {
        MessageModel.receiveList = list as ArrayList<String>
    }

    // 받을 사람 추가
    fun addReceiveList(nickName: String) {
        MessageModel.receiveList.add(nickName)
    }

    // 받을 사람 삭제
    fun removeReceiveList(nickName: String) {
        MessageModel.receiveList.remove(nickName)
    }

    fun getCrewList(): ArrayList<MessageModel.MessageSpinnerCrewNicknameItem> {
        return MessageModel.crewNicknameList
    }

    // 읽음 여부 변경
    fun setCrewNicknameListChecked(index: Int){
//        MessageModel.crewNicknameList[index].isChecked = flag
    }

    // 쪽지 유형 선택값 설정
    fun setTypeSpinnerClickedItemPos(value: Int) {
        MessageModel.typeSpinnerClickPos = value
    }

    // 쪽지 유형 선택값 인덱스 가져오기
    fun getTypeSpinnerClickedItemPos(): Int {
        return MessageModel.typeSpinnerClickPos
    }

    // 쪽지 유형 선택값 가져오기
    fun getTypeSpinnerClickedItem(): String {
        return when(MessageModel.typeSpinnerClickPos) {
            0 -> "NOTICE"
            1 -> "PRIVATE"
            2 -> "REJECT"
            else -> ""
        }
    }

    // 활동 프로젝트 선택값 설정
    fun setActivityNameSpinnerClickPos(value: Int) {
        MessageModel.activityNameSpinnerClickPos = value
    }

    // 활동 프로젝트 선택값의 activityId 가져오기
    fun getActivityNameSpinnerClickActivityId(): String {
        return MessageModel.projectNameList[MessageModel.activityNameSpinnerClickPos].activityId
    }

    // 활동 프로젝트 선택값의 activityId 가져오기
    fun getActivityNameSaveList(): List<MessageModel.MessageSpinnerProjectNameItem> {
        return MessageModel.projectNameList
    }

    fun saveClickedMessageItem(item: MessageItemDto) {
        MessageModel.clickedMessageItem = item
    }

    fun changeTypeTabItem(type: String) {
        MessageModel.typeTabItem = type
    }

    fun getTypeTabItem(): String {
        return MessageModel.typeTabItem
    }

    fun getClickedMessageItem(): MessageItemDto {
        return MessageModel.clickedMessageItem!!
    }

    fun isMessageLast(): Boolean {
        return MessageModel.isLast
    }

    fun clearMessageList() {
        MessageModel.isLast = false
        MessageModel.messageId = null
        MessageModel.messageList.clear()
    }
}