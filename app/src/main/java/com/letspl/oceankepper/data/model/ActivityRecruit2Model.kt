package com.letspl.oceankepper.data.model

import com.letspl.oceankepper.data.dto.ActivityRegisterLocationDto
import java.io.File

object ActivityRecruit2Model {
    // 이미지 파일 저장 변수
    var thumbnailImgFile: File? = null
    var keeperIntroduceImgFile: File? = null
    var activityStoryImgFile: File? = null

    var thumbnailImgStr: String? = null
    var keeperIntroduceImgStr: String? = null
    var activityStoryImgStr: String? = null
}