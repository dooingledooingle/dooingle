package com.dooingle.domain.badreport.controller

import com.dooingle.domain.badreport.dto.AddBadReportRequest
import com.dooingle.domain.badreport.dto.BadReportResponse
import com.dooingle.domain.badreport.model.ReportedTargetType
import com.dooingle.domain.badreport.service.BadReportService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/badreports")
class BadReportController(
    private val badReportService: BadReportService
) {

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    fun addBadReport(
        @RequestBody addBadReportRequest: AddBadReportRequest,
    ): ResponseEntity<Unit> {
        badReportService.addReport()
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun getBadReportPagedList(
        @RequestParam reportedTargetType: ReportedTargetType,
        @RequestParam(defaultValue = "1") page: Int,
    ): ResponseEntity<Page<BadReportResponse>> {
        val pageRequest = PageRequest.of(page, DEFAULT_PAGE_SIZE)

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(badReportService.getBadReportPagedList(reportedTargetType, pageRequest))
    }
}
