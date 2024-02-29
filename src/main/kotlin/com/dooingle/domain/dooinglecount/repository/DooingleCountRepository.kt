package com.dooingle.domain.dooinglecount.repository

import com.dooingle.domain.dooinglecount.model.DooingleCount
import org.springframework.data.jpa.repository.JpaRepository

interface DooingleCountRepository : JpaRepository<DooingleCount, Long>, DooingleCountQueryDslRepository {
    fun findByOwnerId(ownerId: Long): DooingleCount?
}