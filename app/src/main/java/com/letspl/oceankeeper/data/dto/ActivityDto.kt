package com.letspl.oceankeeper.data.dto


// 메인 페이지 하단 활동 dto
@kotlinx.serialization.Serializable
data class MyActivityItem(
    val activityId: String,
    val activityImageUrl: String? = "",
    val garbageCategory: String = "",
    val hostNickname: String = "",
    val location: String = "",
    val locationTag: String = "",
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
@kotlinx.serialization.Serializable
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

// 활동 등록
@kotlinx.serialization.Serializable
data class EditActivityRegisterDto(
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
    val transportation: String // 교통 안내 여부
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
    val statusCode: Int,
    val timestamp: String,
    val response: ActivityRegisterResponseDto
)
@kotlinx.serialization.Serializable
data class ActivityRegisterResponseDto(
    val activityId: String
)

// 요청자가 호스트인 활동의 활동명 불러오기
@kotlinx.serialization.Serializable
data class GetActivityRecruitmentActivityNameResultDto(
    val statusCode: Int,
    val timestamp: String,
    val response: GetActivityRecruitmentActivityNameResponseDto
)
@kotlinx.serialization.Serializable
data class GetActivityRecruitmentActivityNameResponseDto(
    val hostActivities: List<GetActivityRecruitmentActivityNameList>
)
@kotlinx.serialization.Serializable
data class GetActivityRecruitmentActivityNameList(
    val activityId: String,
    val title: String
)

// 요청자가 호스트인 활동의 크루원 닉네임 불러오기
@kotlinx.serialization.Serializable
data class GetActivityRecruitmentCrewNameResultDto(
    val statusCode: Int,
    val timestamp: String,
    val response: GetActivityRecruitmentCrewNameResponseDto
)
@kotlinx.serialization.Serializable
data class GetActivityRecruitmentCrewNameResponseDto(
    val activityId: String,
    val activityTitle: String,
    val crewInformationList: List<GetActivityRecruitmentCrewNameList> = listOf()
)
@kotlinx.serialization.Serializable
data class GetActivityRecruitmentCrewNameList(
    val nickname: String = ""
)

// 요청자가 호스트인 활동의 크루원 닉네임 불러오기
@kotlinx.serialization.Serializable
data class PostSendMessageResultDto(
    val statusCode: Int,
    val timestamp: String,
    val response: PostSendMessageResponseDto
)

// 요청자가 호스트인 활동의 크루원 닉네임 불러오기
@kotlinx.serialization.Serializable
data class PostSendMessageResponseDto(
    val messageId: List<Int>
)


// 요청자가 호스트인 활동의 크루원 닉네임 불러오기
@kotlinx.serialization.Serializable
data class GetActivityInfoDto(
    val statusCode: Int,
    val timestamp: String,
    val response: GetActivityInfoResponseDto
)
@kotlinx.serialization.Serializable
data class GetActivityInfoResponseDto(
    val activity: Int,
    val hosting: Int,
    val noShow: Int
)

// 내 활동 조회
@kotlinx.serialization.Serializable
data class GetUserActivityDto(
    val statusCode: Int,
    val timestamp: String,
    val response: GetUserActivityResponseDto
)

// 내 활동 조회
@kotlinx.serialization.Serializable
data class GetUserActivityResponseDto(
    val activities: List<GetUserActivityListDto>,
    val meta: MetaInfo
)

// 내 활동 조회
@kotlinx.serialization.Serializable
data class GetUserActivityListDto(
    val activityId: String,
    val activityImageUrl: String? = "",
    val address: String,
    val applicationId: String,
    val crewStatus: String,
    val hostNickname: String,
    val participants: Int,
    val quota: Int,
    val recruitEndAt: String,
    val recruitStartAt: String,
    val rejectReason: String,
    val role: String,
    val startAt: String,
    val status: String,
    val title: String
)

// 활동 지원 취소
@kotlinx.serialization.Serializable
data class DeleteApplyDto(
    val statusCode: Int,
    val timestamp: String,
    val response: String
)

// 모집 취소
@kotlinx.serialization.Serializable
data class DeleteRecruitmentDto(
    val statusCode: Int,
    val timestamp: String,
    val response: String
)