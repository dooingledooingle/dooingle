package com.dooingle.domain.dooingle.model

import com.dooingle.domain.user.model.SocialUser
import com.dooingle.domain.catchdomain.model.Catch
import com.dooingle.global.entity.BaseEntity
import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name = "dooingle")
class Dooingle(
    @ManyToOne
    @JoinColumn(name = "guest_id")
    val guest: SocialUser,

    @ManyToOne
    @JoinColumn(name = "owner_id")
    val owner: SocialUser,

    @OneToOne(mappedBy = "dooingle")
    var catch: Catch?,

    @Column
    val content: String,

    @Column
    var blockedAt: ZonedDateTime? = null
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null
}
