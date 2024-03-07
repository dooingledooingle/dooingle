package com.dooingle.global.property

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@EnableConfigurationProperties(DooinglersProperties::class)
@Configuration
class PropertyConfig