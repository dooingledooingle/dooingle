package com.dooingle.domain.dooingle.repository

import com.dooingle.domain.dooingle.dto.DooingleAndCatchResponse
import com.dooingle.domain.dooingle.dto.DooingleFeedResponse
import com.dooingle.domain.user.model.SocialUser
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface DooingleQueryDslRepository {

    fun getDooinglesBySlice(cursor: Long?, pageable: Pageable): Slice<DooingleFeedResponse>

    fun getDooinglesFollowingBySlice(userId: Long, cursor: Long?, pageable: Pageable): Slice<DooingleFeedResponse>

    fun getPersonalPageBySlice(owner: SocialUser, cursor: Long?, pageable: Pageable): Slice<DooingleAndCatchResponse>

}
