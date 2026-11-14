plugins {
    id("org.framefork.build.library-published")
}

dependencies {
    api(project(":hibernate-orm-testing-database"))
    api(libs.junit.jupiter.api)
    compileOnly(libs.hibernate.orm.v70.compile)

    testImplementation(libs.hibernate.orm.v70.test)
    testImplementation(libs.assertj.core)
    testRuntimeOnly(libs.logback.classic)
}

project.description = "Session factory, transaction helpers and JUnit 5 test bases for integration tests against Hibernate ORM 7.0 and 7.1"
