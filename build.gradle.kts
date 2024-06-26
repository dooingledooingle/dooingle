import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    kotlin("plugin.jpa") version "1.9.22"
    kotlin("kapt") version "1.9.22"
}

group = "com"
version = "1.1.0"

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
    // 웹 API 문서화(Swagger): springdoc-openapi-starter-webmvc-ui (http://localhost:8080/swagger-ui/index.html)
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
    // 빈 검증: spring-boot-starter-validation
    implementation("org.springframework.boot:spring-boot-starter-validation")
    // 인증, 인가: spring-boot-starter-security
    implementation("org.springframework.boot:spring-boot-starter-security")
    // Amazon Cloud 연결
    implementation("org.springframework.cloud:spring-cloud-starter-aws:2.2.6.RELEASE")
    // jwt
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")
    // okhttp
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.12")
    implementation("com.squareup.okhttp3:okhttp-sse:5.0.0-alpha.12")

    // 데이터(JPA): spring-boot-starter-data-jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // 데이터(QueryDSL):
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
    // 데이터베이스: h2
    runtimeOnly("com.h2database:h2")
    // 데이터베이스: MySQL
    runtimeOnly("com.mysql:mysql-connector-j")
    // 데이터 이력 관리
    implementation("org.springframework.data:spring-data-envers")
    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.redisson:redisson-spring-boot-starter:3.26.0")
    // Embedded Redis: local-memory-h2, test 프로파일 용도
    implementation("com.github.codemonstur:embedded-redis:1.4.3")

    // 모니터링(액추에이터)
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    // 모니터링(프로메테우스)
    implementation("io.micrometer:micrometer-registry-prometheus")

    // 테스트(스프링 부트 테스트): spring-boot-starter-test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    // 테스트(Kotest): kotest
    testImplementation("io.kotest:kotest-runner-junit5:5.7.2")
    testImplementation("io.kotest:kotest-assertions-core:5.7.2")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")
    // 테스트(MockK): mockk
    testImplementation("io.mockk:mockk:1.13.8")
    // 테스트(MockK): mockk
    testImplementation("com.ninja-squad:springmockk:3.0.1")
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
