package com.letspl.oceankepper.data.repository

import com.letspl.oceankepper.data.model.ManageApplyMemberModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

interface ManageApplyRepository {
    // 크루원 불러오기
    suspend fun getCrewInfoList(activityId: String): Response<ManageApplyMemberModel.GetCrewInfoListResponseDto>
    // 크루원 정보 불러오기
    suspend fun getCrewDetail(applicationId: String): Response<ManageApplyMemberModel.GetCrewDetailResponseDto>
    // 크루원 승인 설정
    suspend fun postCrewStatus(body: ManageApplyMemberModel.PostCrewStatusBody): Response<ManageApplyMemberModel.PostCrewStatusDto>
    // 크루원 엑셀 다운로드 url 가져오기
    suspend fun getCrewInfoFileDownloadUrl(activityId: String): Response<ResponseBody>
}