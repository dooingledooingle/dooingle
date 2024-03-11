package com.dooingle.domain.dooinglecount.model

import com.dooingle.domain.user.model.SocialUser
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.mockk.impl.annotations.MockK
import io.mockk.mockk

@DisplayName("DooingleCount 단위 테스트")
class DooingleCountUnitTest : AnnotationSpec(){

    @MockK private val userA = mockk<SocialUser>()
    @MockK private val userB = mockk<SocialUser>()

    companion object {
        private const val INITIAL_COUNT_VALUE = 10
    }

    @Test
    fun `userA의 DooingleCount의 count 값을 1 증가시키면, 1 증가한 count 값을 확인할 수 있다`() {
        // given
        val userAInitialCount = INITIAL_COUNT_VALUE
        val dooingleCountOfUserA = DooingleCount(userA, userAInitialCount)

        // when
        dooingleCountOfUserA.plus()

        // then
        dooingleCountOfUserA.count shouldBe userAInitialCount + 1
    }

    @Test
    fun `userA의 DooingleCount의 count 값을 1 증가시켜도, userB의 DooingleCount의 count 값은 증가하지 않는다`() {
        // given
        val userAInitialCount = INITIAL_COUNT_VALUE
        val userBInitialCount = INITIAL_COUNT_VALUE

        val dooingleCountOfUserA = DooingleCount(userA, userAInitialCount)
        val dooingleCountOfUserB = DooingleCount(userB, userBInitialCount)

        // when
        dooingleCountOfUserA.plus()

        // then
        dooingleCountOfUserB.count shouldBe userAInitialCount
    }
}
