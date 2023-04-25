package com.letspl.oceankepper.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letspl.oceankepper.data.repository.LoginRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepositoryImpl: LoginRepositoryImpl): ViewModel() {

    // 로그인
    fun onLoginAccount(provider: String, providerId: String) {
        viewModelScope.launch {
            loginRepositoryImpl.loginAccount(provider, providerId)
        }
    }

}