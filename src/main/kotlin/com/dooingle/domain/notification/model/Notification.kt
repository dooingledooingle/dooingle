package com.dooingle.domain.notification.model

import com.dooingle.domain.user.model.SocialUser
import jakarta.persistence.*

@Entity
@Table(name = "notification")
class Notification(
    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: SocialUser,

    @Column
    @Enumerated(EnumType.STRING)
    val notificationType: NotificationType,

    @Column
    val resourceId: Long,
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}
