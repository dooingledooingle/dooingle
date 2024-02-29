package com.dooingle.global.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "dooingler.list.size")
class DooinglersProperties(
    val hot: Int,
    val new: Int
)