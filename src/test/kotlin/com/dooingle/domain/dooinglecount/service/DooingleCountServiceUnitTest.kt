package com.dooingle.domain.dooinglecount.service

import com.dooingle.domain.dooinglecount.repository.DooingleCountRepository
import com.dooingle.domain.user.dto.DooinglerResponse
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import io.mockk.impl.annotations.MockK

@DisplayName("DooingleCountService 단위 테스트")
class DooingleCountServiceUnitTest : AnnotationSpec() {

    @MockK private var dooingleCountRepository = mockk<DooingleCountRepository>()
    private val dooingleCountService = DooingleCountService(dooingleCountRepository)

    @Test
    fun `뜨거운 뒹글러 목록을 조회하고자 하면 뜨거운 뒹글러 목록을 조회할 수 있다`() {
        // given
        val hotDooinglerList = getFixtureOfHotDooingler()
        every { dooingleCountRepository.getHighCountDooinglers() } returns hotDooinglerList

        // when
        val returnHotDooinglerList = dooingleCountService.getHotDooinglerList()

        // then
        returnHotDooinglerList shouldBe hotDooinglerList
    }

    @Test
    fun `DooingleCount를 초기화하려는 경우, DooingleCountService가 DooingleCountRepository의 deleteAllInBatch()를 호출한다`() {
        // given
        every { dooingleCountRepository.deleteAllInBatch() } just Runs

        // when
        dooingleCountService.deleteAllDooingleCount()

        // then
        verify(exactly = 1) { dooingleCountRepository.deleteAllInBatch() }
    }

    private fun getFixtureOfHotDooingler(): List<DooinglerResponse> {
        return listOf(
            DooinglerResponse("1111111111", "뜨거운1"),
            DooinglerResponse("1111111111", "뜨거운2"),
            DooinglerResponse("1111111111", "뜨거운3"),
            DooinglerResponse("1111111111", "뜨거운4"),
            DooinglerResponse("1111111111", "뜨거운5"),
        )
    }
}
