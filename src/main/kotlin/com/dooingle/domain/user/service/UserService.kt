package com.dooingle.domain.user.service

import com.dooingle.domain.dooinglecount.service.DooingleCountService
import com.dooingle.domain.user.dto.DooinglerResponse
import com.dooingle.domain.user.dto.UpdateProfileRequest
import com.dooingle.domain.user.dto.UpdateProfileResponse
import com.dooingle.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class UserService(
    private val userRepository: UserRepository,
    private val dooingleCountService: DooingleCountService
) {
    fun getDooinglerList(condition: String?): List<DooinglerResponse> {
        return when (condition) {
            "hot" -> dooingleCountService.getHotDooinglerList()
            "new" -> userRepository.getNewDooinglers()
            else -> throw IllegalArgumentException() // TODO
        }
    }

    fun updateProfile(userId: Long, request: UpdateProfileRequest, img:MultipartFile?): UpdateProfileResponse {
        //이미지 null 검사 → s3에 이미지 업로드 → DB에 url 저장 → url 반환

        img?.let {

        }

        //repository에 소개, 이미지경로 저장 후 Response dto 반환
        TODO()
    }

}
