package com.letspl.oceankepper.data.model

import android.net.Uri
import com.letspl.oceankepper.data.dto.GetUserActivityListDto
import java.io.File

object MyActivityModel {
    var profileImageFile: File? = null
    var takePhotoUri: Uri? = null
    var crewLast = false
    val crewActivities = arrayListOf<GetUserActivityListDto>()
    val hostActivities = arrayListOf<GetUserActivityListDto>()
    var lastCrewActivityId: String? = null
    var hostLast = false
    var lastHostActivityId: String? = null
}