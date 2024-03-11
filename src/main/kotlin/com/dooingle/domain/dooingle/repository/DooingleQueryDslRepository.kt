package com.dooingle.domain.dooingle.repository

import com.dooingle.domain.dooingle.dto.DooingleAndCatchResponse
import com.dooingle.domain.dooingle.dto.DooingleResponse
import com.dooingle.domain.user.model.SocialUser
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface DooingleQueryDslRepository {

    fun getDooinglesBySlice(cursor: Long?, pageable: Pageable): Slice<DooingleResponse>

    fun getPersonalPageBySlice(owner: SocialUser, cursor: Long?, pageable: Pageable): Slice<DooingleAndCatchResponse>

    // TODO 팔로우 기능 구현 후 구현 필요
    // fun getDooinglePageableOfFollows(pageable: Pageable): Slice<Dooingle>
}
