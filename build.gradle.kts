import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    java
    id("org.springframework.boot") version "3.1.4"
    id("io.spring.dependency-management") version "1.1.3"
    id("org.openapi.generator") version "6.6.0"
    id("com.avast.gradle.docker-compose") version "0.16.11"
}

group = "ru.mnagovitsin"
version = "1.0.0"

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
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    compileOnly("org.projectlombok:lombok")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.flywaydb:flyway-core")
    implementation("org.postgresql:postgresql")

    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.clean {
    delete("$rootDir/frontend/src/generated")
}

dockerCompose {
    useComposeFiles.add("./compose-dev.yml")
    buildBeforeUp.set(true)
    buildBeforePull.set(false)
    captureContainersOutput.set(false)
    removeOrphans.set(true)
    setProjectName("mancala")
    projectNamePrefix = "mancala_"
}

tasks.register<GenerateTask>("generateApiServer") {
    group = "code generation"
    description = "Generates API server code from OpenAPI spec"

    generatorName.set("spring")
    inputSpec.set("$rootDir/openapi/mancala-api.yaml")
    outputDir.set("$buildDir/generated")
    apiPackage.set("ru.mnagovitsin.mancala.api")
    modelPackage.set("ru.mnagovitsin.mancala.model")
    ignoreFileOverride.set("$rootDir/.openapi-generator-ignore")
    configOptions.set(
        mapOf(
            "dateLibrary" to "java8",
            "annotationLibrary" to "none",
            "documentationProvider" to "none",
            "exceptionHandler" to "false",
            "serializationLibrary" to "jackson",
            "useSpringBoot3" to "true",
            "useSwaggerUI" to "false",
            "openApiNullable" to "false",
            "useTags" to "true",
            "enumPropertyNaming" to "UPPERCASE",
            "library" to "spring-boot",
            "interfaceOnly" to "true",
        ),
    )
}

tasks.compileJava {
    dependsOn("generateApiServer")
}

sourceSets {
    main {
        java {
            srcDir("$buildDir/generated/src/main/java")
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
