package com.dooingle.domain.badreport.service

import com.dooingle.domain.badreport.dto.AddBadReportRequest
import com.dooingle.domain.badreport.dto.BadReportResponse
import com.dooingle.domain.badreport.model.ReportedTargetType
import com.dooingle.domain.badreport.repository.BadReportRepository
import com.dooingle.domain.user.repository.SocialUserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class BadReportService(
    private val socialUserRepository: SocialUserRepository,
    private val badReportRepository: BadReportRepository,
) {

    fun addReport(reporterId: Long, addBadReportRequest: AddBadReportRequest) {
        val reporter = socialUserRepository.findByIdOrNull(reporterId) ?: throw IllegalStateException("존재하지 않는 사용자입니다.") // TODO 예외처리 공통화
        // TODO 같은 게시물 여러 번 신고 못하게 해야함 + 따닥 방지까지 고려 필요함

        addBadReportRequest.toEntity(reporter).let { badReportRepository.save(it) }
    }

    fun getBadReportPagedList(reportedTargetType: ReportedTargetType, pageRequest: PageRequest): Page<BadReportResponse>? {
        return badReportRepository.findBadReportPage(reportedTargetType, pageRequest)
    }
}