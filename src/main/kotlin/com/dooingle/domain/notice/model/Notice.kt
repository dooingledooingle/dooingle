package com.dooingle.domain.notice.model

import com.dooingle.domain.user.model.SocialUser
import com.dooingle.global.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "notice")
class Notice(
    @Column
    var title: String,

    @Column
    var content: String,

    @ManyToOne(fetch = FetchType.LAZY)
    val user: SocialUser,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}
