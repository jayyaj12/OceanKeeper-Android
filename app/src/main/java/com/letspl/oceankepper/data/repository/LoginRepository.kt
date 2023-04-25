package com.letspl.oceankepper.data.repository

interface LoginRepository {

    suspend fun loginAccount(provider: String, providerId: String)

}