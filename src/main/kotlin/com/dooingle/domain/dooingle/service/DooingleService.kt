package com.dooingle.domain.dooingle.service

import com.dooingle.domain.dooingle.dto.AddDooingleRequest
import com.dooingle.domain.dooingle.dto.DooingleResponse
import com.dooingle.domain.dooingle.repository.DooingleRepository
import com.dooingle.domain.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service

class DooingleService (
    private val dooingleRepository: DooingleRepository,
    private val userRepository: UserRepository
) {

    // 뒹글 생성
    fun addDooingle(ownerId: Long, addDooingleRequest: AddDooingleRequest): DooingleResponse {
        val guest = userRepository.findByIdOrNull(addDooingleRequest.guestId) ?: throw Exception("") // TODO
        val owner = userRepository.findByIdOrNull(ownerId) ?: throw Exception("") // TODO
        val dooingle = addDooingleRequest.to(guest, owner)

        dooingleRepository.save(dooingle)

        return DooingleResponse.from(dooingle)
    }

    // 단일 뒹글 조회(글자수 제한 정책으로 실제 사용되지는 않지만 정책수정을 통한 추가 기능의 확장성을 위해 남겨둠)
    fun getDooingle(ownerId: Long, dooingleId: Long): DooingleResponse {
        val dooingle = dooingleRepository.findByIdOrNull(dooingleId) ?: throw Exception("")

        return DooingleResponse.from(dooingle)
    }
}