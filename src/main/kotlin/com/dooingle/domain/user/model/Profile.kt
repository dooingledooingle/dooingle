package com.dooingle.domain.user.model

import com.dooingle.global.entity.BaseEntity
import jakarta.persistence.*
import org.hibernate.envers.Audited
import org.hibernate.envers.RelationTargetAuditMode

@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@Entity
@Table(name = "profile")
class Profile(

    @Column
    var description: String? = null,

    @Column
    var imageUrl: String? = null,

    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: SocialUser

) : BaseEntity() {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}