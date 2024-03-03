package com.dooingle.domain.dooingle.repository

import com.dooingle.domain.dooingle.model.Dooingle
import com.dooingle.domain.user.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface DooingleRepository : JpaRepository<Dooingle, Long> {
    fun findAllByOwner(owner: User): List<Dooingle>
}