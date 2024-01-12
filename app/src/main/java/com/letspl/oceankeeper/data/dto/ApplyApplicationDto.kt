package com.letspl.oceankeeper.data.dto

import kotlinx.serialization.Serializable

// 활동 지원하기
@Serializable
data class ApplyApplicationDto(
    val statusCode: Int,
    val timestamp: String,
    val response: ApplyApplicationResponse
)

@Serializable
data class ApplyApplicationResponse(
    val activityId: String,
    val applicationId: String
)

@Serializable
data class PostApplyApplicationBody(
    val activityId: String,
    val dayOfBirth: String,
    val email: String,
    val id1365: String,
    val name: String,
    val phoneNumber: String,
    val privacyAgreement: Boolean,
    val question: String,
    val startPoint: String,
    val transportation: String,
    val userId: String
)

// 특정 활성 지원서 보기
@Serializable
data class GetApplicationDetailDto(
    val statusCode: Int,
    val timestamp: String,
    val response: GetApplicationDetailResponse
)

@Serializable
data class GetApplicationDetailResponse(
    val dayOfBirth: Long,
    val email: String,
    val id1365: String,
    val name: String,
    val phoneNumber: String,
    val question: String,
    val startPoint: String,
    val transportation: String
)


@Serializable
data class PatchApplyApplicationBody(
    val dayOfBirth: Long,
    val email: String,
    val id1365: String,
    val name: String,
    val privacyAgreement: Boolean,
    val phoneNumber: String,
    val question: String,
    val startPoint: String,
    val transportation: String
)

// 활동 지원서 수정
@Serializable
data class PatchApplicationDto(
    val statusCode: Int,
    val timestamp: String,
    val response: String
)