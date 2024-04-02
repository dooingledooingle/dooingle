package com.dooingle.domain.notice.service

import com.dooingle.domain.notice.dto.AddNoticeRequest
import com.dooingle.domain.notice.dto.NoticeResponse
import com.dooingle.domain.notice.model.Notice
import com.dooingle.domain.notice.repository.NoticeRepository
import com.dooingle.domain.user.model.SocialUser
import com.dooingle.domain.user.model.UserRole
import com.dooingle.domain.user.repository.SocialUserRepository
import com.dooingle.global.exception.custom.NotPermittedException
import com.dooingle.global.oauth2.provider.OAuth2Provider
import com.dooingle.global.querydsl.QueryDslConfig
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
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

    @AfterEach
    fun clearData() {
        noticeRepository.deleteAll()
        socialUserRepository.deleteAll()
    }

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

    @Test
    fun `공지사항이 정상적으로 수정된 경우`(){
        noticeRepository.saveAll(noticeList)
        socialUserRepository.saveAll(userList)

        // given
        val userId = admin.id!!
        val notice = noticeList.random()
        val request = AddNoticeRequest(title = "수정된 공지사항", content = "수정된 공지입니다.")

        // when
        noticeService.updateNotice(userId, notice.id!!, request)

        // then
        notice.title shouldBe request.title
        notice.content shouldBe request.content
    }

    @Test
    fun `어드민이 아닌 유저가 수정 시도할 경우 예외 발생`(){
        // given
        noticeRepository.saveAll(noticeList)
        socialUserRepository.saveAll(userList)

        val userId = user.id!!
        val notice = noticeList.random()
        val request = AddNoticeRequest(title = "수정된 공지사항", content = "수정된 공지입니다.")

        // expected
        shouldThrow<NotPermittedException> { noticeService.updateNotice(userId, notice.id!!, request) }
    }

    @Test
    fun `공지사항이 정상적으로 삭제된 경우 딜리티드엣 업데이트`(){
        // given
        noticeRepository.saveAll(noticeList)
        socialUserRepository.saveAll(userList)

        val userId = admin.id!!
        val notice = noticeList.random()

        // when
        noticeService.deleteNotice(userId, notice.id!!)

        // then
        noticeRepository.findById(notice.id!!).get().deletedAt shouldNotBe null
    }

    @Test
    fun `어드민이 아닌 유저가 삭제 시도할 경우 예외 발생`(){
        // given
        noticeRepository.saveAll(noticeList)
        socialUserRepository.saveAll(userList)

        val userId = user.id!!
        val notice = noticeList.random()

        // expected
        shouldThrow<NotPermittedException> { noticeService.deleteNotice(userId, notice.id!!) }
    }

    @Test
    fun `전체 공지사항 리스트가 10개씩 조회되는지 확인`(){
        // given
        noticeRepository.saveAll(noticeList)
        socialUserRepository.saveAll(userList)

        val pageNumber = 1

        // when
        val page = noticeService.findAllNotices(pageNumber)

        // then
        page.size shouldBe 10
        page.hasNext() shouldBe true
        page.first().title shouldBe NoticeResponse.from(noticeList.last()).title
        page.last().title shouldBe NoticeResponse.from(noticeList[noticeList.size - 10]).title
    }

    companion object {
        private val addNoticeRequest = AddNoticeRequest(title = "공지사항 제목", content = "공지사항 내용")
    }

    private val user = SocialUser(nickname = "A", provider = OAuth2Provider.KAKAO, providerId = "1", userLink = "aaaaaaaaaa")
    private val admin = SocialUser(role = UserRole.ADMIN, nickname = "B", provider = OAuth2Provider.KAKAO, providerId = "2", userLink = "bbbbbbbbbb")
    private val userList = listOf(user, admin)

    private val noticeList:List<Notice> = listOf(
        Notice(title = "공지사항1", content = "공지입니다.", admin),
        Notice(title = "공지사항2", content = "공지입니다.", admin),
        Notice(title = "공지사항3", content = "공지입니다.", admin),
        Notice(title = "공지사항4", content = "공지입니다.", admin),
        Notice(title = "공지사항5", content = "공지입니다.", admin),
        Notice(title = "공지사항6", content = "공지입니다.", admin),
        Notice(title = "공지사항7", content = "공지입니다.", admin),
        Notice(title = "공지사항8", content = "공지입니다.", admin),
        Notice(title = "공지사항9", content = "공지입니다.", admin),
        Notice(title = "공지사항10", content = "공지입니다.", admin),
        Notice(title = "공지사항11", content = "공지입니다.", admin),
        Notice(title = "공지사항12", content = "공지입니다.", admin),
        Notice(title = "공지사항13", content = "공지입니다.", admin),
        Notice(title = "공지사항14", content = "공지입니다.", admin)
    )
}