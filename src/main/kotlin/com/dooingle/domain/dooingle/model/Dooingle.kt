package com.dooingle.domain.dooingle.model

import com.dooingle.domain.user.model.SocialUser
import com.dooingle.global.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "dooingle")
class Dooingle(
    @ManyToOne
    @JoinColumn(name = "guest_id")
    val guest: SocialUser,

    @ManyToOne
    @JoinColumn(name = "owner_id")
    val owner: SocialUser,

    @Column
    val content: String,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null
}
