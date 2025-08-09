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
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-web")
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-security")
    implementation(group = "org.springframework.boot", name = "spring-boot-configuration-processor")
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-oauth2-resource-server")
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-jdbc")
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-data-jpa")
    developmentOnly(group = "org.springframework.boot", name = "spring-boot-devtools")

    // Swagger
    implementation(group = "org.springdoc", name = "springdoc-openapi-starter-webmvc-ui", version = "2.6.0")

    // Lombok
    compileOnly(group = "org.projectlombok", name = "lombok")
    annotationProcessor(group = "org.projectlombok", name = "lombok")

    // Test
    testImplementation(group = "org.springframework.boot", name = "spring-boot-starter-test")
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter")
    testImplementation(group = "org.mockito", name = "mockito-core")
    testImplementation(group = "org.mockito", name = "mockito-junit-jupiter")
    testImplementation(group = "org.testcontainers", name = "junit-jupiter")
    testImplementation(group = "org.testcontainers", name = "postgresql")
    testImplementation(group = "org.springframework.security", name = "spring-security-test")
    testImplementation(group = "io.rest-assured", name = "spring-mock-mvc")

    // JWT
    implementation(group = "io.jsonwebtoken", name = "jjwt-api", version = "0.11.5")
    implementation(group = "io.jsonwebtoken", name = "jjwt-impl", version = "0.11.5")
    implementation(group = "io.jsonwebtoken", name = "jjwt-jackson", version = "0.11.5")

    // Database
    runtimeOnly(group = "org.postgresql", name = "postgresql")
    implementation(group = "org.liquibase", name = "liquibase-core")
}


val jacocoExcludedClasses = listOf(
    "**/dto/**",
    "**/entity/**",
    "**/config/**",
    "**/exception/**",
    "**/enums/**",
    "**/*Application*",
    "**/api/**",
    "**/model/**"
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
