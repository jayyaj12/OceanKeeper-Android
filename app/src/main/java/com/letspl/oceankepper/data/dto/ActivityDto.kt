package com.letspl.oceankepper.data.dto

import com.google.gson.annotations.SerializedName


// 메인 페이지 하단 활동 dto
@kotlinx.serialization.Serializable
data class MyActivityItem(
    val activityId: String,
    val activityImageUrl: String? = "",
    val garbageCategory: String,
    val hostNickname: String,
    val location: String,
    val locationTag: String,
    val participants: Int,
    val quota: Int,
    val recruitEndAt: String,
    val recruitStartAt: String,
    val startAt: String,
    val title: String
)

// 메인 페이지 자동 슬라이드 다가오는 일정 영역 아이템 dto
@kotlinx.serialization.Serializable
data class ComingScheduleItem(
    val dday: Int,
    val id: String,
    val title: String,
    val startDay: String,
    val location: String
)

// 메인 페이지 활동 아이템 dto
data class ActivityInfo(
    val thumbnailUrl: String,
    val nickname: String,
    val title: String,
    val location: String,
    val participants: Int,
    val quota: Int,
    val period: String,
    val time: String,
)
// 활동 조회
data class SelectActivity(
    val activities: List<ActivitiesInfo>,
    val meta: MetaInfo
)

// [활동 조회] ActivitiesInfo 내부 data class
data class ActivitiesInfo(
    val activityId: String,
    val activityImageUrl: String,
    val garbageCategory: String,
    val hostNickname: String,
    val locationTag: String,
    val participants: Int,
    val quota: Int,
    val recruitEndAt: String,
    val recruitStartAt: String,
    val startAt: String,
    val title: String
)

// [활동 조회] meta 내부 data class
data class MetaInfo(
    val last: Boolean,
    val size: Int
)

// 활동 등록
@kotlinx.serialization.Serializable
data class ActivityRegisterDto(
    val activityStory: String, // 활동 story 텍스트
    val etc: String, // 기타 안내 사항
    val garbageCategory: String, // 모집 카테고리 선택
    val keeperImageUrl: String?, // 키퍼소개 이미지 url
    val keeperIntroduction: String, // 키퍼 소개 텍스트
    val location: ActivityRegisterLocationDto, // address: 주소, latitude: 위도, longitude: 경도
    val locationTag: String, // 모집 지역 선택
    val preparation: String, // 준비물
    val programDetails: String, // 활동 프로그램 안내
    val quota: Int, // 모집 정원
    val recruitEndAt: String, // 모집 종료일자 yyyy-MM-ddThh:mm:ss 형식
    val recruitStartAt: String, // 모집 tlwkr일자 yyyy-MM-ddThh:mm:ss 형식
    val rewards: String, // 제공 리워드
    val startAt: String, // 시작 시간 yyyy-MM-ddThh:mm:ss 형식
    val storyImageUrl: String?, // 활동 스토리 이미지 url
    val thumbnailUrl: String?, // 썸네일 이미지 url
    val title: String, // 프로젝트 이름
    val transportation: String, // 교통 안내 여부
    val userId: String // 유저 ID
)

// 활동 등록 API Location dto
@kotlinx.serialization.Serializable
data class ActivityRegisterLocationDto(
    var address: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
)

// 활동 등록 API 결과값
@kotlinx.serialization.Serializable
data class ActivityRegisterResultDto(
    val activityId: String
)