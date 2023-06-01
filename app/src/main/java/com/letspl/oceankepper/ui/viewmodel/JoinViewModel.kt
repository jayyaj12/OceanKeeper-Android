package com.letspl.oceankepper.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letspl.oceankepper.data.repository.JoinRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class JoinViewModel @Inject constructor(private val joinRepositoryImpl: JoinRepositoryImpl): ViewModel() {

    fun uploadImageFile(file: File) {
        viewModelScope.launch {
            joinRepositoryImpl.uploadProfileImage(file).let {
                if(it.isSuccessful) {
                    it.body()?.let { body ->
                        Timber.e("body url ${body.url}")
                    }
                }
            }
        }
    }

}