plugins {
    id("org.framefork.build.library-published")
}

dependencies {
    api(project(":hibernate-orm-testing-database"))
    api(libs.junit.jupiter.api)
    compileOnly(libs.hibernate.orm.v72.compile)

    testImplementation(libs.hibernate.orm.v72.test)
    testImplementation(libs.assertj.core)
    testRuntimeOnly(libs.logback.classic)
}

project.description = "Session factory, transaction helpers and JUnit 5 test bases for integration tests against Hibernate ORM 7.2, 7.3 and 7.4"
