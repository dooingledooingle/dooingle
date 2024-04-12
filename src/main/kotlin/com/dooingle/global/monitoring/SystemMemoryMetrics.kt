package com.dooingle.global.monitoring

import com.sun.management.OperatingSystemMXBean
import io.micrometer.core.instrument.Gauge
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Component
import java.lang.management.ManagementFactory

@Component
class SystemMemoryMetrics(registry: MeterRegistry) {

    init {
        val osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean::class.java)

        Gauge.builder("system.memory.total", osBean) { it.totalMemorySize.toDouble() }
            .description("The total size of physical memory")
            .baseUnit("bytes")
            .register(registry)

        Gauge.builder("system.memory.free", osBean) { it.freeMemorySize.toDouble() }
            .description("The amount of free physical memory")
            .baseUnit("bytes")
            .register(registry)
    }
}
