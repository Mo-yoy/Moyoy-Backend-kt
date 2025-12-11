import org.jlleitschuh.gradle.ktlint.tasks.KtLintCheckTask

tasks.getByName("bootJar") {
    enabled = true
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

dependencies {
    implementation(project(":moyoy-common"))
    implementation(project(":moyoy-core:domain"))
    implementation(project(":moyoy-core:infra"))
}
