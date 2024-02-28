package com.dooingle.domain.user.repository

import com.dooingle.domain.user.model.DooingleCount
import org.springframework.data.jpa.repository.JpaRepository

interface DooingleCountRepository : JpaRepository<DooingleCount, Long>, DooingleCountQueryDslRepository {
    fun findByOwnerId(ownerId: Long): DooingleCount?
}