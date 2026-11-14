plugins {
    id("org.framefork.build.library-published")
}

dependencies {
    api(libs.testcontainers.jdbc)
    implementation(libs.testcontainers.postgresql)
    implementation(libs.testcontainers.mysql)
    implementation(libs.postgresql)
    implementation(libs.mysql)

    testImplementation(libs.assertj.core)
    testRuntimeOnly(libs.logback.classic)
}

project.description = "Testcontainers-backed PostgreSQL and MySQL databases for integration tests, independent of the Hibernate ORM version"
