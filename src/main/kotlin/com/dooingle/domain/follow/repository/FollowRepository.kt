package com.dooingle.domain.follow.repository

import com.dooingle.domain.follow.model.Follow
import com.dooingle.domain.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FollowRepository : JpaRepository<Follow, Long> {
    fun existsByFromUserAndToUser(fromUser: User, toUser: User): Boolean

    fun findAllByFromUser(fromUser: User): List<Follow>

    fun findAllByToUser(toUser: User): List<Follow>

    fun deleteByFromUserAndToUser(fromUser: User, toUser: User)
}