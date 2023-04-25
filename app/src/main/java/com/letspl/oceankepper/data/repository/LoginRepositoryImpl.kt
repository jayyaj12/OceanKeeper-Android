package com.letspl.oceankepper.data.repository

import com.letspl.oceankepper.data.network.ApiService
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(private val apiService: ApiService): LoginRepository {
    override suspend fun loginAccount(provider: String, providerId: String) {
//        serviceApi

    }
}