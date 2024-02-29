package com.dooingle.domain.dooingle.repository

import com.dooingle.domain.dooingle.model.Dooingle
import com.querydsl.jpa.impl.JPAQueryFactory

class DooingleQueryDslRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : DooingleQueryDslRepository {

    override fun getDooingleFeeds(): Dooingle {
        TODO("Not yet implemented")
    }

    override fun getDooingleFeedsOfFollows(): Dooingle {
        TODO("Not yet implemented")
    }
}
