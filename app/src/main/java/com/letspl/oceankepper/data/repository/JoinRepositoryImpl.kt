package com.letspl.oceankepper.data.repository

import com.letspl.oceankepper.data.network.ApiService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class JoinRepositoryImpl @Inject constructor(private val apiService: ApiService): JoinRepository {
    override suspend fun joinAccount(
        deviceToken: String,
        provider: String,
        providerId: String,
        nickname: String,
        email: String,
        profile: String
    ) {

    }
}