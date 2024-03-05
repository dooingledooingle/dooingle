package com.dooingle.domain.user.repository

import com.dooingle.domain.user.model.SocialUser
import org.springframework.data.repository.CrudRepository

interface SocialUserRepository : CrudRepository<SocialUser, Long> {
}