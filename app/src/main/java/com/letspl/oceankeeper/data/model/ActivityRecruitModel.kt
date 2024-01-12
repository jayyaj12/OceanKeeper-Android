package com.letspl.oceankeeper.data.model

import com.letspl.oceankeeper.data.dto.ActivityRegisterLocationDto

object ActivityRecruitModel {
    var isLoadTempData = false // 임시저장 활성화 여부
    var isGiveReward = false // 참여 키퍼 리워드 여부
    var projectName = "" // 프로젝트 이름
    var quota : Int? = null // 모집 정원
    var trafficGuide = 0 // 교통 정보
    var recruitCategory = 1 // 모집 카테고리
    var recruitLocation = 1 // 모집 위치
    var location: ActivityRegisterLocationDto = ActivityRegisterLocationDto() // 활동 위치
    var guideActivity = "" // 활동 프로그램 안내
    var material = "" // 준비물
    var giveReward = "" // 제공 리워드
    var otherGuide = "" // 기타 안내 사항

    // 모집 시작일 날짜 변수
    var recruitStartNowYear = 0
    var recruitStartNowMonth = 0
    var recruitStartNowDate = 0
    var recruitStartClickedDate = ""

    // 모집 종료일 날짜 변수
    var recruitEndNowYear = 0
    var recruitEndNowMonth = 0
    var recruitEndNowDate = 0
    var recruitEndClickedDate = ""

    // 활동 시작일 날짜 변수
    var activityStartNowYear = 0
    var activityStartNowMonth = 0
    var activityStartNowDate = 0
    var activityStartTimeHour: Int? = null
    var activityStartTimeMinute: Int? = null
    var activityStartClickedDate = ""

    // 캘린더 클릭 포지션 저장 변수
    var recruitStartClickedPosition = -1
    var recruitEndClickedPosition = -1
    var activityStartClickedPosition = -1
}