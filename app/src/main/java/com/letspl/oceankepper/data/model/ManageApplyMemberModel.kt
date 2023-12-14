package com.letspl.oceankepper.data.model

import com.letspl.oceankepper.util.ApplyMemberStatus

object ManageApplyMemberModel {

    @kotlinx.serialization.Serializable
    data class GetCrewInfoListResponseDto(
        val statusCode: Int,
        val timestamp: String,
        val response: GetCrewInfoListResponse
    )

    @kotlinx.serialization.Serializable
    data class GetCrewInfoListResponse(
        val activityId: String,
        val crewInfo: List<GetCrewInfoListDto>
    )

    @kotlinx.serialization.Serializable
    data class GetCrewInfoListDto(
      val applicationId: String,
      val crewStatus: String,
      val nickname: String,
      val number: Int,
      val username: String
    )


}