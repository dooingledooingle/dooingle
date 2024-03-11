package com.dooingle.domain.user.repository

import com.dooingle.domain.user.model.Profile
import com.dooingle.domain.user.model.SocialUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.history.RevisionRepository

interface ProfileRepository : JpaRepository<Profile, Long>, RevisionRepository<Profile, Long, Long> {

    fun findByUser(user:SocialUser): Profile?
}