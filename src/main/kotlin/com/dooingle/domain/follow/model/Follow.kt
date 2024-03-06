package com.dooingle.domain.follow.model

import com.dooingle.domain.user.model.SocialUser
import jakarta.persistence.*

@Entity
@Table(name = "follow")
class Follow(
    @ManyToOne
    @JoinColumn(name = "to_user_id")
    val toUser: SocialUser,

    @ManyToOne
    @JoinColumn(name = "from_user_id")
    val fromUser: SocialUser,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}
