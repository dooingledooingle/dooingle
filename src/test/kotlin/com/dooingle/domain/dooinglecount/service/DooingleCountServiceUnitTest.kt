package com.dooingle.domain.dooinglecount.service

import com.dooingle.domain.dooingle.repository.DooingleRepository
import com.dooingle.domain.dooinglecount.repository.DooingleCountRedisRepository
import com.dooingle.domain.user.dto.DooinglerResponse
import com.dooingle.domain.user.service.SocialUserService
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import io.mockk.impl.annotations.MockK

@DisplayName("DooingleCountService 단위 테스트")
class DooingleCountServiceUnitTest : AnnotationSpec() {

    @MockK private var dooingleCountRedisRepository = mockk<DooingleCountRedisRepository>()
    @MockK private var dooingleRepository = mockk<DooingleRepository>()
    private val dooingleCountService = DooingleCountService(dooingleCountRedisRepository, dooingleRepository)

    @Test
    fun `뜨거운 뒹글러 목록을 조회하고자 하면 뜨거운 뒹글러 목록을 조회할 수 있다`() {
        // given
        val hotDooinglerList = getFixtureOfHighCountDooinglers()
        val size = SocialUserService.HOT_DOOINGLERS_SIZE
        every { dooingleCountRedisRepository.getHighCountDooinglers(size) } returns hotDooinglerList

        // when
        val returnHotDooinglerList = dooingleCountService.getHotDooinglerList(size)

        // then
        returnHotDooinglerList shouldBe hotDooinglerList.map {
            DooinglerResponse(
                userLink = it.substringBefore(":"),
                nickname = it.substringAfter(":")
            )
        }
    }

    @Test
    fun `DooingleCount를 초기화하려는 경우, DooingleCountService가 DooingleCountRepository의 deleteAllInBatch()를 호출한다`() {
        // given
        every { dooingleCountRedisRepository.deleteAll() } just Runs

        // when
        dooingleCountService.deleteAllDooingleCount()

        // then
        verify(exactly = 1) { dooingleCountRedisRepository.deleteAll() }
    }

    private fun getFixtureOfHotDooingler(): List<DooinglerResponse> {
        return listOf(
            DooinglerResponse("1111111111", "뜨거운1"),
            DooinglerResponse("2222222222", "뜨거운2"),
            DooinglerResponse("1111111111", "뜨거운3"),
            DooinglerResponse("1111111111", "뜨거운4"),
            DooinglerResponse("1111111111", "뜨거운5"),
        )
    }

    private fun getFixtureOfHighCountDooinglers(): Set<String> {
        return setOf("aaaaaaaaaa:김땡땡", "bbbbbbbbbb:박땡땡")
    }
}
