package com.dooingle.domain.follow.service

import com.dooingle.domain.follow.dto.FollowResponse
import com.dooingle.domain.follow.model.Follow
import com.dooingle.domain.follow.repository.FollowRepository
import com.dooingle.domain.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FollowService(
    private val followRepository: FollowRepository,
    private val userRepository: UserRepository
) {
    fun follow(toUserId: Long, fromUserId: Long) {
        val toUser = userRepository.findByIdOrNull(toUserId) ?: throw Exception("") // TODO
        val fromUser = userRepository.findByIdOrNull(fromUserId) ?: throw Exception("") // TODO

        // 정책 1) 자기 자신을 follow할 수 없다
        if (toUserId == fromUserId) throw Exception("") // TODO

        // 정책 2) 중복 follow 불가능
        if (followRepository.existsByFromUserAndToUser(fromUser, toUser)) throw Exception("") // TODO

        followRepository.save(
            Follow(
                toUser = toUser,
                fromUser = fromUser
            )
        )
    }

    fun showFollowingList(userId: Long) : List<FollowResponse> {
        val fromUser = userRepository.findByIdOrNull(userId) ?: throw Exception("") // TODO

        return followRepository.findAllByFromUser(fromUser).map { FollowResponse.from(it) }
    }

    fun showFollowersNumber(userId: Long): Int {
        val toUser = userRepository.findByIdOrNull(userId) ?: throw Exception("") // TODO
        val followersList = followRepository.findAllByToUser(toUser)

        return followersList.size
    }

    @Transactional
    fun cancelFollowing(toUserId: Long, fromUserId: Long) {
        val toUser = userRepository.findByIdOrNull(toUserId) ?: throw Exception("") // TODO
        val fromUser = userRepository.findByIdOrNull(fromUserId) ?: throw Exception("") // TODO

        // 팔로우가 현재 되어 있는지 확인
        if (!followRepository.existsByFromUserAndToUser(fromUser, toUser)) throw Exception("") // TODO

        followRepository.deleteByFromUserAndToUser(fromUser, toUser)
    }
}