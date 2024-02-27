package com.dooingle.domain.dooingle.model

import com.dooingle.domain.user.model.User
import com.dooingle.global.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "dooingle")
class Dooingle(
    @ManyToOne
    @JoinColumn(name = "guest_id")
    val guest: User,

    @ManyToOne
    @JoinColumn(name = "owner_id")
    val owner: User,

    @Column
    val content: String,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null
}
