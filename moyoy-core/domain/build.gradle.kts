tasks.getByName("jar") {
    enabled = true
}

plugins {
    kotlin("plugin.jpa")
}

dependencies {
    implementation(project(":moyoy-common"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}
