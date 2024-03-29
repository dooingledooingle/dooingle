package com.dooingle.domain.dooingle.repository

import com.dooingle.domain.catchdomain.dto.CatchResponse
import com.dooingle.domain.catchdomain.model.QCatch
import com.dooingle.domain.dooingle.dto.DooingleAndCatchResponse
import com.dooingle.domain.dooingle.dto.DooingleFeedResponse
import com.dooingle.domain.dooingle.model.QDooingle
import com.dooingle.domain.follow.model.QFollow
import com.dooingle.domain.user.dto.DooinglerResponse
import com.dooingle.domain.user.model.QSocialUser
import com.dooingle.domain.user.model.SocialUser
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class DooingleQueryDslRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : DooingleQueryDslRepository {

    private val dooingle = QDooingle.dooingle
    private val catch = QCatch("catch")
    private val owner = QSocialUser("ow")
    private val follow = QFollow.follow

    override fun getDooinglesBySlice(cursor: Long?, pageable: Pageable): Slice<DooingleFeedResponse> {
        val selectSize = pageable.pageSize + 1

        return queryFactory
            .select(
                Projections.constructor(
                    DooingleFeedResponse::class.java,
                    owner.nickname,
                    owner.userLink,
                    dooingle.id,
                    dooingle.content,
                    catch.isNotNull,
                    dooingle.createdAt,
                )
            )
            .from(dooingle)
            .join(dooingle.owner, owner)
            .leftJoin(catch).on(dooingle.eq(catch.dooingle))
            .where(lessThanCursor(cursor))
            .orderBy(dooingle.id.desc())
            .limit(selectSize.toLong())
            .fetch()
            .let {
                changeToSlice(it, selectSize, pageable)
            }
    }

    override fun getDooinglesFollowingBySlice(
        userId: Long,
        cursor: Long?,
        pageable: Pageable
    ): Slice<DooingleFeedResponse> {
        val selectSize = pageable.pageSize + 1

        return queryFactory
            .select(
                Projections.constructor(
                    DooingleFeedResponse::class.java,
                    owner.nickname,
                    owner.userLink,
                    dooingle.id,
                    dooingle.content,
                    catch.isNotNull,
                    dooingle.createdAt,
                )
            )
            .from(dooingle)
            .leftJoin(dooingle.owner, owner)
            .leftJoin(follow).on(dooingle.owner.id.eq(follow.toUser.id))
            .leftJoin(catch).on(dooingle.eq(catch.dooingle))
            .where(lessThanCursor(cursor))
            .where(follow.fromUser.id.eq(userId))
            .orderBy(dooingle.id.desc())
            .limit(selectSize.toLong())
            .fetch()
            .let {
                changeToSlice(it, selectSize, pageable)
            }
    }

    override fun getPersonalPageBySlice(owner: SocialUser, cursor: Long?, pageable: Pageable): Slice<DooingleAndCatchResponse> {
        val selectSize = pageable.pageSize + 1
        val whereClause = BooleanBuilder()
            .and(lessThanCursor(cursor))
            .and(dooingle.owner.eq(owner))
        val list = getContents(whereClause, pageable)
        return changeToSlice(list, selectSize, pageable)
    }

    override fun getHotDooinglerList(size: Long): List<DooinglerResponse> {

        return queryFactory.select(
            Projections.constructor(
                DooinglerResponse::class.java,
                owner.id,
                owner.nickname
            )
        )
            .from(dooingle)
            .leftJoin(dooingle.owner, owner)
            .where(dooingle.createdAt.after(ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)))
            .groupBy(dooingle.owner.id)
            .orderBy(dooingle.guest.count().desc())
            .limit(size)
            .fetch()
    }

    private fun lessThanCursor(cursor: Long?) = cursor?.let { dooingle.id.lt(it) }

    private fun <T> hasNextSlice(dooingleList: List<T>, selectSize: Int) = (dooingleList.size == selectSize)

    private fun <T> changeToSlice(list: List<T>, selectSize: Int, pageable: Pageable) =
        SliceImpl(
            if (list.size < selectSize) list else list.dropLast(1),
            pageable,
            hasNextSlice(list, selectSize)
        )

    private fun getContents(whereClause: BooleanBuilder, pageable: Pageable) =
        queryFactory
            .select(
                Projections.constructor(
                    DooingleAndCatchResponse::class.java,
                    owner.nickname,
                    owner.userLink,
                    dooingle.id,
                    dooingle.content,
                    Projections.constructor(
                        CatchResponse::class.java,
                        catch.id,
                        catch.content,
                        catch.createdAt
                    ),
                    dooingle.createdAt
                )
            )
            .from(dooingle)
                .leftJoin(owner).on(dooingle.owner.eq(owner))
                .leftJoin(catch).on(catch.dooingle.eq(dooingle))
            .where(whereClause)
            .orderBy(dooingle.id.desc())
            .limit((pageable.pageSize + 1).toLong())
            .fetch()

}
