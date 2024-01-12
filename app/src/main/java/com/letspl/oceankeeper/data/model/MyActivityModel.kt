package com.letspl.oceankeeper.data.model

import android.net.Uri
import com.letspl.oceankeeper.data.dto.GetUserActivityListDto
import java.io.File

object MyActivityModel {
    var profileImageFile: File? = null
    var takePhotoUri: Uri? = null
    var crewLast = false
    val crewActivities = arrayListOf<GetUserActivityListDto>()
    val hostActivities = arrayListOf<GetUserActivityListDto>()
    // 신청자 관리 엑셀 다운로드 시 파일명으로 프로젝트 명을 사용
    lateinit var clickItem: GetUserActivityListDto
    var lastCrewActivityId: String? = null
    var hostLast = false
    var lastHostActivityId: String? = null
}