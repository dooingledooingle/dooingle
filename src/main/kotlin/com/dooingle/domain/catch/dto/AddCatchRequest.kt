package com.dooingle.domain.catch.dto

import com.dooingle.domain.catch.model.Catch

data class AddCatchRequest(
    val ownerId: Long, // TODO : 추후 삭제 예정
    val content: String
){
    fun to(): Catch {
        return Catch(
            content = content,
            dooingle = dooingle
        )
    }
}
