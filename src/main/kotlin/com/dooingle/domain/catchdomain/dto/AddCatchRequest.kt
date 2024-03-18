package com.dooingle.domain.catchdomain.dto

import com.dooingle.domain.catchdomain.model.Catch
import com.dooingle.domain.dooingle.model.Dooingle

data class AddCatchRequest(
    val content: String
){
    fun to(dooingle: Dooingle): Catch {
        return Catch(
            content = content,
            dooingle = dooingle
        )
    }
}
