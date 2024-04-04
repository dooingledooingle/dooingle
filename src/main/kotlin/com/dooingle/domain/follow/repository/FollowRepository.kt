package com.dooingle.domain.follow.repository

import com.dooingle.domain.follow.model.Follow
import com.dooingle.domain.user.model.SocialUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface FollowRepository : JpaRepository<Follow, Long>, FollowQueryDslRepository {
    fun existsByFromUserAndToUser(fromUser: SocialUser, toUser: SocialUser): Boolean
    fun findAllByFromUser(fromUser: SocialUser): List<Follow>
    fun countByToUser(toUser: SocialUser): Int
    @Transactional
    fun deleteByFromUserAndToUser(fromUser: SocialUser, toUser: SocialUser)
}
