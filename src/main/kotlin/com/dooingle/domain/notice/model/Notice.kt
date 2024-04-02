package com.dooingle.domain.notice.model

import com.dooingle.domain.notice.dto.AddNoticeRequest
import com.dooingle.domain.user.model.SocialUser
import com.dooingle.global.entity.BaseEntity
import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name = "notice")
class Notice(
    @Column
    var title: String,

    @Column
    var content: String,

    @ManyToOne(fetch = FetchType.LAZY)
    val user: SocialUser,

    @Column
    var deletedAt: ZonedDateTime? = null

) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    fun update(request: AddNoticeRequest){
        this.content = request.content
        this.title = request.title
    }

    fun updateForDelete() {
        this.deletedAt = ZonedDateTime.now()
    }
}
