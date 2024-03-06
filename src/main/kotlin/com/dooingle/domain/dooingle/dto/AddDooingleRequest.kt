package com.dooingle.domain.dooingle.dto

import com.dooingle.domain.dooingle.model.Dooingle
import com.dooingle.domain.user.model.User

data class AddDooingleRequest (
    val guestId: Long,
    val content: String
){
    fun to(guest: User, owner: User): Dooingle {
        return Dooingle(
            guest = guest,
            owner = owner,
            content = content,
            catch = null
        )
    }
}