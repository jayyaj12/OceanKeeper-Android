package com.letspl.oceankepper.data.model

import com.letspl.oceankepper.data.dto.LoginUserResponseDto
import com.letspl.oceankepper.data.dto.LoginUserResponseTokenDto
import com.letspl.oceankepper.data.dto.LoginUserResponseUserDto

object UserModel {
    var userInfo: LoginUserResponseDto = LoginUserResponseDto(LoginUserResponseTokenDto("", "", "", ""), LoginUserResponseUserDto("", ""))
}