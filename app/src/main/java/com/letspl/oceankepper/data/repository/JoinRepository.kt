package com.letspl.oceankepper.data.repository

interface JoinRepository {

    suspend fun joinAccount(deviceToken: String, provider: String, providerId: String, nickname:String, email:String, profile: String)

}