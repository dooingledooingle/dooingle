package com.dooingle.domain.dooingle.service

import com.dooingle.domain.catch.model.Catch
import com.dooingle.domain.catch.repository.CatchRepository
import com.dooingle.domain.dooingle.dto.AddDooingleRequest
import com.dooingle.domain.dooingle.dto.DooingleAndCatchResponse
import com.dooingle.domain.dooingle.dto.DooingleResponse
import com.dooingle.domain.dooingle.model.Dooingle
import com.dooingle.domain.dooingle.repository.DooingleRepository
import com.dooingle.domain.dooinglecount.model.DooingleCount
import com.dooingle.domain.dooinglecount.repository.DooingleCountRepository
import com.dooingle.domain.user.repository.SocialUserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DooingleService (
    private val dooingleRepository: DooingleRepository,
    private val socialUserRepository: SocialUserRepository,
    private val catchRepository: CatchRepository,
    private val dooingleCountRepository: DooingleCountRepository
) {
    companion object {
        const val USER_FEED_PAGE_SIZE = 5
    }

    // 뒹글 생성
    @Transactional
    fun addDooingle(ownerId: Long, addDooingleRequest: AddDooingleRequest): DooingleResponse {
        val guest = socialUserRepository.findByIdOrNull(addDooingleRequest.guestId) ?: throw Exception("") // TODO
        val owner = socialUserRepository.findByIdOrNull(ownerId) ?: throw Exception("") // TODO
        val dooingle = addDooingleRequest.to(guest, owner)

        dooingleRepository.save(dooingle)

        val dooingleCount = dooingleCountRepository.findByOwnerId(ownerId)
            ?: dooingleCountRepository.save(DooingleCount(owner = owner))

        dooingleCount.plus()

        return DooingleResponse.from(dooingle)
    }

    // 개인 뒹글 페이지 조회(뒹글,캐치)
    fun getPage(ownerId: Long, loginUserId: Long, cursor: Long?): Slice<DooingleAndCatchResponse> {
        val owner = socialUserRepository.findByIdOrNull(ownerId) ?: throw Exception("") // TODO
        val pageRequest = PageRequest.ofSize(USER_FEED_PAGE_SIZE)

        return dooingleRepository.getPersonalPageBySlice(owner, cursor, pageRequest)
            .map {
                it.toDooingleAndCatchResponse(
                    catch = getCatchWithDooingleId(it.dooingleId)
                )
            }
    }

    // 단일 뒹글 조회(글자수 제한 정책으로 실제 사용되지는 않지만 정책수정을 통한 추가 기능의 확장성을 위해 남겨둠)
    fun getDooingle(ownerId: Long, dooingleId: Long): DooingleResponse {
        val dooingle = dooingleRepository.findByIdOrNull(dooingleId) ?: throw Exception("")

        return DooingleResponse.from(dooingle)
    }

    fun getDooingleFeeds(cursor: Long?, pageRequest: PageRequest): Slice<DooingleResponse> {
        return dooingleRepository.getDooinglesBySlice(cursor, pageRequest)
    }

    private fun getDooingle(dooingleId: Long) : Dooingle {
        return dooingleRepository.findByIdOrNull(dooingleId) ?: throw Exception("")
    }

    // dooingle 에 해당하는 catch 를 가져오는 내부 메서드
    private fun getCatchWithDooingleId(dooingleId: Long) : Catch? {
        val dooingle = getDooingle(dooingleId)
        return catchRepository.findByDooingle(dooingle)
    }

    // TODO 팔로우 기능 구현 후 구현 필요
//    fun getDooingleFeedsOfFollows(cursor: Long, pageRequest: PageRequest): Slice<DooingleResponse> {
//        return
//    }
}
