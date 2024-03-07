package com.dooingle.domain.follow.repository

import com.dooingle.domain.follow.model.Follow
import com.dooingle.domain.user.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FollowRepository : JpaRepository<Follow, Long> {
    fun existsByFromUserAndToUser(fromUser: User, toUser: User): Boolean
    fun findAllByFromUser(fromUser: User): List<Follow>
    fun countByToUser(toUser: User): Int
    fun deleteByFromUserAndToUser(fromUser: User, toUser: User)
}