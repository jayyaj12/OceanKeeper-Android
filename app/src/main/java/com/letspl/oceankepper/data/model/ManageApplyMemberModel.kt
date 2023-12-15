package com.letspl.oceankepper.data.model

object ManageApplyMemberModel {

    var isAllChecked = false
    var applyCrewList = arrayListOf<CrewInfoDto>()
    var tempApplyCrewList = arrayListOf<CrewInfoDto>()

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

    @kotlinx.serialization.Serializable
    data class CrewInfoDto(
        val applicationId: String,
        val crewStatus: String,
        val nickname: String,
        var number: Int,
        val username: String,
        var isClicked: Boolean
    )


}