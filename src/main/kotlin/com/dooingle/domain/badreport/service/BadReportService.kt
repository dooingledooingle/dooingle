package com.dooingle.domain.badreport.service

import com.dooingle.domain.badreport.dto.AddBadReportRequest
import com.dooingle.domain.badreport.dto.BadReportResponse
import com.dooingle.domain.badreport.dto.BlockBadReportDto
import com.dooingle.domain.badreport.model.ReportedTargetType
import com.dooingle.domain.badreport.repository.BadReportRepository
import com.dooingle.domain.user.repository.SocialUserRepository
import com.dooingle.global.aop.DistributedLock
import com.dooingle.global.exception.custom.ModelNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BadReportService(
    private val socialUserRepository: SocialUserRepository,
    private val badReportRepository: BadReportRepository,
    private val distributedLock: DistributedLock,
) {

    fun addReport(reporterId: Long, addBadReportRequest: AddBadReportRequest): Unit = distributedLock("BadReport:$reporterId")  {
        val reporter = socialUserRepository.findByIdOrNull(reporterId)
            ?: throw ModelNotFoundException(modelName = "Social User", modelId = reporterId)
        // TODO 같은 게시물 여러 번 신고 못하게 해야함 + 따닥 방지까지 고려 필요함

        addBadReportRequest.toEntity(reporter).let { badReportRepository.save(it) }
    }

    fun getBadReportPagedList(reportedTargetType: ReportedTargetType, pageRequest: PageRequest): Page<BadReportResponse>? {
        return badReportRepository.findBadReportPage(reportedTargetType, pageRequest)
    }

    @Transactional
    fun blockReportedDooingles(request: BlockBadReportDto){
        badReportRepository.updateReportedDooingles(request.reportedIdList)
    }

    @Transactional
    fun blockReportedCatches(request: BlockBadReportDto){
        badReportRepository.updateReportedCatches(request.reportedIdList)
    }
}
