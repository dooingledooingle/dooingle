package com.dooingle.domain.catch.model

import com.dooingle.domain.dooingle.model.Dooingle
import com.dooingle.global.entity.BaseEntity
import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name = "catch")
class Catch(
    @Column
    var content: String,
  
    @OneToOne
    @JoinColumn(name = "dooingle_id")
    val dooingle: Dooingle,


    @Column
    var deletedAt: ZonedDateTime? = null,
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    fun updateForDelete() {
        this.deletedAt = ZonedDateTime.now()
    }
}
