package com.letspl.oceankepper.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letspl.oceankepper.data.dto.GetActivityInfoResponseDto
import com.letspl.oceankepper.data.model.UserModel
import com.letspl.oceankepper.data.repository.ActivityRepositoryImpl
import com.letspl.oceankepper.util.ParsingErrorMsg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyActivityViewModel @Inject constructor(private val activityViRepositoryImpl: ActivityRepositoryImpl): ViewModel() {

    // 에러 토스트 메세지 text
    private var _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> get() = _errorMsg

    // 에러 토스트 메세지 text
    private var _getActivityInfoResult = MutableLiveData<GetActivityInfoResponseDto>()
    val getActivityInfoResult: LiveData<GetActivityInfoResponseDto> get() = _getActivityInfoResult

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

}