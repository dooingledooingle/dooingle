package com.dooingle.domain.follow.model

import com.dooingle.domain.user.model.User
import jakarta.persistence.*

@Entity
@Table(name = "follow")
class Follow(
    @ManyToOne
    @JoinColumn(name = "to_user_id")
    val toUser: User,

    @ManyToOne
    @JoinColumn(name = "from_user_id")
    val fromUser: User,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}
