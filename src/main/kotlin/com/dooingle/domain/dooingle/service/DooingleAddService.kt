package com.dooingle.domain.dooingle.service

import com.dooingle.domain.dooingle.dto.AddDooingleRequest
import com.dooingle.domain.dooingle.dto.DooingleResponse
import com.dooingle.domain.dooingle.repository.DooingleRepository
import com.dooingle.domain.dooinglecount.model.DooingleCount
import com.dooingle.domain.dooinglecount.repository.DooingleCountRepository
import com.dooingle.domain.notification.service.NotificationService
import com.dooingle.domain.user.repository.SocialUserRepository
import com.dooingle.global.exception.custom.InvalidParameterException
import com.dooingle.global.exception.custom.ModelNotFoundException
import com.dooingle.global.exception.custom.SocialUserNotFoundByUserLinkException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DooingleAddService(
    private val dooingleRepository: DooingleRepository,
    private val socialUserRepository: SocialUserRepository,
    private val dooingleCountRepository: DooingleCountRepository,
    private val notificationService: NotificationService,
) {
    // 뒹글 생성
    @Transactional
    fun addDooingle(
        fromUserId: Long,
        ownerUserLink: String,
        addDooingleRequest: AddDooingleRequest
    ): DooingleResponse {
        val guest = socialUserRepository.findByIdOrNull(fromUserId)
            ?: throw ModelNotFoundException(modelName = "Social User", modelId = fromUserId)
        val owner = socialUserRepository.findByUserLink(ownerUserLink)
            ?: throw SocialUserNotFoundByUserLinkException(userLink = ownerUserLink)
        val dooingle = addDooingleRequest.to(guest, owner)

        if (guest.id == owner.id) throw InvalidParameterException("내 뒹글 페이지에 뒹글을 남길 수 없습니다.")

        dooingleRepository.save(dooingle)

        val dooingleCount = dooingleCountRepository.findByOwnerId(owner.id!!)
            ?: dooingleCountRepository.save(DooingleCount(owner = owner)).also { println("===새로운 DooingleCount 생성!===") }

        dooingleCount.plus()
        dooingleCountRepository.save(dooingleCount)

        notificationService.addDooingleNotification(user = owner, dooingleResponse = DooingleResponse.from(dooingle))

        println("===DooingleService addDooingle() 리턴 직전!===")
        return DooingleResponse.from(dooingle)
    }

}