tasks.getByName("jar") {
    enabled = true
}

plugins {
    kotlin("plugin.jpa")
}

dependencies {
    implementation(project(":moyoy-common"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")
    runtimeOnly("com.mysql:mysql-connector-j")
}
