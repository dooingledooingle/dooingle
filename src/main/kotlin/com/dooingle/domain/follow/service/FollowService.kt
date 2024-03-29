package com.dooingle.domain.follow.service

import com.dooingle.domain.follow.dto.FollowDetailResponse
import com.dooingle.domain.follow.dto.IsFollowingUserResponse
import com.dooingle.domain.follow.model.Follow
import com.dooingle.domain.follow.repository.FollowRepository
import com.dooingle.domain.user.repository.SocialUserRepository
import com.dooingle.global.aop.DistributedLock
import com.dooingle.global.exception.custom.ConflictStateException
import com.dooingle.global.exception.custom.InvalidParameterException
import com.dooingle.global.exception.custom.ModelNotFoundException
import com.dooingle.global.exception.custom.SocialUserNotFoundByUserLinkException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FollowService(
    private val followRepository: FollowRepository,
    private val socialUserRepository: SocialUserRepository,
    private val distributedLock: DistributedLock
) {
    fun follow(toUserLink: String, fromUserId: Long) : Unit = distributedLock("FOLLOW:$fromUserId")  {
        val toUser = socialUserRepository.findByUserLink(toUserLink)
            ?: throw SocialUserNotFoundByUserLinkException(toUserLink)
        val fromUser = socialUserRepository.findByIdOrNull(fromUserId)
            ?: throw ModelNotFoundException(modelName = "Social User", modelId = fromUserId)

        // 정책 1) 자기 자신을 follow할 수 없다
        if (toUser.id == fromUserId) throw InvalidParameterException("자기 자신을 팔로우할 수 없습니다.")

        // 정책 2) 중복 follow 불가능
        if (followRepository.existsByFromUserAndToUser(fromUser, toUser)) throw ConflictStateException("이미 팔로우 중입니다.")

        followRepository.save(
            Follow(
                toUser = toUser,
                fromUser = fromUser
            )
        )
    }

    fun isFollowingUser(toUserLink: String, fromUserId: Long): IsFollowingUserResponse {
        val toUser = socialUserRepository.findByUserLink(toUserLink)
            ?: throw SocialUserNotFoundByUserLinkException(toUserLink)
        val fromUser = socialUserRepository.findByIdOrNull(fromUserId)
            ?: throw ModelNotFoundException(modelName = "Social User", modelId = fromUserId)

        return IsFollowingUserResponse(followRepository.existsByFromUserAndToUser(fromUser, toUser))
    }

    fun showFollowingList(fromUserId: Long) : List<FollowDetailResponse> {
        val fromUser = socialUserRepository.findByIdOrNull(fromUserId)
            ?: throw ModelNotFoundException(modelName = "Social User", modelId = fromUserId)

        return followRepository.getFollowDetailListByFromUser(fromUser)
    }

    fun showFollowersNumber(userId: Long): Int {
        val toUser = socialUserRepository.findByIdOrNull(userId)
            ?: throw ModelNotFoundException(modelName = "Social User", modelId = userId)
        val followers = followRepository.countByToUser(toUser)

        return followers
    }

    @Transactional
    fun cancelFollowing(toUserLink: String, fromUserId: Long) {
        val toUser = socialUserRepository.findByUserLink(toUserLink)
            ?: throw SocialUserNotFoundByUserLinkException(toUserLink)
        val fromUser = socialUserRepository.findByIdOrNull(fromUserId)
            ?: throw ModelNotFoundException(modelName = "Social User", modelId = fromUserId)

        // 팔로우가 현재 되어 있는지 확인
        if (!followRepository.existsByFromUserAndToUser(fromUser, toUser)) throw ConflictStateException("이미 팔로우하지 않은 상태입니다.")

        followRepository.deleteByFromUserAndToUser(fromUser, toUser)
    }
}
