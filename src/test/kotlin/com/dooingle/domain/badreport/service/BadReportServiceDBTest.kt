package com.dooingle.domain.badreport.service

import com.dooingle.domain.badreport.dto.BlockBadReportDto
import com.dooingle.domain.badreport.repository.BadReportRepository
import com.dooingle.domain.catchdomain.repository.CatchRepository
import com.dooingle.domain.dooingle.model.Dooingle
import com.dooingle.domain.dooingle.repository.DooingleRepository
import com.dooingle.domain.user.model.SocialUser
import com.dooingle.domain.user.repository.SocialUserRepository
import com.dooingle.global.oauth2.provider.OAuth2Provider
import com.dooingle.global.property.DooinglersProperties
import com.dooingle.global.querydsl.QueryDslConfig
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.time.ZonedDateTime

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(value = [QueryDslConfig::class])
@ActiveProfiles("test")
class BadReportServiceDBTest @Autowired constructor(
    private val badReportRepository: BadReportRepository,
    private val socialUserRepository: SocialUserRepository,
    private val dooingleRepository: DooingleRepository,
    private val catchRepository: CatchRepository
) {

    @MockBean
    lateinit var dooinglersProperties:DooinglersProperties

    private val badReportService = BadReportService(socialUserRepository, badReportRepository)

    @BeforeEach
    fun clearData() {
        badReportRepository.deleteAll()
        socialUserRepository.deleteAll()
        dooingleRepository.deleteAll()
    }

    @Test
    fun `뒹글 1개를 블락하고자 할 때 해당 뒹글이 블락되는지 확인`(){
        //given
        val 회원1 = SocialUser(provider = OAuth2Provider.KAKAO, providerId = "123", nickname = "A")
        val 회원2 = SocialUser(provider = OAuth2Provider.KAKAO, providerId = "456", nickname = "B")
        val 뒹글 = Dooingle(guest = 회원1, owner = 회원2,  catch = null, content = "질문1", blockedAt = null)
        socialUserRepository.saveAll(listOf(회원1, 회원2))
        dooingleRepository.save(뒹글)

        val request = BlockBadReportDto(listOf(1))

        //when
        badReportService.blockReportedDooingles(request)

        //then
        dooingleRepository.findAll().let {
            it[0].blockedAt shouldNotBe(null)
        }
    }

    @Test
    fun `뒹글 2개를 블락하고자 할 때 해당 뒹글들이 블락되는지 확인`(){
        //given
        val 회원1 = SocialUser(provider = OAuth2Provider.KAKAO, providerId = "123", nickname = "A")
        val 회원2 = SocialUser(provider = OAuth2Provider.KAKAO, providerId = "456", nickname = "B")
        val 뒹글1 = Dooingle(guest = 회원1, owner = 회원2,  catch = null, content = "질문1", blockedAt = null)
        val 뒹글2 = Dooingle(guest = 회원2, owner = 회원1, catch = null, content = "질문2", blockedAt = null)
        socialUserRepository.saveAll(listOf(회원1, 회원2))
        dooingleRepository.saveAll(listOf(뒹글1, 뒹글2))

        val request = BlockBadReportDto(listOf(1,2))

        //when
        badReportService.blockReportedDooingles(request)

        //then
        dooingleRepository.findAll().let {
            it[0].blockedAt shouldNotBe(null)
            it[1].blockedAt shouldNotBe(null)
        }
    }

    @Test
    fun `이미 블락된 뒹글을 블락하고자 할 때 해당 뒹글의 BlockedAt 컬럼이 갱신되지 않는지 확인`() {
        //given
        val 회원1 = SocialUser(provider = OAuth2Provider.KAKAO, providerId = "123", nickname = "A")
        val 회원2 = SocialUser(provider = OAuth2Provider.KAKAO, providerId = "456", nickname = "B")
        val time:ZonedDateTime = ZonedDateTime.now()
        val 뒹글 = Dooingle(guest = 회원1, owner = 회원2,  catch = null, content = "질문1", blockedAt = time)
        socialUserRepository.saveAll(listOf(회원1, 회원2))
        dooingleRepository.save(뒹글)

        val request = BlockBadReportDto(listOf(1))

        //when
        badReportService.blockReportedDooingles(request)

        //then
        dooingleRepository.findAll().let {
            it[0].blockedAt!!.toInstant().toEpochMilli() shouldBeEqual time.toInstant().toEpochMilli()
        }
    }

//    @Test
//    fun `캐치 1개를 블락하고자 할 때 해당 캐치가 블락되는지 확인`(){
//
//    }
//
//    @Test
//    fun `캐치 2개를 블락하고자 할 때 해당 캐치들이 블락되는지 확인`(){
//
//    }
//
//    @Test
//    fun `이미 블락된 캐치를 블락하고자 할 때 해당 캐치의 BlockedAt 컬럼이 갱신되지 않는지 확인`(){
//
//    }
}