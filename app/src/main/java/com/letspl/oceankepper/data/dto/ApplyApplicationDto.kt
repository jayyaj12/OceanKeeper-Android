package com.letspl.oceankepper.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ApplyApplicationDto(
    val status: Int,
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
    val userId: String,

)
