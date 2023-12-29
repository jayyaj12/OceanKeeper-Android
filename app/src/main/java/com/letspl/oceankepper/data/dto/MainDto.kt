package com.letspl.oceankepper.data.dto

import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable
data class GetComingScheduleResponse(
    val response: GetComingScheduleItem
)

@kotlinx.serialization.Serializable
data class GetComingScheduleItem(
    val activities: List<ComingScheduleItem>
)

@kotlinx.serialization.Serializable
data class GetMyActivityResponse(
    val response: GetMyActivityItem
)

@kotlinx.serialization.Serializable
data class GetMyActivityItem(
    val activities: List<MyActivityItem>,
    val meta: GetMyActivityMeta
)

@kotlinx.serialization.Serializable
data class GetMyActivityMeta(
    val last: Boolean,
    val size: Int
)

@Serializable
data class GetMyActivityDetailResponse(
    val response: GetMyActivityDetailItem
)

@kotlinx.serialization.Serializable
data class GetMyActivityDetailItem(
    val activityStatus: String,
    val activityStory: String,
    val etc: String,
    val garbageCategory: String,
    val hostImageUrl: String,
    val hostNickName: String,
    val keeperImageUrl: String?,
    val keeperIntroduction: String,
    val location: GetMyActivityDetailLocation,
    val locationTag: String,
    val participants: Int,
    val preparation: String,
    val programDetails: String,
    val quota: Int,
    val recruitEndAt: String,
    val recruitStartAt: String,
    val rewards: String = "",
    val startAt: String,
    val storyImageUrl: String?,
    val thumbnailUrl: String?,
    val title: String,
    val transportation: String
)
@Serializable
data class GetMyActivityDetailLocation(
    val address: String,
    val latitude: Double,
    val longitude: Double
)