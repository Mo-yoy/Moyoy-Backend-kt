tasks.getByName("jar") {
    enabled = true
}
dependencies {
    implementation(project(":moyoy-common"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}
