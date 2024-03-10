package com.dooingle.domain.badreport.controller

import com.dooingle.domain.badreport.dto.AddBadReportRequest
import com.dooingle.domain.badreport.dto.BadReportResponse
import com.dooingle.domain.badreport.service.BadReportService
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/badreports")
class BadReportController(
    private val badReportService: BadReportService
) {

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
    fun getBadReportList(): ResponseEntity<Page<BadReportResponse>> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(badReportService.getReportList())
    }
}
