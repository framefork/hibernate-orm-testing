plugins {
    id("org.framefork.build.library-internal")
}

dependencies {
    testImplementation(project(":hibernate-orm-testing-63"))
    testImplementation(libs.hibernate.orm.v65.test)
    testImplementation(libs.assertj.core)
    testRuntimeOnly(libs.logback.classic)
}
