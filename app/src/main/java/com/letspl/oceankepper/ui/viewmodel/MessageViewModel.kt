package com.letspl.oceankepper.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letspl.oceankepper.data.dto.MessageItemDto
import com.letspl.oceankepper.data.dto.PostMessageDetailBodyDto
import com.letspl.oceankepper.data.model.MessageModel
import com.letspl.oceankepper.data.model.UserModel
import com.letspl.oceankepper.data.repository.ActivityRepositoryImpl
import com.letspl.oceankepper.data.repository.MessageRepositoryImpl
import com.letspl.oceankepper.util.MessageEnterType
import com.letspl.oceankepper.util.ParsingErrorMsg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.commons.lang3.mutable.Mutable
import timber.log.Timber
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val messageRepositoryImpl: MessageRepositoryImpl,
    private val activityRepositoryImpl: ActivityRepositoryImpl,
) : ViewModel() {

    // 메세지 조회 결과
    private var _getMessageResult = MutableLiveData<List<MessageItemDto>?>()
    val getMessageResult: LiveData<List<MessageItemDto>?>
        get() = _getMessageResult

    // 메세지 전송 결과
    private var _sendMessageResult = MutableLiveData<Boolean>()
    val sendMessageResult: LiveData<Boolean>
        get() = _sendMessageResult


    // 메세지 삭제
    private var _deleteMessageResult = MutableLiveData<Boolean>()
    val deleteMessageResult: LiveData<Boolean>
        get() = _deleteMessageResult

    // 프로젝트 크루 닉네임 불러오기
    private var _getCrewNicknameList =
        MutableLiveData<List<MessageModel.MessageSpinnerCrewNicknameItem>>()
    val getCrewNicknameList: LiveData<List<MessageModel.MessageSpinnerCrewNicknameItem>> get() = _getCrewNicknameList

    // 에러 토스트 메세지 text
    private var _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> get() = _errorMsg

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
        CoroutineScope(Dispatchers.IO).launch {
            activityRepositoryImpl.getActivityProject().let {
                if (it.isSuccessful) {
                    val list = arrayListOf<MessageModel.MessageSpinnerProjectNameItem>()
                    it.body()?.response?.hostActivities.let { projectList ->
                        projectList?.forEach { data ->
                            list.add(MessageModel.MessageSpinnerProjectNameItem(
                                data.activityId, data.title, false
                            ))
                        }
                    }
                    MessageModel.projectNameList = list
                    Timber.e("getActivityNameList")

                    if (list.isNotEmpty()) {
                        getCrewNickName(getActivityNameSpinnerClickActivityId())
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

    // 크루원 닉네임 불러오기
    fun getCrewNickName(activityId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            activityRepositoryImpl.getCrewNickname(activityId).let {
                if (it.isSuccessful) {
                    val list = arrayListOf<MessageModel.MessageSpinnerCrewNicknameItem>()
                    it.body()?.response?.crewInformationList?.forEach {
                        list.add(MessageModel.MessageSpinnerCrewNicknameItem(it.nickname, false))
                    }

                    MessageModel.crewNicknameList = list
                    _getCrewNicknameList.postValue(list)
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

    // 쪽지 보내기
    fun postMessage(activityId: String, content: String, receiveList: List<String>, type: String) {
        CoroutineScope(Dispatchers.IO).launch {
            messageRepositoryImpl.postMessage(
                MessageModel.SendMessageRequestBody(
                    activityId,
                    content,
                    receiveList,
                    type
                )
            ).let {
                if (it.isSuccessful) {
                    _sendMessageResult.postValue(true)
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

    // 쪽지 삭제하기
    fun deleteMessage(messageId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            messageRepositoryImpl.deleteMessage(messageId).let {
                if (it.isSuccessful) {
                    _deleteMessageResult.postValue(true)
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

    // 쪽지의 읽음 상태를 변경
    fun postMessageDetail(messageId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            messageRepositoryImpl.postMessageRead(
                PostMessageDetailBodyDto(
                    messageId,
                    true
                )
            ).let {
                if (!it.isSuccessful) {
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

    fun convertIso8601YYToCustomFormat(date: String): String {
        var dateStr = date.replace("-", ".")
        return "${dateStr.substring(2, 10)} ${dateStr.substring(11, 19)}"
    }

    fun convertIso8601YYYYToCustomFormat(date: String): String {
        var dateStr = date.replace("-", ".")
        return "활동일: 20$dateStr"
    }

    fun convertAMPMToCustomFormat(date: String): String {
        var dateStr = date.replace("-", ".")
        Timber.e("date.substring(11, 13) ${date}")
        Timber.e("date.substring(11, 13) ${date.substring(11, 13)}")
        val ampm = if (date.substring(9, 11).toInt() >= 12) {
            "PM"
        } else {
            "AM"
        }
        return "20${dateStr}$ampm"
    }

    fun getMessage(type: String) {
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
                        Timber.e("item ${item.response.messages}")
                        if (item.response.messages.isNotEmpty()) {

                            MessageModel.messageList.addAll(item.response.messages)
                            Timber.e("MessageModel.messageList ${MessageModel.messageList}")
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

    // MessageSpinnerCrewNicknameItem의 nickname만 필터링 하여 list로 반환함
    fun getConvertFromMessageCrewItemToStringForNickname(): List<String> {
        val crewList: ArrayList<String> = arrayListOf()
        getCrewList().forEach { item ->
            crewList.add(item.nickname)
        }

        return crewList
    }

    fun setMessageEnterType(_enterType: MessageEnterType) {
        MessageModel.enterType = _enterType
    }

    fun getMessageEnterType(): MessageEnterType {
        return MessageModel.enterType
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
    fun setCrewNicknameListChecked(index: Int) {
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

    // 쪽지 보내기 메세지 타입 선택 여부 설정
    fun setIsClickedMessageType(flag: Boolean) {
        MessageModel.isClickedMessageType = flag
    }

    // 쪽지 보내기 메세지 타입 선택 여부 가져오기
    fun isClickedMessageType(): Boolean {
        return MessageModel.isClickedMessageType
    }

    // 쪽지 보내기 프로젝트 명 선택 여부 설정
    fun setIsClickedProjectName(flag: Boolean) {
        MessageModel.isClickedProjectName = flag
    }

    // 쪽지 보내기 프로젝트 명 선택 여부 가져오기
    fun isClickedProjectName(): Boolean {
        return MessageModel.isClickedProjectName
    }

    // 쪽지 보내기 메세지 타입 선택 여부 설정
    fun setIsClickedReceive(flag: Boolean) {
        MessageModel.isClickedReceive = flag
    }

    // 쪽지 보내기 메세지 타입 선택 여부 가져오기
    fun isClickedReceive(): Boolean {
        return MessageModel.isClickedReceive
    }

    // 쪽지 유형 선택값 가져오기
    fun getTypeSpinnerClickedItem(): String {
        return when (MessageModel.typeSpinnerClickPos) {
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

    // 활동 프로젝트 선택값 가져오기
    fun getActivityNameSpinnerClickPos(): Int {
        return MessageModel.activityNameSpinnerClickPos
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

    fun clearMessageSpinnerClick() {
        setIsClickedMessageType(false)
        setIsClickedProjectName(false)
        setIsClickedReceive(false)
    }


    fun clearMessageLiveData() {
        _sendMessageResult.postValue(false)
        _deleteMessageResult.postValue(false)
    }

    fun clearMessageList() {
        MessageModel.isLast = false
        MessageModel.messageId = null
        MessageModel.messageList.clear()
        setTypeSpinnerClickedItemPos(0)
        setActivityNameSpinnerClickPos(0)
        _sendMessageResult.postValue(false)
    }
}