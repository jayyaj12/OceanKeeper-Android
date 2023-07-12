package com.letspl.oceankepper.data.dto

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

@kotlinx.serialization.Serializable
data class ActivityRegisterResultDto(
    val activityId: String
)