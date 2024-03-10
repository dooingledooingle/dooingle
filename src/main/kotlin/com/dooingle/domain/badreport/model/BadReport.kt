package com.dooingle.domain.badreport.model

import com.dooingle.domain.user.model.SocialUser
import com.dooingle.global.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "bad_report")
class BadReport(
    @ManyToOne
    @JoinColumn(name = "reporter_id")
    val reporter: SocialUser,

    @Enumerated(EnumType.STRING)
    @Column
    val reportedTargetType: ReportedTargetType,

    @Column
    val reportedTargetId: Long,

    @Column
    val reportReason: String,

) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null
}
