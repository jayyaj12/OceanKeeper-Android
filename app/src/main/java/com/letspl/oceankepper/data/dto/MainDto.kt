package com.letspl.oceankepper.data.dto

@kotlinx.serialization.Serializable
data class GetComingScheduleResponse(
    val response: GetComingScheduleItem
)

@kotlinx.serialization.Serializable
data class GetComingScheduleItem(
    val activities: List<ComingScheduleItem>
)
