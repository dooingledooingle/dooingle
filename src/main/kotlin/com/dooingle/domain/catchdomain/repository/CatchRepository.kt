package com.dooingle.domain.catchdomain.repository

import com.dooingle.domain.catchdomain.model.Catch
import com.dooingle.domain.dooingle.model.Dooingle
import org.springframework.data.jpa.repository.JpaRepository


interface CatchRepository : JpaRepository<Catch,Long> {
    fun findByDooingle(dooingle: Dooingle): Catch?
}

