import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import org.jlleitschuh.gradle.ktlint.tasks.GenerateReportsTask

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25" apply false
    kotlin("plugin.jpa") version "1.9.25" apply false
    id("io.spring.dependency-management") version "1.1.7"
    id("org.springframework.boot") version "3.5.8" apply false
    id("org.jlleitschuh.gradle.ktlint") version "14.0.1"
    id("org.sonarqube") version "7.2.0.6526"
}

allprojects {
    group = "com"
    version = "0.0.1-SNAPSHOT"
    description = "moyoy"

    repositories {
        mavenCentral()
    }

    tasks.withType<JavaExec> {
        jvmArgs("--add-opens", "java.base/sun.misc=ALL-UNNAMED")
    }

    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    ktlint {
        reporters {
            reporter(ReporterType.JSON)
        }
    }

    tasks.withType<GenerateReportsTask> {
        reportsOutputDirectory.set(
            rootProject.layout.buildDirectory.dir("reports/ktlint/${project.name}")
        )
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "jacoco")

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.springframework.boot:spring-boot-starter-web")

        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()

        testLogging {
            events("passed", "skipped", "failed", "standardOut", "standardError")
            showStandardStreams = true
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
        finalizedBy(tasks.named<JacocoReport>("jacocoTestReport"))
    }

    tasks.named<JacocoReport>("jacocoTestReport") {

        reports {
            xml.required.set(true)
            xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco.xml"))
        }

        classDirectories.setFrom(
            files(
                classDirectories.files.map {
                    project.fileTree(it) {
                        exclude(
                            "**/*Application*",
                            "**/*Config*",
                            "**/*Properties*",
                            "**/*Dto*",
                            "**/*Request*",
                            "**/*Response*",
                            "**/*Interceptor*",
                            "**/*Exception*",
                            "**/*Usecase\$Input*",
                            "**/*Usecase\$Output*",
                            "**/common/const/**",
                            "**/CustomOAuth2UserService*",
                            "**/GithubOAuth2UserPrincipal*",
                            "**/RdbOAuth2AuthorizedClientService*",
                            "**/CustomAuthenticationFailureHandler*",
                            "**/CustomAuthenticationSuccessHandler*",
                            "**/CustomAuthenticationEntryPoint*",
                            "**/JwtExceptionHandleFilter*",
                            "**/jasypt/**"
                        )
                    }
                }
            )
        )
    }

    tasks.getByName("bootJar") {
        enabled = false
    }

    tasks.getByName("jar") {
        enabled = false
    }
}

sonar {
    properties {
        property("sonar.projectKey", "Mo-yoy_Moyoy-Backend-kt")
        property("sonar.organization", "mo-yoy")
        property("sonar.host.url", "https://sonarcloud.io")

        property("sonar.coverage.jacoco.xmlReportPaths", "**/build/reports/jacoco.xml")

        property(
            "sonar.exclusions",
            listOf(
                "**/test/**",
                "**/resources/**",
                "**/*Application*",
                "**/*Config*",
                "**/*Properties*",
                "**/*Dto*",
                "**/*Request*",
                "**/*Response*",
                "**/*Exception*",
                "**/Q*.class",
                "**/Q*.kt",
                "**/*Usecase\$Input*",
                "**/*Usecase\$Output*",
                "**/common/const/**",
                "**/CustomOAuth2UserService*",
                "**/GithubOAuth2UserPrincipal*",
                "**/RdbOAuth2AuthorizedClientService*",
                "**/CustomAuthenticationFailureHandler*",
                "**/CustomAuthenticationSuccessHandler*",
                "**/CustomAuthenticationEntryPoint*",
                "**/JwtExceptionHandleFilter*",
                "**/jasypt/**"
            ).joinToString(",")
        )
    }
}
