package com.dooingle.domain.user.model

import com.dooingle.global.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "dooingle_user")
class User(
    @Column
    val email: String,

    @Column
    val password: String,

    @Column
    var name: String,

    @Column
    var description: String,

    @Column
    @Enumerated(EnumType.STRING)
    val userRole: UserRole = UserRole.USER,

) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}
