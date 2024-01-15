package com.letspl.oceankeeper.data.model

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
        val activityInfo: GetCrewInfoListActivityInfoResponse,
        val crewInfo: List<GetCrewInfoListDto>
    )

    @kotlinx.serialization.Serializable
    data class GetCrewInfoListActivityInfoResponse(
        val activityId: String,
        val activityStatus: String = ""
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
      val dayOfBirth: String? = "",
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

    data class GetExcelDownloadUrlResponseDto(
        val byteArray: String,
        val description: String,
        val file: GetExcelDownloadUrlFileDto,
        val filename: String,
        val inputStream: String? = null,
        val open: Boolean,
        val readable: Boolean,
        val uri: GetExcelDownloadUriFileDto,
        val url: GetExcelDownloadUrlDto
    )

    data class GetExcelDownloadUrlFileDto(
        val absolute: Boolean,
        val absolutePath: String,
        val canonicalPath: String,
        val directory: Boolean,
        val executable: Boolean,
        val file: Boolean,
        val freeSpace: Int,
        val hidden: Boolean,
        val lastModified: Int,
        val name: String,
        val parent: String,
        val path: String,
        val readable: Boolean,
        val totalSpace: Int,
        val usableSpace: Int,
        val writable: Boolean
    )

    data class GetExcelDownloadUriFileDto(
        val absolute: Boolean,
        val authority: String,
        val fragment: String,
        val host: String,
        val opaque: Boolean,
        val path: String,
        val port: Int,
        val query: String,
        val rawAuthority: String,
        val rawFragment: String,
        val rawPath: String,
        val rawQuery: String,
        val rawSchemeSpecificPart: String,
        val rawUserInfo: String,
        val scheme: String,
        val schemeSpecificPart: String,
        val userInfo: String
    )

    data class GetExcelDownloadUrlDto(
        val authority: String,
        val content: String? = null,
        val defaultPort: Int,
        val file: String,
        val host: String,
        val path: String,
        val port: Int,
        val protocol: String,
        val query: String,
        val ref: String,
        val userInfo: String
    )


}