package com.dooingle.domain.follow.model

import com.dooingle.domain.user.model.User
import jakarta.persistence.*

@Entity
@Table(name = "follow")
class Follow(
    @ManyToOne
    @Column
    val toUser: User,

    @ManyToOne
    @Column
    val fromUser: User,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}
