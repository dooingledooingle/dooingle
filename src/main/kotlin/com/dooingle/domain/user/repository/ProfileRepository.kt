package com.dooingle.domain.user.repository

import com.dooingle.domain.user.model.Profile
import com.dooingle.domain.user.model.SocialUser
import org.springframework.data.jpa.repository.JpaRepository

interface ProfileRepository : JpaRepository<Profile, Long> {

    fun findByUser(user:SocialUser): Profile?
}