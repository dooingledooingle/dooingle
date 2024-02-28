package com.dooingle.domain.user.model

import com.dooingle.domain.user.model.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "dooingle_count")
class DooingleCount(
    @ManyToOne
    @JoinColumn(name = "owner_id")
    val owner: User,

    @Column
    var count: Int = 0
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    fun plus() {
        count += 1
    }
}