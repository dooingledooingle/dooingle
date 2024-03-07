package com.dooingle.domain.dooingle.repository

import com.dooingle.domain.dooingle.model.Dooingle
import com.dooingle.domain.user.model.SocialUser
import org.springframework.data.jpa.repository.JpaRepository

interface DooingleRepository : JpaRepository<Dooingle, Long>, DooingleQueryDslRepository {
    fun findAllByOwner(owner: SocialUser): List<Dooingle>
}

