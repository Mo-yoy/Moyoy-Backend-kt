plugins {
	kotlin("jvm") version "2.2.21"
	kotlin("plugin.spring") version "2.2.21"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.springframework.boot") version "4.0.0" apply false
}

allprojects {
	group = "com"
	version = "0.0.1-SNAPSHOT"
	description = "moyoy"

	repositories {
		mavenCentral()
	}
}

subprojects {
	apply(plugin = "org.jetbrains.kotlin.jvm")
	apply(plugin = "org.jetbrains.kotlin.plugin.spring")
	apply(plugin = "io.spring.dependency-management")
	apply(plugin = "org.springframework.boot")

	dependencies {
		implementation("org.jetbrains.kotlin:kotlin-reflect")
		implementation("tools.jackson.module:jackson-module-kotlin")
		implementation("org.springframework.boot:spring-boot-starter-web")

		testImplementation("org.springframework.boot:spring-boot-starter-test")
		testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
		testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	}

	java {
		toolchain {
			languageVersion = JavaLanguageVersion.of(24)
		}
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}

	tasks.getByName("bootJar") {
		enabled = false
	}

	tasks.getByName("jar") {
		enabled = false
	}
}
