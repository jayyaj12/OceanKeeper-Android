package com.letspl.oceankeeper.data.model

import com.letspl.oceankeeper.data.dto.LoginUserResponseDto
import com.letspl.oceankeeper.data.dto.LoginUserResponseTokenDto
import com.letspl.oceankeeper.data.dto.LoginUserResponseUserDto

object UserModel {
    var userInfo: LoginUserResponseDto = LoginUserResponseDto(LoginUserResponseTokenDto("", "", "", ""), LoginUserResponseUserDto("", "", ""))
}