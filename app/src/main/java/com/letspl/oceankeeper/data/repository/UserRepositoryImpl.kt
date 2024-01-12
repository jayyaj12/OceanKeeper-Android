package com.letspl.oceankeeper.data.repository

import com.letspl.oceankeeper.data.dto.GetCheckDuplicateNicknameResponse
import com.letspl.oceankeeper.data.dto.PutNicknameBody
import com.letspl.oceankeeper.data.dto.PutNicknameResponse
import com.letspl.oceankeeper.data.model.UserModel
import com.letspl.oceankeeper.data.network.ApiService
import retrofit2.Response
import timber.log.Timber

class UserRepositoryImpl(private val oceanService: ApiService): UserRepository {
    // 닉네임 중복 체크
    override suspend fun getDuplicateNickname(nickname: String): Response<GetCheckDuplicateNicknameResponse> {
        return oceanService.getDuplicateNickname(nickname)
    }

    // 닉네임 변경
    override suspend fun putNickname(putNicknameBody: PutNicknameBody): Response<PutNicknameResponse> {
        Timber.e("UserModel.userInfo.token.accessToken ${UserModel.userInfo.token.accessToken}")
        return oceanService.putNickname("Bearer ${UserModel.userInfo.token.accessToken}", putNicknameBody)
    }
}