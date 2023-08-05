package com.letspl.oceankepper.data.dto

@kotlinx.serialization.Serializable
data class GetComingScheduleResponse(
    val activities: List<ComingScheduleItem>
)