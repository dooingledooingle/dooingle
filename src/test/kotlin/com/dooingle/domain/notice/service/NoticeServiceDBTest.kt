package com.dooingle.domain.notice.service

import com.dooingle.domain.notice.dto.AddNoticeRequest
import com.dooingle.domain.notice.repository.NoticeRepository
import com.dooingle.domain.user.model.SocialUser
import com.dooingle.domain.user.model.UserRole
import com.dooingle.domain.user.repository.SocialUserRepository
import com.dooingle.global.exception.custom.NotPermittedException
import com.dooingle.global.oauth2.provider.OAuth2Provider
import com.dooingle.global.querydsl.QueryDslConfig
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(value = [QueryDslConfig::class])
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ActiveProfiles("test")
class NoticeServiceDBTest(
    private val socialUserRepository: SocialUserRepository,
    private val noticeRepository: NoticeRepository
) {

    private val noticeService = NoticeService(socialUserRepository, noticeRepository)

    @Test
    fun `ADMIN일 경우 공지사항 등록 가능`() {
        // GIVEN
        val admin = socialUserRepository.save(admin)
        val request = addNoticeRequest

        // WHEN
        val noticeId = noticeService.addNotice(admin.id!!, request)

        // THEN
        val notice = noticeRepository.findByIdOrNull(noticeId)
        notice shouldNotBe null
        notice!!.title shouldBe request.title
        notice.content shouldBe request.content
    }

    @Test
    fun `ADMIN이 아닌 경우 공지사항 등록 시 예외 발생`() {
        // GIVEN
        val user = socialUserRepository.save(user)
        val request = addNoticeRequest

        // WHEN & THEN
        shouldThrow<NotPermittedException> { noticeService.addNotice(user.id!!, request) }
    }

    private val user = SocialUser(nickname = "A", provider = OAuth2Provider.KAKAO, providerId = "1", userLink = "aaaaaaaaaa")
    private val admin = SocialUser(role = UserRole.ADMIN, nickname = "B", provider = OAuth2Provider.KAKAO, providerId = "2", userLink = "bbbbbbbbbb")

    companion object {
        private val addNoticeRequest = AddNoticeRequest(title = "공지사항 제목", content = "공지사항 내용")
    }

}