package com.dooingle.domain.dooingle.model

import com.dooingle.domain.catch.model.Catch
import com.dooingle.domain.user.model.User
import com.dooingle.global.entity.BaseEntity
import com.fasterxml.jackson.annotation.JsonIgnore
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

    @OneToOne(mappedBy = "dooingle")
    var catch: Catch?,

    @Column
    val content: String,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null
}
