package com.dooingle.domain.catch.dto

import com.dooingle.domain.catch.model.Catch
import com.dooingle.domain.dooingle.model.Dooingle

data class AddCatchRequest(
    val ownerId: Long, // TODO : 추후 삭제 예정
    val content: String
){
    fun to(dooingle: Dooingle): Catch {
        return Catch(
            content = content,
            dooingle = dooingle
        )
    }
}
