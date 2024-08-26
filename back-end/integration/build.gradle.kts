plugins {
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    id("org.springframework.boot") version "3.3.2" apply false
    id("io.spring.dependency-management") version "1.1.6"
}

group = "net.stakemetrics"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.telegram:telegrambots-client:7.4.2")
    implementation("com.stripe:stripe-java:26.0.0")
    implementation("com.mailersend:java-sdk:1.0.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")
    implementation("com.fasterxml.jackson.core:jackson-core:2.15.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.15.0")
    implementation("com.google.cloud:google-cloud-tasks:2.46.0")
    implementation("com.google.cloud:google-cloud-pubsub:1.131.0")
    implementation(platform("com.google.cloud:libraries-bom:26.44.0"))
    implementation("com.google.cloud:google-cloud-errorreporting")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation(project(":application"))
    testImplementation("org.springframework.boot:spring-boot-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.register("prepareKotlinBuildScriptModel") {}

tasks.withType<Test> {
    useJUnitPlatform()
}
