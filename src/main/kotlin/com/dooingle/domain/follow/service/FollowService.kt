package com.dooingle.domain.follow.service

import com.dooingle.domain.follow.dto.FollowResponse
import com.dooingle.domain.follow.model.Follow
import com.dooingle.domain.follow.repository.FollowRepository
import com.dooingle.domain.user.repository.SocialUserRepository
import com.dooingle.global.exception.custom.ConflictStateException
import com.dooingle.global.exception.custom.InvalidParameterException
import com.dooingle.global.exception.custom.ModelNotFoundException
import com.dooingle.global.security.UserPrincipal
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FollowService(
    private val followRepository: FollowRepository,
    private val socialUserRepository: SocialUserRepository
) {
    fun follow(toUserId: Long, userPrincipal: UserPrincipal) {
        val toUser = socialUserRepository.findByIdOrNull(toUserId)
            ?: throw ModelNotFoundException(modelName = "Social User", modelId = toUserId)
        val fromUser = socialUserRepository.findByIdOrNull(userPrincipal.id)
            ?: throw ModelNotFoundException(modelName = "Social User", modelId = userPrincipal.id)

        // 정책 1) 자기 자신을 follow할 수 없다
        if (toUserId == userPrincipal.id) throw InvalidParameterException("자기 자신을 팔로우할 수 없습니다.")

        // 정책 2) 중복 follow 불가능
        if (followRepository.existsByFromUserAndToUser(fromUser, toUser)) throw ConflictStateException("이미 팔로우 중입니다.")

        followRepository.save(
            Follow(
                toUser = toUser,
                fromUser = fromUser
            )
        )
    }

    fun showFollowingList(userPrincipal: UserPrincipal) : List<FollowResponse> {
        val fromUser = socialUserRepository.findByIdOrNull(userPrincipal.id)
            ?: throw ModelNotFoundException(modelName = "Social User", modelId = userPrincipal.id)

        return followRepository.findAllByFromUser(fromUser).map { FollowResponse.from(it) }
    }

    fun showFollowersNumber(userId: Long): Int {
        val toUser = socialUserRepository.findByIdOrNull(userId)
            ?: throw ModelNotFoundException(modelName = "Social User", modelId = userId)
        val followers = followRepository.countByToUser(toUser)

        return followers
    }

    @Transactional
    fun cancelFollowing(toUserId: Long, userPrincipal: UserPrincipal) {
        val toUser = socialUserRepository.findByIdOrNull(toUserId)
            ?: throw ModelNotFoundException(modelName = "Social User", modelId = toUserId)
        val fromUser = socialUserRepository.findByIdOrNull(userPrincipal.id)
            ?: throw ModelNotFoundException(modelName = "Social User", modelId = userPrincipal.id)

        // 팔로우가 현재 되어 있는지 확인
        if (!followRepository.existsByFromUserAndToUser(fromUser, toUser)) throw ConflictStateException("이미 팔로우하지 않은 상태입니다.")

        followRepository.deleteByFromUserAndToUser(fromUser, toUser)
    }
}