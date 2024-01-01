package com.letspl.oceankepper.data.model

import kotlinx.serialization.Serializable

object ManageApplyMemberModel {

    var isAllChecked = false
    var applyCrewList = arrayListOf<CrewInfoDto>()
    var tempApplyCrewList = arrayListOf<CrewInfoDto>()

    @Serializable
    data class PostCrewStatusBody(
        val applicationId: List<String>,
        val rejectReason: String?,
        val status: String
    )

    @kotlinx.serialization.Serializable
    data class PostCrewStatusDto(
        val statusCode: Int,
        val timestamp: String,
        val response: PostCrewStatusResponseDto
    )

    @kotlinx.serialization.Serializable
    data class PostCrewStatusResponseDto(
        val crewStatus: Int,
        val messageId: List<Int>,
        val result: Boolean
    )

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

    @kotlinx.serialization.Serializable
    data class GetCrewDetailResponseDto(
        val statusCode: Int,
        val timestamp: String,
        val response: GetCrewDetailResponse
    )

    @kotlinx.serialization.Serializable
    data class GetCrewDetailResponse(
        val activityInfo: GetCrewDetailActivityInfoDto,
        val application: GetCrewDetailApplicationDto,
        val userInfo: GetCrewDetailUserInfoDto
    )

    @kotlinx.serialization.Serializable
    data class GetCrewDetailActivityInfoDto(
      val activity: Int,
      val hosting: Int,
      val noShow: Int
    )


    @kotlinx.serialization.Serializable
    data class GetCrewDetailApplicationDto(
      val dayOfBirth: String,
      val email: String,
      val id1365: String,
      val name: String,
      val phoneNumber: String,
      val question: String,
      val startPoint: String,
      val transportation: String,
    )


    @kotlinx.serialization.Serializable
    data class GetCrewDetailUserInfoDto(
      val nickname: String,
      val profile: String
    )


}