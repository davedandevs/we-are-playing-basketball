plugins {
    java
    checkstyle
    jacoco
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.openapi.generator") version "7.12.0"
}

group = "online.rabko"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

checkstyle {
    toolVersion = "10.21.4"
    isShowViolations = false
}

repositories {
    mavenCentral()
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

    // Util
    implementation("org.apache.commons:commons-csv:1.14.0")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.rest-assured:spring-mock-mvc")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // Database
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.liquibase:liquibase-core")
}

val jacocoExcludedClasses = listOf(
    "**/dto/**",
    "**/entity/**",
    "**/model/**",
    "**/api/**",
    "**/config/**",
    "**/exception/**",
    "**/enums/**",
    "**/*Request*",
    "**/*Response*",
    "**/*Exception*",
    "**/*Application*"
)
val oasResourcesDir = "$projectDir/src/main/resources/static/oas"
val buildDir = layout.buildDirectory.get()

openApiGenerate {
    generatorName.set("spring")
    inputSpec.set("$oasResourcesDir/basketball.yaml")
    outputDir.set("$buildDir/generated")
    modelPackage.set("online.rabko.model")
    apiPackage.set("online.rabko.api")
    library.set("spring-boot")
    configOptions.set(
        mapOf(
            "useSpringBoot3" to "true",
            "useSwaggerUI" to "true",
            "interfaceOnly" to "true",
            "skipDefaultInterface" to "true",
            "openApiNullable" to "false"
        )
    )
}

sourceSets {
    main {
        java.srcDir("$buildDir/generated/src/main/java")
    }
}

tasks {
    withType<Test> {
        useJUnitPlatform()
        systemProperty("spring.profiles.active", "test")
        finalizedBy("jacocoTestReport", "jacocoTestCoverageVerification")
    }

    compileJava {
        dependsOn(openApiGenerate)
    }

    named<JacocoReport>("jacocoTestReport") {
        dependsOn(test)

        reports {
            xml.required.set(true)
            html.required.set(true)
        }

        val filteredClassDirs = files(classDirectories.files.map {
            fileTree(it).exclude(jacocoExcludedClasses)
        })
        classDirectories.setFrom(filteredClassDirs)

        doLast {
            val report = reports.html.outputLocation.get().asFile.resolve("index.html")
            println("Jacoco HTML report: file://${report.absolutePath}")
        }
    }

    named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
        dependsOn(test)

        val filteredClassDirs = files(classDirectories.files.map {
            fileTree(it).exclude(jacocoExcludedClasses)
        })
        classDirectories.setFrom(filteredClassDirs)

        violationRules {
            rule {
                limit {
                    minimum = BigDecimal("0.80")
                }
            }
            isFailOnViolation = true
        }
    }
}
