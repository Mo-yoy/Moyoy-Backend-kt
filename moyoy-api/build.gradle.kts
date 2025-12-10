tasks.getByName("bootJar") {
    enabled = true
}

dependencies {
    implementation(project(":moyoy-common"))
    implementation(project(":moyoy-core:domain"))
    implementation(project(":moyoy-core:infra"))
}
