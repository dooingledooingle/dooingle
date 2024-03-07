package com.dooingle.domain.user.repository

import com.dooingle.domain.user.model.Profile
import org.springframework.data.jpa.repository.JpaRepository

interface ProfileRepository : JpaRepository<Profile, Long> {
}