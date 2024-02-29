package com.dooingle.domain.dooingle.repository

import com.dooingle.domain.dooingle.model.Dooingle

interface DooingleQueryDslRepository {

    fun getDooingleFeeds(): Dooingle

    fun getDooingleFeedsOfFollows(): Dooingle
}
