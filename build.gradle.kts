import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    kotlin("plugin.jpa") version "1.9.22"
}

group = "com"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin 리플렉션 사용: kotlin-reflect
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    // 스프링 부트 구성 프로세서 - spring-boot-configuration-processor
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    // Kotlin-Jackson 모듈: jackson-module-kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // 웹: spring-boot-starter-web
    implementation("org.springframework.boot:spring-boot-starter-web")

    // 데이터(JPA): spring-boot-starter-data-jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // 데이터베이스: h2
    runtimeOnly("com.h2database:h2")

    // 테스트(스프링 부트 테스트): spring-boot-starter-test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
