package com.dooingle.domain.catch.service

import com.dooingle.domain.catch.dto.AddCatchRequest
import com.dooingle.domain.catch.dto.CatchResponse
import com.dooingle.domain.catch.dto.DeleteCatchRequest
import com.dooingle.domain.catch.repository.CatchRepository
import com.dooingle.domain.dooingle.repository.DooingleRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CatchService (
    private val dooingleRepository: DooingleRepository,
    private val catchRepository: CatchRepository
){
    // 캐치 생성
    fun addCatch(dooingleId: Long, addCatchRequest: AddCatchRequest): CatchResponse {
        val dooingle = dooingleRepository.findByIdOrNull(dooingleId) ?: throw Exception("") // TODO

        // 해당 dooingle 의 주인만 catch 를 등록할 수 있다
        if (dooingle.owner.id != addCatchRequest.ownerId) throw Exception("") // TODO

        // 해당 dooingle 의 주인은 하나의 catch 만 작성 가능하다
        if (dooingle.catch != null) throw Exception("")

        val catch = addCatchRequest.to(dooingle)
        dooingle.catch = catch
        catchRepository.save(catch)

        return CatchResponse.from(catch)
    }

    // 캐치 삭제
    @Transactional
    fun deleteCatch(dooingleId: Long, catchId: Long, deleteCatchRequest: DeleteCatchRequest) {
        val dooingle = dooingleRepository.findByIdOrNull(dooingleId) ?: throw Exception("") // TODO

        // 해당 dooingle 의 주인만 catch 를 삭제할 수 있다
        if (dooingle.owner.id != deleteCatchRequest.ownerId) throw Exception("") // TODO

        // soft delete
        val catch = catchRepository.findByIdOrNull(catchId) ?: throw Exception("") // TODO
        catch.updateForDelete() // Catch 의 deletedAt 에 현재 ZonedDateTime 을 대입
    }
}