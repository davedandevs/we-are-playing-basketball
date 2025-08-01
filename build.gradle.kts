plugins {
    java
    checkstyle
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

    compileOnly("com.google.code.findbugs:jsr305:3.0.2")

    // Database
    implementation(group = "org.postgresql", name = "postgresql", version = "42.7.6")

   implementation ("org.liquibase:liquibase-core")
}

tasks {
    withType<Test> {
        useJUnitPlatform()
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
