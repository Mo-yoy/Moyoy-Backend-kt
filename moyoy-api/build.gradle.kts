import com.epages.restdocs.apispec.gradle.OpenApi3Extension
import io.swagger.v3.oas.models.servers.Server
import org.jlleitschuh.gradle.ktlint.tasks.KtLintCheckTask

tasks.getByName("bootJar") {
    enabled = true
}

tasks.named("bootJar") {
    dependsOn("copyOasToSwagger")
}

plugins {
    id("com.epages.restdocs-api-spec") version "0.19.2"
}

dependencies {
    implementation(project(":moyoy-common"))
    implementation(project(":moyoy-core:domain"))
    implementation(project(":moyoy-core:infra"))

    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("com.epages:restdocs-api-spec-mockmvc:0.19.2")
}

configure<OpenApi3Extension> {

    @Suppress("UNCHECKED_CAST")
    val serverClosures: List<groovy.lang.Closure<Server>> =
        listOf(
            closureOf<Server> {
                this.url = "https://api.moyoy.shop"
                this.description = "prod"
                this
            },
            closureOf<Server> {
                this.url = "http://localhost:8080"
                this.description = "local"
                this
            }
        ) as List<groovy.lang.Closure<Server>>

    setServers(serverClosures)

    title = "Moyoy API Server"
    description = "Moyoy API Server description"
    version = "v1.0.0"
    format = "yaml"
}

tasks.register<Copy>("copyOasToSwagger") {
    delete("src/main/resources/static/swagger-ui/openapi3.yaml")
    from(layout.buildDirectory.file("api-spec/openapi3.yaml"))
    into("src/main/resources/static/swagger-ui")
    dependsOn("openapi3")
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
