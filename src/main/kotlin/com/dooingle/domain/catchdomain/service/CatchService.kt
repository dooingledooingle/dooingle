package com.dooingle.domain.catchdomain.service

import com.dooingle.domain.catchdomain.dto.AddCatchRequest
import com.dooingle.domain.catchdomain.dto.CatchResponse
import com.dooingle.domain.catchdomain.dto.DeleteCatchRequest
import com.dooingle.domain.catchdomain.repository.CatchRepository
import com.dooingle.domain.dooingle.repository.DooingleRepository
import com.dooingle.domain.notification.service.NotificationService
import com.dooingle.global.exception.custom.ConflictStateException
import com.dooingle.global.exception.custom.ModelNotFoundException
import com.dooingle.global.exception.custom.NotPermittedException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CatchService(
    private val dooingleRepository: DooingleRepository,
    private val catchRepository: CatchRepository,
    private val notificationService: NotificationService
) {
    // 캐치 생성
    fun addCatch(dooingleId: Long, addCatchRequest: AddCatchRequest): CatchResponse {
        val dooingle = dooingleRepository.findByIdOrNull(dooingleId)
            ?: throw ModelNotFoundException(modelName = "Dooingle", modelId = dooingleId)

        // 해당 dooingle 의 주인만 catch 를 등록할 수 있다
        if (dooingle.owner.id != addCatchRequest.ownerId) {
            throw NotPermittedException(
                userId = addCatchRequest.ownerId,
                modelName = "Dooingle", modelId = dooingleId)
        }

        // 해당 dooingle 의 주인은 하나의 catch 만 작성 가능하다
        if (dooingle.catch != null) {
            throw ConflictStateException("이미 캐치를 등록했습니다.")
        }

        val catch = addCatchRequest.to(dooingle)

        dooingle.catch = catch
        catchRepository.save(catch)

        notificationService.addCatchNotification(user = dooingle.guest, dooingleId = dooingleId)

        return CatchResponse.from(catch)
    }

    // 캐치 삭제
    @Transactional
    fun deleteCatch(dooingleId: Long, catchId: Long, deleteCatchRequest: DeleteCatchRequest) {
        val dooingle = dooingleRepository.findByIdOrNull(dooingleId)
            ?: throw ModelNotFoundException(modelName = "Dooingle", modelId = dooingleId)

        // 해당 dooingle 의 주인만 catch 를 삭제할 수 있다
        if (dooingle.owner.id != deleteCatchRequest.ownerId) {
            throw NotPermittedException(
                userId = deleteCatchRequest.ownerId,
                modelName = "Dooingle", modelId = dooingleId)
        }

        // soft delete
        val catch = catchRepository.findByIdOrNull(catchId)
            ?: throw ModelNotFoundException(modelName = "캐치", modelId = catchId)
        catch.updateForDelete() // Catch 의 deletedAt 에 현재 ZonedDateTime 을 대입
    }
}