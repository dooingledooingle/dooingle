package com.dooingle.domain.dooingle.repository

import com.dooingle.domain.dooingle.dto.DooingleAndCatchResponse
import com.dooingle.domain.dooingle.dto.DooingleResponse
import com.dooingle.domain.user.model.SocialUser
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface DooingleQueryDslRepository {

    fun getDooinglesBySlice(cursor: Long?, pageable: Pageable): Slice<DooingleResponse>

    fun getDooinglesFollowingBySlice(userId: Long, cursor: Long?, pageable: Pageable): Slice<DooingleResponse>

    fun getPersonalPageBySlice(owner: SocialUser, cursor: Long?, pageable: Pageable): Slice<DooingleAndCatchResponse>

}
