plugins {
    id("org.framefork.build.library-internal")
}

dependencies {
    testImplementation(project(":hibernate-orm-testing-72"))
    testImplementation(libs.hibernate.orm.v74.test)
    testImplementation(libs.assertj.core)
    testRuntimeOnly(libs.logback.classic)
}
