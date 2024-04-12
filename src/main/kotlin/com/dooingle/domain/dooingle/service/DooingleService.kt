package com.dooingle.domain.dooingle.service

import com.dooingle.domain.catchdomain.model.Catch
import com.dooingle.domain.catchdomain.repository.CatchRepository
import com.dooingle.domain.dooingle.dto.AddDooingleRequest
import com.dooingle.domain.dooingle.dto.DooingleAndCatchResponse
import com.dooingle.domain.dooingle.dto.DooingleFeedResponse
import com.dooingle.domain.dooingle.dto.DooingleResponse
import com.dooingle.domain.dooingle.model.Dooingle
import com.dooingle.domain.dooingle.repository.DooingleRepository
import com.dooingle.domain.dooinglecount.service.DooingleCountService
import com.dooingle.domain.notification.service.NotificationService
import com.dooingle.domain.user.repository.SocialUserRepository
import com.dooingle.global.exception.custom.InvalidParameterException
import com.dooingle.global.exception.custom.ModelNotFoundException
import com.dooingle.global.exception.custom.SocialUserNotFoundByUserLinkException
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DooingleService(
    private val dooingleRepository: DooingleRepository,
    private val socialUserRepository: SocialUserRepository,
    private val catchRepository: CatchRepository,
    private val dooingleCountService: DooingleCountService,
    private val notificationService: NotificationService
) {
    companion object {
        const val USER_FEED_PAGE_SIZE = 10
    }

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
        if (guest.id == owner.id) throw InvalidParameterException("내 뒹글 페이지에 뒹글을 남길 수 없습니다.")

        val dooingle = dooingleRepository.save(addDooingleRequest.to(guest, owner))
        val dooingleResponse = DooingleResponse.from(dooingle)
        dooingleCountService.plusCount(owner)
        notificationService.addDooingleNotification(user = owner, dooingleResponse = dooingleResponse)

        return dooingleResponse
    }

    // 개인 뒹글 페이지 조회(뒹글,캐치)
    fun getPage(ownerUserLink: String, cursor: Long?): Slice<DooingleAndCatchResponse> {
        val owner = socialUserRepository.findByUserLink(ownerUserLink)
            ?: throw SocialUserNotFoundByUserLinkException(userLink = ownerUserLink)
        val pageRequest = PageRequest.ofSize(USER_FEED_PAGE_SIZE)

        val personalPageBySlice = dooingleRepository.getPersonalPageBySlice(owner, cursor, pageRequest)

        return personalPageBySlice.map { getResponseWithContentHided(it) }
    }

    private fun getResponseWithContentHided(dooingleAndCatchResponse: DooingleAndCatchResponse): DooingleAndCatchResponse {
        val catchResponse = dooingleAndCatchResponse.catch
        val newCatchResponse = catchResponse?.takeIf { it.deletedAt != null || it.blockedAt != null }?.copy(content = "차단된 캐치입니다.")

        return if (dooingleAndCatchResponse.blockedAt == null) {
            if (newCatchResponse == null) dooingleAndCatchResponse else dooingleAndCatchResponse.copy(catch = newCatchResponse)
        } else {
            dooingleAndCatchResponse.copy(content = null, catch = newCatchResponse ?: catchResponse)
        }
    }

    private fun getResponseWithCatchContentHided(dooingleAndCatchResponse: DooingleAndCatchResponse): DooingleAndCatchResponse{
        return if (dooingleAndCatchResponse.catch?.deletedAt == null) {
            // catch가 null이거나 catch의 deletedAt이 null인 경우 map 빠른 종료
            dooingleAndCatchResponse
        } else {
            // deletedAt이 null이 아닌 경우에 대해서 CatchResponse의 content를 숨기는 로직 진행
            val contentHidedCatch = dooingleAndCatchResponse.catch.copy(content = null)
            dooingleAndCatchResponse.copy(catch = contentHidedCatch)
        }
    }

    fun getDooingleFeed(cursor: Long?, pageRequest: PageRequest): Slice<DooingleFeedResponse> {
        return dooingleRepository.getDooinglesBySlice(cursor, pageRequest).map { hideIfBlocked(it) }
    }

    fun getDooingleFeedOfFollowing(userId: Long, cursor: Long?, pageRequest: PageRequest): Slice<DooingleFeedResponse> {
        val user = socialUserRepository.findByIdOrNull(userId)
            ?: throw ModelNotFoundException(modelName = "Social User", modelId = userId)

        return dooingleRepository.getDooinglesFollowingBySlice(userId, cursor, pageRequest).map { hideIfBlocked(it) }
    }

    private fun hideIfBlocked(dooingleFeedResponse: DooingleFeedResponse): DooingleFeedResponse {
        return if (dooingleFeedResponse.blockedAt == null) {
            dooingleFeedResponse
        } else {
            dooingleFeedResponse.copy(content = null)
        }
    }

    // 단일 뒹글 조회(글자수 제한 정책으로 실제 사용되지는 않지만 정책수정을 통한 추가 기능의 확장성을 위해 남겨둠)
    fun getDooingle(userId: Long, dooingleId: Long): DooingleResponse {
        val dooingle = dooingleRepository.findByIdOrNull(dooingleId)
            ?: throw ModelNotFoundException(modelName = "Dooingle", modelId = dooingleId)

        return DooingleResponse.from(dooingle)
    }

    private fun getDooingle(dooingleId: Long): Dooingle {
        return dooingleRepository.findByIdOrNull(dooingleId)
            ?: throw ModelNotFoundException(modelName = "Dooingle", modelId = dooingleId)
    }

    // dooingle 에 해당하는 catch 를 가져오는 내부 메서드
    private fun getCatchWithDooingleId(dooingleId: Long): Catch? {
        val dooingle = getDooingle(dooingleId)
        return catchRepository.findByDooingle(dooingle)
    }

}
