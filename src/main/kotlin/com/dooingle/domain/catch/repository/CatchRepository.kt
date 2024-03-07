package com.dooingle.domain.catch.repository

import com.dooingle.domain.catch.model.Catch
import com.dooingle.domain.dooingle.model.Dooingle
import org.springframework.data.jpa.repository.JpaRepository


interface CatchRepository : JpaRepository<Catch,Long> {
    fun findByDooingle(dooingle: Dooingle): Catch?
}

