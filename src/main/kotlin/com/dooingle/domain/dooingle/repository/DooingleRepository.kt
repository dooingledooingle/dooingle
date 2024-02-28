package com.dooingle.domain.dooingle.repository

import com.dooingle.domain.dooingle.model.Dooingle
import org.springframework.data.jpa.repository.JpaRepository

interface DooingleRepository : JpaRepository<Dooingle, Long>, DooingleQueryDslRepository