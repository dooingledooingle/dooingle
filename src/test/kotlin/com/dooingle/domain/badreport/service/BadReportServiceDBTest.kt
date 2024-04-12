package com.dooingle.domain.badreport.service

import com.dooingle.domain.badreport.dto.BlockBadReportDto
import com.dooingle.domain.badreport.repository.BadReportRepository
import com.dooingle.domain.catchdomain.model.Catch
import com.dooingle.domain.catchdomain.repository.CatchRepository
import com.dooingle.domain.dooingle.model.Dooingle
import com.dooingle.domain.dooingle.repository.DooingleRepository
import com.dooingle.domain.user.model.SocialUser
import com.dooingle.domain.user.repository.SocialUserRepository
import com.dooingle.global.aop.DistributedLock
import com.dooingle.global.aop.TransactionForTrailingLambda
import com.dooingle.global.oauth2.provider.OAuth2Provider
import com.dooingle.global.querydsl.QueryDslConfig
import com.dooingle.global.redis.EmbeddedRedisClientConfig
import com.dooingle.global.redis.EmbeddedRedisServerConfig
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.time.ZonedDateTime

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(value = [QueryDslConfig::class, EmbeddedRedisClientConfig::class, EmbeddedRedisServerConfig::class, DistributedLock::class, TransactionForTrailingLambda::class])
@ActiveProfiles("test")
class BadReportServiceDBTest @Autowired constructor(
    private val badReportRepository: BadReportRepository,
    private val socialUserRepository: SocialUserRepository,
    private val dooingleRepository: DooingleRepository,
    private val catchRepository: CatchRepository,
    private val distributedLock: DistributedLock,
    transactionForTrailingLambda: TransactionForTrailingLambda,
) {

    private val badReportService = BadReportService(socialUserRepository, badReportRepository, distributedLock, transactionForTrailingLambda)

    @AfterEach
    fun clearData() {
        badReportRepository.deleteAll()
        socialUserRepository.deleteAll()
        dooingleRepository.deleteAll()
        catchRepository.deleteAll()
    }

    @Test
    fun `뒹글 1개를 블락하고자 할 때 해당 뒹글이 블락되는지 확인`(){
        //given
        val user1 = SocialUser(provider = OAuth2Provider.KAKAO, providerId = "123", nickname = "A", userLink = "0000000000")
        val user2 = SocialUser(provider = OAuth2Provider.KAKAO, providerId = "456", nickname = "B", userLink = "1111111111")
        val dooingle = Dooingle(guest = user1, owner = user2,  catch = null, content = "질문", blockedAt = null)
        socialUserRepository.saveAll(listOf(user1, user2))
        dooingleRepository.save(dooingle)

        val request = BlockBadReportDto(listOf(dooingle.id!!))

        //when
        badReportService.blockReportedDooingles(request)

        //then
        dooingleRepository.findById(dooingle.id!!).let {
            it.get().blockedAt shouldNotBe(null)
        }
    }

    @Test
    fun `뒹글 2개를 블락하고자 할 때 해당 뒹글들이 블락되는지 확인`(){
        //given
        val user1 = SocialUser(provider = OAuth2Provider.KAKAO, providerId = "123", nickname = "A", userLink = "0000000000")
        val user2 = SocialUser(provider = OAuth2Provider.KAKAO, providerId = "456", nickname = "B", userLink = "1111111111")
        val dooingle1 = Dooingle(guest = user1, owner = user2,  catch = null, content = "질문1", blockedAt = null)
        val dooingle2 = Dooingle(guest = user2, owner = user1, catch = null, content = "질문2", blockedAt = null)
        socialUserRepository.saveAll(listOf(user1, user2))
        dooingleRepository.saveAll(listOf(dooingle1, dooingle2))

        val request = BlockBadReportDto(listOf(dooingle1.id!!, dooingle2.id!!))

        //when
        badReportService.blockReportedDooingles(request)

        //then
        dooingleRepository.findById(dooingle1.id!!).let {
            it.get().blockedAt shouldNotBe(null)
        }
        dooingleRepository.findById(dooingle2.id!!).let {
            it.get().blockedAt shouldNotBe(null)
        }
    }

    @Test
    fun `이미 블락된 뒹글을 블락하고자 할 때 해당 뒹글의 BlockedAt 컬럼이 갱신되지 않는지 확인`() {
        //given
        val user1 = SocialUser(provider = OAuth2Provider.KAKAO, providerId = "123", nickname = "A", userLink = "0000000000")
        val user2 = SocialUser(provider = OAuth2Provider.KAKAO, providerId = "456", nickname = "B", userLink = "1111111111")
        val time = ZonedDateTime.now()
        val dooingle = Dooingle(guest = user1, owner = user2,  catch = null, content = "질문", blockedAt = time)
        socialUserRepository.saveAll(listOf(user1, user2))
        dooingleRepository.save(dooingle)

        val request = BlockBadReportDto(listOf(dooingle.id!!))

        //when
        badReportService.blockReportedDooingles(request)

        //then
        dooingleRepository.findById(dooingle.id!!).let {
            it.get().blockedAt!!.toInstant().toEpochMilli() shouldBeEqual time.toInstant().toEpochMilli()
        }
    }

    @Test
    fun `캐치 1개를 블락하고자 할 때 해당 캐치가 블락되는지 확인`(){
        //given
        val user1 = SocialUser(provider = OAuth2Provider.KAKAO, providerId = "123", nickname = "A", userLink = "0000000000")
        val user2 = SocialUser(provider = OAuth2Provider.KAKAO, providerId = "456", nickname = "B", userLink = "1111111111")
        val dooingle = Dooingle(guest = user1, owner = user2,  catch = null, blockedAt = null, content = "질문")
        val catch = Catch(dooingle = dooingle, deletedAt = null, blockedAt = null, content = "답변")
        socialUserRepository.saveAll(listOf(user1, user2))
        dooingleRepository.save(dooingle)
        catchRepository.save(catch)

        val request = BlockBadReportDto(listOf(catch.id!!))

        //when
        badReportService.blockReportedCatches(request)

        //then
        catchRepository.findById(catch.id!!).let {
            it.get().blockedAt shouldNotBe(null)
        }
    }

    @Test
    fun `캐치 2개를 블락하고자 할 때 해당 캐치들이 블락되는지 확인`(){
        //given
        val user1 = SocialUser(provider = OAuth2Provider.KAKAO, providerId = "123", nickname = "A", userLink = "0000000000")
        val user2 = SocialUser(provider = OAuth2Provider.KAKAO, providerId = "456", nickname = "B", userLink = "1111111111")
        val dooingle1 = Dooingle(guest = user1, owner = user2,  catch = null, blockedAt = null, content = "질문1")
        val dooingle2 = Dooingle(guest = user2, owner = user1,  catch = null, blockedAt = null, content = "질문2")
        val catch1 = Catch(dooingle = dooingle1, deletedAt = null, blockedAt = null, content = "답변1")
        val catch2 = Catch(dooingle = dooingle2, deletedAt = null, blockedAt = null, content = "답변2")

        socialUserRepository.saveAll(listOf(user1, user2))
        dooingleRepository.saveAll(listOf(dooingle1, dooingle2))
        catchRepository.saveAll(listOf(catch1, catch2))

        val request = BlockBadReportDto(listOf(catch1.id!!, catch2.id!!))

        //when
        badReportService.blockReportedCatches(request)

        //then
        catchRepository.findById(catch1.id!!).let {
            it.get().blockedAt shouldNotBe(null)
        }
        catchRepository.findById(catch2.id!!).let {
            it.get().blockedAt shouldNotBe(null)
        }
    }

    @Test
    fun `이미 블락된 캐치를 블락하고자 할 때 해당 캐치의 BlockedAt 컬럼이 갱신되지 않는지 확인`(){
        //given
        val user1 = SocialUser(provider = OAuth2Provider.KAKAO, providerId = "123", nickname = "A", userLink = "0000000000")
        val user2 = SocialUser(provider = OAuth2Provider.KAKAO, providerId = "456", nickname = "B", userLink = "1111111111")
        val dooingle = Dooingle(guest = user1, owner = user2,  catch = null, blockedAt = null, content = "질문")
        val time = ZonedDateTime.now()
        val catch = Catch(dooingle = dooingle, deletedAt = null, blockedAt = time, content = "답변")
        socialUserRepository.saveAll(listOf(user1, user2))
        dooingleRepository.save(dooingle)
        catchRepository.save(catch)

        val request = BlockBadReportDto(listOf(catch.id!!))

        //when
        badReportService.blockReportedCatches(request)

        //then
        catchRepository.findById(catch.id!!).let {
            it.get().blockedAt!!.toInstant().toEpochMilli() shouldBeEqual time.toInstant().toEpochMilli()
        }
    }
}