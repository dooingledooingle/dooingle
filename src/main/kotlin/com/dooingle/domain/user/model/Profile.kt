package com.dooingle.domain.user.model

import com.dooingle.global.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "profile")
class Profile(

    @Column
    var description: String? = null,

    @Column
    var imageUrl: String? = null,

    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: SocialUser

) : BaseEntity() {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}