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

    // Util
    implementation(group = "org.apache.commons", name = "commons-csv", version = "1.14.0")
    
    // Lombok
    compileOnly(group = "org.projectlombok", name = "lombok")
    annotationProcessor(group = "org.projectlombok", name = "lombok")

    // Test
    testImplementation(group = "org.springframework.boot", name = "spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.springframework.security:spring-security-test")


    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.6")
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.6")

    // Database
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.liquibase:liquibase-core")

    //Other
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")

}

tasks {
    withType<Test> {
        useJUnitPlatform()
        systemProperty("spring.profiles.active", "test")
    }
    compileJava {
        dependsOn(openApiGenerate)
    }
}

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

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy("jacocoTestReport", "jacocoTestCoverageVerification")
}

tasks.named<JacocoReport>("jacocoTestReport").configure {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/coverage.xml"))
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/html"))
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

tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification").configure {
    dependsOn(tasks.test)

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
        isFailOnViolation = false
    }
}
