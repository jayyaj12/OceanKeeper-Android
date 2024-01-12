package com.letspl.oceankeeper.data.repository

import com.letspl.oceankeeper.data.dto.GetCheckDuplicateNicknameResponse
import com.letspl.oceankeeper.data.dto.PutNicknameBody
import com.letspl.oceankeeper.data.dto.PutNicknameResponse
import retrofit2.Response

interface UserRepository {
    // 닉네임 중복 확인
    suspend fun getDuplicateNickname(nickname: String): Response<GetCheckDuplicateNicknameResponse>
    // 닉네임 변경
    suspend fun putNickname(putNicknameBody: PutNicknameBody): Response<PutNicknameResponse>
}