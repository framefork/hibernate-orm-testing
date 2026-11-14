plugins {
    id("org.framefork.build.library-internal")
}

dependencies {
    testImplementation(project(":hibernate-orm-testing-70"))
    testImplementation(libs.hibernate.orm.v71.test)
    testImplementation(libs.assertj.core)
    testRuntimeOnly(libs.logback.classic)
}
