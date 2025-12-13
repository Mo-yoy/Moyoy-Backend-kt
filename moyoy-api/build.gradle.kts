import org.asciidoctor.gradle.jvm.AsciidoctorTask
import org.jlleitschuh.gradle.ktlint.tasks.KtLintCheckTask

tasks.getByName("bootJar") {
    enabled = true
}

plugins {
    id("org.asciidoctor.jvm.convert") version "4.0.0"
}

configurations {
    create("asciidoctorExt")
}

dependencies {
    implementation(project(":moyoy-common"))
    implementation(project(":moyoy-core:domain"))
    implementation(project(":moyoy-core:infra"))

    "asciidoctorExt"("org.springframework.restdocs:spring-restdocs-asciidoctor:4.0.0")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc:4.0.0")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
}

val snippetsDir by extra { file("build/generated-snippets") }

tasks.named<Test>("test") {

    outputs.dir(snippetsDir)
}

tasks.named<AsciidoctorTask>("asciidoctor") {
    inputs.dir(snippetsDir)
    configurations("asciidoctorExt")
    dependsOn(tasks.named("test"))
}

afterEvaluate {
    project.tasks.apply {
        this.withType<KtLintCheckTask> {
            dependsOn(
                listOf(
                    ":ktlintCheck",
                    ":moyoy-common:ktlintCheck",
                    ":moyoy-core:domain:ktlintCheck",
                    ":moyoy-core:infra:ktlintCheck"
                )
            )
        }
    }
}
