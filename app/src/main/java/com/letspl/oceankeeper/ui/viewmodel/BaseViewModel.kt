package com.letspl.oceankeeper.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.letspl.oceankeeper.data.model.LoginModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BaseViewModel @Inject constructor(): ViewModel() {

    fun setFcmDeviceToken(token: String) {
        LoginModel.login.deviceToken = token
    }

}